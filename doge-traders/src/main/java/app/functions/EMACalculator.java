package app.functions;

import de.tum.i13.bandency.Indicator;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class EMACalculator extends ProcessWindowFunction<Tuple5<String, Float, Long, Boolean, Long>, Tuple2<Indicator, Long>, String, TimeWindow> {
    private final static ValueStateDescriptor<Float> ema38 = new ValueStateDescriptor<Float>("ema38", Float.class);
    private final static ValueStateDescriptor<Float> ema100 = new ValueStateDescriptor<Float>("ema100", Float.class);
    private final static ValueStateDescriptor<Float> MCAD = new ValueStateDescriptor<Float>("MCAD", Float.class);
    private final static ValueStateDescriptor<Integer> advice = new ValueStateDescriptor<Integer>("advice", Integer.class);
    private final static ValueStateDescriptor<Long> last_breakout_pattern = new ValueStateDescriptor<Long>("last_breakout_pattern", Long.class);
    private final static ValueStateDescriptor<Long> second_last_breakout_pattern = new ValueStateDescriptor<Long>("second_last_breakout_pattern", Long.class);
    private final static ValueStateDescriptor<Long> third_last_breakout_pattern = new ValueStateDescriptor<Long>("third_last_breakout_pattern", Long.class);

    // value state for last breakout pattern
    // value state for 2nd last breakout pattern
    // value state for 3rd last breakout pattern


    @Override
    public void process(String key, Context context, Iterable<Tuple5<String, Float, Long, Boolean, Long>> input, Collector<Tuple2<Indicator, Long>> out) throws Exception{
        float lastPrice = 0;
        long batchID = -1;
        float j38 = 38;
        float j100 = 100;
        long timestamp = 0;
        for (Tuple5<String, Float, Long, Boolean, Long> in: input) {
            lastPrice = in.f1;
            batchID = in.f2;
            timestamp = in.f4;
        }
        ValueState<Float> ema38state = context.globalState().getState(ema38);
        ValueState<Float> ema100state = context.globalState().getState(ema100);
        ValueState<Float> MCADstate = context.globalState().getState(MCAD);
        ValueState<Integer> advicestate = context.globalState().getState(advice);

        float ema38last = 0;
        float ema100last = 0;
        float MCADlast = 0;
        if (ema38state.value() != null) {
            ema38last = ema38state.value();
        }
        if (ema100state.value() != null) {
            ema100last = ema100state.value();
        }
        if(MCADstate.value() != null){
            MCADlast = MCADstate.value();
        }

        float ema38New = (lastPrice * (2/(1+j38))) + ema38last * (1-(2/(1+j38)));
        float ema100New = (lastPrice * (2/(1+j100))) + ema100last * (1-(2/(1+j100)));
        float MCADnew = ema38New-ema100New;

        //IF MCAD is negative then it ia a Bearish Pattern and advice is to sell, signified by 0
        if(MCADnew < 0){
            advicestate.update(0);
        }
        //IF MCAD is positive then it ia a Bullish Pattern and advice is to buy, signified by 1
        else {
            advicestate.update(1);
        }
        // We can detect crossover events by seeing if MCADlast * MCADnew is negative or not. if it is negative then we have a crossover event.

        if(MCADlast*MCADnew<=0){
            //set last breakout pattern timestamp to current timestamp
            //update 2nd last breakout pattern
            // update 3rd last breakout pattern

            context.globalState().getState(third_last_breakout_pattern).update(context.globalState().getState(second_last_breakout_pattern).value());
            context.globalState().getState(second_last_breakout_pattern).update(context.globalState().getState(last_breakout_pattern).value());
            context.globalState().getState(last_breakout_pattern).update(timestamp);
        }

        MCADstate.update(MCADnew);
        ema38state.update(ema38New);
        ema100state.update(ema100New);

        Indicator i = Indicator.newBuilder()
                .setSymbol(key)
                .setEma38(ema38New)
                .setEma100(ema100New)
                .build();

        out.collect(new Tuple2<>(i, batchID));
    }


}