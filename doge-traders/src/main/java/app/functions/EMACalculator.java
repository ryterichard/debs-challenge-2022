package app.functions;

import de.tum.i13.bandency.Indicator;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class EMACalculator extends ProcessWindowFunction<Tuple4<String, Float, Long, Boolean>, Tuple2<Indicator, Long>, String, TimeWindow> {
    private final static MapStateDescriptor<String, Float> ema38 = new MapStateDescriptor<String, Float>("ema38", String.class, Float.class);
    private final static MapStateDescriptor<String, Float> ema100 = new MapStateDescriptor<String, Float>("ema100", String.class, Float.class);
    @Override
    public void process(String key, Context context, Iterable<Tuple4<String, Float, Long, Boolean>> input, Collector<Tuple2<Indicator, Long>> out) throws Exception{
        float lastPrice = 0;
        long batchID = -1;
        float j38 = 38;
        float j100 = 100;
        for (Tuple4<String, Float, Long, Boolean> in: input) {
            lastPrice = in.f1;
            batchID = in.f2;
        }
        MapState<String, Float> ema38map = context.globalState().getMapState(ema38);
        MapState<String, Float> ema100map = context.globalState().getMapState(ema100);
        float ema38last = 0;
        float ema100last = 0;
        if (ema38map.get(key) != null) {
            ema38last = ema38map.get(key);
        }
        if (ema100map.get(key) != null) {
            ema100last = ema100map.get(key);
        }

        float ema38New = (lastPrice * (2/(1+j38))) + ema38last * (1-(2/(1+j38)));
        float ema100New = (lastPrice * (2/(1+j100))) + ema100last * (1-(2/(1+j100)));

        ema38map.put(key, ema38New);
        ema100map.put(key, ema100New);

        Indicator i = Indicator.newBuilder()
                .setSymbol(key)
                .setEma38(ema38New)
                .setEma100(ema100New)
                .build();

        out.collect(new Tuple2<>(i, batchID));
    }


}