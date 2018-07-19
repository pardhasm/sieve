package com.pardhasm.sieve;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class CacheManagerImpl implements ICacheManager {

    private final Cache<Pattern, APIDefinition> cache = Caffeine.newBuilder()
            .expireAfterAccess(1000, TimeUnit.DAYS)
            .maximumSize(10_000).build();

    private final Set<Pattern> patterns = new HashSet<>();


    @Override
    public void put(Pattern key, APIDefinition value) {
        patterns.add(key);
        cache.put(key,value);
    }

    @Override
    public APIDefinition get(Pattern key){
        return cache.getIfPresent(key);
    }

    @Override
    public APIDefinition get(String path){
        for(Pattern pattern : patterns){
            if(pattern.matcher(path).matches()){
                return get(pattern);
            }
        }
        return null;
    }
}