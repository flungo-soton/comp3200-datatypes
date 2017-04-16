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

import java.util.Queue;

/**
 * Abstract base class for {@link DeliveryChannel} implementations.
 *
 * @param <K> The type of the identifier that is assigned to the {@link Updatable}.
 * @param <M> The type of {@link UpdateMessage} sent via this {@link DeliveryChannel}.
 * @param <U> The type of the {@link Updatable}.
 */
public abstract class AbstractDeliveryChannel<K, M extends UpdateMessage<K, ?>, U extends Updatable<K, M>>
    implements DeliveryChannel<K, M, U> {

  protected final DeliveryExchange<K, M> exchange;
  protected final Queue<M> inbox;
  private U updatable;

  /**
   * State of the {@link DeliveryChannel}. If {@code false} then new messages will be rejected.
   * Should never be set to {@code true}, should only be set to {@code false} by {@link #close()}.
   */
  private volatile boolean open = true;

  /**
   * Instantiate a {@linkplain AbstractDeliveryChannel} using the provided
   * {@link LocalDeliveryExchange} and inbox.
   *
   * By providing the queue used for the inbox, the implementation can determine the order of
   * messages that come from the queue. The queue provided should be thread safe to allow concurrent
   * delivery of messages from the {@link DeliveryExchange} while applying updates to the
   * {@link Updatable}.
   *
   * @param exchange the {@link LocalDeliveryExchange} that this {@link AbstractDeliveryChannel}
   *        uses.
   * @param inbox the {@link Queue} to use as the inbox for deliveries received from the
   *        {@link DeliveryExchange}.
   */
  public AbstractDeliveryChannel(DeliveryExchange<K, M> exchange, Queue<M> inbox) {
    this.exchange = exchange;
    this.inbox = inbox;
  }

  @Override
  public final synchronized K register(U updatable) throws IllegalStateException {
    // Is the channel open?
    if (!open) {
      throw new IllegalStateException("Channel has been closed, not accepting registrations.");
    }
    // One updatable per channel
    if (this.updatable != null) {
      throw new IllegalStateException("Channel has already been registered with an Updatable");
    }
    // One channel per updatable
    if (updatable.getDeliveryChannel() != this) {
      throw new IllegalArgumentException(
          "The Updatable provided is must be set to use this DeliveryChannel");
    }

    // Store the updatable
    this.updatable = updatable;
    // Register with the exchange
    K identifier = exchange.register(this);

    // Post-registration hook
    postRegistration(identifier);

    // Return the assigned identifier
    return identifier;
  }

  /**
   * Hook for additional tasks to be performed after registration has been completed. The identifier
   * will not yet have been initialised by the {@link Updatable} and so
   * {@link Updatable#getIdentifier()} should not be used: the valid identifier is provided.
   *
   * @param identifier the identifier that will be assigned to the {@link Updatable}.
   */
  protected void postRegistration(K identifier) {}

  @Override
  public final DeliveryExchange<K, M> getExchange() {
    return exchange;
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
    if (updatable == null) {
      throw new IllegalStateException("Channel has not been registered with an Updatable yet");
    }
    inbox.add(message);
  }

  @Override
  public final boolean hasPendingUpdates() {
    return !inbox.isEmpty();
  }

  /**
   * Determine if this {@link DeliveryChannel} is accepting new messages. After the
   * {@link DeliveryChannel} has been closed, this will return {@code false}.
   *
   * @return {@code true} if the {@link DeliveryChannel} has not been closed, and is still accepting
   *         messages, {@code false} otherwise.
   */
  public boolean isOpen() {
    return open;
  }

  @Override
  public final void close() throws Exception {
    // Set the state to closed
    open = false;

    // Shutdown the channel.
    shutdown();
  }

  /**
   * Perform the shutdown of the {@link DeliveryChannel}. When called, the state will have been set
   * to closed and no new messages will be accepted. This method should ensure that all messages
   * currently held are delivered, any persistence that is required is performed and any background
   * threads and pools are shut down. It is possible that this method is called when the
   * {@link DeliveryChannel} has already been shut down.
   *
   * @throws Exception if shutdown could not be completed successfully.
   */
  protected abstract void shutdown() throws Exception;

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{"
        + (updatable == null ? "unregistered" : updatable.getIdentifier()) + "} on " + exchange;
  }

}
