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

import uk.ac.soton.ecs.fl4g12.crdt.order.IntegerVersion;
import uk.ac.soton.ecs.fl4g12.crdt.order.LamportTimestamp;

/**
 * Abstract tests for {@linkplain CausalDeliveryChannel} implementations.
 *
 * @param <K> The type of the identifier that is assigned to the {@link Updatable}.
 * @param <U> The type of updates sent via the delivery channel.
 * @param <C> the type of the {@linkplain DeliveryChannel} to be tested.
 */
public abstract class CausalDeliveryChannelAbstractTest<K, U extends VersionedUpdateMessage<K, ?>, C extends CausalDeliveryChannel<K, U>>
    extends DeliveryChannelAbstractTest<K, U, C> {

  /**
   * {@linkplain UpdateMessage} that can be used as part of tests.
   *
   * @param <K> the type of identifier used to identify nodes.
   */
  public static class CausalTestUpdateMessage<K>
      extends AbstractVersionedUpdateMessage<K, LamportTimestamp<Integer>> {

    public CausalTestUpdateMessage(K identifier, int order) {
      super(identifier, getVersion(order));
    }

    private static IntegerVersion getVersion(int order) {
      IntegerVersion version = new IntegerVersion();
      version.sync(order);
      return version;
    }

  }

}
