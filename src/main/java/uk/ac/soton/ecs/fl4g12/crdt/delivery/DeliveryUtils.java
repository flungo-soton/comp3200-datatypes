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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public final class DeliveryUtils {

  private static final Logger LOGGER = Logger.getLogger(DeliveryUtils.class.getName());

  // Util class cannot be constructed.
  private DeliveryUtils() {}

  /**
   * Wait until there are no messages waiting to be delivered via the {@link DeliveryChannel}. This
   * method periodically checks {@link DeliveryChannel#hasPendingDeliveries()} and will return once
   * all pending deliveries are sent. If new messages are published while waiting, these will block
   * this method from returning however, there is no guarantee that concurrent published will be
   * waited upon (only that all messages that existed when making the call have now been delivered
   * and that at some time there were no more messages to be delivered). This method may block for a
   * long time if the {@link Updatable} is actively producing update messages.
   *
   * If the {@link DeliveryChannel} is implemented correctly and is using a
   * {@link DeliveryExchange}, then at the time this method returns,
   * {@link DeliveryExchange#hasPendingDeliveries()} will have changed to {@code true} such that a
   * consecutive call to {@link #waitForDelivery(DeliveryExchange)} would wait until the
   * {@link DeliveryExchange} has delivered all messages that have been given to it while this
   * method waited.
   *
   * @param channel the {@link DeliveryChannel} to wait for.
   * @see DeliveryChannel#hasPendingDeliveries() for the conditions which this method waits for.
   * @see #waitForDelivery(DeliveryChannel, DeliveryChannel...) to wait for end-to-end delivery.
   */
  public static void waitForDelivery(DeliveryChannel channel) {
    while (channel.hasPendingDeliveries()) {
      try {
        // TODO: Use a lock
        Thread.sleep(100);
      } catch (InterruptedException ex) {
        LOGGER.log(Level.WARNING,
            "Sleep was interrupted: will check again if wait needs to continue.", ex);
      }
    }
  }

  /**
   * Wait until the {@link DeliveryExchange} has delivered all its messages. This method waits on
   * the {@link DeliveryExchange} until {@link DeliveryExchange#hasPendingDeliveries()} returns
   * {@code false}. If new messages are published while waiting, these will block this method from
   * returning however, there is no guarantee that concurrent published will be waited upon (only
   * that all messages that existed when making the call have now been delivered and that at some
   * time there were no more messages to be delivered). This method may block for a long time if the
   * {@link DeliveryChannel}s are actively producing update messages.
   *
   * If the {@link DeliveryExchange} is implemented correctly, then at the time this method returns,
   * {@link DeliveryExchange#hasPendingDeliveries()} and {@link DeliveryChannel#hasPendingUpdates()}
   * for the {@link DeliveryExchange}s and {@link DeliveryChannel}s which messages have been
   * delivered to will have changed to {@code true} such that a consecutive call to
   * {@link #waitForDelivery(DeliveryExchange)} or {@link #waitForUpdates(DeliveryChannel)} would
   * wait until the {@link DeliveryExchange} or {@link DeliveryChannel} has delivered all messages
   * that have been given to it while this method waited.
   *
   * @param exchange the {@link DeliveryExchange} to wait for.
   * @see DeliveryExchange#hasPendingDeliveries() for the conditions which this method waits for.
   * @see #waitForDelivery(DeliveryChannel, DeliveryChannel...) to wait for end-to-end delivery.
   */
  public static void waitForDelivery(DeliveryExchange exchange) {
    synchronized (exchange) {
      while (exchange.hasPendingDeliveries()) {
        try {
          exchange.wait();
        } catch (InterruptedException ex) {
          LOGGER.log(Level.WARNING,
              "Wait was interrupted: will check again if wait needs to continue.", ex);
        }
      }
    }
  }

  /**
   * Wait for all pending updates held by the {@link DeliveryChannel} to be applied to the
   * {@link Updatable}. This method waits on the {@link DeliveryChannel} while it
   * {@link DeliveryChannel#hasPendingUpdates()} that have not been applied. If new messages are
   * published while waiting, these will block this method from returning however, there is no
   * guarantee that concurrent published will be waited upon (only that all messages that existed
   * when making the call have now been delivered and that at some time there were no more messages
   * to be delivered). This method may block for a long time if the {@link DeliveryChannel}s are
   * actively producing update messages.
   *
   * @param channel the {@link DeliveryChannel} to wait on.
   */
  public static void waitForUpdates(DeliveryChannel channel) {
    synchronized (channel) {
      while (channel.hasPendingUpdates()) {
        try {
          channel.wait();
        } catch (InterruptedException ex) {
          LOGGER.log(Level.WARNING,
              "Wait was interrupted: will check again if wait needs to continue.", ex);
        }
      }
    }
  }

  /**
   * Wait for end-to-end delivery between two {@linkplain DeliveryChannel}s. After messages have
   * been sent to a source {@link DeliveryChannel} this method can be used to wait until those
   * messages are applied to the destination. This utilises the
   * {@link #waitForDelivery(DeliveryChannel)}, {@link #waitForDelivery(DeliveryExchange)} and
   * {@link #waitForUpdates(DeliveryChannel) } and the expectation that the waitable periods of
   * those methods overlaps.
   *
   * @param source the {@link DeliveryChannel} which is the source of the messages which delivery is
   *        being waited upon.
   * @param destinations the {@link DeliveryChannel}s which are the destination of the messages
   *        which delivery is being waited upon.
   */
  public static void waitForDelivery(DeliveryChannel source, DeliveryChannel... destinations) {
    waitForDelivery(source);
    waitForDelivery(source.getExchange());
    // TODO: Multi-thread and use thread join
    for (DeliveryChannel destination : destinations) {
      // Wait for the destination exchange if it uses a different one from the source
      if (!source.getExchange().equals(destination.getExchange())) {
        waitForDelivery(destination.getExchange());
      }
      waitForUpdates(destination);
    }
  }

}
