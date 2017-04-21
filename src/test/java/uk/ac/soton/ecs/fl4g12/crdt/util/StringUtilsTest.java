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
package uk.ac.soton.ecs.fl4g12.crdt.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests for the {@link StringUtils}.
 */
public class StringUtilsTest {

  private static final Logger LOGGER = Logger.getLogger(StringUtilsTest.class.getName());

  private void testGetCollectionString(Collection collection, int elements, String values) {
    String expResult = "ArrayList{size=" + elements + ", elements=" + values + "}";
    assertEquals(expResult, StringUtils.getCollectionString(collection));
  }

  /**
   * Test of getCollectionString with an empty collection.
   */
  @Test
  public void testGetCollectionString_Empty() {
    LOGGER.log(Level.INFO, "testGetCollectionString_Empty");

    testGetCollectionString(Arrays.asList(), 0, "[]");
  }


  /**
   * Test of getCollectionString with a single element collection.
   */
  @Test
  public void testGetCollectionString_1() {
    LOGGER.log(Level.INFO, "testGetCollectionString_1");

    testGetCollectionString(Arrays.asList(0), 1, "[0]");
  }


  /**
   * Test of getCollectionString with a two element collection.
   */
  @Test
  public void testGetCollectionString_2() {
    LOGGER.log(Level.INFO, "testGetCollectionString_2");

    testGetCollectionString(Arrays.asList(0, 1), 2, "[0, 1]");
  }


  /**
   * Test of getCollectionString with a three element collection.
   */
  @Test
  public void testGetCollectionString_3() {
    LOGGER.log(Level.INFO, "testGetCollectionString_3");

    testGetCollectionString(Arrays.asList(0, 1, 2), 3, "[0, 1, 2]");
  }


  /**
   * Test of getCollectionString with a four element collection.
   */
  @Test
  public void testGetCollectionString_4() {
    LOGGER.log(Level.INFO, "testGetCollectionString_4");

    testGetCollectionString(Arrays.asList(0, 1, 2, 3), 4, "[0, 1, 2, ...]");
  }


  /**
   * Test of getCollectionString with a 100 element collection.
   */
  @Test
  public void testGetCollectionString_100() {
    LOGGER.log(Level.INFO, "testGetCollectionString_Empty");

    final int elements = 100;
    ArrayList<Integer> collection = new ArrayList<>(elements);
    for (int i = 0; i < elements; i++) {
      collection.add(i);
    }
    testGetCollectionString(collection, elements, "[0, 1, 2, ...]");
  }

}
