package com.hackorama.mcore.service;

import com.hackorama.mcore.common.TestService;

public class VertxUserServiceTest extends UserServiceTest {

    @Override
    protected void setServer() {
        TestService.setServerTypeVertx();
    }

}
