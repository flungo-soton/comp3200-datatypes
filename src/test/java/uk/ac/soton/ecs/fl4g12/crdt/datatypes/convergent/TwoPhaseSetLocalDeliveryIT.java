/*
 * The MIT License
 *
 * Copyright 2017 Fabrizio Lungo <fl4g12@ecs.soton.ac.uk>.
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

import org.junit.After;
import org.junit.Before;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.GrowableConflictFreeSetAbstractIT;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.local.LocalDeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.idenitifier.IncrementalIntegerIdentifierFactory;
import uk.ac.soton.ecs.fl4g12.crdt.order.IntegerVersion;

/**
 * Test the integration of {@linkplain TwoPhaseSet}s and the {@linkplain LocalDeliveryChannel} for
 * various operations. Ensures that the {@link TwoPhaseSet} is replicated as expected over the
 * {@linkplain LocalDeliveryChannel} and that the state converges to the expected state after a
 * series of operations.
 */
public class TwoPhaseSetLocalDeliveryIT extends
    GrowableConflictFreeSetAbstractIT<Integer, Integer, Integer, TwoPhaseSetState<Integer, Integer, Integer>, TwoPhaseSet<Integer, Integer, Integer>> {

  private static final IncrementalIntegerIdentifierFactory ID_FACTORY =
      new IncrementalIntegerIdentifierFactory();

  private LocalDeliveryChannel<Integer, TwoPhaseSetState<Integer, Integer, Integer>> deliveryChannel;

  @Before
  public void setupDeliveryChannel() {
    deliveryChannel = new LocalDeliveryChannel<>(ID_FACTORY);
  }

  @After
  public void teardownDeliveryChannel() {
    deliveryChannel = null;
  }

  @Override
  public TwoPhaseSet<Integer, Integer, Integer> getSet() {
    return new TwoPhaseSet<>(new IntegerVersion(), null, getDeliveryChannel());
  }

  @Override
  public LocalDeliveryChannel<Integer, TwoPhaseSetState<Integer, Integer, Integer>> getDeliveryChannel() {
    return deliveryChannel;
  }

  @Override
  public Integer getElement(int i) {
    return i;
  }

}
