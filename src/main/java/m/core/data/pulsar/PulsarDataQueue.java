package m.core.data.pulsar;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import m.core.data.queue.DataQueue;

/**
 * DataQueue implementation for Apache Pulsar
 *
 * TODO: Expose additional client, consumer, producer configuration
 * TODO: Wrap Pulsar client exceptions as m.core exceptions
 */
public class PulsarDataQueue implements DataQueue {

    private static final String DEFAULT_SERVICE_URL = "pulsar://localhost:6650";
    private static final Logger logger = LoggerFactory.getLogger(PulsarDataQueue.class);
    private PulsarClient client;

    private static Map<String, ExecutorService> topicConsumerExecutors = new HashMap<>();
    private static Map<String, Boolean> consumerLiveness = new HashMap<>();
    private static Map<String, Consumer<byte[]>> topicConsumers = new HashMap<>();

    public PulsarDataQueue() {
        this(DEFAULT_SERVICE_URL);
    }

    public PulsarDataQueue(String serviceURL) {
        try {
            client = PulsarClient.builder().serviceUrl(serviceURL).build();
        } catch (PulsarClientException e) {
            logger.error("Error while building Pulsar client", e);
        }
    }

    @Override
    public void close() {
        if (client != null) {
            try {
                client.close();
            } catch (PulsarClientException e) {
                logger.error("Error while closing Pulsar client", e);
            }
        }
    }

    public void close(String channel) {
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
                    try {
                        Consumer<byte[]> consumer = client.newConsumer().topic(channel)
                                .subscriptionType(SubscriptionType.Shared).subscriptionName("mcore." + channel)
                                .subscribe();
                        topicConsumers.put(channel, consumer);
                    } catch (PulsarClientException e) {
                        logger.error("Exception while consuming from pulsar channel: " + channel, e);
                    }
                    consumerLiveness.put(channel, true);
                    while (consumerLiveness.get(channel)) {
                        Message<byte[]> message = null;
                        try {
                            message = topicConsumers.get(channel).receive();
                            logger.debug("Received message {} ({})", new String(message.getData()), message.getKey());
                        } catch (PulsarClientException e) {
                            logger.error("Exception while consuming from pulsar channel: " + channel, e);
                        }
                        if (message != null) {
                            handler.apply(new String(message.getData()));
                        }
                    }
                } finally {
                    logger.info("Closing channel {} ...", channel);
                    if (topicConsumers.get(channel) != null) {
                        try {
                            topicConsumers.get(channel).close();
                        } catch (PulsarClientException e) {
                            logger.error("Exception while closing pulsar channel: " + channel, e);
                        }
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

    @Override
    public void publish(String channel, String message) {
        try {
            Producer<byte[]> producer = client.newProducer().topic(channel).create();
            MessageId id = producer.newMessage().value(message.getBytes()).send();
            logger.debug("Send message {} ({})", message, id);
        } catch (PulsarClientException e) {
            logger.error("Exception while publishing to pulsar channel: " + channel, e);
        }
    }

}
