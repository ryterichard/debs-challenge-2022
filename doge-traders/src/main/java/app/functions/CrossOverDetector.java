package app.functions;

import de.tum.i13.bandency.CrossoverEvent;
import de.tum.i13.bandency.Indicator;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

public class CrossOverDetector extends ProcessFunction<Indicator, CrossoverEvent> {
    private final static MapStateDescriptor<String, Float> ema38 = new MapStateDescriptor<String, Float>("ema38", String.class, Float.class);
    private final static MapStateDescriptor<String, Float> ema100 = new MapStateDescriptor<String, Float>("ema100", String.class, Float.class);
    @Override
    public void processElement(Indicator input, ProcessFunction<Indicator, CrossoverEvent>.Context context, Collector<CrossoverEvent> out) throws Exception {
        String symbol = input.getSymbol();
        float ema38last = 0;
        float ema100last = 0;
    }
}
