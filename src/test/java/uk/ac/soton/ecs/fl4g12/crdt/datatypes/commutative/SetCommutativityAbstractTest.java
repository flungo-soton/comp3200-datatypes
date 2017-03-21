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
import org.junit.Test;
import org.mockito.Mockito;
import static uk.ac.soton.ecs.fl4g12.crdt.datatypes.UpdatableSetAbstractTest.MAX_OPERATIONS;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Tests of the commutativity of {@link VersionedUpdatable} {@link Set} implementations.
 *
 * @param <E> the type of values stored in the {@link Set}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp within the {@link VersionVector}
 * @param <U> the type of snapshot made from this state.
 * @param <S> the type of {@link VersionedUpdatable} based {@link Set} being tested.
 */
public abstract class SetCommutativityAbstractTest<E, K, T extends Comparable<T>, U extends SetUpdateMessage<E, K, T>, S extends Set<E> & VersionedUpdatable<K, T, U>>
    extends GrowOnlySetCommutativityAbstractTest<E, K, T, U, S> {

  private static final Logger LOGGER =
      Logger.getLogger(SetCommutativityAbstractTest.class.getName());

  /**
   * Get a {@link SetUpdateMessage} which adds the given elements to the set.
   *
   * @param identifier the identifier of the node that the update message should come from.
   * @param version the version vector at the time of the update,
   * @param elements the elements that should be added as part of the update message.
   * @return an {@link SetUpdateMessage} representing the addition of the given elements.
   */
  protected abstract U getRemoveUpdate(K identifier, VersionVector<K, T> version,
      Collection<E> elements);

  protected final U getRemoveUpdate(K identifier, VersionVector<K, T> version, E... elements) {
    return getRemoveUpdate(identifier, version, Arrays.asList(elements));
  }

  @Override
  protected void makeTestAssertions(GrowOnlySetCommutativityTestCase testCase, S set,
      Object... objects) {
    super.makeTestAssertions(testCase, set, objects);

    U updateMessage = null;

    // Extract variables from cases
    switch (testCase) {
      case ADD:
      case ADDALL_SINGLE:
      case ADDALL_MULTIPLE:
      case ADDALL_OVERLAP:
        updateMessage = (U) objects[1];
        break;
      case UPDATE_ADD_MULTIPLE:
      case UPDATE_ADD_SINGLE:
        updateMessage = (U) objects[0];
        break;
    }

    if (updateMessage != null) {
      assertEquals("Update message should be an ADD operation", SetUpdateMessage.Operation.ADD,
          updateMessage.getOperation());
    }
  }

  /**
   * Called within the provided tests to allow additional assertions to be made. All implementations
   * should call their super to ensure that all assertions are made.
   *
   * @param testCase the {@link SetCommutativityTestCase} which the assertions are being performed
   *        within.
   * @param set the {@link Set} instance being tested.
   * @param objects the objects for assertions to be made on. This differs for each case and so
   *        {@link SetCommutativityTestCase} documentation should be consulted for the details.
   */
  protected void makeTestAssertions(SetCommutativityTestCase testCase, S set, Object... objects) {}

  /**
   * @see SetCommutativityTestCase#REMOVE
   */
  @Test
  public void testRemove_Publish() {
    LOGGER.log(Level.INFO, "testRemove_Publish: Ensure that when an element is removed,"
        + " that the change is published to the DeliveryChannel.");
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
      Mockito.reset(deliveryChannel);
      set.remove(element);

      Mockito.verify(deliveryChannel).publish(updateMessageCaptor.capture());
      U updateMessage = updateMessageCaptor.getValue();
      assertEquals("Update message identifier should be the same as the set's", set.getIdentifier(),
          updateMessage.getIdentifier());
      assertTrue("Update version should be as expected",
          updateMessage.getVersionVector().identical(expectedVersionVector));
      assertEquals("Update message should be an REMOVE operation",
          SetUpdateMessage.Operation.REMOVE, updateMessage.getOperation());
      assertEquals("Update element set should consist of the new element",
          new HashSet<>(Arrays.asList(element)), updateMessage.getElements());

      Mockito.verifyNoMoreInteractions(deliveryChannel);

      makeTestAssertions(SetCommutativityTestCase.REMOVE, set, element, updateMessage);
    }
  }

  /**
   * @see SetCommutativityTestCase#REMOVEALL_SINGLE
   */
  @Test
  public void testRemoveAll_Single() {
    LOGGER.log(Level.INFO,
        "testRemoveAll_Single: Ensure that when an element is removed using removeAll, "
            + "that the change is published to the DeliveryChannel.");
    final S set = getSet();

    // Populate with elements
    for (int i = 0; i < MAX_OPERATIONS; i++) {
      set.add(getElement(i));
    }

    final VersionVector<K, T> expectedVersionVector = set.getVersion().copy();

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      expectedVersionVector.increment(set.getIdentifier());
      final DeliveryChannel<K, U> deliveryChannel = set.getDeliveryChannel();

      final HashSet<E> elements = new HashSet<>(Arrays.asList(getElement(i)));
      Mockito.reset(deliveryChannel);
      set.removeAll(elements);

      Mockito.verify(deliveryChannel).publish(updateMessageCaptor.capture());
      Mockito.verifyNoMoreInteractions(deliveryChannel);

      U updateMessage = updateMessageCaptor.getValue();
      assertEquals("Update message identifier should be the same as the set's", set.getIdentifier(),
          updateMessage.getIdentifier());
      assertTrue("Update version should be as expected",
          updateMessage.getVersionVector().identical(expectedVersionVector));
      assertEquals("Update message should be an REMOVE operation",
          SetUpdateMessage.Operation.REMOVE, updateMessage.getOperation());
      assertEquals("Update element set should consist of the new element", elements,
          updateMessage.getElements());

      makeTestAssertions(SetCommutativityTestCase.REMOVEALL_SINGLE, set, elements, updateMessage);
    }
  }

  /**
   * @see SetCommutativityTestCase#REMOVEALL_MULTIPLE
   */
  @Test
  public void testRemoveAll_Multiple() {
    LOGGER.log(Level.INFO,
        "testRemoveAll_Multiple: Ensure that when elements are removed using removeAll, "
            + "that the change is published to the DeliveryChannel.");
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
      Mockito.reset(deliveryChannel);
      set.removeAll(elements);

      Mockito.verify(deliveryChannel).publish(updateMessageCaptor.capture());
      Mockito.verifyNoMoreInteractions(deliveryChannel);

      U updateMessage = updateMessageCaptor.getValue();
      assertEquals("Update message identifier should be the same as the set's", set.getIdentifier(),
          updateMessage.getIdentifier());
      assertTrue("Update version should be as expected",
          updateMessage.getVersionVector().identical(expectedVersionVector));
      assertEquals("Update message should be an REMOVE operation",
          SetUpdateMessage.Operation.REMOVE, updateMessage.getOperation());
      assertEquals("Update element set should consist of the new elements", elements,
          updateMessage.getElements());

      makeTestAssertions(SetCommutativityTestCase.REMOVEALL_MULTIPLE, set, elements, updateMessage);
    }
  }

  /**
   * @see SetCommutativityTestCase#REMOVEALL_OVERLAP
   */
  @Test
  public void testRemoveAll_Overlap() {
    LOGGER.log(Level.INFO,
        "testRemoveAll_Overlap: Ensure that when an elements are removed using removeAll, "
            + "that only new elements are published to the DeliveryChannel.");
    final S set = getSet();

    // Populate with elements
    for (int i = 0; i < MAX_OPERATIONS; i++) {
      set.addAll(Arrays.asList(getElement(3 * i), getElement(3 * i + 1), getElement(3 * i + 2)));
    }

    set.removeAll(new HashSet<>(Arrays.asList(getElement(0), getElement(1), getElement(2))));

    final VersionVector<K, T> expectedVersionVector = set.getVersion().copy();

    for (int i = 1; i < MAX_OPERATIONS; i++) {
      expectedVersionVector.increment(set.getIdentifier());
      final DeliveryChannel<K, U> deliveryChannel = set.getDeliveryChannel();

      final HashSet<E> elements = new HashSet<>(
          Arrays.asList(getElement(2 * i), getElement(2 * i + 1), getElement(2 * i + 2)));
      final HashSet<E> expectedElements =
          new HashSet<>(Arrays.asList(getElement(2 * i + 1), getElement(2 * i + 2)));
      Mockito.reset(deliveryChannel);
      set.removeAll(elements);

      Mockito.verify(deliveryChannel).publish(updateMessageCaptor.capture());
      Mockito.verifyNoMoreInteractions(deliveryChannel);

      U updateMessage = updateMessageCaptor.getValue();
      assertEquals("Update message identifier should be the same as the set's", set.getIdentifier(),
          updateMessage.getIdentifier());
      assertTrue("Update version should be as expected",
          updateMessage.getVersionVector().identical(expectedVersionVector));
      assertEquals("Update message should be an REMOVE operation",
          SetUpdateMessage.Operation.REMOVE, updateMessage.getOperation());
      assertEquals("Update element set should consist only of the new elements", expectedElements,
          updateMessage.getElements());

      makeTestAssertions(SetCommutativityTestCase.REMOVEALL_OVERLAP, set, elements, updateMessage);
    }
  }

  /**
   * @throws Exception if the test fails.
   * @see SetCommutativityTestCase#UPDATE_REMOVE_SINGLE
   */
  @Test
  public void testUpdate_Remove_Single() throws Exception {
    LOGGER.log(Level.INFO,
        "testUpdate_Remove_Single: Test applying an update with a single element to be removed.");
    final S set = getSet();

    // Populate with elements
    for (int i = 0; i < MAX_OPERATIONS; i++) {
      set.add(getElement(i));
    }

    final VersionVector<K, T> messageVersionVector = set.getVersion().copy();

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      final E element = getElement(i);

      messageVersionVector.increment(set.getIdentifier());
      final U message = getRemoveUpdate(set.getIdentifier(), messageVersionVector, element);

      set.update(message);

      assertTrue("The set should not contain the element which was removed",
          !set.contains(element));

      makeTestAssertions(SetCommutativityTestCase.UPDATE_REMOVE_SINGLE, set, message);
    }
  }

  /**
   * @throws Exception if the test fails.
   * @see SetCommutativityTestCase#UPDATE_REMOVE_MULTIPLE
   */
  @Test
  public void testUpdate_Remove_Multiple() throws Exception {
    LOGGER.log(Level.INFO,
        "testUpdate_Remove_Multiple: Test applying an update with multiple element to be removed.");
    final S set = getSet();

    // Populate with elements
    for (int i = 0; i < MAX_OPERATIONS; i++) {
      set.addAll(Arrays.asList(getElement(3 * i), getElement(3 * i + 1), getElement(3 * i + 2)));
    }

    final VersionVector<K, T> messageVersionVector = set.getVersion().copy();

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      final HashSet<E> elements = new HashSet<>(
          Arrays.asList(getElement(3 * i), getElement(3 * i + 1), getElement(3 * i + 2)));

      messageVersionVector.increment(set.getIdentifier());
      final U message = getRemoveUpdate(set.getIdentifier(), messageVersionVector, elements);

      set.update(message);

      for (E element : elements) {
        assertTrue("The set should contain the elements which were removed",
            !set.contains(element));
      }

      makeTestAssertions(SetCommutativityTestCase.UPDATE_REMOVE_MULTIPLE, set, message);
    }
  }

  // TODO: test retainAll and clear

  @Override
  @Test
  public void testRemove_Duplicate() {
    super.testRemove_Duplicate();
  }

  @Override
  @Test
  public void testRemoveAll_Duplicate() {
    super.testRemoveAll_Duplicate();
  }

  public static enum SetCommutativityTestCase {
    /**
     * Ensure that when an element is removed, that the change is published to the
     * {@linkplain DeliveryChannel}.
     *
     * For {@link #makeTestAssertions(SetCommutativityTestCase, Set, Object...)}, the object
     * arguments are:
     * <ul>
     * <li>The element that was removed from the {@link Set} being tested.
     * <li>The {@link SetUpdateMessage} that was published the {@link Set} being tested.
     * </ul>
     */
    REMOVE,

    /**
     * Ensure that when an element is removed using
     * {@linkplain Set#removeAll(java.util.Collection)}, that the change is published to the
     * {@linkplain DeliveryChannel}.
     *
     * For {@link #makeTestAssertions(SetCommutativityTestCase, Set, Object...)}, the object
     * arguments are:
     * <ul>
     * <li>A {@link Set} that contains the element that was removed from the {@link Set} being
     * tested.
     * <li>The {@link SetUpdateMessage} that was published the {@link Set} being tested.
     * </ul>
     */
    REMOVEALL_SINGLE,

    /**
     * Ensure that when elements are removed using {@linkplain Set#removeAll(java.util.Collection)},
     * that the change is published to the {@linkplain DeliveryChannel}.
     *
     * For {@link #makeTestAssertions(SetCommutativityTestCase, Set, Object...)}, the object
     * arguments are:
     * <ul>
     * <li>A {@link Set} that contains the element that was removed from the {@link Set} being
     * tested.
     * <li>The {@link SetUpdateMessage} that was published the {@link Set} being tested.
     * </ul>
     */
    REMOVEALL_MULTIPLE,

    /**
     * Ensure that when an elements are removed using
     * {@linkplain Set#removeAll(java.util.Collection)}, that only new elements are published to the
     * {@linkplain DeliveryChannel}.
     *
     * For {@link #makeTestAssertions(SetCommutativityTestCase, Set, Object...)}, the object
     * arguments are:
     * <ul>
     * <li>A {@link Set} that contains the element that was removed from the {@link Set} being
     * tested.
     * <li>The {@link SetUpdateMessage} that was published the {@link Set} being tested.
     * </ul>
     */
    REMOVEALL_OVERLAP,

    /**
     * Test applying an update with a single element to be removed.
     *
     * For {@link #makeTestAssertions(SetCommutativityTestCase, Set, Object...)}, the object
     * arguments are:
     * <ul>
     * <li>The {@link SetUpdateMessage} that was given from the {@link Set} being tested.
     * </ul>
     */
    UPDATE_REMOVE_SINGLE,

    /**
     * Test applying an update with multiple element to be removed.
     *
     * For {@link #makeTestAssertions(SetCommutativityTestCase, Set, Object...)}, the object
     * arguments are:
     * <ul>
     * <li>The {@link SetUpdateMessage} that was given from the {@link Set} being tested.
     * </ul>
     */
    UPDATE_REMOVE_MULTIPLE;
  }

}
