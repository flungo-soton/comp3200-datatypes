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
 * Representation of the state for a {@linkplain TwoPhaseSet}.
 *
 * @param <E> the type of values stored in the {@link GSet}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp stored in the {@link VersionVector}
 */
public final class TwoPhaseSetState<E, K, T extends Comparable<T>>
    extends AbstractVersionedUpdateMessage<K, T> implements SetState<E, K, T> {

  private final Set<E> additions;
  private final Set<E> removals;

  /**
   * Instantiate a new {@linkplain GSetState}. Arguments provided that are not expected to be
   * immutable by their type, will be cloned before being stored as part of the set.
   *
   * @param identifier the identifier of the instance that was updated.
   * @param versionVector the version as a result of the update.
   * @param additions the additions set of the {@linkplain GSet}.
   * @param removals the removals set of the {@linkplain GSet}.
   */
  TwoPhaseSetState(K identifier, VersionVector<K, T> versionVector, Set<E> additions,
      Set<E> removals) {
    super(identifier, versionVector);
    this.additions = new HashSet<>(additions);
    this.removals = new HashSet<>(removals);
  }

  /**
   * Get the set of elements that have been added to the {@linkplain TwoPhaseSet}.
   *
   * @return the set of elements that have been added to the {@link TwoPhaseSet}.
   */
  public Set<E> getAdditions() {
    return new HashSet<>(additions);
  }

  /**
   * Get the set of elements that have been removed from the {@linkplain TwoPhaseSet}.
   *
   * @return the set of elements that have been removed from the {@link TwoPhaseSet}.
   */
  public Set<E> getRemovals() {
    return new HashSet<>(removals);
  }

  /**
   * Compute the effective of the {@linkplain TwoPhaseSet}. This is the set difference between the
   * elements added and elements removed.
   *
   * @return the effective of the {@link TwoPhaseSet}.
   */
  @Override
  public Set<E> getState() {
    Set<E> state = getAdditions();
    state.removeAll(removals);
    return state;
  }

  @Override
  public int hashCode() {
    int hash = super.hashCode();
    hash = 97 * hash + this.additions.hashCode();
    hash = 97 * hash + this.removals.hashCode();
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (!super.equals(obj)) {
      return false;
    }
    final TwoPhaseSetState<?, ?, ?> other = (TwoPhaseSetState<?, ?, ?>) obj;
    if (!this.additions.equals(other.additions)) {
      return false;
    }
    if (!this.removals.equals(other.removals)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "TwoPhaseSetState{" + "identifier=" + getIdentifier() + ", version=" + getVersionVector()
        + ", additions=" + additions + ", removals=" + removals + '}';
  }

}
