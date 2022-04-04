package app.functions;

import app.datatypes.BatchResult;
import app.datatypes.SymbolResult;
import de.tum.i13.bandency.*;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;

import java.util.*;
import java.util.stream.Stream;

//import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;
//import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

public class BatchResultProcess extends KeyedProcessFunction<Long, SymbolResult, BatchResult> {

    private transient ValueState<Integer> symbolCountState;
    private transient ListState<Indicator> indicatorListState;
    private transient ListState<CrossoverEvent> crossoverEventListState;

    @Override
    public void open(Configuration parameters) throws Exception {
        symbolCountState = getRuntimeContext().getState(new ValueStateDescriptor<>("symbolCountState", Integer.class, 0));
        indicatorListState = getRuntimeContext().getListState(new ListStateDescriptor<Indicator>("indicatorListState",Indicator.class));
        crossoverEventListState = getRuntimeContext().getListState(new ListStateDescriptor<CrossoverEvent>("crossoverEventListState",CrossoverEvent.class));
    }

    @Override
    public void processElement(SymbolResult symbolResult, Context ctx, Collector<BatchResult> out) throws Exception {

        if(symbolCountState.value()==(symbolResult.getLookupSymbolCount()-1)){

            /*System.out.println("Received all symbol results for " + symbolResult.getBatchId());*/


            List<Indicator> indicatorList = new ArrayList<>();
            List<CrossoverEvent> crossoverEventList = new ArrayList<>();

            for(Indicator indicator: indicatorListState.get())
                indicatorList.add(indicator);

            for(CrossoverEvent crossoverEvent: crossoverEventListState.get())
                crossoverEventList.add(crossoverEvent);

            out.collect(new BatchResult(symbolResult.getBatchId(),symbolResult.getBenchmarkId(),
                    indicatorList,crossoverEventList));


            KafkaProducer<String, String> producer = createKafkaProducer();
            producer.initTransactions();
            try {
                producer.beginTransaction();
                indicatorList.stream().forEach(s -> producer.send(new ProducerRecord<>("input", null, s.toString())));
                producer.commitTransaction();
            } catch (KafkaException e) {
                System.out.println("kafka exception");
                producer.abortTransaction();
            }
            producer.close();

            symbolCountState.clear();
            indicatorListState.clear();
            crossoverEventListState.clear();
        }
        else{

            // System.out.println("Received symbol result for: " + symbolResult.getSymbolEvent() + " #" + symbolResult.getBatchId());


            symbolCountState.update(symbolCountState.value()+1);
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
