package com.pardhasm.sieve;

import com.google.inject.AbstractModule;

public final class Binder extends AbstractModule {
    @Override
    protected void configure() {
        bind(ICacheManager.class).to(CacheManagerImpl.class);
        bind(IRouter.class).to(RouterImpl.class);
    }
}
