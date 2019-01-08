package com.hackorama.mcore.common;

import java.util.concurrent.TimeUnit;

public class TestUtil {

    private static final long DEFUALT_WAIT_SECONDS = 2;

    // Don't let anyone else instantiate this class
    private TestUtil() {
    }

    public static boolean waitForService() {
        return waitForSeconds(DEFUALT_WAIT_SECONDS);
    }

    public static boolean waitForSeconds(long seconds) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

}
