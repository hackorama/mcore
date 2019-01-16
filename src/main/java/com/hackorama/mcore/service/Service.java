package com.hackorama.mcore.service;

import com.hackorama.mcore.data.DataStore;
import com.hackorama.mcore.server.Server;

public interface Service {
    public Service attach(Service service);

    public Service configureUsing(DataStore dataStore);

    public Service configureUsing(Server server);

    public Service start();

    public void stop();
}
