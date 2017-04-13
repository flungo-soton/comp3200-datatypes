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
   * method waits on the channel while it {@link DeliveryChannel#hasPendingDeliveries()} and will
   * return once all pending deliveries are sent. If new messages are
   * {@link DeliveryChannel#publish(UpdateMessage)}ed while waiting, these will block this method
   * from returning however, there is no guarantee that (only that all messages that existed when
   * making the call have now been delivered and that at some time there were no more messages to be
   * delivered). This method may block for a long time if the {@link Updatable} is actively
   * producing update messages.
   *
   * @param channel the {@link DeliveryChannel} to wait for.
   * @see DeliveryChannel#hasPendingDeliveries() for the conditions which this method waits for.
   */
  public static void waitForDelivery(DeliveryChannel channel) {
    synchronized (channel) {
      while (channel.hasPendingDeliveries()) {
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
   * Wait until there are no messages waiting to be delivered via the {@link DeliveryChannel} to the
   * node with the given identifier. This method waits on the channel while it
   * {@link DeliveryChannel#hasPendingDeliveries(Object)} and will return once all pending
   * deliveries are sent. If new messages are {@link DeliveryChannel#publish(UpdateMessage)}ed while
   * waiting, these will block this method from returning however, there is no guarantee that (only
   * that all messages that existed when making the call have now been delivered and that at some
   * time there were no more messages to be delivered). This method may block for a long time if the
   * {@link Updatable} is actively producing update messages.
   *
   * @param <K> The type of the identifier that is assigned to the {@link Updatable}.
   * @param channel the {@link DeliveryChannel} to wait for.
   * @param identifier the identifier of the destination.
   */
  public static <K> void waitForDelivery(DeliveryChannel<K, ?> channel, K identifier) {
    synchronized (channel) {
      while (channel.hasPendingDeliveries(identifier)) {
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
   * Wait for all pending updates to be applied. This method waits on the {@link DeliveryChannel}
   * while it {@link DeliveryChannel#hasPendingMessages()} that have not been applied to the
   * {@link Updatable} which the {@link DeliveryChannel} works for.
   *
   * @param channel the {@link DeliveryChannel} to wait for.
   */
  public static void waitForUpdates(DeliveryChannel channel) {
    synchronized (channel) {
      while (channel.hasPendingMessages()) {
        try {
          channel.wait();
        } catch (InterruptedException ex) {
          LOGGER.log(Level.WARNING,
              "Wait was interrupted: will check again if wait needs to continue.", ex);
        }
      }
    }
  }

}
