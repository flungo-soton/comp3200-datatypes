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
import uk.ac.soton.ecs.fl4g12.crdt.delivery.ReliableDeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.StateDeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.Updatable;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.UpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.order.Version;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Abstract tests for ensuring the conflict-free replicability in incrementable {@linkplain CRDT}
 * {@linkplain Counter}s.
 *
 * @param <E> the type of values stored in the {@link Counter}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp within the {@link VersionVector}
 * @param <M> the type of {@link VersionedUpdateMessage} produced by the {@link VersionedUpdatable}
 *        {@link Counter}.
 * @param <C> the type of {@link Counter} being tested.
 */
public abstract class IncrementableConflictFreeCounterAbstractTest<E, K, T extends Comparable<T>, M extends VersionedUpdateMessage<K, ? extends Version>, C extends Counter<E> & VersionedUpdatable<K, VersionVector<K, T>, M>>
    implements CounterTestInterface<E, C> {

  private static final Logger LOGGER =
      Logger.getLogger(IncrementableConflictFreeCounterAbstractTest.class.getName());

  @Override
  public E getValue(int increments, int decrements) {
    if (decrements != 0) {
      throw new UnsupportedOperationException("Increment only!");
    }
    return getValue(increments);
  }

  protected final void assertFinalState(E expected, C counter1, C counter2) {
    assertTrue("The version vectors should be identical after bi-directional update",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("counter1 should match expected final state", expected, counter1.value());
    assertEquals("counter2 should match expected final state", expected, counter2.value());
  }

  /**
   * Assert that a message was published and capture or create the {@link UpdateMessage} with these
   * changes. If the {@link DeliveryChannel} is a {@link ReliableDeliveryChannel} then the message
   * will have been published to the channel. If the {@link DeliveryChannel} is a
   * {@link StateDeliveryChannel} then capture the state from the {@link Updatable}.
   *
   * @param channel the channel which the message should have been published to.
   * @return the message that was published or a snapshot of the {@link Updatable}.
   */
  public abstract M assertPublish(DeliveryChannel<K, M, ?> channel);

  /**
   * Deliver an update from source to destination. This is provided to allow convergent
   * implementations to deliver changes to counter1 where no change to create an update for
   * commutative implementations. This is only used on tests which are non-concurrent.
   *
   * @param destination the counter to be updated.
   * @param source the counter to get state from.
   * @throws Exception to fail the test.
   */
  protected void intermediateDelivery(C destination, C source) throws Exception {
    // Do nothing by default
  }

  /**
   * Test update with no changes.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_NoChange() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_NoChange: Test update with no changes.");

    final C counter1 = getCounter();
    final C counter2 = getCounter();

    assertTrue("The version vectors should be identical to start with",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counter should be identical to start with", counter1.value(),
        counter2.value());

    intermediateDelivery(counter1, counter2);
    assertTrue("The version vectors should be identical after update",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical after update", counter1.value(),
        counter2.value());

    intermediateDelivery(counter2, counter1);
    assertTrue("The version vectors should be identical after bi-directional update",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical after update", counter1.value(),
        counter2.value());
  }

  /**
   * Test update with a local increment.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_LocalIncrement() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_LocalIncrement: Test update with a local increment.");

    final C counter1 = getCounter();
    final DeliveryChannel<K, M, ?> delivery1 = counter1.getDeliveryChannel();
    final C counter2 = getCounter();

    // Message buffer
    M message;

    // Assert initial state
    assertEquals(getValue(0, 0), counter1.value());
    assertEquals(getValue(0, 0), counter2.value());

    counter1.increment();
    message = assertPublish(delivery1);
    assertTrue("counter2 should have happenedBefore counter1",
        counter2.getVersion().happenedBefore(counter1.getVersion()));

    intermediateDelivery(counter1, counter2);
    assertTrue("counter2 should have happenedBefore counter1",
        counter2.getVersion().happenedBefore(counter1.getVersion()));
    assertEquals(getValue(1, 0), counter1.value());
    assertEquals(getValue(0, 0), counter2.value());

    counter2.update(message);
    assertFinalState(getValue(1, 0), counter1, counter2);
  }

  /**
   * Test update with a remote increment.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_RemoteIncrement() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_RemoteIncrement: Test update with a remote increment.");

    final C counter1 = getCounter();
    final C counter2 = getCounter();
    final DeliveryChannel<K, M, ?> delivery2 = counter2.getDeliveryChannel();

    // Message buffer
    M message;

    // Assert initial state
    assertEquals(getValue(0, 0), counter1.value());
    assertEquals(getValue(0, 0), counter2.value());

    counter2.increment();
    message = assertPublish(delivery2);
    assertTrue("counter1 should have happenedBefore counter2",
        counter1.getVersion().happenedBefore(counter2.getVersion()));

    counter1.update(message);
    assertTrue("The version vectors should be identical after update",
        counter2.getVersion().identical(counter1.getVersion()));
    assertEquals(getValue(1, 0), counter1.value());
    assertEquals(getValue(1, 0), counter2.value());

    intermediateDelivery(counter1, counter2);
    assertFinalState(getValue(1, 0), counter1, counter2);
  }

  /**
   * Test update with concurrent increments.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_BothIncrement() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_BothIncrement: Test update with concurrent increments.");

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
    counter2.increment();
    message = assertPublish(delivery2);
    final M update2 = message;
    assertTrue("counter1 should be concurrent with counter2",
        counter1.getVersion().concurrentWith(counter2.getVersion()));

    counter1.update(update2);
    assertTrue("counter2 should have happenedBefore counter1",
        counter2.getVersion().happenedBefore(counter1.getVersion()));
    assertEquals(getValue(2, 0), counter1.value());
    assertEquals(getValue(1, 0), counter2.value());

    counter2.update(update1);
    assertFinalState(getValue(2, 0), counter1, counter2);
  }

}
