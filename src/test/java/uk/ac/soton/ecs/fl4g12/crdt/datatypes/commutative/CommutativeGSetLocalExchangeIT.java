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

package uk.ac.soton.ecs.fl4g12.crdt.datatypes.commutative;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.GrowableConflictFreeSetAbstractIT;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.LocalDeliveryExchange;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.PeriodicReliableDeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.idenitifier.IncrementalIntegerIdentifierFactory;
import uk.ac.soton.ecs.fl4g12.crdt.order.IntegerVersion;

/**
 * Test the integration of {@linkplain CommutativeGSet}s and the {@link LocalDeliveryExchange} for
 * various operations. Ensures that the {@link CommutativeGSet} is replicated as expected over the
 * {@link LocalDeliveryExchange} and that the state converges to the expected state after a series
 * of operations.
 */
public class CommutativeGSetLocalExchangeIT extends
    GrowableConflictFreeSetAbstractIT<Integer, Integer, Integer, CommutativeGSetUpdate<Integer, Integer, Integer>, CommutativeGSet<Integer, Integer, Integer>> {

  private static final Logger LOGGER =
      Logger.getLogger(CommutativeGSetLocalExchangeIT.class.getName());

  private static final IncrementalIntegerIdentifierFactory ID_FACTORY =
      new IncrementalIntegerIdentifierFactory();
  private static final long EXCHANGE_PERIOD = 100;
  private static final long CHANNEL_PERIOD = 100;
  private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

  private LocalDeliveryExchange<Integer, CommutativeGSetUpdate<Integer, Integer, Integer>> deliveryExchange;

  @Before
  public void setupDeliveryExchange() {
    deliveryExchange = new LocalDeliveryExchange<>(ID_FACTORY, EXCHANGE_PERIOD, TIME_UNIT);
  }

  @After
  public void teardownDeliveryExchange() {
    try {
      deliveryExchange.close();
    } catch (Exception ex) {
      LOGGER.log(Level.SEVERE,
          "DeliveryExchange could not be shutdown between tests: may still be running", ex);
    }
    deliveryExchange = null;
  }

  @Override
  public CommutativeGSet<Integer, Integer, Integer> getSet() {
    return new CommutativeGSet<>(new IntegerVersion(), null, getDeliveryChannel());
  }

  @Override
  public PeriodicReliableDeliveryChannel<Integer, CommutativeGSetUpdate<Integer, Integer, Integer>> getDeliveryChannel() {
    return new PeriodicReliableDeliveryChannel<>(deliveryExchange, CHANNEL_PERIOD, TIME_UNIT);
  }

  @Override
  public Integer getElement(int i) {
    return i;
  }

}
