package com.hackorama.mcore.service;

import com.hackorama.mcore.common.TestUtil;

public class VertxServiceTest extends SpringServiceTest {

    @Override
    protected void setServer() {
        TestUtil.setServerTypeVertx();
    }
}
