package com.pardhasm.sieve.core.model;

import com.google.common.base.Preconditions;

import java.util.List;

public class AppDefinition {

    private String name;
    private List<ApiDefinition> apiList;

    private AppDefinition(Builder builder) {
        this.name = Preconditions.checkNotNull(builder.name);
        this.apiList = Preconditions.checkNotNull(builder.apis);
    }

    public String getName() {
        return name;
    }

    public List<ApiDefinition> getApiList() {
        return apiList;
    }


    public static class Builder {
        private String name;
        private List<ApiDefinition> apis;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder apis(List<ApiDefinition> apis) {
            this.apis = apis;
            return this;
        }

        public Builder fromPrototype(AppDefinition prototype) {
            name = prototype.name;
            apis = prototype.apiList;
            return this;
        }

        public AppDefinition build() {
            return new AppDefinition(this);
        }
    }
}
