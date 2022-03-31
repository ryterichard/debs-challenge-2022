package app;

import app.datatypes.BatchResult;
import app.datatypes.SymbolEvent;
import app.functions.DogeTradersAdvise;
import app.utils.AppBase;
import de.tum.i13.bandency.Benchmark;
import de.tum.i13.bandency.ChallengerGrpc;
import grpc.grpcClient;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class DogeTradersApplication extends AppBase {

    public static ChallengerGrpc.ChallengerBlockingStub client;
    public static Benchmark benchmark;

    public static void main(String[] args) throws Exception {


        // set up streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.getConfig().setAutoWatermarkInterval(500);
        env.setParallelism(1);
        env.enableCheckpointing(2000);

        grpcClient grpc = new grpcClient();

        // start the data generator
        DataStream<SymbolEvent> events = env
                .addSource(grpc)
                .name("API")
                .rebalance()
                .assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());

        DataStream<BatchResult> indicatorStream = events
                .keyBy(symbolEvent -> symbolEvent.getSymbol())
                .process(new DogeTradersAdvise(Time.minutes(5)));

        //printOrTest(indicatorStream);

        env.execute("Continuously count symbol events");
    }
}