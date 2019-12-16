package m.core.samples;

import java.util.concurrent.TimeUnit;

import m.core.data.kafka.KafkaDataQueue;
import m.core.data.queue.DataQueue;
import m.core.data.redis.RedisDataStoreCacheQueue;

public class Queue {

    public static void main(String[] args) throws InterruptedException {
        use(new KafkaDataQueue());
        use(new RedisDataStoreCacheQueue());
    }

    public static boolean consumer(String message) {
        System.out.println("Received: " + message);
        return true;
    }

    private static void use(DataQueue queue) throws InterruptedException {
        queue.consume("test_one", Queue::consumer);
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        queue.publish("test_one", "hello mcore cache");
        queue.close();

    }

}
