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

package uk.ac.soton.ecs.fl4g12.crdt.datatypes;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests for implementations of {@linkplain Counter}.
 */
public abstract class CounterAbstractTest<E, C extends Counter<E>>
    extends IncrementableCounterAbstractTest<E, C> {

  private static final Logger LOGGER = Logger.getLogger(CounterAbstractTest.class.getName());

  /**
   * Get the value that a counter should have after a given number of increments and decrements.
   *
   * @param increments the number of increments.
   * @param decrements the number of decrements.
   * @return the expected value of the counter
   */
  protected abstract E getValue(int increments, int decrements);

  @Override
  protected final E getValue(int count) {
    return getValue(count, 0);
  }

  /**
   * Test that the counter decrements as expected.
   */
  @Override
  @Test
  public void testDecrement() {
    LOGGER.log(Level.INFO, "testDecrement: Test that the counter decrements as expected.");
    C instance = getCounter();
    assertEquals(getValue(0), instance.value());

    for (int i = 1; i <= MAX_OPERATIONS; i++) {
      instance.decrement();
      assertEquals(getValue(0, i), instance.value());
    }
  }

  /**
   * Test incrementing and decrementing.
   */
  @Test
  public void testWeave() {
    LOGGER.log(Level.INFO, "testWeave: Test incrementing and decrementing.");

    C instance = getCounter();
    int increments = 0;
    int decrements = 0;

    for (int i = 0; i <= MAX_OPERATIONS / 20; i++) {
      for (int j = 0; j < i; j++) {
        instance.increment();
        assertEquals(getValue(++increments, decrements), instance.value());
      }
      for (int j = 0; j < 2 * i; j++) {
        instance.decrement();
        assertEquals(getValue(increments, ++decrements), instance.value());
      }
      for (int j = 0; j < i; j++) {
        instance.increment();
        assertEquals(getValue(++increments, decrements), instance.value());
      }
    }
  }

  /**
   * Test random incrementing and decrementing.
   */
  @Test
  public void testRandom() {
    LOGGER.log(Level.INFO, "testRandom: Test random incrementing and decrementing.");

    C instance = getCounter();
    int increments = 0;
    int decrements = 0;
    Random rand = new Random();

    for (int i = 0; i <= MAX_OPERATIONS; i++) {
      if (rand.nextFloat() > 0.5f) {
        instance.increment();
        increments++;
      } else {
        instance.decrement();
        decrements++;
      }
      assertEquals(getValue(increments, decrements), instance.value());
    }
  }

}
