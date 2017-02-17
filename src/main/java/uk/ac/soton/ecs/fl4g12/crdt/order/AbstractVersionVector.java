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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Abstract {@link VersionVector}. To be extended by implementations {@link VersionVector} that wish
 * to take advantage of the abstract methods provided by this class. When extending this abstract
 * class, it is assumed that timestamps are stored internally as {@link LogicalVersion}s.
 *
 * @param <K> the type of the identifier.
 * @param <T> the type of the version.
 */
public abstract class AbstractVersionVector<K, T extends Comparable<T>>
        extends AbstractVersion<Map<K, T>> implements VersionVector<K, T> {

  private final T zero;
  private final boolean dotted;

  /**
   * Construct an {@linkplain AbstractVersionVector}.
   *
   * The {@code zero} timestamp provided should be immutable or ensured to never be mutated.
   *
   * @param zero the timestamp that represents zero.
   * @param dotted whether or not this is a dotted {@link VersionVector}.
   */
  public AbstractVersionVector(T zero, boolean dotted) {
    this.zero = zero;
    this.dotted = dotted;
  }

  protected abstract LogicalVersion<T> getInternal(K id);

  @Override
  public T get(K id) {
    return getInternal(id).get();
  }

  @Override
  public Map<K, T> get() {
    Map<K, T> map = new HashMap<>();
    for (K id : getIdentifiers()) {
      map.put(id, get(id));
    }
    return map;
  }

  @Override
  public void increment(K id) {
    if (!getIdentifiers().contains(id)) {
      throw new IllegalArgumentException("Provided ID has not been initialised as part of the vector.");
    }
    getInternal(id).increment();
  }

  @Override
  public void sync(Map<K, T> timestamp) {
    for (Map.Entry<K, T> entry : timestamp.entrySet()) {
      sync(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public boolean happenedBefore(Version<Map<K, T>> o) {
    // Get snapshot of each vector to work with
    Map<K, T> local = get();
    Map<K, T> other = o.get();

    // Iterate through all ids known locally. Any id that might exist in
    // other that does not exist locally will be greater than or equal to
    // zero and so satisfies the happens-before relation.
    for (Map.Entry<K, T> entry : local.entrySet()) {
      // Get the ID
      K id = entry.getKey();
      // Get the local and other value for the current ID.
      T localValue = local.get(id);
      if (localValue == null) {
        // If the node has not been seen locally, then its value is zero.
        localValue = zero;
      }
      T otherValue = other.get(id);
      if (otherValue == null) {
        // If the node has not been seen by the other, then its value is zero.
        otherValue = zero;
      }

      // Compare the local to the other
      int comparison = localValue.compareTo(otherValue);

      // Determine if there is a partial ordering.
      if (comparison < 0) {
        return false;
      }
    }
    return true;
  }

  private Integer compareToInternal(Version<Map<K, T>> o) {
    // Get snapshot of each vector to work with
    Map<K, T> local = get();
    Map<K, T> other = o.get();
    // TODO: Implement support for dotted vectors

    // Merge all identifiers together
    Set<K> identifiers = new HashSet(local.keySet());
    identifiers.addAll(other.keySet());

    // TODO: What happens in case of over/underflow?
    int accumulator = 0;
    for (K id : identifiers) {
      // Get the local and other value for the current ID.
      T localValue = local.get(id);
      if (localValue == null) {
        // If the node has not been seen locally, then its value is zero.
        localValue = zero;
      }
      T otherValue = other.get(id);
      if (otherValue == null) {
        // If the node has not been seen by the other, then its value is zero.
        otherValue = zero;
      }

      // Compare the local to the other
      int comparison = localValue.compareTo(otherValue);

      // Determine if there is a partial ordering.
      if (comparison < 0) {
        if (accumulator > 0) {
          // No ordering, must be concurrent.
          return null;
        }
        accumulator += comparison;
      } else if (comparison > 0) {
        if (accumulator < 0) {
          // No ordering, must be concurrent.
          return null;
        }
        accumulator += comparison;
      } // Else they are equal and we don't need to do anything.
    }
    return accumulator;
  }

  @Override
  public boolean concurrentWith(VersionVector<K, T> other) {
    return compareToInternal(other) == null;
  }

  @Override
  public int compareTo(Version<Map<K, T>> o) {
    // Null gets cast to zero
    return compareToInternal(o);
  }

  @Override
  public boolean isDotted() {
    return dotted;
  }

  @Override
  public abstract AbstractVersionVector<K, T> copy();

}
