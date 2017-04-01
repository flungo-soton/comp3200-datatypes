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

import uk.ac.soton.ecs.fl4g12.crdt.delivery.AbstractVersionedUpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.StateSnapshot;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Representation of the state for a {@linkplain GCounter}.
 *
 * @param <E> the type of the counter value.
 * @param <K> the type of identifier used to identify nodes.
 */
public final class GCounterState<E extends Comparable<E>, K>
    extends AbstractVersionedUpdateMessage<K, E> implements StateSnapshot<K, E> {

  /**
   * Instantiate a new {@linkplain GCounterState}.
   *
   * @param identifier the identifier of the instance that was updated.
   * @param versionVector the version as a result of the update.
   */
  GCounterState(K identifier, VersionVector<K, E> versionVector) {
    super(identifier, versionVector);
  }

  @Override
  public String toString() {
    return "GCounterState{" + "identifier=" + getIdentifier() + ", version=" + getVersionVector()
        + '}';
  }

}
