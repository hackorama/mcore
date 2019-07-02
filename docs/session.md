# Session

## Spark (HttpSession  Wrapper)

- https://static.javadoc.io/com.sparkjava/spark-core/2.6.0/spark/Session.html
- https://tomcat.apache.org/tomcat-5.5-doc/servletapi/javax/servlet/http/HttpSession.html

## Vertx

- https://vertx.io/docs/apidocs/io/vertx/ext/web/Session.html

context -> sessionHandler  Using session cookies

## Spring (WebFlux)

- https://www.baeldung.com/spring-session-reactive
- https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/server/WebSession.html

| Spark                      | Vertx            | Spring             |
| -------------------------- | ---------------- | ------------------ |
| id                         | id               | getId              |
| attribute(name)            | get(name)        | getAttribute(name) |
| invalidate                 | destroy          | invalidate         |
| lastAccessTime             | lastAccessed     | getLastAccessTime  |
| maxInavativeInterval       | timeout          | getMaxIdleTime     |
| attribute(name, value)     | put(name, value) |                    |
| removeAttribute(name)      | delete(name)     |                    |
| creationTime               |                  | getCreationTime    |
| maxInactiveInterval(time)  |                  | setMaxIdleTime     |
|                            | data             | getAttributes      |
|                            | regenerateId     | changeSessionid    |
|                            | isDestroyed      | isExpired          |
| isnew                      |                  |                    |
|                            | isRegenerated    |                    |
|                            | isEmpty          |                    |
|                            | oldId            |                    |
|                            | setAccessed      |                    |
|                            | value            |                    |
|                            |                  | isStarted          |
|                            |                  | start              |
|                            |                  | save               |


