package com.hackorama.mcore.service;

import com.hackorama.mcore.common.TestUtil;

public class SpringGroupServiceTest extends GroupServiceTest {

    @Override
    protected void setServer() {
        TestUtil.setServerTypeSpring();
    }

}
