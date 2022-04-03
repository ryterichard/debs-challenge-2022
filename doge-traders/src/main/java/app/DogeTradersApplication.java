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
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.util.Properties;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer.Semantic;

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


//        final String BOOTSTRAP_SERVER = "localhost:9092";
//        final String TOPIC_OUT = "input";
//        Properties prodProps = new Properties();
//        prodProps.put("bootstrap.servers", BOOTSTRAP_SERVER);
//
//
//        FlinkKafkaProducer<BatchResult> kafkaProducer =
//                new FlinkKafkaProducer<>(TOPIC_OUT,
//                        ((value, timestamp) -> new ProducerRecord<>(TOPIC_OUT, null, value.toString().getBytes())),
//                        prodProps,
//                        Semantic.EXACTLY_ONCE);


//        symbolResultDataStream.keyBy(symbolResult -> symbolResult.getBatchId()).addSink(kafkaProducer);

//        batchResultDataStream.addSink(kafkaProducer);
        System.out.println( env.getExecutionPlan() );

        env.execute("Continuously count symbol events");
    }
}