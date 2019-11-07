# m.core demo application

Three services (Group, Workspace, Environment) that can be deployed together or independently.

Each service uses its own data store with data dependencies across the services coordinated through REST API calls.

## Build

`../gradlew build`

## Deploy

### As a single service

```
$ java -jar build/libs/demo-all.jar
47 [main] INFO ServiceManager - Starting services with in memory data store ...
60 [main] INFO ServiceManager - Starting Workspace Service server on 0.0.0.0:4567 ...
688 [main] WARN ServiceManager - No service url configured for Group Service, so starting a group service on the same server as Workspace Service ...
688 [main] WARN ServiceManager - No service url configured for Environment Service, so starting an Environment Service on the same server as Workspace Service ... …
700 [Thread-3] INFO log - Logging initialized @822ms to org.eclipse.jetty.util.log.Slf4jLog
746 [Thread-3] INFO EmbeddedJettyServer - == Spark has ignited ...
746 [Thread-3] INFO EmbeddedJettyServer - >> Listening on 0.0.0.0:4567 …
```

### As separate services

Start Group Directory Service

```
$ java -Dservice.group.port=4565 -jar build/libs/demo-all.jar
48 [main] INFO ServiceManager - Starting services with in memory data store ...
60 [main] INFO ServiceManager - Starting Group Service server on 0.0.0.0:4565 ...
178 [Thread-0] INFO log - Logging initialized @325ms to org.eclipse.jetty.util.log.Slf4jLog
232 [Thread-0] INFO EmbeddedJettyServer - == Spark has ignited ...
232 [Thread-0] INFO EmbeddedJettyServer - >> Listening on 0.0.0.0:4565
```

Start Environment Service

```
$ java -Dservice.environment.port=4566 -jar build/libs/demo-all.jar
48 [main] INFO ServiceManager - Starting services with in memory data store ...
61 [main] INFO ServiceManager - Starting Environment Service server on 0.0.0.0:4566 ...
179 [Thread-0] INFO log - Logging initialized @300ms to org.eclipse.jetty.util.log.Slf4jLog
232 [Thread-0] INFO EmbeddedJettyServer - == Spark has ignited ...
232 [Thread-0] INFO EmbeddedJettyServer - >> Listening on 0.0.0.0:4566
```

Start Workspace Service

```
$ java -Dservice.group.url=http://127.0.0.1:4565 -Dservice.environment.url=http://127.0.0.1:4566 -jar build/libs/demo-all.jar
46 [main] INFO ServiceManager - Starting services with in memory data store ...
58 [main] INFO ServiceManager - Starting Workspace Service server on 0.0.0.0:4567 ...
682 [main] INFO ServiceManager - Using external Group Service at http://127.0.0.1:4565
683 [main] INFO ServiceManager - Using external Environment Service at http://127.0.0.1:4566
697 [Thread-3] INFO log - Logging initialized @817ms to org.eclipse.jetty.util.log.Slf4jLog
743 [Thread-3] INFO EmbeddedJettyServer - == Spark has ignited ...
743 [Thread-3] INFO EmbeddedJettyServer - >> Listening on 0.0.0.0:4567
```

## Quick Test

Following curl examples uses three different services running independently

```
$ java -Dservice.group.port=4565 -jar build/libs/demo-all.jar

$ java -Dservice.environment.port=4566 -jar build/libs/demo-all.jar

$ java -Dservice.group.url=http://127.0.0.1:4565 -Dservice.environment.url=http://127.0.0.1:4566 -jar build/libs/demo-all.jar
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

 Use generic Component API to list all root level nodes like Workspaces

```
$ curl -H  "Accept: application/json" -X GET http://127.0.0.1:4567/node

{"workspaces":["ca6a182a-80bd-40d4-85e3-878610596fe2"]}
```

 For each root level Workspace we can list the children nodes by type

```
$ curl -H  "Accept: application/json" -X GET http://127.0.0.1:4567/node/ca6a182a-80bd-40d4-85e3878610596fe2

{"environments":["306f3f85-2e20-466f-8b55-9dbe7506e50t1"]}
```

 A node like Environment do not support children

```
$ curl -H  "Accept: application/json" -X GET http://127.0.0.1:4567/node/306f3f85-2e20-466f-8b559dbe7506e501

{"error":"Invalid Component"}

