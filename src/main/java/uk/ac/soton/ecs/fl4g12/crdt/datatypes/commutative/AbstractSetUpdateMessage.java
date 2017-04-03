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

import java.util.Set;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.commutative.SetUpdateMessage.Operation;
import uk.ac.soton.ecs.fl4g12.crdt.order.Dot;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Abstract {@link SetUpdateMessage} to be extended for use with {@linkplain CmRDT}
 * {@linkplain Set}s. Contains a {@link Set} of elements that were involved in the operation.
 *
 * @param <E> the type of values stored in the {@linkplain CmRDT} {@link Set}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp stored in the {@link VersionVector}
 */
public abstract class AbstractSetUpdateMessage<E, K, T extends Comparable<T>>
    extends AbstractGrowOnlySetUpdateMessage<E, K, T> implements SetUpdateMessage<E, K, Dot<K, T>> {

  private final Operation operation;

  /**
   * Construct an {@link AbstractSetUpdateMessage} with a list of elements that were added.
   *
   * @param dot the {@link Dot} for the update.
   * @param operation the {@link Operation} performed by the update.
   * @param elements the elements that were added to the set. The set will not be copied.
   */
  public AbstractSetUpdateMessage(Dot<K, T> dot, Operation operation, Set<? extends E> elements) {
    super(dot, elements);
    this.operation = operation;
  }

  @Override
  public Operation getOperation() {
    return operation;
  }

  @Override
  public String toString() {
    return "AbstractSetUpdateMessage{" + "identifier=" + identifier + ", version=" + version
        + ", operation=" + operation + ", elements=" + elements + '}';
  }



}
