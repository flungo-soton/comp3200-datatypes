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
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.IncrementableCounterAbstractTest;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.Updatable;
import uk.ac.soton.ecs.fl4g12.crdt.order.HashVersionVector;
import uk.ac.soton.ecs.fl4g12.crdt.order.IntegerVersion;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Tests for the {@linkplain GCounter} class.
 */
public class GCounterTest
    extends IncrementableCounterAbstractTest<Integer, GCounter<Integer, Object>> {

  private static final Logger LOGGER = Logger.getLogger(GCounterTest.class.getName());

  @Captor
  public ArgumentCaptor<GCounterState> updateMessageCaptor;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Override
  protected GCounter<Integer, Object> getCounter() {
    DeliveryChannel<Object, GCounterState<Integer, Object>> deliveryChannel =
        Mockito.mock(DeliveryChannel.class);
    Mockito.doReturn(new Object()).doThrow(IllegalStateException.class).when(deliveryChannel)
        .register(Mockito.any(Updatable.class));
    return GCounter.newIntegerGCounter(deliveryChannel);
  }

  @Override
  protected Integer getValue(int count) {
    return count;
  }

  /**
   * Ensure that when the counter is incremented, that the change is published to the
   * {@linkplain DeliveryChannel}.
   */
  @Test
  public void testIncrement_Publish() {
    LOGGER.log(Level.INFO, "testIncrement_Publish: "
        + "Ensure that when the counter is incremented, that the change is published to the DeliveryChannel");
    final GCounter<Integer, Object> counter = getCounter();

    final VersionVector<Object, Integer> expectedVersionVector =
        new HashVersionVector<>(new IntegerVersion());
    expectedVersionVector.init(counter.getIdentifier());
    expectedVersionVector.increment(counter.getIdentifier());
    final DeliveryChannel<Object, GCounterState<Integer, Object>> deliveryChannel =
        counter.getDeliveryChannel();

    Mockito.reset(deliveryChannel);
    counter.increment();

    Mockito.verify(deliveryChannel).publish(updateMessageCaptor.capture());
    Mockito.verifyNoMoreInteractions(deliveryChannel);

    GCounterState updateMessage = updateMessageCaptor.getValue();

    assertEquals("Update message identifier should be the same as the set's",
        counter.getIdentifier(), updateMessage.getIdentifier());
    assertTrue("Update version should be as expected",
        updateMessage.getVersionVector().identical(expectedVersionVector));
  }

  /**
   * Test snapshot of a zero counter.
   */
  @Test
  public void testSnapshot_Zero() {
    LOGGER.log(Level.INFO, "testSnapshot_Zero: Test snapshot of a zero counter.");
    final GCounter<Integer, Object> counter = getCounter();

    final VersionVector<Object, Integer> expectedVersionVector =
        new HashVersionVector<>(new IntegerVersion());

    GCounterState<Integer, Object> state = counter.snapshot();

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
   * Test snapshot of an incremented counter.
   */
  @Test
  public void testSnapshot_Incremented() {
    LOGGER.log(Level.INFO, "testSnapshot_Zero: Test snapshot of an incremented counter.");
    final GCounter<Integer, Object> counter = getCounter();

    final VersionVector<Object, Integer> expectedVersionVector =
        new HashVersionVector<>(new IntegerVersion());
    expectedVersionVector.init(counter.getIdentifier());
    expectedVersionVector.increment(counter.getIdentifier());

    counter.increment();
    GCounterState<Integer, Object> state = counter.snapshot();

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
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_NoChange() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_NoChange: Test update with no changes.");

    final GCounter<Integer, Object> counter1 = getCounter();
    final GCounter<Integer, Object> counter2 = getCounter();

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
   * Test update with a local increment.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_LocalIncrement() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_LocalIncrement: Test update with a local increment.");

    final GCounter<Integer, Object> counter1 = getCounter();
    final GCounter<Integer, Object> counter2 = getCounter();

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
   * Test update with a remote increment.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_RemoteIncrement() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_RemoteIncrement: Test update with a remote increment.");

    final GCounter<Integer, Object> counter1 = getCounter();
    final GCounter<Integer, Object> counter2 = getCounter();

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
    assertEquals("counter2 should have seen 1 increment", getValue(1), counter2.value());

    counter2.update(counter1.snapshot());
    assertTrue("The counters should be identical after bi-directional update",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical after bi-directional update", counter1.value(),
        counter2.value());
    assertEquals("counter1 should have seen 1 increment", getValue(1), counter1.value());
    assertEquals("counter2 should have seen 1 increment", getValue(1), counter2.value());
  }

  /**
   * Test update with concurrent changes.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_BothIncrement() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_BothIncrement: Test update with concurrent increments.");

    final GCounter<Integer, Object> counter1 = getCounter();
    final GCounter<Integer, Object> counter2 = getCounter();

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
    assertEquals("counter2 should have seen 1 increment", getValue(1), counter2.value());

    counter2.update(counter1.snapshot());
    assertTrue("The counters should be identical after bi-directional update",
        counter1.getVersion().identical(counter2.getVersion()));
    assertEquals("The counters should be identical after bi-directional update", counter1.value(),
        counter2.value());
    assertEquals("counter1 should have seen 2 increment", getValue(2), counter1.value());
    assertEquals("counter2 should have seen 2 increment", getValue(2), counter2.value());
  }

}
