/*
 * The MIT License
 *
 * Copyright 2016 Fabrizio Lungo <fl4g12@ecs.soton.ac.uk>.
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

import java.util.Map;
import java.util.Set;

/**
 * Interface for version vectors. A typical version vector maps an identifier of the node to a
 * monotonically increasing timestamp value which can be incremented with
 * {@link #increment(java.lang.Object)}. The timestamps should be initialised with a zero value when
 * the identifier is initialised.
 *
 * Implementations should be thread safe.
 *
 * @param <K> the type of the identifier.
 * @param <T> the type of the timestamp.
 */
public interface VersionVector<K, T extends Comparable<T>>
    extends Version<Map<K, T>, VersionVector<K, T>, VersionVector<K, T>> {

  /**
   * Returns a map that represents the version vector. The map will be created and changes to the
   * map will not affect the version vector. This can be used to get a snapshot of the vector at a
   * specific time. The map should contain all keys which would be returned by
   * {@link #getIdentifiers()}.
   *
   * @return a map representing the version vector.
   */
  @Override
  Map<K, T> get();

  /**
   * Gets the current timestamp of the vector for a given identifier. If the {@code id} is not
   * initialised, then its value will implicitly be {@code zero}.
   *
   * @param id the identifier which the timestamp should be returned for.
   * @return the timestamp from the vector for the specified identifier.
   */
  T get(K id);

  /**
   * Gets the {@linkplain LogicalVersion} being used for a specific identifier. If the identifier
   * has not been initialised then {@code null} will be returned. Mutating the
   * {@linkplain LogicalVersion} will alter the {@linkplain VersionVector} but can be the
   * {@link LogicalVersion#copy()} method can be used to get a copy which will not affect the
   * {@linkplain VersionVector}.
   *
   * @param id the identifier which the {@link LogicalVersion} should be returned for.
   * @return the timestamp from the vector for the specified identifier.
   */
  LogicalVersion<T, ?> getLogicalVersion(K id);

  /**
   * Get the dot for the given identifier. The dot is tied to this version vector and so changes to
   * the {@link Dot} will affect the state of this {@linkplain Version}. To get an independent dot,
   * call {@link Dot#copy()}.
   *
   * @param id the identifier to create a dot for.
   * @return the dot for the given version.
   */
  Dot<K, T> getDot(K id);

  /**
   * Gets a set of initialised identifiers contained within this vector.
   *
   * @return the set of identifiers which this vector contains.
   */
  Set<K> getIdentifiers();

  /**
   * Initialise the given ID in the vector. Adds the identifier to the vector initialising with a 0
   * value returning the {@linkplain LogicalVersion} that is created. If the version already exists
   * then no changes are made and the existing {@linkplain LogicalVersion} stored in the
   * {@linkplain VersionVector} is returned.
   *
   * @param id the id to initialise.
   * @return the initialised {@linkplain LogicalVersion} for the given identifier.
   */
  LogicalVersion<T, ?> init(K id);

  /**
   * Increments the timestamp for the node represented by the given identifier. Only the local id
   * should be incremented.
   *
   * @param id the id of the node who's timestamp is to be incremented.
   */
  void increment(K id);

  /**
   * The the state of the {@linkplain VersionVector} with given id incremented. This method does not
   * change the state of the {@linkplain VersionVector} and represents a successor of this
   * {@linkplain VersionVector}. The {@link Map} returned is the same as the one which would be
   * returned by {@link #get()} with the given {@code id} incremented. If the given identifier is
   * not initialised, its implicit zero value should be used.
   *
   * @param id the id of the node who's timestamp would be incremented.
   * @return a map of the current state with the given id incremented.
   * @see #get() for details about the map that is returned.
   */
  Map<K, T> successor(K id);

  /**
   * Synchronise a single node's timestamp. If the timestamp given is greater than the local
   * timestamp for the same node, then it should be set to the provided value. If the the provided
   * id has not been initialised locally, then it should be initialised.
   *
   * @param id the id of the node to synchronise within the vector.
   * @param value the timestamp of the node.
   */
  void sync(K id, T value);

  /**
   * Synchronise this {@linkplain VersionVector} with the given {@linkplain Dot}. Synchronised the
   * version for the node with the identifier matching that which is found in the {@link Dot}.
   *
   * @param dot the {@link Dot} to synchronise with.
   */
  void sync(Dot<K, T> dot);

  /**
   * Determine if this version is concurrent with the provided one. Useful for distinguishing
   * between vectors that are equal or concurrent.
   *
   * @param other the vector to compare with.
   * @return {@code true} if this {@linkplain VersionVector} is concurrent with the {@code other}
   *         {@linkplain VersionVector}, {@code false} otherwise.
   */
  boolean concurrentWith(VersionVector<K, T> other);

  /**
   * Determine if this {@linkplain VersionVector} happened-before the given {@linkplain Dot}.
   * Compares the {@link Dot} with this {@linkplain VersionVector}'s timestamp for the same
   * identifier that the {@link Dot} is for.
   *
   * @param dot the {@link Dot} to compare to.
   * @return {@code true} if this {@linkplain Version} happened-before the provided {@link Dot},
   *         {@code false} otherwise.
   */
  public boolean happenedBefore(Dot<K, T> dot);

  /**
   * Determine if this {@linkplain VersionVector} happens exactly one increment before the
   * {@linkplain Dot} provided for the identifier which the {@linkplain Dot} is used for. If this
   * returns true, then there should exist no version which happened-before this version but
   * happened-after the provided one for the identifier that the {@link Dot} represents.
   *
   * @param dot the {@link Dot} to compare against.
   * @return {@code true} if this {@linkplain VersionVector} directly proceeds the provided
   *         {@linkplain Dot} such that no other {@linkplain Dot} exists which happened-before this
   *         version but happened-after the provided one for the identifier that the {@link Dot}
   *         represents, {@code false} otherwise.
   */
  public boolean precedes(Dot<K, T> dot);

  /**
   * Checks if this {@linkplain VersionVector} is identical to the provided {@linkplain Dot} for the
   * identifier which the {@linkplain Dot} represents. This method compares the timestamp of the
   * identifier which the {@link Dot} representsin this {@linkplain VersionVector}.
   *
   * @param dot the {@link Dot} to compare with.
   * @return whether the provided {@link Dot} is identical to the value with the same identifier in
   *         this {@linkplain VersionVector}.
   */
  public boolean identical(Dot<K, T> dot);

  /**
   * Compares this {@linkplain VersionVector} to another provided {@linkplain VersionVector}.
   *
   * @param other the version vector to compare to.
   * @return {@code 0} if this {@linkplain VersionVector} and the {@code other}
   *         {@linkplain VersionVector} are identical or concurrent, negative if this
   *         {@linkplain VersionVector} happened before and positive if the {@code other}
   *         {@linkplain VersionVector} happened before.
   */
  @Override
  int compareTo(VersionVector<K, T> other);

}
