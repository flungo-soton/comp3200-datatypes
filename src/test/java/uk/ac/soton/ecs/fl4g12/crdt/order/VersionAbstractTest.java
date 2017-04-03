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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Abstract test for implementations of {@linkplain Version}.
 *
 * @param <T> the type of the timestamp in the {@link Version} being tested.
 * @param <V1> the type of {@link Version}s that the {@link Version} being tested can interact with.
 */
public abstract class VersionAbstractTest<T, V1 extends Version<T, V1, ?>, V2 extends V1> {

  protected final int VERSION_MAX_ORDER = 9;

  /**
   * Create a version given an order.
   *
   * The order value will be between 0 and 9. If it is 0, then the version returned should be a
   * newly instantiated un-incremented version. Sequential values for order should get values that
   * precede each other ({@code getVersion(x).precedes(getVersion(x+1))}).
   *
   * @param order the order of the version relative to the other values returned by this method.
   * @return the version with order {@code order} relative to other versions from this method.
   * @see #getTimestamp(int) to get the expected value of the version.
   */
  public abstract V1 getVersion(int order);

  /**
   * Get the timestamp of the version with the given order.
   *
   * The order value will be between 0 and 9. If it is 0, then the timestamp should be the zero
   * timestamp.
   *
   * @param order the order of the version relative to the other values returned by this method.
   * @return the timestamp of the version with order {@code order}.
   * @see #getVersion(int) to create a version with the same timestamp value.
   */
  public abstract T getTimestamp(int order);

  private void testGet(final int order) {
    // Get the version instance
    V1 instance = getVersion(order);

    // Get the instance value
    T result = instance.get();

    // Verify the value is the expected timestamp
    assertEquals(getTimestamp(order), result);
  }

  /**
   * Test that get for a new version returns a zero value.
   */
  @Test
  public void testGet_0() {
    testGet(0);
  }

  /**
   * Test that the version with order 1 returns the expected timestamp.
   */
  @Test
  public void testGet_1() {
    testGet(1);
  }

  /**
   * Test that the version with order 2 returns the expected timestamp.
   */
  @Test
  public void testGet_2() {
    testGet(2);
  }

  /**
   * Test that the version with order 3 returns the expected timestamp.
   */
  @Test
  public void testGet_3() {
    testGet(3);
  }

  /**
   * Test that the version with order 4 returns the expected timestamp.
   */
  @Test
  public void testGet_4() {
    testGet(4);
  }

  /**
   * Test that the version with order 5 returns the expected timestamp.
   */
  @Test
  public void testGet_5() {
    testGet(5);
  }

  /**
   * Test that the version with order 6 returns the expected timestamp.
   */
  @Test
  public void testGet_6() {
    testGet(6);
  }

  /**
   * Test that the version with order 7 returns the expected timestamp.
   */
  @Test
  public void testGet_7() {
    testGet(7);
  }

  /**
   * Test that the version with order 8 returns the expected timestamp.
   */
  @Test
  public void testGet_8() {
    testGet(8);
  }

  /**
   * Test that the version with order 9 returns the expected timestamp.
   */
  @Test
  public void testGet_9() {
    testGet(9);
  }

  private void testHappenedBefore(final int order) {
    // Get the version instance
    V1 instance = getVersion(order);

    for (int i = 0; i <= VERSION_MAX_ORDER; i++) {
      // Verify the happensBefore relation
      assertEquals(order < i, instance.happenedBefore(getVersion(i)));
    }
  }

  /**
   * Test that the version with order 0 has the expected happensBefore relation with the other
   * versions.
   */
  @Test
  public void testHappenedBefore_0() {
    testHappenedBefore(0);
  }

  /**
   * Test that the version with order 1 has the expected happensBefore relation with the other
   * versions.
   */
  @Test
  public void testHappenedBefore_1() {
    testHappenedBefore(1);
  }


  /**
   * Test that the version with order 2 has the expected happensBefore relation with the other
   * versions.
   */
  @Test
  public void testHappenedBefore_2() {
    testHappenedBefore(2);
  }

  /**
   * Test that the version with order 3 has the expected happensBefore relation with the other
   * versions.
   */
  @Test
  public void testHappenedBefore_3() {
    testHappenedBefore(3);
  }

  /**
   * Test that the version with order 4 has the expected happensBefore relation with the other
   * versions.
   */
  @Test
  public void testHappenedBefore_4() {
    testHappenedBefore(4);
  }

  /**
   * Test that the version with order 5 has the expected happensBefore relation with the other
   * versions.
   */
  @Test
  public void testHappenedBefore_5() {
    testHappenedBefore(5);
  }

  /**
   * Test that the version with order 6 has the expected happensBefore relation with the other
   * versions.
   */
  @Test
  public void testHappenedBefore_6() {
    testHappenedBefore(6);
  }

  /**
   * Test that the version with order 7 has the expected happensBefore relation with the other
   * versions.
   */
  @Test
  public void testHappenedBefore_7() {
    testHappenedBefore(7);
  }

  /**
   * Test that the version with order 8 has the expected happensBefore relation with the other
   * versions.
   */
  @Test
  public void testHappenedBefore_8() {
    testHappenedBefore(8);
  }

  /**
   * Test that the version with order 9 has the expected happensBefore relation with the other
   * versions.
   */
  @Test
  public void testHappenedBefore_9() {
    testHappenedBefore(9);
  }


  private void testPrecedes(final int order) {
    // Get the version instance
    V1 instance = getVersion(order);

    for (int i = 0; i <= VERSION_MAX_ORDER; i++) {
      // Verify the happensBefore relation
      assertEquals(order + 1 == i, instance.precedes(getVersion(i)));
    }
  }

  /**
   * Test that the version with order 0 has the expected happensBefore relation with the other
   * versions.
   */
  @Test
  public void testPrecedes_0() {
    testPrecedes(0);
  }

  /**
   * Test that the version with order 1 has the expected happensBefore relation with the other
   * versions.
   */
  @Test
  public void testPrecedes_1() {
    testPrecedes(1);
  }


  /**
   * Test that the version with order 2 has the expected happensBefore relation with the other
   * versions.
   */
  @Test
  public void testPrecedes_2() {
    testPrecedes(2);
  }

  /**
   * Test that the version with order 3 has the expected happensBefore relation with the other
   * versions.
   */
  @Test
  public void testPrecedes_3() {
    testPrecedes(3);
  }

  /**
   * Test that the version with order 4 has the expected happensBefore relation with the other
   * versions.
   */
  @Test
  public void testPrecedes_4() {
    testPrecedes(4);
  }

  /**
   * Test that the version with order 5 has the expected happensBefore relation with the other
   * versions.
   */
  @Test
  public void testPrecedes_5() {
    testPrecedes(5);
  }

  /**
   * Test that the version with order 6 has the expected happensBefore relation with the other
   * versions.
   */
  @Test
  public void testPrecedes_6() {
    testPrecedes(6);
  }

  /**
   * Test that the version with order 7 has the expected happensBefore relation with the other
   * versions.
   */
  @Test
  public void testPrecedes_7() {
    testPrecedes(7);
  }

  /**
   * Test that the version with order 8 has the expected happensBefore relation with the other
   * versions.
   */
  @Test
  public void testPrecedes_8() {
    testPrecedes(8);
  }

  /**
   * Test that the version with order 9 has the expected happensBefore relation with the other
   * versions.
   */
  @Test
  public void testPrecedes_9() {
    testPrecedes(9);
  }

  private void testSync_Version(final int order) {
    for (int i = 0; i <= VERSION_MAX_ORDER; i++) {
      // Get the version instance
      V1 vector = getVersion(order);
      vector.sync(getVersion(i));

      // Verify the happensBefore relation
      assertEquals(getVersion(order > i ? order : i), vector);
    }
  }

  /**
   * Test that synching the version with order 0 with the other versions gives the expected result.
   */
  @Test
  public void testSync_Version_0() {
    testSync_Version(0);
  }

  /**
   * Test that synching the version with order 1 with the other versions gives the expected result.
   */
  @Test
  public void testSync_Version_1() {
    testSync_Version(1);
  }

  /**
   * Test that synching the version with order 2 with the other versions gives the expected result.
   */
  @Test
  public void testSync_Version_2() {
    testSync_Version(2);
  }

  /**
   * Test that synching the version with order 3 with the other versions gives the expected result.
   */
  @Test
  public void testSync_Version_3() {
    testSync_Version(3);
  }

  /**
   * Test that synching the version with order 4 with the other versions gives the expected result.
   */
  @Test
  public void testSync_Version_4() {
    testSync_Version(4);
  }

  /**
   * Test that synching the version with order 5 with the other versions gives the expected result.
   */
  @Test
  public void testSync_Version_5() {
    testSync_Version(5);
  }

  /**
   * Test that synching the version with order 6 with the other versions gives the expected result.
   */
  @Test
  public void testSync_Version_6() {
    testSync_Version(5);
  }

  /**
   * Test that synching the version with order 7 with the other versions gives the expected result.
   */
  @Test
  public void testSync_Version_7() {
    testSync_Version(7);
  }

  /**
   * Test that synching the version with order 8 with the other versions gives the expected result.
   */
  @Test
  public void testSync_Version_8() {
    testSync_Version(8);
  }

  /**
   * Test that synching the version with order 9 with the other versions gives the expected result.
   */
  @Test
  public void testSync_Version_9() {
    testSync_Version(9);
  }

  private void testSync_Timestamp(final int order) {
    for (int i = 0; i <= VERSION_MAX_ORDER; i++) {
      // Get the version instance
      V1 vector = getVersion(order);
      vector.sync(getTimestamp(i));

      // Verify the happensBefore relation
      assertEquals(getVersion(order > i ? order : i), vector);
    }
  }

  /**
   * Test that synching the version with order 0 with the other timestamp gives the expected result.
   */
  @Test
  public void testSync_Timestamp_0() {
    testSync_Timestamp(0);
  }

  /**
   * Test that synching the version with order 1 with the other timestamp gives the expected result.
   */
  @Test
  public void testSync_Timestamp_1() {
    testSync_Timestamp(1);
  }

  /**
   * Test that synching the version with order 2 with the other timestamp gives the expected result.
   */
  @Test
  public void testSync_Timestamp_2() {
    testSync_Timestamp(2);
  }

  /**
   * Test that synching the version with order 3 with the other timestamp gives the expected result.
   */
  @Test
  public void testSync_Timestamp_3() {
    testSync_Timestamp(3);
  }

  /**
   * Test that synching the version with order 4 with the other timestamp gives the expected result.
   */
  @Test
  public void testSync_Timestamp_4() {
    testSync_Timestamp(4);
  }

  /**
   * Test that synching the version with order 5 with the other timestamp gives the expected result.
   */
  @Test
  public void testSync_Timestamp_5() {
    testSync_Timestamp(5);
  }

  /**
   * Test that synching the version with order 6 with the other timestamp gives the expected result.
   */
  @Test
  public void testSync_Timestamp_6() {
    testSync_Timestamp(5);
  }

  /**
   * Test that synching the version with order 7 with the other timestamp gives the expected result.
   */
  @Test
  public void testSync_Timestamp_7() {
    testSync_Timestamp(7);
  }

  /**
   * Test that synching the version with order 8 with the other timestamp gives the expected result.
   */
  @Test
  public void testSync_Timestamp_8() {
    testSync_Timestamp(8);
  }

  /**
   * Test that synching the version with order 9 with the other timestamp gives the expected result.
   */
  @Test
  public void testSync_Timestamp_9() {
    testSync_Timestamp(9);
  }

  private void testIdentical(final int order) {
    // Get the version instance
    V1 instance = getVersion(order);

    for (int i = 0; i <= VERSION_MAX_ORDER; i++) {
      // Verify the happensBefore relation
      assertEquals(order == i, instance.identical(getVersion(i)));
    }
  }

  /**
   * Test version 0 against all other versions with the identical method.
   */
  @Test
  public void testIdentical_0() {
    testIdentical(0);
  }

  /**
   * Test version 1 against all other versions with the identical method.
   */
  @Test
  public void testIdentical_1() {
    testIdentical(1);
  }

  /**
   * Test version 2 against all other versions with the identical method.
   */
  @Test
  public void testIdentical_2() {
    testIdentical(2);
  }

  /**
   * Test version 3 against all other versions with the identical method.
   */
  @Test
  public void testIdentical_3() {
    testIdentical(3);
  }

  /**
   * Test version 4 against all other versions with the identical method.
   */
  @Test
  public void testIdentical_4() {
    testIdentical(4);
  }

  /**
   * Test version 5 against all other versions with the identical method.
   */
  @Test
  public void testIdentical_5() {
    testIdentical(5);
  }

  /**
   * Test version 6 against all other versions with the identical method.
   */
  @Test
  public void testIdentical_6() {
    testIdentical(5);
  }

  /**
   * Test version 7 against all other versions with the identical method.
   */
  @Test
  public void testIdentical_7() {
    testIdentical(7);
  }

  /**
   * Test version 8 against all other versions with the identical method.
   */
  @Test
  public void testIdentical_8() {
    testIdentical(8);
  }

  /**
   * Test version 9 against all other versions with the identical method.
   */
  @Test
  public void testIdentical_9() {
    testIdentical(9);
  }

  private void testEquals(final int order) {
    // Get the version instance
    V1 instance = getVersion(order);

    for (int i = 0; i <= VERSION_MAX_ORDER; i++) {
      // Verify the happensBefore relation
      assertEquals(order == i, instance.equals(getVersion(i)));
    }
  }

  /**
   * Test version 0 against all other versions with the equals method.
   */
  @Test
  public void testEquals_0() {
    testEquals(0);
  }

  /**
   * Test version 1 against all other versions with the equals method.
   */
  @Test
  public void testEquals_1() {
    testEquals(1);
  }

  /**
   * Test version 2 against all other versions with the equals method.
   */
  @Test
  public void testEquals_2() {
    testEquals(2);
  }

  /**
   * Test version 3 against all other versions with the equals method.
   */
  @Test
  public void testEquals_3() {
    testEquals(3);
  }

  /**
   * Test version 4 against all other versions with the equals method.
   */
  @Test
  public void testEquals_4() {
    testEquals(4);
  }

  /**
   * Test version 5 against all other versions with the equals method.
   */
  @Test
  public void testEquals_5() {
    testEquals(5);
  }

  /**
   * Test version 6 against all other versions with the equals method.
   */
  @Test
  public void testEquals_6() {
    testEquals(5);
  }

  /**
   * Test version 7 against all other versions with the equals method.
   */
  @Test
  public void testEquals_7() {
    testEquals(7);
  }

  /**
   * Test version 8 against all other versions with the equals method.
   */
  @Test
  public void testEquals_8() {
    testEquals(8);
  }

  /**
   * Test version 9 against all other versions with the equals method.
   */
  @Test
  public void testEquals_9() {
    testEquals(9);
  }

  private void testCopy(final int order) {
    // Get the version instance
    V1 instance = getVersion(order);

    // Copy it
    V1 copy = instance.copy();

    // Make assertions
    assertEquals(instance, copy);
    assertTrue(copy.identical(instance));
    assertNotSame(instance, copy);

    // TODO: Ensure that modifying copy does not change original and vice-versa
  }

  /**
   * Test clone method on version 0.
   */
  @Test
  public void testCopy_0() {
    testCopy(0);
  }

  /**
   * Test clone method on version 1.
   */
  @Test
  public void testCopy_1() {
    testCopy(1);
  }

  /**
   * Test clone method on version 2.
   */
  @Test
  public void testCopy_2() {
    testCopy(2);
  }

  /**
   * Test clone method on version 3.
   */
  @Test
  public void testCopy_3() {
    testCopy(3);
  }

  /**
   * Test clone method on version 4.
   */
  @Test
  public void testCopy_4() {
    testCopy(4);
  }

  /**
   * Test clone method on version 5.
   */
  @Test
  public void testCopy_5() {
    testCopy(5);
  }

  /**
   * Test clone method on version 6.
   */
  @Test
  public void testCopy_6() {
    testCopy(5);
  }

  /**
   * Test clone method on version 7.
   */
  @Test
  public void testCopy_7() {
    testCopy(7);
  }

  /**
   * Test clone method on version 8.
   */
  @Test
  public void testCopy_8() {
    testCopy(8);
  }

  /**
   * Test clone method on version 9.
   */
  @Test
  public void testCopy_9() {
    testCopy(9);
  }
}
