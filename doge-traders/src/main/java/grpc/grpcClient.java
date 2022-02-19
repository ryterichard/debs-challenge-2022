package grpc;

import app.DataPlaceholder;
import de.tum.i13.bandency.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class grpcClient extends RichSourceFunction<DataPlaceholder> { //<Data> {

    public void run(SourceContext<DataPlaceholder> ctx){ //<Data> ctx) {

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("challenge.msrg.in.tum.de", 5023)
                //.forAddress("192.168.1.4", 5023) //in case it is used internally
                .usePlaintext()
                .build();


        var challengeClient = ChallengerGrpc.newBlockingStub(channel) //for demo, we show the blocking stub
                .withMaxInboundMessageSize(100 * 1024 * 1024)
                .withMaxOutboundMessageSize(100 * 1024 * 1024);

        BenchmarkConfiguration bc = BenchmarkConfiguration.newBuilder()
                .setBenchmarkName("Testrun " + new Date().toString())
                .addQueries(Query.Q1)
                .addQueries(Query.Q2)
                .setToken("cwdplbdpzfatmndjqbhhmjktflhghdtx") //go to: https://challenge.msrg.in.tum.de/profile/
                .setBenchmarkType("evaluation") //Benchmark Type for evaluation
                .setBenchmarkType("test") //Benchmark Type for testing
                .build();

        //Create a new Benchmark
        Benchmark newBenchmark = challengeClient.createNewBenchmark(bc);

        //Start the benchmark
        challengeClient.startBenchmark(newBenchmark);

        //Process the events
        int cnt = 0;
        while(true) {
            Batch batch = challengeClient.nextBatch(newBenchmark);
            Event event = batch.getEvents(0);
            System.out.println("\nSymbol: " + event.getSymbol());
            System.out.println("Last Trade Price: " + event.getLastTradePrice());
            System.out.println("Last Trade Time: " + event.getLastTrade());
            if (batch.getLast()) { //Stop when we get the last batch
                System.out.println("Received lastbatch, finished!");
                break;
            }

            //process the batch of events we have
            var q1Results = calculateIndicators(batch);

            ResultQ1 q1Result = ResultQ1.newBuilder()
                    .setBenchmarkId(newBenchmark.getId()) //set the benchmark id
                    .setBatchSeqId(batch.getSeqId()) //set the sequence number
                    .addAllIndicators(q1Results)
                    .build();

            //return the result of Q1
            challengeClient.resultQ1(q1Result);


            var crossOverevents = calculateCrossoverEvents(batch);

            ResultQ2 q2Result = ResultQ2.newBuilder()
                    .setBenchmarkId(newBenchmark.getId()) //set the benchmark id
                    .setBatchSeqId(batch.getSeqId()) //set the sequence number
                    .addAllCrossoverEvents(crossOverevents)
                    .build();

            challengeClient.resultQ2(q2Result);
            System.out.println("Processed batch #" + cnt);
            ++cnt;

            if(cnt > 100) { //for testing you can stop early, in an evaluation run, run until getLast() is True.
                break;
            }
        }

        challengeClient.endBenchmark(newBenchmark);
        System.out.println("ended Benchmark");
    }

    private static List<Indicator> calculateIndicators(Batch batch) {
        //TODO: improve implementation

        return new ArrayList<>();
    }

    private static List<CrossoverEvent> calculateCrossoverEvents(Batch batch) {
        //TODO: improve this implementation

        return new ArrayList<>();
    }

    public void cancel() { System.out.println("CANCEL CALLED. TODO."); }
}