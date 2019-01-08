package com.hackorama.mcore.config;

import org.aeonbits.owner.Config;

public interface ClientConfig extends Config {
    @Key("client.cache.entries.count")
    @DefaultValue("1000")
    int clientCacheEntriesCount();

    @Key("client.cache.object.size,bytes")
    @DefaultValue("8192")
    long clientCacheMaxObjectSizeBytes();

    @Key("client.connect.timeout.millis")
    @DefaultValue("50000")
    int clientConnectTimeoutMillis();

    @Key("client.socket.timeout.millis")
    @DefaultValue("50000")
    int clientSocketTimeoutMillis();
}
