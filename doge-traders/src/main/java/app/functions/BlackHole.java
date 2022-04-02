package app.functions;

import app.datatypes.BatchResult;
import app.datatypes.SymbolResult;
import de.tum.i13.bandency.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.util.List;

public class BlackHole extends RichSinkFunction<BatchResult> {

    public static ChallengerGrpc.ChallengerBlockingStub client;

    @Override
    public void open(Configuration conf) {
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

        /*System.out.println("In the black hole");*/

        ResultQ1 q1Result = ResultQ1.newBuilder()
                .setBenchmarkId(batchResult.getBenchmarkId()) //set the benchmark id
                .setBatchSeqId(batchResult.getBatchId()) //set the sequence number
                .addAllIndicators(batchResult.getIndicatorList())
                .build();

        //return the result of Q1
        client.resultQ1(q1Result);

        ResultQ2 q2Result = ResultQ2.newBuilder()
                .setBenchmarkId(batchResult.getBenchmarkId()) //set the benchmark id
                .setBatchSeqId(batchResult.getBatchId()) //set the sequence number
                .addAllCrossoverEvents(batchResult.getCrossoverEventList())
                .build();

        client.resultQ2(q2Result);

        /*System.out.println("End of black hole");*/

    }

}
