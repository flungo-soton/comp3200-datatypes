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

import java.io.Serializable;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.Register;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.AbstractVersionedUpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.StateSnapshot;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Representation of the state for a {@linkplain LWWRegister}.
 *
 * @param <E> the type of value stored in the {@link Register}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp stored in the {@link VersionVector}
 */
public final class LWWRegisterState<E extends Serializable, K extends Comparable<K>, T extends Comparable<T>>
    extends AbstractVersionedUpdateMessage<K, VersionVector<K, T>>
    implements StateSnapshot<K, VersionVector<K, T>> {

  private final LWWRegister.Element<E> element;

  /**
   * Instantiate a new {@linkplain LWWRegisterState}.
   *
   * @param identifier the identifier of the instance that was updated.
   * @param versionVector the version as a result of the update.
   * @param element the element to store as part of the state.
   */
  LWWRegisterState(K identifier, VersionVector<K, T> versionVector,
      LWWRegister.Element<E> element) {
    super(identifier, versionVector);
    this.element = element;
  }

  /**
   * Get the element contained that was stored in the {@linkplain Register}.
   *
   * @return the element stored in the {@linkplain LWWRegister}.
   */
  public LWWRegister.Element<E> getElement() {
    return element;
  }

  @Override
  public String toString() {
    return "LWWRegisterState{" + "identifier=" + getIdentifier() + ", version=" + getVersion()
        + ", element=" + element + '}';
  }



}
