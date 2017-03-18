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

import uk.ac.soton.ecs.fl4g12.crdt.delivery.AbstractVersionedUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryUpdateException;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.UpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Abstract base class for {@linkplain CmRDT}s.
 *
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of timestamps which are used by each node.
 * @param <U> the type of updates which this object can be updated by.
 */
public abstract class AbstractCmRDT<K, T extends Comparable<T>, U extends VersionedUpdateMessage<K, T>>
    extends AbstractVersionedUpdatable<K, T, U> implements CmRDT<K, T, U> {

  public AbstractCmRDT(VersionVector<K, T> initialVersion, K identifier,
      DeliveryChannel<K, U> deliveryChannel) {
    super(initialVersion, identifier, deliveryChannel);
  }

  @Override
  public synchronized final void update(U message) throws DeliveryUpdateException {
    // Has the message already been delivered?
    if (message.getVersionVector().happenedBefore(version)
        || message.getVersionVector().identical(version)) {
      // Nothing to do, update message is in the past.
      return;
    }

    // Is this the next message?
    if (!version.precedes(message.getVersionVector())) {
      throw new DeliveryUpdateException(this, message, "Out of order delivery");
    }

    // Apply the update
    applyUpdate(message);
    version.sync(message.getVersionVector());
  }

  /**
   * Apply the update contained in the message where all preconditions relating to the order of
   * message delivery have already been checked. The version will be synchronised automatically
   * after this method returns as long as no exceptions are thrown.
   *
   * @param message the {@link UpdateMessage} to apply.
   */
  protected abstract void applyUpdate(U message);

}
