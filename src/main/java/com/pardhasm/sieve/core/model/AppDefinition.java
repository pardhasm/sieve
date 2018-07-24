package com.pardhasm.sieve.core.model;

import com.google.common.base.Preconditions;

import java.util.List;

public class AppDefinition {

    private String name;
    private List<ApiDefinition> apiList;
    private List<EndPointDefinition> endpoints;

    private AppDefinition(Builder builder) {
        this.name = Preconditions.checkNotNull(builder.name);
        this.apiList = Preconditions.checkNotNull(builder.apiList);
        this.endpoints = Preconditions.checkNotNull(builder.endpoints);
    }

    public String getName() {
        return name;
    }

    public List<ApiDefinition> getApiList() {
        return apiList;
    }

    public List<EndPointDefinition> getEndpoints() {
        return endpoints;
    }


    public static class Builder {
        private String name;
        private List<ApiDefinition> apiList;
        private List<EndPointDefinition> endpoints;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder apiList(List<ApiDefinition> apiList) {
            this.apiList = apiList;
            return this;
        }

        public Builder endpoints(List<EndPointDefinition> endpoints) {
            this.endpoints = endpoints;
            return this;
        }

        public Builder fromPrototype(AppDefinition prototype) {
            name = prototype.name;
            apiList = prototype.apiList;
            endpoints = prototype.endpoints;
            return this;
        }

        public AppDefinition build() {
            return new AppDefinition(this);
        }
    }
}
