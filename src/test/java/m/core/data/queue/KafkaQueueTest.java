package m.core.data.queue;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import m.core.common.TestService;
import m.core.data.kafka.KafkaDataQueue;

public class KafkaQueueTest {

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

    private static void openQueues() {
        queue = new KafkaDataQueue();
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        serverIntegrationTestsIsEnabled = TestService.getEnv("KAFKA_TEST");
        if (!serverIntegrationTestsIsEnabled) { // when not enabled, run tests only when there is a connection
            System.out.println("Skipping data tests since KAFKA_TEST server is not available");
            org.junit.Assume.assumeTrue(serverConnectionIsAvailable);
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        closeQueues();
    }

    private String receivedMessage = "";

    @Test
    public void addingConsumer_ExpectsNoErrors() throws InterruptedException {
        queue.consume("test-2", this::testHandler);
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
        openQueues();
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
