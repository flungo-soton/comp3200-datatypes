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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.mockito.Mockito;
import static uk.ac.soton.ecs.fl4g12.crdt.datatypes.GrowableUpdatableSetAbstractTest.MAX_OPERATIONS;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.order.Version;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Tests for growable {@link VersionedUpdatable} {@link Set} implementations.
 *
 * @param <E> the type of values stored in the {@link Set}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp within the {@link VersionVector}
 * @param <M> the type of {@link VersionedUpdateMessage} produced by the {@link VersionedUpdatable}
 *        {@link Set}.
 * @param <S> the type of {@link VersionedUpdatable} based {@link Set} being tested.
 */
public abstract class UpdatableSetAbstractTest<E, K, T extends Comparable<T>, M extends VersionedUpdateMessage<K, ? extends Version>, S extends Set<E> & VersionedUpdatable<K, VersionVector<K, T>, M>>
    extends GrowableUpdatableSetAbstractTest<E, K, T, M, S> {

  private static final Logger LOGGER = Logger.getLogger(UpdatableSetAbstractTest.class.getName());

  /**
   * Get a {@link VersionedUpdateMessage} which adds the given elements to the set.
   *
   * @param set the set which the update will be delivered to.
   * @param identifier the identifier of the node that the update message should come from.
   * @param version the version vector at the time of the update,
   * @param elements the elements that should be added as part of the update message.
   * @return an {@link VersionedUpdateMessage} representing the addition of the given elements.
   */
  protected abstract M getRemoveUpdate(S set, K identifier, VersionVector<K, T> version,
      Collection<E> elements);

  /**
   * Get a {@link VersionedUpdateMessage} which adds the given elements to the set.
   *
   * @param set the set which the update will be delivered to.
   * @param identifier the identifier of the node that the update message should come from.
   * @param version the version vector at the time of the update,
   * @param elements the elements that should be added as part of the update message.
   * @return an {@link VersionedUpdateMessage} representing the addition of the given elements.
   * @see #getRemoveUpdate(Set, Object, VersionVector, Collection) for more details.
   */
  protected final M getRemoveUpdate(S set, K identifier, VersionVector<K, T> version,
      E... elements) {
    return getRemoveUpdate(set, identifier, version, Arrays.asList(elements));
  }

  /**
   * Ensure that when an element is removed, that the change is published to the
   * {@linkplain DeliveryChannel}.
   */
  @Test
  public void testRemove() {
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
      final DeliveryChannel<K, M> deliveryChannel = set.getDeliveryChannel();

      final E element = getElement(i);
      Mockito.reset(deliveryChannel);
      set.remove(element);

      Mockito.verify(deliveryChannel).publish(updateMessageCaptor.capture());
      Mockito.verifyNoMoreInteractions(deliveryChannel);

      M updateMessage = updateMessageCaptor.getValue();

      assertExpectedUpdateMessage(set, expectedVersionVector, updateMessage);
      assertRemove(set, element, updateMessage);
    }
  }

  /**
   * Make assertions for the {@linkplain #testRemove()} method. Needs to ensure that the update
   * message is accurate given the element that was added.
   *
   * @param set the {@link Set} instance being tested.
   * @param element the element that was removed from the {@link Set} being tested.
   * @param updateMessage the {@link VersionedUpdateMessage} that was published by the {@link Set}
   *        being tested.
   */
  protected abstract void assertRemove(S set, E element, M updateMessage);

  /**
   * Ensure that when an element that has already been removed is removed, that no change is
   * published to the {@linkplain DeliveryChannel}.
   *
   * This test needs to be overridden with the {@code @Test} annotation and a call to this
   * implementation (by {@code super.testRemoveAll_Duplicate_NoPublish()}) in order for it to be
   * activated.
   */
  @Test
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
      final DeliveryChannel<K, M> deliveryChannel = set.getDeliveryChannel();

      final E element = getElement(i);
      set.remove(element);
      Mockito.reset(deliveryChannel);
      set.remove(element);
      Mockito.verifyZeroInteractions(deliveryChannel);

      assertRemove_Duplicate(set, element);
    }
  }

  /**
   * Make assertions for the {@linkplain #testRemove_Duplicate()} method.
   *
   * @param set the {@link Set} instance being tested.
   * @param element the element that was removed from the {@link Set} being tested.
   */
  protected void assertRemove_Duplicate(S set, E element) {
    // Do nothing by default
  }

  /**
   * Check the order of the messages that are published to the {@linkplain DeliveryChannel} when
   * using {@linkplain Set#remove(Object)}.
   */
  @Test
  public void testRemove_MessageOrder() {
    LOGGER.log(Level.INFO, "testRemove_MessageOrder: "
        + "Ensure that when an element is removed, that the change is published to the DeliveryChannel.");
    final S set = getSet();
    final DeliveryChannel<K, M> deliveryChannel = set.getDeliveryChannel();

    // TODO: May need to add elements first to avoid problems with observed remove sets.

    final ArrayList<M> previousMessages = new ArrayList<>();

    // Add initial element to generate first update message
    set.remove(getElement(0));
    Mockito.verify(deliveryChannel).publish(updateMessageCaptor.capture());
    M previousMessage = updateMessageCaptor.getValue();
    previousMessages.add(previousMessage);

    assertTrue("Initial message should be preceded by the the version of a new set.",
        precedes(getSet(), previousMessage));

    for (int i = 1; i < MAX_OPERATIONS; i++) {
      set.remove(getElement(i));
      // Using times instead of resetting ensures that no async operations are taking place.
      Mockito.verify(deliveryChannel, Mockito.times(i + 1)).publish(updateMessageCaptor.capture());
      M currentMessage = updateMessageCaptor.getValue();

      // Compare to all previous messages
      for (M message : previousMessages) {
        assertEquals("Should only be preceded by previousMessage", message == previousMessage,
            precedes(message, currentMessage));
        assertTrue("All previous messages should come before this one",
            compare(message, currentMessage) < 0);
      }

      // Update previousMessage(s)
      previousMessage = currentMessage;
      previousMessages.add(previousMessage);
    }
  }

  /**
   * Ensure that when an element is removed using {@linkplain Set#removeAll(java.util.Collection)},
   * that the change is published to the {@linkplain DeliveryChannel}.
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
      final DeliveryChannel<K, M> deliveryChannel = set.getDeliveryChannel();

      final E element = getElement(i);
      Mockito.reset(deliveryChannel);
      set.removeAll(new HashSet<>(Arrays.asList(element)));

      Mockito.verify(deliveryChannel).publish(updateMessageCaptor.capture());
      Mockito.verifyNoMoreInteractions(deliveryChannel);

      M updateMessage = updateMessageCaptor.getValue();

      assertExpectedUpdateMessage(set, expectedVersionVector, updateMessage);
      assertRemoveAll_Single(set, element, updateMessage);
    }
  }


  /**
   * Make assertions for the {@linkplain #testRemoveAll_Single()} method. Needs to ensure that the
   * update message is accurate given the element that was added.
   *
   * @param set the {@link Set} instance being tested.
   * @param element the element that was removed from the {@link Set} being tested.
   * @param updateMessage the {@link VersionedUpdateMessage} that was published by the {@link Set}
   *        being tested.
   */
  protected abstract void assertRemoveAll_Single(S set, E element, M updateMessage);

  /**
   * Ensure that when elements are removed using {@linkplain Set#removeAll(java.util.Collection)},
   * that the change is published to the {@linkplain DeliveryChannel}.
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
      final DeliveryChannel<K, M> deliveryChannel = set.getDeliveryChannel();

      final HashSet<E> elements = new HashSet<>(
          Arrays.asList(getElement(3 * i), getElement(3 * i + 1), getElement(3 * i + 2)));
      Mockito.reset(deliveryChannel);
      set.removeAll(elements);

      Mockito.verify(deliveryChannel).publish(updateMessageCaptor.capture());
      Mockito.verifyNoMoreInteractions(deliveryChannel);

      M updateMessage = updateMessageCaptor.getValue();
      assertExpectedUpdateMessage(set, expectedVersionVector, updateMessage);

      assertRemoveAll_Multiple(set, elements, updateMessage);
    }
  }

  /**
   * Make assertions for the {@linkplain #testRemoveAll_Multiple()} method. Needs to ensure that the
   * update message is accurate given the elements that were added.
   *
   * @param set the {@link Set} instance being tested.
   * @param elements A {@link Collection} that contains the elements that were removed from the
   *        {@link Set} being tested.
   * @param updateMessage the {@link VersionedUpdateMessage} that was published by the {@link Set}
   *        being tested.
   */
  protected abstract void assertRemoveAll_Multiple(S set, Collection<E> elements, M updateMessage);

  /**
   * Ensure that when an elements are removed using
   * {@linkplain Set#removeAll(java.util.Collection)}, that only new elements are published to the
   * {@linkplain DeliveryChannel}.
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
      final DeliveryChannel<K, M> deliveryChannel = set.getDeliveryChannel();

      final HashSet<E> elements = new HashSet<>(
          Arrays.asList(getElement(2 * i), getElement(2 * i + 1), getElement(2 * i + 2)));
      final HashSet<E> newElements =
          new HashSet<>(Arrays.asList(getElement(2 * i + 1), getElement(2 * i + 2)));
      Mockito.reset(deliveryChannel);
      set.removeAll(elements);

      Mockito.verify(deliveryChannel).publish(updateMessageCaptor.capture());
      Mockito.verifyNoMoreInteractions(deliveryChannel);

      M updateMessage = updateMessageCaptor.getValue();

      assertExpectedUpdateMessage(set, expectedVersionVector, updateMessage);
      assertRemoveAll_Overlap(set, elements, newElements, updateMessage);
    }
  }

  /**
   * Make assertions for the {@linkplain #testRemoveAll_Overlap()} method. Needs to ensure that the
   * update message is accurate given the elements that were added.
   *
   * @param set the {@link Set} instance being tested.
   * @param elements A {@link Collection} that contains the elements that were removed from the
   *        {@link Set} being tested.
   * @param newElements A {@link Collection} that contains the new elements that were removed to the
   *        {@link Set} being tested.
   * @param updateMessage the {@link VersionedUpdateMessage} that was published by the {@link Set}
   *        being tested.
   */
  protected abstract void assertRemoveAll_Overlap(S set, Collection<E> elements,
      Collection<E> newElements, M updateMessage);

  /**
   * Ensure that when elements that have already been removed are removed, that no change is
   * published to the {@linkplain DeliveryChannel}.
   *
   * This test needs to be overridden with the {@code @Test} annotation and a call to this
   * implementation (by {@code super.testRemoveAll_Duplicate_NoPublish()}) in order for it to be
   * activated.
   */
  @Test
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
      final DeliveryChannel<K, M> deliveryChannel = set.getDeliveryChannel();

      final HashSet<E> elements = new HashSet<>(
          Arrays.asList(getElement(3 * i), getElement(3 * i + 1), getElement(3 * i + 2)));
      set.removeAll(elements);
      Mockito.reset(deliveryChannel);
      set.removeAll(elements);
      Mockito.verifyZeroInteractions(deliveryChannel);

      assertRemoveAll_Duplicate(set, elements);
    }
  }

  /**
   * Make assertions for the {@linkplain #testRemoveAll_Duplicate()} method.
   *
   * @param set the {@link Set} instance being tested.
   * @param elements A {@link Set} that contains the elements that were added to the {@link Set}
   *        being tested.
   */
  protected void assertRemoveAll_Duplicate(S set, Collection<E> elements) {
    // Do nothing by default
  }

  /**
   * Test applying an update with a single element to be removed.
   *
   * @throws Exception if the test fails.
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
      final M message = getRemoveUpdate(set, set.getIdentifier(), messageVersionVector, element);

      set.update(message);

      assertTrue("The set should not contain the element which was removed",
          !set.contains(element));

      assertUpdate_Remove_Single(set, message);
    }
  }

  /**
   * Make assertions for the {@linkplain #testUpdate_Remove_Single()} method. Needs to ensure that
   * the update message is accurate given the element that was added.
   *
   * @param set the {@link Set} instance being tested.
   * @param updateMessage the {@link VersionedUpdateMessage} that was applied to the {@link Set}
   *        being tested.
   */
  protected void assertUpdate_Remove_Single(S set, M updateMessage) {
    // Do nothing by default
  }

  /**
   * Test applying an update with multiple element to be removed.
   *
   * @throws Exception if the test fails.
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
      final M message = getRemoveUpdate(set, set.getIdentifier(), messageVersionVector, elements);

      set.update(message);

      for (E element : elements) {
        assertTrue("The set should contain the elements which were removed",
            !set.contains(element));
      }

      assertUpdate_Remove_Multiple(set, message);
    }
  }

  /**
   * Make assertions for the {@linkplain #testUpdate_Add_Multiple()} method. Needs to ensure that
   * the update message is accurate given the element that was added.
   *
   * @param set the {@link Set} instance being tested.
   * @param updateMessage the {@link VersionedUpdateMessage} that was applied to the {@link Set}
   *        being tested.
   */
  protected void assertUpdate_Remove_Multiple(S set, M updateMessage) {
    // Do nothing by default
  }

  /**
   * Check the order of the messages that are published to the {@linkplain DeliveryChannel} when
   * using {@linkplain Set#removeAll(Collection)}.
   */
  @Test
  public void testRemoveAll_MessageOrder() {
    LOGGER.log(Level.INFO, "testRemoveAll_MessageOrder: "
        + "Ensure that when element are removed, that the change is published to the DeliveryChannel.");
    final S set = getSet();
    final DeliveryChannel<K, M> deliveryChannel = set.getDeliveryChannel();

    // TODO: May need to add elements first to avoid problems with observed remove sets.

    final ArrayList<M> previousMessages = new ArrayList<>();

    // Add initial element to generate first update message
    set.removeAll(Arrays.asList(getElement(0), getElement(1), getElement(2)));
    Mockito.verify(deliveryChannel).publish(updateMessageCaptor.capture());
    M previousMessage = updateMessageCaptor.getValue();
    previousMessages.add(previousMessage);

    assertTrue("Initial message should be preceded by the the version of a new set.",
        precedes(getSet(), previousMessage));

    for (int i = 1; i < MAX_OPERATIONS; i++) {
      set.removeAll(Arrays.asList(getElement(3 * i), getElement(3 * i + 1), getElement(3 * i + 2)));
      // Using times instead of resetting ensures that no async operations are taking place.
      Mockito.verify(deliveryChannel, Mockito.times(i + 1)).publish(updateMessageCaptor.capture());
      M currentMessage = updateMessageCaptor.getValue();

      // Compare to all previous messages
      for (M message : previousMessages) {
        assertEquals("Should only be preceded by previousMessage", message == previousMessage,
            precedes(message, currentMessage));
        assertTrue("All previous messages should come before this one",
            compare(message, currentMessage) < 0);
      }

      // Update previousMessage(s)
      previousMessage = currentMessage;
      previousMessages.add(previousMessage);
    }
  }

  // TODO: test retainAll and clear

}
