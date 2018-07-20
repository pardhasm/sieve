package com.pardhasm.sieve.core.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.pardhasm.sieve.core.ICacheManager;
import com.pardhasm.sieve.core.model.ApiDefinition;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class CacheManagerImpl implements ICacheManager {

    private final Cache<Pattern, ApiDefinition> cache = Caffeine.newBuilder()
            .expireAfterAccess(1000, TimeUnit.DAYS)
            .maximumSize(10_000).build();

    private final Set<Pattern> patterns = new HashSet<>();


    @Override
    public void put(Pattern key, ApiDefinition value) {
        patterns.add(key);
        cache.put(key,value);
    }

    @Override
    public ApiDefinition get(Pattern key) {
        return cache.getIfPresent(key);
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
}
