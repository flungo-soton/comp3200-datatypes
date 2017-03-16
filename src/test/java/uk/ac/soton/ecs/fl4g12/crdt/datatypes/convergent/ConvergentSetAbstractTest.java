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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.order.HashVersionVector;
import uk.ac.soton.ecs.fl4g12.crdt.order.LogicalVersion;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.StatefulUpdatable;

/**
 * Tests for convergent {@link StatefulUpdatable} based {@link Set} implementations.
 *
 * @param <E> the type of values stored in the {@link Set}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp within the {@link VersionVector}
 * @param <U> the type of snapshot made from this state.
 * @param <S> the type of {@link StatefulUpdatable} based {@link Set} being tested.
 */
public abstract class ConvergentSetAbstractTest<E, K, T extends Comparable<T>, U extends SetState<E, K, T>, S extends Set<E> & StatefulUpdatable<K, T, U>> {

  private static final Logger LOGGER = Logger.getLogger(ConvergentSetAbstractTest.class.getName());

  public static final int MAX_OPERATIONS = 10;

  /**
   * Get the {@linkplain Set} instance for testing.
   *
   * @return a {@link Set} to be tested.
   */
  protected abstract S getSet();

  /**
   * Get a random element to store in the {@linkplain Set}. {@code i} is in order to denote unique
   * elements.
   *
   * @param i the iteration number.
   * @return a value to store in the set.
   */
  protected abstract E getElement(int i);

  protected abstract LogicalVersion<T> getZeroVersion();

  /**
   * Ensure that when an element is added, that the change is published to the
   * {@linkplain DeliveryChannel}.
   */
  @Test
  public void testAdd_Publish() {
    LOGGER.log(Level.INFO, "testAdd_Publish: "
        + "Ensure that when an element is added, that the change is published to the DeliveryChannel.");
    final S set = getSet();

    final VersionVector<K, T> expectedVersionVector =
        new HashVersionVector<>(getZeroVersion(), false);
    expectedVersionVector.init(set.getIdentifier());

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      expectedVersionVector.increment(set.getIdentifier());
      final DeliveryChannel<K, U> deliveryChannel = set.getDeliveryChannel();

      final E element = getElement(i);
      Mockito.reset(deliveryChannel);
      set.add(element);
      Mockito.verify(deliveryChannel).publish(Mockito.argThat(new ArgumentMatcher<U>() {
        @Override
        public boolean matches(U t) {
          if (!t.getIdentifier().equals(set.getIdentifier())) {
            return false;
          }
          if (!t.getVersionVector().identical(expectedVersionVector)) {
            return false;
          }
          if (!t.getState().contains(element)) {
            return false;
          }
          return true;
        }
      }));
      Mockito.verifyNoMoreInteractions(deliveryChannel);
    }
  }

  /**
   * Ensure that when a duplicate element is added, that no change is published to the
   * {@linkplain DeliveryChannel}.
   */
  @Test
  public void testAdd_Duplicate_NoPublish() {
    LOGGER.log(Level.INFO, "testAdd_Publish: "
        + "Ensure that when a duplicate element is added, that no change is published to the DeliveryChannel.");
    final S set = getSet();

    final VersionVector<K, T> expectedVersionVector =
        new HashVersionVector<>(getZeroVersion(), false);
    expectedVersionVector.init(set.getIdentifier());

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      expectedVersionVector.increment(set.getIdentifier());
      final DeliveryChannel<K, U> deliveryChannel = set.getDeliveryChannel();

      final E element = getElement(i);
      set.add(element);
      Mockito.reset(deliveryChannel);
      set.add(element);
      Mockito.verifyZeroInteractions(deliveryChannel);
    }
  }

  /**
   * Ensure that when an elements are added, that the change is published to the
   * {@linkplain DeliveryChannel}.
   */
  @Test
  public void testAddAll_Publish() {
    LOGGER.log(Level.INFO, "testAddAll_Publish: "
        + "Ensure that when an elements are added, that the change is published to the DeliveryChannel.");
    final S set = getSet();

    final VersionVector<K, T> expectedVersionVector =
        new HashVersionVector<>(getZeroVersion(), false);
    expectedVersionVector.init(set.getIdentifier());

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      expectedVersionVector.increment(set.getIdentifier());
      final DeliveryChannel<K, U> deliveryChannel = set.getDeliveryChannel();

      final E element = getElement(i);
      Mockito.reset(deliveryChannel);
      set.add(element);
      Mockito.verify(deliveryChannel).publish(Mockito.argThat(new ArgumentMatcher<U>() {
        @Override
        public boolean matches(U t) {
          if (!t.getIdentifier().equals(set.getIdentifier())) {
            return false;
          }
          if (!t.getVersionVector().identical(expectedVersionVector)) {
            return false;
          }
          if (!t.getState().contains(element)) {
            return false;
          }
          return true;
        }
      }));
      Mockito.verifyNoMoreInteractions(deliveryChannel);
    }
  }

  /**
   * Ensure that when duplicates element is added, that no change is published to the
   * {@linkplain DeliveryChannel}.
   */
  @Test
  public void testAddAll_Duplicate_NoPublish() {
    LOGGER.log(Level.INFO, "testAdd_Publish: "
        + "Ensure that when duplicate elements are added, that no change is published to the DeliveryChannel.");
    final S set = getSet();

    final VersionVector<K, T> expectedVersionVector =
        new HashVersionVector<>(getZeroVersion(), false);
    expectedVersionVector.init(set.getIdentifier());

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      expectedVersionVector.increment(set.getIdentifier());
      final DeliveryChannel<K, U> deliveryChannel = set.getDeliveryChannel();

      final Collection<E> elements = new ArrayList<>();
      for (int j = 0; j < MAX_OPERATIONS; j++) {
        elements.add(getElement(i * MAX_OPERATIONS + j));
      }
      set.addAll(elements);
      Mockito.reset(deliveryChannel);
      set.addAll(elements);
      Mockito.verifyZeroInteractions(deliveryChannel);
    }
  }

  /**
   * Test snapshot of the initial {@linkplain GSet} state.
   */
  @Test
  public void testSnapshot_Initial() {
    LOGGER.log(Level.INFO, "testSnapshot_Initial: Test snapshot of the initial set state.");
    final S set = getSet();

    final VersionVector<K, T> expectedVersionVector =
        new HashVersionVector<>(getZeroVersion(), false);

    U state = set.snapshot();

    assertEquals("state identifier should be the same as the set", set.getIdentifier(),
        state.getIdentifier());
    assertTrue("The VersionVector should match the expectation",
        state.getVersionVector().identical(expectedVersionVector));
    assertTrue("The state should be empty", state.getState().isEmpty());

    // Check that the state snapshot is immutable by changes to the set.
    set.add(getElement(0));

    assertEquals("state identifier should be the same as the set", set.getIdentifier(),
        state.getIdentifier());
    assertTrue("The VersionVector should still match the expectation",
        state.getVersionVector().identical(expectedVersionVector));
    assertTrue("The state should still be empty", state.getState().isEmpty());
  }

  /**
   * Test snapshot of a set with an element added.
   */
  @Test
  public void testSnapshot_Add() {
    LOGGER.log(Level.INFO, "testSnapshot_Zero: Test snapshot of a set with an element added.");
    final S set = getSet();

    final VersionVector<K, T> expectedVersionVector =
        new HashVersionVector<>(getZeroVersion(), false);
    expectedVersionVector.init(set.getIdentifier());
    expectedVersionVector.increment(set.getIdentifier());

    set.add(getElement(0));
    U state = set.snapshot();

    assertEquals("state identifier should be the same as the set", set.getIdentifier(),
        state.getIdentifier());
    assertTrue("The VersionVector should match the expectation",
        state.getVersionVector().identical(expectedVersionVector));

    // Check that the state snapshot is immutable by changes to the set.
    set.add(getElement(1));

    assertEquals("state identifier should be the same as the set", set.getIdentifier(),
        state.getIdentifier());
    assertTrue("The VersionVector should still match the expectation",
        state.getVersionVector().identical(expectedVersionVector));
  }

  /**
   * Test snapshot of a set with an element added.
   */
  @Test
  public void testSnapshot_AddAll() {
    LOGGER.log(Level.INFO, "testSnapshot_Zero: Test snapshot of a set with an element added.");
    final S set = getSet();

    final VersionVector<K, T> expectedVersionVector =
        new HashVersionVector<>(getZeroVersion(), false);
    expectedVersionVector.init(set.getIdentifier());
    expectedVersionVector.increment(set.getIdentifier());

    set.addAll(Arrays.asList(getElement(0), getElement(1), getElement(2)));
    U state = set.snapshot();

    assertEquals("state identifier should be the same as the set", set.getIdentifier(),
        state.getIdentifier());
    assertTrue("The VersionVector should match the expectation",
        state.getVersionVector().identical(expectedVersionVector));

    // Check that the state snapshot is immutable by changes to the set.
    set.addAll(Arrays.asList(getElement(3), getElement(4), getElement(5)));

    assertEquals("state identifier should be the same as the set", set.getIdentifier(),
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

    final S set1 = getSet();
    final S set2 = getSet();

    assertTrue("The version vectors should be identical to start with",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The set should be identical to start with", set1.snapshot().getState(),
        set2.snapshot().getState());

    set1.update(set2.snapshot());
    assertTrue("The version vectors should be identical after update",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The sets should be identical after update", set1.snapshot().getState(),
        set2.snapshot().getState());

    set2.update(set1.snapshot());
    assertTrue("The version vectors should be identical after bi-directional update",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The sets should be identical after update", set1.snapshot().getState(),
        set2.snapshot().getState());
  }

  /**
   * Test update with a local increment.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_LocalAdd() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_LocalIncrement: Test update with a local increment.");

    final S set1 = getSet();
    final S set2 = getSet();

    final HashSet<E> comparison0 = new HashSet<>();
    final HashSet<E> comparison1 = new HashSet<>(Arrays.asList(getElement(1)));

    assertTrue("The version vectors should be identical to start with",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The sets should be identical to start with", set1.snapshot().getState(),
        set2.snapshot().getState());
    assertEquals("set1 should have seen 0 elements", comparison0, set1.snapshot().getState());
    assertEquals("set2 should have seen 0 elements", comparison0, set2.snapshot().getState());

    set1.add(getElement(1));
    assertTrue("set2 should have happenedBefore set1",
        set2.getVersion().happenedBefore(set1.getVersion()));

    set1.update(set2.snapshot());
    assertTrue("set2 should have happenedBefore set1",
        set2.getVersion().happenedBefore(set1.getVersion()));
    assertEquals("set1 should have seen 1 element", comparison1, set1.snapshot().getState());
    assertEquals("set2 should have seen 0 elements", comparison0, set2.snapshot().getState());

    set2.update(set1.snapshot());
    assertTrue("The version vectors should be identical after bi-directional update",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The sets should be identical after bi-directional update",
        set1.snapshot().getState(), set2.snapshot().getState());
    assertEquals("set1 should have seen 1 element", comparison1, set1.snapshot().getState());
    assertEquals("set2 should have seen 1 element", comparison1, set2.snapshot().getState());
  }

  /**
   * Test update with a remote increment.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_RemoteIncrement() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_RemoteIncrement: Test update with a remote increment.");

    final S set1 = getSet();
    final S set2 = getSet();

    final HashSet<E> comparison0 = new HashSet<>();
    final HashSet<E> comparison1 = new HashSet<>(Arrays.asList(getElement(1)));

    assertTrue("The version vectors should be identical to start with",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The sets should be identical to start with", set1.snapshot().getState(),
        set2.snapshot().getState());
    assertEquals("set1 should have seen 0 elements", comparison0, set1.snapshot().getState());
    assertEquals("set2 should have seen 0 elements", comparison0, set2.snapshot().getState());

    set2.add(getElement(1));
    assertTrue("set1 should have happenedBefore set2",
        set1.getVersion().happenedBefore(set2.getVersion()));

    set1.update(set2.snapshot());
    assertTrue("The version vectors should be identical after update",
        set2.getVersion().identical(set1.getVersion()));
    assertEquals("set1 should have seen 1 element", comparison1, set1.snapshot().getState());
    assertEquals("set2 should have seen 1 element", comparison1, set2.snapshot().getState());

    set2.update(set1.snapshot());
    assertTrue("The version vectors should be identical after bi-directional update",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The sets should be identical after bi-directional update",
        set1.snapshot().getState(), set2.snapshot().getState());
    assertEquals("set1 should have seen 1 element", comparison1, set1.snapshot().getState());
    assertEquals("set2 should have seen 1 element", comparison1, set2.snapshot().getState());
  }

  /**
   * Test update with concurrent changes.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_BothAdd() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_BothIncrement: Test update with concurrent increments.");

    final S set1 = getSet();
    final S set2 = getSet();

    final HashSet<E> comparison0 = new HashSet<>();
    final HashSet<E> comparison1 = new HashSet<>(Arrays.asList(getElement(2)));
    final HashSet<E> comparison2 = new HashSet<>(Arrays.asList(getElement(1), getElement(2)));

    assertTrue("The version vectors should be identical to start with",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The sets should be identical to start with", set1.snapshot().getState(),
        set2.snapshot().getState());
    assertEquals("set1 should have seen 0 elements", comparison0, set1.snapshot().getState());
    assertEquals("set2 should have seen 0 elements", comparison0, set2.snapshot().getState());

    set1.add(getElement(1));
    set2.add(getElement(2));
    assertTrue("set1 should be concurrent with set2",
        set1.getVersion().concurrentWith(set2.getVersion()));

    set1.update(set2.snapshot());
    assertTrue("set2 should have happenedBefore set1",
        set2.getVersion().happenedBefore(set1.getVersion()));
    assertEquals("set1 should have seen 2 elements", comparison2, set1.snapshot().getState());
    assertEquals("set2 should have seen 1 element", comparison1, set2.snapshot().getState());

    set2.update(set1.snapshot());
    assertTrue("The sets should be identical after bi-directional update",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The sets should be identical after bi-directional update",
        set1.snapshot().getState(), set2.snapshot().getState());
    assertEquals("set1 should have seen 2 elements", comparison2, set1.snapshot().getState());
    assertEquals("set2 should have seen 2 elements", comparison2, set2.snapshot().getState());
  }

}
