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

package uk.ac.soton.ecs.fl4g12.crdt.datatypes.commutative;

import java.util.HashSet;
import java.util.Set;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.AbstractVersionedUpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Abstract {@link GrowOnlySetUpdateMessage} to be extended for use with grow-only
 * {@linkplain CmRDT} {@linkplain Set}s. Contains a {@link Set} of elements that were involved in
 * the operation.
 *
 * @param <E> the type of values stored in the {@linkplain CmRDT} {@link Set}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp stored in the {@link VersionVector}
 */
public abstract class AbstractGrowOnlySetUpdateMessage<E, K, T extends Comparable<T>>
    extends AbstractVersionedUpdateMessage<K, T> implements GrowOnlySetUpdateMessage<E, K, T> {

  private final Set<? extends E> elements;

  /**
   * Construct an {@link AbstractGrowOnlySetUpdateMessage} with a list of elements that were added.
   *
   * @param identifier the identifier of the instance that was updated.
   * @param versionVector the version as a result of the update.
   * @param elements the elements that were added to the set. The set will not be copied.
   */
  public AbstractGrowOnlySetUpdateMessage(K identifier, VersionVector<K, T> versionVector,
      Set<? extends E> elements) {
    super(identifier, versionVector);
    this.elements = elements;
  }

  @Override
  public final Set<? extends E> getElements() {
    return new HashSet<>(elements);
  }

  @Override
  public String toString() {
    return "CommutativeGSetUpdate{" + "identifier=" + getIdentifier() + ", version="
        + getVersionVector() + ", elements=" + getElements() + '}';
  }

}
