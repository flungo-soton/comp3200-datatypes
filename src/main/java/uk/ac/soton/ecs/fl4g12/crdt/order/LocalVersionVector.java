/*
 * The MIT License
 *
 * Copyright 2017 Fabrizio Lungo <fl4g12@ecs.soton.ac.uk>
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
import java.util.Objects;
import java.util.Set;

/**
 * A locally incrementable {@linkplain VersionVector}. Wraps {@linkplain VersionVector} which acts
 * as a {@linkplain LogicalVersion} by incrementing a fixed identifier in the
 * {@linkplain VersionVector}.
 *
 * @param <K> the type of the identifier.
 * @param <T> the type of the timestamp.
 */
public final class LocalVersionVector<K, T extends Comparable<T>>
    implements VersionVector<K, T>, LogicalVersion<Map<K, T>> {

  private final VersionVector<K, T> versionVector;
  private final K identifier;
  private final LogicalVersion<T> localVersion;

  /**
   * Wrap a given {@link VersionVector} as a {@link LocalVersionVector}. If the provided id is not
   * already initialised within the vector provided, this will be done automatically.
   *
   * @param versionVector the {@link VersionVector} to wrap.
   * @param identifier the identifier to increment in the {@link VersionVector} when
   *        {@link #increment()} is called.
   */
  public LocalVersionVector(VersionVector<K, T> versionVector, K identifier) {
    this.versionVector = versionVector;
    this.identifier = identifier;
    this.localVersion = versionVector.init(identifier);
  }

  /**
   * Get the underlying version vector. Mutating the returned {@link VersionVector} will change the
   * state of this {@linkplain LocalVersionVector}.
   *
   * @return the underlying version vector.
   */
  public VersionVector<K, T> getVersionVector() {
    return versionVector;
  }

  /**
   * Get the identifier that this {@linkplain LocalVersionVector} increments within the underlying
   * {@link VersionVector}.
   *
   * @return the identifier used by the {@linkplain LocalVersionVector}.
   */
  public K getIdentifier() {
    return identifier;
  }

  @Override
  public LogicalVersion<T> getLogicalVersion(K id) {
    return versionVector.getLogicalVersion(id);
  }

  @Override
  public Map<K, T> get() {
    return versionVector.get();
  }

  @Override
  public T get(K id) {
    return versionVector.get(id);
  }

  @Override
  public Dot<K, T> getDot(K id) {
    return versionVector.getDot(id);
  }

  @Override
  public Set<K> getIdentifiers() {
    return versionVector.getIdentifiers();
  }

  @Override
  public LogicalVersion<T> init(K id) {
    return versionVector.init(id);
  }

  @Override
  public void increment(K id) {
    versionVector.increment(id);
  }

  @Override
  public void increment() throws ArithmeticException {
    localVersion.increment();
  }

  @Override
  public Map<K, T> successor(K id) {
    return versionVector.successor(id);
  }

  @Override
  public Map<K, T> successor() {
    return versionVector.successor(identifier);
  }

  @Override
  public void sync(K id, T value) {
    versionVector.sync(id, value);
  }

  @Override
  public void sync(Version<Map<K, T>> version) {
    versionVector.sync(version);
  }

  @Override
  public void sync(Map<K, T> timestamp) {
    versionVector.sync(timestamp);
  }

  @Override
  public void sync(Dot<K, T> dot) {
    versionVector.sync(dot);
  }

  @Override
  public boolean happenedBefore(Version<Map<K, T>> version) {
    return versionVector.happenedBefore(version);
  }

  @Override
  public boolean happenedBefore(Dot<K, T> dot) {
    return versionVector.happenedBefore(dot);
  }

  @Override
  public boolean precedes(Version<Map<K, T>> version) {
    return versionVector.precedes(version);
  }

  @Override
  public boolean concurrentWith(VersionVector<K, T> other) {
    return versionVector.concurrentWith(other);
  }

  @Override
  public int compareTo(Version<Map<K, T>> other) {
    return versionVector.compareTo(other);
  }

  @Override
  public boolean identical(Version<Map<K, T>> version) {
    return versionVector.identical(version);
  }

  @Override
  public LocalVersionVector<K, T> copy() {
    return new LocalVersionVector<>(versionVector.copy(), identifier);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 79 * hash + Objects.hashCode(this.versionVector);
    hash = 79 * hash + Objects.hashCode(this.identifier);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final LocalVersionVector<?, ?> other = (LocalVersionVector<?, ?>) obj;
    if (!Objects.equals(this.versionVector, other.versionVector)) {
      return false;
    }
    if (!Objects.equals(this.identifier, other.identifier)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return versionVector.toString();
  }

}
