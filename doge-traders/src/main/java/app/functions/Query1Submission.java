package app.functions;

import de.tum.i13.bandency.Indicator;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.kafka.common.metrics.Stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Query1Submission extends ProcessFunction<Tuple2<Indicator,Long>, Tuple2<Indicator,Long>> {
    private class State {
        public List<Indicator> curRes;
        public long lastBatchID;
    }

    private ValueState<State> state;

    @Override
    public void processElement(Tuple2<Indicator, Long> in, Context ctx, Collector<Tuple2<Indicator, Long>> collector) throws Exception {
        State curState = state.value();
        if (curState == null) {
            curState = new State();
            curState.curRes = new ArrayList<>();
            curState.lastBatchID = in.f1;
        }

        if (in.f1 == curState.lastBatchID) {
            curState.curRes.add(in.f0);
        }





    }
}
