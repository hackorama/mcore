package com.hackorama.mcore.server;

import static org.junit.Assert.*;

import org.junit.Test;

import com.hackorama.mcore.server.vertx.VertxServer;

public class ServerTest {

    @Test
    public void testVertxServer() {
        Server server = new VertxServer("echo");
        assertTrue(server.start());
        server.stop();
        server = new VertxServer("echo");
        assertTrue(server.start());
        server.stop();
    }

}
