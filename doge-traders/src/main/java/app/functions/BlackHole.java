package app.functions;

import de.tum.i13.bandency.Benchmark;
import de.tum.i13.bandency.ChallengerGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

public class BlackHole extends RichSinkFunction<Tuple2<Long, Boolean>> {

    private ChallengerGrpc.ChallengerBlockingStub client;
    private Benchmark benchmark;
    private Long batchCount;
    private Long lastBatchId;

    public BlackHole(Benchmark benchmark) {
        this.benchmark = benchmark;
        batchCount = 0L;
    }

    @Override
    public void open(Configuration conf) {ManagedChannel channel = ManagedChannelBuilder
                .forAddress("challenge.msrg.in.tum.de", 5023)
                //.forAddress("192.168.1.4", 5023) //in case it is used internally
                .usePlaintext()
                .build();

        client = ChallengerGrpc.newBlockingStub(channel) //for demo, we show the blocking stub
                .withMaxInboundMessageSize(100 * 1024 * 1024)
                .withMaxOutboundMessageSize(100 * 1024 * 1024);
    }

    @Override
    public void invoke(Tuple2<Long, Boolean> batchInfo, Context ctx) throws Exception {
        batchCount+=1;
        if(batchInfo.f1)
            lastBatchId = batchInfo.f0+1;
        if (batchCount.equals(lastBatchId)) {
            System.out.println("Ended benchmark");
            client.endBenchmark(benchmark);
        }

        /*System.out.println("End of black hole");*/

    }

}
