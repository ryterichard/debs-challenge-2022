import app.datatypes.SymbolEvent;
import app.datatypes.SymbolResult;
import app.functions.BatchResultProcess;
import app.functions.BlackHole;
import app.functions.SymbolQueryProcess;
import de.tum.i13.bandency.Benchmark;
import de.tum.i13.bandency.BenchmarkConfiguration;
import de.tum.i13.bandency.ChallengerGrpc;
import de.tum.i13.bandency.Query;
import grpc.GrpcClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Date;

public class DogeTradersApplication {

    public static void main(String[] args) throws Exception {

        // set up streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.getConfig().setAutoWatermarkInterval(500);

        final ChallengerGrpc.ChallengerBlockingStub client;
        final Benchmark benchmark;

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
                .setBenchmarkType("test") // Benchmark Type for testing
                .build();

        //Create a new Benchmark
        benchmark = client.createNewBenchmark(bc);

        GrpcClient grpc = new GrpcClient(benchmark);

        // start the data generator
        DataStream<SymbolEvent> events = env
                .addSource(grpc)
                .name("API")
                .rebalance()
                .assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());

        DataStream<SymbolResult> symbolResultDataStream = events
                .keyBy(symbolEvent -> symbolEvent.getSymbol())
                .process(new SymbolQueryProcess(Time.minutes(5))).setParallelism(4);

        DataStream<Tuple2<Long, Boolean>> batchResultDataStream = symbolResultDataStream
                .keyBy(symbolResult -> symbolResult.getBatchId()).process(new BatchResultProcess()).setParallelism(4);

        batchResultDataStream.addSink(new BlackHole(benchmark)).setParallelism(1);

        //printOrTest(indicatorStream);

        env.execute("Continuously count symbol events");
    }
}