# MCore

[![Build Status](https://travis-ci.org/hackorama/mcore.svg?branch=master)](https://travis-ci.org/hackorama/mcore)
[![Code Cov](https://codecov.io/gh/hackorama/mcore/branch/master/graph/badge.svg)](https://codecov.io/gh/hackorama/mcore)

>[Demo](https://github.com/hackorama/mcore-demo)

Distributed Micro Service Design

## Framework Features and Status

### Web

| Framework | Minimal | Most | Complete | Performance |
| --- | --- | --- | --- | --- |
| Sparkjava | :heavy_check_mark: | | | |
| Spring | :heavy_check_mark: | | | |
| Vert.x | :heavy_check_mark: | | | |


### Data

| Framework | Minimal | Most | Complete |  Performance |
| --- | --- | --- | --- | --- |
| H2 |  | :heavy_check_mark: | | |
| MongoDB | | | | |
| Redis |  | :heavy_check_mark: | | |
| MapsDB |  | :heavy_check_mark: | | |
| MySQL |  :heavy_check_mark: | | | |
| Postgresql |   :heavy_check_mark: | | | |
| Sqlite | | | | |
| Derby | | | | |
| HSQL | | | | |
| Firebird | | | | |

### Cache

| Framework | Minimal | Most | Complete |  Performance |
| --- | --- | --- | --- | --- |
| Redis | :heavy_check_mark: | | | |
| Hazelcast |  |  |  |  |
| JCache |  |  |  |  |

### Queue

| Framework | Minimal | Most | Complete |  Performance |
| --- | --- | --- | --- | --- |
| RabbitMQ |  |  |  |  |
| Kafka |  |  |  |  |
| Redis |  |  |  |  |
| Hazelcast |  |  |  |  |

### Client

| Framework | Minimal | Most | Complete |  Performance |
| --- | --- | --- | --- | --- |
| Unirest |  | :heavy_check_mark: |  |  |
| Hystrix |  |  |  |  |
| Resilience4j |  |  |  |  |

## Build

Requires Java 8 or later in path and web access for pulling in the maven dependencies.

```
$ git clone git@github.com:hackorama/mcore.git
$ cd mcore
```

If you are behind a corp web proxy, please uncomment and add the proxy servers in gradle.properties.

Without the proxy set, you will see build errors failing to download the Maven dependencies, like “Could not determine the dependencies of task …”, “Unable to load Maven meta-data from https://jcenter.bintray.com …”)

```
$ vi gradle.properties
```

```
$ ./gradlew build
```

## Develop

Build an Eclipse project

```
$ ./gradlew eclipse
```

Import project into Eclipse


File -> Import -> Existing Projects into Workspace

## Design

Approach Pragmatic adoption of Twelve-Factor App, SOLID, YAGNI, DRY principles in the design.

### Service design

Complete separation of each service from underlying web framework and data store framework through interfaces.
So different web frameworks and data stores can be easily used without changing the service code at all.

Services are composed fluently by injecting web, data and client implementation as needed.

Provides implementation of [three different web frameworks](src/main/java/com/hackorama/mcore/server) to choose from Spark, Spring Web Flux, Vert.x.

```
Service helloService = new HelloService().configureUsing(new SparkServer("hello")).configureUsing(new MapdbDataStore());
```
A service manager based on external configuration automatically starts each service. With no configuration provided starts all services in the same server for quick testing.

### Data design

Instead of auto incrementing sequence we are using application generated UUID as the key which will be better for distributed servers.

Using an in memory data store implementation by default. A [MapDB](src/main/java/com/hackorama/mcore/data/mapdb) and [JDBC](src/main/java/com/hackorama/mcore/data/jdbc) (tested with MySQL, Postgres, H2) data stores are also implemented.

Plans to support MongoDB, RocksDB in future.

#### Data Cache

Plans to implement a generic caching interface with support planned for Redis, Hazelcast etc.

#### Data Message Queue

Plans to implement a generic message queue interface with support planned for RabbitMQ, Kafka etc.


## Testing

Integrated into Gradle build

- SpotBugs : build/reports/spotbugs/main.html (0 errors)
- JUnit : build/reports/tests/test/index.html (100%)
- Coverage: build/reports/jacoco/test/html/index.html (83%)

## To Do

- Complete Java Doc in code and generate the Java Docs in Gradle build
- Add OpenAPI definitions and create API interactive dashboard with help
- Include Postman file for API testing


