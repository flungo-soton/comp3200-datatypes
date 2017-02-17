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

package uk.ac.soton.ecs.fl4g12.crdt.delivery;

import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * An {@link Updatable} object that keeps a {@link VersionVector}. The version can be used to track
 * the changes in the {@link Updatable} object and determine causality between events and states of
 * the object.
 *
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp within the {@link VersionVector}
 * @param <U> the type of updates sent via the delivery channel.
 */
public interface VersionedUpdatable<K, T extends Comparable<T>, U extends UpdateMessage<K>>
    extends Updatable<K, U> {

  /**
   * Get a copy of the current version of the {@linkplain VersionedUpdatable}. The version is
   * tracked and represented as a {@link VersionVector}.
   *
   * @return a copy of the current version of this object.
   */
  VersionVector<K, T> getVersion();

}
