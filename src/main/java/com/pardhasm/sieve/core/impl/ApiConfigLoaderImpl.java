package com.pardhasm.sieve.core.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.pardhasm.sieve.core.IApiConfigLoader;
import com.pardhasm.sieve.core.ICacheManager;
import com.pardhasm.sieve.core.model.ApiDefinition;
import com.pardhasm.sieve.core.model.AppDefinition;
import com.pardhasm.sieve.core.model.EndPointDefinition;
import com.pardhasm.sieve.core.model.HttpType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ApiConfigLoaderImpl implements IApiConfigLoader {


    private ObjectMapper objectMapper = new ObjectMapper();
    private ICacheManager cacheManager;

    @Inject
    public ApiConfigLoaderImpl(ICacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void loadConfig(String config) throws URISyntaxException, IOException {
        HashMap configMap = objectMapper.readValue(config, HashMap.class);
        parseAllConfig(configMap);
    }

    private void parseAllConfig(HashMap config) throws URISyntaxException {
        for (HashMap conf : (ArrayList<HashMap>) config.get("configs")) {
            AppDefinition appDefinition = parseConfig(conf);
            for (ApiDefinition apiDefinition : appDefinition.getApiList()) {
                cacheManager.put(apiDefinition.getPattern(), apiDefinition);
            }
        }
    }

    private AppDefinition parseConfig(HashMap config) throws URISyntaxException {
        List<EndPointDefinition> result = extractEndPointDefinitions(config);
        List<ApiDefinition> list = extractApiDefinitions(config);
        return new AppDefinition.Builder()
                .name(config.get("name").toString())
                .endpoints(result)
                .apiList(list)
                .build();
    }

    private List<ApiDefinition> extractApiDefinitions(HashMap config) throws URISyntaxException {
        List<ApiDefinition> list = new ArrayList<>();
        List<HashMap> apiList = (ArrayList<HashMap>) config.get("apiList");
        for (HashMap api : apiList) {
            ApiDefinition apiDefinition = parseAPIConfig(api);
            list.add(apiDefinition);
        }
        return list;
    }

    private List<EndPointDefinition> extractEndPointDefinitions(HashMap config) {
        List<EndPointDefinition> result = new ArrayList<>();
        List<HashMap> endpoints = (ArrayList<HashMap>) config.get("endpoints");
        for (HashMap conf : endpoints) {
            EndPointDefinition endPointDefinition = parseEndPointConfig(conf);
            cacheManager.put(endPointDefinition.getName(), endPointDefinition);
            result.add(endPointDefinition);
        }
        return result;
    }

    private ApiDefinition parseAPIConfig(HashMap config) throws URISyntaxException {
        return new ApiDefinition.Builder()
                .name(config.get("name").toString())
                .pattern(config.get("pattern").toString())
                .globalRateLimit((Integer) config.get("globalRateLimit"))
                .globalRateLimitUnit(TimeUnit.valueOf(config.get("globalRateLimitUnit").toString()))
                .userRateLimit((Integer) config.get("userRateLimit"))
                .userRateLimitUnit(TimeUnit.valueOf(config.get("userRateLimitUnit").toString()))
                .loadBalancer(((ArrayList<String>) config.get("targets"))
                        .stream().map(p -> cacheManager.getEndpoints(p)).collect(Collectors.toList()))
                .build();
    }

    private EndPointDefinition parseEndPointConfig(HashMap config) {
        return new EndPointDefinition.Builder()
                .name(config.get("name").toString())
                .maxThroughput((Integer) config.get("maxThroughput"))
                .host(config.get("host").toString())
                .port((Integer) config.get("port"))
                .httpType(HttpType.valueOf(config.get("httpType").toString().toUpperCase()))
                .build();
    }
}
