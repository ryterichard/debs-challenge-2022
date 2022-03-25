package app;

import app.datatypes.SymbolEvent;
import app.functions.EMACalculator;
import app.utils.AppBase;
import de.tum.i13.bandency.Benchmark;
import de.tum.i13.bandency.ChallengerGrpc;
import de.tum.i13.bandency.Indicator;
import grpc.grpcClient;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DogeTradersApplication extends AppBase {

    public static ChallengerGrpc.ChallengerBlockingStub client;
    public static Benchmark benchmark;
    public static Map<Long, List<String>> lookUpSymbolMap = new HashMap<>();
    public static Map<Long, List<Indicator>> query1Submission = new HashMap<>();

    public static void setBatchMaps(long batchID, List<String> li) {
        lookUpSymbolMap.put(batchID, li);
        query1Submission.put(batchID, new ArrayList<>());
    }

    public static void main(String[] args) throws Exception {


        // set up streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.getConfig().setAutoWatermarkInterval(500);
        env.setParallelism(1);
        env.enableCheckpointing(2000);

        grpcClient grpc = new grpcClient();


        // start the data generator
        DataStream<SymbolEvent> events = env
                .addSource(grpc)
                .name("API")
                .rebalance()
                .assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());


        // map each job event to a 2-tuple
        DataStream<Tuple4<String, Float, Long, Boolean>> mappedEvents = events.map(new DogeTradersApplication.SymbolMapper());

        // group the stream of tuples by symbol
        KeyedStream<Tuple4<String, Float, Long, Boolean>, String> keyedEvents = mappedEvents.keyBy(tuple -> tuple.getField(0));

        DataStream<Tuple2<Indicator, Long>> indicatorStream = keyedEvents.window(TumblingEventTimeWindows.of(Time.minutes(5))).process(new EMACalculator());
                                                            // we need a list of batch id that ended within this window


        printOrTest(indicatorStream);

//        DataStream<Tuple3<String, Float, Float>> mappedIndicator = indicatorStream.map(new IndicatorMapper());
//
//        KeyedStream<Tuple3<String, Float, Float>, String> keyedInndicatorEvents = mappedIndicator.keyBy(tuple -> tuple.getField(0));






        // execute the dataflow
        env.execute("Continuously count symbol events");
    }

    /**
     * A helper class that implements a map transformation.
     * For each input SymbolEvent record, the mapper output a tuple-2 containing the symbol as the first field
     * and its last traded price as the second field.
     */
    private static final class SymbolMapper implements MapFunction<SymbolEvent, Tuple4<String, Float, Long, Boolean>> {
        @Override
        public Tuple4<String, Float, Long, Boolean> map(SymbolEvent symbolEvent) {
            return new Tuple4<>(symbolEvent.symbol, symbolEvent.lastTradePrice, symbolEvent.batchID, symbolEvent.isLastEventOfKeyOfBatch);
        }
    }

    private static final class IndicatorMapper implements MapFunction<Indicator, Tuple3<String, Float, Float>> {
        @Override
        public Tuple3<String, Float, Float> map(Indicator ind) {
            return new Tuple3<>(ind.getSymbol(), ind.getEma38(), ind.getEma100());
        }
    }

}