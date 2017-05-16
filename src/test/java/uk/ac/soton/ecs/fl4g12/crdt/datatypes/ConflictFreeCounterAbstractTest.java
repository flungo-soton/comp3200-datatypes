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

package uk.ac.soton.ecs.fl4g12.crdt.datatypes;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.order.Version;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Abstract tests for {@linkplain CRDT} {@linkplain Counter} implementations. Tests that the
 * outcomes of operations are conflict-free.
 *
 * @param <E> the type of values stored in the {@link Counter}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp within the {@link VersionVector}
 * @param <M> the type of {@link VersionedUpdateMessage} produced by the {@link VersionedUpdatable}
 *        {@link Counter}.
 * @param <C> the type of {@link Counter} being tested.
 */
public abstract class ConflictFreeCounterAbstractTest<E, K, T extends Comparable<T>, M extends VersionedUpdateMessage<K, ? extends Version>, C extends Counter<E> & VersionedUpdatable<K, VersionVector<K, T>, M>>
    extends IncrementableConflictFreeCounterAbstractTest<E, K, T, M, C> {

  private static final Logger LOGGER =
      Logger.getLogger(ConflictFreeCounterAbstractTest.class.getName());

  @Override
  public final E getValue(int count) {
    return getValue(count, 0);
  }

  @Override
  public abstract E getValue(int increments, int decrements);

  /**
   * Test update with a local decrement.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_LocalDecrement() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_LocalDecrement: Test update with a local decrement.");

    final C counter1 = getCounter();
    final DeliveryChannel<K, M, ?> delivery1 = counter1.getDeliveryChannel();
    final C counter2 = getCounter();

    // Message buffer
    M message;

    // Assert initial state
    assertEquals(getValue(0, 0), counter1.value());
    assertEquals(getValue(0, 0), counter2.value());

    counter1.decrement();
    message = assertPublish(delivery1);
    assertTrue("counter2 should have happenedBefore counter1",
        counter2.getVersion().happenedBefore(counter1.getVersion()));

    intermediateDelivery(counter1, counter2);
    assertTrue("counter2 should have happenedBefore counter1",
        counter2.getVersion().happenedBefore(counter1.getVersion()));
    assertEquals(getValue(0, 1), counter1.value());
    assertEquals(getValue(0, 0), counter2.value());

    counter2.update(message);
    assertFinalState(getValue(0, 1), counter1, counter2);
  }

  /**
   * Test update with a remote decrement.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_RemoteDecrement() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_RemoteDecrement: Test update with a remote decrement.");

    final C counter1 = getCounter();
    final C counter2 = getCounter();
    final DeliveryChannel<K, M, ?> delivery2 = counter2.getDeliveryChannel();

    // Message buffer
    M message;

    // Assert initial state
    assertEquals(getValue(0, 0), counter1.value());
    assertEquals(getValue(0, 0), counter2.value());

    counter2.decrement();
    message = assertPublish(delivery2);
    assertTrue("counter1 should have happenedBefore counter2",
        counter1.getVersion().happenedBefore(counter2.getVersion()));

    counter1.update(message);
    assertTrue("The version vectors should be identical after update",
        counter2.getVersion().identical(counter1.getVersion()));
    assertEquals(getValue(0, 1), counter1.value());
    assertEquals(getValue(0, 1), counter2.value());

    intermediateDelivery(counter1, counter2);
    assertFinalState(getValue(0, 1), counter1, counter2);
  }

  /**
   * Test update with concurrent decrements.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_BothDecrement() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_BothDecrement: Test update with concurrent decrements.");

    final C counter1 = getCounter();
    final DeliveryChannel<K, M, ?> delivery1 = counter1.getDeliveryChannel();
    final C counter2 = getCounter();
    final DeliveryChannel<K, M, ?> delivery2 = counter2.getDeliveryChannel();

    // Message buffer
    M message;

    // Assert initial state
    assertEquals(getValue(0, 0), counter1.value());
    assertEquals(getValue(0, 0), counter2.value());

    counter1.decrement();
    message = assertPublish(delivery1);
    final M update1 = message;
    counter2.decrement();
    message = assertPublish(delivery2);
    final M update2 = message;
    assertTrue("counter1 should be concurrent with counter2",
        counter1.getVersion().concurrentWith(counter2.getVersion()));

    counter1.update(update2);
    assertTrue("counter2 should have happenedBefore counter1",
        counter2.getVersion().happenedBefore(counter1.getVersion()));
    assertEquals(getValue(0, 2), counter1.value());
    assertEquals(getValue(0, 1), counter2.value());

    counter2.update(update1);
    assertFinalState(getValue(0, 2), counter1, counter2);
  }

  /**
   * Test update with a local increment and remote decrement of different elements.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_IncrementDecrement() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_IncrementDecrement: "
        + "Test update with a local increment and remote decrement of different elements.");

    final C counter1 = getCounter();
    final DeliveryChannel<K, M, ?> delivery1 = counter1.getDeliveryChannel();
    final C counter2 = getCounter();
    final DeliveryChannel<K, M, ?> delivery2 = counter2.getDeliveryChannel();

    // Message buffer
    M message;

    // Assert initial state
    assertEquals(getValue(0, 0), counter1.value());
    assertEquals(getValue(0, 0), counter2.value());

    counter1.increment();
    message = assertPublish(delivery1);
    final M update1 = message;
    counter2.decrement();
    message = assertPublish(delivery2);
    final M update2 = message;
    assertTrue("counter1 should be concurrent with counter2",
        counter1.getVersion().concurrentWith(counter2.getVersion()));

    counter1.update(update2);
    assertTrue("counter2 should have happenedBefore counter1",
        counter2.getVersion().happenedBefore(counter1.getVersion()));
    assertEquals(getValue(1, 1), counter1.value());
    assertEquals(getValue(0, 1), counter2.value());

    counter2.update(update1);
    assertFinalState(getValue(1, 1), counter1, counter2);
  }

  /**
   * Test update with a local decrement and remote increment of different elements.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_DecrementIncrement() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_DecrementIncrement: "
        + "Test update with a local decrement and remote increment of different elements.");

    final C counter1 = getCounter();
    final DeliveryChannel<K, M, ?> delivery1 = counter1.getDeliveryChannel();
    final C counter2 = getCounter();
    final DeliveryChannel<K, M, ?> delivery2 = counter2.getDeliveryChannel();

    // Message buffer
    M message;

    // Assert initial state
    assertEquals(getValue(0, 0), counter1.value());
    assertEquals(getValue(0, 0), counter2.value());

    counter1.decrement();
    message = assertPublish(delivery1);
    final M update1 = message;
    counter2.increment();
    message = assertPublish(delivery2);
    final M update2 = message;
    assertTrue("counter1 should be concurrent with counter2",
        counter1.getVersion().concurrentWith(counter2.getVersion()));

    counter1.update(update2);
    assertTrue("counter2 should have happenedBefore counter1",
        counter2.getVersion().happenedBefore(counter1.getVersion()));
    assertEquals(getValue(1, 1), counter1.value());
    assertEquals(getValue(1, 0), counter2.value());

    counter2.update(update1);
    assertFinalState(getValue(1, 1), counter1, counter2);
  }
}
