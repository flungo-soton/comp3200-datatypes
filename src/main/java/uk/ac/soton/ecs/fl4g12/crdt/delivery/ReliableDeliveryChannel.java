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

package uk.ac.soton.ecs.fl4g12.crdt.delivery;

import uk.ac.soton.ecs.fl4g12.crdt.order.Version;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Interface for a {@link DeliveryChannel} which provide reliable delivery of messages. Reliable
 * delivery ensures at-least-once delivery, that is that every {@link VersionedUpdateMessage} which
 * is published reaches every node of the network at least once.
 *
 * The order in which messages are delivered is determined by the natural ordering of the messages.
 * For the semantics of the {@linkplain ReliableDeliveryChannel} to hold, the messages must be
 * ordered by their {@link Version} hence why {@link VersionedUpdateMessage}s are required. If
 * causal ordering of messages is required, then messages should be versioned with a version that
 * represents the causality of the message (such as a {@link VersionVector}).
 *
 * The {@link Version} of a {@link VersionedUpdateMessage} should be unique for the update that is
 * being delivered and when creating a new message it should always be preceded by the previous
 * message produced by that source: that is the {@link Version} of the pervious
 * {@link VersionedUpdateMessage} should {@link Version#precedes(Version)} the {@link Version} of
 * the new {@link VersionedUpdateMessage}.
 *
 * If the effect of a {@link VersionedUpdateMessage} on a {@link Updatable} is not idempotent, then
 * it will be required that the {@link Updatable} ensures the message has not already been applied
 * in a thread safe way. This can be achieved by checking that the {@link Version} of the message
 * did not {@link Version#happenedBefore(Version)} and is not {@link Version#identical(Version)} to
 * the {@link Version} of the {@link Updatable} while synchronized to ensure that no concurrent
 * updates are applied.
 *
 * If the delivery of a message fails for any reason, delivery should be re-attempted until
 * successful. To handle node failure, any cached messages should be persisted such that delivery
 * can be gracefully recovered.
 *
 * @param <K> The type of the identifier that is assigned to the {@link Updatable}.
 * @param <M> The type of updates sent via the delivery channel.
 */
public interface ReliableDeliveryChannel<K, M extends VersionedUpdateMessage<K, ?>>
    extends DeliveryChannel<K, M> {

}
