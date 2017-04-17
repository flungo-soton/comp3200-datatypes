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
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.verification.VerificationMode;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.GrowableUpdatableSetAbstractTest;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.ReliableDeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.order.Dot;
import uk.ac.soton.ecs.fl4g12.crdt.order.Version;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Tests of the commutativity of growable {@link VersionedUpdatable} {@link Set} implementations.
 *
 * @param <E> the type of values stored in the {@link Set}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp within the {@link VersionVector}.
 * @param <M> the type of {@link GrowableSetUpdateMessage} produced by the
 *        {@link VersionedUpdatable} {@link Set}.
 * @param <S> the type of {@link VersionedUpdatable} based {@link Set} being tested.
 */
public abstract class GrowableCommutativeSetAbstractTest<E, K, T extends Comparable<T>, M extends GrowableSetUpdateMessage<E, K, Dot<K, T>>, S extends Set<E> & VersionedUpdatable<K, VersionVector<K, T>, M>>
    extends GrowableUpdatableSetAbstractTest<E, K, T, M, S> {

  @Captor
  ArgumentCaptor<M> updateMessageCaptor;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Override
  protected boolean precedes(S updatable, M message) {
    return updatable.getVersion().precedes(message.getVersion());
  }

  @Override
  protected boolean precedes(M message1, M message2) {
    return message1.getVersion().precedes(message2.getVersion());
  }

  @Override
  protected int compare(M message1, M message2) {
    return message1.compareTo(message2);
  }

  public static <E, K, T extends Comparable<T>, U extends GrowableSetUpdateMessage<E, K, ? extends Version<T, ?, ?>>> void assertElementsMatch(
      Set<E> elements, U updateMessage) {
    assertEquals("Update element set should consist only of the new element(s)", elements,
        updateMessage.getElements());
  }

  @Override
  protected void assertExpectedUpdateMessage(S set, VersionVector<K, T> expectedVersion,
      M updateMessage) {
    // Overridden to support Dot vs Vector comparisons,
    assertEquals("Update message identifier should be the same as the set's", set.getIdentifier(),
        updateMessage.getIdentifier());
    assertTrue("Update version should be as expected",
        updateMessage.getVersion().identical(expectedVersion));
  }

  @Override
  protected void assertAdd(S set, E element, M updateMessage) {
    assertElementsMatch(new HashSet<>(Arrays.asList(element)), updateMessage);
  }

  @Override
  protected void assertAddAll_Single(S set, E element, M updateMessage) {
    assertElementsMatch(new HashSet<>(Arrays.asList(element)), updateMessage);
  }

  @Override
  protected void assertAddAll_Multiple(S set, Set<E> elements, M updateMessage) {
    assertElementsMatch(elements, updateMessage);
  }

  @Override
  protected void assertAddAll_Overlap(S set, Set<E> elements, Set<E> newElements, M updateMessage) {
    assertElementsMatch(newElements, updateMessage);
  }

  @Override
  protected M assertPublish(DeliveryChannel<K, M, ?> channel, VerificationMode mode) {
    Mockito.verify((ReliableDeliveryChannel<K, M>) channel, mode)
        .publish(updateMessageCaptor.capture());
    return updateMessageCaptor.getValue();
  }



}
