package m.core.data.queue;

import java.util.function.Function;

/**
 * A key value data-queue.
 * <p>
 * The messages of this data-queue are of {@code String} type.
 * <p>
 * Messages are published and consumed through channels identified by a unique
 * name.
 *
 * The channels get created on the first insertion of a message.
 *
 * @implSpec The channel behavior for message retention, delete on consumption,
 *           acknowledgments will depend on the underlying message queue
 *           implementation.
 */
public interface DataQueue {

    /**
     * Publish the specified message to the specified channel in this data-queue.
     *
     * @param channel the channel name
     * @param message the message
     */
    public void publish(String channel, String message);

    /**
     * Consume messages from the specified channel in this data-queue.
     *
     * @param channel the channel name
     * @param handler the message consumer handler method
     */
    public void consume(String channel, Function<String, Boolean> handler);

    /**
     * Closes this data-queue.
     * <p>
     * Closes all open resources (file handles or network connections) of this
     * data-queue.
     */
    public void close();

}
