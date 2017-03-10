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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import org.junit.Test;

/**
 * Abstract tests for {@linkplain Register} implementations.
 *
 * @param <E> the type of register value that the test uses.
 * @param <R> the type of the register being tested.
 */
public abstract class RegisterAbstractTest<E, R extends Register<E>> {

  private static final Logger LOGGER = Logger.getLogger(RegisterAbstractTest.class.getName());

  public static final int MAX_OPERATIONS = 10;

  /**
   * Get the {@linkplain Register} instance for testing.
   *
   * @return a {@link Register} to be tested.
   */
  protected abstract R getRegister();

  /**
   * Get a random value to store in the {@linkplain Register}. {@code i} is provided so that
   * deterministic values can be given for each iteration although, not required.
   *
   * @param i the iteration number.
   * @return a value to store in the register.
   */
  protected abstract E getValue(int i);

  /**
   * Test that the {@linkplain Register} {@linkplain Register#assign(java.lang.Object)} method
   * performs as expected.
   */
  @Test
  public void testAssign() {
    LOGGER.log(Level.INFO, "testAssign: Test that register assignment works as expected.");
    R instance = getRegister();

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      E value = getValue(i);
      instance.assign(value);
      assertSame(value, instance.value());
    }
  }

  /**
   * Test of value method, of class {@linkplain Register}.
   */
  @Test
  public void testValue_Initial() {
    LOGGER.log(Level.INFO, "testValue_Initial: Test the initial state of the register");
    R instance = getRegister();

    assertNull(instance.value());
  }

}
