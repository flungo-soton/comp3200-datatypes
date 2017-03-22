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

import java.util.Collection;
import java.util.HashSet;
import org.mockito.Mockito;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.CausalDeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.Updatable;
import uk.ac.soton.ecs.fl4g12.crdt.idenitifier.IncrementalIntegerIdentifierFactory;
import uk.ac.soton.ecs.fl4g12.crdt.order.IntegerVersion;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Tests for the {@linkplain CommutativeTwoPhaseSetUpdate} implementation.
 */
public final class CommutativeTwoPhaseSetCommutativityTest extends
    SetCommutativityAbstractTest<Integer, Integer, Integer, CommutativeTwoPhaseSetUpdate<Integer, Integer, Integer>, CommutativeTwoPhaseSet<Integer, Integer, Integer>> {

  private static final IncrementalIntegerIdentifierFactory ID_FACTORY =
      new IncrementalIntegerIdentifierFactory();

  @Override
  protected CommutativeTwoPhaseSet<Integer, Integer, Integer> getSet() {
    CausalDeliveryChannel<Integer, Integer, CommutativeTwoPhaseSetUpdate<Integer, Integer, Integer>> deliveryChannel =
        Mockito.mock(CausalDeliveryChannel.class);
    Mockito.doReturn(ID_FACTORY.create()).doThrow(IllegalStateException.class).when(deliveryChannel)
        .register(Mockito.any(Updatable.class));
    return new CommutativeTwoPhaseSet<>(new IntegerVersion(), null, deliveryChannel);
  }

  @Override
  protected Integer getElement(int i) {
    return i;
  }

  @Override
  protected CommutativeTwoPhaseSetUpdate<Integer, Integer, Integer> getAddUpdate(
      CommutativeTwoPhaseSet<Integer, Integer, Integer> set, Integer identifier,
      VersionVector<Integer, Integer> version, Collection<Integer> elements) {
    return new CommutativeTwoPhaseSetUpdate<>(identifier, version, SetUpdateMessage.Operation.ADD,
        new HashSet<>(elements));
  }

  @Override
  protected CommutativeTwoPhaseSetUpdate<Integer, Integer, Integer> getRemoveUpdate(
      Integer identifier, VersionVector<Integer, Integer> version, Collection<Integer> elements) {
    return new CommutativeTwoPhaseSetUpdate<>(identifier, version,
        SetUpdateMessage.Operation.REMOVE, new HashSet<>(elements));
  }

}
