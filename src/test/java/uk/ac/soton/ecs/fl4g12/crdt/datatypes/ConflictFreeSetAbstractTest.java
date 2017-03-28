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
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.mockito.Mockito;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Abstract tests for {@linkplain CRDT} {@linkplain Set} implementations. Tests that the outcomes of
 * operations are conflict-free.
 *
 * It is assumed that the update messages sent by removeAll, retain, retainAll and clear are
 * effective to remove and so these are currently not tested explicitly.
 *
 * @param <E> the type of values stored in the {@link Set}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp within the {@link VersionVector}
 * @param <U> the type of snapshot made from this state.
 * @param <S> the type of {@link Set} being tested.
 */
public abstract class ConflictFreeSetAbstractTest<E, K, T extends Comparable<T>, U extends VersionedUpdateMessage<K, T>, S extends Set<E> & VersionedUpdatable<K, T, U>>
    extends GrowableConflictFreeSetAbstractTest<E, K, T, U, S> {

  private static final Logger LOGGER =
      Logger.getLogger(ConflictFreeSetAbstractTest.class.getName());

  private final boolean addWins;

  /**
   * Instantiate the abstract tests for {@linkplain CRDT} {@linkplain Set} implementations.
   *
   * @param addWins {@code true} if the implementation being tested is an add-wins implementation or
   *        {@code false} if its a remove wins implementation.
   */
  public ConflictFreeSetAbstractTest(boolean addWins) {
    this.addWins = addWins;
  }

  /**
   * Test update with a local remove.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_LocalRemove() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_LocalRemove: Test update with a local remove.");

    final S set1 = getSet();
    final DeliveryChannel<K, U> delivery1 = set1.getDeliveryChannel();
    final S set2 = getSet();

    // Setup the initial states
    final HashSet<E> initial = new HashSet<>(Arrays.asList(getElement(0), getElement(1)));
    set1.addAll(initial);
    Mockito.verify(delivery1).publish(updateMessageCaptor.capture());
    Mockito.reset(delivery1);
    set2.update(updateMessageCaptor.getValue());

    final HashSet<E> mutated = new HashSet<>(Arrays.asList(getElement(1)));

    assertInitialState(initial, set1, set2);

    set1.remove(getElement(0));
    Mockito.verify(delivery1).publish(updateMessageCaptor.capture());
    assertTrue("set2 should have happenedBefore set1",
        set2.getVersion().happenedBefore(set1.getVersion()));

    intermediateDelivery(set1, set2);
    assertTrue("set2 should have happenedBefore set1",
        set2.getVersion().happenedBefore(set1.getVersion()));
    assertIntermediateState(mutated, set1, initial, set2);

    set2.update(updateMessageCaptor.getValue());
    assertFinalState(mutated, set1, set2);
  }

  /**
   * Test update with a remote remove.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_RemoteRemove() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_RemoteRemove: Test update with a remote remove.");

    final S set1 = getSet();
    final DeliveryChannel<K, U> delivery1 = set1.getDeliveryChannel();
    final S set2 = getSet();
    final DeliveryChannel<K, U> delivery2 = set2.getDeliveryChannel();

    // Setup the initial states
    final HashSet<E> initial = new HashSet<>(Arrays.asList(getElement(0), getElement(1)));
    set1.addAll(initial);
    Mockito.verify(delivery1).publish(updateMessageCaptor.capture());
    Mockito.reset(delivery1);
    set2.update(updateMessageCaptor.getValue());

    final HashSet<E> mutated = new HashSet<>(Arrays.asList(getElement(0)));

    assertInitialState(initial, set1, set2);

    set2.remove(getElement(1));
    Mockito.verify(delivery2).publish(updateMessageCaptor.capture());
    assertTrue("set1 should have happenedBefore set2",
        set1.getVersion().happenedBefore(set2.getVersion()));

    set1.update(updateMessageCaptor.getValue());
    assertTrue("The version vectors should be identical after update",
        set2.getVersion().identical(set1.getVersion()));
    assertIntermediateState(mutated, set1, mutated, set2);

    intermediateDelivery(set2, set1);
    assertFinalState(mutated, set1, set2);
  }

  /**
   * Test update with concurrent removals.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_BothRemove() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_BothRemove: Test update with concurrent removals.");

    final S set1 = getSet();
    final DeliveryChannel<K, U> delivery1 = set1.getDeliveryChannel();
    final S set2 = getSet();
    final DeliveryChannel<K, U> delivery2 = set2.getDeliveryChannel();

    // Setup the initial states
    final HashSet<E> initial =
        new HashSet<>(Arrays.asList(getElement(0), getElement(1), getElement(2)));
    set1.addAll(initial);
    Mockito.verify(delivery1).publish(updateMessageCaptor.capture());
    Mockito.reset(delivery1);
    set2.update(updateMessageCaptor.getValue());

    final HashSet<E> removed1 = new HashSet<>(Arrays.asList(getElement(0), getElement(2)));
    final HashSet<E> removed2 = new HashSet<>(Arrays.asList(getElement(2)));

    assertInitialState(initial, set1, set2);

    set1.remove(getElement(0));
    Mockito.verify(delivery1).publish(updateMessageCaptor.capture());
    final U update1 = updateMessageCaptor.getValue();
    set2.remove(getElement(1));
    Mockito.verify(delivery2).publish(updateMessageCaptor.capture());
    final U update2 = updateMessageCaptor.getValue();
    assertTrue("set1 should be concurrent with set2",
        set1.getVersion().concurrentWith(set2.getVersion()));

    set1.update(update2);
    assertTrue("set2 should have happenedBefore set1",
        set2.getVersion().happenedBefore(set1.getVersion()));
    assertIntermediateState(removed2, set1, removed1, set2);

    set2.update(update1);
    assertFinalState(removed2, set1, set2);
  }

  /**
   * Test update with concurrent removals of the same item.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_BothRemove_Same() throws Exception {
    LOGGER.log(Level.INFO,
        "testUpdate_BothRemove_Same: Test update with concurrent removals of the same item.");

    final S set1 = getSet();
    final DeliveryChannel<K, U> delivery1 = set1.getDeliveryChannel();
    final S set2 = getSet();
    final DeliveryChannel<K, U> delivery2 = set2.getDeliveryChannel();

    // Setup the initial states
    final HashSet<E> initial = new HashSet<>(Arrays.asList(getElement(0), getElement(1)));
    set1.addAll(initial);
    Mockito.verify(delivery1).publish(updateMessageCaptor.capture());
    Mockito.reset(delivery1);
    set2.update(updateMessageCaptor.getValue());

    final HashSet<E> removed = new HashSet<>(Arrays.asList(getElement(0)));

    assertInitialState(initial, set1, set2);

    set1.remove(getElement(1));
    Mockito.verify(delivery1).publish(updateMessageCaptor.capture());
    final U update1 = updateMessageCaptor.getValue();
    set2.remove(getElement(1));
    Mockito.verify(delivery2).publish(updateMessageCaptor.capture());
    final U update2 = updateMessageCaptor.getValue();
    assertTrue("set1 should be concurrent with set2",
        set1.getVersion().concurrentWith(set2.getVersion()));

    set1.update(update2);
    assertTrue("set2 should have happenedBefore set1",
        set2.getVersion().happenedBefore(set1.getVersion()));
    assertIntermediateState(removed, set1, removed, set2);

    set2.update(update1);
    assertFinalState(removed, set1, set2);
  }

  /**
   * Test update with a local add and remote remove of different elements.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_AddRemove() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_AddRemove: "
        + "Test update with a local add and remote remove of different elements.");

    final S set1 = getSet();
    final DeliveryChannel<K, U> delivery1 = set1.getDeliveryChannel();
    final S set2 = getSet();
    final DeliveryChannel<K, U> delivery2 = set2.getDeliveryChannel();

    // Setup the initial states
    final HashSet<E> initial = new HashSet<>(Arrays.asList(getElement(0), getElement(1)));
    set1.addAll(initial);
    Mockito.verify(delivery1).publish(updateMessageCaptor.capture());
    Mockito.reset(delivery1);
    set2.update(updateMessageCaptor.getValue());

    final HashSet<E> removed = new HashSet<>(Arrays.asList(getElement(0)));
    final HashSet<E> both = new HashSet<>(Arrays.asList(getElement(0), getElement(2)));

    assertInitialState(initial, set1, set2);

    set1.add(getElement(2));
    Mockito.verify(delivery1).publish(updateMessageCaptor.capture());
    final U update1 = updateMessageCaptor.getValue();
    set2.remove(getElement(1));
    Mockito.verify(delivery2).publish(updateMessageCaptor.capture());
    final U update2 = updateMessageCaptor.getValue();
    assertTrue("set1 should be concurrent with set2",
        set1.getVersion().concurrentWith(set2.getVersion()));

    set1.update(update2);
    assertTrue("set2 should have happenedBefore set1",
        set2.getVersion().happenedBefore(set1.getVersion()));
    assertIntermediateState(both, set1, removed, set2);

    set2.update(update1);
    assertFinalState(both, set1, set2);
  }

  /**
   * Test update with a local remove and remote add of different elements.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_RemoveAdd() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_RemoveAdd: "
        + "Test update with a local remove and remote add of different elements.");

    final S set1 = getSet();
    final DeliveryChannel<K, U> delivery1 = set1.getDeliveryChannel();
    final S set2 = getSet();
    final DeliveryChannel<K, U> delivery2 = set2.getDeliveryChannel();

    // Setup the initial states
    final HashSet<E> initial = new HashSet<>(Arrays.asList(getElement(0), getElement(1)));
    set1.addAll(initial);
    Mockito.verify(delivery1).publish(updateMessageCaptor.capture());
    Mockito.reset(delivery1);
    set2.update(updateMessageCaptor.getValue());

    final HashSet<E> added =
        new HashSet<>(Arrays.asList(getElement(0), getElement(1), getElement(2)));
    final HashSet<E> both = new HashSet<>(Arrays.asList(getElement(0), getElement(2)));

    assertInitialState(initial, set1, set2);

    set1.remove(getElement(1));
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
    assertIntermediateState(both, set1, added, set2);

    set2.update(update1);
    assertFinalState(both, set1, set2);
  }

  /**
   * Test update with a local add and remote remove of the same element (not already in the set).
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_AddRemove_Same() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_AddRemove: "
        + "Test update with a local add and remote remove of different elements.");

    final S set1 = getSet();
    final DeliveryChannel<K, U> delivery1 = set1.getDeliveryChannel();
    final S set2 = getSet();
    final DeliveryChannel<K, U> delivery2 = set2.getDeliveryChannel();

    // Setup the initial states
    final HashSet<E> initial = new HashSet<>(Arrays.asList(getElement(0)));
    set1.addAll(initial);
    Mockito.verify(delivery1).publish(updateMessageCaptor.capture());
    Mockito.reset(delivery1);
    set2.update(updateMessageCaptor.getValue());

    final HashSet<E> added = new HashSet<>(Arrays.asList(getElement(0), getElement(1)));

    final HashSet<E> expected;
    if (addWins) {
      expected = added;
    } else {
      expected = initial;
    }

    assertInitialState(initial, set1, set2);

    set1.add(getElement(1));
    Mockito.verify(delivery1).publish(updateMessageCaptor.capture());
    final U update1 = updateMessageCaptor.getValue();
    set2.remove(getElement(1));
    Mockito.verify(delivery2).publish(updateMessageCaptor.capture());
    final U update2 = updateMessageCaptor.getValue();
    assertTrue("set1 should be concurrent with set2",
        set1.getVersion().concurrentWith(set2.getVersion()));

    set1.update(update2);
    assertTrue("set2 should have happenedBefore set1",
        set2.getVersion().happenedBefore(set1.getVersion()));
    assertIntermediateState(expected, set1, initial, set2);

    set2.update(update1);
    assertFinalState(expected, set1, set2);
  }

  /**
   * Test update with a local remove and remote add of the same element (not already in the set).
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_RemoveAdd_Same() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_AddRemove: "
        + "Test update with a local add and remote remove of different elements.");

    final S set1 = getSet();
    final DeliveryChannel<K, U> delivery1 = set1.getDeliveryChannel();
    final S set2 = getSet();
    final DeliveryChannel<K, U> delivery2 = set2.getDeliveryChannel();

    // Setup the initial states
    final HashSet<E> initial = new HashSet<>(Arrays.asList(getElement(0)));
    set1.addAll(initial);
    Mockito.verify(delivery1).publish(updateMessageCaptor.capture());
    Mockito.reset(delivery1);
    set2.update(updateMessageCaptor.getValue());

    final HashSet<E> added = new HashSet<>(Arrays.asList(getElement(0), getElement(1)));

    final HashSet<E> expected;
    if (addWins) {
      expected = added;
    } else {
      expected = initial;
    }

    assertInitialState(initial, set1, set2);

    set1.remove(getElement(1));
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
    assertIntermediateState(expected, set1, added, set2);

    set2.update(update1);
    assertFinalState(expected, set1, set2);
  }
}
