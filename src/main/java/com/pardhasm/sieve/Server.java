package com.pardhasm.sieve;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public enum Server {
    instance;

    private Undertow server = null;


    private void init() {
        if (server == null) {
            server = Undertow.builder().addHttpListener(9090, "localhost")
                    .setHandler(new HttpHandler() {
                        @Override
                        public void handleRequest(HttpServerExchange exchange) throws Exception {
                            Router.instance.handle(exchange);
                        }
                    }).build();

        }
    }

    public synchronized void start() {
        if (server == null) {
            init();
        }
        server.start();
    }
}
