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

/**
 * A channel that handles delivering {@linkplain UpdateMessage}s to an {@linkplain Updatable}
 * object. Only one object should be associated with a given {@linkplain DeliveryChannel} which is
 * registered at instantiation of the {@link Updatable} object.
 *
 * @param <K> The type of the identifier that is assigned to the {@link Updatable}.
 * @param <U> The type of updates sent via the delivery channel.
 */
public interface DeliveryChannel<K, U extends UpdateMessage<K, ?>> {

  /**
   * Register the {@link Updatable} to deliver messages to as part of the channel. Only one object
   * can be registered per {@link DeliveryChannel} and calling when already registered will cause an
   * {@link IllegalStateException}. Typically an {@link Updatable} object is provided a delivery
   * channel when its instantiated and that object will then register itself using this method.
   *
   * When registering, if the {@link Updatable} is new ({@link Updatable#getIdentifier()} is
   * {@code null}) and has not been seen before by other nodes on the delivery channel, appropriate
   * actions should be taken to ensure that the
   *
   * @param updatable the {@link Updatable} to register with this channel.
   * @return the id of the object that has been assigned.
   * @throws IllegalStateException if the channel has already been registered to another object.
   */
  K register(Updatable<K, U> updatable) throws IllegalStateException;

  /**
   * Publish messages via this {@linkplain DeliveryChannel}. The messages will be delivered reliably
   * in natural order to all other nodes. This method should be shortlived and prepare the message
   * for delivery asynchronously so that the caller does not block other operations.
   *
   * If this method throws any {@link Throwable} then the behaviour of the instance calling is
   * undefined. If unable to deliver immediately, for any reason, the message should be reliably
   * cached until it can be delivered.
   *
   * After a successful delivery, {@link Object#notifyAll()} should be called.
   *
   * @param message the messages to send via the {@linkplain DeliveryChannel}.
   */
  void publish(U message); // TODO: Move this to ReliableDeliveryChannel

  /**
   * Determine if there are any messages waiting to be delivered. If the
   * {@linkplain DeliveryChannel} knows of any messages which have not been acknowledged then this
   * method should return {@code true}.
   *
   * @return {@code true} if there are any messages which have not been successfully delivered to
   *         its destination, {@code false} otherwise.
   */
  boolean hasPendingDeliveries();

  /**
   * Determine if there are any messages waiting to be delivered to node identified by the provided
   * identifier. If the {@linkplain DeliveryChannel} knows of any messages which have not been
   * acknowledged then this method should return {@code true}.
   *
   *
   * @param id the identifier of the message destination.
   * @return {@code true} if there are any messages which have not been successfully delivered to
   *         its destination, {@code false} otherwise.
   */
  boolean hasPendingDeliveries(K id);

  /**
   * Determine if there are any messages not yet applied to the {@linkplain Updatable} which this
   * {@linkplain DeliveryChannel} acts for.
   *
   * @return {@code true} if there are any messages which have not been successfully applied to the
   *         {@link Updatable} which this channel acts for, {@code false} otherwise.
   */
  boolean hasPendingMessages();

}
