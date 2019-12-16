package m.core.data.queue;

import m.core.data.kafka.KafkaDataQueue;

public class KafkaQueueTest extends QueueTest {

    @Override
    protected void createQueue() {
        queue = new KafkaDataQueue();
    }

    @Override
    protected String getType() {
        return "KAFKA_TEST";
    }

}
