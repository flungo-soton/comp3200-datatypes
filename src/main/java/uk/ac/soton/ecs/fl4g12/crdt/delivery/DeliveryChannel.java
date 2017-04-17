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

package uk.ac.soton.ecs.fl4g12.crdt.delivery;

import uk.ac.soton.ecs.fl4g12.crdt.idenitifier.Identifiable;

/**
 * A channel that handles delivering {@linkplain UpdateMessage}s to an {@linkplain Updatable} object
 * as well as receiving notifications and messages from the {@link Updatable}. Only one
 * {@link Updatable} should be associated with a given {@linkplain DeliveryChannel} which is
 * registered at instantiation of the object.
 *
 * The application of updates to the {@link Updatable} should be done within a synchronized block so
 * that race conditions are avoidable by synchronizing on the {@link DeliveryChannel}.
 *
 * @param <K> The type of the identifier that is assigned to the {@link Updatable}.
 * @param <M> The type of {@link UpdateMessage} sent via this {@link DeliveryChannel}.
 * @param <U> The type of the {@link Updatable}.
 * @see DeliveryUtils for details on how {@link #hasPendingDeliveries()} and
 *      {@link #hasPendingUpdates()} should be implemented in conjunction with the use of
 *      {@link Object#notify()}.
 */
public interface DeliveryChannel<K, M extends UpdateMessage<K, ?>, U extends Updatable<K, M>>
    extends Identifiable<K>, AutoCloseable {

  /**
   * Get the {@linkplain DeliveryExchange} being used by this {@linkplain DeliveryChannel}.
   *
   * @return the {@linkplain DeliveryExchange} being used by this {@linkplain DeliveryChannel}.
   */
  DeliveryExchange<K, M> getExchange();


  /**
   * Get the {@linkplain Updatable} registered with this {@linkplain DeliveryChannel}.
   *
   * @return the {@linkplain Updatable} registered with this {@linkplain DeliveryChannel}.
   */
  U getUpdatable();

  /**
   * Register the {@link Updatable} to deliver messages to as part of the channel. Only one object
   * can be registered per {@link DeliveryChannel} and calling when already registered will cause an
   * {@link IllegalStateException}. Typically an {@link Updatable} object is provided a
   * {@link DeliveryChannel} when its instantiated and that object will then register itself using
   * this method.
   *
   * When registering, if the {@link Updatable} is new (when registering
   * {@link Updatable#getIdentifier()} is {@code null}) and has not been seen before by other nodes
   * on the delivery channel, appropriate actions should be taken to ensure that the state is
   * updated such that the {@link Updatable} is able to accept any new messages.
   *
   * @param updatable the {@link Updatable} to register with this channel.
   * @return the identifier of the object that has been assigned.
   * @throws IllegalStateException if the channel has already been registered to another
   *         {@link Updatable}.
   * @throws IllegalArgumentException if the {@link Updatable} is already registered to another
   *         {@link DeliveryChannel}.
   */
  K register(U updatable) throws IllegalStateException, IllegalArgumentException;

  /**
   * Receive an {@link UpdateMessage} to be applied to the {@link Updatable}. This is used by
   * {@link DeliveryExchange}s to provide new messages that have been published by the
   * {@link DeliveryChannel} of other replicas.
   *
   * @param message the message to be applied to the {@link Updatable}.
   */
  void receive(M message);

  /**
   * Determine if there are any messages waiting to be delivered. If the
   * {@linkplain DeliveryChannel} knows of any messages which have not been reliably transfered to
   * the {@link DeliveryExchange} then this method will return {@code true}.
   *
   * Before this state changes to {@code false}, from {@code true}, the {@link DeliveryExchange}
   * which messages have been passed to, should have changed the return of
   * {@link DeliveryExchange#hasPendingDeliveries()} to {@code true} as a result of the messages
   * being delivered so that a consecutive call to {@link DeliveryExchange#hasPendingDeliveries()}
   * on the {@link DeliveryExchange} which the messages were delivered to, will only return
   * {@code false} if those messages have been delivered to their destination.
   *
   * @return {@code true} if there are any messages which have not been successfully delivered to
   *         the {@link DeliveryExchange}, {@code false} otherwise.
   * @see DeliveryUtils#waitForDelivery(DeliveryChannel) for details on how this is used to wait for
   *      messages to be sent.
   * @see DeliveryUtils#waitForDelivery(DeliveryChannel, DeliveryChannel...) for details on how
   *      end-to-end delivery is waited upon.
   */
  boolean hasPendingDeliveries();

  /**
   * Determine if there are any messages not yet applied to the {@linkplain Updatable} which this
   * {@linkplain DeliveryChannel} acts for.
   *
   * @return {@code true} if there are any messages which have not been successfully applied to the
   *         {@link Updatable} which this channel acts for, {@code false} otherwise.
   */
  boolean hasPendingUpdates();

}
