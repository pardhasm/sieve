package com.pardhasm.sieve;

import io.undertow.server.HttpServerExchange;

public interface IRouter {
    void handle(HttpServerExchange exchange) throws Exception;
}
