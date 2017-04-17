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
 * An object that can be updated with a {@linkplain DeliveryChannel}. The object should take a
 * {@link DeliveryChannel} as part of its instantiation and call
 * {@link DeliveryChannel#register(Updatable)} with itself.
 *
 * {@linkplain Updatable} objects can be updated by an {@link UpdateMessage}. The type of the
 * {@link UpdateMessage} for the {@linkplain Updatable} can be specified with generics allowing for
 * the use of an {@link UpdateMessage} appropriate for the object.
 *
 * @param <K> the type of identifier used to identify nodes.
 * @param <M> the type of updates sent via the delivery channel.
 */
public interface Updatable<K, M extends UpdateMessage<K, ?>> {

  /**
   * Gets the identifier for this object. This is a globally unique identifier for the instance of
   * the updatable across the network.
   *
   * For new objects, this is provided by the {@link DeliveryChannel} when calling
   * {@link DeliveryChannel#register(Updatable)} and should return {@code null} until registered.
   *
   * @return the identifier of the {@linkplain Updatable} object or {@code null} if it has not been
   *         registered with its {@link DeliveryChannel} yet.
   */
  K getIdentifier();

  /**
   * Update the object with the provided message. The contents contained within the message will be
   * applied to the locally updatable object.
   *
   * @param message the message containing the update to be performed.
   * @throws DeliveryUpdateException if the update could not be applied.
   */
  void update(M message) throws DeliveryUpdateException;

  /**
   * Get the delivery channel that is used by this {@linkplain Updatable} to deliver updates to
   * replicas.
   *
   * @return the delivery channel that is used by this {@link Updatable} to deliver updates to
   *         replicas.
   */
  DeliveryChannel<K, M> getDeliveryChannel();
}
