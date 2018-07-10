package com.pardhasm.sieve;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public enum Router {
    instance;

    public void handle(HttpServerExchange exchange) throws Exception {
        APIDefinition definition = CacheManager.instance.get(exchange.getRequestPath());
        if(definition != null){
            definition.proxyHandler().handleRequest(exchange);
        }else{
            exchange.setStatusCode(404);
            exchange.getResponseHeaders().put(
                    Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Request URL not found");
        }
    }
}
