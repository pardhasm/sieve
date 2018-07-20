package com.pardhasm.sieve;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.pardhasm.sieve.core.Binder;
import com.pardhasm.sieve.core.Sieve;

import java.io.IOException;
import java.net.URISyntaxException;

public class Application {
    public static void main(String[] args) throws IOException, URISyntaxException {
        Injector injector = Guice.createInjector(new Binder());
        Sieve sieve = new Sieve(injector);
        sieve.start(args);
    }
}
