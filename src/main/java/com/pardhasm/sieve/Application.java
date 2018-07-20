package com.pardhasm.sieve;

import com.pardhasm.sieve.core.Sieve;

import java.io.IOException;
import java.net.URISyntaxException;

public class Application {
    public static void main(String[] args) throws IOException, URISyntaxException {
        Sieve sieve = new Sieve();
        sieve.start(args);
    }
}
