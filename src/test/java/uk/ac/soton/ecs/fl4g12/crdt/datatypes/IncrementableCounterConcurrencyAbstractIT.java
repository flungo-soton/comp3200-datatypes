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

package uk.ac.soton.ecs.fl4g12.crdt.datatypes;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import uk.ac.soton.ecs.fl4g12.crdt.util.ConcurrencyTestUtil;

/**
 * Test/Benchmark for concurrent operations on an incrementable {@link Counter}.
 *
 * @param <E> the type of counter value that the test uses.
 * @param <C> the type of the counter being tested.
 */
public abstract class IncrementableCounterConcurrencyAbstractIT<E, C extends Counter<E>>
    implements CounterTestInterface<E, C> {

  private static final Logger LOGGER =
      Logger.getLogger(IncrementableCounterConcurrencyAbstractIT.class.getName());

  public static int THREADS = 10;

  @Rule
  public Timeout timeout = new Timeout(60, TimeUnit.SECONDS);

  @Override
  public E getValue(int increments, int decrements) {
    if (decrements != 0) {
      throw new UnsupportedOperationException("Increment only!");
    }
    return getValue(increments);
  }

  private void testIncrement(int increments) throws Exception {
    C counter = getCounter();

    // Setup the threads
    Collection<Thread> threads = new HashSet<>();
    for (int i = 0; i < THREADS; i++) {
      threads.add(new Thread(new CounterIncrementRunnable(counter, increments)));
    }

    // Start the threads and wait
    ConcurrencyTestUtil.startAll(threads);
    ConcurrencyTestUtil.joinAll(threads);

    // Make assertions
    Assert.assertEquals(getValue(THREADS * increments), counter.value());
  }

  /**
   * Test 10 increments per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testIncrement_10() throws Exception {
    LOGGER.log(Level.INFO, "Test 10 increments per thread");
    testIncrement(10);
  }

  /**
   * Test 1000 increments per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testIncrement_1000() throws Exception {
    LOGGER.log(Level.INFO, "Test 1000 increments per thread");
    testIncrement(1000);
  }

  /**
   * Test 100000 increments per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testIncrement_100000() throws Exception {
    LOGGER.log(Level.INFO, "Test 100000 increments per thread");
    testIncrement(100000);
  }

  /**
   * Test 10000000 increments per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testIncrement_10000000() throws Exception {
    LOGGER.log(Level.INFO, "Test 100000000 increments per thread");
    testIncrement(10000000);
  }

  protected class CounterIncrementRunnable implements Runnable {

    private final C counter;
    private final int increments;

    public CounterIncrementRunnable(C counter, int increments) {
      this.counter = counter;
      this.increments = increments;
    }

    @Override
    public void run() {
      for (int i = 0; i < increments; i++) {
        counter.increment();
      }
    }

  }

}
