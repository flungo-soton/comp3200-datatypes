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
import static org.junit.Assert.assertSame;
import org.junit.Test;
import static uk.ac.soton.ecs.fl4g12.crdt.datatypes.RegisterAbstractTest.MAX_OPERATIONS;

/**
 * Tests for the {@linkplain AtomicRegister}.
 */
public class AtomicRegisterTest extends RegisterAbstractTest<Object, AtomicRegister<Object>> {

  private static final Logger LOGGER = Logger.getLogger(AtomicRegisterTest.class.getName());

  @Override
  protected AtomicRegister<Object> getRegister() {
    return new AtomicRegister<>();
  }

  @Override
  protected Object getValue(int i) {
    return new Object();
  }

  /**
   * Test of value method, of class {@linkplain Register}.
   */
  @Test
  public void testValue_AssignedInitial() {
    LOGGER.log(Level.INFO,
        "testValue_AssignedInitial: Test the initial state of the register when constructed with a value");

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      Object value = getValue(i);
      AtomicRegister<Object> instance = new AtomicRegister<Object>(value);
      assertSame(value, instance.value());
    }
  }

}
