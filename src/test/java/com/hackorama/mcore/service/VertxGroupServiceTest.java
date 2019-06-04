package com.hackorama.mcore.service;

import com.hackorama.mcore.common.TestUtil;

public class VertxGroupServiceTest extends GroupServiceTest {

    @Override
    protected void setServer() {
        TestUtil.setServerTypeVertx();
    }

}
