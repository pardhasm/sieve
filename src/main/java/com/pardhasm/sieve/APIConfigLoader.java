package com.pardhasm.sieve;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public enum APIConfigLoader {
    INSTANCE;


    public void init() throws IOException, URISyntaxException {
        loadConfig();
    }

    public void loadConfig() throws IOException, URISyntaxException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL resource = classLoader.getResource(Constants.CONFIG_FOLDER);
        if (resource != null) {
            File directory = new File(resource.getFile());
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    String content = new String(Files.readAllBytes(file.toPath()));
                    Any config = JsonIterator.deserialize(content);
                    for(Any conf : config.get("configs").asList()){
                        APIDefinition apiDefinition = parseConfig(conf);
                        CacheManager.INSTANCE.put(apiDefinition.getPattern(), apiDefinition);
                    }
                }
            }
        }
    }

    private APIDefinition parseConfig(Any config) throws URISyntaxException {
        return new APIDefinition.Builder()
                .name(config.get("name").toString())
                .pattern(config.get("pattern").toString())
                .whiteListEnabled(config.get("whiteListEnabled").toBoolean())
                .isRoundRobinEnabled(config.get("roundRobinEnabled").toBoolean())
                .globalRateLimit(config.get("globalRateLimit").toLong())
                .globalRateLimitUnit(TimeUnit.valueOf(config.get("globalRateLimitUnit").toString()))
                .userRateLimit(config.get("userRateLimit").toLong())
                .userRateLimitUnit(TimeUnit.valueOf(config.get("userRateLimitUnit").toString()))
                .targets(parseTargets(config)).build();
    }

    private List<APIDefinition.Target> parseTargets(Any config) {
        ArrayList<APIDefinition.Target> list = new ArrayList<>();
        for (Any p : config.get("targets").asList()) {
            list.add(new APIDefinition.Target(p.asMap()));
        }
        return list;
    }


}
