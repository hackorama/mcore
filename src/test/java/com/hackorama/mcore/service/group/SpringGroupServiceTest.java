package com.hackorama.mcore.service.group;

import com.hackorama.mcore.common.TestUtil;

public class SpringGroupServiceTest extends GroupServiceTest {

    @Override
    protected void setServer() {
        TestUtil.setServerTypeSpring();
    }

}
