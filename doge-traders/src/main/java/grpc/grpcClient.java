package grpc;

import app.DogeTradersApplication;
import app.datatypes.SymbolEvent;
import de.tum.i13.bandency.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.util.*;

public class grpcClient extends RichSourceFunction<SymbolEvent> { //<Data> {
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

        DogeTradersApplication.client = client;

        BenchmarkConfiguration bc = BenchmarkConfiguration.newBuilder()
                .setBenchmarkName("Testrun " + new Date().toString())
                .addQueries(Query.Q1)
                .addQueries(Query.Q2)
                .setToken("cwdplbdpzfatmndjqbhhmjktflhghdtx") //go to: https://challenge.msrg.in.tum.de/profile/
                .setBenchmarkType("evaluation") // Benchmark Type for evaluation
                //.setBenchmarkType("test") // Benchmark Type for testing
                .build();

        //Create a new Benchmark
        Benchmark newBenchmark = client.createNewBenchmark(bc);
        DogeTradersApplication.benchmark = newBenchmark;



        // Start the benchmark
        client.startBenchmark(newBenchmark);

        //Process the events
        int cnt = 0;
        while(true) {
            Batch batch = client.nextBatch(newBenchmark);
            DogeTradersApplication.setBatchMaps(batch.getSeqId(), batch.getLookupSymbolsList());
            //System.out.println(batch);

            if (batch.getLast()) { //Stop when we get the last batch
                System.out.println("Received lastbatch, finished!");
                break;
            }

            //process the batch of events we have
            var q1Results = calculateIndicators(batch, ctx);

            ResultQ1 q1Result = ResultQ1.newBuilder()
                    .setBenchmarkId(newBenchmark.getId()) //set the benchmark id
                    .setBatchSeqId(batch.getSeqId()) //set the sequence number
                    .addAllIndicators(q1Results)
                    .build();

            //return the result of Q1
            client.resultQ1(q1Result);


            var crossOverevents = calculateCrossoverEvents(batch);

            ResultQ2 q2Result = ResultQ2.newBuilder()
                    .setBenchmarkId(newBenchmark.getId()) //set the benchmark id
                    .setBatchSeqId(batch.getSeqId()) //set the sequence number
                    .addAllCrossoverEvents(crossOverevents)
                    .build();

            client.resultQ2(q2Result);
            System.out.println("Processed batch #" + cnt);
            ++cnt;

            if(cnt > 100) { //for testing you can stop early, in an evaluation run, run until getLast() is True.
                break;
            }
        }

        client.endBenchmark(newBenchmark);
        System.out.println("ended Benchmark");
    }

    private static List<Indicator> calculateIndicators(Batch batch, SourceContext<SymbolEvent> ctx) {
        //TODO: improve implementation
        List<Event> eventList = batch.getEventsList();
        for (Event ce : eventList) {
            SymbolEvent se = new SymbolEvent(ce.getSymbol(), ce.getSecurityType(), ce.getLastTradePrice(), ce.getLastTrade(), batch.getSeqId(), false);
            ctx.collectWithTimestamp(se, se.timestamp);
        }

        return new ArrayList<>();
    }

    private static List<CrossoverEvent> calculateCrossoverEvents(Batch batch) {
        //TODO: improve this implementation

        return new ArrayList<>();
    }

    public void cancel() { System.out.println("CANCEL CALLED. TODO."); }
}