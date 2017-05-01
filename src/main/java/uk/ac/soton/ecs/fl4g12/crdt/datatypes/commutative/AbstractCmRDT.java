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

import uk.ac.soton.ecs.fl4g12.crdt.delivery.AbstractVersionedUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryUpdateException;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.ReliableDeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.UpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.order.Version;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Abstract base class for {@linkplain CmRDT}s. Provides the semantics to ensure exactly-once
 * delivery of {@link VersionedUpdateMessage}s.
 *
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of timestamps which are used by each node.
 * @param <M> the type of updates which this object can be updated by.
 */
public abstract class AbstractCmRDT<K, T extends Comparable<T>, M extends VersionedUpdateMessage<K, ?>>
    extends
    AbstractVersionedUpdatable<K, T, M, ReliableDeliveryChannel<K, M>, AbstractCmRDT<K, T, M>>
    implements CmRDT<K, M> {

  public AbstractCmRDT(VersionVector<K, T> initialVersion, K identifier,
      ReliableDeliveryChannel<K, M> deliveryChannel) {
    super(initialVersion, identifier, deliveryChannel);
  }

  /**
   * Determine if the {@linkplain VersionVector} of this {@linkplain VersionedUpdatable} precedes
   * the {@linkplain Version} in the {@linkplain VersionedUpdateMessage}.
   *
   * @param message the {@linkplain VersionedUpdateMessage} to check.
   * @return {@code true} if the {@link VersionVector} of this {@linkplain VersionedUpdatable}
   *         precedes the version in the {@link VersionedUpdateMessage}, {@code false} otherwise.
   */
  protected abstract boolean precedes(M message);

  /**
   * Determine if the provided {@linkplain VersionedUpdateMessage} has already been applied.
   * Typically this would mean that the {@linkplain Version} in the
   * {@linkplain VersionedUpdateMessage} happened-before or is identical to the
   * {@linkplain VersionVector} of this {@linkplain VersionedUpdatable}
   *
   * @param message the {@linkplain VersionedUpdateMessage} to check.
   * @return {@code true} if the {@linkplain VersionedUpdateMessage} has already been applied,
   *         {@code false} otherwise.
   */
  protected abstract boolean hasBeenApplied(M message);

  /**
   * Synchronise the {@link VersionVector} of this {@linkplain VersionedUpdatable} with the
   * {@linkplain Version} in the {@linkplain VersionedUpdateMessage}.
   *
   * @param message the {@linkplain VersionedUpdateMessage} to check.
   */
  protected abstract void sync(M message);

  @Override
  public synchronized final void update(M message) throws DeliveryUpdateException {
    // Is this the next message?
    // Checking this first saves checking happenedBefore and identical for most messages
    // Precedence implies that the message has not happened before and is not identical.
    // a.precedes(b) -> a.happenedBefore(b) -> !b.happenedBefore(a) && !b.identical(a)
    if (!precedes(message)) {
      // Has the message already been applied?
      if (hasBeenApplied(message)) {
        // Nothing to do, update message is in the past.
        return;
      }
      // The message is being delivered too early - wait for another message.
      throw new DeliveryUpdateException(this, message, "Out of order delivery");
    }

    // Apply the update
    effectUpdate(message);
    sync(message);
  }

  /**
   * Apply the update contained in the message where all preconditions relating to the order of
   * message delivery have already been checked. The version will be synchronised automatically
   * after this method returns as long as no exceptions are thrown.
   *
   * On success (no {@link Throwable} being thrown) the local {@link VersionVector} of this
   * {@link VersionedUpdatable} will be synchronised with the {@link UpdateMessage}'s
   * {@link Version}. If this fails then the state of the object should be the same as before the
   * update was applied.
   *
   * @param message the {@link UpdateMessage} to apply.
   */
  protected abstract void effectUpdate(M message);

}
