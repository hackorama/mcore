package com.hackorama.mcore.service.environment;

import com.hackorama.mcore.common.TestUtil;

public class SpringEnvironmentServiceTest extends EnvironmentServiceTest {

    @Override
    protected void setServer() {
        TestUtil.setServerTypeSpring();
    }

}
