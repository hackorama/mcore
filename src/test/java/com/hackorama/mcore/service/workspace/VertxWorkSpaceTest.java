package com.hackorama.mcore.service.workspace;

import com.hackorama.mcore.common.TestUtil;

public class VertxWorkSpaceTest extends WorkSpaceTest {

    @Override
    protected void setServer() {
        TestUtil.setServerTypeVertx();
    }

}
