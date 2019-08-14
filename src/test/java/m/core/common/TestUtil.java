package m.core.common;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class TestUtil {

    private static final long DEFAULT_POLL_INTERVAL_MS = TimeUnit.SECONDS.toMillis(1);
    private static final long DEFAULT_SERVICE_CLOSE_WAIT_MS = 0;
    private static final long DEFAULT_SERVICE_READY_WAIT_MS = TimeUnit.SECONDS.toMillis(1);
    private static final long DEFAULT_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(60);

    public static boolean usingPort(String host, int port) {
        Socket socket = null;
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return socket != null;
    }

    public static boolean waitForMilliSeconds(long milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    public static boolean waitForPortDown(String host, int port) {
        return waitForPortDown(host, port, DEFAULT_TIMEOUT_MS);
    }

    public static boolean waitForPortDown(String host, int port, long timeOutMilliSeconds) {
        return waitForPortDown(host, port, timeOutMilliSeconds, DEFAULT_POLL_INTERVAL_MS);
    }

    public static boolean waitForPortDown(String host, int port, long timeOutMilliSeconds, long pollIntervalMilliSeconds) {
        int elapsedMilliSeconds = 0;
        while (usingPort(host, port)) {
            waitForMilliSeconds(pollIntervalMilliSeconds);
            elapsedMilliSeconds += pollIntervalMilliSeconds;
            if (timeOutMilliSeconds > 0 && elapsedMilliSeconds > timeOutMilliSeconds) {
                return false;
            }
        }
        // Additional wait for any service shutdown after port is closed
        waitForMilliSeconds(DEFAULT_SERVICE_CLOSE_WAIT_MS);
        return true;
    }

    public static boolean waitForPortUp(String host, int port) {
        return waitForPortUp(host, port, DEFAULT_TIMEOUT_MS);
    }

    public static boolean waitForPortUp(String host, int port, long timeOutMilliSeconds) {
        return waitForPortUp(host, port, timeOutMilliSeconds, DEFAULT_POLL_INTERVAL_MS);
    }

    public static boolean waitForPortUp(String host, int port, long timeOutMilliSeconds, long pollIntervalMilliSeconds) {
        int elapsedMilliSeconds = 0;
        while (!usingPort(host, port)) {
            waitForMilliSeconds(pollIntervalMilliSeconds);
            elapsedMilliSeconds += pollIntervalMilliSeconds;
            if (timeOutMilliSeconds > 0 && elapsedMilliSeconds > timeOutMilliSeconds) {
                return false;
            }
        }
        // Additional wait for any service initialization after port is up
        waitForMilliSeconds(DEFAULT_SERVICE_READY_WAIT_MS);
        return true;
    }

    public static boolean waitForSeconds(long seconds) {
        return waitForMilliSeconds(TimeUnit.SECONDS.toMillis(seconds));
    }
}
