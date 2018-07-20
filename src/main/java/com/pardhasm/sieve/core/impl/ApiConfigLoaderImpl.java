package com.pardhasm.sieve.core.impl;

import com.google.inject.Inject;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.pardhasm.sieve.core.IApiConfigLoader;
import com.pardhasm.sieve.core.ICacheManager;
import com.pardhasm.sieve.core.model.ApiDefinition;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ApiConfigLoaderImpl implements IApiConfigLoader {

    private ICacheManager cacheManager;

    @Inject
    public ApiConfigLoaderImpl(ICacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void loadConfig(String config) throws URISyntaxException {
        parseAllConfig(JsonIterator.deserialize(config));
    }

    private void parseAllConfig(Any config) throws URISyntaxException {
        for (Any conf : config.get("configs").asList()) {
            ApiDefinition apiDefinition = parseConfig(conf);
            cacheManager.put(apiDefinition.getPattern(), apiDefinition);
        }
    }

    private ApiDefinition parseConfig(Any config) throws URISyntaxException {
        return new ApiDefinition.Builder()
                .name(config.get("name").toString())
                .pattern(config.get("pattern").toString())
                .globalRateLimit(config.get("globalRateLimit").toLong())
                .globalRateLimitUnit(TimeUnit.valueOf(config.get("globalRateLimitUnit").toString()))
                .userRateLimit(config.get("userRateLimit").toLong())
                .userRateLimitUnit(TimeUnit.valueOf(config.get("userRateLimitUnit").toString()))
                .targets(parseTargets(config)).build();
    }

    private List<ApiDefinition.Target> parseTargets(Any config) {
        ArrayList<ApiDefinition.Target> list = new ArrayList<>();
        List<Any> asList = config.get("targets").asList();
        for (Any p : asList) {
            list.add(new ApiDefinition.Target(p.asMap()));
        }
        return list;
    }


}