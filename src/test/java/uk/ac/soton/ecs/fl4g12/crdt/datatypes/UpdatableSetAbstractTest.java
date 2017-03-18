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
import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import org.mockito.Mockito;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.Updatable;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.UpdateMessage;
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
 * @param <S> the type of {@link VersionedUpdatable} based {@link Set} being tested.
 */
public abstract class UpdatableSetAbstractTest<E, K, T extends Comparable<T>, U extends UpdateMessage<K, ?>, S extends Set<E> & Updatable<K, U>> {

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

}
