/*
 * The MIT License
 *
 * Copyright 2016 Fabrizio Lungo <fl4g12@ecs.soton.ac.uk>.
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

import uk.ac.soton.ecs.fl4g12.crdt.datatypes.CRDT;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DottedUpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.order.Version;

/**
 * Interface for Commutative Replicated Data Types.
 *
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp used in the {@link Version}.
 * @param <V> the type of the {@link Version}
 * @param <U> the type of updates which this object can be updated by.
 */
public interface CmRDT<K, T extends Comparable<T>, V extends Version<T, ? super V, V>, U extends DottedUpdateMessage<K, T>>
    extends CRDT<K, U> {

}
