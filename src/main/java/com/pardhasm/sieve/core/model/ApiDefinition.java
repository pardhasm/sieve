package com.pardhasm.sieve.core.model;

import com.pardhasm.sieve.core.Constants;
import io.undertow.server.handlers.proxy.LoadBalancingProxyClient;
import io.undertow.server.handlers.proxy.ProxyHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


public class ApiDefinition {

    private String name;
    private Pattern pattern;
    private Long globalRateLimit;
    private TimeUnit globalRateLimitUnit;
    private Long userRateLimit;
    private TimeUnit userRateLimitUnit;
    private ProxyHandler proxyHandler;

    private ApiDefinition(Builder builder) {
        this.name = checkNotNull(builder.name);
        this.pattern = checkNotNull(builder.pattern);
        this.globalRateLimit = checkNotNull(builder.globalRateLimit);
        this.globalRateLimitUnit = checkNotNull(builder.globalRateLimitUnit);
        this.userRateLimit = checkNotNull(builder.userRateLimit);
        this.userRateLimitUnit = checkNotNull(builder.userRateLimitUnit);
        this.proxyHandler = checkNotNull(builder.build);
    }



    public Pattern getPattern() {
        return pattern;
    }

    public ProxyHandler proxyHandler() {
        return proxyHandler;
    }

    public static String removeSlashesAtBothEnds(String path) {
        checkNotNull(path);

        if (path.isEmpty()) {
            return path;
        }

        int beginIndex = 0;
        while (beginIndex < path.length() && path.charAt(beginIndex) == '/') {
            beginIndex++;
        }
        if (beginIndex == path.length()) {
            return "";
        }

        int endIndex = path.length() - 1;
        while (endIndex > beginIndex && path.charAt(endIndex) == '/') {
            endIndex--;
        }
        return path.substring(beginIndex, endIndex + 1);
    }

    public String getName() {
        return name;
    }

    public Long getGlobalRateLimit() {
        return globalRateLimit;
    }

    public TimeUnit getGlobalRateLimitUnit() {
        return globalRateLimitUnit;
    }

    public Long getUserRateLimit() {
        return userRateLimit;
    }

    public TimeUnit getUserRateLimitUnit() {
        return userRateLimitUnit;
    }


    public static class Target {
        HttpType httpType;
        String host;
        String port;


        public Target(Map params) {
            this.httpType = HttpType.valueOf(String.valueOf(params.get("httpType")).toUpperCase());
            this.host = String.valueOf(params.get("host")).trim();
            this.port = String.valueOf(params.get("port")).trim();
        }
    }

    public static class Builder {
        private String name;
        private Pattern pattern;
        private Long globalRateLimit;
        private TimeUnit globalRateLimitUnit;
        private Long userRateLimit;
        private TimeUnit userRateLimitUnit;
        private LoadBalancingProxyClient loadBalancer;
        private ProxyHandler build;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder pattern(String path) {
            this.pattern = buildRegex(path);
            return this;
        }


        private static Pattern buildRegex(String path) {
            StringBuilder sb = new StringBuilder();
            for (String token : removeSlashesAtBothEnds(path).split("/")) {
                sb.append("/");
                if (token.charAt(0) == ':') {
                    sb.append("([^/]+)");
                } else {
                    sb.append(token);
                }
            }
            return java.util.regex.Pattern.compile(sb.toString());
        }

        public Builder loadBalancer(List<EndPointDefinition> endPointDefinitions) throws URISyntaxException {
            this.build = ProxyHandler.builder().setProxyClient(configureLoadBalancer(endPointDefinitions)).setMaxRequestTime(30000).build();
            return this;
        }

        public Builder globalRateLimit(long globalRateLimit) {
            this.globalRateLimit = globalRateLimit;
            return this;
        }

        public Builder globalRateLimitUnit(TimeUnit globalRateLimitUnit) {
            this.globalRateLimitUnit = globalRateLimitUnit;
            return this;
        }

        public Builder userRateLimit(long userRateLimit) {
            this.userRateLimit = userRateLimit;
            return this;
        }

        public Builder userRateLimitUnit(TimeUnit userRateLimitUnit) {
            this.userRateLimitUnit = userRateLimitUnit;
            return this;
        }

        public Builder fromPrototype(ApiDefinition prototype) {
            name = prototype.name;
            pattern = prototype.pattern;
            globalRateLimit = prototype.globalRateLimit;
            globalRateLimitUnit = prototype.globalRateLimitUnit;
            userRateLimit = prototype.userRateLimit;
            userRateLimitUnit = prototype.userRateLimitUnit;
            build = prototype.proxyHandler;
            return this;
        }

        public ApiDefinition build() {
            return new ApiDefinition(this);
        }

        private LoadBalancingProxyClient configureLoadBalancer(List<EndPointDefinition> endPointDefinitions) throws URISyntaxException {
            loadBalancer = new LoadBalancingProxyClient();
            for (EndPointDefinition endPointDefinition : endPointDefinitions) {
                loadBalancer.addHost(
                        new URI(String.valueOf(endPointDefinition.getHttpType()).toLowerCase() +
                                Constants.ADDRESS +
                                endPointDefinition.getHost() +
                                Constants.COLON +
                                endPointDefinition.getPort()));
            }
            return loadBalancer;
        }
    }


    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        } else {
            return reference;
        }
    }
}


