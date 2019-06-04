package com.hackorama.mcore.service;

import com.hackorama.mcore.common.TestUtil;

public class SpringUserServiceTest extends UserServiceTest {

    @Override
    protected void setServer() {
        TestUtil.setServerTypeSpring();
    }

}
