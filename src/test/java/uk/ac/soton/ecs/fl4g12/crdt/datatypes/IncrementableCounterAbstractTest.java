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

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for increment only implementations of {@linkplain Counter}.
 */
public abstract class IncrementableCounterAbstractTest<E, C extends Counter<E>> {

  private static final Logger LOGGER =
      Logger.getLogger(IncrementableCounterAbstractTest.class.getName());

  public static final int MAX_OPERATIONS = 1000;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Get the counter instance for testing.
   *
   * @return a counter to be tested.
   */
  protected abstract C getCounter();

  /**
   *
   * @param count the number of increments.
   * @return the value the counter should have after the specified number of increments.
   */
  protected abstract E getValue(int count);

  /**
   * Test that the counter increments as expected.
   */
  @Test
  public void testIncrement() {
    LOGGER.log(Level.INFO, "testIncrement: Test that the counter increments as expected.");
    C instance = getCounter();
    assertEquals(getValue(0), instance.value());

    for (int i = 1; i <= MAX_OPERATIONS; i++) {
      instance.increment();
      assertEquals(getValue(i), instance.value());
    }
  }

  /**
   * Test that the counter can only be incremented. Trying to decrement should throw an exception.
   */
  @Test
  public void testDecrement() {
    LOGGER.log(Level.INFO, "testDecrement: Test that the counter can only be incremented.");
    C instance = getCounter();

    thrown.expect(UnsupportedOperationException.class);
    instance.decrement();
  }

  /**
   * Test of value method, of class Counter.
   */
  @Test
  public void testValue_Initial() {
    LOGGER.log(Level.INFO, "testValue_Initial: Test the initial state of the counter");
    C instance = getCounter();

    assertEquals(getValue(0), instance.value());
  }

}
