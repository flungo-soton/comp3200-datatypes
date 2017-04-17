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

package uk.ac.soton.ecs.fl4g12.crdt.delivery;

import uk.ac.soton.ecs.fl4g12.crdt.order.Version;

/**
 * Interface for {@link Updatable}s which have a snapshotable state. A {@link StateSnapshot} can be
 * taken which is also the {@link UpdateMessage} that can be delivered by a {@link DeliveryChannel}
 * to update other {@link StatefulUpdatable}s of the same type.
 *
 * @param <K> the type of identifier used to identify nodes.
 * @param <V> the type of the {@link Version}.
 * @param <M> the type of {@link StateSnapshot} made from this {@link StatefulUpdatable}.
 */
public interface StatefulUpdatable<K, V extends Version, M extends StateSnapshot<K, V>>
    extends VersionedUpdatable<K, V, M> {

  /**
   * Creates a snapshot of the state which can be used as an {@linkplain UpdateMessage}. The
   * snapshot takes a snapshot of the state at a given time.
   *
   * @return a snapshot of the state.
   */
  M snapshot();

}
