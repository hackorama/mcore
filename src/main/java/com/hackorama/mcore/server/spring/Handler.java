package com.hackorama.mcore.server.spring;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class Handler {

    public Mono<ServerResponse> all(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).body(
                BodyInserters.fromObject(request.methodName() + " " + request.path() + " " + request.pathVariables()));
    }

}
