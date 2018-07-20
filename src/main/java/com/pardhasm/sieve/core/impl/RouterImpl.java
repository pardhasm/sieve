package com.pardhasm.sieve.core.impl;

import com.google.inject.Inject;
import com.pardhasm.sieve.core.ICacheManager;
import com.pardhasm.sieve.core.IRouter;
import com.pardhasm.sieve.core.model.ApiDefinition;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class RouterImpl implements IRouter {


    private ICacheManager cacheManager;

    @Inject
    public RouterImpl(ICacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void handle(HttpServerExchange exchange) throws Exception {
        ApiDefinition definition = cacheManager.get(exchange.getRequestPath());
        if(definition != null){
            definition.proxyHandler().handleRequest(exchange);
        }else{
            exchange.setStatusCode(404).getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Request URL not found");
        }
    }
}
