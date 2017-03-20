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

package uk.ac.soton.ecs.fl4g12.crdt.datatypes.commutative;

import java.util.Arrays;
import java.util.Collection;
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
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.UpdatableSetAbstractTest;
import static uk.ac.soton.ecs.fl4g12.crdt.datatypes.UpdatableSetAbstractTest.MAX_OPERATIONS;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.UpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.order.HashVersionVector;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Tests of the commutativity of grow only {@link VersionedUpdatable} {@link Set} implementations.
 *
 * @param <E> the type of values stored in the {@link Set}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp within the {@link VersionVector}
 * @param <U> the type of snapshot made from this state.
 * @param <S> the type of {@link VersionedUpdatable} based {@link Set} being tested.
 */
public abstract class GrowOnlySetCommutativityAbstractTest<E, K, T extends Comparable<T>, U extends GrowOnlySetUpdateMessage<E, K, T>, S extends Set<E> & VersionedUpdatable<K, T, U>>
    extends UpdatableSetAbstractTest<E, K, T, U, S> {

  private static final Logger LOGGER =
      Logger.getLogger(GrowOnlySetCommutativityAbstractTest.class.getName());

  @Captor
  public ArgumentCaptor<U> updateMessageCaptor;

  @Before
  public void setUpSetCommutativityAbstractTest() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Get a {@link GrowOnlySetUpdateMessage} which adds the given elements to the set.
   *
   * @param identifier the identifier of the node that the update message should come from.
   * @param version the version vector at the time of the update,
   * @param elements the elements that should be added as part of the update message.
   * @return an {@link GrowOnlySetUpdateMessage} representing the addition of the given elements.
   */
  protected abstract U getAddUpdate(K identifier, VersionVector<K, T> version,
      Collection<E> elements);

  protected final U getAddUpdate(K identifier, VersionVector<K, T> version, E... elements) {
    return getAddUpdate(identifier, version, Arrays.asList(elements));
  }

  /**
   * Called within the provided tests to allow additional assertions to be made. All implementations
   * should call their super to ensure that all assertions are made.
   *
   * @param testCase the {@link GrowOnlySetCommutativityTestCase} which the assertions are being
   *        performed within.
   * @param set the {@link Set} instance being tested.
   * @param objects the objects for assertions to be made on. This differs for each case and so
   *        {@link GrowOnlySetCommutativityTestCase} documentation should be consulted for the
   *        details.
   */
  protected void makeTestAssertions(GrowOnlySetCommutativityTestCase testCase, S set,
      Object... objects) {}

  /**
   * @see GrowOnlySetCommutativityTestCase#ADD
   */
  @Test
  public void testAdd() {
    LOGGER.log(Level.INFO, "testAdd: "
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

      Mockito.verify(deliveryChannel).publish(updateMessageCaptor.capture());
      Mockito.verifyNoMoreInteractions(deliveryChannel);

      U updateMessage = updateMessageCaptor.getValue();
      assertEquals("Update message identifier should be the same as the set's", set.getIdentifier(),
          updateMessage.getIdentifier());
      assertTrue("Update version should be as expected",
          updateMessage.getVersionVector().identical(expectedVersionVector));
      assertEquals("Update element set should consist of the new element",
          new HashSet<>(Arrays.asList(element)), updateMessage.getElements());

      makeTestAssertions(GrowOnlySetCommutativityTestCase.ADD, set, element, updateMessage);
    }
  }


  /**
   * @see GrowOnlySetCommutativityTestCase#ADD_DUPLICATE
   */
  @Test
  public void testAdd_Duplicate() {
    LOGGER.log(Level.INFO,
        "testAdd_Duplicate: "
            + "Ensure that when an element that has already been removed is removed, "
            + "that no change is published to the DeliveryChannel.");
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

      makeTestAssertions(GrowOnlySetCommutativityTestCase.ADD_DUPLICATE, set, element);
    }
  }

  /**
   * @see GrowOnlySetCommutativityTestCase#ADDALL_SINGLE
   */
  @Test
  public void testAddAll_Single() {
    LOGGER.log(Level.INFO, "testAddAll_Single: Ensure that when an element is added using addAll, "
        + "that the change is published to the DeliveryChannel.");
    final S set = getSet();

    final VersionVector<K, T> expectedVersionVector =
        new HashVersionVector<>(getZeroVersion(), false);
    expectedVersionVector.init(set.getIdentifier());

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      expectedVersionVector.increment(set.getIdentifier());
      final DeliveryChannel<K, U> deliveryChannel = set.getDeliveryChannel();

      final HashSet<E> elements = new HashSet<>(Arrays.asList(getElement(i)));
      Mockito.reset(deliveryChannel);
      set.addAll(elements);

      Mockito.verify(deliveryChannel).publish(updateMessageCaptor.capture());
      Mockito.verifyNoMoreInteractions(deliveryChannel);

      U updateMessage = updateMessageCaptor.getValue();
      assertEquals("Update message identifier should be the same as the set's", set.getIdentifier(),
          updateMessage.getIdentifier());
      assertTrue("Update version should be as expected",
          updateMessage.getVersionVector().identical(expectedVersionVector));
      assertEquals("Update element set should consist of the new element", elements,
          updateMessage.getElements());

      makeTestAssertions(GrowOnlySetCommutativityTestCase.ADDALL_SINGLE, set, elements,
          updateMessage);
    }
  }

  /**
   * @see GrowOnlySetCommutativityTestCase#ADDALL_MULTIPLE
   */
  @Test
  public void testAddAll_Multiple() {
    LOGGER.log(Level.INFO, "testAddAll_Multiple: Ensure that when elements are added using addAll, "
        + "that the change is published to the DeliveryChannel.");
    final S set = getSet();

    final VersionVector<K, T> expectedVersionVector =
        new HashVersionVector<>(getZeroVersion(), false);
    expectedVersionVector.init(set.getIdentifier());

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      expectedVersionVector.increment(set.getIdentifier());
      final DeliveryChannel<K, U> deliveryChannel = set.getDeliveryChannel();

      final HashSet<E> elements = new HashSet<>(
          Arrays.asList(getElement(3 * i), getElement(3 * i + 1), getElement(3 * i + 2)));
      Mockito.reset(deliveryChannel);
      set.addAll(elements);

      Mockito.verify(deliveryChannel).publish(updateMessageCaptor.capture());
      Mockito.verifyNoMoreInteractions(deliveryChannel);

      U updateMessage = updateMessageCaptor.getValue();
      assertEquals("Update message identifier should be the same as the set's", set.getIdentifier(),
          updateMessage.getIdentifier());
      assertTrue("Update version should be as expected",
          updateMessage.getVersionVector().identical(expectedVersionVector));
      assertEquals("Update element set should consist of the new elements", elements,
          updateMessage.getElements());

      makeTestAssertions(GrowOnlySetCommutativityTestCase.ADDALL_MULTIPLE, set, elements,
          updateMessage);
    }
  }

  /**
   * @see GrowOnlySetCommutativityTestCase#ADDALL_OVERLAP
   */
  @Test
  public void testAddAll_Overlap() {
    LOGGER.log(Level.INFO,
        "testAddAll_Overlap: Ensure that when an elements are added using addAll, "
            + "that only new elements are published to the DeliveryChannel.");
    final S set = getSet();

    final VersionVector<K, T> expectedVersionVector =
        new HashVersionVector<>(getZeroVersion(), false);
    expectedVersionVector.init(set.getIdentifier());

    set.addAll(new HashSet<>(Arrays.asList(getElement(0), getElement(1), getElement(2))));
    expectedVersionVector.increment(set.getIdentifier());

    for (int i = 1; i < MAX_OPERATIONS; i++) {
      expectedVersionVector.increment(set.getIdentifier());
      final DeliveryChannel<K, U> deliveryChannel = set.getDeliveryChannel();

      final HashSet<E> elements = new HashSet<>(
          Arrays.asList(getElement(2 * i), getElement(2 * i + 1), getElement(2 * i + 2)));
      final HashSet<E> expectedElements =
          new HashSet<>(Arrays.asList(getElement(2 * i + 1), getElement(2 * i + 2)));
      Mockito.reset(deliveryChannel);
      set.addAll(elements);

      Mockito.verify(deliveryChannel).publish(updateMessageCaptor.capture());
      Mockito.verifyNoMoreInteractions(deliveryChannel);

      U updateMessage = updateMessageCaptor.getValue();
      assertEquals("Update message identifier should be the same as the set's", set.getIdentifier(),
          updateMessage.getIdentifier());
      assertTrue("Update version should be as expected",
          updateMessage.getVersionVector().identical(expectedVersionVector));
      assertEquals("Update element set should consist only of the new elements", expectedElements,
          updateMessage.getElements());

      makeTestAssertions(GrowOnlySetCommutativityTestCase.ADDALL_OVERLAP, set, elements,
          updateMessage);
    }
  }

  /**
   * @throws Exception if the test fails.
   * @see GrowOnlySetCommutativityTestCase#UPDATE_ADD_SINGLE
   */
  @Test
  public void testUpdate_Add_Single() throws Exception {
    LOGGER.log(Level.INFO,
        "testUpdate_Add_Single: Test applying an update with a single element to be added.");
    final S set = getSet();

    final VersionVector<K, T> messageVersionVector = set.getVersion().copy();

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      final E element = getElement(i);

      messageVersionVector.increment(set.getIdentifier());
      final U message = getAddUpdate(set.getIdentifier(), messageVersionVector, element);

      set.update(message);

      assertTrue("The set should contain the element which was added", set.contains(element));

      makeTestAssertions(GrowOnlySetCommutativityTestCase.UPDATE_ADD_SINGLE, set, message);
    }
  }

  /**
   * @throws Exception if the test fails.
   * @see GrowOnlySetCommutativityTestCase#UPDATE_ADD_MULTIPLE
   */
  @Test
  public void testUpdate_Add_Multiple() throws Exception {
    LOGGER.log(Level.INFO,
        "testUpdate_Add_Multiple: Test applying an update with multiple element to be added.");
    final S set = getSet();

    final VersionVector<K, T> messageVersionVector = set.getVersion().copy();

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      final HashSet<E> elements = new HashSet<>(
          Arrays.asList(getElement(3 * i), getElement(3 * i + 1), getElement(3 * i + 2)));

      messageVersionVector.increment(set.getIdentifier());
      final U message = getAddUpdate(set.getIdentifier(), messageVersionVector, elements);

      set.update(message);

      assertTrue("The set should contain the elements which were added", set.containsAll(elements));

      makeTestAssertions(GrowOnlySetCommutativityTestCase.UPDATE_ADD_MULTIPLE, set, message);
    }
  }

  public static enum GrowOnlySetCommutativityTestCase {
    /**
     * Ensure that when an element is added, that the change is published to the
     * {@linkplain DeliveryChannel}.
     *
     * For {@link #makeTestAssertions(GrowOnlySetCommutativityTestCase, Set, Object...)}, the object
     * arguments are:
     * <ul>
     * <li>The element that was added to the {@link Set} being tested.
     * <li>The {@link UpdateMessage} that was published the {@link Set} being tested.
     * </ul>
     */
    ADD,

    /**
     * Ensure that when an element that has already been added is added, that no change is published
     * to the {@linkplain DeliveryChannel}.
     *
     * For {@link #makeTestAssertions(GrowOnlySetCommutativityTestCase, Set, Object...)}, the object
     * arguments are:
     * <ul>
     * <li>The element that was added to the {@link Set} being tested.
     * </ul>
     */
    ADD_DUPLICATE,

    /**
     * Ensure that when an element is added using {@linkplain Set#addAll(java.util.Collection)},
     * that the change is published to the {@linkplain DeliveryChannel}.
     *
     * For {@link #makeTestAssertions(GrowOnlySetCommutativityTestCase, Set, Object...)}, the object
     * arguments are:
     * <ul>
     * <li>A {@link Set} that contains the element that was added to the {@link Set} being tested.
     * <li>The {@link UpdateMessage} that was published the {@link Set} being tested.
     * </ul>
     */
    ADDALL_SINGLE,

    /**
     * Ensure that when elements are added using {@linkplain Set#addAll(java.util.Collection)}, that
     * the change is published to the {@linkplain DeliveryChannel}.
     *
     * For {@link #makeTestAssertions(GrowOnlySetCommutativityTestCase, Set, Object...)}, the object
     * arguments are:
     * <ul>
     * <li>A {@link Set} that contains the element that was added to the {@link Set} being tested.
     * <li>The {@link GrowOnlySetUpdateMessage} that was published the {@link Set} being tested.
     * </ul>
     */
    ADDALL_MULTIPLE,

    /**
     * Ensure that when an elements are added using {@linkplain Set#addAll(java.util.Collection)},
     * that only new elements are published to the {@linkplain DeliveryChannel}.
     *
     * For {@link #makeTestAssertions(GrowOnlySetCommutativityTestCase, Set, Object...)}, the object
     * arguments are:
     * <ul>
     * <li>A {@link Set} that contains the element that was added to the {@link Set} being tested.
     * <li>The {@link GrowOnlySetUpdateMessage} that was published the {@link Set} being tested.
     * </ul>
     */
    ADDALL_OVERLAP,

    /**
     * Test applying an update with a single element to be added.
     *
     * For {@link #makeTestAssertions(GrowOnlySetCommutativityTestCase, Set, Object...)}, the object
     * arguments are:
     * <ul>
     * <li>The {@link GrowOnlySetUpdateMessage} that was given to the {@link Set} being tested.
     * </ul>
     */
    UPDATE_ADD_SINGLE,

    /**
     * Test applying an update with multiple element to be added.
     *
     * For {@link #makeTestAssertions(GrowOnlySetCommutativityTestCase, Set, Object...)}, the object
     * arguments are:
     * <ul>
     * <li>The {@link GrowOnlySetUpdateMessage} that was given to the {@link Set} being tested.
     * </ul>
     */
    UPDATE_ADD_MULTIPLE;
  }

}
