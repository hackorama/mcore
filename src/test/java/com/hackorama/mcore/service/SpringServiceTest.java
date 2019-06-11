package com.hackorama.mcore.service;

import com.hackorama.mcore.common.TestService;

/**
 * Tests for Spring server implementation
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class SpringServiceTest extends CommonServiceTest {

    protected void setServer() {
        TestService.setServerTypeSpring();
    }

}
