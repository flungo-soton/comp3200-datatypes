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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.order.Version;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Abstract tests for ensuring the conflict-free replicability in growable {@linkplain CRDT}
 * {@linkplain Set}s.
 *
 * @param <E> the type of values stored in the {@link Set}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp within the {@link VersionVector}
 * @param <U> the type of snapshot made from this state.
 * @param <S> the type of {@link Set} being tested.
 */
public abstract class GrowableConflictFreeSetAbstractTest<E, K, T extends Comparable<T>, U extends VersionedUpdateMessage<K, ? extends Version>, S extends Set<E> & VersionedUpdatable<K, VersionVector<K, T>, U>>
    implements SetTestInterface<E, S> {

  private static final Logger LOGGER =
      Logger.getLogger(GrowableConflictFreeSetAbstractTest.class.getName());

  @Captor
  public ArgumentCaptor<U> updateMessageCaptor;

  @Before
  public final void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  protected final void assertInitialState(Set<E> initial, S set1, S set2) {
    assertTrue("The version vectors should be identical to start with",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("set1 should have the initial state", initial, set1);
    assertEquals("set2 should have the initial state", initial, set2);
  }

  protected final void assertIntermediateState(Set<E> set1expected, S set1, Set<E> set2expected,
      S set2) {
    assertEquals("set1 should match the expectation", set1expected, set1);
    assertEquals("set2 should match the expectation", set2expected, set2);
  }

  protected final void assertFinalState(Set<E> expected, S set1, S set2) {
    assertTrue("The version vectors should be identical after bi-directional update",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("set1 should match expected final state", expected, set1);
    assertEquals("set2 should match expected final state", expected, set2);
  }

  /**
   * Deliver an update from source to destination. This is provided to allow convergent
   * implementations to deliver changes to set1 where no change to create an update for commutative
   * implementations. This is only used on tests which are non-concurrent.
   *
   * @param destination the set to be updated.
   * @param source the set to get state from.
   * @throws Exception to fail the test.
   */
  protected void intermediateDelivery(S destination, S source) throws Exception {
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

    final S set1 = getSet();
    final S set2 = getSet();

    assertTrue("The version vectors should be identical to start with",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The set should be identical to start with", set1, set2);

    intermediateDelivery(set1, set2);
    assertTrue("The version vectors should be identical after update",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The sets should be identical after update", set1, set2);

    intermediateDelivery(set2, set1);
    assertTrue("The version vectors should be identical after bi-directional update",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The sets should be identical after update", set1, set2);
  }

  /**
   * Test update with a local addition.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_LocalAdd() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_LocalAdd: Test update with a local addition.");

    final S set1 = getSet();
    final DeliveryChannel<K, U> delivery1 = set1.getDeliveryChannel();
    final S set2 = getSet();

    final HashSet<E> initial = new HashSet<>();
    final HashSet<E> added = new HashSet<>(Arrays.asList(getElement(1)));

    assertInitialState(initial, set1, set2);

    set1.add(getElement(1));
    Mockito.verify(delivery1).publish(updateMessageCaptor.capture());
    assertTrue("set2 should have happenedBefore set1",
        set2.getVersion().happenedBefore(set1.getVersion()));

    intermediateDelivery(set1, set2);
    assertTrue("set2 should have happenedBefore set1",
        set2.getVersion().happenedBefore(set1.getVersion()));
    assertIntermediateState(added, set1, initial, set2);

    set2.update(updateMessageCaptor.getValue());
    assertFinalState(added, set1, set2);
  }

  /**
   * Test update with a remote addition.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_RemoteAdd() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_RemoteAdd: Test update with a remote addition.");

    final S set1 = getSet();
    final S set2 = getSet();
    final DeliveryChannel<K, U> delivery2 = set2.getDeliveryChannel();

    final HashSet<E> initial = new HashSet<>();
    final HashSet<E> added = new HashSet<>(Arrays.asList(getElement(1)));

    assertInitialState(initial, set1, set2);

    set2.add(getElement(1));
    Mockito.verify(delivery2).publish(updateMessageCaptor.capture());
    assertTrue("set1 should have happenedBefore set2",
        set1.getVersion().happenedBefore(set2.getVersion()));

    set1.update(updateMessageCaptor.getValue());
    assertTrue("The version vectors should be identical after update",
        set2.getVersion().identical(set1.getVersion()));
    assertIntermediateState(added, set1, added, set2);

    intermediateDelivery(set1, set2);
    assertFinalState(added, set1, set2);
  }

  /**
   * Test update with concurrent additions.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_BothAdd() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_BothAdd: Test update with concurrent additions.");

    final S set1 = getSet();
    final DeliveryChannel<K, U> delivery1 = set1.getDeliveryChannel();
    final S set2 = getSet();
    final DeliveryChannel<K, U> delivery2 = set2.getDeliveryChannel();

    final HashSet<E> initial = new HashSet<>();
    final HashSet<E> added1 = new HashSet<>(Arrays.asList(getElement(2)));
    final HashSet<E> added2 = new HashSet<>(Arrays.asList(getElement(1), getElement(2)));

    assertInitialState(initial, set1, set2);

    set1.add(getElement(1));
    Mockito.verify(delivery1).publish(updateMessageCaptor.capture());
    final U update1 = updateMessageCaptor.getValue();
    set2.add(getElement(2));
    Mockito.verify(delivery2).publish(updateMessageCaptor.capture());
    final U update2 = updateMessageCaptor.getValue();
    assertTrue("set1 should be concurrent with set2",
        set1.getVersion().concurrentWith(set2.getVersion()));

    set1.update(update2);
    assertTrue("set2 should have happenedBefore set1",
        set2.getVersion().happenedBefore(set1.getVersion()));
    assertIntermediateState(added2, set1, added1, set2);

    set2.update(update1);
    assertFinalState(added2, set1, set2);
  }

  /**
   * Test update with concurrent additions of the same element.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_BothAdd_Same() throws Exception {
    LOGGER.log(Level.INFO,
        "testUpdate_BothAdd_Same: Test update with concurrent additions of the same element.");

    final S set1 = getSet();
    final DeliveryChannel<K, U> delivery1 = set1.getDeliveryChannel();
    final S set2 = getSet();
    final DeliveryChannel<K, U> delivery2 = set2.getDeliveryChannel();


    final HashSet<E> initial = new HashSet<>();
    final HashSet<E> added = new HashSet<>(Arrays.asList(getElement(1)));

    assertInitialState(initial, set1, set2);

    set1.add(getElement(1));
    Mockito.verify(delivery1).publish(updateMessageCaptor.capture());
    final U update1 = updateMessageCaptor.getValue();
    set2.add(getElement(1));
    Mockito.verify(delivery2).publish(updateMessageCaptor.capture());
    final U update2 = updateMessageCaptor.getValue();
    assertTrue("set1 should be concurrent with set2",
        set1.getVersion().concurrentWith(set2.getVersion()));

    set1.update(update2);
    assertTrue("set2 should have happenedBefore set1",
        set2.getVersion().happenedBefore(set1.getVersion()));
    assertIntermediateState(added, set1, added, set2);

    set2.update(update1);
    assertFinalState(added, set1, set2);
  }
}
