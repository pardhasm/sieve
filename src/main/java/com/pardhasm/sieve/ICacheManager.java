package com.pardhasm.sieve;

import java.util.regex.Pattern;

public interface ICacheManager {
    void put(Pattern key, ApiDefinition value);

    ApiDefinition get(Pattern key);

    ApiDefinition get(String path);
}
