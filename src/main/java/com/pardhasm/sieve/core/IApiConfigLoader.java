package com.pardhasm.sieve.core;

import java.io.IOException;
import java.net.URISyntaxException;

public interface IApiConfigLoader {
    void loadConfig(String config) throws URISyntaxException, IOException;
}
