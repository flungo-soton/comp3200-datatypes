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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for the {@link LongVersion} class.
 */
public class LongVersionTest extends VersionAbstractTest<Long, LongVersion> {

  private static final Logger LOGGER = Logger.getLogger(LongVersionTest.class.getName());

  private static final Long MAX_ITTERATIONS = 100l;

  @Override
  public LongVersion getVersion(int order) {
    return new LongVersion(getTimestamp(order));
  }

  @Override
  public Long getTimestamp(int order) {
    return order == 0 ? 0 : 1l << (order - 1);
  }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private LongVersion instance;

  @BeforeClass
  public static void setUpClass() {}

  @AfterClass
  public static void tearDownClass() {}

  @Before
  public void setUp() {
    instance = new LongVersion();
  }

  @After
  public void tearDown() {
    instance = null;
  }

  /**
   * Test the initial value of an {@linkplain LongVersion}.
   */
  @Test
  public void testGet_Initial() {
    LOGGER.log(Level.INFO, "testGet_Initial: Testing the initial value of the version");
    Long result = instance.get();
    assertEquals("The initial value of an LongVersion should be 0", (Long) 0l, result);
  }

  /**
   * Test of increment method of an {@linkplain LongVersion}.
   */
  @Test
  public void testIncrement() {
    LOGGER.log(Level.INFO, "testIncrement: Testing incrementing the version");
    for (Long i = 1l; i <= MAX_ITTERATIONS; i++) {
      instance.increment();
      assertEquals("The version should have been incremented by 1", i, instance.get());
    }
  }

  /**
   * Test of increment method of an {@linkplain LongVersion} at {@linkplain Long#MAX_VALUE}.
   */
  @Test
  public void testIncrement_MAX_VALUE() {
    LOGGER.log(Level.INFO,
        "testIncrement_MAX_VALUE: Testing incrementing the version beyond the MAX_VALUE");
    instance.sync(Long.MAX_VALUE);
    thrown.expect(ArithmeticException.class);
    thrown.expectMessage("overflow");
    instance.increment();
  }

  /**
   * Test of sync method of an {@linkplain LongVersion} with increasing values.
   */
  @Test
  public void testSync_Increasing() {
    LOGGER.log(Level.INFO, "testSync_Increasing: Testing sync method with increasing values");
    // Test till shifting causes overflow.
    for (Long i = 1l; i > 0; i <<= 1) {
      instance.sync(i);
      assertEquals("The version should be updated to the increased value", i, instance.get());
    }
  }

  /**
   * Test of sync method of an {@linkplain LongVersion} with varying values.
   */
  @Test
  public void testSync_Decreasing() {
    LOGGER.log(Level.INFO, "testSync_Decreasing: Testing sync method with varying values");
    // Test till shifting causes overflow.
    for (Long i = 1l; i > 0; i <<= 1) {
      // Sync with an increased value
      instance.sync(i);
      assertEquals("The version should be updated to the increased value", i, instance.get());

      // Try to sync with lower values
      instance.sync(i - 1);
      assertEquals("The version should not have changed", i, instance.get());
      instance.sync(i / 2);
      assertEquals("The version should not have changed", i, instance.get());

    }
  }

  /**
   * Test of sync method of an {@linkplain LongVersion} with varying values.
   */
  @Test
  public void testSync_Negative() {
    LOGGER.log(Level.INFO, "testSync_Negative: Testing sync method with negative values");
    instance.sync(-1l);
    assertEquals("The version should not have changed", (Long) 0l, instance.get());
  }

  /**
   * Test of copy method of an unmodified {@linkplain LongVersion} unmodified.
   */
  @Test
  public void testCopy_Zero() {
    LOGGER.log(Level.INFO, "testCopy_Zero: Testing copy method of a version with 0 value");
    Long expInstanceValue = 0l;
    Long expCopyValue = 0l;
    LongVersion copy = instance.copy();
    assertEquals("Expected value of copy to be same as initial value", expCopyValue, copy.get());

    // Increment original
    instance.increment();
    expInstanceValue++;
    assertEquals("The instance should have been incremented", expInstanceValue, instance.get());
    assertEquals("The copy shouldn't have been incremented", expCopyValue, copy.get());

    // Increment the copy
    copy.increment();
    expCopyValue++;
    assertEquals("The instance should have been incremented", expInstanceValue, instance.get());
    assertEquals("The copy shouldn't have been incremented", expCopyValue, copy.get());
    // Increment original
    copy.increment();
    expCopyValue++;
    assertEquals("The instance should have been incremented", expInstanceValue, instance.get());
    assertEquals("The copy shouldn't have been incremented", expCopyValue, copy.get());
  }

  /**
   * Test of copy method of an {@linkplain LongVersion} which has been incremented.
   */
  @Test
  public void testCopy_456() {
    LOGGER.log(Level.INFO, "testCopy_456: Testing copy method of a version with 456 value");
    Long expInstanceValue = 456l;
    Long expCopyValue = 456l;
    instance.sync(expInstanceValue);
    LongVersion copy = instance.copy();
    assertEquals(expInstanceValue, copy.get());

    // Increment original
    instance.increment();
    expInstanceValue++;
    assertEquals("The instance should have been incremented", expInstanceValue, instance.get());
    assertEquals("The copy shouldn't have been incremented", expCopyValue, copy.get());

    // Increment the copy
    copy.increment();
    expCopyValue++;
    assertEquals("The instance should have been incremented", expInstanceValue, instance.get());
    assertEquals("The copy shouldn't have been incremented", expCopyValue, copy.get());
    // Increment original
    copy.increment();
    expCopyValue++;
    assertEquals("The instance should have been incremented", expInstanceValue, instance.get());
    assertEquals("The copy shouldn't have been incremented", expCopyValue, copy.get());
  }

  /**
   * Test of copy method of an {@linkplain LongVersion} which has been incremented to
   * {@linkplain Long#MAX_VALUE}.
   */
  @Test
  public void testCopy_MAX_VALUE() {
    LOGGER.log(Level.INFO,
        "testCopy_MAX_VALUE: Testing copy method of a version with MAX_VALUE value");
    Long expValue = Long.MAX_VALUE;
    instance.sync(expValue);
    LongVersion result = instance.copy();
    assertEquals(expValue, result.get());
  }

}
