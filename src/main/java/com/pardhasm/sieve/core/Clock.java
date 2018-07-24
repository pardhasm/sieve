package com.pardhasm.sieve.core;

import java.util.concurrent.atomic.AtomicLong;

public interface Clock {

    Clock DEFAULT_CLOCK = System::currentTimeMillis;

    static Clock defaultClock() {
        return DEFAULT_CLOCK;
    }

    static Clock mock(AtomicLong currentTime) {
        return currentTime::get;
    }

    /**
     * Returns the current time in milliseconds.
     *
     * @return the difference, measured in milliseconds, between the current time and midnight, January 1, 1970 UTC.
     */
    long currentTimeMillis();

}
