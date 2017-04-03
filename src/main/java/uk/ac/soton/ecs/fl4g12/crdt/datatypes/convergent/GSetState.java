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

package uk.ac.soton.ecs.fl4g12.crdt.datatypes.convergent;

import java.util.HashSet;
import java.util.Set;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.AbstractVersionedUpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Representation of the state for a {@linkplain GSet}.
 *
 * @param <E> the type of values stored in the {@link GSet}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp stored in the {@link VersionVector}
 */
public final class GSetState<E, K, T extends Comparable<T>>
    extends AbstractVersionedUpdateMessage<K, VersionVector<K, T>>
    implements SetState<E, K, VersionVector<K, T>> {

  private final Set<E> state;

  /**
   * Instantiate a new {@linkplain GSetState}.
   *
   * @param identifier the identifier of the instance that was updated.
   * @param versionVector the version as a result of the update.
   * @param state the state of the {@linkplain GSet}.
   */
  GSetState(K identifier, VersionVector<K, T> versionVector, Set<E> state) {
    super(identifier, versionVector);
    this.state = new HashSet<>(state);
  }

  /**
   * Get a copy of the set of elements that have been added to the {@link GSet}.
   *
   * @return a copy of the set of elements that have been added to the {@link GSet}.
   */
  @Override
  public Set<E> getState() {
    return new HashSet<>(state);
  }

  @Override
  public int hashCode() {
    int hash = super.hashCode();
    hash = 97 * hash + this.state.hashCode();
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (!super.equals(obj)) {
      return false;
    }
    final GSetState<?, ?, ?> other = (GSetState<?, ?, ?>) obj;
    if (!this.state.equals(other.state)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "GSetState{" + "identifier=" + getIdentifier() + ", version=" + getVersion() + ", state="
        + state + '}';
  }

}
