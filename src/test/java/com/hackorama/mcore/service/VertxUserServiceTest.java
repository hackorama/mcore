package com.hackorama.mcore.service;

import com.hackorama.mcore.common.TestUtil;

public class VertxUserServiceTest extends UserServiceTest {

    @Override
    protected void setServer() {
        TestUtil.setServerTypeVertx();
    }

}
