package com.pardhasm.sieve.core.model;

import com.google.common.base.Preconditions;

public class EndPointDefinition {

    private String name;
    private String host;
    private int port;
    private HttpType httpType;
    private long maxThroughput = -1;
    private String health;

    private EndPointDefinition(Builder builder) {
        this.name = Preconditions.checkNotNull(builder.name);
        this.host = Preconditions.checkNotNull(builder.host);
        this.port = builder.port;
        this.httpType = Preconditions.checkNotNull(builder.httpType);
        this.maxThroughput = builder.maxThroughput;
        this.health = builder.health;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public HttpType getHttpType() {
        return httpType;
    }

    public long getMaxThroughput() {
        return maxThroughput;
    }

    public String getHealth() {
        return health;
    }


    public static class Builder {
        private String name;
        private String host;
        private int port;
        private HttpType httpType;
        private long maxThroughput;
        private String health;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder httpType(HttpType httpType) {
            this.httpType = httpType;
            return this;
        }

        public Builder maxThroughput(long maxThroughput) {
            this.maxThroughput = maxThroughput;
            return this;
        }

        public Builder health(String health) {
            this.health = health;
            return this;
        }

        public Builder fromPrototype(EndPointDefinition prototype) {
            name = prototype.name;
            host = prototype.host;
            port = prototype.port;
            httpType = prototype.httpType;
            maxThroughput = prototype.maxThroughput;
            health = prototype.health;
            return this;
        }

        public EndPointDefinition build() {
            return new EndPointDefinition(this);
        }
    }
}
