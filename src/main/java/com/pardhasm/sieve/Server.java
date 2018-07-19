package com.pardhasm.sieve;

import com.google.inject.Inject;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import javax.annotation.PostConstruct;

public class Server {

    @Inject
    IRouter router;


    @PostConstruct
    private void init() {

        Undertow undertow = Undertow.builder().addHttpListener(9090, "localhost")
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                        router.handle(exchange);
                    }
                }).build();
        undertow.start();
    }
}
