package com.hackorama.mcore.service.workspace;

import com.hackorama.mcore.common.TestUtil;

public class SpringWorkSpaceTest extends WorkSpaceTest {

    @Override
    protected void setServer() {
        TestUtil.setServerTypeSpring();
    }

}
