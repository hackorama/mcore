package m.core.data.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import m.core.data.queue.DataQueue;

public class KafkaDataQueue implements DataQueue {

    private static final String DEFAULT_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final Object DEFAULT_GROUP_ID = "MCORE_CACHE";
    private static final Logger logger = LoggerFactory.getLogger(KafkaDataQueue.class);
    private static Map<String, ExecutorService> topicConsumerExecutors = new HashMap<>();
    private static Map<String, KafkaConsumer<String, String>> topicConsumers = new HashMap<>();

    private KafkaProducer<String, String> kafkaProducer;
    private Properties properties = new Properties();

    public KafkaDataQueue() {
        this(DEFAULT_BOOTSTRAP_SERVERS);
    }

    public KafkaDataQueue(String bootstrapServers) {
        // TODO FIXME Separate configuration for producers and consumers
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        properties.put("group.id", DEFAULT_GROUP_ID);
        init(properties);
    }

    @Override
    public void close() {

    }

    @Override
    public void consume(String channel, Function<String, Boolean> handler) {
        if (topicConsumers.containsKey(channel)) {
            logger.warn("Already subscribed to channel {}", channel);
            return;
        }
        topicConsumerExecutors.put(channel, Executors.newSingleThreadExecutor()); // TODO pooled executor
        try {
            topicConsumerExecutors.get(channel).submit(() -> {
                topicConsumers.put(channel, new KafkaConsumer<String, String>(properties));
                topicConsumers.get(channel).subscribe(Arrays.asList(channel));
                while (true) {
                    ConsumerRecords<String, String> consumerRecords = topicConsumers.get(channel)
                            .poll(Duration.ofMillis(100));
                    consumerRecords.forEach(record -> {
                        handler.apply(record.value());
                        topicConsumers.get(channel).commitSync();
                    });
                }
            });
        } finally {
            if (topicConsumers.get(channel) != null) {
                topicConsumers.get(channel).close();
            }
        }
    }

    public void init(Properties properties) {
        kafkaProducer = new KafkaProducer<String, String>(properties);
        logger.info("Connected to Kafka server: {}", properties.get("bootstrap.servers"));
    }

    @Override
    public void publish(String channel, String message) {
        kafkaProducer.send(new ProducerRecord<String, String>(channel, message));
    }

}
