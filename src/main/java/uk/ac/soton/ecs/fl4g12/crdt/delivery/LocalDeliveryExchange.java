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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.soton.ecs.fl4g12.crdt.idenitifier.IdentifierFactory;

/**
 * A {@linkplain DeliveryExchange} for transferring {@linkplain UpdateMessage}s between registered
 * {@link DeliveryChannel}s on the same machine. This is primarily built for testing, however, for
 * applications that have a lot of local concurrency, there can be a performance benefit with using
 * this.
 *
 * When a message is published, it will be delivered to all
 *
 * @param <K> The type of the identifier that is used to identify {@link DeliveryChannel}s.
 * @param <M> The type of {@link UpdateMessage} sent via the {@link DeliveryChannel}s.
 */
public class LocalDeliveryExchange<K, M extends VersionedUpdateMessage<K, ?>>
    implements DeliveryExchange<K, M> {

  private static final Logger LOGGER = Logger.getLogger(LocalDeliveryExchange.class.getName());
  /**
   * Map of {@link UpdateMessage} timestamps to a set of {@link DeliveryChannel}s yet to acknowledge
   * that message.
   */
  // TODO: Document that Versions should have only use timestamps which is reliably hashed.
  private final Map<M, HashSet<DeliveryChannel<K, M, ?>>> unacked = new ConcurrentHashMap<>();
  private final BlockingQueue<M> messages = new LinkedBlockingQueue<>();

  private final IdentifierFactory<K> idFactory;
  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  private final Map<K, DeliveryChannel<K, M, ?>> channels = new ConcurrentHashMap<>();

  private volatile boolean open = true;

  /**
   * Instantiate a {@linkplain LocalDeliveryExchange} with the given {@linkplain IdentifierFactory}.
   *
   * @param idFactory the {@link IdentifierFactory} to use to assign IDs when new
   *        {@link DeliveryChannel}s are registered.
   * @param period the period between successive delivery attempts.
   * @param unit the {@link TimeUnit} of the {@code period} parameter.
   */
  public LocalDeliveryExchange(IdentifierFactory<K> idFactory, long period, TimeUnit unit) {
    this.idFactory = idFactory;
    this.executor.scheduleAtFixedRate(new ExchangeDeliveryRunnable(), 0, period, unit);
  }

  @Override
  public synchronized K register(DeliveryChannel<K, M, ?> channel) throws IllegalStateException {
    // No new channels once the exchange is closed.
    if (!open) {
      throw new IllegalStateException(
          "Cannot publish: DeliveryExchange has already been shut down");
    }
    // Channel should be using this exchange
    if (channel.getExchange() != this) {
      throw new IllegalArgumentException(
          "The DeliveryChannel provided is must be set to use this DeliveryExchange");
    }

    // Do the registration
    K identifier = channel.getIdentifier();
    if (identifier == null) {
      do {
        identifier = idFactory.create();
      } while (channels.containsKey(identifier));
    } else if (channels.containsKey(identifier)) {
      throw new IllegalArgumentException(
          "An updatable with that ID is already registered:" + channel.getIdentifier());
    }
    channels.put(identifier, channel);
    return identifier;
  }

  @Override
  public void publish(M message) {
    // No new messages once the exchange is closed.
    if (!open) {
      throw new IllegalStateException(
          "Cannot publish: DeliveryExchange has already been shut down");
    }

    // Is the channel registered to this exchnage
    if (!channels.containsKey(message.getIdentifier())) {
      throw new IllegalArgumentException("The message source is not registered with this exchange");
    }

    // Create a set for unacked messages
    final HashSet<DeliveryChannel<K, M, ?>> unackedChannels;
    // Reserve the timestamp
    synchronized (unacked) {
      if (unacked.containsKey(message)) {
        throw new IllegalStateException("A message with that version has already been published");
      }
      unackedChannels = new HashSet<>(); // TODO: Concurrent?
      unacked.put(message, unackedChannels);
    }

    // Prepare unackedChannels
    for (DeliveryChannel<K, M, ?> channnel : channels.values()) {
      if (!message.getIdentifier().equals(channnel.getIdentifier())) {
        unackedChannels.add(channnel);
      }
    }

    // Add the message to the queue - ready to be delivered
    messages.add(message);
  }

  public synchronized void doDelivery() {
    // Create a set for the messages which were not sent
    // Avoids getting stuck in a busy loop for a message that can't be delivered.
    // LinkedHashSet maintains order while deduping if something weird happens.
    Collection<M> failed = new LinkedHashSet<>();
    try {
      // Go through the messages in the order they were received
      M message;
      while ((message = messages.poll()) != null) {
        Set<DeliveryChannel<K, M, ?>> destinations = unacked.get(message);

        synchronized (destinations) {
          Iterator<DeliveryChannel<K, M, ?>> destinationsIt = destinations.iterator();
          while (destinationsIt.hasNext()) {
            DeliveryChannel<K, M, ?> channel = destinationsIt.next();
            try {
              channel.receive(message);
              // Successfully received by channel, acknowledge by removing from set
              destinationsIt.remove();
            } catch (Throwable t) {
              LOGGER.log(Level.WARNING, "Delivery of message to " + channel + " failed", t);
            }
          }
          // If channels is empty, all messages delivered
          if (destinations.isEmpty()) {
            // TODO: send ACK to source DeliveryChannel
            unacked.remove(message);
          } else {
            // Message will need redelivering at next cycle
            failed.add(message);
          }
        }
      }
    } finally {
      // Put the failed messages back in the queue
      messages.addAll(failed);

      // Notify of delivery cycle
      notifyAll();
    }
  }

  @Override
  public boolean hasPendingDeliveries() {
    return !unacked.isEmpty();
  }

  @Override
  public synchronized void close() throws Exception {
    // Synchronization means that no new channels will be added before open is set to false.
    // Not setting open to false before shutting down channel so that channels can publish new
    // messages.
    // TODO: However, since the channel is shut down, we can't deliver the messages they give to any
    // other channels because they will be closed.

    // Shutdown channels
    for (DeliveryChannel<K, M, ?> channel : channels.values()) {
      try {
        channel.close();
      } catch (Exception ex) {
        LOGGER.log(Level.SEVERE, "Could not shutdown DeliveryChannel: " + channel, ex);
      }
    }

    // Stop accepting messages
    open = false;

    // TOOD: complete any existing then shutdown
    executor.shutdown();
  }

  private class ExchangeDeliveryRunnable implements Runnable {
    @Override
    public void run() {
      try {
        doDelivery();
      } catch (Throwable t) {
        LOGGER.log(Level.SEVERE, "Throwable while attempting delivery in ExchangeDeliveryRunnable",
            t);
      }
    }
  }

}
