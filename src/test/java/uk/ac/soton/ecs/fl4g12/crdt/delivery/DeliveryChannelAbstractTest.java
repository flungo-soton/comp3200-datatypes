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

package uk.ac.soton.ecs.fl4g12.crdt.delivery;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.mockito.Mockito;
import uk.ac.soton.ecs.fl4g12.crdt.util.TestUtil;

/**
 * Abstract tests for {@linkplain DeliveryChannel} implementations.
 *
 * @param <K> The type of the identifier that is assigned to the {@link Updatable}.
 * @param <M> The type of {@link UpdateMessage} sent via the {@link DeliveryChannel}.
 * @param <U> the type of {@link Updatable} which the {@link DeliveryChannel} delivers for.
 * @param <C> the type of the {@linkplain DeliveryChannel} to be tested.
 */
public abstract class DeliveryChannelAbstractTest<K, M extends UpdateMessage<K, ?>, U extends Updatable<K, M>, C extends DeliveryChannel<K, M, U>> {

  private static final Logger LOGGER =
      Logger.getLogger(DeliveryChannelAbstractTest.class.getName());

  public static final long BUFFER_TIME = 2000;

  public static final int MAX_CHANNELS = 10;
  public static final int MESSAGES = 100;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Rule
  public Timeout timeout = TestUtil.getTimeout(10, TimeUnit.SECONDS);

  /**
   * Gets an identifier. This should be a bijection such that for any unique value of {@code i}
   * given, a unique identifier is returned.
   *
   * @param i the unique specifier of which identifier to return.
   * @return the unique identifier mapped to input {@code i}.
   */
  public abstract K getIdentifier(int i);

  /**
   * Get a {@linkplain DeliveryChannel} which can be registered with the
   * {@linkplain DeliveryExchange}. The {@link DeliveryChannel} should not be registered the the
   * exchange but should be an object on which Mockito verifications can be performed. A Mock Object
   * is sufficient providing that the {@link DeliveryChannel} returns any values required by the
   * {@link DeliveryExchange}. IT is expected that during the registration,
   * {@link DeliveryChannel#getExchange()} and {@link DeliveryChannel#getIdentifier()} will be
   * called and these should return the values given in the function arguments.
   *
   * @param channel the exchange to return when {@link DeliveryChannel#getExchange()} is called.
   * @param identifier the identifier to return when {@link DeliveryChannel#getIdentifier()} is
   *        called.
   * @return a {@link DeliveryChannel} which can have Mockito verifications made against it.
   */
  public final U getUpdatable(C channel, K identifier) {
    U updatable = getUpdatable();
    Mockito.doReturn(channel).when(updatable).getDeliveryChannel();
    Mockito.doReturn(identifier).when(updatable).getIdentifier();
    return updatable;
  }

  /**
   * Get a {@linkplain DeliveryChannel} which can be registered with the
   * {@linkplain DeliveryExchange}. The {@link DeliveryChannel} should not be registered with the
   * exchange but should be an object on which Mockito verifications can be performed.
   *
   * @return a {@link DeliveryChannel} which can have Mockito verifications made against it.
   */
  public abstract U getUpdatable();

  public M getUpdateMessage(K identifier, int order) {
    M message = getUpdateMessage();
    Mockito.doReturn(identifier).when(message).getIdentifier();
    return message;
  }

  /**
   * Get a mockable {@linkplain UpdateMessage}.
   *
   * @return a mockable {@link UpdateMessage}.
   */
  public abstract M getUpdateMessage();

  /**
   * Get a {@linkplain DeliveryChannel} that can be used for testing. Returns a
   * {@link DeliveryChannel} with a mock {@link DeliveryExchange} (returned by
   * {@link DeliveryChannel#getExchange()}.
   *
   * @return the {@link DeliveryChannel} to be tested.
   */
  public abstract C getDeliveryChannel();

  /**
   * Trigger the application of messages to the updatable if required. If the
   * {@link DeliveryChannel} does not update automatically, this method is called once all messages
   * have been received and the {@link DeliveryChannel} needs to apply the updates.
   *
   * @param channel the {@link DeliveryChannel} to trigger delivery on.
   */
  public void triggerUpdates(C channel) {}

  /**
   * Test that registering an unidentified updatable delegates to the exchange.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testRegister_NoID() throws Exception {
    LOGGER.log(Level.INFO, "testRegister_NoID: "
        + "Test that registering an unidentified updatable delegates to the exchange");

    for (int i = 0; i < MAX_CHANNELS; i++) {
      try (C channel = getDeliveryChannel()) {
        U updatable = getUpdatable(channel, null);
        K expected = getIdentifier(i);
        Mockito.doReturn(expected).when(channel.getExchange()).register(channel);

        K identifier = channel.register(updatable);

        assertEquals(expected, identifier);
        Mockito.verify(channel.getExchange()).register(channel);
        Mockito.verifyNoMoreInteractions(channel.getExchange());
      }
    }
  }

  /**
   * Test that registering an identified updatable delegates to the exchange.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testRegister_WithID() throws Exception {
    LOGGER.log(Level.INFO, "testRegister_WithID: "
        + "Test that registering an identified updatable delegates to the exchange");

    for (int i = 0; i < MAX_CHANNELS; i++) {
      try (C channel = getDeliveryChannel()) {
        U updatable = getUpdatable(channel, getIdentifier(i));
        K expected = getIdentifier(MAX_CHANNELS + i);
        Mockito.doReturn(expected).when(channel.getExchange()).register(channel);

        K identifier = channel.register(updatable);

        assertEquals(expected, identifier);
        Mockito.verify(channel.getExchange()).register(channel);
        Mockito.verifyNoMoreInteractions(channel.getExchange());
      }
    }
  }

  /**
   * Test registering two updatables to the same channel.
   *
   * @throws Exception if the test fails, unless expected.
   */
  @Test
  public void testRegister_TwiceDifferent() throws Exception {
    LOGGER.log(Level.INFO,
        "testRegister_TwiceDifferent: Test registering two updatables with the same ID");

    try (C channel = getDeliveryChannel()) {
      U updatable1 = getUpdatable(channel, getIdentifier(0));
      U updatable2 = getUpdatable(channel, getIdentifier(1));

      channel.register(updatable1);

      // Expect exception
      thrown.expect(IllegalStateException.class);
      channel.register(updatable2);
    }
  }

  /**
   * Test registering the same updatable twice.
   *
   * @throws Exception if the test fails, unless expected.
   */
  @Test
  public void testRegister_TwiceSame() throws Exception {
    LOGGER.log(Level.INFO, "testRegister_Twice: Test registering the same updatable twice");
    try (C channel = getDeliveryChannel()) {
      U updatable = getUpdatable(channel, getIdentifier(0));
      channel.register(updatable);

      // Expect exception
      thrown.expect(IllegalStateException.class);
      channel.register(updatable);
    }
  }

  /**
   * Test registering an updatable with the wrong channel set.
   *
   * @throws Exception if the test fails, unless expected.
   */
  @Test
  public void testRegister_WrongChannel() throws Exception {
    LOGGER.log(Level.INFO,
        "testRegister_WrongChannel: Test registering an updatable with the wrong channel set");
    try (C channel = getDeliveryChannel()) {
      try (C other = getDeliveryChannel()) {
        U updatable = getUpdatable(other, getIdentifier(0));

        // Expect exception
        thrown.expect(IllegalArgumentException.class);
        channel.register(updatable);
      }
    }
  }

  /**
   * Test registering an updatable with no channel set.
   *
   * @throws Exception if the test fails, unless expected.
   */
  @Test
  public void testRegister_NoChannel() throws Exception {
    LOGGER.log(Level.INFO, "testRegister_Twice: Test registering an updatable with no channel set");
    try (C channel = getDeliveryChannel()) {
      K id = getIdentifier(0);

      U updatable = getUpdatable(null, id);

      // Expect exception
      thrown.expect(IllegalArgumentException.class);
      channel.register(updatable);
    }
  }

  /**
   * Test registering on a closed channel.
   *
   * @throws Exception if the test fails, unless expected.
   */
  @Test
  public void testRegister_Closed() throws Exception {
    LOGGER.log(Level.INFO, "testRegister_Closed: Test registering on a closed channel");

    // Get the DeliveryExchange and close it
    C channel = getDeliveryChannel();
    channel.close();

    // Get the DeliveryChannel
    U updatable = getUpdatable(channel, getIdentifier(0));

    // Expect exception
    thrown.expect(IllegalStateException.class);
    channel.register(updatable);
  }

  /**
   * Test getUpdatable on an unregistered channel.
   *
   * @throws Exception if the test fails, unless expected.
   */
  @Test
  public void testGetUpdatable_Unregistered() throws Exception {
    LOGGER.log(Level.INFO,
        "testGetUpdatable_Unregistered: Test getUpdatable on an unregistered channel");

    try (C channel = getDeliveryChannel()) {
      // Expect exception
      thrown.expect(IllegalStateException.class);
      channel.getUpdatable();
    }
  }

  /**
   * Test getUpdatable.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testGetUpdatable() throws Exception {
    LOGGER.log(Level.INFO, "testGetUpdatable_Unregistered: Test getUpdatable");

    try (C channel = getDeliveryChannel()) {
      U expected = getUpdatable(channel, getIdentifier(0));
      channel.register(expected);

      U result = channel.getUpdatable();
      assertSame(expected, result);
    }
  }



  /**
   * Test getIdentifier on an unregistered channel.
   *
   * @throws Exception if the test fails, unless expected.
   */
  @Test
  public void testGetIdentifier_Unregistered() throws Exception {
    LOGGER.log(Level.INFO,
        "testGetUpdatable_Unregistered: Test getIdentifier on an unregistered channel");

    try (C channel = getDeliveryChannel()) {
      // Expect exception
      thrown.expect(IllegalStateException.class);
      channel.getIdentifier();
    }
  }

  /**
   * Test getIdentifier.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testGetIdentifier() throws Exception {
    LOGGER.log(Level.INFO, "testGetUpdatable_Unregistered: Test getIdentifier");

    try (C channel = getDeliveryChannel()) {
      K id = getIdentifier(0);

      U updatable = getUpdatable(channel, id);
      channel.register(updatable);

      K result = channel.getIdentifier();
      assertSame(id, result);
      Mockito.verify(updatable).getIdentifier();
    }
  }

  /**
   * Test getUpdatable on an unregistered channel.
   *
   * @throws Exception if the test fails, unless expected.
   */
  @Test
  public void testReceive_Unregistered() throws Exception {
    LOGGER.log(Level.INFO,
        "testGetUpdatable_Unregistered: Test getUpdatable on an unregistered channel");

    try (C channel = getDeliveryChannel()) {
      K id = getIdentifier(0);

      // Expect exception
      thrown.expect(IllegalStateException.class);
      channel.receive(getUpdateMessage(id, 1));
    }
  }

  /**
   * Test registering the same updatable twice.
   *
   * @throws Exception if the test fails, unless expected.
   */
  @Test
  public void testReceive_Single() throws Exception {
    LOGGER.log(Level.INFO, "testReceive_Single: Test registering the same updatable twice");
    try (C channel = getDeliveryChannel()) {
      U updatable = getUpdatable(channel, getIdentifier(0));
      channel.register(updatable);

      // Receive an update
      M message = getUpdateMessage(getIdentifier(1), 1);
      channel.receive(message);
      triggerUpdates(channel);

      // Wait for the update to be applied
      DeliveryUtils.waitForUpdates(channel);

      // Make assertions
      Mockito.verify(updatable).update((M) Mockito.any());
      Mockito.verify(updatable).update(message);
    }
  }

  /**
   * Test registering the same updatable twice.
   *
   * @throws Exception if the test fails, unless expected.
   */
  @Test
  public void testReceive_Multiple() throws Exception {
    LOGGER.log(Level.INFO, "testReceive_Single: Test registering the same updatable twice");
    try (C channel = getDeliveryChannel()) {
      U updatable = getUpdatable(channel, getIdentifier(0));
      channel.register(updatable);

      // Receive updates
      Set<M> messages = new HashSet<>();
      for (int i = 0; i < MESSAGES; i++) {
        M message = getUpdateMessage(getIdentifier(1), i);
        channel.receive(message);
        messages.add(message);
      }
      triggerUpdates(channel);

      // Wait for the update to be applied
      DeliveryUtils.waitForUpdates(channel);

      // Verify that the correct messages were applied.
      Mockito.verify(updatable, Mockito.times(MESSAGES)).update((M) Mockito.any());
      for (M message : messages) {
        Mockito.verify(updatable).update(message);
      }

      // Wait a little longer and make sure nothing else happened
      Thread.sleep(BUFFER_TIME);
      Mockito.verify(updatable, Mockito.times(MESSAGES)).update((M) Mockito.any());
    }
  }

  // TODO: Test other methods (receive, hasPendingMessages, etc.)

}
