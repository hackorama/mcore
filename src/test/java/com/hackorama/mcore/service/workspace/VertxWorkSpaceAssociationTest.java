package com.hackorama.mcore.service.workspace;

import com.hackorama.mcore.common.TestUtil;

public class VertxWorkSpaceAssociationTest extends WorkSpaceAssociationTest {

    @Override
    protected void setServer() {
        TestUtil.setServerTypeVertx();
    }

}
