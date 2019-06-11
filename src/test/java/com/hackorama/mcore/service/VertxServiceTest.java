package com.hackorama.mcore.service;

import com.hackorama.mcore.common.TestService;

public class VertxServiceTest extends CommonServiceTest {

    @Override
    protected void setServer() {
        TestService.setServerTypeVertx();
    }

}
