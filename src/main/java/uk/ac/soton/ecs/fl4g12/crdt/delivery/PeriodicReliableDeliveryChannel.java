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

import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A {@link StateDeliveryChannel} which updates state between local instances periodically. Uses a
 * periodic runnable which applies any pending updates from other replicas. Messages provided to the
 * {@link #publish(VersionedUpdateMessage)} are immediately published to the
 * {@link DeliveryExchange}.
 *
 * @param <K> The type of the identifier that is assigned to the {@link Updatable}.
 * @param <M> The type of updates sent via the delivery channel.
 */
public final class PeriodicReliableDeliveryChannel<K, M extends VersionedUpdateMessage<K, ?>>
    extends AbstractDeliveryChannel<K, M, VersionedUpdatable<K, ?, M>>
    implements ReliableDeliveryChannel<K, M> {

  private static final Logger LOGGER =
      Logger.getLogger(PeriodicReliableDeliveryChannel.class.getName());

  private final long period;
  private final TimeUnit unit;

  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
  private final AtomicInteger publishCounter = new AtomicInteger();

  public PeriodicReliableDeliveryChannel(DeliveryExchange<K, M> exchange, long period,
      TimeUnit unit) {
    super(exchange, new PriorityBlockingQueue<M>());
    this.period = period;
    this.unit = unit;
  }

  @Override
  protected void postRegistration(K identifier) {
    // Start the delivery thread.
    executor.scheduleAtFixedRate(new ReliableDeliveryRunnable(), 0, period, unit);
  }

  @Override
  public boolean hasPendingDeliveries() {
    return publishCounter.get() > 0;
  }

  @Override
  public void publish(M message) {
    if (!isOpen()) {
      throw new IllegalStateException("Channel has been closed, not accepting new messages.");
    }
    // Increment the counter
    publishCounter.incrementAndGet();
    try {
      // Send the message to the exchange
      exchange.publish(message);
    } finally {
      // Make sure that the counter is always decremented
      publishCounter.decrementAndGet();
      // Notify anyone waiting for the value to change
      synchronized (publishCounter) {
        publishCounter.notifyAll();
      }
    }
  }

  public synchronized void doUpdates() {
    VersionedUpdatable<K, ?, M> updatable = getUpdatable();
    M message;
    while ((message = inbox.poll()) != null) {
      try {
        // TODO: Check precedence before attempting delivery
        updatable.update(message);
      } catch (Throwable ex) {
        LOGGER.log(Level.SEVERE, "Throwable caught while trying to deliver message to " + updatable,
            ex);
        // Put the message back
        LOGGER.log(Level.INFO, "Requeuing message for delivery later: {0}", message);
        inbox.add(message);
        // Cannot continue delivery
        // TODO: Try to apply concurrent messages from the queue?
        // TODO: Reschedule sooner if next delivery is a long time away.
        break;
      }
    }

    // Notify any threads waiting for the next update cycle to complete.
    notifyAll();
  }

  @Override
  protected void shutdown() throws Exception {
    // Shutdown the executor
    executor.shutdown();

    // If there are messagse try to apply them
    if (hasPendingUpdates()) {
      doUpdates();
    }
  }

  private class ReliableDeliveryRunnable implements Runnable {
    @Override
    public void run() {
      try {
        doUpdates();
      } catch (Throwable t) {
        LOGGER.log(Level.SEVERE, "Throwable while applying updates in ReliableDeliveryRunnable", t);
      }
    }
  }

}
