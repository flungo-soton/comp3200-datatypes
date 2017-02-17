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
public interface VersionVector<K, T extends Comparable<T>> extends Version<Map<K, T>> {

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
   * Gets a set of identifiers contained within this vector.
   *
   * @return the set of identifiers which this vector contains.
   */
  Set<K> getIdentifiers();

  /**
   * Gets the current timestamp of the vector for a given identifier.
   *
   * @param id the identifier which the timestamp should be returned for.
   * @return the timestamp from the vector for the specified identifier.
   */
  T get(K id);

  /**
   * Initialise the given ID in the vector. Adds the identifier to the vector initialising with a 0
   * value.
   *
   * @param id the id to initialise.
   */
  void init(K id);

  /**
   * Increments the timestamp for the node represented by the given identifier.
   *
   * @param id the id of the node who's timestamp is to be incremented.
   */
  void increment(K id);

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
   * Determine if this version is concurrent with the provided one. Useful for distinguishing
   * between vectors that are equal or concurrent.
   *
   * @param other the vector to compare with.
   * @return {@code true} if this {@link VersionVector} is concurrent with the {@code other}
   *         {@link VersionVector}, {@code false} otherwise.
   */
  boolean concurrentWith(VersionVector<K, T> other);

  /**
   * Compares this version vector to another provided version vector.
   *
   * @param other the version vector to compare to.
   * @return {@code 0} if this {@link VersionVector} and the {@code other} {@link VersionVector} are
   *         identical or concurrent, negative if this {@link VersionVector} happened before and
   *         positive if the {@code other} {@link VersionVector} happened before.
   */
  @Override
  int compareTo(Version<Map<K, T>> other);

  /**
   * Determine if this version vector will be treated as a dotted version vector for the purposes of
   * incrementing and synchronisation.
   *
   * @return {@code true} if the vector should be treated as a dotted version vector, {@code false}
   *         otherwise.
   */
  boolean isDotted();

  @Override
  VersionVector<K, T> copy();
}
