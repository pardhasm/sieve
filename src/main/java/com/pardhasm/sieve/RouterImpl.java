package com.pardhasm.sieve;

import com.google.inject.Inject;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class RouterImpl implements IRouter {

    @Inject
    ICacheManager cacheManager;

    @Override
    public void handle(HttpServerExchange exchange) throws Exception {
        APIDefinition definition = cacheManager.get(exchange.getRequestPath());
        if(definition != null){
            definition.proxyHandler().handleRequest(exchange);
        }else{
            exchange.setStatusCode(404).getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Request URL not found");
        }
    }
}