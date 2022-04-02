package app.functions;

import app.datatypes.BatchResult;
import app.datatypes.SymbolEvent;
import app.datatypes.SymbolResult;
import de.tum.i13.bandency.*;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

public class BatchResultProcess extends KeyedProcessFunction<Long, SymbolResult, BatchResult> {

    private transient ValueState<Integer> symbolCountState;
    private transient ListState<Indicator> indicatorListState;
    private transient ListState<CrossoverEvent> crossoverEventListState;

    @Override
    public void open(Configuration parameters) throws Exception {
        symbolCountState = getRuntimeContext().getState(new ValueStateDescriptor<>("symbolCountState", Integer.class, 0));
        indicatorListState = getRuntimeContext().getListState(new ListStateDescriptor<Indicator>("indicatorListState",Indicator.class));
        crossoverEventListState = getRuntimeContext().getListState(new ListStateDescriptor<CrossoverEvent>("crossoverEventListState",CrossoverEvent.class));
    }

    @Override
    public void processElement(SymbolResult symbolResult, Context ctx, Collector<BatchResult> out) throws Exception {

        if(symbolCountState.value()==(symbolResult.getLookupSymbolCount()-1)){

            /*System.out.println("Received all symbol results for " + symbolResult.getBatchId());*/

            List<Indicator> indicatorList = new ArrayList<>();
            List<CrossoverEvent> crossoverEventList = new ArrayList<>();

            for(Indicator indicator: indicatorListState.get())
                indicatorList.add(indicator);

            for(CrossoverEvent crossoverEvent: crossoverEventListState.get())
                crossoverEventList.add(crossoverEvent);

            out.collect(new BatchResult(symbolResult.getBatchId(),symbolResult.getBenchmarkId(),
                    indicatorList,crossoverEventList));

            symbolCountState.clear();
            indicatorListState.clear();
            crossoverEventListState.clear();
        }
        else{

            /*System.out.println("Received symbol result for: " + symbolResult.getSymbolEvent() + " #" + symbolResult.getBatchId());*/

            symbolCountState.update(symbolCountState.value()+1);
            indicatorListState.add(symbolResult.getIndicator());
            crossoverEventListState.addAll(symbolResult.getCrossoverEventList());
        }
    }

    @Override
    public void close() {
        symbolCountState.clear();
        indicatorListState.clear();
        crossoverEventListState.clear();
    }
}
