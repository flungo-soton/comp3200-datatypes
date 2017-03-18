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
import org.mockito.ArgumentMatcher;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.UpdatableSetAbstractTest;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.UpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.order.HashVersionVector;
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
    extends UpdatableSetAbstractTest<E, K, T, U, S> {

  private static final Logger LOGGER =
      Logger.getLogger(SetCommutativityAbstractTest.class.getName());

  @Captor
  public ArgumentCaptor<U> updateMessageCaptor;

  @Before
  public void setUpSetCommutativityAbstractTest() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Get an {@link UpdateMessage} which adds the given elements to the set. It is expected that the
   *
   * @param identifier the identifier of the node that the update message should come from.
   * @param version the version vector at the time of the update,
   * @param elements the elements that should be added as part of the update message.
   * @return an {@link UpdateMessage} representing the addition of the given elements.
   */
  protected abstract U getAddUpdate(K identifier, VersionVector<K, T> version,
      Collection<E> elements);

  protected final U getAddUpdate(K identifier, VersionVector<K, T> version, E... elements) {
    return getAddUpdate(identifier, version, Arrays.asList(elements));
  }

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
          if (!t.getElements().equals(new HashSet<>(Arrays.asList(element)))) {
            return false;
          }
          return true;
        }
      }));
      Mockito.verifyNoMoreInteractions(deliveryChannel);
    }
  }

  /**
   * Ensure that when an element is added using addAll, that the change is published to the
   * {@linkplain DeliveryChannel}.
   */
  @Test
  public void testAddAll_Single_Publish() {
    LOGGER.log(Level.INFO, "testAddAll_Publish: Ensure that when an element is added using addAll, "
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
      Mockito.verify(deliveryChannel).publish(Mockito.argThat(new ArgumentMatcher<U>() {
        @Override
        public boolean matches(U t) {
          if (!t.getIdentifier().equals(set.getIdentifier())) {
            return false;
          }
          if (!t.getVersionVector().identical(expectedVersionVector)) {
            return false;
          }
          if (!t.getElements().equals(elements)) {
            return false;
          }
          return true;
        }
      }));
      Mockito.verifyNoMoreInteractions(deliveryChannel);
    }
  }

  /**
   * Ensure that when elements are added using addAll, that the change is published to the
   * {@linkplain DeliveryChannel}.
   */
  @Test
  public void testAddAll_Multiple_Publish() {
    LOGGER.log(Level.INFO, "testAddAll_Publish: Ensure that when elements are added using addAll, "
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
      Mockito.verify(deliveryChannel).publish(Mockito.argThat(new ArgumentMatcher<U>() {
        @Override
        public boolean matches(U t) {
          if (!t.getIdentifier().equals(set.getIdentifier())) {
            return false;
          }
          if (!t.getVersionVector().identical(expectedVersionVector)) {
            return false;
          }
          if (!t.getElements().equals(elements)) {
            return false;
          }
          return true;
        }
      }));
      Mockito.verifyNoMoreInteractions(deliveryChannel);
    }
  }

  /**
   * Ensure that when an elements are added, that only new elements are published to the
   * {@linkplain DeliveryChannel}.
   */
  @Test
  public void testAddAll_Overlap_Publish() {
    LOGGER.log(Level.INFO, "testAddAll_Publish: Ensure that when an elements are added, "
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
      U updateMessage = updateMessageCaptor.getValue();
      assertEquals("Update message identifier should be the same as the set's", set.getIdentifier(),
          updateMessage.getIdentifier());
      assertTrue("Update version should be as expected",
          updateMessage.getVersionVector().identical(expectedVersionVector));
      assertEquals("Update element set should contain only the new elements", expectedElements,
          updateMessage.getElements());

      Mockito.verifyNoMoreInteractions(deliveryChannel);
    }
  }

  @Test
  public void testUpdate_Add_Single() throws Exception {
    LOGGER.log(Level.INFO,
        "testUpdate_Single: Test applying an update with a single element to be added.");
    final S set = getSet();

    final VersionVector<K, T> messageVersionVector = getSet().getVersion();
    messageVersionVector.init(set.getIdentifier());
    for (int i = 0; i < MAX_OPERATIONS; i++) {
      final E element = getElement(i);

      messageVersionVector.increment(set.getIdentifier());
      final U message = getAddUpdate(set.getIdentifier(), messageVersionVector, element);

      set.update(message);

      assertTrue("The set should contain the element which was added", set.contains(element));
    }
  }

  @Test
  public void testUpdate_Add_Multiple() throws Exception {
    LOGGER.log(Level.INFO,
        "testUpdate_Single: Test applying an update with a single element to be added.");
    final S set = getSet();

    final VersionVector<K, T> messageVersionVector = getSet().getVersion();
    messageVersionVector.init(set.getIdentifier());
    for (int i = 0; i < MAX_OPERATIONS; i++) {
      final HashSet<E> elements = new HashSet<>(
          Arrays.asList(getElement(3 * i), getElement(3 * i + 1), getElement(3 * i + 2)));

      messageVersionVector.increment(set.getIdentifier());
      final U message = getAddUpdate(set.getIdentifier(), messageVersionVector, elements);

      set.update(message);

      assertTrue("The set should contain the elements which were added", set.containsAll(elements));
    }
  }

}
