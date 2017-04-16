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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A {@link StateDeliveryChannel} which updates state between local instances periodically. Uses a
 * periodic runnable which applies any pending updates and creates a state snapshot (if there have
 * been any modifications since the last message published) which is published via a
 * {@link DeliveryExchange} to other replicas.
 *
 * Updates are applied in reverse causal order such that if an older message is fully encapsulated
 * within a newer one, that message is not applied (assuming that the data type performs the check).
 *
 * @param <K> The type of the identifier that is assigned to the {@link Updatable}.
 * @param <M> The type of updates sent via the delivery channel.
 */
public final class PeriodicStateDeiveryChannel<K, M extends StateSnapshot<K, ?>>
    extends AbstractDeliveryChannel<K, M, StatefulUpdatable<K, ?, M>>
    implements StateDeliveryChannel<K, M> {

  private static final Logger LOGGER =
      Logger.getLogger(PeriodicStateDeiveryChannel.class.getName());

  private static final int INITIAL_QUEUE_CAPACITY = 10;

  private final long period;
  private final TimeUnit unit;

  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
  private final AtomicInteger publishCounter = new AtomicInteger();

  /**
   * Create a {@linkplain PeriodicStateDeiveryChannel} which will create a snapshot of the state and
   * publish to other replicas via the {@link DeliveryExchange} at the period specified. The
   * background delivery thread will not be started until this {@link DeliveryChannel} has an
   * {@link Updatable} registered.
   *
   * @param exchange the {@link DeliveryExchange} which messages will be published to.
   * @param period the period at which to publish messages to the {@link DeliveryExchange}.
   * @param unit the {@link TimeUnit} of the {@code period} parameter.
   */
  public PeriodicStateDeiveryChannel(DeliveryExchange<K, M> exchange, long period, TimeUnit unit) {
    // Applying newest first should result in the greatest number of messages that require no work.
    super(exchange,
        new PriorityBlockingQueue<M>(INITIAL_QUEUE_CAPACITY, Collections.reverseOrder()));
    this.period = period;
    this.unit = unit;
  }

  @Override
  protected void postRegistration(K identifier) {
    // Start the delivery thread.
    executor.scheduleAtFixedRate(new StateDeliveryRunnable(), 0, period, unit);
  }

  @Override
  public boolean hasPendingDeliveries() {
    // If the publish counter is greater than 0, there are messages which have not yet been
    // delivered.
    return publishCounter.get() > 0;
  }

  @Override
  public void publish() {
    // Increment the publish counter.
    publishCounter.incrementAndGet();
  }

  public synchronized void doDelivery() {
    // Check if there are messages to be delivered.
    int publishes = publishCounter.get();
    if (publishes <= 0) {
      // No new messages
      return;
    }

    // Get a snapshot and publish it
    exchange.publish(getUpdatable().snapshot());

    // Remove the number of pubishes that there were when this method started
    // If there were any published while this method executed, this will be leave the publishCounter
    // in a positive state and publishing will be reattempted at the next cycle.
    publishCounter.addAndGet(-publishes);
  }

  public synchronized void doUpdates() {
    // Collection of failed messages which will be added back to the inbox afterwards.
    Collection<M> failed = new ArrayList<>();

    StatefulUpdatable<K, ?, M> updatable = getUpdatable();
    M message;
    while ((message = inbox.poll()) != null) {
      try {
        // TODO: Check precedence before attempting delivery
        updatable.update(message);
      } catch (Throwable ex) {
        LOGGER.log(Level.SEVERE, "Throwable caught while trying to deliver message to " + updatable,
            ex);
        LOGGER.log(Level.INFO, "Requeuing message for delivery later: {0}", message);
        failed.add(message);
      }
    }

    // Put the failed items back in the inbox for next delivery cycle.
    inbox.addAll(failed);

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
    // If there are pending deliverie send them out
    if (hasPendingDeliveries()) {
      doDelivery();
    }
  }

  private class StateDeliveryRunnable implements Runnable {
    @Override
    public void run() {
      // Apply updates first so they are included in the new state.
      try {
        doUpdates();
      } catch (Throwable t) {
        LOGGER.log(Level.SEVERE, "Throwable while applying updates in StateDeliveryRunnable", t);
      }
      try {
        doDelivery();
      } catch (Throwable t) {
        LOGGER.log(Level.SEVERE, "Throwable while attempting delivery in StateDeliveryRunnable", t);
      }
    }
  }

}
