package grpc;

import app.datatypes.SymbolEvent;
import de.tum.i13.bandency.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.util.Date;
import java.util.List;

public class GrpcClient extends RichSourceFunction<SymbolEvent> { //<Data> {

    public static ChallengerGrpc.ChallengerBlockingStub client;
    public static Benchmark benchmark;

    public void run(SourceContext<SymbolEvent> ctx){ //<Data> ctx) {

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("challenge.msrg.in.tum.de", 5023)
                //.forAddress("192.168.1.4", 5023) //in case it is used internally
                .usePlaintext()
                .build();


        client = ChallengerGrpc.newBlockingStub(channel) //for demo, we show the blocking stub
                .withMaxInboundMessageSize(100 * 1024 * 1024)
                .withMaxOutboundMessageSize(100 * 1024 * 1024);

        BenchmarkConfiguration bc = BenchmarkConfiguration.newBuilder()
                .setBenchmarkName("Testrun " + new Date().toString())
                .addQueries(Query.Q1)
                .addQueries(Query.Q2)
                .setToken("cwdplbdpzfatmndjqbhhmjktflhghdtx") //go to: https://challenge.msrg.in.tum.de/profile/
                .setBenchmarkType("evaluation") // Benchmark Type for evaluation
                //.setBenchmarkType("test") // Benchmark Type for testing
                .build();

        //Create a new Benchmark
        benchmark = client.createNewBenchmark(bc);

        // Start the benchmark
        client.startBenchmark(benchmark);

        //Process the events
        int cnt = 0;
        while(true) {
            Batch batch = client.nextBatch(benchmark);
            Long benchmarkId = benchmark.getId();
            boolean isLastBatch = batch.getLast();
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
            for (String symbol: batch.getLookupSymbolsList()){
                SymbolEvent se = new SymbolEvent(symbol, sequenceId, lookupSymbolCount, benchmarkId,true);
                ctx.collect(se);
            }

            System.out.println("Processed batch #" + batch.getSeqId());
            /*++cnt;

            if(cnt > 100) { //for testing you can stop early, in an evaluation run, run until getLast() is True.
                break;
            }*/

            if(isLastBatch){
                break;
            }

        }


    }

    public void cancel() { System.out.println("CANCEL CALLED. TODO."); }

    @Override
    public void close() throws Exception
    {
        client.endBenchmark(benchmark);
        System.out.println("ended Benchmark");
    }
}