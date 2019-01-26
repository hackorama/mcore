package com.hackorama.mcore.service.environment;

import com.hackorama.mcore.common.TestUtil;

public class VertxEnvironmentServiceTest extends EnvironmentServiceTest {

    @Override
    protected void setServer() {
        TestUtil.setServerTypeVertx();
    }

}
