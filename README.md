# MCore

Distributed Micro Service Design 

# Build  
 
Requires Java 8 or later in path and web access for pulling in the maven dependencies. 

```
$ tar –xzvf mcore.tar.gz $ cd mcore 
```
 
If you are behind a corp web proxy, please uncomment and add the proxy servers in gradle.properties.

Without the proxy set, you will see build errors failing to download the Maven dependencies, like “Could not determine the dependencies of task …”, “Unable to load Maven meta-data from https://jcenter.bintray.com …”) 

```
$ vi gradle.properties 
```

```
$ ./gradlew build 
```
 
# Develop  Build an Eclipse project  

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

NOTE: Singular naming used throughout API for entities, could switch to plural naming if so preferred. Return codes not listed due to time constraints but uses standard expected HTTP return codes according to accepted convention. Group Allows standard CRUD operations for Group (Owner Group Directory) Method URL Request Response GET /group/:id  {"name":"group-one", "email":"one@example.com", "id":"f2743bdc-9775-4a9f-9201-3484259ee885"} POST /group {"name":"group-one", "email":"one@example.com"} {"name":"group-one", "email":"one@example.com", "id":"f2743bdc-9775-4a9f-9201-3484259ee885"} PUT /group/:id {"name":"group-one", "email":"one@example.com"} {"name":"groupone", "email":"one@example.com", "id":"f2743bdc-9775-4a9f-9201-3484259ee885"} DELETE /group/:id   
  
## Environment Allows standard CRUD operations for Environment. 

| Method | URL | Request | Response |
+--------+-----+---------+----------+
| GET | /environment/:id |  | {"name":"env-one", "id":"f2743bdc-9775-4a9f-9201-3484259ee885"} |
| POST | /environment | {"name":"env-one"} | {"name":"env-one", "id":"f2743bdc-9775-4a9f-9201-3484259ee885"}  |
| PUT | /environment/:id | {"name"":"env-one"} |{"name":"env-one", "id":"f2743bdc-9775-4a9f-9201-3484259ee885"}  |
| DELETE | /environment/:id | 
  
## Workspace 

Allows standard CRUD operations for Workspace and also allows link/unlink of child component like Environment. 
Also allows adding/removing of owner groups for each Workspace. 

Method URL Request Response 

GET /workspace/:id  {"name":"workspaceone", "owners":[{"name":"groupone","email":"one@example.com","id":"f2743bdc-97754a9f-9201-3484259ee885"}], "id":"ca6a182a-80bd-40d4-85e3-878610596fe2"} 
  
POST /workspace {"name":"workspaceone"} 
{"name":"workspace-one", "owners":[], "id":"ca6a182a-80bd-40d4-85e3-878610596fe2"} 
 
PUT /workspace/:id {"name":"workspaceone"} 
{"name":"workspace-one", "owners":[{"name":"groupone","email":"one@example.com","id":"f2743bdc-97754a9f-9201-3484259ee885"}], "id":"ca6a182a-80bd-40d4-85e3-878610596fe2"} DELETE /workspace/:id   GET /workspace/:id/environm ent  ["306f3f85-2e20-466f-8b55-9dbe7506e501"] POST /workspace/:id/environm ent/:envid  {"message":"Linked Environment 306f3f85-2e20-466f8b55-9dbe7506e501 to Workspace ca6a182a-80bd-40d485e3-878610596fe2"} DELETE /workspace/:id/environm ent/:envid  {"message":"Unlinked Environment 306f3f85-2e20466f-8b55-9dbe7506e501 frpm Workspace ca6a182a80bd-40d4-85e3-878610596fe2"} POST /workspace/:id/group/:gr oupid  {"message":"Added Group 306f3f85-2e20-466f-8b559dbe7506e501 to Workspace ca6a182a-80bd-40d4-85e3878610596fe2"} DELETE /workspace/:id/group/:gr oupid  {"message":"Removed Group 306f3f85-2e20-466f-8b559dbe7506e501 from Workspace ca6a182a-80bd-40d485e3-878610596fe2"} 
 
Component Provided by Workspace Service. Allows to navigate the component tree from root nodes to all the children nodes by recursively using component ids returned from root level. Method URL Request Response GET /component  Return root nodes: 
 {"workspaces":["ca6a182a-80bd-40d4-85e3878610596fe2"]} 
  
GET  /component/:id  For components allowing children: 
  {"environments":["306f3f85-2e20-466f-8b559dbe7506e50t1"]} 
   
  If a component does not allow children: 
   
{"error":"Invalid Component"} 
 
 
# Design 
 
Approach Pragmatic adoption of Twelve-Factor App, SOLID, YAGNI, DRY principles in the design.  


Service design Complete separation of each service from underlying web framework and data store framework through interfaces.  So different web frameworks and data stores can be easily used without changing the service code at all.  Services are composed fluently by injecting web, data and client implementation as needed.  


```
Service groupService = new GroupService().configureUsing(new SparkServer("group")).configureUsing(new MapdbDataStore()); 
```

A service manager based on external configuration automatically starts each service or out of the box without and configuration starts all services in the same server for quick testing. Data design Since each entity runs its own separate service we are using separate data store for each service and only workspace service keeps the relation tables that it depends own. Work Space to Environment is One to Many association. Work Space to Groups is Many to Many association. Both association allows zero or one/many since each service operates independently from each other. Instead of a monolithic single data store with foreign keys we are using a key value approach with entities stored as json document keyed using unique key. Instead of auto incrementing sequence we are using application generated UUID as the key which will be better for distributed servers. Using an in memory data store implementation now. A MapDB based implementation is next. Distributed service design Workspaces depends on other two services for association lookups. During creation of association we try to validate the dependent entities if service is available, if not we optimistically do the association anyway.  Associations are also checked during the read of workspace to see if the associated entity is still valid and associations are updated. This is also only if the service is available. Proposed Design Improvements Instead of workspace directly talking to other services introduces a message queue where other services can publish changes so workspace service can update the associations based on these entity change events. Introduce the data repository separation in each service implementation. Clustering of services with proxy in the front. Separate Data Store with its own clustering.

# Testing 

Integrated into Gradle build 

- SpotBugs : build/reports/spotbugs/main.html (0 errors)
- JUnit : build/reports/tests/test/index.html (100%) 
- Coverage: build/reports/jacoco/test/html/index.html (83%) 

# To Do 

- Complete Java Doc in code and generate the Java Docs in Gradle build
- Add OpenAPI definitions and create API interactive dashboard with help 
- Include Postman file for API testing 
 

