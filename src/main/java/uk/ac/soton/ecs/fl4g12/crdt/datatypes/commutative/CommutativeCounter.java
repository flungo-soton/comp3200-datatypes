/*
 * The MIT License
 *
 * Copyright 2017 Fabrizio Lungo <fl4g12@ecs.soton.ac.uk>.
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

import uk.ac.soton.ecs.fl4g12.crdt.datatypes.Counter;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.ReliableDeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * A {@link CmRDT} {@link Counter} which wraps a local counter to provide commutative replication.
 *
 * @param <E> the type of the counter value.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp stored in the {@link VersionVector}
 */
public class CommutativeCounter<E, K, T extends Comparable<T>>
    extends AbstractDottedCmRDT<K, T, CommutativeCounterUpdate<E, K, T>> implements Counter<E> {

  private final Counter<E> counter;

  /**
   * Create a commutative counter which is replicated by delivering operations to replicas. This
   * wraps the provided {@link Counter} and replicates the updates using the provided
   * {@link DeliveryChannel}.
   *
   * @param counter the {@link Counter} which will be used as the local counter.
   * @param initialVersion the initial {@link VersionVector} which the {@link Counter} should be
   *        initialised with.
   * @param identifier the identifier of this instance or {@code null} for it to be assigned by the
   *        {@link DeliveryChannel}.
   * @param deliveryChannel the {@link DeliveryChannel} which this {@link Counter} should
   *        communicate changes over.
   */
  public CommutativeCounter(Counter<E> counter, VersionVector<K, T> initialVersion, K identifier,
      ReliableDeliveryChannel<K, CommutativeCounterUpdate<E, K, T>> deliveryChannel) {
    super(initialVersion, identifier, deliveryChannel);
    this.counter = counter;
  }

  @Override
  protected void effectUpdate(CommutativeCounterUpdate<E, K, T> message) {
    switch (message.getOperation()) {
      case INCREMENT:
        counter.increment();
        break;
      case DECREMENT:
        counter.decrement();
        break;
      default:
        throw new UnsupportedOperationException(
            "Unsupported update operation: " + message.getOperation());
    }
  }

  public synchronized CommutativeCounterUpdate<E, K, T> createUpdateMessage(
      CommutativeCounterUpdate.Operation operation) {
    version.increment();
    return new CommutativeCounterUpdate<>(version.getDot(identifier), operation);
  }

  @Override
  public void increment() {
    counter.increment();
    getDeliveryChannel().publish(createUpdateMessage(CommutativeCounterUpdate.Operation.INCREMENT));
  }

  @Override
  public void decrement() {
    counter.decrement();
    getDeliveryChannel().publish(createUpdateMessage(CommutativeCounterUpdate.Operation.DECREMENT));
  }

  @Override
  public E value() {
    return counter.value();
  }

}
