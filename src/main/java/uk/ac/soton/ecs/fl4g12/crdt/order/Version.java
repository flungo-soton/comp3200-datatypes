/*
 * The MIT License
 *
 * Copyright 2017 Fabrizio Lungo <fl4g12@ecs.soton.ac.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package uk.ac.soton.ecs.fl4g12.crdt.order;

/**
 * Interface for synchronisable {@linkplain Version}s. {@linkplain Version}s represent the a timestamp which can be used
 * to determine causality between versioned objects. {@linkplain Version}s should be monotonically increasing.
 * {@linkplain Version}s have a happened-before relationship between them which can be used to determine causal order.
 *
 * @param <T> the type of the timestamp.
 */
public interface Version<T> extends Comparable<Version<T>> {

    /**
     * Gets a usable representation of the timestamp for this {@linkplain Version}. The returned value should either be
     * immutable or a clone of the internal value to ensure no modifications can be made to this version.
     *
     * @return the timestamp for this {@linkplain Version}.
     */
    T get();

    /**
     * Determine if this {@linkplain Version} happened before the provided one. If they are equal, then this method will
     * also return false. If {@code a} happened-before {@code b} then {@code a.happenedBefore(b) == true}.
     *
     * @param version the {@linkplain Version} to compare with.
     * @return {@code true} if this {@linkplain Version} is concurrent with the {@code other}
     * {@linkplain Version}, {@code false} otherwise.
     */
    boolean happenedBefore(Version<T> version);

    /**
     * Synchronise the local {@linkplain Version} with another {@linkplain Version}. A synchronisation updates the local
     * state of the version to include everything in the provided version. Performing the sync will ensure that
     *
     * Only the local version is mutated and so to perform a two way merge, {@code a.sync(b); b.sync(a);} must be
     * performed.
     *
     * @param version the {@linkplain Version} to synchronise with.
     */
    void sync(Version<T> version);

    /**
     * Synchronise the local version with the given timestamp.
     *
     * @param timestamp the timestamp to synchronise with.
     * @see #sync(Version) for more detail on synchronisation.
     */
    void sync(T timestamp);

    /**
     * Checks if the {@linkplain Version} is equal to the provided version.
     *
     * @param obj the object to evaluate equality with.
     * @return {@code true} if this {@linkplain Version} and the {@code other} {@linkplain Version} are identical,
     * {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj);

    /**
     * Make a copy of the version with the same state.
     *
     * @return the cloned version.
     */
    public Version<T> copy();

}
