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

package uk.ac.soton.ecs.fl4g12.crdt.delivery;

/**
 * Exchanges wrap the functionality of the underlying communication layer allowing for messages to
 * be delivered. When a {@link DeliveryChannel} needs to communicate, it uses the exchange to do so.
 * The same {@linkplain DeliveryExchange} should only be used with {@link DeliveryChannel}s which
 * are for replicas of the same object, if that's supported, otherwise it is one
 * {@link DeliveryExchange} per {@link DeliveryChannel}.
 *
 * All identifiers registered to an exchange must be unique so that the destinations of messages can
 * be identified. To ensure global uniqueness, a {@linkplain DeliveryExchange} is able to assign an
 * identifier to the {@link DeliveryChannel} if one is not already assigned.
 *
 * {@link Object#notifyAll()} is called whenever a batch of messages are delivered to
 * {@link DeliveryChannel}s associated with this {@link DeliveryExchange} allowing other instances
 * to {@link Object#wait()} for the next delivery cycle to complete.
 *
 * @param <K> The type of the identifier that is used to identify {@link DeliveryChannel}s.
 * @param <M> The type of {@link UpdateMessage} sent via the {@link DeliveryChannel}s.
 * @see DeliveryUtils for details on how {@link #hasPendingDeliveries()} should be implemented in
 *      conjunction with the use of {@link Object#notify()}.
 */
public interface DeliveryExchange<K, M extends UpdateMessage<K, ?>> extends AutoCloseable {

  /**
   * Register a {@link DeliveryChannel} to deliver messages to as part of the exchange. Typically a
   * {@link DeliveryChannel} object is provided a {@link DeliveryExchange} when its instantiated and
   * that object will then register itself using this method.
   *
   * When registering, if the {@link DeliveryChannel} is new (when registering
   * {@link DeliveryChannel#getIdentifier()} is {@code null}) and has not been seen before by other
   * nodes on the delivery channel, appropriate actions should be taken to ensure that the state is
   * updated such that the {@link DeliveryChannel} is able to accept any new messages.
   *
   * @param channel the {@link DeliveryChannel} to register with this exchange.
   * @return the identifier of the object that has been assigned.
   * @throws IllegalStateException if the {@link DeliveryExchange} has been shut down.
   * @throws IllegalArgumentException if the {@link DeliveryChannel} has already been registered to
   *         another {@link DeliveryExchange}.
   */
  K register(DeliveryChannel<K, M, ?> channel)
      throws IllegalStateException, IllegalArgumentException;

  /**
   * Publish messages via this {@linkplain DeliveryExchange}. The messages will be delivered
   * reliably although no ordering guarantees are provided. This method should be shortlived and
   * prepare the message for delivery asynchronously so that the caller does not block other
   * operations, but should ensure that the message has been reliably persisted.
   *
   * This will publish a message to all other replicas which are connected to the same
   * {@linkplain DeliveryExchange} at the time of calling. The message is only considered to be
   * successful, once all of these destinations receive and apply the message.
   *
   * If this method throws any {@link Throwable} then the behaviour of the instance calling is
   * undefined. If unable to deliver immediately, for any reason, the message delivery should be
   * re-attempted until successful.
   *
   * After a successful delivery, {@link Object#notifyAll()} should be called.
   *
   * @param message the {@link UpdateMessage} to send via the {@linkplain DeliveryChannel}.
   */
  void publish(M message); // TODO: Move this to ReliableDeliveryChannel

  /**
   * Determine if there are any messages waiting to be delivered. If the
   * {@linkplain DeliveryExchange} knows of any messages which have not been acknowledged by the
   * recipient then this method should return {@code true}.
   *
   * Before this state changes to {@code false}, from {@code true}, the {@link DeliveryExchange} or
   * {@link DeliveryChannel} which messages have been passed to, should have changed the return of
   * {@link DeliveryExchange#hasPendingDeliveries()} or {@link DeliveryChannel#hasPendingUpdates()}
   * to {@code true} as a result of the messages being delivered so that a consecutive call to those
   * methods on the recipient, will only return {@code false} if those messages have been
   * delivered/applied.
   *
   * @return {@code true} if there are any messages which have not been successfully delivered to
   *         its destination, {@code false} otherwise.
   * @see DeliveryUtils#waitForDelivery(DeliveryExchange) for details on how this is used to wait
   *      for messages to be sent.
   * @see DeliveryUtils#waitForDelivery(DeliveryChannel, DeliveryChannel...) for details on how
   *      end-to-end delivery is waited upon.
   */
  boolean hasPendingDeliveries();

}
