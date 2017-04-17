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

package uk.ac.soton.ecs.fl4g12.crdt.datatypes.convergent;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.GrowableConflictFreeSetAbstractIT;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.PeriodicStateDeiveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.LocalDeliveryExchange;
import uk.ac.soton.ecs.fl4g12.crdt.idenitifier.IncrementalIntegerIdentifierFactory;
import uk.ac.soton.ecs.fl4g12.crdt.order.IntegerVersion;

/**
 * Test the integration of {@linkplain GSet}s and the {@link PeriodicStateDeiveryChannel} for
 * various operations. Ensures that the {@link GSet} is replicated as expected over the
 * {@link PeriodicStateDeiveryChannel} and that the state converges to the expected state after a
 * series of operations.
 */
public class GSetLocalExchangeIT extends
    GrowableConflictFreeSetAbstractIT<Integer, Integer, Integer, GSetState<Integer, Integer, Integer>, GSet<Integer, Integer, Integer>> {

  private static final Logger LOGGER = Logger.getLogger(GSetLocalExchangeIT.class.getName());

  private static final IncrementalIntegerIdentifierFactory ID_FACTORY =
      new IncrementalIntegerIdentifierFactory();

  private static final long DELIVERY_PERIOD = 100;
  private static final long EXCHANGE_PERIOD = 100;
  private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

  private LocalDeliveryExchange<Integer, GSetState<Integer, Integer, Integer>> exchange;

  @Before
  public void setupExchange() {
    exchange = new LocalDeliveryExchange<>(ID_FACTORY, EXCHANGE_PERIOD, TIME_UNIT);
  }

  @After
  public void teardownExchange() {
    try {
      exchange.close();
    } catch (Exception ex) {
      LOGGER.log(Level.SEVERE,
          "DeliveryExchange could not be shut down between tests, may still be running", ex);
    }
    exchange = null;
  }

  @Override
  public GSet<Integer, Integer, Integer> getSet() {
    return new GSet<>(new IntegerVersion(), null, getDeliveryChannel());
  }

  @Override
  public PeriodicStateDeiveryChannel<Integer, GSetState<Integer, Integer, Integer>> getDeliveryChannel() {
    return new PeriodicStateDeiveryChannel<>(exchange, DELIVERY_PERIOD, TIME_UNIT);
  }

  @Override
  public Integer getElement(int i) {
    return i;
  }

}
