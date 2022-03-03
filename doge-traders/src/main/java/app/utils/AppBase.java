package app.utils;

import app.datatypes.SymbolEvent;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class AppBase {
    public static SourceFunction<SymbolEvent> symbolEvents = null;
    public static SinkFunction out = null;

    public final static String pathToSymbolEventData = "C:\\Users\\nddan\\College\\Spring 2022\\CS551\\debs-challenge-2022\\doge-traders\\data";

    public static SourceFunction<SymbolEvent> symbolSourceOrTest(SourceFunction<SymbolEvent> source) {
        if (symbolEvents == null) {
            return source;
        }
        return symbolEvents;
    }

    public static void printOrTest(org.apache.flink.streaming.api.datastream.DataStream<?> ds) {
        if (out == null) {
            ds.print();
        } else {
            ds.addSink(out);
        }
    }
}
