# Request

| Attributes | MCore | [Spark](https://static.javadoc.io/com.sparkjava/spark-core/2.6.0/spark/Request.html) | [Vertx](https://vertx.io/docs/apidocs/io/vertx/core/http/HttpServerRequest.html) | [Spring](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/reactive/function/server/ServerRequest.html) |
| -------------- | ----- | ----- | ------ | ----- |
| [HttpServletRequest](https://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html) | [Mcore](https://github.com/hackorama/mcore/blob/master/src/main/java/com/hackorama/mcore/common/Request.java) | [Spark](https://github.com/perwendel/spark/blob/master/src/main/java/spark/Request.java) | [Vertx](https://github.com/eclipse-vertx/vert.x/blob/master/src/main/java/io/vertx/core/http/HttpServerRequest.java) | [Spring](https://github.com/spring-projects/spring-framework/blob/master/spring-webflux/src/main/java/org/springframework/web/reactive/function/server/ServerRequest.java) |
| body           |x|x|x|x|
| path_variables |x|x|x|x|
| query_params   |x|x|x|x|
| headers        |x|x|x|x|
| request_method | |x|x|x|
| url            | |x|x|x|
| path_info      | |x|x|x|
| is_secure      | |x|x|x|
| cookies        | |x| |x|
| port           | |x|x| |
| query_string   | |x|x| |
| remote_host    | |x| |x|
| remote_address | |x|x| |
| multi_part     | | |x|x|
| session        | |x| |x|
| is_forwarded   | |x|x| |
| user_principal | | | |x|
| auth_type      | | |x| |
| scheme         | |x|x| |
| is_xhr         | |x| | |
| is_form_data   | |x| | |
| referrer       | |x| | |
| content_length | |x| | |
| media_type     | |x| | |
| user_agent     | |x| | |
| remote_user    | | | | |
