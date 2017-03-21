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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import org.mockito.Mockito;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.Updatable;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.UpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.order.HashVersionVector;
import uk.ac.soton.ecs.fl4g12.crdt.order.LogicalVersion;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Tests for {@link Updatable} {@link Set} implementations.
 *
 * @param <E> the type of values stored in the {@link Set}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp within the {@link VersionVector}
 * @param <U> the type of snapshot made from this state.
 * @param <S> the type of {@link Updatable} based {@link Set} being tested.
 */
public abstract class UpdatableSetAbstractTest<E, K, T extends Comparable<T>, U extends UpdateMessage<K, ?>, S extends Set<E> & VersionedUpdatable<K, T, U>> {

  private static final Logger LOGGER = Logger.getLogger(UpdatableSetAbstractTest.class.getName());

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
   * Called within the provided tests to allow additional assertions to be made. All implementations
   * should call their super to ensure that all assertions are made.
   *
   * @param testCase the {@link UpdatableSetTestCase} which the assertions are being performed
   *        within.
   * @param set the {@link Set} instance being tested.
   * @param objects the objects for assertions to be made on. This differs for each case and so
   *        {@link UpdatableSetTestCase} documentation should be consulted for the details.
   */
  protected void makeTestAssertions(UpdatableSetTestCase testCase, S set, Object... objects) {}

  /**
   * Test {@link Set#add(java.lang.Object)} twice.
   *
   * @see UpdatableSetTestCase#ADD_DUPLICATE
   */
  @Test
  public void testAdd_Duplicate() {
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

      makeTestAssertions(UpdatableSetTestCase.ADD_DUPLICATE, set, element);
    }
  }

  /**
   * Test {@link Set#addAll(java.util.Collection)} twice.
   *
   * @see UpdatableSetTestCase#ADD_ALL_DUPLICATE
   */
  @Test
  public void testAddAll_Duplicate() {
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

      makeTestAssertions(UpdatableSetTestCase.ADD_ALL_DUPLICATE, set, elements);
    }
  }

  /**
   * Test {@link Set#remove(java.lang.Object)} twice.
   *
   * This test needs to be overridden with the {@code @Test} annotation and a call to this
   * implementation (by {@code super.testRemoveAll_Duplicate_NoPublish()}) in order for it to be
   * activated.
   *
   * @see UpdatableSetTestCase#REMOVE_DUPLICATE
   */
  public void testRemove_Duplicate() {
    LOGGER.log(Level.INFO,
        "testRemove_Duplicate: "
            + "Ensure that when an element that has already been removed is removed, "
            + "that no change is published to the DeliveryChannel.");
    final S set = getSet();

    // Populate with elements
    for (int i = 0; i < MAX_OPERATIONS; i++) {
      set.add(getElement(i));
    }

    final VersionVector<K, T> expectedVersionVector = set.getVersion().copy();

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      expectedVersionVector.increment(set.getIdentifier());
      final DeliveryChannel<K, U> deliveryChannel = set.getDeliveryChannel();

      final E element = getElement(i);
      set.remove(element);
      Mockito.reset(deliveryChannel);
      set.remove(element);
      Mockito.verifyZeroInteractions(deliveryChannel);

      makeTestAssertions(UpdatableSetTestCase.REMOVE_DUPLICATE, set, element);
    }
  }

  /**
   * Test {@link Set#removeAll(java.util.Collection)} twice.
   *
   * This test needs to be overridden with the {@code @Test} annotation and a call to this
   * implementation (by {@code super.testRemoveAll_Duplicate_NoPublish()}) in order for it to be
   * activated.
   *
   * @see UpdatableSetTestCase#REMOVE_ALL_DUPLICATE
   */
  public void testRemoveAll_Duplicate() {
    LOGGER.log(Level.INFO,
        "testRemove_Duplicate: "
            + "Ensure that when an element that has already been removed is removed, "
            + "that no change is published to the DeliveryChannel.");
    final S set = getSet();

    // Populate with elements
    for (int i = 0; i < MAX_OPERATIONS; i++) {
      set.addAll(Arrays.asList(getElement(3 * i), getElement(3 * i + 1), getElement(3 * i + 2)));
    }

    final VersionVector<K, T> expectedVersionVector = set.getVersion().copy();

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      expectedVersionVector.increment(set.getIdentifier());
      final DeliveryChannel<K, U> deliveryChannel = set.getDeliveryChannel();

      final HashSet<E> elements = new HashSet<>(
          Arrays.asList(getElement(3 * i), getElement(3 * i + 1), getElement(3 * i + 2)));
      set.removeAll(elements);
      Mockito.reset(deliveryChannel);
      set.removeAll(elements);
      Mockito.verifyZeroInteractions(deliveryChannel);

      makeTestAssertions(UpdatableSetTestCase.REMOVE_ALL_DUPLICATE, set, elements);
    }
  }

  // TODO: test retainAll and clear

  public static enum UpdatableSetTestCase {
    /**
     * Ensure that when a duplicate element is added, that no change is published to the
     * {@linkplain DeliveryChannel}.
     *
     * For {@link #makeTestAssertions(UpdatableSetTestCase, Set, Object...)}, the object arguments
     * are:
     * <ul>
     * <li>The element that was added to the {@link Set} being tested.
     * <li>The {@link UpdateMessage} that was published the {@link Set} being tested.
     * </ul>
     */
    ADD_DUPLICATE,

    /**
     * Ensure that when duplicates element is added, that no change is published to the
     * {@linkplain DeliveryChannel}.
     *
     * For {@link #makeTestAssertions(UpdatableSetTestCase, Set, Object...)}, the object arguments
     * are:
     * <ul>
     * <li>A {@link Set} that contains the elements that were added to the {@link Set} being tested.
     * <li>The {@link UpdateMessage} that was published the {@link Set} being tested.
     * </ul>
     */
    ADD_ALL_DUPLICATE,

    /**
     * Ensure that when an element that has already been removed is removed, that no change is
     * published to the {@linkplain DeliveryChannel}.
     *
     * For {@link #makeTestAssertions(UpdatableSetTestCase, Set, Object...)}, the object arguments
     * are:
     * <ul>
     * <li>The element that was removed from the {@link Set} being tested.
     * <li>The {@link UpdateMessage} that was published the {@link Set} being tested.
     * </ul>
     */
    REMOVE_DUPLICATE,

    /**
     * Ensure that when elements that have already been removed are removed, that no change is
     * published to the {@linkplain DeliveryChannel}.
     *
     * For {@link #makeTestAssertions(UpdatableSetTestCase, Set, Object...)}, the object arguments
     * are:
     * <ul>
     * <li>A {@link Set} that contains the elements that were removed from the {@link Set} being
     * tested.
     * <li>The {@link UpdateMessage} that was published the {@link Set} being tested.
     * </ul>
     */
    REMOVE_ALL_DUPLICATE
  }

}
