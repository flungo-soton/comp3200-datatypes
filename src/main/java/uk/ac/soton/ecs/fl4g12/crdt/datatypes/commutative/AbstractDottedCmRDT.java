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

import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DottedUpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.UpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.order.Dot;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Abstract base class for {@linkplain CmRDT}s where the {@linkplain UpdateMessage} uses a
 * {@linkplain Dot}. Messages are not applied in causal order but exactly-once delivery is
 * respected.
 *
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of timestamps which are used by each node.
 * @param <M> the type of updates which this object can be updated by.
 * @see AbstractCmRDT for the guarantees which this provides and implementation details.
 */
public abstract class AbstractDottedCmRDT<K, T extends Comparable<T>, M extends DottedUpdateMessage<K, T>>
    extends AbstractCmRDT<K, T, M> {

  public AbstractDottedCmRDT(VersionVector<K, T> initialVersion, K identifier,
      DeliveryChannel<K, M> deliveryChannel) {
    super(initialVersion, identifier, deliveryChannel);
  }

  @Override
  protected final boolean precedes(M message) {
    return version.precedes(message.getVersion());
  }

  @Override
  protected final boolean hasBeenApplied(M message) {
    return message.getVersion().happenedBefore(version) || message.getVersion().identical(version);
  }

  @Override
  protected final void sync(M message) {
    version.sync(message.getVersion());
  }

}
