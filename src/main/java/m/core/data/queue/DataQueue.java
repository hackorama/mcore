package m.core.data.queue;

import java.util.function.Function;


public interface DataQueue {

    public void publish(String channel, String message);

    public void consume(String channel, Function<String, Boolean> handler);

    public void close();

}
