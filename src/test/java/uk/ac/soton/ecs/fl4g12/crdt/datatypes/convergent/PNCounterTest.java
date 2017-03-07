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

package uk.ac.soton.ecs.fl4g12.crdt.datatypes.convergent;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.CounterAbstractTest;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.Updatable;
import uk.ac.soton.ecs.fl4g12.crdt.order.HashVersionVector;
import uk.ac.soton.ecs.fl4g12.crdt.order.IntegerVersion;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Tests for the {@linkplain PNCounter} class.
 */
public class PNCounterTest extends CounterAbstractTest<Integer, PNCounter<Integer, Object>> {

  private static final Logger LOGGER = Logger.getLogger(PNCounterTest.class.getName());

  @Override
  protected PNCounter<Integer, Object> getCounter() {
    DeliveryChannel<Object, PNCounterState<Integer, Object>> deliveryChannel =
        Mockito.mock(DeliveryChannel.class);
    Mockito.doReturn(new Object()).doThrow(IllegalStateException.class).when(deliveryChannel)
        .register(Mockito.any(Updatable.class));
    return PNCounter.newIntegerPNCounter(deliveryChannel);
  }

  @Override
  protected Integer getValue(int increments, int decrements) {
    return increments - decrements;
  }

  /**
   * Ensure that when the counter is incremented, that the change is published to the
   * {@linkplain DeliveryChannel}.
   */
  @Test
  public void testIncrement_Publish() {
    LOGGER.log(Level.INFO, "testIncrement_Publish: "
        + "Ensure that when the counter is incremented, that the change is published to the DeliveryChannel");
    final PNCounter<Integer, Object> counter = getCounter();

    final VersionVector<Object, Integer> expectedVersionVector =
        new HashVersionVector<>(new IntegerVersion(), false);
    final VersionVector<Object, Integer> expectedN = expectedVersionVector.copy();
    expectedVersionVector.init(counter.getIdentifier());
    expectedVersionVector.increment(counter.getIdentifier());
    final VersionVector<Object, Integer> expectedP = expectedVersionVector.copy();
    final DeliveryChannel<Object, PNCounterState<Integer, Object>> deliveryChannel =
        counter.getDeliveryChannel();

    Mockito.reset(deliveryChannel);
    counter.increment();
    Mockito.verify(deliveryChannel).publish(Mockito.argThat(new ArgumentMatcher<PNCounterState>() {
      @Override
      public boolean matches(PNCounterState t) {
        if (!t.getIdentifier().equals(counter.getIdentifier())) {
          return false;
        }
        if (!t.getVersionVector().identical(expectedVersionVector)) {
          return false;
        }
        if (!t.getP().identical(expectedP)) {
          return false;
        }
        if (!t.getN().identical(expectedN)) {
          return false;
        }
        return true;
      }
    }));
    Mockito.verifyNoMoreInteractions(deliveryChannel);
  }

  /**
   * Ensure that when the counter is decremented, that the change is published to the
   * {@linkplain DeliveryChannel}.
   */
  @Test
  public void testDecrement_Publish() {
    LOGGER.log(Level.INFO, "testIncrement_Publish: "
        + "Ensure that when the counter is incremented, that the change is published to the DeliveryChannel");
    final PNCounter<Integer, Object> counter = getCounter();

    final VersionVector<Object, Integer> expectedVersionVector =
        new HashVersionVector<>(new IntegerVersion(), false);
    final VersionVector<Object, Integer> expectedP = expectedVersionVector.copy();
    expectedVersionVector.init(counter.getIdentifier());
    expectedVersionVector.increment(counter.getIdentifier());
    final VersionVector<Object, Integer> expectedN = expectedVersionVector.copy();
    final DeliveryChannel<Object, PNCounterState<Integer, Object>> deliveryChannel =
        counter.getDeliveryChannel();

    Mockito.reset(deliveryChannel);
    counter.decrement();
    Mockito.verify(deliveryChannel).publish(Mockito.argThat(new ArgumentMatcher<PNCounterState>() {
      @Override
      public boolean matches(PNCounterState t) {
        if (!t.getIdentifier().equals(counter.getIdentifier())) {
          return false;
        }
        if (!t.getVersionVector().identical(expectedVersionVector)) {
          return false;
        }
        if (!t.getP().identical(expectedP)) {
          return false;
        }
        if (!t.getN().identical(expectedN)) {
          return false;
        }
        return true;
      }
    }));
    Mockito.verifyNoMoreInteractions(deliveryChannel);
  }

  /**
   * Ensure that when the counter is decremented after an increment, that the change is published to
   * the {@linkplain DeliveryChannel}.
   */
  @Test
  public void testIncrementDecrement_Publish() {
    LOGGER.log(Level.INFO, "testIncrement_Publish: "
        + "Ensure that when the counter is incremented, that the change is published to the DeliveryChannel");
    final PNCounter<Integer, Object> counter = getCounter();

    final VersionVector<Object, Integer> expectedVersionVector =
        new HashVersionVector<>(new IntegerVersion(), false);
    expectedVersionVector.init(counter.getIdentifier());
    expectedVersionVector.increment(counter.getIdentifier());
    final VersionVector<Object, Integer> expectedP = expectedVersionVector.copy();
    final VersionVector<Object, Integer> expectedN = expectedVersionVector.copy();
    expectedVersionVector.increment(counter.getIdentifier());
    final DeliveryChannel<Object, PNCounterState<Integer, Object>> deliveryChannel =
        counter.getDeliveryChannel();

    counter.increment();
    Mockito.reset(deliveryChannel);
    counter.decrement();
    Mockito.verify(deliveryChannel).publish(Mockito.argThat(new ArgumentMatcher<PNCounterState>() {
      @Override
      public boolean matches(PNCounterState t) {
        if (!t.getIdentifier().equals(counter.getIdentifier())) {
          return false;
        }
        if (!t.getVersionVector().identical(expectedVersionVector)) {
          return false;
        }
        if (!t.getP().identical(expectedP)) {
          return false;
        }
        if (!t.getN().identical(expectedN)) {
          return false;
        }
        return true;
      }
    }));
    Mockito.verifyNoMoreInteractions(deliveryChannel);
  }

  /**
   * Ensure that when the counter is decremented after an increment, that the change is published to
   * the {@linkplain DeliveryChannel}.
   */
  @Test
  public void testDecrementIncrement_Publish() {
    LOGGER.log(Level.INFO, "testIncrement_Publish: "
        + "Ensure that when the counter is incremented, that the change is published to the DeliveryChannel");
    final PNCounter<Integer, Object> counter = getCounter();

    final VersionVector<Object, Integer> expectedVersionVector =
        new HashVersionVector<>(new IntegerVersion(), false);
    expectedVersionVector.init(counter.getIdentifier());
    expectedVersionVector.increment(counter.getIdentifier());
    final VersionVector<Object, Integer> expectedP = expectedVersionVector.copy();
    final VersionVector<Object, Integer> expectedN = expectedVersionVector.copy();
    expectedVersionVector.increment(counter.getIdentifier());
    final DeliveryChannel<Object, PNCounterState<Integer, Object>> deliveryChannel =
        counter.getDeliveryChannel();

    counter.decrement();
    Mockito.reset(deliveryChannel);
    counter.increment();
    Mockito.verify(deliveryChannel).publish(Mockito.argThat(new ArgumentMatcher<PNCounterState>() {
      @Override
      public boolean matches(PNCounterState t) {
        if (!t.getIdentifier().equals(counter.getIdentifier())) {
          return false;
        }
        if (!t.getVersionVector().identical(expectedVersionVector)) {
          return false;
        }
        if (!t.getP().identical(expectedP)) {
          return false;
        }
        if (!t.getN().identical(expectedN)) {
          return false;
        }
        return true;
      }
    }));
    Mockito.verifyNoMoreInteractions(deliveryChannel);
  }

  /**
   * Test snapshot of a zero counter.
   */
  @Test
  public void testSnapshot_Zero() {
    LOGGER.log(Level.INFO, "testSnapshot_Zero: Test snapshot of a zero counter.");
    final PNCounter<Integer, Object> counter = getCounter();

    final VersionVector<Object, Integer> expectedVersionVector =
        new HashVersionVector<>(new IntegerVersion(), false);

    PNCounterState<Integer, Object> state = counter.snapshot();

    assertEquals("state identifier should be the same as the counter", counter.getIdentifier(),
        state.getIdentifier());
    assertTrue("The VersionVector should match the expectation",
        state.getVersionVector().identical(expectedVersionVector));

    // Check that the state snapshot is immutable by changes to the counter.
    counter.increment();

    assertEquals("state identifier should be the same as the counter", counter.getIdentifier(),
        state.getIdentifier());
    assertTrue("The VersionVector should still match the expectation",
        state.getVersionVector().identical(expectedVersionVector));
  }

  /**
   * Test snapshot of a zero counter.
   */
  @Test
  public void testSnapshot_Incremented() {
    LOGGER.log(Level.INFO, "testSnapshot_Zero: Test snapshot of a zero counter.");
    final PNCounter<Integer, Object> counter = getCounter();

    final VersionVector<Object, Integer> expectedVersionVector =
        new HashVersionVector<>(new IntegerVersion(), false);
    expectedVersionVector.init(counter.getIdentifier());
    expectedVersionVector.increment(counter.getIdentifier());

    counter.increment();
    PNCounterState<Integer, Object> state = counter.snapshot();

    assertEquals("state identifier should be the same as the counter", counter.getIdentifier(),
        state.getIdentifier());
    assertTrue("The VersionVector should match the expectation",
        state.getVersionVector().identical(expectedVersionVector));

    // Check that the state snapshot is immutable by changes to the counter.
    counter.increment();

    assertEquals("state identifier should be the same as the counter", counter.getIdentifier(),
        state.getIdentifier());
    assertTrue("The VersionVector should still match the expectation",
        state.getVersionVector().identical(expectedVersionVector));
  }

  /**
   * Test update with no changes.
   */
  @Test
  public void testUpdate_NoChange() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_NoChange: Test update with no changes.");

    final PNCounter<Integer, Object> counter1 = getCounter();
    final PNCounter<Integer, Object> counter2 = getCounter();

    assertTrue("The counters should be identical to start with",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical to start with", counter1.value(),
        counter2.value());

    counter1.update(counter2.snapshot());
    assertTrue("The counters should be identical after update",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical after update", counter1.value(),
        counter2.value());

    counter2.update(counter1.snapshot());
    assertTrue("The counters should be identical after bi-directional update",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical after update", counter1.value(),
        counter2.value());
  }

  /**
   * Test update with no changes.
   */
  @Test
  public void testUpdate_LocalIncrement() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_LocalIncrement: Test update with no changes.");

    final PNCounter<Integer, Object> counter1 = getCounter();
    final PNCounter<Integer, Object> counter2 = getCounter();

    assertTrue("The counters should be identical to start with",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical to start with", counter1.value(),
        counter2.value());
    assertEquals("counter1 should have seen 0 increments", getValue(0), counter1.value());
    assertEquals("counter2 should have seen 0 increments", getValue(0), counter2.value());

    counter1.increment();
    assertTrue("counter2 should have happenedBefore counter1",
        counter2.getVersion().happenedBefore(counter1.getVersion()));

    counter1.update(counter2.snapshot());
    assertTrue("counter2 should have happenedBefore counter1",
        counter2.getVersion().happenedBefore(counter1.getVersion()));
    assertEquals("counter1 should have seen 1 increment", getValue(1), counter1.value());
    assertEquals("counter2 should have seen 0 increments", getValue(0), counter2.value());

    counter2.update(counter1.snapshot());
    assertTrue("The counters should be identical after bi-directional update",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical after bi-directional update", counter1.value(),
        counter2.value());
    assertEquals("counter1 should have seen 1 increment", getValue(1), counter1.value());
    assertEquals("counter2 should have seen 1 increment", getValue(1), counter2.value());
  }

  /**
   * Test update with no changes.
   */
  @Test
  public void testUpdate_RemoteIncrement() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_RemoteIncrement: Test update with no changes.");

    final PNCounter<Integer, Object> counter1 = getCounter();
    final PNCounter<Integer, Object> counter2 = getCounter();

    assertTrue("The counters should be identical to start with",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical to start with", counter1.value(),
        counter2.value());
    assertEquals("counter1 should have seen 0 increments", getValue(0), counter1.value());
    assertEquals("counter2 should have seen 0 increments", getValue(0), counter2.value());

    counter2.increment();
    assertTrue("counter1 should have happenedBefore counter2",
        counter1.getVersion().happenedBefore(counter2.getVersion()));

    counter1.update(counter2.snapshot());
    assertTrue("The counters should be identical after update",
        counter2.getVersion().identical(counter1.getVersion()));
    assertEquals("counter1 should have seen 1 increment", getValue(1), counter1.value());
    assertEquals("counter2 should have seen 1 increments", getValue(1), counter2.value());

    counter2.update(counter1.snapshot());
    assertTrue("The counters should be identical after bi-directional update",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical after bi-directional update", counter1.value(),
        counter2.value());
    assertEquals("counter1 should have seen 1 increment", getValue(1), counter1.value());
    assertEquals("counter2 should have seen 1 increment", getValue(1), counter2.value());
  }

  /**
   * Test update with no changes.
   */
  @Test
  public void testUpdate_BothIncrement() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_BothIncrement: Test update with no changes.");

    final PNCounter<Integer, Object> counter1 = getCounter();
    final PNCounter<Integer, Object> counter2 = getCounter();

    assertTrue("The counters should be identical to start with",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical to start with", counter1.value(),
        counter2.value());
    assertEquals("counter1 should have seen 0 increments", getValue(0), counter1.value());
    assertEquals("counter2 should have seen 0 increments", getValue(0), counter2.value());

    counter1.increment();
    counter2.increment();
    assertTrue("counter1 should be concurrent with counter2",
        counter1.getVersion().concurrentWith(counter2.getVersion()));

    counter1.update(counter2.snapshot());
    assertTrue("counter2 should have happenedBefore counter1",
        counter2.getVersion().happenedBefore(counter1.getVersion()));
    assertEquals("counter1 should have seen 2 increment", getValue(2), counter1.value());
    assertEquals("counter2 should have seen 1 increments", getValue(1), counter2.value());

    counter2.update(counter1.snapshot());
    assertTrue("The counters should be identical after bi-directional update",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical after bi-directional update", counter1.value(),
        counter2.value());
    assertEquals("counter1 should have seen 2 increment", getValue(2), counter1.value());
    assertEquals("counter2 should have seen 2 increment", getValue(2), counter2.value());
  }

  /**
   * Test update with no changes.
   */
  @Test
  public void testUpdate_LocalDecrement() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_LocalIncrement: Test update with no changes.");

    final PNCounter<Integer, Object> counter1 = getCounter();
    final PNCounter<Integer, Object> counter2 = getCounter();

    assertTrue("The counters should be identical to start with",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical to start with", counter1.value(),
        counter2.value());
    assertEquals("counter1 should have seen 0 increments", getValue(0, 0), counter1.value());
    assertEquals("counter2 should have seen 0 increments", getValue(0, 0), counter2.value());

    counter1.decrement();
    assertTrue("counter2 should have happenedBefore counter1",
        counter2.getVersion().happenedBefore(counter1.getVersion()));

    counter1.update(counter2.snapshot());
    assertTrue("counter2 should have happenedBefore counter1",
        counter2.getVersion().happenedBefore(counter1.getVersion()));
    assertEquals("counter1 should have seen 1 increment", getValue(0, 1), counter1.value());
    assertEquals("counter2 should have seen 0 increments", getValue(0, 0), counter2.value());

    counter2.update(counter1.snapshot());
    assertTrue("The counters should be identical after bi-directional update",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical after bi-directional update", counter1.value(),
        counter2.value());
    assertEquals("counter1 should have seen 1 increment", getValue(0, 1), counter1.value());
    assertEquals("counter2 should have seen 1 increment", getValue(0, 1), counter2.value());
  }

  /**
   * Test update with no changes.
   */
  @Test
  public void testUpdate_RemoteDecrement() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_RemoteIncrement: Test update with no changes.");

    final PNCounter<Integer, Object> counter1 = getCounter();
    final PNCounter<Integer, Object> counter2 = getCounter();

    assertTrue("The counters should be identical to start with",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical to start with", counter1.value(),
        counter2.value());
    assertEquals("counter1 should have seen 0 increments", getValue(0, 0), counter1.value());
    assertEquals("counter2 should have seen 0 increments", getValue(0, 0), counter2.value());

    counter2.decrement();
    assertTrue("counter1 should have happenedBefore counter2",
        counter1.getVersion().happenedBefore(counter2.getVersion()));

    counter1.update(counter2.snapshot());
    assertTrue("The counters should be identical after update",
        counter2.getVersion().identical(counter1.getVersion()));
    assertEquals("counter1 should have seen 1 increment", getValue(0, 1), counter1.value());
    assertEquals("counter2 should have seen 1 increments", getValue(0, 1), counter2.value());

    counter2.update(counter1.snapshot());
    assertTrue("The counters should be identical after bi-directional update",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical after bi-directional update", counter1.value(),
        counter2.value());
    assertEquals("counter1 should have seen 1 increment", getValue(0, 1), counter1.value());
    assertEquals("counter2 should have seen 1 increment", getValue(0, 1), counter2.value());
  }

  /**
   * Test update with no changes.
   */
  @Test
  public void testUpdate_BothDecrement() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_BothIncrement: Test update with no changes.");

    final PNCounter<Integer, Object> counter1 = getCounter();
    final PNCounter<Integer, Object> counter2 = getCounter();

    assertTrue("The counters should be identical to start with",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical to start with", counter1.value(),
        counter2.value());
    assertEquals("counter1 should have seen 0 increments", getValue(0, 0), counter1.value());
    assertEquals("counter2 should have seen 0 increments", getValue(0, 0), counter2.value());

    counter1.decrement();
    counter2.decrement();
    assertTrue("counter1 should be concurrent with counter2",
        counter1.getVersion().concurrentWith(counter2.getVersion()));

    counter1.update(counter2.snapshot());
    assertTrue("counter2 should have happenedBefore counter1",
        counter2.getVersion().happenedBefore(counter1.getVersion()));
    assertEquals("counter1 should have seen 2 increment", getValue(0, 2), counter1.value());
    assertEquals("counter2 should have seen 1 increments", getValue(0, 1), counter2.value());

    counter2.update(counter1.snapshot());
    assertTrue("The counters should be identical after bi-directional update",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical after bi-directional update", counter1.value(),
        counter2.value());
    assertEquals("counter1 should have seen 2 increment", getValue(0, 2), counter1.value());
    assertEquals("counter2 should have seen 2 increment", getValue(0, 2), counter2.value());
  }

  /**
   * Test update with no changes.
   */
  @Test
  public void testUpdate_IncrementDecrement() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_BothIncrement: Test update with no changes.");

    final PNCounter<Integer, Object> counter1 = getCounter();
    final PNCounter<Integer, Object> counter2 = getCounter();

    assertTrue("The counters should be identical to start with",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical to start with", counter1.value(),
        counter2.value());
    assertEquals("counter1 should have seen 0 increments", getValue(0, 0), counter1.value());
    assertEquals("counter2 should have seen 0 increments", getValue(0, 0), counter2.value());

    counter1.increment();
    counter2.decrement();
    assertTrue("counter1 should be concurrent with counter2",
        counter1.getVersion().concurrentWith(counter2.getVersion()));

    counter1.update(counter2.snapshot());
    assertTrue("counter2 should have happenedBefore counter1",
        counter2.getVersion().happenedBefore(counter1.getVersion()));
    assertEquals("counter1 should have seen 2 increment", getValue(1, 1), counter1.value());
    assertEquals("counter2 should have seen 1 increments", getValue(0, 1), counter2.value());

    counter2.update(counter1.snapshot());
    assertTrue("The counters should be identical after bi-directional update",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical after bi-directional update", counter1.value(),
        counter2.value());
    assertEquals("counter1 should have seen 2 increment", getValue(1, 1), counter1.value());
    assertEquals("counter2 should have seen 2 increment", getValue(1, 1), counter2.value());
  }

}
