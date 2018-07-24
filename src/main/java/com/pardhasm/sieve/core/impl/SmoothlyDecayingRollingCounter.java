package com.pardhasm.sieve.core.impl;

import com.pardhasm.sieve.core.Clock;
import com.pardhasm.sieve.core.WindowCounter;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

public class SmoothlyDecayingRollingCounter implements WindowCounter {
    // meaningful limits to disallow user to kill performance(or memory footprint) by mistake
    static final int MAX_CHUNKS = 1000;
    static final long MIN_CHUNK_RESETTING_INTERVAL_MILLIS = 100;

    private final long intervalBetweenResettingMillis;
    private final Clock clock;
    private final long creationTimestamp;

    private final Chunk[] chunks;

    /**
     * Constructs the chunked counter divided by {@code numberChunks}.
     * The counter will invalidate one chunk each time when {@code rollingWindow/numberChunks} millis has elapsed,
     * except oldest chunk which invalidated continuously.
     * The memory consumed by counter and latency of sum calculation depend directly from {@code numberChunks}
     *
     * <p> Example of usage:
     * <pre><code>
     *         // constructs the counter which divided by 10 chunks with 60 seconds time window.
     *         // one chunk will be reset to zero after each 6 second,
     *         WindowCounter counter = new SmoothlyDecayingRollingCounter(Duration.ofSeconds(60), 10);
     *         counter.add(42);
     *     </code>
     * </pre>
     *
     * @param rollingWindow the rolling time window duration
     * @param numberChunks  The count of chunk to split counter
     */
    public SmoothlyDecayingRollingCounter(Duration rollingWindow, int numberChunks) {
        this(rollingWindow, numberChunks, Clock.defaultClock());
    }

    public SmoothlyDecayingRollingCounter(Duration rollingWindow, int numberChunks, Clock clock) {
        if (numberChunks < 2) {
            throw new IllegalArgumentException("numberChunks should be >= 2");
        }

        if (numberChunks > MAX_CHUNKS) {
            throw new IllegalArgumentException("number of chunks should be <=" + MAX_CHUNKS);
        }

        long rollingWindowMillis = rollingWindow.toMillis();
        this.intervalBetweenResettingMillis = rollingWindowMillis / numberChunks;
        if (intervalBetweenResettingMillis < MIN_CHUNK_RESETTING_INTERVAL_MILLIS) {
            throw new IllegalArgumentException("intervalBetweenResettingMillis should be >=" + MIN_CHUNK_RESETTING_INTERVAL_MILLIS);
        }

        this.clock = clock;
        this.creationTimestamp = clock.currentTimeMillis();

        this.chunks = new Chunk[numberChunks + 1];
        for (int i = 0; i < chunks.length; i++) {
            this.chunks[i] = new Chunk(i);
        }
    }

    public static String printArray(Object[] array, String elementName) {
        String msg = "{";
        for (int i = 0; i < array.length; i++) {
            Object element = array[i];
            msg += "\n" + elementName + "[" + i + "]=" + element;
        }
        msg += "\n}";
        return msg;
    }

    /**
     * @return the rolling window duration for this counter
     */
    public Duration getRollingWindow() {
        return Duration.ofMillis((chunks.length - 1) * intervalBetweenResettingMillis);
    }

    /**
     * @return the number of chunks
     */
    public int getChunkCount() {
        return chunks.length - 1;
    }

    @Override
    public void add(long delta) {
        long nowMillis = clock.currentTimeMillis();
        long millisSinceCreation = nowMillis - creationTimestamp;
        long intervalsSinceCreation = millisSinceCreation / intervalBetweenResettingMillis;
        int chunkIndex = (int) intervalsSinceCreation % chunks.length;
        chunks[chunkIndex].add(delta, nowMillis);
    }

    @Override
    public long getSum() {
        long currentTimeMillis = clock.currentTimeMillis();

        // To get as fresh value as possible we need to calculate sum in order from oldest to newest
        long millisSinceCreation = currentTimeMillis - creationTimestamp;
        long intervalsSinceCreation = millisSinceCreation / intervalBetweenResettingMillis;
        int newestChunkIndex = (int) intervalsSinceCreation % chunks.length;

        long sum = 0;
        for (int i = newestChunkIndex + 1, iteration = 0; iteration < chunks.length; i++, iteration++) {
            if (i == chunks.length) {
                i = 0;
            }
            Chunk chunk = chunks[i];
            sum += chunk.getSum(currentTimeMillis);
        }
        return sum;
    }

    @Override
    public String toString() {
        return "SmoothlyDecayingRollingCounter{" +
                ", intervalBetweenResettingMillis=" + intervalBetweenResettingMillis +
                ", clock=" + clock +
                ", creationTimestamp=" + creationTimestamp +
                ", chunks=" + printArray(chunks, "chunk") +
                '}';
    }

    private final class Chunk {

        final Phase left;
        final Phase right;

        final AtomicReference<Phase> currentPhaseRef;

        Chunk(int chunkIndex) {
            long invalidationTimestamp = creationTimestamp + (chunks.length + chunkIndex) * intervalBetweenResettingMillis;
            this.left = new Phase(invalidationTimestamp);
            this.right = new Phase(Long.MAX_VALUE);

            this.currentPhaseRef = new AtomicReference<>(left);
        }

        long getSum(long currentTimeMillis) {
            return currentPhaseRef.get().getSum(currentTimeMillis);
        }

        void add(long delta, long currentTimeMillis) {
            Phase currentPhase = currentPhaseRef.get();
            long currentPhaseProposedInvalidationTimestamp = currentPhase.proposedInvalidationTimestamp;

            if (currentTimeMillis < currentPhaseProposedInvalidationTimestamp) {
                if (currentPhaseProposedInvalidationTimestamp != Long.MAX_VALUE) {
                    // this is main path - there are no rotation in the middle and we are writing to non-expired phase
                    currentPhase.adder.add(delta);
                } else {
                    // another thread is in the middle of phase rotation.
                    // We need to re-read current phase to be sure that we are not writing to inactive phase
                    currentPhaseRef.get().adder.add(delta);
                }
            } else {
                // it is need to flip the phases
                Phase expiredPhase = currentPhase;

                // write to next phase because current is expired
                Phase nextPhase = expiredPhase == left ? right : left;
                nextPhase.adder.add(delta);

                // try flip phase
                if (currentPhaseRef.compareAndSet(expiredPhase, nextPhase)) {
                    // Prepare expired phase to next iteration
                    expiredPhase.adder.reset();
                    expiredPhase.proposedInvalidationTimestamp = Long.MAX_VALUE;

                    // allow to next phase to be expired
                    long millisSinceCreation = currentTimeMillis - creationTimestamp;
                    long intervalsSinceCreation = millisSinceCreation / intervalBetweenResettingMillis;
                    nextPhase.proposedInvalidationTimestamp = creationTimestamp + (intervalsSinceCreation + chunks.length) * intervalBetweenResettingMillis;
                }
            }
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Chunk{");
            sb.append("currentPhaseRef=").append(currentPhaseRef);
            sb.append('}');
            return sb.toString();
        }
    }

    private final class Phase {

        final LongAdder adder;
        volatile long proposedInvalidationTimestamp;

        Phase(long proposedInvalidationTimestamp) {
            this.adder = new LongAdder();
            this.proposedInvalidationTimestamp = proposedInvalidationTimestamp;
        }

        long getSum(long currentTimeMillis) {
            long proposedInvalidationTimestamp = this.proposedInvalidationTimestamp;
            if (currentTimeMillis >= proposedInvalidationTimestamp) {
                // The chunk was unused by writers for a long time
                return 0;
            }

            long sum = this.adder.sum();

            // if this is oldest chunk then we need to reduce its weight
            long beforeInvalidateMillis = proposedInvalidationTimestamp - currentTimeMillis;
            if (beforeInvalidateMillis < intervalBetweenResettingMillis) {
                double decayingCoefficient = (double) beforeInvalidateMillis / (double) intervalBetweenResettingMillis;
                sum = (long) ((double) sum * decayingCoefficient);
            }

            return sum;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Phase{");
            sb.append("sum=").append(adder);
            sb.append(", proposedInvalidationTimestamp=").append(proposedInvalidationTimestamp);
            sb.append('}');
            return sb.toString();
        }
    }
}
