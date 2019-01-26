package com.hackorama.mcore.service;

import com.hackorama.mcore.common.TestUtil;

/**
 * Tests for Spark server implementation
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class SparkServiceTest extends SpringServiceTest {

    @Override
    protected void setServer() {
        TestUtil.setServerTypeSpark();
    }

}
