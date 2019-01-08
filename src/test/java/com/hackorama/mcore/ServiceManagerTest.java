package com.hackorama.mcore;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hackorama.mcore.common.TestUtil;
import com.hackorama.mcore.config.Configuration;

public class ServiceManagerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        TestUtil.waitForService();
    }

    @Test
    public void workSpaceService_defaultStart_expectsNoErrors() throws FileNotFoundException, IOException {
        ServiceManager.start();
        TestUtil.waitForService();
        ServiceManager.stop();
    }

    @Test
    public void workSpaceService_startWithDependentServiceURLs_expectsNoErrors() throws FileNotFoundException, IOException {
        System.setProperty("service.environment.url", String.valueOf(Configuration.defaultConfig().environmentServiceURL()));
        System.setProperty("service.group.url", String.valueOf(Configuration.defaultConfig().groupServiceURL()));
        ServiceManager.start();
        TestUtil.waitForService();
        ServiceManager.stop();
    }

    @Test
    public void environmentService_defaultStart_expectsNoErrors() throws FileNotFoundException, IOException {
        System.setProperty("service.environment.port", String.valueOf(Configuration.defaultConfig().environmentServerPort()));
        ServiceManager.start();
        TestUtil.waitForService();
        ServiceManager.stop();
    }

    @Test
    public void groupService_defaultStart_expectsNoErrors() throws FileNotFoundException, IOException {
        System.setProperty("service.group.port", String.valueOf(Configuration.defaultConfig().groupServerPort()));
        ServiceManager.start();
        TestUtil.waitForService();
        ServiceManager.stop();
    }

}
