package com.hackorama.mcore.service.workspace;

import com.hackorama.mcore.common.TestUtil;

public class SpringWorkSpaceAssociationTest extends WorkSpaceAssociationTest {

    @Override
    protected void setServer() {
        TestUtil.setServerTypeSpring();
    }

}
