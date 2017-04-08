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

package uk.ac.soton.ecs.fl4g12.crdt.delivery.local;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.CausalDeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.Updatable;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.UpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.idenitifier.IdentifierFactory;
import uk.ac.soton.ecs.fl4g12.crdt.order.Version;

/**
 * A delivery channel which delivers messages between local instances. This is not useful in
 * production and is primarily designed for testing. Delivery will be performed between
 * {@link Updatable} instances which are instantiated with this {@link DeliveryChannel}.
 *
 * @param <K> The type of the identifier that is assigned to the {@link Updatable}.
 * @param <U> The type of updates sent via the delivery channel.
 */
public class LocalDeliveryChannel<K, V extends Version, U extends VersionedUpdateMessage<K, V>>
    implements CausalDeliveryChannel<K, V, U> {

  private static final Logger LOGGER = Logger.getLogger(LocalDeliveryChannel.class.getName());

  private final IdentifierFactory<K> idFactory;
  private final Map<K, Updatable<K, U>> objects = new HashMap<>();
  private final Map<K, PriorityQueue<U>> queues = new HashMap<>();

  /**
   * Instantiate a {@linkplain LocalDeliveryChannel} with the given {@linkplain IdentifierFactory}.
   *
   * @param idFactory the {@link IdentifierFactory} to use to assign IDs when new {@link Updatable}s
   *        are registered.
   */
  public LocalDeliveryChannel(IdentifierFactory<K> idFactory) {
    this.idFactory = idFactory;
  }

  @Override
  public synchronized K register(Updatable<K, U> updatable) throws IllegalStateException {
    K identifier = updatable.getIdentifier();
    if (identifier == null) {
      do {
        identifier = idFactory.create();
      } while (objects.containsKey(identifier));
    } else if (objects.containsKey(identifier)) {
      throw new IllegalStateException(
          "An updatable with that ID is already registered:" + updatable.getIdentifier());
    }
    objects.put(identifier, updatable);
    queues.put(identifier, new PriorityQueue<U>());
    return identifier;
  }

  @Override
  public void publish(U message) {
    // Put the message onto the queues
    for (Updatable<K, U> updatable : objects.values()) {
      PriorityQueue<U> queue = queues.get(updatable.getIdentifier());
      if (!message.getIdentifier().equals(updatable.getIdentifier())) {
        synchronized (queue) {
          queue.add(message);
        }
      }
    }
    // Schedule delivery
    deliverUpdates(); // TODO: Async schedule
  }

  /**
   * Deliver queued {@linkplain UpdateMessage}s to their respective {@linkplain Updatable}.
   */
  public void deliverUpdates() {
    for (Updatable<K, U> updatable : objects.values()) {
      PriorityQueue<U> queue = queues.get(updatable.getIdentifier());
      U message;

      // Synchronize delivery on the queue to allow pipelining.
      synchronized (queue) {
        while ((message = queue.poll()) != null) {
          try {
            updatable.update(message);
          } catch (Throwable ex) {
            LOGGER.log(Level.SEVERE,
                "Throwable caught while trying to deliver message to " + updatable, ex);
            LOGGER.log(Level.INFO, "Requeuing message for deliver later: {0}", message);
            queue.add(message);
            LOGGER.log(Level.INFO,
                "No further messages will be delivered to {0} in this delivery cycle", updatable);
            // TODO: Schedule redelivery attempt.
            break;
          }
        }
      }
    }
  }

}
