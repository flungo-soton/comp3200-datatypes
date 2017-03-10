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

package uk.ac.soton.ecs.fl4g12.crdt.idenitifier;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Abstract tests for incremental {@linkplain IdentifierFactory}.
 * 
 * @param <I> the type of the {@link IdentifierFactory} being tested.
 */
public abstract class IncrementalIdentifierFactoryAbstractTest<I extends IdentifierFactory<? extends Number>> {

  private static final Logger LOGGER =
      Logger.getLogger(IncrementalIdentifierFactoryAbstractTest.class.getName());

  /**
   * Instantiate the {@linkplain IdentifierFactory}.
   *
   * @return the {@link IdentifierFactory} to be tested.
   */
  protected abstract I getIdentifierFactory();

  /**
   * Test of create method, of class IncrementalIntegerIdentifierFactory.
   */
  @Test
  public void testCreate() {
    LOGGER.log(Level.INFO, "create");
    I identifierFactory = getIdentifierFactory();
    for (long i = 0; i < 100; i++) {
      assertEquals(i, identifierFactory.create().longValue());
    }
  }

}
