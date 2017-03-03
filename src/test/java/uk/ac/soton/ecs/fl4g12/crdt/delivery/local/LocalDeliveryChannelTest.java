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

package uk.ac.soton.ecs.fl4g12.crdt.delivery.local;

import java.util.EnumMap;
import java.util.Map;
import org.junit.After;
import org.mockito.Mockito;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannelAbstractTest;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannelAbstractTest.TestUpdateMessage;

/**
 * Tests for {@link LocalDeliveryChannel}.
 */
public final class LocalDeliveryChannelTest extends
    DeliveryChannelAbstractTest<Integer, TestUpdateMessage<Integer>, DeliveryChannel<Integer, TestUpdateMessage<Integer>>> {

  @Override
  public Integer getIdentifier(int i) {
    return (Integer) i;
  }

  public TestUpdateMessage<Integer> getUpdateMessage(final Integer id, final int order) {
    return Mockito.spy(new TestUpdateMessage(id, order));
  }

  private final Map<Channel, DeliveryChannel<Integer, TestUpdateMessage<Integer>>> channels =
      new EnumMap<>(Channel.class);

  @Override
  public synchronized DeliveryChannel<Integer, TestUpdateMessage<Integer>> getDeliveryChannel(
      Channel channel, int i) {
    if (channels.containsKey(channel)) {
      return channels.get(channel);
    }
    DeliveryChannel<Integer, TestUpdateMessage<Integer>> deliveryChannel =
        new LocalDeliveryChannel<>(new TestIdentifierFactory());
    channels.put(channel, deliveryChannel);
    return deliveryChannel;
  }

  @After
  public synchronized void clearDeliveryChannelCache() {
    channels.clear();
  }


}
