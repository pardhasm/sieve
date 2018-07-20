package com.pardhasm.sieve.core;

import com.google.inject.AbstractModule;
import com.pardhasm.sieve.core.impl.CacheManagerImpl;
import com.pardhasm.sieve.core.impl.RouterImpl;

public class Binder extends AbstractModule {
    @Override
    protected void configure() {
        bind(ICacheManager.class).to(CacheManagerImpl.class);
        bind(IRouter.class).to(RouterImpl.class);
    }
}
