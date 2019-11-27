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
    private static final Object DEFAULT_GROUP_ID = "MCORE";
    private static final Logger logger = LoggerFactory.getLogger(KafkaDataQueue.class);
    private static Map<String, ExecutorService> topicConsumerExecutors = new HashMap<>();
    private static Map<String, Boolean> consumerLiveness = new HashMap<>();
    private static Map<String, KafkaConsumer<String, String>> topicConsumers = new HashMap<>();

    private KafkaProducer<String, String> kafkaProducer;
    private Properties producerProperties = new Properties();
    private Properties consumerProperties = new Properties();

    public KafkaDataQueue() {
        this(DEFAULT_BOOTSTRAP_SERVERS);
    }

    public KafkaDataQueue(String bootstrapServers) {
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");

        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, DEFAULT_GROUP_ID);
        init(producerProperties, consumerProperties);
    }

    @Override
    public void close() {
        consumerLiveness.keySet().forEach(k -> {
            consumerLiveness.put(k, false);
        });
    }

    public void close(String channel) {
        consumerLiveness.put(channel, false);
    }

    @Override
    public void consume(String channel, Function<String, Boolean> handler) {
        if (topicConsumers.containsKey(channel)) {
            logger.warn("Already subscribed to channel {}", channel);
            return;
        }
        topicConsumerExecutors.put(channel, Executors.newSingleThreadExecutor()); // TODO pooled executor
        Runnable consumerTask = new Runnable() {
            @Override
            public void run() {
                try {
                    topicConsumers.put(channel, new KafkaConsumer<String, String>(consumerProperties));
                    topicConsumers.get(channel).subscribe(Arrays.asList(channel));
                    consumerLiveness.put(channel, true);
                    while (consumerLiveness.get(channel)) {
                        ConsumerRecords<String, String> consumerRecords = topicConsumers.get(channel)
                                .poll(Duration.ofMillis(100));
                        consumerRecords.forEach(record -> {
                            handler.apply(record.value());
                            topicConsumers.get(channel).commitSync();
                        });
                    }
                } finally {
                    logger.info("Closing channel {} ...", channel);
                    if (topicConsumers.get(channel) != null) {
                        topicConsumers.get(channel).close();
                    } else {
                        logger.warn("Channel {} is not valid to close", channel);
                    }
                    if (topicConsumerExecutors.get(channel) != null
                            && !topicConsumerExecutors.get(channel).isShutdown()) {
                        topicConsumerExecutors.get(channel).shutdown();
                    } else {
                        logger.warn("Channel {} thread executor service is already shutdown or not valid", channel);
                    }

                }
            }
        };
        topicConsumerExecutors.get(channel).submit(consumerTask);
    }

    public void init(Properties producerProperties, Properties consumerProperties) {
        this.producerProperties = producerProperties;
        this.consumerProperties = consumerProperties;
        kafkaProducer = new KafkaProducer<String, String>(producerProperties);
        logger.info("Connected to Kafka server: {}", producerProperties.get("bootstrap.servers"));
    }

    @Override
    public void publish(String channel, String message) {
        kafkaProducer.send(new ProducerRecord<String, String>(channel, message));
    }

}
