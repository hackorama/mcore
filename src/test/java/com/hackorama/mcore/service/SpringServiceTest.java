package com.hackorama.mcore.service;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.common.TestUtil;

/**
 * Tests for Spring server implementation
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class SpringServiceTest extends CommonServiceTest {

    @Before
    public void setUp() throws Exception {
        TestUtil.setServerTypeSpring();
        TestUtil.initGroupServiceInstance();
    }

    @After
    public void tearDown() throws Exception {
        TestUtil.clearDataOfServiceInstance();
    }

    @AfterClass
    public static void afterAllTests() throws Exception {
        TestUtil.stopServiceInstance();
        TestUtil.resetServerType();
    }

    @Test
    public void service_getResource_expectsOKStataus() throws UnirestException {
        super.service_getResource_expectsOKStataus();
    }

    @Test
    public void workspaceService_postResource_expectsOKStataus() throws UnirestException {
        super.workspaceService_postResource_expectsOKStataus();
    }

    @Test
    public void workspaceService_invalidURL_expectsNotFoundStatus() throws UnirestException {
        super.service_invalidURL_expectsNotFoundStatus();
    }

    @Test
    public void service_postingMultiple_expectsSameOnGetAll() throws UnirestException {
        super.service_postingMultiple_expectsSameOnGetAll();
    }

    @Test
    public void service_getEntity_expectsMatchingEntity() throws UnirestException {
        super.service_getEntity_expectsMatchingEntity();
    }

    @Test
    public void service_deleteEntity_expectsEntityRemoved() throws UnirestException {
        super.service_deleteEntity_expectsEntityRemoved();
    }

    @Test
    public void service_updateEntity_expectsUpdatedEntity() throws UnirestException {
        super.service_updateEntity_expectsUpdatedEntity();
    }
}
