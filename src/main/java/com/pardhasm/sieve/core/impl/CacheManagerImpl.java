package com.pardhasm.sieve.core.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.pardhasm.sieve.core.ICacheManager;
import com.pardhasm.sieve.core.model.ApiDefinition;
import com.pardhasm.sieve.core.model.EndPointDefinition;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class CacheManagerImpl implements ICacheManager {

    private final Cache<Pattern, ApiDefinition> apiDefinitionCache = Caffeine.newBuilder()
            .expireAfterAccess(1000, TimeUnit.DAYS)
            .maximumSize(10_000).build();

    private final Cache<String, EndPointDefinition> endPointDefinitionCache = Caffeine.newBuilder()
            .expireAfterAccess(1000, TimeUnit.DAYS)
            .maximumSize(10_000).build();

    private final Set<Pattern> patterns = new HashSet<>();


    @Override
    public void put(Pattern key, ApiDefinition value) {
        patterns.add(key);
        apiDefinitionCache.put(key, value);
    }

    public ApiDefinition get(Pattern key) {
        return apiDefinitionCache.getIfPresent(key);
    }

    @Override
    public ApiDefinition get(String path) {
        for(Pattern pattern : patterns){
            if(pattern.matcher(path).matches()){
                return get(pattern);
            }
        }
        return null;
    }

    @Override
    public void put(String name, EndPointDefinition endPoint) {
        endPointDefinitionCache.put(name, endPoint);
    }

    @Override
    public EndPointDefinition getEndpoints(String name) {
        return endPointDefinitionCache.getIfPresent(name);
    }
}
