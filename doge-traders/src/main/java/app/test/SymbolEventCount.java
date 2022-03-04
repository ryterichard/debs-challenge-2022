package app.test;

import app.datatypes.SymbolEvent;
import app.functions.EMACalculator;
import app.utils.AppBase;
import de.tum.i13.bandency.Indicator;
import grpc.grpcClient;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

/**
 * This application continuously outputs the number of events per symbol seen so far in the symbol events stream.
 */
public class SymbolEventCount extends AppBase {
    public static void main(String[] args) throws Exception {

        final int servingSpeedFactor = 60; // events of 1 minute are served in 1 second

        // set up streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.getConfig().setAutoWatermarkInterval(500);
        env.setParallelism(4);
        env.enableCheckpointing(2000);

        grpcClient grpc = new grpcClient();

        // start the data generator
        DataStream<SymbolEvent> events = env
                .addSource(grpc)
                .name("API")
                .rebalance()
                .assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());

        // map each job event to a 2-tuple
        DataStream<Tuple2<String, Float>> mappedEvents = events.map(new AppendOneMapper());

        // group the stream of tuples by symbol
        KeyedStream<Tuple2<String, Float>, String> keyedEvents = mappedEvents.keyBy(tuple -> tuple.getField(0));

        // sum all the 1s and print the result stream
        DataStream<Indicator> result = keyedEvents.window(TumblingEventTimeWindows.of(Time.seconds(300))).process(new EMACalculator());

        printOrTest(result);

        // execute the dataflow
        env.execute("Continuously count symbol events");
    }

    /**
     * A helper class that implements a map transformation.
     * For each input SymbolEvent record, the mapper output a tuple-2 containing the symbol as the first field
     * and the number 1 as the second field.
     */
    private static final class AppendOneMapper implements MapFunction<SymbolEvent, Tuple2<String, Float>> {
        @Override
        public Tuple2<String, Float> map(SymbolEvent symbolEvent) {
            return new Tuple2<>(symbolEvent.symbol, symbolEvent.lastTradePrice);
        }
    }

}
