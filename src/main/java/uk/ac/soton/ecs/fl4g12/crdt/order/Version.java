/*
 * The MIT License
 *
 * Copyright 2017 Fabrizio Lungo <fl4g12@ecs.soton.ac.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.ac.soton.ecs.fl4g12.crdt.order;

/**
 * Interface for synchronisable {@linkplain Version}s. {@linkplain Version}s represent the a
 * timestamp which can be used to determine causality between versioned objects.
 * {@linkplain Version}s should be monotonically increasing. {@linkplain Version}s have a
 * happened-before relationship between them which can be used to determine causal order.
 *
 * @param <T> the type of the timestamp.
 * @param <V1> the type of {@linkplain Version}s which this {@linkplain Version} can perform
 *        operations with.
 * @param <V2> the specific type of {@link Version} which is returned when cloning.
 */
public interface Version<T, V1 extends Version<T, V1, ?>, V2 extends V1> extends Comparable<V1> {

  /**
   * Gets a usable representation of the timestamp for this {@linkplain Version}. The returned value
   * should either be immutable or a clone of the internal value to ensure no modifications can be
   * made to this {@linkplain Version}.
   *
   * @return the timestamp for this {@linkplain Version}.
   */
  T get();

  /**
   * Synchronise the local {@linkplain Version} with another {@linkplain Version}. A synchronisation
   * updates the local state of this {@linkplain Version} to include everything in the provided
   * {@linkplain Version}. Performing the sync will ensure that
   *
   * Only this {@linkplain Version} is mutated and so to perform a two way merge,
   * {@code a.sync(b); b.sync(a);} must be performed.
   *
   * @param version the {@linkplain Version} to synchronise with.
   */
  void sync(V1 version);

  /**
   * Synchronise the local version with the given timestamp.
   *
   * @param timestamp the timestamp to synchronise with.
   * @see #sync(Version) for more detail on synchronisation.
   */
  void sync(T timestamp);

  /**
   * Determine if this {@linkplain Version} happened before the provided one. If they are equal,
   * then this method will also return false. If {@code a} happened-before {@code b} then
   * {@code a.happenedBefore(b) == true}.
   *
   * @param version the {@linkplain Version} to compare with.
   * @return {@code true} if this {@linkplain Version} happened-before the provided
   *         {@linkplain Version}, {@code false} otherwise.
   */
  boolean happenedBefore(V1 version);

  /**
   * Determine if this {@linkplain Version} precedes the provided one. If this {@linkplain Version}
   * precedes the provided one, then there should exists no {@linkplain Version} which
   * happened-after this {@linkplain Version} but happened-before the provided one.
   *
   * Precedence can be used to provide causal exactly-once delivery of messages providing that the
   * version encapsulates the causality of the message, and there exists a message for every unit
   * mutation of the {@linkplain Version}.
   *
   * @param version the {@linkplain Version} to compare against.
   * @return {@code true} if this {@linkplain Version} precedes the provided {@linkplain Version}
   *         such that no other {@linkplain Version} exists which happened-after this version but
   *         happened-before the provided one, {@code false} otherwise.
   */
  boolean precedes(V1 version);

  /**
   * Checks if two {@linkplain Version}s are identical. This method compares the state of the two
   * {@linkplain Version}s and will allow implicitly identical versions and versions of different
   * implementations (unlike the {@link #equals(Object)} method).
   *
   * @param version the {@linkplain Version} to compare with.
   * @return whether the provided version is identical to this version.
   */
  boolean identical(V1 version);

  /**
   * Checks if the {@linkplain Version} is strictly identical and of the same type to the provided
   * version.
   *
   * Strictly identical means that implicit values will not be considered and so not all
   * {@link #identical(uk.ac.soton.ecs.fl4g12.crdt.order.Version)} objects of the same type will be
   * {@linkplain #equals(java.lang.Object)}. This behaviour is to ensure the fulfilment of the
   * {@link Object#Object()} and {@link Object#hashCode()} contracts.
   *
   * @param obj the object to evaluate equality with.
   * @return {@code true} if this {@linkplain Version} and the {@code other} {@linkplain Version}
   *         are identical, {@code false} otherwise.
   */
  @Override
  boolean equals(Object obj);

  /**
   * Make a copy of the {@linkplain Version} with the same state.
   *
   * @return the cloned version.
   */
  V2 copy();

}
