/*
 * The MIT License
 *
 * Copyright 2016 Fabrizio Lungo <fl4g12@ecs.soton.ac.uk>.
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

import uk.ac.soton.ecs.fl4g12.crdt.datatypes.CRDT;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.ReliableDeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.UpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.order.Version;

/**
 * Interface for Commutative Replicated Data Types. Commutative Replicated Data Types are data types
 * which produce and consume operations which are shared to all other replicas for them to be able
 * to apply the changes. The commutativity of these operations can then determine the constraints of
 * the {@link DeliveryChannel} used to communicate messages.
 *
 * The minimum requirement for the {@link DeliveryChannel} and delivery semantics is that there is
 * at-least-once delivery. If the operations are not idempotent then exactly-once-delivery is
 * required. If the operations are only guaranteed to commute under concurrent operations (as
 * opposed to for all operations) then causally ordered delivery of operations is required.
 *
 * A {@link ReliableDeliveryChannel} provides the guarantees that are required and supports causal
 * ordering where the natural ordering of messages is causal (their {@link Version}s represent the
 * causality at the time of the operation being performed). Exactly-once-delivery can be achieved in
 * with a simple check on the {@link Version} of the {@link UpdateMessage} in the
 * {@link #update(UpdateMessage)} method. Both of these semantics can be implemented by the
 * {@linkplain CmRDT} and associated {@link UpdateMessage} if required without any alteration to the
 * {@link ReliableDeliveryChannel}.
 *
 * @param <K> the type of identifier used to identify nodes.
 * @param <M> the type of updates which this object can be updated by.
 */
public interface CmRDT<K, M extends VersionedUpdateMessage<K, ?>> extends CRDT<K, M> {

}
