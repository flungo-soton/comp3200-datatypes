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
 * @param <M> the type of {@link UpdateMessage} sent via the {@link DeliveryChannel}.
 * @param <D> the type of {@link DeliveryChannel} this {@link Updatable} can use.
 * @param <U> the type of this {@link Updatable}.
 */
public abstract class AbstractUpdatable<K, M extends UpdateMessage<K, ?>, D extends DeliveryChannel<K, M, ? super U>, U extends AbstractUpdatable<K, M, D, U>>
    implements Updatable<K, M> {

  protected final K identifier;
  protected final D deliveryChannel;

  /**
   * Instantiate the {@linkplain AbstractUpdatable} with the provided {@linkplain DeliveryChannel}.
   * Registers this {@link Updatable} with the provided {@link DeliveryChannel} and initialises the
   * local identifier for this {@link Updatable}.
   *
   * @param identifier the identifier for this instance or null if a new ID should be assigned by
   *        the {@link DeliveryChannel}.
   * @param deliveryChannel the {@link DeliveryChannel} to register with.
   */
  public AbstractUpdatable(K identifier, D deliveryChannel) {
    this.deliveryChannel = deliveryChannel;
    if (identifier == null) {
      this.identifier = deliveryChannel.register((U) this);
    } else {
      this.identifier = identifier;
      deliveryChannel.register((U) this);
    }
  }

  @Override
  public final K getIdentifier() {
    return identifier;
  }

  /**
   * Get the {@linkplain DeliveryChannel} which this {@linkplain Updatable} communicates
   * {@linkplain UpdateMessage}s.
   *
   * @return the {@linkplain DeliveryChannel} of this {@linkplain Updatable}.
   */
  public final D getDeliveryChannel() {
    return deliveryChannel;
  }

}
