package com.pardhasm.sieve;

import java.util.regex.Pattern;

public interface ICacheManager {
    void put(Pattern key, APIDefinition value);

    APIDefinition get(Pattern key);

    APIDefinition get(String path);
}
