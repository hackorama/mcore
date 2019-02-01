package com.hackorama.mcore.data.redis;

import java.util.function.Function;

import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoutingMessageListner implements MessageListener<String> {

    private static final Logger logger = LoggerFactory.getLogger(RoutingMessageListner.class);

    Function<String, Boolean> handler;

    public RoutingMessageListner(Function<String, Boolean> handler) {
        logger.debug("Adding listner {}", handler.toString());
        this.handler = handler;
    }

    @Override
    public void onMessage(String channel, String msg) {
        logger.debug("Applying handler {}, channel {}, message {}", handler.toString(), channel, msg);
        handler.apply(msg);
    }

}
