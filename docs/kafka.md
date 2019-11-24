# Kafka 

Setting up on Mac for dev

https://kafka.apache.org/quickstart
https://kafka.apache.org/downloads

`tar -xvf kafka_2.12-2.3.0.tar`

```
$ bin/zookeeper-server-start.sh config/zookeeper.properties
...
[2019-11-19 16:26:28,790] INFO binding to port 0.0.0.0/0.0.0.0:2181 (org.apache.zookeeper.server.NIOServerCnxnFactory)
```

```
$ bin/kafka-server-start.sh config/server.properties
...
[2019-11-19 16:28:01,820] INFO Connecting to zookeeper on localhost:2181 (kafka.server.KafkaServer)
...
[2019-11-19 16:28:03,113] INFO Kafka version: 2.3.0 (org.apache.kafka.common.utils.AppInfoParser)
...
[2019-11-19 16:28:03,115] INFO [KafkaServer id=0] started (kafka.server.KafkaServer)
```

```
$ bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic test
$ bin/kafka-topics.sh --list --bootstrap-server localhost:9092
test
$
```

```
$ bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning
$ bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
```

```
$ bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
>uno
>dos
>tres
>
$ bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning
uno
dos
tres
```

