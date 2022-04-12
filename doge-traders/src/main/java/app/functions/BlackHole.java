package app.functions;

import app.datatypes.BatchResult;
import de.tum.i13.bandency.Benchmark;
import de.tum.i13.bandency.ChallengerGrpc;
import de.tum.i13.bandency.ResultQ1;
import de.tum.i13.bandency.ResultQ2;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

public class BlackHole extends RichSinkFunction<BatchResult> {

    private ChallengerGrpc.ChallengerBlockingStub client;
    private Benchmark benchmark;
    private transient ValueState<Long> batchCountState;
    private transient ValueState<Long> lastBatchIdState;

    public BlackHole(Benchmark benchmark) {
        this.benchmark = benchmark;
    }

    @Override
    public void open(Configuration conf) {
        batchCountState = getRuntimeContext().getState(new ValueStateDescriptor<>("batchCountState", Long.class, 0L));
        lastBatchIdState = getRuntimeContext().getState(new ValueStateDescriptor<>("lastBatchIdState", Long.class, 0L));

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("challenge.msrg.in.tum.de", 5023)
                //.forAddress("192.168.1.4", 5023) //in case it is used internally
                .usePlaintext()
                .build();

        client = ChallengerGrpc.newBlockingStub(channel) //for demo, we show the blocking stub
                .withMaxInboundMessageSize(100 * 1024 * 1024)
                .withMaxOutboundMessageSize(100 * 1024 * 1024);
    }

    @Override
    public void invoke(BatchResult batchResult, Context ctx) throws Exception {

        Long batchId = batchResult.getBatchId();

        /*System.out.println("In the black hole");*/
        batchCountState.update(batchCountState.value() + 1);
        System.out.println("Batch Count: " + batchCountState.value());
        System.out.println("Last Batch Id: " + lastBatchIdState.value());

        ResultQ1 q1Result = ResultQ1.newBuilder()
                .setBenchmarkId(batchResult.getBenchmarkId()) //set the benchmark id
                .setBatchSeqId(batchId) //set the sequence number
                .addAllIndicators(batchResult.getIndicatorList())
                .build();

        //return the result of Q1
        client.resultQ1(q1Result);

        ResultQ2 q2Result = ResultQ2.newBuilder()
                .setBenchmarkId(batchResult.getBenchmarkId()) //set the benchmark id
                .setBatchSeqId(batchId) //set the sequence number
                .addAllCrossoverEvents(batchResult.getCrossoverEventList())
                .build();

        client.resultQ2(q2Result);

        System.out.println("Processed batch #" + batchId);

        if (batchResult.getLastBatch()) {
            System.out.println("Update Last Batch Value: " + batchId);
            lastBatchIdState.update(batchId + 1);
        }
        if (batchCountState.value().equals(lastBatchIdState.value())) {
            System.out.println("Ended benchmark");
            client.endBenchmark(benchmark);
        }

        /*System.out.println("End of black hole");*/

    }

}
