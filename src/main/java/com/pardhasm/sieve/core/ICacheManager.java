package com.pardhasm.sieve.core;

import com.pardhasm.sieve.core.model.ApiDefinition;
import com.pardhasm.sieve.core.model.EndPointDefinition;

import java.util.regex.Pattern;

public interface ICacheManager {
    void put(Pattern key, ApiDefinition value);

    ApiDefinition get(String name);

    void put(String name, EndPointDefinition endPoint);

    EndPointDefinition getEndpoints(String name);
}
