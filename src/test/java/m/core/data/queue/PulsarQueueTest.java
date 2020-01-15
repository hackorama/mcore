package m.core.data.queue;

import m.core.data.pulsar.PulsarDataQueue;

public class PulsarQueueTest extends QueueTest {

    @Override
    protected void createQueue() {
        queue = new PulsarDataQueue();
    }

    @Override
    protected String getType() {
        return "PULSAR_TEST";
    }

}
