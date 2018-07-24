package com.pardhasm.sieve.core;

public interface WindowCounter {

    /**
     * Increment the counter by {@code delta}.
     * If You want to decrement instead of increment then use negative {@code delta}.
     *
     * @param delta the amount by which the counter will be increased
     */
    void add(long delta);

    /**
     * Returns the counter's current value.
     *
     * @return the counter's current value
     */
    long getSum();

}
