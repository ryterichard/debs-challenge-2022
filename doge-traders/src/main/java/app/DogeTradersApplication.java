package app;

import app.datatypes.BatchResult;
import app.datatypes.SymbolResult;
import app.datatypes.SymbolEvent;
import app.functions.BatchResultProcess;
import app.functions.BlackHole;
import app.functions.SymbolQueryProcess;
import app.utils.AppBase;
import de.tum.i13.bandency.CrossoverEvent;
import de.tum.i13.bandency.Indicator;
import grpc.GrpcClient;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

public class DogeTradersApplication extends AppBase {

    public static void main(String[] args) throws Exception {

        // set up streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.getConfig().setAutoWatermarkInterval(500);
        env.setParallelism(8);
        env.enableCheckpointing(2000);

        GrpcClient grpc = new GrpcClient();

        // start the data generator
        DataStream<SymbolEvent> events = env
                .addSource(grpc)
                .name("API")
                .rebalance()
                .assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());

        DataStream<SymbolResult> symbolResultDataStream = events
                .keyBy(symbolEvent -> symbolEvent.getSymbol())
                .process(new SymbolQueryProcess(Time.minutes(5)));

        DataStream<BatchResult> batchResultDataStream = symbolResultDataStream
                .keyBy(symbolResult -> symbolResult.getBatchId()).process(new BatchResultProcess());

        batchResultDataStream.addSink(new BlackHole());

        //printOrTest(indicatorStream);

        env.execute("Continuously count symbol events");
    }
}