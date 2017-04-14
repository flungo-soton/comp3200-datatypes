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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import static uk.ac.soton.ecs.fl4g12.crdt.datatypes.IncrementableCounterConcurrencyAbstractIT.THREADS;
import uk.ac.soton.ecs.fl4g12.crdt.util.ConcurrencyTestUtil;

/**
 * Test/Benchmark for concurrent operations on a {@link Counter}.
 *
 * @param <E> the type of counter value that the test uses.
 * @param <C> the type of the counter being tested.
 */
public abstract class CounterConcurrencyAbstractIT<E, C extends Counter<E>>
    extends IncrementableCounterConcurrencyAbstractIT<E, C> {

  private static final Logger LOGGER =
      Logger.getLogger(CounterConcurrencyAbstractIT.class.getName());

  @Override
  public final E getValue(int count) {
    return getValue(count, 0);
  }

  @Override
  public abstract E getValue(int increments, int decrements);

  private void testDecrement(int decrements) throws Exception {
    C counter = getCounter();

    // Setup the threads
    Collection<Thread> threads = new HashSet<>();
    for (int i = 0; i < THREADS; i++) {
      threads.add(new Thread(new CounterDecrementRunnable(counter, decrements)));
    }

    // Start the threads and wait
    ConcurrencyTestUtil.startAll(threads);
    ConcurrencyTestUtil.joinAll(threads);

    // Make assertions
    Assert.assertEquals(getValue(0, THREADS * decrements), counter.value());
  }

  /**
   * Test 10 decrements per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testDecrement_10() throws Exception {
    LOGGER.log(Level.INFO, "Test 10 decrements per thread");
    testDecrement(10);
  }

  /**
   * Test 1000 decrements per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testDecrement_1000() throws Exception {
    LOGGER.log(Level.INFO, "Test 1000 decrements per thread");
    testDecrement(1000);
  }

  /**
   * Test 100000 decrements per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testDecrement_100000() throws Exception {
    LOGGER.log(Level.INFO, "Test  100000 decrements per thread");
    testDecrement(100000);
  }

  /**
   * Test 10000000 decrements per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testDecrement_10000000() throws Exception {
    LOGGER.log(Level.INFO, "Test 100000000 decrements per thread");
    testDecrement(10000000);
  }

  private void testIncrementDecrement(int increments, int decrements) throws Exception {
    C counter = getCounter();

    // Setup the threads
    Collection<Thread> threads = new HashSet<>();
    for (int i = 0; i < THREADS; i++) {
      threads.add(new Thread(new CounterIncrementRunnable(counter, increments)));
      threads.add(new Thread(new CounterDecrementRunnable(counter, decrements)));
    }

    // Start the threads and wait
    ConcurrencyTestUtil.startAll(threads);
    ConcurrencyTestUtil.joinAll(threads);

    // Make assertions
    Assert.assertEquals(getValue(THREADS * increments, THREADS * decrements), counter.value());
  }

  /**
   * Test 10 increments and 10 decrements per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testIncrementDecrement_10_10() throws Exception {
    LOGGER.log(Level.INFO, "Test 10 increments and 10 decrements per thread");
    testIncrementDecrement(10, 10);
  }

  /**
   * Test 10 increments and 1000 decrements per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testIncrementDecrement_10_1000() throws Exception {
    LOGGER.log(Level.INFO, "Test 10 increments and 1000 decrements per thread");
    testIncrementDecrement(10, 1000);
  }

  /**
   * Test 10 increments and 100000 decrements per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testIncrementDecrement_10_100000() throws Exception {
    LOGGER.log(Level.INFO, "Test 10 increments and 100000 decrements per thread");
    testIncrementDecrement(10, 100000);
  }

  /**
   * Test 10 increments and 10000000 decrements per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testIncrementDecrement_10_10000000() throws Exception {
    LOGGER.log(Level.INFO, "Test 10 increments and 10000000 decrements per thread");
    testIncrementDecrement(10, 10000000);
  }

  /**
   * Test 1000 increments and 10 decrements per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testIncrementDecrement_1000_10() throws Exception {
    LOGGER.log(Level.INFO, "Test 10 increments and 10 decrements per thread");
    testIncrementDecrement(1000, 10);
  }

  /**
   * Test 1000 increments and 1000 decrements per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testIncrementDecrement_1000_1000() throws Exception {
    LOGGER.log(Level.INFO, "Test 1000 increments and 1000 decrements per thread");
    testIncrementDecrement(1000, 1000);
  }

  /**
   * Test 1000 increments and 100000 decrements per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testIncrementDecrement_1000_100000() throws Exception {
    LOGGER.log(Level.INFO, "Test 1000 increments and 100000 decrements per thread");
    testIncrementDecrement(1000, 100000);
  }

  /**
   * Test 1000 increments and 1000000 decrements per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testIncrementDecrement_1000_10000000() throws Exception {
    LOGGER.log(Level.INFO, "Test 1000 increments and 10000000 decrements per thread");
    testIncrementDecrement(1000, 10000000);
  }

  /**
   * Test 100000 increments and 10 decrements per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testIncrementDecrement_100000_10() throws Exception {
    LOGGER.log(Level.INFO, "Test 100000 increments and 10 decrements per thread");
    testIncrementDecrement(100000, 10);
  }

  /**
   * Test 100000 increments and 1000 decrements per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testIncrementDecrement_100000_1000() throws Exception {
    LOGGER.log(Level.INFO, "Test 100000 increments and 1000 decrements per thread");
    testIncrementDecrement(100000, 1000);
  }

  /**
   * Test 100000 increments and 100000 decrements per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testIncrementDecrement_100000_100000() throws Exception {
    LOGGER.log(Level.INFO, "Test 100000 increments and 100000 decrements per thread");
    testIncrementDecrement(100000, 100000);
  }

  /**
   * Test 100000 increments and 10000000 decrements per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testIncrementDecrement_100000_10000000() throws Exception {
    LOGGER.log(Level.INFO, "Test 100000 increments and 10000000 decrements per thread");
    testIncrementDecrement(100000, 10000000);
  }

  /**
   * Test 10000000 increments and 10 decrements per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testIncrementDecrement_10000000_10() throws Exception {
    LOGGER.log(Level.INFO, "Test 10000000 increments and 10 decrements per thread");
    testIncrementDecrement(10000000, 10);
  }

  /**
   * Test 10000000 increments and 1000 decrements per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testIncrementDecrement_10000000_1000() throws Exception {
    LOGGER.log(Level.INFO, "Test 10000000 increments and 1000 decrements per thread");
    testIncrementDecrement(10000000, 1000);
  }

  /**
   * Test 10000000 increments and 10 decrements per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testIncrementDecrement_10000000_100000() throws Exception {
    LOGGER.log(Level.INFO, "Test 10000000 increments and 100000 decrements per thread");
    testIncrementDecrement(10000000, 100000);
  }

  /**
   * Test 10000000 increments and 10000000 decrements per thread.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testIncrementDecrement_10000000_10000000() throws Exception {
    LOGGER.log(Level.INFO, "Test 10000000 increments and 10000000 decrements per thread");
    testIncrementDecrement(10000000, 10000000);
  }

  protected class CounterDecrementRunnable implements Runnable {

    private final C counter;
    private final int decrements;

    public CounterDecrementRunnable(C counter, int decrements) {
      this.counter = counter;
      this.decrements = decrements;
    }

    @Override
    public void run() {
      for (int i = 0; i < decrements; i++) {
        counter.decrement();
      }
    }

  }

}
