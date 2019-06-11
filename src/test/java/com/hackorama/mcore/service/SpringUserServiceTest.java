package com.hackorama.mcore.service;

import com.hackorama.mcore.common.TestService;

public class SpringUserServiceTest extends UserServiceTest {

    @Override
    protected void setServer() {
        TestService.setServerTypeSpring();
    }

}
