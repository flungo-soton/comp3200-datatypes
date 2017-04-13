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

import uk.ac.soton.ecs.fl4g12.crdt.order.Dot;

/**
 * Abstract implementation of {@linkplain DottedUpdateMessage}.
 *
 * @param <K> the type of the identifier.
 * @param <T> the type of the timestamp.
 */
public class AbstractDottedUpdateMessage<K, T extends Comparable<T>>
    extends AbstractVersionedUpdateMessage<K, Dot<K, T>> implements DottedUpdateMessage<K, T> {

  /**
   * Instantiate a {@linkplain AbstractDottedUpdateMessage} with the given {@linkplain Dot} as the
   * version. The {@link UpdateMessage} identifier will be captured from the {@link Dot} using
   * {@link Dot#getIdentifier()}.
   *
   * @param dot the {@link Dot} for the update.
   */
  public AbstractDottedUpdateMessage(Dot<K, T> dot) {
    super(dot.getIdentifier(), dot);
  }

}
