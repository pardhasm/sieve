package com.pardhasm.sieve.core;

import io.undertow.server.HttpServerExchange;

public interface IRouter {
    void handle(HttpServerExchange exchange) throws Exception;
}
