package com.hackorama.mcore.common;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class TestUtil {

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

    public static boolean waitForPort(String host, int port, int timeOutSeconds) {
        int elapsedSeconds = 0;
        while (!usingPort(host, port)) {
            waitForSeconds(1);
            if (timeOutSeconds > 0 && elapsedSeconds++ > timeOutSeconds) {
                return false;
            }
        }
        return true;
    }

    public static boolean waitForSeconds(long seconds) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    public static boolean waitOnPort(String host, int port, int timeOutSeconds) {
        int elapsedSeconds = 0;
        while (usingPort(host, port)) {
            waitForSeconds(1);
            if (timeOutSeconds > 0 && elapsedSeconds++ > timeOutSeconds) {
                return false;
            }
        }
        return true;
    }
}
