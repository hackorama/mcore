package m.core.data.queue;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import m.core.common.TestService;

public abstract class QueueTest {

    protected static AtomicBoolean once = new AtomicBoolean(false);
    protected static DataQueue queue;
    protected static String type;

    private static void closeQueues() throws InterruptedException {
        if (queue != null) {
            // wait for any errors from consumer threads
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            queue.close();
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        closeQueues();
    }

    private String receivedMessage = "";

    @Test
    public void addingConsumer_ExpectsNoErrors() throws InterruptedException {
        queue.consume("test", this::testHandler);
    }

    protected abstract void createQueue();

    protected abstract String getType();

    private void openQueues() {
        try {
            createQueue();
        } catch (Exception e) {
            fail("Queue connection failed for " + type); // Fail fast instead of each testing failing
            e.printStackTrace();
        }
    }

    @Test
    public void publishingMessage_ExpectsConsumingHandlerToRecieve() throws InterruptedException {
        receivedMessage = "";
        String publishedMessage = "hello test one";
        queue.consume("test_one", this::testHandler);
        queue.publish("test_one", publishedMessage);
        // wait for handler invocation and any errors from publisher/consumer threads
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        assertEquals(publishedMessage, receivedMessage);
    }

    @Test
    public void publishingMessage_ExpectsNoErrros() throws InterruptedException {
        queue.publish("test", "hello");
    }

    @Before
    public void setup() throws Exception {
        if (TestService.getEnv(getType())) {
            openQueues();
        } else {
            System.out.println("Skipping data tests since " + getType() + " server is not available");
            org.junit.Assume.assumeTrue(false);
        }
    }

    @After
    public void tearDown() throws Exception {
        closeQueues();
    }

    public boolean testHandler(String message) {
        receivedMessage = message;
        return true;
    }
}
