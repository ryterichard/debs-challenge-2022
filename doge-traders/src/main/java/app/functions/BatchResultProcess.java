package app.functions;

import app.datatypes.SymbolResult;
import com.google.common.collect.Lists;
import de.tum.i13.bandency.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.List;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;

import java.util.*;
import java.util.stream.Stream;
import static org.apache.kafka.clients.producer.ProducerConfig.*;


public class BatchResultProcess extends KeyedProcessFunction<Long, SymbolResult, Tuple2<Long, Boolean>> {

    private transient ValueState<Integer> symbolCountState;
    private transient ListState<Indicator> indicatorListState;
    private transient ListState<CrossoverEvent> crossoverEventListState;
    private ChallengerGrpc.ChallengerBlockingStub client;

    @Override
    public void open(Configuration parameters) throws Exception {
        symbolCountState = getRuntimeContext().getState(new ValueStateDescriptor<>("symbolCountState", Integer.class, 0));
        indicatorListState = getRuntimeContext().getListState(new ListStateDescriptor<Indicator>("indicatorListState", Indicator.class));
        crossoverEventListState = getRuntimeContext().getListState(new ListStateDescriptor<CrossoverEvent>("crossoverEventListState", CrossoverEvent.class));

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
    public void processElement(SymbolResult symbolResult, Context ctx, Collector<Tuple2<Long, Boolean>> out) throws Exception {

        if (symbolCountState.value() == (symbolResult.getLookupSymbolCount() - 1)) {

            /*System.out.println("Received all symbol results for " + symbolResult.getBatchId());*/

            indicatorListState.add(symbolResult.getIndicator());
            crossoverEventListState.addAll(symbolResult.getCrossoverEventList());

            List<Indicator> indicatorList = Lists.newArrayList(indicatorListState.get());
            List<CrossoverEvent> crossoverEventList = Lists.newArrayList(crossoverEventListState.get());

            Long benchmarkId = symbolResult.getBenchmarkId();
            Long batchId = symbolResult.getBatchId();

            ResultQ1 q1Result = ResultQ1.newBuilder()
                    .setBenchmarkId(benchmarkId) //set the benchmark id
                    .setBatchSeqId(batchId) //set the sequence number
                    .addAllIndicators(indicatorList)
                    .build();

            client.resultQ1(q1Result);

            ResultQ2 q2Result = ResultQ2.newBuilder()
                    .setBenchmarkId(benchmarkId) //set the benchmark id
                    .setBatchSeqId(batchId) //set the sequence number
                    .addAllCrossoverEvents(crossoverEventList)
                    .build();

            client.resultQ2(q2Result);

	        String sendToKafkaStr = System.getenv("SEND_TO_KAFKA");
            if(sendToKafkaStr != null && !sendToKafkaStr.isBlank()) {
                KafkaProducer producer = createKafkaProducer();
                producer.initTransactions();
                try {
                    producer.beginTransaction();
                    q1Result.getIndicatorsList().stream()
                            .filter(s -> s.getEma38() != 0.0f)
                            .forEach(s -> producer.send(new ProducerRecord<>("query1", null, s.toString())));
                    q2Result.getCrossoverEventsList().stream()
                            .filter(s -> s.getTs().getSeconds() != 0.0)
                            .forEach(s -> producer.send(new ProducerRecord<>("query2", null,
									     s.getSymbol()+" "+s.getSignalType()+" "+s.getTs().getSeconds())));
                    producer.commitTransaction();
                } catch (KafkaException e) {
                    System.out.println("kafka exception");
                    producer.abortTransaction();
                }
                producer.close();
            }
	    
            System.out.println("Processed batch #" + batchId);

            out.collect(new Tuple2<>(batchId,symbolResult.getLastBatch()));

            symbolCountState.clear();
            indicatorListState.clear();
            crossoverEventListState.clear();

        } else {

            /*System.out.println("Received symbol result for: " + symbolResult.getSymbolEvent() + " #" + symbolResult.getBatchId());*/

            symbolCountState.update(symbolCountState.value() + 1);
            indicatorListState.add(symbolResult.getIndicator());
            crossoverEventListState.addAll(symbolResult.getCrossoverEventList());
        }
    }

    @Override
    public void close() {
        symbolCountState.clear();
        indicatorListState.clear();
        crossoverEventListState.clear();
    }


    private static KafkaProducer<String, String> createKafkaProducer() {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString());

        props.put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        return new KafkaProducer(props);

    }

}
