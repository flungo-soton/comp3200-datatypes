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

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Abstract tests for {@linkplain Set} implementations.
 *
 * @param <E> the type of set value that the test uses.
 * @param <S> the type of the set being tested.
 */
public abstract class SetAbstractTest<E, S extends Set<E>> extends AddOnceSetAbstractTest<E, S> {

  private static final Logger LOGGER = Logger.getLogger(AddOnceSetAbstractTest.class.getName());

  public SetAbstractTest(Class<E> elementClass, Class<E[]> elementArrayClass) {
    super(elementClass, elementArrayClass);
  }

  /**
   * Test the size of the set as elements are added and removed.
   */
  @Test
  public void testSize_AddRemoveRepeat() {
    LOGGER.log(Level.INFO,
        "testSize_AddRemove: Test the size of the set as elements are added and removed.");

    S set = getSet();
    int expected = 0;

    // How many elements to add
    for (int i = 1; i <= MAX_OPERATIONS; i++) {
      // How many elements to remove
      for (int j = 1; j <= MAX_OPERATIONS; j++) {

        // Populate the set
        for (int k = 1; k <= i; k++) {
          if (set.add(getElement(k))) {
            expected++;
          }
          assertEquals(expected, set.size()); // TODO: Calculate mathematically and remove expected
                                              // variable and dependence on the return of add/remove
        }

        // Remove elements
        for (int k = 1; k <= j; k++) {
          if (set.remove(getElement(k))) {
            expected--;
          }
          assertEquals(i < k ? 0 : i - k, set.size());
        }
      }
    }
  }

}
