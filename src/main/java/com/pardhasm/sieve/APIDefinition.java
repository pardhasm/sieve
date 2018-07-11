package com.pardhasm.sieve;

import io.undertow.server.handlers.proxy.LoadBalancingProxyClient;
import io.undertow.server.handlers.proxy.ProxyHandler;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.api.AuthorizationManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;


public class APIDefinition {

    private String name;
    private Pattern pattern;
    private Boolean whiteListEnabled;
    private AuthorizationManager authManager;
    private SessionManager sessionManager;
    private OAuthManager oAuthManager;
    private Boolean isRoundRobinEnabled;
    private List<Target> targets;
    private AtomicInteger next;
    private Health health;
    private Long globalRateLimit;
    private TimeUnit globalRateLimitUnit;
    private Long userRateLimit;
    private TimeUnit userRateLimitUnit;
    private LoadBalancingProxyClient loadBalancer;
    private ProxyHandler proxyHandler;

    private APIDefinition(Builder builder) {
        this.name = checkNotNull(builder.name);
        this.pattern = checkNotNull(builder.pattern);
//        this.whiteListEnabled = checkNotNull(builder.whiteListEnabled);
//        this.authManager = checkNotNull(builder.authManager);
//        this.sessionManager = checkNotNull(builder.sessionManager);
//        this.oAuthManager = checkNotNull(builder.oAuthManager);
//        this.isRoundRobinEnabled = checkNotNull(builder.isRoundRobinEnabled);
//        this.next = checkNotNull(builder.next);
//        this.health = checkNotNull(builder.health);
        this.targets = checkNotNull(builder.targets);
        this.globalRateLimit = checkNotNull(builder.globalRateLimit);
        this.globalRateLimitUnit = checkNotNull(builder.globalRateLimitUnit);
        this.userRateLimit = checkNotNull(builder.userRateLimit);
        this.userRateLimitUnit = checkNotNull(builder.userRateLimitUnit);
        this.loadBalancer = checkNotNull(builder.loadBalancer);
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

    public ArrayList<Target> getTargets() {
        return (ArrayList<Target>) targets;
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

    public LoadBalancingProxyClient getLoadBalancer() {
        return loadBalancer;
    }

    public enum HttpType {
        HTTP("http"), HTTPS("https");
        private String value;

        HttpType(String value) {
            this.value = value;
        }
    }


    public enum Health {
        ACTIVE, SLOW, DEAD
    }

    static class Target {
        HttpType httpType;
        String host;
        String port;

        public Target(String httpType, String host, String port) {
            this.httpType = HttpType.valueOf(httpType);
            this.host = host;
            this.port = port;
        }

        public Target(Map params) {
            this.httpType = HttpType.valueOf(String.valueOf(params.get("httpType")));
            this.host = String.valueOf(params.get("host"));
            this.port = String.valueOf(params.get("port"));
        }
    }

    public static class Builder {
        private String name;
        private Pattern pattern;
        private Boolean whiteListEnabled;
        private AuthorizationManager authManager;
        private SessionManager sessionManager;
        private OAuthManager oAuthManager;
        private Boolean isRoundRobinEnabled;
        private List<Target> targets;
        private AtomicInteger next;
        private Health health;
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

        public Builder whiteListEnabled(Boolean whiteListEnabled) {
            this.whiteListEnabled = whiteListEnabled;
            return this;
        }

        public Builder authManager(AuthorizationManager authManager) {
            this.authManager = authManager;
            return this;
        }

        public Builder sessionManager(SessionManager sessionManager) {
            this.sessionManager = sessionManager;
            return this;
        }

        public Builder oAuthManager(OAuthManager oAuthManager) {
            this.oAuthManager = oAuthManager;
            return this;
        }

        public Builder isRoundRobinEnabled(Boolean isRoundRobinEnabled) {
            this.isRoundRobinEnabled = isRoundRobinEnabled;
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

        public Builder targets(List<Target> targets) throws URISyntaxException {
            this.targets = targets;
            this.build = ProxyHandler.builder().setProxyClient(configureLoadBalancer(targets)).setMaxRequestTime(30000).build();
            return this;
        }

        public Builder next(AtomicInteger next) {
            this.next = next;
            return this;
        }

        public Builder health(Health health) {
            this.health = health;
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

        public Builder fromPrototype(APIDefinition prototype) {
            name = prototype.name;
            pattern = prototype.pattern;
            whiteListEnabled = prototype.whiteListEnabled;
            authManager = prototype.authManager;
            sessionManager = prototype.sessionManager;
            oAuthManager = prototype.oAuthManager;
            isRoundRobinEnabled = prototype.isRoundRobinEnabled;
            targets = prototype.targets;
            next = prototype.next;
            health = prototype.health;
            globalRateLimit = prototype.globalRateLimit;
            globalRateLimitUnit = prototype.globalRateLimitUnit;
            userRateLimit = prototype.userRateLimit;
            userRateLimitUnit = prototype.userRateLimitUnit;
            return this;
        }

        public APIDefinition build() {
            return new APIDefinition(this);
        }

        private LoadBalancingProxyClient configureLoadBalancer(List<Target> targets) throws URISyntaxException {
            loadBalancer = new LoadBalancingProxyClient();
            loadBalancer.setConnectionsPerThread(20 * targets.size());
            for (Target target : targets) {
                loadBalancer.addHost(
                        new URI(new StringBuilder()
                                .append(target.httpType)
                                .append(Constants.ADDRESS)
                                .append(target.host)
                                .append(Constants.COLON)
                                .append(target.port)
                                .toString()));
            }
            return loadBalancer;
        }
    }

    public ProxyHandler getProxyHandler() {
        return proxyHandler;
    }

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        } else {
            return reference;
        }
    }
}


