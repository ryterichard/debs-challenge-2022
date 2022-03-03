package app.test;

import app.datatypes.SymbolEvent;
import app.sources.SymbolEventSource;
import app.utils.AppBase;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * This application continuously outputs the number of events per symbol seen so far in the symbol events stream.
 */
public class SymbolEventCount extends AppBase {
    public static void main(String[] args) throws Exception {

        ParameterTool params = ParameterTool.fromArgs(args);
        final String input = params.get("input", AppBase.pathToSymbolEventData);

        final int servingSpeedFactor = 60; // events of 1 minute are served in 1 second

        // set up streaming execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        // start the data generator
        DataStream<SymbolEvent> events = env
                .addSource(jobSourceOrTest(new SymbolEventSource(input)));

        // map each job event to a 2-tuple
        DataStream<Tuple2<String, Long>> mappedEvents = events.map(new AppendOneMapper());

        // group the stream of tuples by symbol
        KeyedStream<Tuple2<String, Long>, String> keyedEvents = mappedEvents.keyBy(tuple -> tuple.getField(0));

        // sum all the 1s and print the result stream
        DataStream<Tuple2<String, Long>> result = keyedEvents.sum(1);

        printOrTest(result);

        // execute the dataflow
        env.execute("Continuously count symbol events");
    }

    /**
     * A helper class that implements a map transformation.
     * For each input SymbolEvent record, the mapper output a tuple-2 containing the symbol as the first field
     * and the number 1 as the second field.
     */
    private static final class AppendOneMapper implements MapFunction<SymbolEvent, Tuple2<String, Long>> {
        @Override
        public Tuple2<String, Long> map(SymbolEvent jobEvent) {
            return new Tuple2<>(jobEvent.symbol, 1L);
        }
    }
}
