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

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.mockito.Mockito;
import uk.ac.soton.ecs.fl4g12.crdt.order.IntegerVersion;
import uk.ac.soton.ecs.fl4g12.crdt.order.Version;

/**
 *
 */
public class PeriodicReliableDeliveryChannelTest extends
    ReliableDeliveryChannelAbstractTest<Integer, VersionedUpdateMessage<Integer, ?>, PeriodicReliableDeliveryChannel<Integer, VersionedUpdateMessage<Integer, ?>>> {

  private static final Logger LOGGER =
      Logger.getLogger(PeriodicReliableDeliveryChannelTest.class.getName());

  private static final long CHANNEL_PERIOD = BUFFER_TIME / 10;
  private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

  @Override
  public Integer getIdentifier(int i) {
    return i;
  }

  @Override
  public VersionedUpdateMessage<Integer, ?> getUpdateMessage(Integer identifier, Version version) {
    return new BasicVersionedUpdateMessage<>(identifier, version);
  }

  @Override
  public Version getVersion(int order) {
    IntegerVersion version = new IntegerVersion();
    version.sync(order);
    return version;
  }

  @Override
  public PeriodicReliableDeliveryChannel<Integer, VersionedUpdateMessage<Integer, ?>> getDeliveryChannel() {
    DeliveryExchange<Integer, VersionedUpdateMessage<Integer, ?>> exchange =
        Mockito.mock(DeliveryExchange.class);
    return new PeriodicReliableDeliveryChannel<>(exchange, CHANNEL_PERIOD, TIME_UNIT);
  }

}
