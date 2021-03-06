package com.github.msemys.esjc;

/**
 * Expected version - optimistic concurrency check for stream write operations.
 */
public class ExpectedVersion {
    private static final ExpectedVersion NO_STREAM = new ExpectedVersion(-1);
    private static final ExpectedVersion ANY = new ExpectedVersion(-2);
    private static final ExpectedVersion STREAM_EXISTS = new ExpectedVersion(-4);

    /**
     * Stream version value
     */
    public final int value;

    private ExpectedVersion(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpectedVersion that = (ExpectedVersion) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    /**
     * Creates expected version of the specified event number (idempotence is guaranteed).
     *
     * @param eventNumber event number that you expect the target stream to currently be at.
     * @return expected version of the specified event number.
     */
    public static ExpectedVersion of(int eventNumber) {
        return new ExpectedVersion(eventNumber);
    }

    /**
     * Specifies the expectation that target stream does not yet exist.
     */
    public static ExpectedVersion noStream() {
        return NO_STREAM;
    }

    /**
     * Disables the optimistic concurrency check (idempotence is not guaranteed).
     */
    public static ExpectedVersion any() {
        return ANY;
    }

    /**
     * Specifies the expectation that stream should exist (idempotence is not guaranteed).
     * If it or a metadata stream does not exist treat that as a concurrency problem.
     */
    public static ExpectedVersion streamExists() {
        return STREAM_EXISTS;
    }
}
