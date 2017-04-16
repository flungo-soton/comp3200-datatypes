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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.mockito.Mockito;
import uk.ac.soton.ecs.fl4g12.crdt.order.Version;
import uk.ac.soton.ecs.fl4g12.crdt.util.TestUtil;

/**
 * Abstract tests for {@linkplain DeliveryExchange} implementations.
 *
 * @param <K> The type of the identifier used by {@link DeliveryChannel}s.
 * @param <M> The type of {@link UpdateMessage} delivered via the {@link DeliveryExchange}.
 * @param <X> The type of the {@link DeliveryExchange} being tested.
 */
public abstract class DeliveryExchangeAbstractTest<K, M extends VersionedUpdateMessage<K, ?>, X extends DeliveryExchange<K, M>> {

  private static final Logger LOGGER =
      Logger.getLogger(DeliveryExchangeAbstractTest.class.getName());

  public static final long BUFFER_TIME = 2000;

  public static final int MAX_CHANNELS = 10;
  public static final int MESSAGES = 100;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Rule
  public Timeout timeout = TestUtil.getTimeout(20 * MAX_CHANNELS * MESSAGES, TimeUnit.MILLISECONDS);

  /**
   * Gets an identifier. This should be a bijection such that for any unique value of {@code i}
   * given, a unique identifier is returned.
   *
   * @param i the unique specifier of which identifier to return.
   * @return the unique identifier mapped to input {@code i}.
   */
  public abstract K getIdentifier(int i);

  /**
   * Get a {@link DeliveryExchange} instance to test.
   *
   * @return a {@link DeliveryExchange} for testing.
   */
  public abstract X getDeliveryExchange();

  public M getUpdateMessage(K identifier, int order) {
    M message = getUpdateMessage();
    Mockito.doReturn(identifier).when(message).getIdentifier();
    Mockito.doReturn(getVersion(order)).when(message).getVersion();
    return message;
  }

  /**
   * Get a mockable {@linkplain UpdateMessage}.
   *
   * @return a mockable {@link UpdateMessage}.
   */
  public abstract M getUpdateMessage();

  /**
   * Get a version which has logical ordering based on the provided order.
   *
   * @param order the ordering of the version.
   * @return a version with relative ordering based on the provided order.
   */
  public abstract Version getVersion(int order);

  /**
   * Get a {@linkplain DeliveryChannel} which can be registered with the
   * {@linkplain DeliveryExchange}. The {@link DeliveryChannel} should not be registered the the
   * exchange but should be an object on which Mockito verifications can be performed. A Mock Object
   * is sufficient providing that the {@link DeliveryChannel} returns any values required by the
   * {@link DeliveryExchange}. IT is expected that during the registration,
   * {@link DeliveryChannel#getExchange()} and {@link DeliveryChannel#getIdentifier()} will be
   * called and these should return the values given in the function arguments.
   *
   * @param exchange the exchange to return when {@link DeliveryChannel#getExchange()} is called.
   * @param identifier the identifier to return when {@link DeliveryChannel#getIdentifier()} is
   *        called.
   * @return a {@link DeliveryChannel} which can have Mockito verifications made against it.
   */
  public DeliveryChannel<K, M, ?> getDeliveryChannel(X exchange, K identifier) {
    DeliveryChannel<K, M, ?> channel = Mockito.mock(DeliveryChannel.class);
    Mockito.doReturn(exchange).when(channel).getExchange();
    Mockito.doReturn(identifier).when(channel).getIdentifier();
    return channel;
  }

  /**
   * Trigger the delivery of messages using the exchange if required. If the
   * {@link DeliveryExchange} does not deliver automatically, this method is called once all
   * messages have been published and the {@link DeliveryExchange} needs to perform delivery.
   *
   * @param exchange the exchange to trigger delivery on.
   */
  public void triggerDelivery(X exchange) {}

  /**
   * Test registering a channel that does not have an ID
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testRegister_NoID() throws Exception {
    LOGGER.log(Level.INFO,
        "testRegister_NoID: Test registering an channel that does not have an ID");

    // Get the DeliveryExchange
    try (X exchange = getDeliveryExchange()) {
      Set<K> seenIdentifiers = new HashSet<>();
      for (int i = 0; i < MAX_CHANNELS; i++) {
        // Get the DeliveryChannel
        DeliveryChannel<K, M, ?> channel = getDeliveryChannel(exchange, null);

        // Register the channel
        K id = exchange.register(channel);

        // Make assertions
        assertNotNull(id);
        Mockito.verify(channel).getExchange();
        Mockito.verify(channel).getIdentifier();
        Mockito.verifyNoMoreInteractions(channel);
        assertFalse("ID should be unique", seenIdentifiers.contains(id));

        seenIdentifiers.add(id);
      }
    }
  }

  /**
   * Test registering a channel that already has an ID.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testRegister_WithID() throws Exception {
    LOGGER.log(Level.INFO, "testRegister_WithID: Test registering channel that already has an ID");

    // Get the DeliveryExchange
    try (X exchange = getDeliveryExchange()) {
      for (int i = 0; i < MAX_CHANNELS; i++) {
        K expectedId = getIdentifier(i);

        // Get the DeliveryChannel
        DeliveryChannel<K, M, ?> channel = getDeliveryChannel(exchange, expectedId);

        // Register the channel
        K id = exchange.register(channel);

        // Make assertions
        assertEquals("The ID returned after registration should be the original ID", expectedId,
            id);
        Mockito.verify(channel).getExchange();
        Mockito.verify(channel).getIdentifier();
        Mockito.verifyNoMoreInteractions(channel);
      }
    }
  }

  /**
   * Test registering two updatables with the same ID.
   *
   * @throws Exception if the test fails, unless expected.
   */
  @Test
  public void testRegister_DuplicateID() throws Exception {
    LOGGER.log(Level.INFO,
        "testRegister_DuplicateID: Test registering two chnnels with the same ID");

    // Get the DeliveryExchange
    try (X exchange = getDeliveryExchange()) {
      // Get the DeliveryChannels
      DeliveryChannel<K, M, ?> channel1 = getDeliveryChannel(exchange, getIdentifier(0));
      DeliveryChannel<K, M, ?> channel2 = getDeliveryChannel(exchange, getIdentifier(0));

      // Register the channel
      exchange.register(channel1);

      // Expect exception
      thrown.expect(IllegalArgumentException.class);
      exchange.register(channel2);
    }
  }

  // TODO: testRegister_WrongExchange, testRegister_NullExchange

  /**
   * Test registering the same channel twice.
   *
   * @throws Exception if the test fails, unless expected.
   */
  @Test
  public void testRegister_Twice() throws Exception {
    LOGGER.log(Level.INFO, "testRegister_Twice: Test registering the same channel twice");

    // Get the DeliveryExchange
    try (X exchange = getDeliveryExchange()) {
      // Get the DeliveryChannel
      DeliveryChannel<K, M, ?> channel = getDeliveryChannel(exchange, getIdentifier(0));

      // Register the channel
      exchange.register(channel);

      // Expect exception
      thrown.expect(IllegalArgumentException.class);
      exchange.register(channel);
    }
  }

  /**
   * Test registering an updatable on a closed exchange.
   *
   * @throws Exception if the test fails, unless expected.
   */
  @Test
  public void testRegister_Closed() throws Exception {
    LOGGER.log(Level.INFO,
        "testRegister_Closed: Test registering an updatable on a closed exchange");

    // Get the DeliveryExchange and close it
    X exchange = getDeliveryExchange();
    exchange.close();

    // Get the DeliveryChannel
    DeliveryChannel<K, M, ?> channel = getDeliveryChannel(exchange, getIdentifier(0));

    // Expect exception
    thrown.expect(IllegalStateException.class);
    exchange.register(channel);
  }

  /**
   * Test publishing a single message from one channel with no recipients.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testPublish_OneChannel_SingleMessage() throws Exception {
    LOGGER.log(Level.INFO, "testPublish_TwoChannels_SingleMessage: "
        + "Test publishing a single message from one channel with no recipients");

    // Get the DeliveryExchange
    try (X exchange = getDeliveryExchange()) {
      // Register a source and distination channel
      DeliveryChannel<K, M, ?> source = getDeliveryChannel(exchange, getIdentifier(0));
      exchange.register(source);

      // Create and send the message
      M message = getUpdateMessage(source.getIdentifier(), 1);
      exchange.publish(message);
      triggerDelivery(exchange);

      // Wait for the exchange to deliver
      DeliveryUtils.waitForDelivery(exchange);

      // Verify that the correct messages were delivered.
      Mockito.verify(source, Mockito.times(0)).receive((M) Mockito.any());

      // Wait a little longer and make sure nothing else happened
      Thread.sleep(BUFFER_TIME);
      Mockito.verify(source, Mockito.times(0)).receive((M) Mockito.any());
    }
  }

  /**
   * Test publishing a multiple messages from one channel with no recipients.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testPublish_OneChannel_MultipleMessages() throws Exception {
    LOGGER.log(Level.INFO, "testPublish_OneChannel_MultipleMessages: "
        + "Test publishing a multiple messages from one channel with no recipients");

    // Get the DeliveryExchange
    try (X exchange = getDeliveryExchange()) {
      // Register a source and distination channel
      DeliveryChannel<K, M, ?> source = getDeliveryChannel(exchange, getIdentifier(0));
      exchange.register(source);

      // Create and send the messages
      for (int i = 0; i < MESSAGES; i++) {
        M message = getUpdateMessage(source.getIdentifier(), i);
        exchange.publish(message);
      }
      triggerDelivery(exchange);

      // Wait for the exchange to deliver
      DeliveryUtils.waitForDelivery(exchange);

      // Verify that the correct messages were delivered.
      Mockito.verify(source, Mockito.times(0)).receive((M) Mockito.any());

      // Wait a little longer and make sure nothing else happened
      Thread.sleep(BUFFER_TIME);
      Mockito.verify(source, Mockito.times(0)).receive((M) Mockito.any());
    }
  }

  /**
   * Test publishing a single message from one channel to another.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testPublish_TwoChannels_SingleMessage() throws Exception {
    LOGGER.log(Level.INFO, "testPublish_TwoChannels_SingleMessage: "
        + "Test publishing a single message from one channel to another");

    // Get the DeliveryExchange
    try (X exchange = getDeliveryExchange()) {
      // Register a source and distination channel
      DeliveryChannel<K, M, ?> source = getDeliveryChannel(exchange, getIdentifier(0));
      exchange.register(source);
      DeliveryChannel<K, M, ?> destination = getDeliveryChannel(exchange, getIdentifier(1));
      exchange.register(destination);

      // Create and send the message
      M message = getUpdateMessage(source.getIdentifier(), 1);
      exchange.publish(message);
      triggerDelivery(exchange);

      // Wait for the exchange to deliver
      DeliveryUtils.waitForDelivery(exchange);

      // Verify that the correct messages were delivered.
      Mockito.verify(destination).receive((M) Mockito.any());
      Mockito.verify(destination).receive(message);
      Mockito.verify(source, Mockito.times(0)).receive((M) Mockito.any());

      // Wait a little longer and make sure nothing else happened
      Thread.sleep(BUFFER_TIME);
      Mockito.verify(destination).receive((M) Mockito.any());
      Mockito.verify(source, Mockito.times(0)).receive((M) Mockito.any());
    }
  }

  /**
   * Test publishing a multiple messages from one channel to another.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testPublish_TwoChannels_MultipleMessages() throws Exception {
    LOGGER.log(Level.INFO, "testPublish_TwoChannels_MultipleMessages: "
        + "Test publishing a multiple messages from one channel to another");

    // Get the DeliveryExchange
    try (X exchange = getDeliveryExchange()) {
      // Register a source and distination channel
      DeliveryChannel<K, M, ?> source = getDeliveryChannel(exchange, getIdentifier(0));
      exchange.register(source);
      DeliveryChannel<K, M, ?> destination = getDeliveryChannel(exchange, getIdentifier(1));
      exchange.register(destination);

      // Create and send the messages
      Set<M> messages = new HashSet<>();
      for (int i = 0; i < MESSAGES; i++) {
        M message = getUpdateMessage(source.getIdentifier(), i);
        exchange.publish(message);
        messages.add(message);
      }
      triggerDelivery(exchange);

      // Wait for the exchange to deliver
      DeliveryUtils.waitForDelivery(exchange);

      // Verify that the correct messages were delivered.
      Mockito.verify(destination, Mockito.times(MESSAGES)).receive((M) Mockito.any());
      for (M message : messages) {
        Mockito.verify(destination).receive(message);
      }
      Mockito.verify(source, Mockito.times(0)).receive((M) Mockito.any());

      // Wait a little longer and make sure nothing else happened
      Thread.sleep(BUFFER_TIME);
      Mockito.verify(destination, Mockito.times(MESSAGES)).receive((M) Mockito.any());
      Mockito.verify(source, Mockito.times(0)).receive((M) Mockito.any());
    }
  }

  /**
   * Test publishing a single message to multiple destinations.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testPublish_MultipleChannels_SingleMessage() throws Exception {
    LOGGER.log(Level.INFO, "testPublish_MultipleChannels_SingleMessage: "
        + "Test publishing a single message to multiple destinations");

    // Get the DeliveryExchange
    try (X exchange = getDeliveryExchange()) {
      // Register a source
      DeliveryChannel<K, M, ?> source = getDeliveryChannel(exchange, getIdentifier(0));
      exchange.register(source);

      // Register the destinations
      Set<DeliveryChannel<K, M, ?>> destinations = new HashSet<>();
      for (int i = 1; i < MAX_CHANNELS; i++) {
        DeliveryChannel<K, M, ?> destination = getDeliveryChannel(exchange, getIdentifier(i));
        exchange.register(destination);
      }

      // Create and send the message
      M message = getUpdateMessage(source.getIdentifier(), 1);
      exchange.publish(message);
      triggerDelivery(exchange);

      // Wait for the exchange to deliver
      DeliveryUtils.waitForDelivery(exchange);

      // Verify that the correct messages were delivered.
      for (DeliveryChannel<K, M, ?> destination : destinations) {
        Mockito.verify(destination).receive((M) Mockito.any());
        Mockito.verify(destination).receive(message);
        Mockito.verify(source, Mockito.times(0)).receive((M) Mockito.any());
      }

      // Wait a little longer and make sure nothing else happened
      Thread.sleep(BUFFER_TIME);
      for (DeliveryChannel<K, M, ?> destination : destinations) {
        Mockito.verify(destination).receive((M) Mockito.any());
        Mockito.verify(source, Mockito.times(0)).receive((M) Mockito.any());
      }
    }
  }

  /**
   * Test publishing multiple messages to multiple destinations.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testPublish_MultipleChannels_MultipleMessages() throws Exception {
    LOGGER.log(Level.INFO, "testPublish_MultipleChannels_MultipleMessages: "
        + "Test publishing a multiple messages to multiple destinations");

    // Get the DeliveryExchange
    try (X exchange = getDeliveryExchange()) {
      // Register a source
      DeliveryChannel<K, M, ?> source = getDeliveryChannel(exchange, getIdentifier(0));
      exchange.register(source);

      // Register the destinations
      Set<DeliveryChannel<K, M, ?>> destinations = new HashSet<>();
      for (int i = 1; i < MAX_CHANNELS; i++) {
        DeliveryChannel<K, M, ?> destination = getDeliveryChannel(exchange, getIdentifier(i));
        exchange.register(destination);
      }

      // Create and send the message
      Set<M> messages = new HashSet<>();
      for (int i = 0; i < MESSAGES; i++) {
        M message = getUpdateMessage(source.getIdentifier(), i);
        exchange.publish(message);
        messages.add(message);
      }
      triggerDelivery(exchange);

      // Wait for the exchange to deliver
      DeliveryUtils.waitForDelivery(exchange);

      // Verify that the correct messages were delivered.
      for (DeliveryChannel<K, M, ?> destination : destinations) {
        Mockito.verify(destination, Mockito.times(MESSAGES)).receive((M) Mockito.any());
        for (M message : messages) {
          Mockito.verify(destination).receive(message);
        }
        Mockito.verify(source, Mockito.times(0)).receive((M) Mockito.any());
      }

      // Wait a little longer and make sure nothing else happened
      Thread.sleep(BUFFER_TIME);
      for (DeliveryChannel<K, M, ?> destination : destinations) {
        Mockito.verify(destination, Mockito.times(MESSAGES)).receive((M) Mockito.any());
        Mockito.verify(source, Mockito.times(0)).receive((M) Mockito.any());
      }
    }
  }

  /**
   * Test multiple channels publishing a single message each.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testPublish_MultipleChannels_SingleMessageEach() throws Exception {
    LOGGER.log(Level.INFO, "testPublish_MultipleChannels_SingleMessageEach: "
        + "Test multiple channels publishing a single message each");

    // Get the DeliveryExchange
    try (X exchange = getDeliveryExchange()) {
      // Register the destinations
      Set<DeliveryChannel<K, M, ?>> channels = new HashSet<>();
      for (int i = 0; i < MAX_CHANNELS; i++) {
        DeliveryChannel<K, M, ?> destination = getDeliveryChannel(exchange, getIdentifier(i));
        exchange.register(destination);
      }

      // Create and send the message
      Map<K, M> messages = new HashMap<>();
      for (DeliveryChannel<K, M, ?> channel : channels) {
        M message = getUpdateMessage(channel.getIdentifier(), 1);
        exchange.publish(message);
        messages.put(channel.getIdentifier(), message);
      }
      triggerDelivery(exchange);

      // Wait for the exchange to deliver
      DeliveryUtils.waitForDelivery(exchange);

      // Verify that the correct messages were delivered.
      for (DeliveryChannel<K, M, ?> destination : channels) {
        Mockito.verify(destination, Mockito.times(MAX_CHANNELS - 1)).receive((M) Mockito.any());
        for (DeliveryChannel<K, M, ?> source : channels) {
          // Should not be received at source but should be received once elsewhere
          Mockito.verify(source, Mockito.times(source == destination ? 0 : 1))
              .receive(messages.get(source.getIdentifier()));
        }
      }

      // Wait a little longer and make sure nothing else happened
      Thread.sleep(BUFFER_TIME);
      for (DeliveryChannel<K, M, ?> destination : channels) {
        Mockito.verify(destination, Mockito.times(MAX_CHANNELS - 1)).receive((M) Mockito.any());
      }
    }
  }

  /**
   * Test multiple channels publishing multiple message each.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testPublish_MultipleChannels_MultipleMessageEach() throws Exception {
    LOGGER.log(Level.INFO, "testPublish_MultipleChannels_MultipleMessageEach: "
        + "Test multiple channels publishing multiple message each");

    // Get the DeliveryExchange
    try (X exchange = getDeliveryExchange()) {
      // Register the destinations
      Set<DeliveryChannel<K, M, ?>> channels = new HashSet<>();
      for (int i = 0; i < MAX_CHANNELS; i++) {
        DeliveryChannel<K, M, ?> destination = getDeliveryChannel(exchange, getIdentifier(i));
        exchange.register(destination);
      }

      // Create and send the message
      Map<K, Set<M>> messages = new HashMap<>();
      for (DeliveryChannel<K, M, ?> channel : channels) {
        Set<M> channelMessages = new HashSet<>();
        for (int i = 0; i < MESSAGES; i++) {
          M message = getUpdateMessage(channel.getIdentifier(), i);
          exchange.publish(message);
          channelMessages.add(message);
        }
        messages.put(channel.getIdentifier(), channelMessages);
      }
      triggerDelivery(exchange);

      // Wait for the exchange to deliver
      DeliveryUtils.waitForDelivery(exchange);

      // Verify that the correct messages were delivered.
      for (DeliveryChannel<K, M, ?> destination : channels) {
        Mockito.verify(destination, Mockito.times((MAX_CHANNELS - 1) * MESSAGES))
            .receive((M) Mockito.any());
        for (DeliveryChannel<K, M, ?> source : channels) {
          for (M message : messages.get(source.getIdentifier())) {
            // Should not be received at source but should be received once elsewhere
            Mockito.verify(source, Mockito.times(source == destination ? 0 : 1)).receive(message);
          }
        }
      }

      // Wait a little longer and make sure nothing else happened
      Thread.sleep(BUFFER_TIME);
      for (DeliveryChannel<K, M, ?> destination : channels) {
        Mockito.verify(destination, Mockito.times((MAX_CHANNELS - 1) * MESSAGES))
            .receive((M) Mockito.any());
      }
    }
  }

  /**
   * Test publishing to a closed exchange.
   *
   * @throws Exception if the test fails, unless expected
   */
  @Test
  @SuppressWarnings("null")
  public void testPublish_Closed() throws Exception {
    LOGGER.log(Level.INFO, "testPublish_Closed: Test publishing to a closed exchange");

    // Get the DeliveryExchange and close it
    X exchange = null;
    DeliveryChannel<K, M, ?> channel;
    try {
      exchange = getDeliveryExchange();

      // Get the DeliveryChannel and register it
      channel = getDeliveryChannel(exchange, getIdentifier(0));
      exchange.register(channel);
    } finally {
      if (exchange != null) {
        exchange.close();
      }
    }

    // Create the message
    M message = getUpdateMessage(channel.getIdentifier(), 1);

    // Expect exception
    thrown.expect(IllegalStateException.class);
    exchange.publish(message);
  }

  /**
   * Test registering the same updatable twice.
   *
   * @throws Exception if the test fails, unless expected.
   */
  @Test
  public void testPublish_Unregistered() throws Exception {
    LOGGER.log(Level.INFO, "testRegister_Twice: Test registering the same updatable twice");

    // Get the DeliveryExchange
    try (X exchange = getDeliveryExchange()) {
      // Create the message
      M message = getUpdateMessage(getIdentifier(0), 1);

      // Expect exception
      thrown.expect(IllegalArgumentException.class);
      exchange.publish(message);
    }
  }

  // TODO: Test other methods (hasPendingMessages)

  // TODO: Test that channels are closed with exchnage.

}
