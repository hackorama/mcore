# m.core Microservices Framework

[![Build Status](https://travis-ci.org/hackorama/mcore.svg?branch=master)](https://travis-ci.org/hackorama/mcore)
[![Code Cov](https://codecov.io/gh/hackorama/mcore/branch/master/graph/badge.svg)](https://codecov.io/gh/hackorama/mcore)

A microservices framework with complete separation of service code from underlying frameworks which can be interchanged without service code changes.

- Complete separation of each service from underlying web framework and data store framework through interfaces.
- So different web frameworks and data stores can be easily used without changing the service code at all.
- Services are composed fluently by injecting web, data, queue, cache and client implementation as needed.
- Instead of auto incrementing sequence auto generated UUID is used for keys (to support distributed servers).

| Tier | Supported | Planned |
| --- | --- | --- |
| Web | Sparkjava, Spring Web Flux, Vert.x, Play | Micronaut |
| Client | Unirest | Resilience4j |
| Data | H2, Redis, MapsDB, MySQL, Postgresql, Sqlite | MongoDB, RocksDB, Sqlite, Firebird, Derby, HSQL |
| Cache | Redis | Hazelcast, JCache |
| Queue | Redis, Kafka, Pulsar | RabbitMQ, Hazelcast |

## Docs

[Javadoc](https://www.javadoc.io/doc/com.hackorama.m.core/mcore/0.1.0)

## Packages

[com.hackorama.m.core:mcore:0.1.0](https://search.maven.org/artifact/com.hackorama.m.core/mcore/0.1.0/jar)

Signed with PGP key: D791 8556 0576 7757 6235 6402 6C13 01F5 1B93 B750 mcore@hackorama.com

## Build

`./gradlew build`

## Code

### Simple service

See [HelloService.java](samples/src/main/java/m/core/samples/HelloService.java), more [samples](samples/src/main/java/m/core/samples).

```
dependencies {
    implementation 'com.hackorama.m.core:mcore:0.1.0'
}
```

```
new Service() {
    private Response hello(Request request) {
        if (request.getPathParams().containsKey("name")) {
            return new Response("Hello " + request.getPathParams().get("name"));
        }
        return new Response("Hello world");
    }
    @Override
    public void configure() {
        GET("/hello", this::hello);
        GET("/hello/{name}", this::hello);
    }

}.configureUsing(new SparkServer("Hello")).start();
```

### Launching service using different frameworks

See [UserService.java](samples/src/main/java/m/core/samples/UserService.java)

Service started with Sparkjava server and an in-memory database.

```
Service userService = new UserService().configureUsing(new SparkServer("User Service"))
                .configureUsing(new MemoryDataStore()).start();
```

Same service can also be started with Spring Flux web server and a database like Postgresql/MySQL using JDBC

```
Service userService = new UserService().configureUsing(new SpringServer("User Service"))
                .configureUsing(new JDBCDataStore()).start();
```

For this there is no service code changes needed.

If you check the [UserService.java](samples/src/main/java/m/core/samples/UserService.java) there is no framework specific (Sparkjava/Spring Flux) packages imported into the service class, providing complete separation of service from the underlying frameworks.



