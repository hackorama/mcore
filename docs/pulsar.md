# Pulsar

On Mac/Linux

```
$ wget https://archive.apache.org/dist/pulsar/pulsar-2.4.2/apache-pulsar-2.4.2-bin.tar.gz
$ tar xvfz apache-pulsar-2.4.2-bin.tar.gz
$ cd apache-pulsar-2.4.2
```

```
$ bin/pulsar standalone
...
16:28:56.368 [main] INFO  org.apache.pulsar.broker.service.BrokerService - Started Pulsar Broker service on port 6650
...
```

```
$ bin/pulsar-admin clusters list
standalone
$ bin/pulsar-admin tenants list
public
sample
$ bin/pulsar-admin brokers list standlaone
devbox:8080
$ bin/pulsar-admin namespaces list public
public/default
public/functions
$ bin/pulsar-admin topics list public/default
persistent://public/default/test_one
persistent://public/default/test
$
```
