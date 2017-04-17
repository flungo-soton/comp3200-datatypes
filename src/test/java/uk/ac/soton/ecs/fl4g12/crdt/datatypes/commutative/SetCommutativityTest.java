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

import java.util.Set;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.ConflictFreeSetAbstractTest;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.order.LogicalVersion;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Tests to ensure that two {@linkplain VersionedUpdatable} {@linkplain Set}s commute under various
 * operations.
 *
 * @param <E> the type of values stored in the {@link Set}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp within the {@link VersionVector}
 * @param <M> the type of {@link GrowableSetUpdateMessage} produced by the
 *        {@link VersionedUpdatable} {@link Set}.
 * @param <S> the type of {@link Set} being tested.
 */
public abstract class SetCommutativityTest<E, K, T extends Comparable<T>, M extends GrowableSetUpdateMessage<E, K, ? extends LogicalVersion<T, ?>>, S extends Set<E> & VersionedUpdatable<K, VersionVector<K, T>, M>>
    extends ConflictFreeSetAbstractTest<E, K, T, M, S> {

  /**
   * Instantiate the abstract tests for {@linkplain VersionedUpdatable} {@linkplain Set}
   * implementations.
   *
   * @param addWins {@code true} if the implementation being tested is an add-wins implementation or
   *        {@code false} if its a remove wins implementation.
   */
  public SetCommutativityTest(boolean addWins) {
    super(addWins);
  }

}
