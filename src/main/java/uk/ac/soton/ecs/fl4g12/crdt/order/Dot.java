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

import uk.ac.soton.ecs.fl4g12.crdt.datatypes.commutative.CmRDT;

/**
 * Representation of a single identifier from a {@linkplain VersionVector} as a
 * {@linkplain LogicalVersion}. This can be used to represent the timestamp of an event from a node
 * with a given identifier where the event represents only the local change. This is particularly
 * used in {@link CmRDT}s.
 *
 * @param <K> the type of the identifier.
 * @param <T> the type of the timestamp.
 */
public final class Dot<K, T extends Comparable<T>> implements LogicalVersion<T, Dot<K, T>> {

  private final K identifier;
  private final LogicalVersion<T, ?> logicalVersion;

  public Dot(K identifier, LogicalVersion<T, ?> version) {
    this.identifier = identifier;
    this.logicalVersion = version;
  }

  /**
   * Get the identifier that this {@linkplain Dot} represents the {@linkplain Version} for.
   *
   * @return the identifier that this {@link Dot} represents.
   */
  public K getIdentifier() {
    return identifier;
  }

  /**
   * Get the {@linkplain LogicalVersion} that this {@linkplain Dot} wraps. Altering the
   * {@link LogicalVersion} will alter this {@link Dot}, to avoid this,
   * {@link LogicalVersion#copy()} can be used.
   *
   * @return the {@link LogicalVersion} that this {@link Dot} wraps.
   */
  public LogicalVersion<T, ?> getLogicalVersion() {
    return logicalVersion;
  }

  @Override
  public T get() {
    return logicalVersion.get();
  }

  @Override
  public void increment() throws ArithmeticException {
    logicalVersion.increment();
  }

  @Override
  public T successor() {
    return logicalVersion.successor();
  }

  @Override
  public boolean precedes(LogicalVersion<T, ?> version) {
    return logicalVersion.precedes(version);
  }

  /**
   * Determine if this {@linkplain Dot} happens exactly one increment before the provided
   * {@linkplain VersionVector}. If this returns true, then there should exist no version which
   * happened-before this version but happened-after the provided one. This only considers the
   * identifier for which this {@linkplain Dot} represents.
   *
   * @param vector the {@link VersionVector} to compare against.
   * @return {@code true} if this {@linkplain Dot} directly proceeds the provided
   *         {@linkplain VersionVector} such that no other {@linkplain VersionVector} exists which
   *         happened-before this version but happened-after the provided one with respect to the
   *         identifier that this {@linkplain Dot} represents, {@code false} otherwise.
   */
  public boolean precedes(VersionVector<K, T> vector) {
    LogicalVersion<T, ?> otherVersion = vector.getLogicalVersion(identifier);
    // If null, can't hapen before a zero vector
    return otherVersion == null ? false : logicalVersion.precedes(otherVersion);
  }

  @Override
  public boolean identical(LogicalVersion<T, ?> version) {
    return logicalVersion.identical(version);
  }

  /**
   * Checks if this {@linkplain Dot} is identical to the provided {@linkplain VersionVector} for the
   * identifier which this {@linkplain Dot} represents. This method compares the timestamp of the
   * identifier which this {@link Dot} represents with the corresponding timestamp in the provided
   * {@linkplain VersionVector}.
   *
   * @param vector the {@link VersionVector} to compare with.
   * @return whether the provided {@link VersionVector} is identical to the value with the same
   *         identifier as this {@linkplain Dot}.
   */
  public boolean identical(VersionVector<K, T> vector) {
    LogicalVersion<T, ?> other = vector.getLogicalVersion(identifier);
    if (other == null) {
      other = logicalVersion.getZero();
    }
    return logicalVersion.identical(other);
  }

  @Override
  public boolean happenedBefore(LogicalVersion<T, ?> version) {
    return logicalVersion.happenedBefore(version);
  }

  /**
   * Determine if this {@linkplain Dot} happened before the provided one. If they are equal, then
   * this method will also return false. If {@code a} happened-before {@code b} then
   * {@code a.happenedBefore(b) == true}. This only considers the identifier for which this
   * {@linkplain Dot} represents.
   *
   * @param vector the {@linkplain VersionVector} to compare with.
   * @return {@code true} if this {@linkplain Dot} happened-before the provided
   *         {@linkplain Version}, {@code false} otherwise.
   */
  public boolean happenedBefore(VersionVector<K, T> vector) {
    LogicalVersion<T, ?> otherVersion = vector.getLogicalVersion(identifier);
    // If null, can't hapen before a zero vector
    return otherVersion == null ? false : logicalVersion.happenedBefore(otherVersion);
  }

  @Override
  public void sync(T timestamp) {
    logicalVersion.sync(timestamp);
  }

  @Override
  public void sync(LogicalVersion<T, ?> version) {
    logicalVersion.sync(version);
  }

  /**
   * Synchronise the local {@linkplain Dot} with another {@linkplain VersionVector}. A
   * synchronisation updates the local state of this {@linkplain Dot} using the value of the
   * matching identifier in the {@link Dot} provided. Performing the sync will ensure that
   *
   * Only the local version is mutated and so to perform a two way merge,
   * {@code a.sync(b); b.sync(a);} must be performed.
   *
   * @param vector the {@linkplain VersionVector} to synchronise with.
   */
  public void sync(VersionVector<K, T> vector) {
    sync(vector.get(identifier));
  }

  @Override
  public int compareTo(LogicalVersion<T, ?> o) {
    if (o instanceof Dot) {
      // Dot specific implementation
      // If identifiers are different they are concurrent
      if (!identifier.equals(((Dot) o).identifier)) {
        return 0;
      }
    }
    // Treat as a plain LogicalVersion
    return logicalVersion.compareTo(o);
  }

  @Override
  public Dot<K, T> copy() {
    return new Dot<>(identifier, logicalVersion.copy());
  }

  @Override
  public Dot<K, T> getZero() {
    return new Dot<>(identifier, logicalVersion.getZero().copy());
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 53 * hash + this.identifier.hashCode();
    hash = 53 * hash + this.logicalVersion.hashCode();
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
    final Dot<?, ?> other = (Dot<?, ?>) obj;
    if (!this.identifier.equals(other.identifier)) {
      return false;
    }
    if (!this.logicalVersion.equals(other.logicalVersion)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Dot{" + "identifier=" + identifier + ", version=" + logicalVersion + '}';
  }



}
