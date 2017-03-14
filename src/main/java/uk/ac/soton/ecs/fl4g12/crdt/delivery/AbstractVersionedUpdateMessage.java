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

package uk.ac.soton.ecs.fl4g12.crdt.delivery;

import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Abstract implementation of {@linkplain VersionedUpdateMessage}. Contains the
 * {@link VersionVector} of the {@link Updatable} at the time of the update and the identifier of
 * the instance that was updated.
 *
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp within the {@link VersionVector}.
 */
public abstract class AbstractVersionedUpdateMessage<K, T extends Comparable<T>>
    implements VersionedUpdateMessage<K, T> {

  private final K identifier;
  private final VersionVector<K, T> versionVector;

  /**
   * Instantiate the {@linkplain AbstractVersionedUpdateMessage}.
   *
   * @param identifier the identifier of the instance that was updated.
   * @param versionVector the version as a result of the update.
   */
  public AbstractVersionedUpdateMessage(K identifier, VersionVector<K, T> versionVector) {
    this.identifier = identifier;
    this.versionVector = versionVector.copy();
  }

  @Override
  public final K getIdentifier() {
    return identifier;
  }

  @Override
  public final VersionVector<K, T> getVersionVector() {
    return versionVector.copy();
  }

  @Override
  public final int compareTo(VersionedUpdateMessage<K, T> o) {
    return versionVector.compareTo(o.getVersionVector());
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + this.identifier.hashCode();
    hash = 97 * hash + this.versionVector.hashCode();
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
    final AbstractVersionedUpdateMessage other = (AbstractVersionedUpdateMessage) obj;
    if (!this.identifier.equals(other.identifier)) {
      return false;
    }
    if (!this.versionVector.equals(other.versionVector)) {
      return false;
    }
    return true;
  }

}
