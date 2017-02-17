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
package uk.ac.soton.ecs.fl4g12.crdt.order;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests of the {@linkplain AbstractVersion} class.
 */
public class AbstractVersionTest {

  private static final Logger LOGGER = Logger.getLogger(AbstractVersionTest.class.getName());

  public AbstractVersionTest() {}

  private static final Integer INITIAL_VERSION_VALUE = 50;
  private static final Integer INCREMENTED_VERSION_VALUE = INITIAL_VERSION_VALUE + 1;

  private IntegerVersion integerVersion1;
  private IntegerVersion integerVersion2;
  private LongVersion longVersion1;
  private LongVersion longVersion2;

  @BeforeClass
  public static void setUpClass() {}

  @AfterClass
  public static void tearDownClass() {}

  @Before
  public void setUp() {
    // Setup the integer versions
    integerVersion1 = new IntegerVersion();
    integerVersion1.sync(INITIAL_VERSION_VALUE);
    integerVersion2 = integerVersion1.copy();
    integerVersion2.increment();
    // Setup the long versions
    longVersion1 = new LongVersion();
    longVersion1.sync((long) INITIAL_VERSION_VALUE);
    longVersion2 = longVersion1.copy();
    longVersion2.increment();
  }

  @After
  public void tearDown() {}

  @Test
  public void testHappenedBefore_Integer_True() {
    LOGGER.log(Level.INFO,
        "testHappenedBefore_Integer_True: Test happenedBefore for integerVersion1 < integerVersion2");
    boolean result = integerVersion1.happenedBefore(integerVersion2);
    assertEquals("integerVersion1 should have happenedBefore integerVersion2", true, result);
  }

  @Test
  public void testHappenedBefore_Integer_False() {
    LOGGER.log(Level.INFO,
        "testHappenedBefore_Integer_False: Test happenedBefore for integerVersion1 > integerVersion2");
    boolean result = integerVersion2.happenedBefore(integerVersion1);
    assertEquals("integerVersion2 shouldn't have happenedBefore integerVersion1", false, result);
  }

  @Test
  public void testHappenedBefore_Integer_Equal() {
    LOGGER.log(Level.INFO,
        "testHappenedBefore_Integer_Equal: Test happenedBefore for integerVersion1 = integerVersion2");
    boolean result = integerVersion1.happenedBefore(integerVersion1);
    assertEquals("integerVersion1 shouldn't happenedBefore itself", false, result);
    result = integerVersion2.happenedBefore(integerVersion2);
    assertEquals("integerVersion2 shouldn't happenedBefore itself", false, result);
  }

  @Test
  public void testHappenedBefore_Long_True() {
    LOGGER.log(Level.INFO,
        "testHappenedBefore_Long_True: Test happenedBefore for longVersion1 < longVersion2");
    boolean result = longVersion1.happenedBefore(longVersion2);
    assertEquals("longVersion1 should have happenedBefore longVersion2", true, result);
  }

  @Test
  public void testHappenedBefore_Long_False() {
    LOGGER.log(Level.INFO,
        "testHappenedBefore_Long_False: Test happenedBefore for longVersion1 > longVersion2");
    boolean result = longVersion2.happenedBefore(longVersion1);
    assertEquals("longVersion2 shouldn't have happenedBefore longVersion1", false, result);
  }

  @Test
  public void testHappenedBefore_Long_Equal() {
    LOGGER.log(Level.INFO,
        "testHappenedBefore_Long_Equal: Test happenedBefore for longVersion1 = longVersion2");
    boolean result = longVersion1.happenedBefore(longVersion1);
    assertEquals("longVersion1 shouldn't happenedBefore itself", false, result);
    result = longVersion2.happenedBefore(longVersion2);
    assertEquals("longVersion2 shouldn't happenedBefore itself", false, result);
  }

  @Test
  public void testSync_Self() {
    LOGGER.log(Level.INFO, "testSync_Remain: Test sync where the value should remain");
    integerVersion1.sync(integerVersion1);
    assertEquals("integerVersion1 value should not have changed", INITIAL_VERSION_VALUE,
        integerVersion1.get());
    integerVersion2.sync(integerVersion2);
    assertEquals("integerVersion2 value should not have changed", INCREMENTED_VERSION_VALUE,
        integerVersion2.get());
    longVersion1.sync(longVersion1);
    assertEquals("longVersion1 value should not have changed", (Long) (long) INITIAL_VERSION_VALUE,
        longVersion1.get());
    longVersion2.sync(longVersion2);
    assertEquals("longVersion2 value should not have changed",
        (Long) (long) INCREMENTED_VERSION_VALUE, longVersion2.get());
  }

  @Test
  public void testSync_Remain() {
    LOGGER.log(Level.INFO, "testSync_Remain: Test sync where the value should remain");
    integerVersion2.sync(integerVersion1);
    assertEquals("integerVersion1 value should not have changed", INITIAL_VERSION_VALUE,
        integerVersion1.get());
    assertEquals("integerVersion2 value should not have changed", INCREMENTED_VERSION_VALUE,
        integerVersion2.get());
    longVersion2.sync(longVersion1);
    assertEquals("longVersion1 value should not have changed", (Long) (long) INITIAL_VERSION_VALUE,
        longVersion1.get());
    assertEquals("longVersion2 value should not have changed",
        (Long) (long) INCREMENTED_VERSION_VALUE, longVersion2.get());

  }

  @Test
  public void testSync_Incremented() {
    LOGGER.log(Level.INFO, "testSync_Increase: Test sync where the value should increase");
    integerVersion1.sync(integerVersion2);
    assertEquals("integerVersion1 value should have been incremented", INCREMENTED_VERSION_VALUE,
        integerVersion1.get());
    assertEquals("integerVersion2 value should not have changed", INCREMENTED_VERSION_VALUE,
        integerVersion2.get());
    longVersion1.sync(longVersion2);
    assertEquals("longVersion1 value should have been incremented",
        (Long) (long) INCREMENTED_VERSION_VALUE, longVersion1.get());
    assertEquals("longVersion2 value should not have changed",
        (Long) (long) INCREMENTED_VERSION_VALUE, longVersion2.get());
  }

  @Test
  public void testHashCode_Integer() {
    LOGGER.log(Level.INFO, "testHashCode_Integer: Test hashCode for IntegerVersions");
    int result = integerVersion1.hashCode();
    assertEquals("integerVersion1 should have the same hashCode as its value",
        integerVersion1.get().hashCode(), result);
    result = integerVersion2.hashCode();
    assertEquals("integerVersion2 should have the same hashCode as its value",
        integerVersion2.get().hashCode(), result);
  }

  @Test
  public void testHashCode_Long() {
    LOGGER.log(Level.INFO, "testHashCode_Long: Test hashCode for IntegerVersions");
    int result = longVersion1.hashCode();
    assertEquals("integerVersion1 should have the same hashCode as its value",
        longVersion1.get().hashCode(), result);
    result = longVersion2.hashCode();
    assertEquals("longVersion2 should have the same hashCode as its value",
        longVersion2.get().hashCode(), result);
  }

  @Test
  public void testEquals_Integer_True() {
    LOGGER.log(Level.INFO, "testEquals_Integer_True: Test equals for equal IntegerVersions");
    boolean result = integerVersion1.equals(integerVersion1);
    assertEquals("integerVersion1 should equal itself", true, result);
    result = integerVersion2.equals(integerVersion2);
    assertEquals("integerVersion2 should equal itself", true, result);
  }

  @Test
  public void testEquals_Integer_False() {
    LOGGER.log(Level.INFO, "testEquals_Integer_False: Test equals for non-equal IntegerVersions");
    boolean result = integerVersion1.equals(integerVersion2);
    assertEquals("integerVersion1 shouldn't equal integerVersion2", false, result);
    result = integerVersion2.equals(integerVersion1);
    assertEquals("integerVersion2 shouldn't equal integerVersion1", false, result);
  }

  @Test
  public void testEquals_Long_True() {
    LOGGER.log(Level.INFO, "testEquals_Long_True: Test equals for equal LongVersions");
    boolean result = longVersion1.equals(longVersion1);
    assertEquals("longVersion1 should equal itself", true, result);
    result = longVersion2.equals(longVersion2);
    assertEquals("longVersion2 should equal itself", true, result);
  }

  @Test
  public void testEquals_Long_False() {
    LOGGER.log(Level.INFO, "testEquals_Long_False: Test equals for non-equal LongVersions");
    boolean result = longVersion1.equals(longVersion2);
    assertEquals("longVersion1 shouldn't equal longVersion2", false, result);
    result = longVersion2.equals(longVersion1);
    assertEquals("longVersion2 shouldn't equal longVersion1", false, result);
  }

  @Test
  public void testEquals_Object() {
    LOGGER.log(Level.INFO, "testEquals_Null: Test equals against Object");
    boolean result = longVersion1.equals(new Object());
    assertEquals("longVersion1 shouldn't equal longVersion2", false, result);
  }

  @Test
  public void testEquals_Null() {
    LOGGER.log(Level.INFO, "testEquals_Null: Test equals against null");
    boolean result = longVersion1.equals(null);
    assertEquals("longVersion1 shouldn't equal longVersion2", false, result);
  }
}
