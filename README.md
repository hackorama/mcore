# MCore

[![Build Status](https://travis-ci.org/hackorama/mcore.svg?branch=master)](https://travis-ci.org/hackorama/mcore)
[![Code Cov](https://codecov.io/gh/hackorama/mcore/branch/master/graph/badge.svg)](https://codecov.io/gh/hackorama/mcore)


Distributed Micro Service Design

# Framework Features and Status

## Web

| Framework | Minimal | Most | Complete | Performance |
| --- | --- | --- | --- | --- |
| Sparkjava | :heavy_check_mark: | | | |
| Spring | :heavy_check_mark: | | | |
| Vert.x | :heavy_check_mark: | | | |


## Data 

| Framework | Minimal | Most | Complete |  Performance |
| --- | --- | --- | --- | --- |
| H2 |  | :heavy_check_mark: | | |
| MapsDB |  | :heavy_check_mark: | | |
| MySQL |  :heavy_check_mark: | | | |
| Postgresql |   :heavy_check_mark: | | | |
| Sqlite | | | | |
| Derby | | | | |
| HSQL | | | | |
| Firebird | | | | |

## Cache

| Framework | Minimal | Most | Complete |  Performance |
| --- | --- | --- | --- | --- |
| Redis |  |  |  |  |
| Hazelcast |  |  |  |  |
| JCache |  |  |  |  |

## Queue

| Framework | Minimal | Most | Complete |  Performance |
| --- | --- | --- | --- | --- |
| RabbitMQ |  |  |  |  |
| Kafka |  |  |  |  |
| Redis |  |  |  |  |
| Hazelcast |  |  |  |  |

## Client

| Framework | Minimal | Most | Complete |  Performance |
| --- | --- | --- | --- | --- |
| Unirest |  | :heavy_check_mark: |  |  |
| Hystrix |  |  |  |  |
| Resilience4j |  |  |  |  |

# Build  
 
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
 
# Develop  

Build an Eclipse project  

```
$ ./gradlew eclipse 
```
 
Import project into Eclipse  


File -> Import -> Existing Projects into Workspace 
 
# Deploy 

## As a single service  

```
$ java -jar build/libs/mcore-all.jar 
47 [main] INFO ServiceManager - Starting services with in memory data store ... 
60 [main] INFO ServiceManager - Starting Workspace Service server on 0.0.0.0:4567 ... 
688 [main] WARN ServiceManager - No service url configured for Group Service, so starting a group service on the same server as Workspace Service ... 
688 [main] WARN ServiceManager - No service url configured for Environment Service, so starting an Environment Service on the same server as Workspace Service ... … 
700 [Thread-3] INFO log - Logging initialized @822ms to org.eclipse.jetty.util.log.Slf4jLog 
746 [Thread-3] INFO EmbeddedJettyServer - == Spark has ignited ... 
746 [Thread-3] INFO EmbeddedJettyServer - >> Listening on 0.0.0.0:4567 … 
```
 
## As separate services 

Start Group Directory Service 

```
$ java -Dservice.group.port=4565 -jar build/libs/mcore-all.jar 
48 [main] INFO ServiceManager - Starting services with in memory data store ... 
60 [main] INFO ServiceManager - Starting Group Service server on 0.0.0.0:4565 ... 
178 [Thread-0] INFO log - Logging initialized @325ms to org.eclipse.jetty.util.log.Slf4jLog 
232 [Thread-0] INFO EmbeddedJettyServer - == Spark has ignited ... 
232 [Thread-0] INFO EmbeddedJettyServer - >> Listening on 0.0.0.0:4565 
```
 
Start Environment Service 

```
$ java -Dservice.environment.port=4566 -jar build/libs/mcore-all.jar 
48 [main] INFO ServiceManager - Starting services with in memory data store ... 
61 [main] INFO ServiceManager - Starting Environment Service server on 0.0.0.0:4566 ... 
179 [Thread-0] INFO log - Logging initialized @300ms to org.eclipse.jetty.util.log.Slf4jLog 
232 [Thread-0] INFO EmbeddedJettyServer - == Spark has ignited ... 
232 [Thread-0] INFO EmbeddedJettyServer - >> Listening on 0.0.0.0:4566 
```
 
Start Workspace Service 
 
```
$ java -Dservice.group.url=http://127.0.0.1:4565 -Dservice.environment.url=http://127.0.0.1:4566 -jar build/libs/mcore-all.jar 
46 [main] INFO ServiceManager - Starting services with in memory data store ... 
58 [main] INFO ServiceManager - Starting Workspace Service server on 0.0.0.0:4567 ... 
682 [main] INFO ServiceManager - Using external Group Service at http://127.0.0.1:4565 
683 [main] INFO ServiceManager - Using external Environment Service at http://127.0.0.1:4566 
697 [Thread-3] INFO log - Logging initialized @817ms to org.eclipse.jetty.util.log.Slf4jLog 
743 [Thread-3] INFO EmbeddedJettyServer - == Spark has ignited ... 
743 [Thread-3] INFO EmbeddedJettyServer - >> Listening on 0.0.0.0:4567 
```
 
# Quick Test 

Following curl examples uses three different services 

```
$ java -Dservice.group.port=4565 -jar build/libs/mcore-all.jar 

$ java -Dservice.environment.port=4566 -jar build/libs/mcore-all.jar 

$ java -Dservice.group.url=http://127.0.0.1:4565 -Dservice.environment.url=http://127.0.0.1:4566 -jar build/libs/mcore-all.jar 
```
 
Running at these endpoints 

- http://127.0.0.1:4565 Group Service
- http://127.0.0.1:4566 Environment Service 
- http://127.0.0.1:4567 Workspace Service 

 
Create a Group 
 
```
$ curl -H  "Accept: application/json" -X POST -d  "{ \"name\" : \"group-one\", \"email\" : \"one@example.com\" }" http://127.0.0.1:4565/group 

{"name":"group-one","email":"one@example.com","id":"f2743bdc-9775-4a9f-9201-3484259ee885"} 
```
 
```
$ curl -H  "Accept: application/json" -X GET http://127.0.0.1:4565/group/f2743bdc-9775-4a9f-92013484259ee885 

{"name":"group-one","email":"one@example.com","id":"f2743bdc-9775-4a9f-9201-3484259ee885"} 
```
 
```
$ curl -H  "Accept: application/json" -X GET http://127.0.0.1:4565/group 

[{"name":"group-one","email":"one@example.com","id":"f2743bdc-9775-4a9f-9201-3484259ee885"}] 
```
 
Create an Environment 
 
```
$ curl -H  "Accept: application/json" -X POST -d  "{ \"name\" : \"env-one\" }" http://127.0.0.1:4566/environment 

{"name":"env-one","id":"306f3f85-2e20-466f-8b55-9dbe7506e501"} 
```
 
```
$ curl -H  "Accept: application/json" -X GET http://127.0.0.1:4566/environment/306f3f85-2e20-466f8b55-9dbe7506e501 

{"name":"env-one","id":"306f3f85-2e20-466f-8b55-9dbe7506e501"} 
```
 
```
$ curl -H  "Accept: application/json" -X GET http://127.0.0.1:4566/environment 

[{"name":"env-one","id":"306f3f85-2e20-466f-8b55-9dbe7506e501"}] 
```
 
Create a Workspace 
 
```
$ curl -H  "Accept: application/json" -X POST -d  "{ \"name\" : \"workspace-one\" }" 

http://127.0.0.1:4567/workspace {"name":"workspace-one","owners":[],"id":"ca6a182a-80bd-40d4-85e3-878610596fe2"} 
```
 
```
$ curl -H  "Accept: application/json" -X GET http://127.0.0.1:4567/workspace 

[{"name":"workspace-one","owners":[],"id":"ca6a182a-80bd-40d4-85e3-878610596fe2"}] 
```
 
```
$ curl -H  "Accept: application/json" -X GET http://127.0.0.1:4567/workspace/ca6a182a-80bd-40d4-85e3878610596fe2 

{"name":"workspace-one","owners":[],"id":"ca6a182a-80bd-40d4-85e3-878610596fe2"} 
```
 
Check if any Environment attached to Workspace 
 
```
$ curl -H  "Accept: application/json" -X GET http://127.0.0.1:4567/workspace/ca6a182a-80bd-40d4-85e3878610596fe2/environment 

[] 
```
 
Attach an Environment to workspace 
 
```
$ curl -H  "Accept: application/json" -X POST http://127.0.0.1:4567/workspace/ca6a182a-80bd-40d4-85e3878610596fe2/environment/306f3f85-2e20-466f-8b55-9dbe7506e501 

{"message":"Linked Environment 306f3f85-2e20-466f-8b55-9dbe7506e501 to Workspace ca6a182a-80bd-40d485e3-878610596fe2"} 
```
 
Verify the Environment is attached to workspace 
 
```
$ curl -H  "Accept: application/json" -X GET http://127.0.0.1:4567/workspace/ca6a182a-80bd-40d4-85e3878610596fe2/environment 

["306f3f85-2e20-466f-8b55-9dbe7506e501"] 
```
 
The Workspace have no owners added yet 
 
```
$ curl -H  "Accept: application/json" -X GET http://127.0.0.1:4567/workspace/ca6a182a-80bd-40d4-85e3878610596fe2 

{"name":"workspace-one","owners":[],"id":"ca6a182a-80bd-40d4-85e3-878610596fe2"}KITHO@USKITHO01:~ 
```
 
Add a Group to the owner list 
 
```
$ curl -H  "Accept: application/json" -X POST http://127.0.0.1:4567/workspace/ca6a182a-80bd-40d4-85e3878610596fe2/group/f2743bdc-9775-4a9f-9201-3484259ee885 

{"message":"Added Group f2743bdc-9775-4a9f-9201-3484259ee885 to Workspace ca6a182a-80bd-40d4-85e3878610596fe2"} 
```
 
Now the Workspace will return with owners populated in response 

```
$ curl -H  "Accept: application/json" -X GET http://127.0.0.1:4567/workspace/ca6a182a-80bd-40d4-85e3-878610596fe2 

{"name":"workspace-one","owners":[{"name":"group-one","email":"one@example.com","id":"f2743bdc-97754a9f-9201-3484259ee885"}],"id":"ca6a182a-80bd-40d4-85e3-878610596fe2"} 
```
  
 Use generic Component API to list all root level components like Workspaces 
  
```
$ curl -H  "Accept: application/json" -X GET http://127.0.0.1:4567/component 

{"workspaces":["ca6a182a-80bd-40d4-85e3-878610596fe2"]} 
```
  
 For each root level Workspace we can list the children components by type 
  
```
$ curl -H  "Accept: application/json" -X GET http://127.0.0.1:4567/component/ca6a182a-80bd-40d4-85e3878610596fe2 

{"environments":["306f3f85-2e20-466f-8b55-9dbe7506e50t1"]} 
```
  
 A component like Environment do not support children 
  
```
$ curl -H  "Accept: application/json" -X GET http://127.0.0.1:4567/component/306f3f85-2e20-466f-8b559dbe7506e501 

{"error":"Invalid Component"} 
```
  
# API 

NOTE: Singular naming used throughout API for entities, could switch to plural naming if so preferred. 

Return codes not listed due to time constraints but uses standard expected HTTP return codes according to accepted convention. 

## Group 

Allows standard CRUD operations for Group (Owner Group Directory) 
  
| Method | URL | Request | Response |
|--------|-----|---------|----------|
| GET | /group/:id  | | {"name":"group-one", "email":"one@example.com", "id":"f2743bdc-9775-4a9f-9201-3484259ee885"} |
| POST | /group | {"name":"group-one", "email":"one@example.com"} | {"name":"group-one", "email":"one@example.com", "id":"f2743bdc-9775-4a9f-9201-3484259ee885"} | 
| PUT | /group/:id | {"name":"group-one", "email":"one@example.com"} | {"name":"groupone", "email":"one@example.com", "id":"f2743bdc-9775-4a9f-9201-3484259ee885"} | 
| DELETE | /group/:id | | | 
  
## Environment Allows standard CRUD operations for Environment. 

| Method | URL | Request | Response |
|--------|-----|---------|----------|
| GET | /environment/:id |  | {"name":"env-one", "id":"f2743bdc-9775-4a9f-9201-3484259ee885"} |
| POST | /environment | {"name":"env-one"} | {"name":"env-one", "id":"f2743bdc-9775-4a9f-9201-3484259ee885"}  |
| PUT | /environment/:id | {"name"":"env-one"} |{"name":"env-one", "id":"f2743bdc-9775-4a9f-9201-3484259ee885"}  |
| DELETE | /environment/:id | | | 
  
## Workspace 

Allows standard CRUD operations for Workspace and also allows link/unlink of child component like Environment. 
Also allows adding/removing of owner groups for each Workspace. 

| Method | URL | Request | Response |
|--------|-----|---------|----------|
| GET | /workspace/:id  | | {"nameame":"workspaceone", "owners":[{"name":"groupone","email":"one@example.com","id":"f2743bdc-97754a9f-9201-3484259ee885"}], "id":"ca6a182a-80bd-40d4-85e3-878610596fe2"} | 
| POST | /workspace | {"name":"workspaceone"} | {"name":"workspace-one", "owners":[], "id":"ca6a182a-80bd-40d4-85e3-878610596fe2"}  |
| PUT | /workspace/:id | {"name":"workspaceone"} | {"name":"workspace-one", "owners":[{"name":"groupone","email":"one@example.com","id":"f2743bdc-97754a9f-9201-3484259ee885"}], "id":"ca6a182a-80bd-40d4-85e3-878610596fe2"} | 
| DELETE | /workspace/:id | | | 
| GET | /workspace/:id/environment | |  ["306f3f85-2e20-466f-8b55-9dbe7506e501"] |
| POST | /workspace/:id/environment/:envid | | {"message":"Linked Environment 306f3f85-2e20-466f8b55-9dbe7506e501 to Workspace ca6a182a-80bd-40d485e3-878610596fe2"} |
| DELETE | /workspace/:id/environment/:envid | | {"message":"Unlinked Environment 306f3f85-2e20466f-8b55-9dbe7506e501 frpm Workspace ca6a182a80bd-40d4-85e3-878610596fe2"} | 
| POST | /workspace/:id/group/:groupid | | {"message":"Added Group 306f3f85-2e20-466f-8b559dbe7506e501 to Workspace ca6a182a-80bd-40d4-85e3878610596fe2"} | 
| DELETE | /workspace/:id/group/:groupid | | {"message":"Removed Group 306f3f85-2e20-466f-8b559dbe7506e501 from Workspace ca6a182a-80bd-40d485e3-878610596fe2"} |
 
## Component 

Provided by Workspace Service. 

Allows to navigate the component tree from root nodes to all the children nodes by recursively using component ids returned from root level.

| Method | URL | Request | Response |
|--------|-----|---------|----------|
| GET | /component| | Return root nodes: {"workspaces":["ca6a182a-80bd-40d4-85e3878610596fe2"]} | 
| GET | /component/:id | | For components allowing children: {"environments":["306f3f85-2e20-466f-8b559dbe7506e50t1"]} If a component does not allow children: {"error":"Invalid Component"} |
 
# Design 
 
Approach Pragmatic adoption of Twelve-Factor App, SOLID, YAGNI, DRY principles in the design.  

## Service design 

Complete separation of each service from underlying web framework and data store framework through interfaces.
So different web frameworks and data stores can be easily used without changing the service code at all.  

Services are composed fluently by injecting web, data and client implementation as needed.  

Provides implementation of [three different web frameworks](src/main/java/com/hackorama/mcore/server) to choose from Spark, Spring Web Flux, Vert.x.

```
Service groupService = new GroupService().configureUsing(new SparkServer("group")).configureUsing(new MapdbDataStore()); 
```
A service manager based on external configuration automatically starts each service. With no configuration provided starts all services in the same server for quick testing.

## Data design 

Since each entity runs its own separate service we are using separate data store for each service and only workspace service keeps the relation tables that it depends on.
Work Space to Environment is One to Many association. Work Space to Groups is Many to Many association. 
Both association allows zero or one/many since each service operates independently from each other. 

Instead of a monolithic single data store with foreign keys we are using a key value approach with entities stored as json document keyed using unique key. 

Instead of auto incrementing sequence we are using application generated UUID as the key which will be better for distributed servers. 

Using an in memory data store implementation by default. A [MapDB](src/main/java/com/hackorama/mcore/data/mapdb) and [JDBC](src/main/java/com/hackorama/mcore/data/jdbc) (tested with MySQL, Postgres, H2) data stores are also implemented.

Plans to support MongoDB, RocksDB in future.

### Data Cache

Plans to implement a generic caching interface with support planned for Redis, Hazelcast etc.

### Data Message Queue

Plans to implement a generic message queue interface with support planned for RabbitMQ, Kafka etc.

## Distributed service design

Workspaces depends on other two services for association lookups. 

During creation of association we try to validate the dependent entities if service is available, if not available we optimistically do the association anyway.

Associations are also checked during the read of workspace to see if the associated entity is still valid and associations are updated. 

This is also only if the service is available. 

These association updating behavior will be changed when the [message queue service](src/main/java/com/hackorama/mcore/data/queue) is available through messaging.


## Proposed Design Improvements 

Instead of workspace directly talking to other services introduces a message queue where other services can publish changes so workspace service can update the associations based on these entity change events.

- Introduce the data repository separation in each service initializedmplementation. 
- Clustering of services with proxy in the front. 
- Separate Data Store with its own clustering.

# Testing 

Integrated into Gradle build 

- SpotBugs : build/reports/spotbugs/main.html (0 errors)
- JUnit : build/reports/tests/test/index.html (100%) 
- Coverage: build/reports/jacoco/test/html/index.html (83%) 

# To Do 

- Complete Java Doc in code and generate the Java Docs in Gradle build
- Add OpenAPI definitions and create API interactive dashboard with help 
- Include Postman file for API testing 
 

