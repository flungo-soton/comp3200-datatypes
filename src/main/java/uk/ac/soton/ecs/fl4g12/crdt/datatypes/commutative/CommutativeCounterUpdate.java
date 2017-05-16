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

import uk.ac.soton.ecs.fl4g12.crdt.delivery.AbstractDottedUpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.UpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.order.Dot;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * {@linkplain UpdateMessage} for {@link CommutativeCounter} representing an increment or decrement.
 *
 * @param <E> the type counted by the {@link CommutativeCounter}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp stored in the {@link VersionVector}
 */
public final class CommutativeCounterUpdate<E, K, T extends Comparable<T>>
    extends AbstractDottedUpdateMessage<K, T> {

  private final Operation operation;

  /**
   * Construct a {@link CommutativeTwoPhaseSetUpdate} with a list of elements that were added.
   *
   * @param dot the {@link Dot} for the update.
   * @param operation the {@link Operation} that triggered the message.
   */
  public CommutativeCounterUpdate(Dot<K, T> dot, Operation operation) {
    super(dot);
    this.operation = operation;
  }

  public Operation getOperation() {
    return operation;
  }

  public static enum Operation {
    INCREMENT, DECREMENT;
  }

}
