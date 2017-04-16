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
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import static uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannelAbstractTest.MESSAGES;
import uk.ac.soton.ecs.fl4g12.crdt.order.Version;

/**
 *
 */
public abstract class ReliableDeliveryChannelAbstractTest<K, M extends VersionedUpdateMessage<K, ?>, C extends ReliableDeliveryChannel<K, M>>
    extends DeliveryChannelAbstractTest<K, M, VersionedUpdatable<K, ?, M>, C> {

  private static final Logger LOGGER =
      Logger.getLogger(ReliableDeliveryChannelAbstractTest.class.getName());

  @Override
  public VersionedUpdatable<K, ?, M> getUpdatable() {
    return Mockito.mock(VersionedUpdatable.class);
  }

  @Override
  public M getUpdateMessage(K identifier, int order) {
    return getUpdateMessage(identifier, getVersion(order));
  }

  /**
   * Create a {@link VersionedUpdateMessage} with the given identifier and {@link Version}.
   *
   * @param identifier the identifier to use in the {@link VersionedUpdateMessage}.
   * @param version the {@link Version} to use in the {@link VersionedUpdateMessage}.
   * @return a {@link VersionedUpdateMessage} of type {@code M} using the provided parameters.
   * @see BasicVersionedUpdateMessage for a simple implementation that can be used.
   */
  public abstract M getUpdateMessage(K identifier, Version version);

  @Override
  @Deprecated
  public M getUpdateMessage() {
    throw new UnsupportedOperationException("Deprecated: see getUpdateMessage(K, Version)");
  }

  /**
   * Get a version which has logical ordering based on the provided order.
   *
   * @param order the ordering of the version.
   * @return a version with relative ordering based on the provided order.
   */
  public abstract Version getVersion(int order);

  // TODO: testPublish, testPublish_WrongIdentifier, etc.

  /**
   * Test receive applies in the correct order when given in the correct order.
   *
   * @throws Exception if the test fails, unless expected.
   */
  @Test
  public void testReceive_Multiple_InOrder() throws Exception {
    LOGGER.log(Level.INFO, "testReceive_Multiple_InOrder: "
        + "Test receive applies in the correct order when given in the correct order");
    try (C channel = getDeliveryChannel()) {
      // Synchroizing avoid race condition.
      // Assumes that the delivery is synchronized on the object.
      // No delivery can take place until the lock is release by waiting on the channel.
      synchronized (channel) {
        VersionedUpdatable<K, ?, M> updatable = getUpdatable(channel, getIdentifier(0));
        channel.register(updatable);

        // Receive updates
        List<M> messages = new ArrayList<>();
        for (int i = 0; i < MESSAGES; i++) {
          M message = getUpdateMessage(getIdentifier(1), i);
          channel.receive(message);
          messages.add(message);
        }
        triggerUpdates(channel);

        // Wait for the update to be applied
        DeliveryUtils.waitForUpdates(channel);

        // Verify that the correct messages were applied in order
        InOrder inOrder = Mockito.inOrder(updatable);
        for (M message : messages) {
          inOrder.verify(updatable).update(message);
        }
      }
    }
  }

  /**
   * Test receive applies in the correct order when given in the correct order.
   *
   * @throws Exception if the test fails, unless expected.
   */
  @Test
  public void testReceive_Multiple_OutOfOrder() throws Exception {
    LOGGER.log(Level.INFO, "testReceive_Multiple_InOrder: "
        + "Test receive applies in the correct order when given in the correct order");
    try (C channel = getDeliveryChannel()) {
      // Synchroizing avoid race condition.
      // Assumes that the delivery is synchronized on the object.
      // No delivery can take place until the lock is release by waiting on the channel.
      synchronized (channel) {
        VersionedUpdatable<K, ?, M> updatable = getUpdatable(channel, getIdentifier(0));
        channel.register(updatable);

        // Create updates
        List<M> messages = new ArrayList<>();
        for (int i = 0; i < MESSAGES; i++) {
          M message = getUpdateMessage(getIdentifier(1), i);
          messages.add(message);
        }
        // Shuffle and receive
        List<M> shuffled = new ArrayList<>(messages);
        Collections.shuffle(shuffled);
        for (M message : shuffled) {
          channel.receive(message);
        }
        triggerUpdates(channel);

        // Wait for the update to be applied
        DeliveryUtils.waitForUpdates(channel);

        // Verify that the correct messages were applied in order
        InOrder inOrder = Mockito.inOrder(updatable);
        for (M message : messages) {
          inOrder.verify(updatable).update(message);
        }
      }
    }
  }

  public static class BasicVersionedUpdateMessage<K, V extends Version<?, ? super V, V>>
      extends AbstractVersionedUpdateMessage<K, V> {

    public BasicVersionedUpdateMessage(K identifier, V version) {
      super(identifier, version);
    }

  }

}
