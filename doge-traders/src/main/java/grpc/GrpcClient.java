package grpc;

import app.datatypes.SymbolEvent;
import de.tum.i13.bandency.Batch;
import de.tum.i13.bandency.Benchmark;
import de.tum.i13.bandency.ChallengerGrpc;
import de.tum.i13.bandency.Event;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.util.List;

public class GrpcClient extends RichSourceFunction<SymbolEvent> { //<Data> {

    private ChallengerGrpc.ChallengerBlockingStub client;
    private Benchmark benchmark;

    public GrpcClient(Benchmark benchmark) {
        this.benchmark = benchmark;
    }

    public void open(Configuration parameters) throws Exception {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("challenge.msrg.in.tum.de", 5023)
                //.forAddress("192.168.1.4", 5023) //in case it is used internally
                .usePlaintext()
                .build();

        this.client = ChallengerGrpc.newBlockingStub(channel) //for demo, we show the blocking stub
                .withMaxInboundMessageSize(100 * 1024 * 1024)
                .withMaxOutboundMessageSize(100 * 1024 * 1024);
    }

    public void run(SourceContext<SymbolEvent> ctx) {

        // Start the benchmark
        this.client.startBenchmark(benchmark);

        //Process the events
        int cnt = 0;
        while (true) {
            Batch batch = client.nextBatch(benchmark);
            Long benchmarkId = benchmark.getId();
            boolean isLastBatch = batch.getLast();
            if (isLastBatch)
                System.out.println("Last batch");
            System.out.println("Received batch #" + batch.getSeqId());
            /*System.out.println("Subscribed Symbol List" + batch.getLookupSymbolsList());*/

            /*if (batch.getLast()) { //Stop when we get the last batch
                System.out.println("Received lastbatch, finished!");
                break;
            }*/

            List<Event> eventList = batch.getEventsList();
            Integer lookupSymbolCount = batch.getLookupSymbolsCount();
            long sequenceId = batch.getSeqId();
            for (Event ce : eventList) {
                SymbolEvent se = new SymbolEvent(ce.getSymbol(), ce.getSecurityType(), ce.getLastTradePrice(), ce.getLastTrade(), sequenceId, false);
                ctx.collectWithTimestamp(se, se.getTimestamp());
            }
            for (String symbol : batch.getLookupSymbolsList()) {
                SymbolEvent se = new SymbolEvent(symbol, sequenceId, lookupSymbolCount, benchmarkId, true, isLastBatch);
                ctx.collect(se);
            }
            /*++cnt;

            if(cnt > 100) { //for testing you can stop early, in an evaluation run, run until getLast() is True.
                break;
            }*/

            if (isLastBatch) {
                break;
            }

        }


    }

    public void cancel() {
        System.out.println("CANCEL CALLED. TODO.");
    }
}