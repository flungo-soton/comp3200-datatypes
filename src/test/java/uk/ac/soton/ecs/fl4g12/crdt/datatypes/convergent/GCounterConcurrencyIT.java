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

import uk.ac.soton.ecs.fl4g12.crdt.datatypes.IncrementableCounterConcurrencyAbstractIT;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.NullStateDeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.StateDeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.idenitifier.IdentifierFactory;
import uk.ac.soton.ecs.fl4g12.crdt.idenitifier.IncrementalIntegerIdentifierFactory;

/**
 * Test/Benchmark for concurrent operations on a {@link GCounter}.
 */
public class GCounterConcurrencyIT
    extends IncrementableCounterConcurrencyAbstractIT<Integer, GCounter<Integer, Integer>> {

  private static IdentifierFactory<Integer> ID_FACTORY = new IncrementalIntegerIdentifierFactory();

  @Override
  public GCounter<Integer, Integer> getCounter() {
    StateDeliveryChannel<Integer, GCounterState<Integer, Integer>> deliveryChannel =
        new NullStateDeliveryChannel<>(ID_FACTORY);
    return GCounter.newIntegerGCounter(deliveryChannel);
  }

  @Override
  public Integer getValue(int count) {
    return count;
  }

}
