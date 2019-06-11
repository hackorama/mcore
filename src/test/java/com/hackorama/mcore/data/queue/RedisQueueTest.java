package com.hackorama.mcore.data.queue;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hackorama.mcore.common.TestService;
import com.hackorama.mcore.data.redis.RedisDataStoreCacheQueue;

public class RedisQueueTest {

    private static DataQueue queue;
    private static boolean serverIntegrationTestsIsEnabled;
    private static boolean serverConnectionIsAvailable;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        serverIntegrationTestsIsEnabled = TestService.getEnv("REDIS_TEST");
        try {
            queue = new RedisDataStoreCacheQueue();
            serverConnectionIsAvailable = true;
        } catch (Exception e) {
            if (serverIntegrationTestsIsEnabled) {
                fail("Redis queue connection failed"); // Fail fast instead of each testing failing
            }
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (queue != null) {
            queue.close();
        }
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
        TimeUnit.MILLISECONDS.sleep(100); // wait for handler invocation
        assertEquals(receivedMessage, publishedMessage);
    }

    @Test
    public void publishingMessage_ExpectsNoErrros() {
        queue.publish("test", "hello");
    }

    @Before
    public void setUp() throws Exception {
        if (!serverIntegrationTestsIsEnabled) { // when not enabled, run tests only when there is a connection
            org.junit.Assume.assumeTrue(serverConnectionIsAvailable);
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    public boolean testHandler(String message) {
        receivedMessage = message;
        return true;
    }
}
