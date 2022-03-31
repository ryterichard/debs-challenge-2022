package app.functions;

import app.datatypes.BatchResult;
import app.datatypes.SymbolEvent;
import com.google.protobuf.util.Timestamps;
import de.tum.i13.bandency.CrossoverEvent;
import de.tum.i13.bandency.Indicator;
import de.tum.i13.bandency.SecurityType;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimerService;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.Arrays;


public class DogeTradersAdvise extends KeyedProcessFunction<String, SymbolEvent, BatchResult> {
    private transient MapState<Long,Float> windowLastTradingPrice;
    private transient ValueState<Float> ema38;
    private transient ValueState<Float> ema100;
    private transient ValueState<Tuple2<Long,Long>> lastCrossover;
    private transient ValueState<Tuple2<Long,Long>> secondLastCrossover;
    private transient ValueState<Tuple2<Long,Long>> thirdLastCrossover;

    private final long windowDuration;
    private final float j38 = 38;
    private final float j100 = 100;

    public DogeTradersAdvise(Time windowDuration) {
        this.windowDuration = windowDuration.toMilliseconds();
    }

    @Override
    public void open(Configuration conf) {
        windowLastTradingPrice = getRuntimeContext().getMapState(new MapStateDescriptor<>("windowLastTradingPrice", Long.class, Float.class));
        ema38 = getRuntimeContext().getState(new ValueStateDescriptor<>("ema38", Float.class, 0.0F));
        ema100 = getRuntimeContext().getState(new ValueStateDescriptor<>("ema100", Float.class, 0.0F));
        lastCrossover = getRuntimeContext().getState(new ValueStateDescriptor<>("lastCrossover", TypeInformation.of(new TypeHint<Tuple2<Long, Long>>() {}), Tuple2.of(0L, 0L)));
        secondLastCrossover = getRuntimeContext().getState(new ValueStateDescriptor<>("secondLastCrossover", TypeInformation.of(new TypeHint<Tuple2<Long, Long>>() {}), Tuple2.of(0L, 0L)));
        thirdLastCrossover = getRuntimeContext().getState(new ValueStateDescriptor<>("thirdLastCrossover", TypeInformation.of(new TypeHint<Tuple2<Long, Long>>() {}), Tuple2.of(0L, 0L)));
    }

    @Override
    public void processElement(SymbolEvent event,
                               Context ctx,
                               Collector<BatchResult> out) throws Exception {

        long eventTime = event.getTimestamp();
        TimerService timerService = ctx.timerService();

        if(event.isLastEventOfKeyOfBatch()){
            Indicator indicator = Indicator.newBuilder()
                    .setSymbol(ctx.getCurrentKey())
                    .setEma38(ema38.value())
                    .setEma100(ema100.value())
                    .build();
            String eventSymbol = ctx.getCurrentKey();
            SecurityType securityType = SecurityType.Index;
            CrossoverEvent crossoverEvent1 = CrossoverEvent.newBuilder()
                    .setSymbol(eventSymbol).setSecurityType(securityType)
                    .setTs(Timestamps.fromMillis(lastCrossover.value().f0))
                    .setSignalType(CrossoverEvent.SignalType.forNumber(lastCrossover.value().f1.intValue())).build();
            CrossoverEvent crossoverEvent2 = CrossoverEvent.newBuilder()
                    .setSymbol(eventSymbol).setSecurityType(securityType)
                    .setTs(Timestamps.fromMillis(secondLastCrossover.value().f0))
                    .setSignalType(CrossoverEvent.SignalType.forNumber(secondLastCrossover.value().f1.intValue())).build();
            CrossoverEvent crossoverEvent3 = CrossoverEvent.newBuilder()
                    .setSymbol(eventSymbol).setSecurityType(securityType)
                    .setTs(Timestamps.fromMillis(thirdLastCrossover.value().f0))
                    .setSignalType(CrossoverEvent.SignalType.forNumber(thirdLastCrossover.value().f1.intValue())).build();
            System.out.println("Hit dummy event for " + ctx.getCurrentKey());
            System.out.println("Current EMA38: " + ema38.value());
            System.out.println("Current EMA100: " + ema100.value());
            out.collect(new BatchResult(event.getBatchID(),indicator, Arrays.asList(crossoverEvent1,crossoverEvent2,crossoverEvent3)));
            return;
        }
        if (eventTime <= timerService.currentWatermark()) {
            //System.out.println("Out of order" + event.toString() + " " + timerService.currentWatermark());
            // This event is late; its window has already been triggered.
        }
        else {
            long windowTriggerTimestamp = (eventTime - (eventTime % windowDuration) + windowDuration - 1);
            timerService.registerEventTimeTimer(windowTriggerTimestamp);
            //System.out.println(windowTriggerTimestamp + " - " + event.toString());
            windowLastTradingPrice.put(windowTriggerTimestamp,event.getLastTradePrice());
        }
    }

    @Override
    public void onTimer(long timestamp,
                        OnTimerContext ctx,
                        Collector<BatchResult> out) throws Exception {

        System.out.println("End of 5 minute event time window for " + ctx.getCurrentKey());

        float ema38Prev = ema38.value();
        float ema100Prev = ema100.value();

        float ema38New = (windowLastTradingPrice.get(timestamp) * (2/(1+j38))) + ema38Prev * (1-(2/(1+j38)));
        float ema100New = (windowLastTradingPrice.get(timestamp) * (2/(1+j100))) + ema100Prev * (1-(2/(1+j100)));

        ema38.update(ema38New);
        ema100.update(ema100New);

        if(ema38Prev<=ema100Prev && ema38New>ema100New){
            if(secondLastCrossover.value()!=null)
                thirdLastCrossover.update(Tuple2.of(secondLastCrossover.value().f0,secondLastCrossover.value().f1));
            if(lastCrossover.value()!=null)
                secondLastCrossover.update(Tuple2.of(lastCrossover.value().f0,lastCrossover.value().f1));
            lastCrossover.update(Tuple2.of(timestamp,0L));
        }
        else if(ema38Prev>=ema100Prev && ema38New<ema100New){
            if(secondLastCrossover!=null)
                thirdLastCrossover.update(Tuple2.of(secondLastCrossover.value().f0,secondLastCrossover.value().f1));
            if(lastCrossover!=null)
                secondLastCrossover.update(Tuple2.of(lastCrossover.value().f0,lastCrossover.value().f1));
            lastCrossover.update(Tuple2.of(timestamp,1L));
        }

        System.out.println("Window Trigger Timestamp: " + timestamp);
        System.out.println("Prev EMA38: " + ema38Prev);
        System.out.println("Prev EMA100: " + ema100Prev);
        System.out.println("New EMA38: " + ema38New);
        System.out.println("New EMA100: " + ema100New);

        this.windowLastTradingPrice.remove(timestamp);

    }
}
