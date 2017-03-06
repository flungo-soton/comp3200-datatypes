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

/**
 * Abstract {@linkplain Updatable} providing initialisation with a {@link DeliveryChannel}.
 * Registers the {@link Updatable} with a {@link DeliveryChannel}.
 *
 * @param <K> the type of identifier used to identify nodes.
 * @param <U> the type of {@link UpdateMessage} sent via the {@link DeliveryChannel}.
 */
public abstract class AbstractUpdatable<K, U extends UpdateMessage<K, ?>>
    implements Updatable<K, U> {

  private final K identifier;
  private final DeliveryChannel<K, U> deliveryChannel;

  /**
   * Instantiate the {@linkplain AbstractUpdatable} with the provided {@linkplain DeliveryChannel}.
   * Registers this {@link Updatable} with the provided {@link DeliveryChannel} and initialises the
   * local identifier for this {@link Updatable}.
   *
   * @param identifier the identifier for this instance or null if a new ID should be assigned by
   *        the {@link DeliveryChannel}.
   * @param deliveryChannel the {@link DeliveryChannel} to register with.
   */
  public AbstractUpdatable(K identifier, DeliveryChannel<K, U> deliveryChannel) {
    this.deliveryChannel = deliveryChannel;
    if (identifier == null) {
      this.identifier = deliveryChannel.register(this);
    } else {
      this.identifier = identifier;
      deliveryChannel.register(this);
    }
  }

  @Override
  public final K getIdentifier() {
    return identifier;
  }

  public final DeliveryChannel<K, U> getDeliveryChannel() {
    return deliveryChannel;
  }

}
