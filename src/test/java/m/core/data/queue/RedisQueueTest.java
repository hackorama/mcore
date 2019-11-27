package m.core.data.queue;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import m.core.common.TestService;
import m.core.data.redis.RedisDataStoreCacheQueue;

public class RedisQueueTest {

    private static DataQueue queue;
    private static boolean serverConnectionIsAvailable;
    private static boolean serverIntegrationTestsIsEnabled;
    private static void closeQueues() throws InterruptedException {
        if (queue != null) {
            // wait for any errors from consumer threads
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            queue.close();
        }
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        serverIntegrationTestsIsEnabled = TestService.getEnv("REDIS_TEST");
        if (!serverIntegrationTestsIsEnabled) { // when not enabled, run tests only when there is a connection
            System.out.println("Skipping data tests since REDIS_TEST server is not available");
            org.junit.Assume.assumeTrue(serverConnectionIsAvailable);
        } else {
            try {
                queue = new RedisDataStoreCacheQueue();
                serverConnectionIsAvailable = true;
            } catch (Exception e) {
                fail("Redis queue connection failed"); // Fail fast instead of each testing failing
                e.printStackTrace();
            }
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        closeQueues();
    }

    private String receivedMessage = "";

    @Test
    public void addingConsumer_ExpectsNoErrors() {
        queue.consume("test", this::testHandler);
    }

    @Test
    public void publishingMessage_ExpectsConsumingHandlerToRecieve() throws InterruptedException {
        receivedMessage = "";
        String publishedMessage = "hello test one";
        queue.consume("test_one", this::testHandler);
        queue.publish("test_one", publishedMessage);
        // wait for handler invocation
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
        assertEquals(publishedMessage, receivedMessage);
    }

    @Test
    public void publishingMessage_ExpectsNoErrros() {
        queue.publish("test", "hello");
    }

    public boolean testHandler(String message) {
        receivedMessage = message;
        return true;
    }
}
