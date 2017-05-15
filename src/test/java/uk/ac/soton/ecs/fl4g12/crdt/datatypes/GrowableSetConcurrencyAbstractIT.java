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
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import uk.ac.soton.ecs.fl4g12.crdt.util.ConcurrencyTestUtil;

/**
 *
 *
 * @param <E> the type of values stored in the {@link Set}.
 * @param <S> the type of {@link Set} being tested.
 */
public abstract class GrowableSetConcurrencyAbstractIT<E, S extends Set<E>>
    implements SetTestInterface<E, S> {

  private static final Logger LOGGER =
      Logger.getLogger(GrowableSetConcurrencyAbstractIT.class.getName());

  public static final int THREADS = 10;

  @Rule
  public Timeout timeout = new Timeout(60, TimeUnit.SECONDS);

  private void testAdd(int elements) throws Exception {
    S set = getSet();
    Set<E> expected = new HashSet<>();

    // Setup the threads
    Collection<Thread> threads = new HashSet<>();
    for (int i = 0; i < THREADS; i++) {
      int start = i * elements;
      int stop = (i + 1) * elements;
      // Create the thread
      threads.add(new Thread(new SetAddRunnable(set, start, stop)));
      // Add the expected elements that this thread will create
      for (int j = start; j < stop; j++) {
        expected.add(getElement(j));
      }
    }

    // Start the threads and wait
    ConcurrencyTestUtil.startAll(threads);
    ConcurrencyTestUtil.joinAll(threads);

    // Make assertions
    Assert.assertEquals(expected, set);
  }

  /**
   * Test adding 10 elements to a set.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testAdd_10() throws Exception {
    LOGGER.log(Level.INFO, "testAdd_10: Test adding 10 elements to a set");
    testAdd(10);
  }

  /**
   * Test adding 1000 elements to a set.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testAdd_1000() throws Exception {
    LOGGER.log(Level.INFO, "testAdd_1000: Test adding 1000 elements to a set");
    testAdd(1000);
  }

  /**
   * Test adding 100000 elements to a set.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testAdd_100000() throws Exception {
    LOGGER.log(Level.INFO, "testAdd_100000: Test adding 100000 elements to a set");
    testAdd(100000);
  }

  /**
   * Test adding 10000000 elements to a set.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testAdd_10000000() throws Exception {
    LOGGER.log(Level.INFO, "testAdd_10000000: Test adding 10000000 elements to a set");
    testAdd(10000000);
  }

  // TODO: Tests with addAll

  protected class SetAddRunnable implements Runnable {

    private final S set;
    private final int start;
    private final int stop;

    public SetAddRunnable(S set, int start, int stop) {
      this.set = set;
      this.start = start;
      this.stop = stop;
    }

    @Override
    public void run() {
      for (int i = start; i < stop; i++) {
        set.add(getElement(i));
      }
    }

  }
}
