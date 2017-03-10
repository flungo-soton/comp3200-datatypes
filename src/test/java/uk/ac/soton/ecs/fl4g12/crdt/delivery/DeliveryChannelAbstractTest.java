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

import static org.junit.Assert.assertNotNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mockito;
import uk.ac.soton.ecs.fl4g12.crdt.idenitifier.IdentifierFactory;

/**
 * Abstract tests for {@linkplain DeliveryChannel} implementations.
 *
 * @param <K> The type of the identifier that is assigned to the {@link Updatable}.
 * @param <U> The type of updates sent via the delivery channel.
 * @param <C> the type of the {@linkplain DeliveryChannel} to be tested.
 */
public abstract class DeliveryChannelAbstractTest<K, U extends UpdateMessage<K, ?>, C extends DeliveryChannel<K, U>> {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Gets an identifier. This should be a bijection such that for any unique value of {@code i}
   * given, a unique identifier is returned.
   *
   * @param i the unique specifier of which identifier to return.
   * @return the unique identifier mapped to input {@code i}.
   */
  public abstract K getIdentifier(int i);

  /**
   * Gets an update message which may be sent by the {@linkplain Updatable} with given identifier.
   * The value of {@code order} determines the ordering of the messages relative to other messages
   * from the same {@link Updatable}.
   *
   * The update message returned should be a Mockito Mock or Spy to allow verifications on method
   * calls.
   *
   * @param id the identifier of the {@link Updatable} that produced the message.
   * @param order the order of the {@link UpdateMessage} relative to others from the same
   *        {@link Updatable}.
   * @return an update message for {@link Updatable} with identifier {@code id}.
   */
  public abstract U getUpdateMessage(K id, int order);

  /**
   * Get a {@linkplain DeliveryChannel} that can be used for testing. {@link DeliveryChannel}s with
   * the same {@link Channel} are used with replicas of the same {@link Updatable} and {@code i}
   * uniquely identifies the instances with the same {@link Channel}.
   *
   * Between tests, the {@link DeliveryChannel}s should be cleared by utilising an
   * {@link org.junit.After} handler.
   *
   * @param channel the channel which the
   * @param i a unique identifier for the instance of the {@link Updatable}
   * @return the {@link DeliveryChannel} for the given {@link Channel}.
   */
  public abstract C getDeliveryChannel(Channel channel, int i);

  /**
   * Test that when an {@link Updatable} which does not have an ID is registered, that an ID is
   * assigned.
   */
  @Test
  public void testRegister_NoID() {
    C channel = getDeliveryChannel(Channel.A, 0);

    Updatable updatable = Mockito.mock(Updatable.class);
    Mockito.doReturn(null).when(updatable).getIdentifier();

    Object id = channel.register(updatable);

    assertNotNull(id);
    Mockito.verify(updatable).getIdentifier();
    Mockito.verifyNoMoreInteractions(updatable);
  }

  /**
   * Test that when an object is registered and already has an ID, that the ID is kept.
   */
  @Test
  public void testRegister_HasID() {
    C channel = getDeliveryChannel(Channel.A, 0);

    K expectedId = getIdentifier(3);

    Updatable updatable = Mockito.mock(Updatable.class);
    Mockito.doReturn(expectedId).when(updatable).getIdentifier();

    Object id = channel.register(updatable);

    assertNotNull(id);
    Mockito.verify(updatable).getIdentifier();
    Mockito.verifyNoMoreInteractions(updatable);
  }

  /**
   * Test that registering the same object twice fails on the second attempt.
   */
  @Test
  public void testRegister_Twice() {
    C channel = getDeliveryChannel(Channel.A, 0);

    K id = getIdentifier(0);

    Updatable updatable = Mockito.mock(Updatable.class);
    Mockito.doReturn(id).when(updatable).getIdentifier();

    channel.register(updatable);

    // Expect exception
    thrown.expect(IllegalStateException.class);
    channel.register(updatable);
  }

  /**
   * Test that registration fails if an object with the same ID as an already registered object is
   * given.
   */
  @Test
  public void testRegister_DuplicateID() {
    C channel = getDeliveryChannel(Channel.A, 0);

    K id = getIdentifier(0);

    Updatable updatable1 = Mockito.mock(Updatable.class);
    Mockito.doReturn(id).when(updatable1).getIdentifier();

    channel.register(updatable1);

    Updatable updatable2 = Mockito.mock(Updatable.class);
    Mockito.doReturn(id).when(updatable2).getIdentifier();

    // Expect exception
    thrown.expect(IllegalStateException.class);
    channel.register(updatable2);
  }

  /**
   * Test that individually publiushed messages are sent to the right reciptients.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testPublish_Single() throws Exception {
    // Setup channel A0
    C channelA0 = getDeliveryChannel(Channel.A, 0);

    K id0 = getIdentifier(0);
    Updatable updatableA0 = Mockito.mock(Updatable.class);
    Mockito.doReturn(id0).when(updatableA0).getIdentifier();
    channelA0.register(updatableA0);

    // Setup channel A1
    C channelA1 = getDeliveryChannel(Channel.A, 1);

    K id1 = getIdentifier(1);
    Updatable updatableA1 = Mockito.mock(Updatable.class);
    Mockito.doReturn(id1).when(updatableA1).getIdentifier();
    channelA1.register(updatableA1);

    // Setup channel A2
    C channelA2 = getDeliveryChannel(Channel.A, 2);

    K id2 = getIdentifier(2);
    Updatable updatableA2 = Mockito.mock(Updatable.class);
    Mockito.doReturn(id2).when(updatableA2).getIdentifier();
    channelA2.register(updatableA2);

    // Setup channel B0
    C channelB0 = getDeliveryChannel(Channel.B, 0);

    Updatable updatableB0 = Mockito.mock(Updatable.class);
    Mockito.doReturn(id0).when(updatableB0).getIdentifier();
    channelB0.register(updatableB0);

    // Setup channel B1
    C channelB1 = getDeliveryChannel(Channel.B, 1);

    Updatable updatableB1 = Mockito.mock(Updatable.class);
    Mockito.doReturn(id1).when(updatableB1).getIdentifier();
    channelB1.register(updatableB1);

    // Setup channel B2
    C channelB2 = getDeliveryChannel(Channel.B, 2);

    Updatable updatableB2 = Mockito.mock(Updatable.class);
    Mockito.doReturn(id2).when(updatableB2).getIdentifier();
    channelB2.register(updatableB2);

    // Publish a message on A0
    U updateA0_0 = getUpdateMessage(id0, 0);
    channelA0.publish(updateA0_0);

    // Make assertions
    Mockito.verify(updatableA0, Mockito.times(0)).update(updateA0_0);
    Mockito.verify(updatableA1).update(updateA0_0);
    Mockito.verify(updatableA2).update(updateA0_0);
    Mockito.verify(updatableB0, Mockito.times(0)).update(updateA0_0);
    Mockito.verify(updatableB1, Mockito.times(0)).update(updateA0_0);
    Mockito.verify(updatableB2, Mockito.times(0)).update(updateA0_0);

    // Publish a message on A1
    U updateA1_0 = getUpdateMessage(id1, 0);
    channelA1.publish(updateA1_0);

    // Make assertions
    Mockito.verify(updatableA0).update(updateA1_0);
    Mockito.verify(updatableA1, Mockito.times(0)).update(updateA1_0);
    Mockito.verify(updatableA2).update(updateA1_0);
    Mockito.verify(updatableB0, Mockito.times(0)).update(updateA1_0);
    Mockito.verify(updatableB1, Mockito.times(0)).update(updateA1_0);
    Mockito.verify(updatableB2, Mockito.times(0)).update(updateA1_0);
  }

  /**
   * Test that if an exception is thrown delivering to one {@linkplain Updatable} it is delivered to
   * the others. Tested with both receivers throwing an exception for each message to ensure that
   * regardless of the order that the messages are delivered, this method will test that the
   * exception does not stop delivery to other receivers.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testPublish_SingleException() throws Exception {
    // Setup channel A0
    C channelA0 = getDeliveryChannel(Channel.A, 0);

    K id0 = getIdentifier(0);
    Updatable updatableA0 = Mockito.mock(Updatable.class);
    Mockito.doReturn(id0).when(updatableA0).getIdentifier();
    channelA0.register(updatableA0);

    // Setup channel A1
    C channelA1 = getDeliveryChannel(Channel.A, 1);

    K id1 = getIdentifier(1);
    Updatable updatableA1 = Mockito.mock(Updatable.class);
    Mockito.doReturn(id1).when(updatableA1).getIdentifier();
    Mockito.doThrow(DeliveryUpdateException.class).doNothing().when(updatableA1)
        .update(Mockito.any(UpdateMessage.class));
    channelA1.register(updatableA1);

    // Setup channel A2
    C channelA2 = getDeliveryChannel(Channel.A, 2);

    K id2 = getIdentifier(2);
    Updatable updatableA2 = Mockito.mock(Updatable.class);
    Mockito.doReturn(id2).when(updatableA2).getIdentifier();
    Mockito.doNothing().doThrow(DeliveryUpdateException.class).when(updatableA1)
        .update(Mockito.any(UpdateMessage.class));
    channelA2.register(updatableA2);

    // Publish a message on A0
    U updateA0_0 = getUpdateMessage(id0, 0);
    channelA0.publish(updateA0_0);

    // Make assertions
    Mockito.verify(updatableA0, Mockito.times(0)).update(updateA0_0);
    Mockito.verify(updatableA1).update(updateA0_0);
    Mockito.verify(updatableA2).update(updateA0_0);

    // Publish a message on A0
    U updateA0_1 = getUpdateMessage(id0, 1);
    channelA0.publish(updateA0_1);

    // Make assertions
    Mockito.verify(updatableA0, Mockito.times(0)).update(updateA0_1);
    Mockito.verify(updatableA1).update(updateA0_1);
    Mockito.verify(updatableA2).update(updateA0_1);
  }

  /**
   * Test that when multiple messages are published, they are published in the correct order.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testPublish_Multiple() throws Exception {
    // Setup channel A0
    C channelA0 = getDeliveryChannel(Channel.A, 0);

    K id0 = getIdentifier(0);
    Updatable updatableA0 = Mockito.mock(Updatable.class);
    Mockito.doReturn(id0).when(updatableA0).getIdentifier();
    channelA0.register(updatableA0);

    // Setup channel A1
    C channelA1 = getDeliveryChannel(Channel.A, 1);

    K id1 = getIdentifier(1);
    Updatable updatableA1 = Mockito.mock(Updatable.class);
    Mockito.doReturn(id1).when(updatableA1).getIdentifier();
    channelA1.register(updatableA1);

    // Setup inOrder
    InOrder inOrder = Mockito.inOrder(updatableA1);

    // Publish a message on A0
    U updateA0_0 = getUpdateMessage(id0, 0);
    U updateA0_1 = getUpdateMessage(id0, 1);
    U updateA0_2 = getUpdateMessage(id0, 2);
    U updateA0_3 = getUpdateMessage(id0, 3);
    U updateA0_4 = getUpdateMessage(id0, 4);
    U updateA0_5 = getUpdateMessage(id0, 5);
    channelA0.publish(updateA0_0, updateA0_1, updateA0_2, updateA0_3, updateA0_4, updateA0_5);

    // Make assertions
    Mockito.verify(updatableA0, Mockito.times(0)).update(updateA0_0);
    inOrder.verify(updatableA1).update(updateA0_0);
    inOrder.verify(updatableA1).update(updateA0_1);
    inOrder.verify(updatableA1).update(updateA0_2);
    inOrder.verify(updatableA1).update(updateA0_3);
    inOrder.verify(updatableA1).update(updateA0_4);
    inOrder.verify(updatableA1).update(updateA0_5);
  }

  /**
   * Test that when an exception delivering one message in a batch fails, the rest are not
   * delivered.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testPublish_MultipleException() throws Exception {
    // Setup channel A0
    C channelA0 = getDeliveryChannel(Channel.A, 0);

    K id0 = getIdentifier(0);
    Updatable updatableA0 = Mockito.mock(Updatable.class);
    Mockito.doReturn(id0).when(updatableA0).getIdentifier();
    channelA0.register(updatableA0);

    // Setup channel A1
    C channelA1 = getDeliveryChannel(Channel.A, 1);

    K id1 = getIdentifier(1);
    Updatable updatableA1 = Mockito.mock(Updatable.class);
    Mockito.doNothing().doNothing().doThrow(DeliveryUpdateException.class).doNothing()
        .when(updatableA1).update(Mockito.any(UpdateMessage.class));
    Mockito.doReturn(id1).when(updatableA1).getIdentifier();
    channelA1.register(updatableA1);

    // Setup inOrder
    InOrder inOrder = Mockito.inOrder(updatableA1);

    // Publish a message on A0
    U updateA0_0 = getUpdateMessage(id0, 0);
    U updateA0_1 = getUpdateMessage(id0, 1);
    U updateA0_2 = getUpdateMessage(id0, 2);
    U updateA0_3 = getUpdateMessage(id0, 3);
    U updateA0_4 = getUpdateMessage(id0, 4);
    U updateA0_5 = getUpdateMessage(id0, 5);
    channelA0.publish(updateA0_0, updateA0_1, updateA0_2, updateA0_3, updateA0_4, updateA0_5);

    // Make assertions
    Mockito.verify(updatableA0, Mockito.times(0)).update(updateA0_0);
    inOrder.verify(updatableA1).update(updateA0_0);
    inOrder.verify(updatableA1).update(updateA0_1);
    inOrder.verify(updatableA1).update(updateA0_2);
    Mockito.verify(updatableA1, Mockito.times(0)).update(updateA0_3);
    Mockito.verify(updatableA1, Mockito.times(0)).update(updateA0_4);
    Mockito.verify(updatableA1, Mockito.times(0)).update(updateA0_5);
  }

  // TODO: Test redelivery and causal ordering.

  /**
   * {@linkplain IdentifierFactory} that can be used as part of tests.
   */
  public class TestIdentifierFactory implements IdentifierFactory<K> {

    private int next;

    @Override
    public K create() {
      return getIdentifier(next++);
    }

    public int getNext() {
      return next;
    }

    public void setNext(int next) {
      this.next = next;
    }

  }

  /**
   * {@linkplain UpdateMessage} that can be used as part of tests.
   * 
   * @param <K> the type of identifier used to identify nodes.
   */
  public static class TestUpdateMessage<K> implements UpdateMessage<K, TestUpdateMessage<K>> {

    private final K identifier;
    private final Integer order;

    public TestUpdateMessage(K identifier, Integer order) {
      this.identifier = identifier;
      this.order = order;
    }

    @Override
    public K getIdentifier() {
      return identifier;
    }

    @Override
    public int compareTo(TestUpdateMessage o) {
      return order.compareTo(o.order);
    }

  }

  /**
   * Channels which are used for testing. Each channel is used for one replicated data type and
   * should be isolated from the others. When a message is published to any {@link DeliveryChannel},
   * part of the {@linkplain Channel}, it should be delivered to all other {@link DeliveryChannel}s
   * which are on the same {@link Channel}.
   */
  public static enum Channel {
    A, B
  }

}
