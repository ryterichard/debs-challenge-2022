package app;

import grpc.grpcClient;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class DogeTradersApplication {

    public static void main(String[] args) throws Exception {

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        grpcClient grpc = new grpcClient();
        DataStream<DataPlaceholder> measurements = env.addSource(grpc)
                .name("API")
                .rebalance();

        env.execute("Print Measurements Stream");

    }

}
