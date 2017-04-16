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

import uk.ac.soton.ecs.fl4g12.crdt.idenitifier.IdentifierFactory;

/**
 * Abstract {@link DeliveryChannel} which does not replicate to any other nodes. All publish
 * messages are ignored. Useful for testing and as a placeholder while developing.
 *
 * @param <K> The type of the identifier that is assigned to the {@link Updatable}.
 * @param <M> The type of {@link UpdateMessage} sent via this {@link DeliveryChannel}.
 * @param <U> The type of the {@link Updatable}.
 */
public abstract class NullDeliveryChannel<K, M extends UpdateMessage<K, ?>, U extends Updatable<K, M>>
    implements DeliveryChannel<K, M, U> {

  private final IdentifierFactory<K> idFactory;
  private U updatable;

  /**
   * State of the {@link DeliveryChannel}. If {@code false} then new messages will be rejected.
   * Should never be set to {@code true}, should only be set to {@code false} by {@link #close()}.
   */
  protected volatile boolean open = true;

  /**
   * Create a {@linkplain NullDeliveryChannel}.
   *
   * @param idFactory {@link IdentifierFactory} used to assign an identifier if the
   *        {@link Updatable} doesn't have one when registering.
   */
  public NullDeliveryChannel(IdentifierFactory<K> idFactory) {
    this.idFactory = idFactory;
  }

  @Override
  public final synchronized K register(U updatable) throws IllegalStateException {
    // One updatable per channel
    if (this.updatable != null) {
      throw new IllegalStateException("Channel has already been registered with an Updatable");
    }
    // One channel per updatable
    if (updatable.getDeliveryChannel() != this) {
      throw new IllegalArgumentException(
          "The Updatable provided is must be set to use this DeliveryChannel");
    }

    // Register the updatable
    this.updatable = updatable;

    // Assign ID if required
    K identifier = updatable.getIdentifier();
    if (identifier == null) {
      identifier = idFactory.create();
    }
    return identifier;
  }


  @Override
  public final DeliveryExchange<K, M> getExchange() {
    // No exchnage associated
    return null;
  }

  @Override
  public final U getUpdatable() {
    if (updatable == null) {
      throw new IllegalStateException("Channel has not been registered with an Updatable yet");
    }
    return updatable;
  }

  @Override
  public final K getIdentifier() {
    return getUpdatable().getIdentifier();
  }

  @Override
  public final void receive(M message) {
    if (!open) {
      throw new IllegalStateException("Channel has been closed, not accepting new messages.");
    }
    try {
      // Pass directly to the updatable
      updatable.update(message);
    } catch (DeliveryUpdateException ex) {
      throw new RuntimeException("Could not deliver message to updatable", ex);
    }
  }

  @Override
  public final boolean hasPendingDeliveries() {
    // There are never pending deliveries
    return false;
  }

  @Override
  public final boolean hasPendingUpdates() {
    // There are never pending updates
    return false;
  }

  @Override
  public void close() throws Exception {
    open = false;
  }

}
