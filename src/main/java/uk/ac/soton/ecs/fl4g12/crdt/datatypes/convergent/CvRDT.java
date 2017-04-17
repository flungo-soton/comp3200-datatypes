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

package uk.ac.soton.ecs.fl4g12.crdt.datatypes.convergent;

import uk.ac.soton.ecs.fl4g12.crdt.datatypes.CRDT;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.StateSnapshot;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.StatefulUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.UpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.order.Version;

/**
 * Interface for Convergent Replicated Data Types. {@linkplain CvRDT}s are based on
 * {@link StatefulUpdatable}s and use {@link StateSnapshot}s to communicate changes made.
 *
 * @param <K> the type of identifier used to identify nodes.
 * @param <V> the type of the {@link Version}.
 * @param <M> the type of the {@link StateSnapshot} which is taken and used as the
 *        {@link UpdateMessage}.
 */
public interface CvRDT<K, V extends Version, M extends StateSnapshot<K, V>>
    extends CRDT<K, M>, StatefulUpdatable<K, V, M> {

}
