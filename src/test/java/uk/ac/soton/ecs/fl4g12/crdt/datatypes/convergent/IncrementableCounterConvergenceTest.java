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

package uk.ac.soton.ecs.fl4g12.crdt.datatypes.convergent;

import org.mockito.Mockito;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.Counter;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.IncrementableConflictFreeCounterAbstractTest;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.StateDeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.StateSnapshot;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.StatefulUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Tests to ensure that two incrementable {@linkplain StatefulUpdatable} {@linkplain Counter}s
 * converge under various operations.
 *
 * @param <E> the type of values stored in the {@link Counter}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp within the {@link VersionVector}
 * @param <M> the type of {@link StateSnapshot} made from the {@link StatefulUpdatable}
 *        {@link Counter}.
 * @param <S> the type of {@link Counter} being tested.
 */
public abstract class IncrementableCounterConvergenceTest<E, K, T extends Comparable<T>, M extends StateSnapshot<K, VersionVector<K, T>>, S extends Counter<E> & StatefulUpdatable<K, VersionVector<K, T>, M>>
    extends IncrementableConflictFreeCounterAbstractTest<E, K, T, M, S> {

  public static <K, T extends Comparable<T>, U extends StateSnapshot<K, VersionVector<K, T>>, C extends StatefulUpdatable<K, VersionVector<K, T>, U>> void updateCounter(
      C destination, C source) throws Exception {
    destination.update(source.snapshot());
  }

  @Override
  public M assertPublish(DeliveryChannel<K, M, ?> channel) {
    StateDeliveryChannel<K, M> stateDeliveryChannel = (StateDeliveryChannel<K, M>) channel;
    Mockito.verify(stateDeliveryChannel).publish();
    return stateDeliveryChannel.getUpdatable().snapshot();
  }

  @Override
  protected void intermediateDelivery(S destination, S source) throws Exception {
    updateCounter(destination, source);
  }

}
