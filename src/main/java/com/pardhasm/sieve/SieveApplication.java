package com.pardhasm.sieve;


import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.io.IOException;
import java.net.URISyntaxException;

public class SieveApplication {

    public static void main(String[] args) throws IOException, URISyntaxException {
        APIConfigLoader.instance.init();
        Server.instance.start();
        test();
    }

    private static void test() {
        final Undertow server1 = Undertow.builder()
                .addHttpListener(9081, "localhost")
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        exchange.getResponseSender().send("Server1");
                    }
                })
                .build();

        server1.start();
        final Undertow server2 = Undertow.builder()
                .addHttpListener(9082, "localhost")
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        exchange.getResponseSender().send("Server2");
                    }
                })
                .build();

        server2.start();
        final Undertow server3 = Undertow.builder()
                .addHttpListener(9083, "localhost")
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        exchange.getResponseSender().send("Server3");
                    }
                })
                .build();

        server3.start();
    }


}
