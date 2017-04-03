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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Abstract test for implementations of {@linkplain VersionVector}.
 *
 * @param <K> the type of the key being used in the {@link VersionVector}s.
 * @param <V> the type of the version being tested.
 */
public abstract class VersionVectorAbstractTest<K, V extends VersionVector<K, Integer>> extends
    VersionAbstractTest<Map<K, Integer>, VersionVector<K, Integer>, VersionVector<K, Integer>> {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private final boolean mutable;

  public VersionVectorAbstractTest(boolean mutable) {
    this.mutable = mutable;
  }

  /**
   * Get the {@linkplain VersionVector} from a set of examples.
   *
   * @param id the id of the version vector to get.
   * @return the {@link VersionVector} of the specified example.
   * @see #getTimestamp(String) for details about the examples and id.
   */
  protected abstract V getVersion(String id);

  /**
   * Gets the version vector timestamp from a set of examples.
   *
   * The examples are taken from <a href=
   * "https://upload.wikimedia.org/wikipedia/commons/thumb/5/55/Vector_Clock.svg/500px-Vector_Clock.svg.png">
   * this image</a> and are identified by a string {@code id} of the form {@code xn} where {@code x}
   * is the timeline which the version lies on ({@code a}, {@code b} or {@code c}) and {@code n} is
   * the value of the timestamp for its timeline. For example, the node {@code {A:2, B:4, C:1}} on
   * timeline {@code b} would have an {@code id}, {@code "b4"}.
   *
   * @param id the id of the timestamp to get.
   * @return the timestamp of the specified example.
   */
  protected HashMap<K, Integer> getTimestamp(String id) {
    HashMap<K, Integer> timestamp = new HashMap(3);

    switch (id) {
      case "a0":
        timestamp.put(getKey(0), 0);
        break;
      case "a1":
        timestamp.put(getKey(0), 1);
        timestamp.put(getKey(1), 2);
        timestamp.put(getKey(2), 1);
        break;
      case "a2":
        timestamp.put(getKey(0), 2);
        timestamp.put(getKey(1), 2);
        timestamp.put(getKey(2), 1);
        break;
      case "a3":
        timestamp.put(getKey(0), 3);
        timestamp.put(getKey(1), 3);
        timestamp.put(getKey(2), 3);
        break;
      case "a4":
        timestamp.put(getKey(0), 4);
        timestamp.put(getKey(1), 5);
        timestamp.put(getKey(2), 5);
        break;
      case "b0":
        timestamp.put(getKey(1), 0);
        break;
      case "b1":
        timestamp.put(getKey(1), 1);
        timestamp.put(getKey(2), 1);
        break;
      case "b2":
        timestamp.put(getKey(1), 2);
        timestamp.put(getKey(2), 1);
        break;
      case "b3":
        timestamp.put(getKey(1), 3);
        timestamp.put(getKey(2), 1);
        break;
      case "b4":
        timestamp.put(getKey(0), 2);
        timestamp.put(getKey(1), 4);
        timestamp.put(getKey(2), 1);
        break;
      case "b5":
        timestamp.put(getKey(0), 2);
        timestamp.put(getKey(1), 5);
        timestamp.put(getKey(2), 1);
        break;
      case "c0":
        timestamp.put(getKey(2), 0);
        break;
      case "c1":
        timestamp.put(getKey(2), 1);
        break;
      case "c2":
        timestamp.put(getKey(1), 3);
        timestamp.put(getKey(2), 2);
        break;
      case "c3":
        timestamp.put(getKey(1), 3);
        timestamp.put(getKey(2), 3);
        break;
      case "c4":
        timestamp.put(getKey(0), 2);
        timestamp.put(getKey(1), 5);
        timestamp.put(getKey(2), 4);
        break;
      case "c5":
        timestamp.put(getKey(0), 2);
        timestamp.put(getKey(1), 5);
        timestamp.put(getKey(2), 5);
        break;
      default:
        throw new UnsupportedOperationException("No timestamp defined for " + id);
    }
    return timestamp;
  }

  /**
   * Gets a key with the given index. Should provide keys for 0, 1, 2 and 3. Key 3 should be unused
   * for all timestamps given by {@link #getTimestamp(int)} and versions given by
   * {@link #getVersion(int)}.
   *
   * @param index the index of the key to return.
   * @return the key.
   */
  protected abstract K getKey(int index);

  @Override
  public HashMap<K, Integer> getTimestamp(int order) {
    HashMap<K, Integer> timestamp;
    switch (order) {
      case 0:
        return getTimestamp("c0");
      case 1:
        return getTimestamp("c1");
      case 2:
        return getTimestamp("b1");
      case 3:
        return getTimestamp("b2");
      case 4:
        return getTimestamp("a1");
      case 5:
        return getTimestamp("a2");
      case 6:
        timestamp = getTimestamp("a2");
        timestamp.put(getKey(1), 3);
        return timestamp;
      case 7:
        return getTimestamp("b4");
      case 8:
        timestamp = getTimestamp("b4");
        timestamp.put(getKey(2), 2);
        return timestamp;
      case 9:
        timestamp = getTimestamp("b4");
        timestamp.put(getKey(2), 3);
        return timestamp;
      default:
        throw new UnsupportedOperationException("order should be between 0 and 9");
    }
  }

  private void testGet_K(final int order) {
    // Get the version instance
    VersionVector<K, Integer> instance = getVersion(order);

    // Get the timestamp which has the expected values
    Map<K, Integer> timestamp = getTimestamp(order);

    // For each key, check that the expected value is given
    for (int i = 0; i <= 3; i++) {
      K id = getKey(i);
      Integer expectedValue = timestamp.get(id);

      // If the timestamp is not defined, the expected value is 0.
      if (expectedValue == null) {
        expectedValue = 0;
      }

      assertEquals(expectedValue, instance.get(id));
    }
  }

  @Test
  public void testGet_K_0() {
    testGet_K(0);
  }

  @Test
  public void testGet_K_1() {
    testGet_K(1);
  }

  @Test
  public void testGet_K_2() {
    testGet_K(2);
  }

  @Test
  public void testGet_K_3() {
    testGet_K(3);
  }

  @Test
  public void testGet_K_4() {
    testGet_K(4);
  }

  @Test
  public void testGet_K_5() {
    testGet_K(5);
  }

  @Test
  public void testGet_K_6() {
    testGet_K(6);
  }

  @Test
  public void testGet_K_7() {
    testGet_K(7);
  }

  @Test
  public void testGet_K_8() {
    testGet_K(8);
  }

  @Test
  public void testGet_K_9() {
    testGet_K(9);
  }

  private void testGetLogicalVersion(final int order) {
    // Get the version instance
    VersionVector<K, Integer> instance = getVersion(order);

    // Get the timestamp which has the expected values
    Map<K, Integer> timestamp = getTimestamp(order);

    // For each key, check that the expected value is given
    for (int i = 0; i <= 3; i++) {
      K id = getKey(i);
      Integer expectedValue = timestamp.get(id);

      LogicalVersion<Integer, ?> logicalVersion = instance.getLogicalVersion(id);
      if (expectedValue == null) {
        Assert.assertNull(logicalVersion);
      } else {
        assertEquals(expectedValue, logicalVersion.get());
      }

      if (logicalVersion != null) {
        if (mutable) {
          // Test that the logical version affects the version vector and vice-versa.
          logicalVersion.increment();
          expectedValue++;
          assertEquals(expectedValue, logicalVersion.get());
          assertEquals(expectedValue, instance.get(id));

          instance.increment(id);
          expectedValue++;
          assertEquals(expectedValue, logicalVersion.get());
          assertEquals(expectedValue, instance.get(id));
        } else {
          try {
            logicalVersion.increment();
            fail("UnsupportedOperationException should have been thrown.");
          } catch (UnsupportedOperationException ex) {
            // Do nothing, this is what is expected.
          }
        }
      }
    }
  }

  @Test
  public void testGetLogicalVersion_0() {
    testGetLogicalVersion(0);
  }

  @Test
  public void testGetLogicalVersion_1() {
    testGetLogicalVersion(1);
  }

  @Test
  public void testGetLogicalVersion_2() {
    testGetLogicalVersion(2);
  }

  @Test
  public void testGetLogicalVersion_3() {
    testGetLogicalVersion(3);
  }

  @Test
  public void testGetLogicalVersion_4() {
    testGetLogicalVersion(4);
  }

  @Test
  public void testGetLogicalVersion_5() {
    testGetLogicalVersion(5);
  }

  @Test
  public void testGetLogicalVersion_6() {
    testGetLogicalVersion(6);
  }

  @Test
  public void testGetLogicalVersion_7() {
    testGetLogicalVersion(7);
  }

  @Test
  public void testGetLogicalVersion_8() {
    testGetLogicalVersion(8);
  }

  @Test
  public void testGetLogicalVersion_9() {
    testGetLogicalVersion(9);
  }

  private void testGetDot(final int order) {
    // Get the version instance
    VersionVector<K, Integer> instance = getVersion(order);

    // Get the timestamp which has the expected values
    Map<K, Integer> timestamp = getTimestamp(order);

    // For each key, check that the expected value is given
    for (int i = 0; i <= 3; i++) {
      K id = getKey(i);
      Integer expectedValue = timestamp.get(id);

      if (expectedValue == null) {
        // Exception should be thrown if version has not been initialised
        try {
          instance.getDot(id);
          fail("IllegalArgumentException should have been thrown.");
        } catch (IllegalArgumentException ex) {
          // Do nothing, this is what is expected.
        }
      } else {
        Dot<K, Integer> expectedDot = new Dot(id, instance.getLogicalVersion(id));

        Dot<K, Integer> dot = instance.getDot(id);
        assertEquals(expectedDot, dot);

        if (mutable) {
          // Test that the dot affects the version vector and vice-versa.
          dot.increment();
          expectedValue++;
          assertEquals(expectedValue, dot.get());
          assertEquals(expectedValue, instance.get(id));

          instance.increment(id);
          expectedValue++;
          assertEquals(expectedValue, dot.get());
          assertEquals(expectedValue, instance.get(id));
        } else {
          try {
            dot.increment();
            fail("UnsupportedOperationException should have been thrown.");
          } catch (UnsupportedOperationException ex) {
            // Do nothing, this is what is expected.
          }
        }
      }
    }
  }

  @Test
  public void testGetDot_0() {
    testGetDot(0);
  }

  @Test
  public void testGetDot_1() {
    testGetDot(1);
  }

  @Test
  public void testGetDot_2() {
    testGetDot(2);
  }

  @Test
  public void testGetDot_3() {
    testGetDot(3);
  }

  @Test
  public void testGetDot_4() {
    testGetDot(4);
  }

  @Test
  public void testGetDot_5() {
    testGetDot(5);
  }

  @Test
  public void testGetDot_6() {
    testGetDot(6);
  }

  @Test
  public void testGetDot_7() {
    testGetDot(7);
  }

  @Test
  public void testGetDot_8() {
    testGetDot(8);
  }

  @Test
  public void testGetDot_9() {
    testGetDot(9);
  }

  private void testGetIdentifiers(final int order) {
    // Get the version instance
    VersionVector<K, Integer> instance = getVersion(order);

    // Get the instance value
    Set<K> result = instance.getIdentifiers();

    // Verify the value is the expected timestamp
    assertEquals(getTimestamp(order).keySet(), result);
  }

  @Test
  public void testGetIdentifiers_0() {
    testGetIdentifiers(0);
  }

  @Test
  public void testGetIdentifiers_1() {
    testGetIdentifiers(1);
  }

  @Test
  public void testGetIdentifiers_2() {
    testGetIdentifiers(2);
  }

  @Test
  public void testGetIdentifiers_3() {
    testGetIdentifiers(3);
  }

  @Test
  public void testGetIdentifiers_4() {
    testGetIdentifiers(4);
  }

  @Test
  public void testGetIdentifiers_5() {
    testGetIdentifiers(5);
  }

  @Test
  public void testGetIdentifiers_6() {
    testGetIdentifiers(6);
  }

  @Test
  public void testGetIdentifiers_7() {
    testGetIdentifiers(7);
  }

  @Test
  public void testGetIdentifiers_8() {
    testGetIdentifiers(8);
  }

  @Test
  public void testGetIdentifiers_9() {
    testGetIdentifiers(9);
  }

  private void testInit(final int order) {
    // Setup the mock (for cummulative testing)
    VersionVector<K, Integer> instance = getVersion(order);

    // Get the timestamp which has the expected values (for cummulative testing)
    Map<K, Integer> timestamp = getTimestamp(order);

    for (int i = 0; i <= 3; i++) {
      K id = getKey(i);

      // Test init on the initial version
      // Get a fresh version to test on
      VersionVector<K, Integer> initialInitted = getVersion(order);
      Map<K, Integer> expectedTimestamp = getTimestamp(order);
      if (!expectedTimestamp.containsKey(id)) {
        expectedTimestamp.put(id, 0);
      }

      LogicalVersion<Integer, ?> initialVersion = initialInitted.init(id);
      assertEquals("The LogicalVersion should have a 0 value", expectedTimestamp.get(id),
          initialVersion.get());

      assertTrue("Identifiers should contain the new identifier",
          expectedTimestamp.keySet().contains(id));
      assertEquals("Identifiers should match the expectation", expectedTimestamp.keySet(),
          initialInitted.getIdentifiers());

      assertEquals("Version timestamp should match the expectation", expectedTimestamp,
          initialInitted.get());

      assertSame("The same version should be returned using getLogicalVersion", initialVersion,
          initialInitted.getLogicalVersion(id));

      // Test init cumulatively
      // Test on the version kept between itterations of the loop.
      if (!timestamp.containsKey(id)) {
        timestamp.put(id, 0);
      }

      LogicalVersion<Integer, ?> logicalVersion = instance.init(id);
      assertEquals("The LogicalVersion should have a 0 value", expectedTimestamp.get(id),
          logicalVersion.get());

      assertTrue("Identifiers should contain the new identifier",
          expectedTimestamp.keySet().contains(id));
      assertEquals("Identifiers should match the expectation", timestamp.keySet(),
          instance.getIdentifiers());

      assertEquals("Version timestamp should match the expectation", timestamp, instance.get());

      assertSame("The same version should be returned using getLogicalVersion", logicalVersion,
          instance.getLogicalVersion(id));
    }
  }

  @Test
  public void testInit_0() {
    testInit(0);
  }

  @Test
  public void testInit_1() {
    testInit(1);
  }

  @Test
  public void testInit_2() {
    testInit(2);
  }

  @Test
  public void testInit_3() {
    testInit(3);
  }

  @Test
  public void testInit_4() {
    testInit(4);
  }

  @Test
  public void testInit_5() {
    testInit(5);
  }

  @Test
  public void testInit_6() {
    testInit(6);
  }

  @Test
  public void testInit_7() {
    testInit(7);
  }

  @Test
  public void testInit_8() {
    testInit(8);
  }

  @Test
  public void testInit_9() {
    testInit(9);
  }

  private void testIncrement(final int order) {
    // Setup the mock
    VersionVector<K, Integer> instance = getVersion(order);

    // Get the timestamp which has the expected values
    Map<K, Integer> timestamp = getTimestamp(order);

    // Test the method
    V previous;
    for (int i = 0; i <= 3; i++) {
      K id = getKey(i);

      if (timestamp.containsKey(id)) {
        // Increment initial
        VersionVector<K, Integer> initialIncremented = getVersion(order);
        initialIncremented.increment(id);
        assertTrue("Initial version should have happenedBefore incremented version",
            getVersion(order).happenedBefore(initialIncremented));

        // Cumlative incrementing
        previous = (V) instance.copy();
        instance.increment(id);
        assertTrue("Previous version should have happenedBefore incremented version",
            previous.happenedBefore(instance));
        assertTrue("Initial version should have happenedBefore cummulatively incremented version",
            getVersion(order).happenedBefore(instance));
      } else {
        // ID has not been initialised, exception should be thrown.
        try {
          instance.increment(id);
          fail("Expected IllegalArgumentException when incrementing uninitialised");
        } catch (IllegalArgumentException ex) {
          // Do nothing, this is what's expected
        }
      }
    }
  }

  @Test
  public void testIncrement_0() {
    testIncrement(0);
  }

  @Test
  public void testIncrement_1() {
    testIncrement(1);
  }

  @Test
  public void testIncrement_2() {
    testIncrement(2);
  }

  @Test
  public void testIncrement_3() {
    testIncrement(3);
  }

  @Test
  public void testIncrement_4() {
    testIncrement(4);
  }

  @Test
  public void testIncrement_5() {
    testIncrement(5);
  }

  @Test
  public void testIncrement_6() {
    testIncrement(6);
  }

  @Test
  public void testIncrement_7() {
    testIncrement(7);
  }

  @Test
  public void testIncrement_8() {
    testIncrement(8);
  }

  @Test
  public void testIncrement_9() {
    testIncrement(9);
  }

  private void testSuccessor_K(final int order) {
    for (int i = 0; i < 3; i++) {
      VersionVector<K, Integer> instance = getVersion(order);

      K id = getKey(i);

      Map<K, Integer> successor = instance.successor(id);

      Map<K, Integer> expected = instance.get();
      LogicalVersion<Integer, ?> successive = new IntegerVersion();
      successive.sync(instance.get(id));
      successive.increment();

      expected.put(id, successive.get());

      assertEquals(expected, successor);
    }
  }

  @Test
  public void testSuccessor_K_0() {
    testSuccessor_K(0);
  }

  @Test
  public void testSuccessor_K_1() {
    testSuccessor_K(1);
  }

  @Test
  public void testSuccessor_K_2() {
    testSuccessor_K(2);
  }

  @Test
  public void testSuccessor_K_3() {
    testSuccessor_K(3);
  }

  @Test
  public void testSuccessor_K_4() {
    testSuccessor_K(4);
  }

  @Test
  public void testSuccessor_K_5() {
    testSuccessor_K(5);
  }

  @Test
  public void testSuccessor_K_6() {
    testSuccessor_K(6);
  }

  @Test
  public void testSuccessor_K_7() {
    testSuccessor_K(7);
  }

  @Test
  public void testSuccessor_K_8() {
    testSuccessor_K(8);
  }

  @Test
  public void testSuccessor_K_9() {
    testSuccessor_K(9);
  }

  private void testSync_Dot(final int order) {
    for (int i = 0; i < VERSION_MAX_ORDER; i++) {
      VersionVector<K, Integer> other = getVersion(i);

      for (int j = 0; j < 3; j++) {
        K id = getKey(j);

        if (other.getIdentifiers().contains(id)) {
          // Sync with the dot
          VersionVector<K, Integer> instance = getVersion(order);
          Dot<K, Integer> dot = other.getDot(id);
          instance.sync(dot);

          // Make a comparison of the effective result of thre dot sync
          VersionVector<K, Integer> comparison = getVersion(order);
          comparison.sync(id, other.get(id));

          assertTrue("Instance should be identical to comparison after sync",
              instance.identical(comparison));
        }
      }
    }
  }

  @Test
  public void testSync_Dot_0() {
    testSync_Dot(0);
  }

  @Test
  public void testSync_Dot_1() {
    testSync_Dot(1);
  }

  @Test
  public void testSync_Dot_2() {
    testSync_Dot(2);
  }

  @Test
  public void testSync_Dot_3() {
    testSync_Dot(3);
  }

  @Test
  public void testSync_Dot_4() {
    testSync_Dot(4);
  }

  @Test
  public void testSync_Dot_5() {
    testSync_Dot(5);
  }

  @Test
  public void testSync_Dot_6() {
    testSync_Dot(6);
  }

  @Test
  public void testSync_Dot_7() {
    testSync_Dot(7);
  }

  @Test
  public void testSync_Dot_8() {
    testSync_Dot(8);
  }

  @Test
  public void testSync_Dot_9() {
    testSync_Dot(9);
  }

  /**
   * Test happenedBefore against a example vector a0.
   */
  @Test
  public void testHappenedBefore_examples_a0() {
    VersionVector<K, Integer> instance = getVersion("a0");

    assertEquals(false, instance.happenedBefore(getVersion("a0")));
    assertEquals(true, instance.happenedBefore(getVersion("a1")));
    assertEquals(true, instance.happenedBefore(getVersion("a2")));
    assertEquals(true, instance.happenedBefore(getVersion("a3")));
    assertEquals(true, instance.happenedBefore(getVersion("a4")));
    assertEquals(false, instance.happenedBefore(getVersion("b0")));
    assertEquals(true, instance.happenedBefore(getVersion("b1")));
    assertEquals(true, instance.happenedBefore(getVersion("b2")));
    assertEquals(true, instance.happenedBefore(getVersion("b3")));
    assertEquals(true, instance.happenedBefore(getVersion("b4")));
    assertEquals(true, instance.happenedBefore(getVersion("b5")));
    assertEquals(false, instance.happenedBefore(getVersion("c0")));
    assertEquals(true, instance.happenedBefore(getVersion("c1")));
    assertEquals(true, instance.happenedBefore(getVersion("c2")));
    assertEquals(true, instance.happenedBefore(getVersion("c3")));
    assertEquals(true, instance.happenedBefore(getVersion("c4")));
    assertEquals(true, instance.happenedBefore(getVersion("c5")));
  }

  /**
   * Test happenedBefore against a example vector a1.
   */
  @Test
  public void testHappenedBefore_examples_a1() {
    VersionVector<K, Integer> instance = getVersion("a1");

    assertEquals(false, instance.happenedBefore(getVersion("a0")));
    assertEquals(false, instance.happenedBefore(getVersion("a1")));
    assertEquals(true, instance.happenedBefore(getVersion("a2")));
    assertEquals(true, instance.happenedBefore(getVersion("a3")));
    assertEquals(true, instance.happenedBefore(getVersion("a4")));
    assertEquals(false, instance.happenedBefore(getVersion("b0")));
    assertEquals(false, instance.happenedBefore(getVersion("b1")));
    assertEquals(false, instance.happenedBefore(getVersion("b2")));
    assertEquals(false, instance.happenedBefore(getVersion("b3")));
    assertEquals(true, instance.happenedBefore(getVersion("b4")));
    assertEquals(true, instance.happenedBefore(getVersion("b5")));
    assertEquals(false, instance.happenedBefore(getVersion("c0")));
    assertEquals(false, instance.happenedBefore(getVersion("c1")));
    assertEquals(false, instance.happenedBefore(getVersion("c2")));
    assertEquals(false, instance.happenedBefore(getVersion("c3")));
    assertEquals(true, instance.happenedBefore(getVersion("c4")));
    assertEquals(true, instance.happenedBefore(getVersion("c5")));
  }

  /**
   * Test happenedBefore against a example vector a2.
   */
  @Test
  public void testHappenedBefore_examples_a2() {
    VersionVector<K, Integer> instance = getVersion("a2");

    assertEquals(false, instance.happenedBefore(getVersion("a0")));
    assertEquals(false, instance.happenedBefore(getVersion("a1")));
    assertEquals(false, instance.happenedBefore(getVersion("a2")));
    assertEquals(true, instance.happenedBefore(getVersion("a3")));
    assertEquals(true, instance.happenedBefore(getVersion("a4")));
    assertEquals(false, instance.happenedBefore(getVersion("b0")));
    assertEquals(false, instance.happenedBefore(getVersion("b1")));
    assertEquals(false, instance.happenedBefore(getVersion("b2")));
    assertEquals(false, instance.happenedBefore(getVersion("b3")));
    assertEquals(true, instance.happenedBefore(getVersion("b4")));
    assertEquals(true, instance.happenedBefore(getVersion("b5")));
    assertEquals(false, instance.happenedBefore(getVersion("c0")));
    assertEquals(false, instance.happenedBefore(getVersion("c1")));
    assertEquals(false, instance.happenedBefore(getVersion("c2")));
    assertEquals(false, instance.happenedBefore(getVersion("c3")));
    assertEquals(true, instance.happenedBefore(getVersion("c4")));
    assertEquals(true, instance.happenedBefore(getVersion("c5")));
  }

  /**
   * Test happenedBefore against a example vector a3.
   */
  @Test
  public void testHappenedBefore_examples_a3() {
    VersionVector<K, Integer> instance = getVersion("a3");

    assertEquals(false, instance.happenedBefore(getVersion("a0")));
    assertEquals(false, instance.happenedBefore(getVersion("a1")));
    assertEquals(false, instance.happenedBefore(getVersion("a2")));
    assertEquals(false, instance.happenedBefore(getVersion("a3")));
    assertEquals(true, instance.happenedBefore(getVersion("a4")));
    assertEquals(false, instance.happenedBefore(getVersion("b0")));
    assertEquals(false, instance.happenedBefore(getVersion("b1")));
    assertEquals(false, instance.happenedBefore(getVersion("b2")));
    assertEquals(false, instance.happenedBefore(getVersion("b3")));
    assertEquals(false, instance.happenedBefore(getVersion("b4")));
    assertEquals(false, instance.happenedBefore(getVersion("b5")));
    assertEquals(false, instance.happenedBefore(getVersion("c0")));
    assertEquals(false, instance.happenedBefore(getVersion("c1")));
    assertEquals(false, instance.happenedBefore(getVersion("c2")));
    assertEquals(false, instance.happenedBefore(getVersion("c3")));
    assertEquals(false, instance.happenedBefore(getVersion("c4")));
    assertEquals(false, instance.happenedBefore(getVersion("c5")));
  }

  /**
   * Test happenedBefore against a example vector a4.
   */
  @Test
  public void testHappenedBefore_examples_a4() {
    VersionVector<K, Integer> instance = getVersion("a4");

    assertEquals(false, instance.happenedBefore(getVersion("a0")));
    assertEquals(false, instance.happenedBefore(getVersion("a1")));
    assertEquals(false, instance.happenedBefore(getVersion("a2")));
    assertEquals(false, instance.happenedBefore(getVersion("a3")));
    assertEquals(false, instance.happenedBefore(getVersion("a4")));
    assertEquals(false, instance.happenedBefore(getVersion("b0")));
    assertEquals(false, instance.happenedBefore(getVersion("b1")));
    assertEquals(false, instance.happenedBefore(getVersion("b2")));
    assertEquals(false, instance.happenedBefore(getVersion("b3")));
    assertEquals(false, instance.happenedBefore(getVersion("b4")));
    assertEquals(false, instance.happenedBefore(getVersion("b5")));
    assertEquals(false, instance.happenedBefore(getVersion("c0")));
    assertEquals(false, instance.happenedBefore(getVersion("c1")));
    assertEquals(false, instance.happenedBefore(getVersion("c2")));
    assertEquals(false, instance.happenedBefore(getVersion("c3")));
    assertEquals(false, instance.happenedBefore(getVersion("c4")));
    assertEquals(false, instance.happenedBefore(getVersion("c5")));
  }

  /**
   * Test happenedBefore against a example vector b0.
   */
  @Test
  public void testHappenedBefore_examples_b0() {
    VersionVector<K, Integer> instance = getVersion("b0");

    assertEquals(false, instance.happenedBefore(getVersion("a0")));
    assertEquals(true, instance.happenedBefore(getVersion("a1")));
    assertEquals(true, instance.happenedBefore(getVersion("a2")));
    assertEquals(true, instance.happenedBefore(getVersion("a3")));
    assertEquals(true, instance.happenedBefore(getVersion("a4")));
    assertEquals(false, instance.happenedBefore(getVersion("b0")));
    assertEquals(true, instance.happenedBefore(getVersion("b1")));
    assertEquals(true, instance.happenedBefore(getVersion("b2")));
    assertEquals(true, instance.happenedBefore(getVersion("b3")));
    assertEquals(true, instance.happenedBefore(getVersion("b4")));
    assertEquals(true, instance.happenedBefore(getVersion("b5")));
    assertEquals(false, instance.happenedBefore(getVersion("c0")));
    assertEquals(true, instance.happenedBefore(getVersion("c1")));
    assertEquals(true, instance.happenedBefore(getVersion("c2")));
    assertEquals(true, instance.happenedBefore(getVersion("c3")));
    assertEquals(true, instance.happenedBefore(getVersion("c4")));
    assertEquals(true, instance.happenedBefore(getVersion("c5")));
  }

  /**
   * Test happenedBefore against a example vector b1.
   */
  @Test
  public void testHappenedBefore_examples_b1() {
    VersionVector<K, Integer> instance = getVersion("b1");

    assertEquals(false, instance.happenedBefore(getVersion("a0")));
    assertEquals(true, instance.happenedBefore(getVersion("a1")));
    assertEquals(true, instance.happenedBefore(getVersion("a2")));
    assertEquals(true, instance.happenedBefore(getVersion("a3")));
    assertEquals(true, instance.happenedBefore(getVersion("a4")));
    assertEquals(false, instance.happenedBefore(getVersion("b0")));
    assertEquals(false, instance.happenedBefore(getVersion("b1")));
    assertEquals(true, instance.happenedBefore(getVersion("b2")));
    assertEquals(true, instance.happenedBefore(getVersion("b3")));
    assertEquals(true, instance.happenedBefore(getVersion("b4")));
    assertEquals(true, instance.happenedBefore(getVersion("b5")));
    assertEquals(false, instance.happenedBefore(getVersion("c0")));
    assertEquals(false, instance.happenedBefore(getVersion("c1")));
    assertEquals(true, instance.happenedBefore(getVersion("c2")));
    assertEquals(true, instance.happenedBefore(getVersion("c3")));
    assertEquals(true, instance.happenedBefore(getVersion("c4")));
    assertEquals(true, instance.happenedBefore(getVersion("c5")));
  }

  /**
   * Test happenedBefore against a example vector b2.
   */
  @Test
  public void testHappenedBefore_examples_b2() {
    VersionVector<K, Integer> instance = getVersion("b2");

    assertEquals(false, instance.happenedBefore(getVersion("a0")));
    assertEquals(true, instance.happenedBefore(getVersion("a1")));
    assertEquals(true, instance.happenedBefore(getVersion("a2")));
    assertEquals(true, instance.happenedBefore(getVersion("a3")));
    assertEquals(true, instance.happenedBefore(getVersion("a4")));
    assertEquals(false, instance.happenedBefore(getVersion("b0")));
    assertEquals(false, instance.happenedBefore(getVersion("b1")));
    assertEquals(false, instance.happenedBefore(getVersion("b2")));
    assertEquals(true, instance.happenedBefore(getVersion("b3")));
    assertEquals(true, instance.happenedBefore(getVersion("b4")));
    assertEquals(true, instance.happenedBefore(getVersion("b5")));
    assertEquals(false, instance.happenedBefore(getVersion("c0")));
    assertEquals(false, instance.happenedBefore(getVersion("c1")));
    assertEquals(true, instance.happenedBefore(getVersion("c2")));
    assertEquals(true, instance.happenedBefore(getVersion("c3")));
    assertEquals(true, instance.happenedBefore(getVersion("c4")));
    assertEquals(true, instance.happenedBefore(getVersion("c5")));
  }

  /**
   * Test happenedBefore against a example vector b3.
   */
  @Test
  public void testHappenedBefore_examples_b3() {
    VersionVector<K, Integer> instance = getVersion("b3");

    assertEquals(false, instance.happenedBefore(getVersion("a0")));
    assertEquals(false, instance.happenedBefore(getVersion("a1")));
    assertEquals(false, instance.happenedBefore(getVersion("a2")));
    assertEquals(true, instance.happenedBefore(getVersion("a3")));
    assertEquals(true, instance.happenedBefore(getVersion("a4")));
    assertEquals(false, instance.happenedBefore(getVersion("b0")));
    assertEquals(false, instance.happenedBefore(getVersion("b1")));
    assertEquals(false, instance.happenedBefore(getVersion("b2")));
    assertEquals(false, instance.happenedBefore(getVersion("b3")));
    assertEquals(true, instance.happenedBefore(getVersion("b4")));
    assertEquals(true, instance.happenedBefore(getVersion("b5")));
    assertEquals(false, instance.happenedBefore(getVersion("c0")));
    assertEquals(false, instance.happenedBefore(getVersion("c1")));
    assertEquals(true, instance.happenedBefore(getVersion("c2")));
    assertEquals(true, instance.happenedBefore(getVersion("c3")));
    assertEquals(true, instance.happenedBefore(getVersion("c4")));
    assertEquals(true, instance.happenedBefore(getVersion("c5")));
  }

  /**
   * Test happenedBefore against a example vector b4.
   */
  @Test
  public void testHappenedBefore_examples_b4() {
    VersionVector<K, Integer> instance = getVersion("b4");

    assertEquals(false, instance.happenedBefore(getVersion("a0")));
    assertEquals(false, instance.happenedBefore(getVersion("a1")));
    assertEquals(false, instance.happenedBefore(getVersion("a2")));
    assertEquals(false, instance.happenedBefore(getVersion("a3")));
    assertEquals(true, instance.happenedBefore(getVersion("a4")));
    assertEquals(false, instance.happenedBefore(getVersion("b0")));
    assertEquals(false, instance.happenedBefore(getVersion("b1")));
    assertEquals(false, instance.happenedBefore(getVersion("b2")));
    assertEquals(false, instance.happenedBefore(getVersion("b3")));
    assertEquals(false, instance.happenedBefore(getVersion("b4")));
    assertEquals(true, instance.happenedBefore(getVersion("b5")));
    assertEquals(false, instance.happenedBefore(getVersion("c0")));
    assertEquals(false, instance.happenedBefore(getVersion("c1")));
    assertEquals(false, instance.happenedBefore(getVersion("c2")));
    assertEquals(false, instance.happenedBefore(getVersion("c3")));
    assertEquals(true, instance.happenedBefore(getVersion("c4")));
    assertEquals(true, instance.happenedBefore(getVersion("c5")));
  }

  /**
   * Test happenedBefore against a example vector b5.
   */
  @Test
  public void testHappenedBefore_examples_b5() {
    VersionVector<K, Integer> instance = getVersion("b5");

    assertEquals(false, instance.happenedBefore(getVersion("a0")));
    assertEquals(false, instance.happenedBefore(getVersion("a1")));
    assertEquals(false, instance.happenedBefore(getVersion("a2")));
    assertEquals(false, instance.happenedBefore(getVersion("a3")));
    assertEquals(true, instance.happenedBefore(getVersion("a4")));
    assertEquals(false, instance.happenedBefore(getVersion("b0")));
    assertEquals(false, instance.happenedBefore(getVersion("b1")));
    assertEquals(false, instance.happenedBefore(getVersion("b2")));
    assertEquals(false, instance.happenedBefore(getVersion("b3")));
    assertEquals(false, instance.happenedBefore(getVersion("b4")));
    assertEquals(false, instance.happenedBefore(getVersion("b5")));
    assertEquals(false, instance.happenedBefore(getVersion("c0")));
    assertEquals(false, instance.happenedBefore(getVersion("c1")));
    assertEquals(false, instance.happenedBefore(getVersion("c2")));
    assertEquals(false, instance.happenedBefore(getVersion("c3")));
    assertEquals(true, instance.happenedBefore(getVersion("c4")));
    assertEquals(true, instance.happenedBefore(getVersion("c5")));
  }

  /**
   * Test happenedBefore against a example vector c0.
   */
  @Test
  public void testHappenedBefore_examples_c0() {
    VersionVector<K, Integer> instance = getVersion("c0");

    assertEquals(false, instance.happenedBefore(getVersion("a0")));
    assertEquals(true, instance.happenedBefore(getVersion("a1")));
    assertEquals(true, instance.happenedBefore(getVersion("a2")));
    assertEquals(true, instance.happenedBefore(getVersion("a3")));
    assertEquals(true, instance.happenedBefore(getVersion("a4")));
    assertEquals(false, instance.happenedBefore(getVersion("b0")));
    assertEquals(true, instance.happenedBefore(getVersion("b1")));
    assertEquals(true, instance.happenedBefore(getVersion("b2")));
    assertEquals(true, instance.happenedBefore(getVersion("b3")));
    assertEquals(true, instance.happenedBefore(getVersion("b4")));
    assertEquals(true, instance.happenedBefore(getVersion("b5")));
    assertEquals(false, instance.happenedBefore(getVersion("c0")));
    assertEquals(true, instance.happenedBefore(getVersion("c1")));
    assertEquals(true, instance.happenedBefore(getVersion("c2")));
    assertEquals(true, instance.happenedBefore(getVersion("c3")));
    assertEquals(true, instance.happenedBefore(getVersion("c4")));
    assertEquals(true, instance.happenedBefore(getVersion("c5")));
  }

  /**
   * Test happenedBefore against a example vector c1.
   */
  @Test
  public void testHappenedBefore_examples_c1() {
    VersionVector<K, Integer> instance = getVersion("c1");

    assertEquals(false, instance.happenedBefore(getVersion("a0")));
    assertEquals(true, instance.happenedBefore(getVersion("a1")));
    assertEquals(true, instance.happenedBefore(getVersion("a2")));
    assertEquals(true, instance.happenedBefore(getVersion("a3")));
    assertEquals(true, instance.happenedBefore(getVersion("a4")));
    assertEquals(false, instance.happenedBefore(getVersion("b0")));
    assertEquals(true, instance.happenedBefore(getVersion("b1")));
    assertEquals(true, instance.happenedBefore(getVersion("b2")));
    assertEquals(true, instance.happenedBefore(getVersion("b3")));
    assertEquals(true, instance.happenedBefore(getVersion("b4")));
    assertEquals(true, instance.happenedBefore(getVersion("b5")));
    assertEquals(false, instance.happenedBefore(getVersion("c0")));
    assertEquals(false, instance.happenedBefore(getVersion("c1")));
    assertEquals(true, instance.happenedBefore(getVersion("c2")));
    assertEquals(true, instance.happenedBefore(getVersion("c3")));
    assertEquals(true, instance.happenedBefore(getVersion("c4")));
    assertEquals(true, instance.happenedBefore(getVersion("c5")));
  }

  /**
   * Test happenedBefore against a example vector c2.
   */
  @Test
  public void testHappenedBefore_examples_c2() {
    VersionVector<K, Integer> instance = getVersion("c2");

    assertEquals(false, instance.happenedBefore(getVersion("a0")));
    assertEquals(false, instance.happenedBefore(getVersion("a1")));
    assertEquals(false, instance.happenedBefore(getVersion("a2")));
    assertEquals(true, instance.happenedBefore(getVersion("a3")));
    assertEquals(true, instance.happenedBefore(getVersion("a4")));
    assertEquals(false, instance.happenedBefore(getVersion("b0")));
    assertEquals(false, instance.happenedBefore(getVersion("b1")));
    assertEquals(false, instance.happenedBefore(getVersion("b2")));
    assertEquals(false, instance.happenedBefore(getVersion("b3")));
    assertEquals(false, instance.happenedBefore(getVersion("b4")));
    assertEquals(false, instance.happenedBefore(getVersion("b5")));
    assertEquals(false, instance.happenedBefore(getVersion("c0")));
    assertEquals(false, instance.happenedBefore(getVersion("c1")));
    assertEquals(false, instance.happenedBefore(getVersion("c2")));
    assertEquals(true, instance.happenedBefore(getVersion("c3")));
    assertEquals(true, instance.happenedBefore(getVersion("c4")));
    assertEquals(true, instance.happenedBefore(getVersion("c5")));
  }

  /**
   * Test happenedBefore against a example vector c3.
   */
  @Test
  public void testHappenedBefore_examples_c3() {
    VersionVector<K, Integer> instance = getVersion("c3");

    assertEquals(false, instance.happenedBefore(getVersion("a0")));
    assertEquals(false, instance.happenedBefore(getVersion("a1")));
    assertEquals(false, instance.happenedBefore(getVersion("a2")));
    assertEquals(true, instance.happenedBefore(getVersion("a3")));
    assertEquals(true, instance.happenedBefore(getVersion("a4")));
    assertEquals(false, instance.happenedBefore(getVersion("b0")));
    assertEquals(false, instance.happenedBefore(getVersion("b1")));
    assertEquals(false, instance.happenedBefore(getVersion("b2")));
    assertEquals(false, instance.happenedBefore(getVersion("b3")));
    assertEquals(false, instance.happenedBefore(getVersion("b4")));
    assertEquals(false, instance.happenedBefore(getVersion("b5")));
    assertEquals(false, instance.happenedBefore(getVersion("c0")));
    assertEquals(false, instance.happenedBefore(getVersion("c1")));
    assertEquals(false, instance.happenedBefore(getVersion("c2")));
    assertEquals(false, instance.happenedBefore(getVersion("c3")));
    assertEquals(true, instance.happenedBefore(getVersion("c4")));
    assertEquals(true, instance.happenedBefore(getVersion("c5")));
  }

  /**
   * Test happenedBefore against a example vector c4.
   */
  @Test
  public void testHappenedBefore_examples_c4() {
    VersionVector<K, Integer> instance = getVersion("c4");

    assertEquals(false, instance.happenedBefore(getVersion("a0")));
    assertEquals(false, instance.happenedBefore(getVersion("a1")));
    assertEquals(false, instance.happenedBefore(getVersion("a2")));
    assertEquals(false, instance.happenedBefore(getVersion("a3")));
    assertEquals(true, instance.happenedBefore(getVersion("a4")));
    assertEquals(false, instance.happenedBefore(getVersion("b0")));
    assertEquals(false, instance.happenedBefore(getVersion("b1")));
    assertEquals(false, instance.happenedBefore(getVersion("b2")));
    assertEquals(false, instance.happenedBefore(getVersion("b3")));
    assertEquals(false, instance.happenedBefore(getVersion("b4")));
    assertEquals(false, instance.happenedBefore(getVersion("b5")));
    assertEquals(false, instance.happenedBefore(getVersion("c0")));
    assertEquals(false, instance.happenedBefore(getVersion("c1")));
    assertEquals(false, instance.happenedBefore(getVersion("c2")));
    assertEquals(false, instance.happenedBefore(getVersion("c3")));
    assertEquals(false, instance.happenedBefore(getVersion("c4")));
    assertEquals(true, instance.happenedBefore(getVersion("c5")));
  }

  /**
   * Test happenedBefore against a example vector c5.
   */
  @Test
  public void testHappenedBefore_examples_c5() {
    VersionVector<K, Integer> instance = getVersion("c5");

    assertEquals(false, instance.happenedBefore(getVersion("a0")));
    assertEquals(false, instance.happenedBefore(getVersion("a1")));
    assertEquals(false, instance.happenedBefore(getVersion("a2")));
    assertEquals(false, instance.happenedBefore(getVersion("a3")));
    assertEquals(true, instance.happenedBefore(getVersion("a4")));
    assertEquals(false, instance.happenedBefore(getVersion("b0")));
    assertEquals(false, instance.happenedBefore(getVersion("b1")));
    assertEquals(false, instance.happenedBefore(getVersion("b2")));
    assertEquals(false, instance.happenedBefore(getVersion("b3")));
    assertEquals(false, instance.happenedBefore(getVersion("b4")));
    assertEquals(false, instance.happenedBefore(getVersion("b5")));
    assertEquals(false, instance.happenedBefore(getVersion("c0")));
    assertEquals(false, instance.happenedBefore(getVersion("c1")));
    assertEquals(false, instance.happenedBefore(getVersion("c2")));
    assertEquals(false, instance.happenedBefore(getVersion("c3")));
    assertEquals(false, instance.happenedBefore(getVersion("c4")));
    assertEquals(false, instance.happenedBefore(getVersion("c5")));
  }

  private void testHappenedBefore_Dot(final int order) {
    VersionVector<K, Integer> instance = getVersion(order);

    for (int i = 0; i < VERSION_MAX_ORDER; i++) {
      VersionVector<K, Integer> other = getVersion(i);

      for (int j = 0; j < 3; j++) {
        K id = getKey(j);

        if (other.getIdentifiers().contains(id)) {
          Dot<K, Integer> dot = other.getDot(id);

          assertEquals(instance.get(id) < other.get(id), instance.happenedBefore(dot));
        }
      }
    }
  }

  @Test
  public void testHappenedBefore_Dot_0() {
    testHappenedBefore_Dot(0);
  }

  @Test
  public void testHappenedBefore_Dot_1() {
    testHappenedBefore_Dot(1);
  }

  @Test
  public void testHappenedBefore_Dot_2() {
    testHappenedBefore_Dot(2);
  }

  @Test
  public void testHappenedBefore_Dot_3() {
    testHappenedBefore_Dot(3);
  }

  @Test
  public void testHappenedBefore_Dot_4() {
    testHappenedBefore_Dot(4);
  }

  @Test
  public void testHappenedBefore_Dot_5() {
    testHappenedBefore_Dot(5);
  }

  @Test
  public void testHappenedBefore_Dot_6() {
    testHappenedBefore_Dot(6);
  }

  @Test
  public void testHappenedBefore_Dot_7() {
    testHappenedBefore_Dot(7);
  }

  @Test
  public void testHappenedBefore_Dot_8() {
    testHappenedBefore_Dot(8);
  }

  @Test
  public void testHappenedBefore_Dot_9() {
    testHappenedBefore_Dot(9);
  }

  @Override
  public void testHappenedBefore_0() {}

  @Override
  public void testHappenedBefore_1() {}

  @Override
  public void testHappenedBefore_2() {}

  @Override
  public void testHappenedBefore_3() {}

  @Override
  public void testHappenedBefore_4() {}

  @Override
  public void testHappenedBefore_5() {}

  @Override
  public void testHappenedBefore_6() {}

  @Override
  public void testHappenedBefore_7() {}

  @Override
  public void testHappenedBefore_8() {}

  @Override
  public void testHappenedBefore_9() {}

  /**
   * Test concurrentWith against a example vector a0.
   */
  @Test
  public void testConcurrentWith_examples_a0() {
    VersionVector<K, Integer> instance = getVersion("a0");

    assertEquals(false, instance.concurrentWith(getVersion("a0")));
    assertEquals(false, instance.concurrentWith(getVersion("a1")));
    assertEquals(false, instance.concurrentWith(getVersion("a2")));
    assertEquals(false, instance.concurrentWith(getVersion("a3")));
    assertEquals(false, instance.concurrentWith(getVersion("a4")));
    assertEquals(false, instance.concurrentWith(getVersion("b0")));
    assertEquals(false, instance.concurrentWith(getVersion("b1")));
    assertEquals(false, instance.concurrentWith(getVersion("b2")));
    assertEquals(false, instance.concurrentWith(getVersion("b3")));
    assertEquals(false, instance.concurrentWith(getVersion("b4")));
    assertEquals(false, instance.concurrentWith(getVersion("b5")));
    assertEquals(false, instance.concurrentWith(getVersion("c0")));
    assertEquals(false, instance.concurrentWith(getVersion("c1")));
    assertEquals(false, instance.concurrentWith(getVersion("c2")));
    assertEquals(false, instance.concurrentWith(getVersion("c3")));
    assertEquals(false, instance.concurrentWith(getVersion("c4")));
    assertEquals(false, instance.concurrentWith(getVersion("c5")));
  }

  /**
   * Test concurrentWith against a example vector a1.
   */
  @Test
  public void testConcurrentWith_examples_a1() {
    VersionVector<K, Integer> instance = getVersion("a1");

    assertEquals(false, instance.concurrentWith(getVersion("a0")));
    assertEquals(false, instance.concurrentWith(getVersion("a1")));
    assertEquals(false, instance.concurrentWith(getVersion("a2")));
    assertEquals(false, instance.concurrentWith(getVersion("a3")));
    assertEquals(false, instance.concurrentWith(getVersion("a4")));
    assertEquals(false, instance.concurrentWith(getVersion("b0")));
    assertEquals(false, instance.concurrentWith(getVersion("b1")));
    assertEquals(false, instance.concurrentWith(getVersion("b2")));
    assertEquals(true, instance.concurrentWith(getVersion("b3")));
    assertEquals(false, instance.concurrentWith(getVersion("b4")));
    assertEquals(false, instance.concurrentWith(getVersion("b5")));
    assertEquals(false, instance.concurrentWith(getVersion("c0")));
    assertEquals(false, instance.concurrentWith(getVersion("c1")));
    assertEquals(true, instance.concurrentWith(getVersion("c2")));
    assertEquals(true, instance.concurrentWith(getVersion("c3")));
    assertEquals(false, instance.concurrentWith(getVersion("c4")));
    assertEquals(false, instance.concurrentWith(getVersion("c5")));
  }

  /**
   * Test concurrentWith against a example vector a2.
   */
  @Test
  public void testConcurrentWith_examples_a2() {
    VersionVector<K, Integer> instance = getVersion("a2");

    assertEquals(false, instance.concurrentWith(getVersion("a0")));
    assertEquals(false, instance.concurrentWith(getVersion("a1")));
    assertEquals(false, instance.concurrentWith(getVersion("a2")));
    assertEquals(false, instance.concurrentWith(getVersion("a3")));
    assertEquals(false, instance.concurrentWith(getVersion("a4")));
    assertEquals(false, instance.concurrentWith(getVersion("b0")));
    assertEquals(false, instance.concurrentWith(getVersion("b1")));
    assertEquals(false, instance.concurrentWith(getVersion("b2")));
    assertEquals(true, instance.concurrentWith(getVersion("b3")));
    assertEquals(false, instance.concurrentWith(getVersion("b4")));
    assertEquals(false, instance.concurrentWith(getVersion("b5")));
    assertEquals(false, instance.concurrentWith(getVersion("c0")));
    assertEquals(false, instance.concurrentWith(getVersion("c1")));
    assertEquals(true, instance.concurrentWith(getVersion("c2")));
    assertEquals(true, instance.concurrentWith(getVersion("c3")));
    assertEquals(false, instance.concurrentWith(getVersion("c4")));
    assertEquals(false, instance.concurrentWith(getVersion("c5")));
  }

  /**
   * Test concurrentWith against a example vector a3.
   */
  @Test
  public void testConcurrentWith_examples_a3() {
    VersionVector<K, Integer> instance = getVersion("a3");

    assertEquals(false, instance.concurrentWith(getVersion("a0")));
    assertEquals(false, instance.concurrentWith(getVersion("a1")));
    assertEquals(false, instance.concurrentWith(getVersion("a2")));
    assertEquals(false, instance.concurrentWith(getVersion("a3")));
    assertEquals(false, instance.concurrentWith(getVersion("a4")));
    assertEquals(false, instance.concurrentWith(getVersion("b0")));
    assertEquals(false, instance.concurrentWith(getVersion("b1")));
    assertEquals(false, instance.concurrentWith(getVersion("b2")));
    assertEquals(false, instance.concurrentWith(getVersion("b3")));
    assertEquals(true, instance.concurrentWith(getVersion("b4")));
    assertEquals(true, instance.concurrentWith(getVersion("b5")));
    assertEquals(false, instance.concurrentWith(getVersion("c0")));
    assertEquals(false, instance.concurrentWith(getVersion("c1")));
    assertEquals(false, instance.concurrentWith(getVersion("c2")));
    assertEquals(false, instance.concurrentWith(getVersion("c3")));
    assertEquals(true, instance.concurrentWith(getVersion("c4")));
    assertEquals(true, instance.concurrentWith(getVersion("c5")));
  }

  /**
   * Test concurrentWith against a example vector a4.
   */
  @Test
  public void testConcurrentWith_examples_a4() {
    VersionVector<K, Integer> instance = getVersion("a4");

    assertEquals(false, instance.concurrentWith(getVersion("a0")));
    assertEquals(false, instance.concurrentWith(getVersion("a1")));
    assertEquals(false, instance.concurrentWith(getVersion("a2")));
    assertEquals(false, instance.concurrentWith(getVersion("a3")));
    assertEquals(false, instance.concurrentWith(getVersion("a4")));
    assertEquals(false, instance.concurrentWith(getVersion("b0")));
    assertEquals(false, instance.concurrentWith(getVersion("b1")));
    assertEquals(false, instance.concurrentWith(getVersion("b2")));
    assertEquals(false, instance.concurrentWith(getVersion("b3")));
    assertEquals(false, instance.concurrentWith(getVersion("b4")));
    assertEquals(false, instance.concurrentWith(getVersion("b5")));
    assertEquals(false, instance.concurrentWith(getVersion("c0")));
    assertEquals(false, instance.concurrentWith(getVersion("c1")));
    assertEquals(false, instance.concurrentWith(getVersion("c2")));
    assertEquals(false, instance.concurrentWith(getVersion("c3")));
    assertEquals(false, instance.concurrentWith(getVersion("c4")));
    assertEquals(false, instance.concurrentWith(getVersion("c5")));
  }

  /**
   * Test concurrentWith against a example vector b0.
   */
  @Test
  public void testConcurrentWith_examples_b0() {
    VersionVector<K, Integer> instance = getVersion("b0");

    assertEquals(false, instance.concurrentWith(getVersion("a0")));
    assertEquals(false, instance.concurrentWith(getVersion("a1")));
    assertEquals(false, instance.concurrentWith(getVersion("a2")));
    assertEquals(false, instance.concurrentWith(getVersion("a3")));
    assertEquals(false, instance.concurrentWith(getVersion("a4")));
    assertEquals(false, instance.concurrentWith(getVersion("b0")));
    assertEquals(false, instance.concurrentWith(getVersion("b1")));
    assertEquals(false, instance.concurrentWith(getVersion("b2")));
    assertEquals(false, instance.concurrentWith(getVersion("b3")));
    assertEquals(false, instance.concurrentWith(getVersion("b4")));
    assertEquals(false, instance.concurrentWith(getVersion("b5")));
    assertEquals(false, instance.concurrentWith(getVersion("c0")));
    assertEquals(false, instance.concurrentWith(getVersion("c1")));
    assertEquals(false, instance.concurrentWith(getVersion("c2")));
    assertEquals(false, instance.concurrentWith(getVersion("c3")));
    assertEquals(false, instance.concurrentWith(getVersion("c4")));
    assertEquals(false, instance.concurrentWith(getVersion("c5")));
  }

  /**
   * Test concurrentWith against a example vector b1.
   */
  @Test
  public void testConcurrentWith_examples_b1() {
    VersionVector<K, Integer> instance = getVersion("b1");

    assertEquals(false, instance.concurrentWith(getVersion("a0")));
    assertEquals(false, instance.concurrentWith(getVersion("a1")));
    assertEquals(false, instance.concurrentWith(getVersion("a2")));
    assertEquals(false, instance.concurrentWith(getVersion("a3")));
    assertEquals(false, instance.concurrentWith(getVersion("a4")));
    assertEquals(false, instance.concurrentWith(getVersion("b0")));
    assertEquals(false, instance.concurrentWith(getVersion("b1")));
    assertEquals(false, instance.concurrentWith(getVersion("b2")));
    assertEquals(false, instance.concurrentWith(getVersion("b3")));
    assertEquals(false, instance.concurrentWith(getVersion("b4")));
    assertEquals(false, instance.concurrentWith(getVersion("b5")));
    assertEquals(false, instance.concurrentWith(getVersion("c0")));
    assertEquals(false, instance.concurrentWith(getVersion("c1")));
    assertEquals(false, instance.concurrentWith(getVersion("c2")));
    assertEquals(false, instance.concurrentWith(getVersion("c3")));
    assertEquals(false, instance.concurrentWith(getVersion("c4")));
    assertEquals(false, instance.concurrentWith(getVersion("c5")));
  }

  /**
   * Test concurrentWith against a example vector b2.
   */
  @Test
  public void testConcurrentWith_examples_b2() {
    VersionVector<K, Integer> instance = getVersion("b2");

    assertEquals(false, instance.concurrentWith(getVersion("a0")));
    assertEquals(false, instance.concurrentWith(getVersion("a1")));
    assertEquals(false, instance.concurrentWith(getVersion("a2")));
    assertEquals(false, instance.concurrentWith(getVersion("a3")));
    assertEquals(false, instance.concurrentWith(getVersion("a4")));
    assertEquals(false, instance.concurrentWith(getVersion("b0")));
    assertEquals(false, instance.concurrentWith(getVersion("b1")));
    assertEquals(false, instance.concurrentWith(getVersion("b2")));
    assertEquals(false, instance.concurrentWith(getVersion("b3")));
    assertEquals(false, instance.concurrentWith(getVersion("b4")));
    assertEquals(false, instance.concurrentWith(getVersion("b5")));
    assertEquals(false, instance.concurrentWith(getVersion("c0")));
    assertEquals(false, instance.concurrentWith(getVersion("c1")));
    assertEquals(false, instance.concurrentWith(getVersion("c2")));
    assertEquals(false, instance.concurrentWith(getVersion("c3")));
    assertEquals(false, instance.concurrentWith(getVersion("c4")));
    assertEquals(false, instance.concurrentWith(getVersion("c5")));
  }

  /**
   * Test concurrentWith against a example vector b3.
   */
  @Test
  public void testConcurrentWith_examples_b3() {
    VersionVector<K, Integer> instance = getVersion("b3");

    assertEquals(false, instance.concurrentWith(getVersion("a0")));
    assertEquals(true, instance.concurrentWith(getVersion("a1")));
    assertEquals(true, instance.concurrentWith(getVersion("a2")));
    assertEquals(false, instance.concurrentWith(getVersion("a3")));
    assertEquals(false, instance.concurrentWith(getVersion("a4")));
    assertEquals(false, instance.concurrentWith(getVersion("b0")));
    assertEquals(false, instance.concurrentWith(getVersion("b1")));
    assertEquals(false, instance.concurrentWith(getVersion("b2")));
    assertEquals(false, instance.concurrentWith(getVersion("b3")));
    assertEquals(false, instance.concurrentWith(getVersion("b4")));
    assertEquals(false, instance.concurrentWith(getVersion("b5")));
    assertEquals(false, instance.concurrentWith(getVersion("c0")));
    assertEquals(false, instance.concurrentWith(getVersion("c1")));
    assertEquals(false, instance.concurrentWith(getVersion("c2")));
    assertEquals(false, instance.concurrentWith(getVersion("c3")));
    assertEquals(false, instance.concurrentWith(getVersion("c4")));
    assertEquals(false, instance.concurrentWith(getVersion("c5")));
  }

  /**
   * Test concurrentWith against a example vector b4.
   */
  @Test
  public void testConcurrentWith_examples_b4() {
    VersionVector<K, Integer> instance = getVersion("b4");

    assertEquals(false, instance.concurrentWith(getVersion("a0")));
    assertEquals(false, instance.concurrentWith(getVersion("a1")));
    assertEquals(false, instance.concurrentWith(getVersion("a2")));
    assertEquals(true, instance.concurrentWith(getVersion("a3")));
    assertEquals(false, instance.concurrentWith(getVersion("a4")));
    assertEquals(false, instance.concurrentWith(getVersion("b0")));
    assertEquals(false, instance.concurrentWith(getVersion("b1")));
    assertEquals(false, instance.concurrentWith(getVersion("b2")));
    assertEquals(false, instance.concurrentWith(getVersion("b3")));
    assertEquals(false, instance.concurrentWith(getVersion("b4")));
    assertEquals(false, instance.concurrentWith(getVersion("b5")));
    assertEquals(false, instance.concurrentWith(getVersion("c0")));
    assertEquals(false, instance.concurrentWith(getVersion("c1")));
    assertEquals(true, instance.concurrentWith(getVersion("c2")));
    assertEquals(true, instance.concurrentWith(getVersion("c3")));
    assertEquals(false, instance.concurrentWith(getVersion("c4")));
    assertEquals(false, instance.concurrentWith(getVersion("c5")));
  }

  /**
   * Test concurrentWith against a example vector b5.
   */
  @Test
  public void testConcurrentWith_examples_b5() {
    VersionVector<K, Integer> instance = getVersion("b5");

    assertEquals(false, instance.concurrentWith(getVersion("a0")));
    assertEquals(false, instance.concurrentWith(getVersion("a1")));
    assertEquals(false, instance.concurrentWith(getVersion("a2")));
    assertEquals(true, instance.concurrentWith(getVersion("a3")));
    assertEquals(false, instance.concurrentWith(getVersion("a4")));
    assertEquals(false, instance.concurrentWith(getVersion("b0")));
    assertEquals(false, instance.concurrentWith(getVersion("b1")));
    assertEquals(false, instance.concurrentWith(getVersion("b2")));
    assertEquals(false, instance.concurrentWith(getVersion("b3")));
    assertEquals(false, instance.concurrentWith(getVersion("b4")));
    assertEquals(false, instance.concurrentWith(getVersion("b5")));
    assertEquals(false, instance.concurrentWith(getVersion("c0")));
    assertEquals(false, instance.concurrentWith(getVersion("c1")));
    assertEquals(true, instance.concurrentWith(getVersion("c2")));
    assertEquals(true, instance.concurrentWith(getVersion("c3")));
    assertEquals(false, instance.concurrentWith(getVersion("c4")));
    assertEquals(false, instance.concurrentWith(getVersion("c5")));
  }

  /**
   * Test concurrentWith against a example vector c0.
   */
  @Test
  public void testConcurrentWith_examples_c0() {
    VersionVector<K, Integer> instance = getVersion("c0");

    assertEquals(false, instance.concurrentWith(getVersion("a0")));
    assertEquals(false, instance.concurrentWith(getVersion("a1")));
    assertEquals(false, instance.concurrentWith(getVersion("a2")));
    assertEquals(false, instance.concurrentWith(getVersion("a3")));
    assertEquals(false, instance.concurrentWith(getVersion("a4")));
    assertEquals(false, instance.concurrentWith(getVersion("b0")));
    assertEquals(false, instance.concurrentWith(getVersion("b1")));
    assertEquals(false, instance.concurrentWith(getVersion("b2")));
    assertEquals(false, instance.concurrentWith(getVersion("b3")));
    assertEquals(false, instance.concurrentWith(getVersion("b4")));
    assertEquals(false, instance.concurrentWith(getVersion("b5")));
    assertEquals(false, instance.concurrentWith(getVersion("c0")));
    assertEquals(false, instance.concurrentWith(getVersion("c1")));
    assertEquals(false, instance.concurrentWith(getVersion("c2")));
    assertEquals(false, instance.concurrentWith(getVersion("c3")));
    assertEquals(false, instance.concurrentWith(getVersion("c4")));
    assertEquals(false, instance.concurrentWith(getVersion("c5")));
  }

  /**
   * Test concurrentWith against a example vector c1.
   */
  @Test
  public void testConcurrentWith_examples_c1() {
    VersionVector<K, Integer> instance = getVersion("c1");

    assertEquals(false, instance.concurrentWith(getVersion("a0")));
    assertEquals(false, instance.concurrentWith(getVersion("a1")));
    assertEquals(false, instance.concurrentWith(getVersion("a2")));
    assertEquals(false, instance.concurrentWith(getVersion("a3")));
    assertEquals(false, instance.concurrentWith(getVersion("a4")));
    assertEquals(false, instance.concurrentWith(getVersion("b0")));
    assertEquals(false, instance.concurrentWith(getVersion("b1")));
    assertEquals(false, instance.concurrentWith(getVersion("b2")));
    assertEquals(false, instance.concurrentWith(getVersion("b3")));
    assertEquals(false, instance.concurrentWith(getVersion("b4")));
    assertEquals(false, instance.concurrentWith(getVersion("b5")));
    assertEquals(false, instance.concurrentWith(getVersion("c0")));
    assertEquals(false, instance.concurrentWith(getVersion("c1")));
    assertEquals(false, instance.concurrentWith(getVersion("c2")));
    assertEquals(false, instance.concurrentWith(getVersion("c3")));
    assertEquals(false, instance.concurrentWith(getVersion("c4")));
    assertEquals(false, instance.concurrentWith(getVersion("c5")));
  }

  /**
   * Test concurrentWith against a example vector c2.
   */
  @Test
  public void testConcurrentWith_examples_c2() {
    VersionVector<K, Integer> instance = getVersion("c2");

    assertEquals(false, instance.concurrentWith(getVersion("a0")));
    assertEquals(true, instance.concurrentWith(getVersion("a1")));
    assertEquals(true, instance.concurrentWith(getVersion("a2")));
    assertEquals(false, instance.concurrentWith(getVersion("a3")));
    assertEquals(false, instance.concurrentWith(getVersion("a4")));
    assertEquals(false, instance.concurrentWith(getVersion("b0")));
    assertEquals(false, instance.concurrentWith(getVersion("b1")));
    assertEquals(false, instance.concurrentWith(getVersion("b2")));
    assertEquals(false, instance.concurrentWith(getVersion("b3")));
    assertEquals(true, instance.concurrentWith(getVersion("b4")));
    assertEquals(true, instance.concurrentWith(getVersion("b5")));
    assertEquals(false, instance.concurrentWith(getVersion("c0")));
    assertEquals(false, instance.concurrentWith(getVersion("c1")));
    assertEquals(false, instance.concurrentWith(getVersion("c2")));
    assertEquals(false, instance.concurrentWith(getVersion("c3")));
    assertEquals(false, instance.concurrentWith(getVersion("c4")));
    assertEquals(false, instance.concurrentWith(getVersion("c5")));
  }

  /**
   * Test concurrentWith against a example vector c3.
   */
  @Test
  public void testConcurrentWith_examples_c3() {
    VersionVector<K, Integer> instance = getVersion("c3");

    assertEquals(false, instance.concurrentWith(getVersion("a0")));
    assertEquals(true, instance.concurrentWith(getVersion("a1")));
    assertEquals(true, instance.concurrentWith(getVersion("a2")));
    assertEquals(false, instance.concurrentWith(getVersion("a3")));
    assertEquals(false, instance.concurrentWith(getVersion("a4")));
    assertEquals(false, instance.concurrentWith(getVersion("b0")));
    assertEquals(false, instance.concurrentWith(getVersion("b1")));
    assertEquals(false, instance.concurrentWith(getVersion("b2")));
    assertEquals(false, instance.concurrentWith(getVersion("b3")));
    assertEquals(true, instance.concurrentWith(getVersion("b4")));
    assertEquals(true, instance.concurrentWith(getVersion("b5")));
    assertEquals(false, instance.concurrentWith(getVersion("c0")));
    assertEquals(false, instance.concurrentWith(getVersion("c1")));
    assertEquals(false, instance.concurrentWith(getVersion("c2")));
    assertEquals(false, instance.concurrentWith(getVersion("c3")));
    assertEquals(false, instance.concurrentWith(getVersion("c4")));
    assertEquals(false, instance.concurrentWith(getVersion("c5")));
  }

  /**
   * Test concurrentWith against a example vector c4.
   */
  @Test
  public void testConcurrentWith_examples_c4() {
    VersionVector<K, Integer> instance = getVersion("c4");

    assertEquals(false, instance.concurrentWith(getVersion("a0")));
    assertEquals(false, instance.concurrentWith(getVersion("a1")));
    assertEquals(false, instance.concurrentWith(getVersion("a2")));
    assertEquals(true, instance.concurrentWith(getVersion("a3")));
    assertEquals(false, instance.concurrentWith(getVersion("a4")));
    assertEquals(false, instance.concurrentWith(getVersion("b0")));
    assertEquals(false, instance.concurrentWith(getVersion("b1")));
    assertEquals(false, instance.concurrentWith(getVersion("b2")));
    assertEquals(false, instance.concurrentWith(getVersion("b3")));
    assertEquals(false, instance.concurrentWith(getVersion("b4")));
    assertEquals(false, instance.concurrentWith(getVersion("b5")));
    assertEquals(false, instance.concurrentWith(getVersion("c0")));
    assertEquals(false, instance.concurrentWith(getVersion("c1")));
    assertEquals(false, instance.concurrentWith(getVersion("c2")));
    assertEquals(false, instance.concurrentWith(getVersion("c3")));
    assertEquals(false, instance.concurrentWith(getVersion("c4")));
    assertEquals(false, instance.concurrentWith(getVersion("c5")));
  }

  /**
   * Test concurrentWith against a example vector c5.
   */
  @Test
  public void testConcurrentWith_examples_c5() {
    VersionVector<K, Integer> instance = getVersion("c5");

    assertEquals(false, instance.concurrentWith(getVersion("a0")));
    assertEquals(false, instance.concurrentWith(getVersion("a1")));
    assertEquals(false, instance.concurrentWith(getVersion("a2")));
    assertEquals(true, instance.concurrentWith(getVersion("a3")));
    assertEquals(false, instance.concurrentWith(getVersion("a4")));
    assertEquals(false, instance.concurrentWith(getVersion("b0")));
    assertEquals(false, instance.concurrentWith(getVersion("b1")));
    assertEquals(false, instance.concurrentWith(getVersion("b2")));
    assertEquals(false, instance.concurrentWith(getVersion("b3")));
    assertEquals(false, instance.concurrentWith(getVersion("b4")));
    assertEquals(false, instance.concurrentWith(getVersion("b5")));
    assertEquals(false, instance.concurrentWith(getVersion("c0")));
    assertEquals(false, instance.concurrentWith(getVersion("c1")));
    assertEquals(false, instance.concurrentWith(getVersion("c2")));
    assertEquals(false, instance.concurrentWith(getVersion("c3")));
    assertEquals(false, instance.concurrentWith(getVersion("c4")));
    assertEquals(false, instance.concurrentWith(getVersion("c5")));
  }

  /**
   * Test compareTo against a example vector a0.
   */
  @Test
  public void testCompareTo_examples_a0() {
    VersionVector<K, Integer> instance = getVersion("a0");

    assertEquals(0, instance.compareTo(getVersion("a0")));
    assertEquals(-3, instance.compareTo(getVersion("a1")));
    assertEquals(-3, instance.compareTo(getVersion("a2")));
    assertEquals(-3, instance.compareTo(getVersion("a3")));
    assertEquals(-3, instance.compareTo(getVersion("a4")));
    assertEquals(0, instance.compareTo(getVersion("b0")));
    assertEquals(-2, instance.compareTo(getVersion("b1")));
    assertEquals(-2, instance.compareTo(getVersion("b2")));
    assertEquals(-2, instance.compareTo(getVersion("b3")));
    assertEquals(-3, instance.compareTo(getVersion("b4")));
    assertEquals(-3, instance.compareTo(getVersion("b5")));
    assertEquals(-0, instance.compareTo(getVersion("c0")));
    assertEquals(-1, instance.compareTo(getVersion("c1")));
    assertEquals(-2, instance.compareTo(getVersion("c2")));
    assertEquals(-2, instance.compareTo(getVersion("c3")));
    assertEquals(-3, instance.compareTo(getVersion("c4")));
    assertEquals(-3, instance.compareTo(getVersion("c5")));
  }

  /**
   * Test compareTo against a example vector a1.
   */
  @Test
  public void testCompareTo_examples_a1() {
    VersionVector<K, Integer> instance = getVersion("a1");

    assertEquals(3, instance.compareTo(getVersion("a0")));
    assertEquals(0, instance.compareTo(getVersion("a1")));
    assertEquals(-1, instance.compareTo(getVersion("a2")));
    assertEquals(-3, instance.compareTo(getVersion("a3")));
    assertEquals(-3, instance.compareTo(getVersion("a4")));
    assertEquals(3, instance.compareTo(getVersion("b0")));
    assertEquals(2, instance.compareTo(getVersion("b1")));
    assertEquals(1, instance.compareTo(getVersion("b2")));
    assertEquals(0, instance.compareTo(getVersion("b3")));
    assertEquals(-2, instance.compareTo(getVersion("b4")));
    assertEquals(-2, instance.compareTo(getVersion("b5")));
    assertEquals(3, instance.compareTo(getVersion("c0")));
    assertEquals(2, instance.compareTo(getVersion("c1")));
    assertEquals(0, instance.compareTo(getVersion("c2")));
    assertEquals(0, instance.compareTo(getVersion("c3")));
    assertEquals(-3, instance.compareTo(getVersion("c4")));
    assertEquals(-3, instance.compareTo(getVersion("c5")));
  }

  /**
   * Test compareTo against a example vector a2.
   */
  @Test
  public void testCompareTo_examples_a2() {
    VersionVector<K, Integer> instance = getVersion("a2");

    assertEquals(3, instance.compareTo(getVersion("a0")));
    assertEquals(1, instance.compareTo(getVersion("a1")));
    assertEquals(0, instance.compareTo(getVersion("a2")));
    assertEquals(-3, instance.compareTo(getVersion("a3")));
    assertEquals(-3, instance.compareTo(getVersion("a4")));
    assertEquals(3, instance.compareTo(getVersion("b0")));
    assertEquals(2, instance.compareTo(getVersion("b1")));
    assertEquals(1, instance.compareTo(getVersion("b2")));
    assertEquals(0, instance.compareTo(getVersion("b3")));
    assertEquals(-1, instance.compareTo(getVersion("b4")));
    assertEquals(-1, instance.compareTo(getVersion("b5")));
    assertEquals(3, instance.compareTo(getVersion("c0")));
    assertEquals(2, instance.compareTo(getVersion("c1")));
    assertEquals(0, instance.compareTo(getVersion("c2")));
    assertEquals(0, instance.compareTo(getVersion("c3")));
    assertEquals(-2, instance.compareTo(getVersion("c4")));
    assertEquals(-2, instance.compareTo(getVersion("c5")));
  }

  /**
   * Test compareTo against a example vector a3.
   */
  @Test
  public void testCompareTo_examples_a3() {
    VersionVector<K, Integer> instance = getVersion("a3");

    assertEquals(3, instance.compareTo(getVersion("a0")));
    assertEquals(3, instance.compareTo(getVersion("a1")));
    assertEquals(3, instance.compareTo(getVersion("a2")));
    assertEquals(0, instance.compareTo(getVersion("a3")));
    assertEquals(-3, instance.compareTo(getVersion("a4")));
    assertEquals(3, instance.compareTo(getVersion("b0")));
    assertEquals(3, instance.compareTo(getVersion("b1")));
    assertEquals(3, instance.compareTo(getVersion("b2")));
    assertEquals(2, instance.compareTo(getVersion("b3")));
    assertEquals(0, instance.compareTo(getVersion("b4")));
    assertEquals(0, instance.compareTo(getVersion("b5")));
    assertEquals(3, instance.compareTo(getVersion("c0")));
    assertEquals(3, instance.compareTo(getVersion("c1")));
    assertEquals(2, instance.compareTo(getVersion("c2")));
    assertEquals(1, instance.compareTo(getVersion("c3")));
    assertEquals(0, instance.compareTo(getVersion("c4")));
    assertEquals(0, instance.compareTo(getVersion("c5")));
  }

  /**
   * Test compareTo against a example vector a4.
   */
  @Test
  public void testCompareTo_examples_a4() {
    VersionVector<K, Integer> instance = getVersion("a4");

    assertEquals(3, instance.compareTo(getVersion("a0")));
    assertEquals(3, instance.compareTo(getVersion("a1")));
    assertEquals(3, instance.compareTo(getVersion("a2")));
    assertEquals(3, instance.compareTo(getVersion("a3")));
    assertEquals(0, instance.compareTo(getVersion("a4")));
    assertEquals(3, instance.compareTo(getVersion("b0")));
    assertEquals(3, instance.compareTo(getVersion("b1")));
    assertEquals(3, instance.compareTo(getVersion("b2")));
    assertEquals(3, instance.compareTo(getVersion("b3")));
    assertEquals(3, instance.compareTo(getVersion("b4")));
    assertEquals(2, instance.compareTo(getVersion("b5")));
    assertEquals(3, instance.compareTo(getVersion("c0")));
    assertEquals(3, instance.compareTo(getVersion("c1")));
    assertEquals(3, instance.compareTo(getVersion("c2")));
    assertEquals(3, instance.compareTo(getVersion("c3")));
    assertEquals(2, instance.compareTo(getVersion("c4")));
    assertEquals(1, instance.compareTo(getVersion("c5")));
  }

  /**
   * Test compareTo against a example vector b0.
   */
  @Test
  public void testCompareTo_examples_b0() {
    VersionVector<K, Integer> instance = getVersion("b0");

    assertEquals(0, instance.compareTo(getVersion("a0")));
    assertEquals(-3, instance.compareTo(getVersion("a1")));
    assertEquals(-3, instance.compareTo(getVersion("a2")));
    assertEquals(-3, instance.compareTo(getVersion("a3")));
    assertEquals(-3, instance.compareTo(getVersion("a4")));
    assertEquals(0, instance.compareTo(getVersion("b0")));
    assertEquals(-2, instance.compareTo(getVersion("b1")));
    assertEquals(-2, instance.compareTo(getVersion("b2")));
    assertEquals(-2, instance.compareTo(getVersion("b3")));
    assertEquals(-3, instance.compareTo(getVersion("b4")));
    assertEquals(-3, instance.compareTo(getVersion("b5")));
    assertEquals(0, instance.compareTo(getVersion("c0")));
    assertEquals(-1, instance.compareTo(getVersion("c1")));
    assertEquals(-2, instance.compareTo(getVersion("c2")));
    assertEquals(-2, instance.compareTo(getVersion("c3")));
    assertEquals(-3, instance.compareTo(getVersion("c4")));
    assertEquals(-3, instance.compareTo(getVersion("c5")));
  }

  /**
   * Test compareTo against a example vector b1.
   */
  @Test
  public void testCompareTo_examples_b1() {
    VersionVector<K, Integer> instance = getVersion("b1");

    assertEquals(2, instance.compareTo(getVersion("a0")));
    assertEquals(-2, instance.compareTo(getVersion("a1")));
    assertEquals(-2, instance.compareTo(getVersion("a2")));
    assertEquals(-3, instance.compareTo(getVersion("a3")));
    assertEquals(-3, instance.compareTo(getVersion("a4")));
    assertEquals(2, instance.compareTo(getVersion("b0")));
    assertEquals(0, instance.compareTo(getVersion("b1")));
    assertEquals(-1, instance.compareTo(getVersion("b2")));
    assertEquals(-1, instance.compareTo(getVersion("b3")));
    assertEquals(-2, instance.compareTo(getVersion("b4")));
    assertEquals(-2, instance.compareTo(getVersion("b5")));
    assertEquals(2, instance.compareTo(getVersion("c0")));
    assertEquals(1, instance.compareTo(getVersion("c1")));
    assertEquals(-2, instance.compareTo(getVersion("c2")));
    assertEquals(-2, instance.compareTo(getVersion("c3")));
    assertEquals(-3, instance.compareTo(getVersion("c4")));
    assertEquals(-3, instance.compareTo(getVersion("c5")));
  }

  /**
   * Test compareTo against a example vector b2.
   */
  @Test
  public void testCompareTo_examples_b2() {
    VersionVector<K, Integer> instance = getVersion("b2");

    assertEquals(2, instance.compareTo(getVersion("a0")));
    assertEquals(-1, instance.compareTo(getVersion("a1")));
    assertEquals(-1, instance.compareTo(getVersion("a2")));
    assertEquals(-3, instance.compareTo(getVersion("a3")));
    assertEquals(-3, instance.compareTo(getVersion("a4")));
    assertEquals(2, instance.compareTo(getVersion("b0")));
    assertEquals(1, instance.compareTo(getVersion("b1")));
    assertEquals(0, instance.compareTo(getVersion("b2")));
    assertEquals(-1, instance.compareTo(getVersion("b3")));
    assertEquals(-2, instance.compareTo(getVersion("b4")));
    assertEquals(-2, instance.compareTo(getVersion("b5")));
    assertEquals(2, instance.compareTo(getVersion("c0")));
    assertEquals(1, instance.compareTo(getVersion("c1")));
    assertEquals(-2, instance.compareTo(getVersion("c2")));
    assertEquals(-2, instance.compareTo(getVersion("c3")));
    assertEquals(-3, instance.compareTo(getVersion("c4")));
    assertEquals(-3, instance.compareTo(getVersion("c5")));
  }

  /**
   * Test compareTo against a example vector b3.
   */
  @Test
  public void testCompareTo_examples_b3() {
    VersionVector<K, Integer> instance = getVersion("b3");

    assertEquals(2, instance.compareTo(getVersion("a0")));
    assertEquals(0, instance.compareTo(getVersion("a1")));
    assertEquals(0, instance.compareTo(getVersion("a2")));
    assertEquals(-2, instance.compareTo(getVersion("a3")));
    assertEquals(-3, instance.compareTo(getVersion("a4")));
    assertEquals(2, instance.compareTo(getVersion("b0")));
    assertEquals(1, instance.compareTo(getVersion("b1")));
    assertEquals(1, instance.compareTo(getVersion("b2")));
    assertEquals(0, instance.compareTo(getVersion("b3")));
    assertEquals(-2, instance.compareTo(getVersion("b4")));
    assertEquals(-2, instance.compareTo(getVersion("b5")));
    assertEquals(2, instance.compareTo(getVersion("c0")));
    assertEquals(1, instance.compareTo(getVersion("c1")));
    assertEquals(-1, instance.compareTo(getVersion("c2")));
    assertEquals(-1, instance.compareTo(getVersion("c3")));
    assertEquals(-3, instance.compareTo(getVersion("c4")));
    assertEquals(-3, instance.compareTo(getVersion("c5")));
  }

  /**
   * Test compareTo against a example vector b4.
   */
  @Test
  public void testCompareTo_examples_b4() {
    VersionVector<K, Integer> instance = getVersion("b4");

    assertEquals(3, instance.compareTo(getVersion("a0")));
    assertEquals(2, instance.compareTo(getVersion("a1")));
    assertEquals(1, instance.compareTo(getVersion("a2")));
    assertEquals(0, instance.compareTo(getVersion("a3")));
    assertEquals(-3, instance.compareTo(getVersion("a4")));
    assertEquals(3, instance.compareTo(getVersion("b0")));
    assertEquals(2, instance.compareTo(getVersion("b1")));
    assertEquals(2, instance.compareTo(getVersion("b2")));
    assertEquals(2, instance.compareTo(getVersion("b3")));
    assertEquals(0, instance.compareTo(getVersion("b4")));
    assertEquals(-1, instance.compareTo(getVersion("b5")));
    assertEquals(3, instance.compareTo(getVersion("c0")));
    assertEquals(2, instance.compareTo(getVersion("c1")));
    assertEquals(0, instance.compareTo(getVersion("c2")));
    assertEquals(0, instance.compareTo(getVersion("c3")));
    assertEquals(-2, instance.compareTo(getVersion("c4")));
    assertEquals(-2, instance.compareTo(getVersion("c5")));
  }

  /**
   * Test compareTo against a example vector b5.
   */
  @Test
  public void testCompareTo_examples_b5() {
    VersionVector<K, Integer> instance = getVersion("b5");

    assertEquals(3, instance.compareTo(getVersion("a0")));
    assertEquals(2, instance.compareTo(getVersion("a1")));
    assertEquals(1, instance.compareTo(getVersion("a2")));
    assertEquals(0, instance.compareTo(getVersion("a3")));
    assertEquals(-2, instance.compareTo(getVersion("a4")));
    assertEquals(3, instance.compareTo(getVersion("b0")));
    assertEquals(2, instance.compareTo(getVersion("b1")));
    assertEquals(2, instance.compareTo(getVersion("b2")));
    assertEquals(2, instance.compareTo(getVersion("b3")));
    assertEquals(1, instance.compareTo(getVersion("b4")));
    assertEquals(0, instance.compareTo(getVersion("b5")));
    assertEquals(3, instance.compareTo(getVersion("c0")));
    assertEquals(2, instance.compareTo(getVersion("c1")));
    assertEquals(0, instance.compareTo(getVersion("c2")));
    assertEquals(0, instance.compareTo(getVersion("c3")));
    assertEquals(-1, instance.compareTo(getVersion("c4")));
    assertEquals(-1, instance.compareTo(getVersion("c5")));
  }

  /**
   * Test compareTo against a example vector c0.
   */
  @Test
  public void testCompareTo_examples_c0() {
    VersionVector<K, Integer> instance = getVersion("c0");

    assertEquals(0, instance.compareTo(getVersion("a0")));
    assertEquals(-3, instance.compareTo(getVersion("a1")));
    assertEquals(-3, instance.compareTo(getVersion("a2")));
    assertEquals(-3, instance.compareTo(getVersion("a3")));
    assertEquals(-3, instance.compareTo(getVersion("a4")));
    assertEquals(0, instance.compareTo(getVersion("b0")));
    assertEquals(-2, instance.compareTo(getVersion("b1")));
    assertEquals(-2, instance.compareTo(getVersion("b2")));
    assertEquals(-2, instance.compareTo(getVersion("b3")));
    assertEquals(-3, instance.compareTo(getVersion("b4")));
    assertEquals(-3, instance.compareTo(getVersion("b5")));
    assertEquals(0, instance.compareTo(getVersion("c0")));
    assertEquals(-1, instance.compareTo(getVersion("c1")));
    assertEquals(-2, instance.compareTo(getVersion("c2")));
    assertEquals(-2, instance.compareTo(getVersion("c3")));
    assertEquals(-3, instance.compareTo(getVersion("c4")));
    assertEquals(-3, instance.compareTo(getVersion("c5")));
  }

  /**
   * Test compareTo against a example vector c1.
   */
  @Test
  public void testCompareTo_examples_c1() {
    VersionVector<K, Integer> instance = getVersion("c1");

    assertEquals(1, instance.compareTo(getVersion("a0")));
    assertEquals(-2, instance.compareTo(getVersion("a1")));
    assertEquals(-2, instance.compareTo(getVersion("a2")));
    assertEquals(-3, instance.compareTo(getVersion("a3")));
    assertEquals(-3, instance.compareTo(getVersion("a4")));
    assertEquals(1, instance.compareTo(getVersion("b0")));
    assertEquals(-1, instance.compareTo(getVersion("b1")));
    assertEquals(-1, instance.compareTo(getVersion("b2")));
    assertEquals(-1, instance.compareTo(getVersion("b3")));
    assertEquals(-2, instance.compareTo(getVersion("b4")));
    assertEquals(-2, instance.compareTo(getVersion("b5")));
    assertEquals(1, instance.compareTo(getVersion("c0")));
    assertEquals(0, instance.compareTo(getVersion("c1")));
    assertEquals(-2, instance.compareTo(getVersion("c2")));
    assertEquals(-2, instance.compareTo(getVersion("c3")));
    assertEquals(-3, instance.compareTo(getVersion("c4")));
    assertEquals(-3, instance.compareTo(getVersion("c5")));
  }

  /**
   * Test compareTo against a example vector c2.
   */
  @Test
  public void testCompareTo_examples_c2() {
    VersionVector<K, Integer> instance = getVersion("c2");

    assertEquals(2, instance.compareTo(getVersion("a0")));
    assertEquals(0, instance.compareTo(getVersion("a1")));
    assertEquals(0, instance.compareTo(getVersion("a2")));
    assertEquals(-2, instance.compareTo(getVersion("a3")));
    assertEquals(-3, instance.compareTo(getVersion("a4")));
    assertEquals(2, instance.compareTo(getVersion("b0")));
    assertEquals(2, instance.compareTo(getVersion("b1")));
    assertEquals(2, instance.compareTo(getVersion("b2")));
    assertEquals(1, instance.compareTo(getVersion("b3")));
    assertEquals(0, instance.compareTo(getVersion("b4")));
    assertEquals(0, instance.compareTo(getVersion("b5")));
    assertEquals(2, instance.compareTo(getVersion("c0")));
    assertEquals(2, instance.compareTo(getVersion("c1")));
    assertEquals(0, instance.compareTo(getVersion("c2")));
    assertEquals(-1, instance.compareTo(getVersion("c3")));
    assertEquals(-3, instance.compareTo(getVersion("c4")));
    assertEquals(-3, instance.compareTo(getVersion("c5")));
  }

  /**
   * Test compareTo against a example vector c3.
   */
  @Test
  public void testCompareTo_examples_c3() {
    VersionVector<K, Integer> instance = getVersion("c3");

    assertEquals(2, instance.compareTo(getVersion("a0")));
    assertEquals(0, instance.compareTo(getVersion("a1")));
    assertEquals(0, instance.compareTo(getVersion("a2")));
    assertEquals(-1, instance.compareTo(getVersion("a3")));
    assertEquals(-3, instance.compareTo(getVersion("a4")));
    assertEquals(2, instance.compareTo(getVersion("b0")));
    assertEquals(2, instance.compareTo(getVersion("b1")));
    assertEquals(2, instance.compareTo(getVersion("b2")));
    assertEquals(1, instance.compareTo(getVersion("b3")));
    assertEquals(0, instance.compareTo(getVersion("b4")));
    assertEquals(0, instance.compareTo(getVersion("b5")));
    assertEquals(2, instance.compareTo(getVersion("c0")));
    assertEquals(2, instance.compareTo(getVersion("c1")));
    assertEquals(1, instance.compareTo(getVersion("c2")));
    assertEquals(0, instance.compareTo(getVersion("c3")));
    assertEquals(-3, instance.compareTo(getVersion("c4")));
    assertEquals(-3, instance.compareTo(getVersion("c5")));
  }

  /**
   * Test compareTo against a example vector c4.
   */
  @Test
  public void testCompareTo_examples_c4() {
    VersionVector<K, Integer> instance = getVersion("c4");

    assertEquals(3, instance.compareTo(getVersion("a0")));
    assertEquals(3, instance.compareTo(getVersion("a1")));
    assertEquals(2, instance.compareTo(getVersion("a2")));
    assertEquals(0, instance.compareTo(getVersion("a3")));
    assertEquals(-2, instance.compareTo(getVersion("a4")));
    assertEquals(3, instance.compareTo(getVersion("b0")));
    assertEquals(3, instance.compareTo(getVersion("b1")));
    assertEquals(3, instance.compareTo(getVersion("b2")));
    assertEquals(3, instance.compareTo(getVersion("b3")));
    assertEquals(2, instance.compareTo(getVersion("b4")));
    assertEquals(1, instance.compareTo(getVersion("b5")));
    assertEquals(3, instance.compareTo(getVersion("c0")));
    assertEquals(3, instance.compareTo(getVersion("c1")));
    assertEquals(3, instance.compareTo(getVersion("c2")));
    assertEquals(3, instance.compareTo(getVersion("c3")));
    assertEquals(0, instance.compareTo(getVersion("c4")));
    assertEquals(-1, instance.compareTo(getVersion("c5")));
  }

  /**
   * Test compareTo against a example vector c5.
   */
  @Test
  public void testCompareTo_examples_c5() {
    VersionVector<K, Integer> instance = getVersion("c5");

    assertEquals(3, instance.compareTo(getVersion("a0")));
    assertEquals(3, instance.compareTo(getVersion("a1")));
    assertEquals(2, instance.compareTo(getVersion("a2")));
    assertEquals(0, instance.compareTo(getVersion("a3")));
    assertEquals(-1, instance.compareTo(getVersion("a4")));
    assertEquals(3, instance.compareTo(getVersion("b0")));
    assertEquals(3, instance.compareTo(getVersion("b1")));
    assertEquals(3, instance.compareTo(getVersion("b2")));
    assertEquals(3, instance.compareTo(getVersion("b3")));
    assertEquals(2, instance.compareTo(getVersion("b4")));
    assertEquals(1, instance.compareTo(getVersion("b5")));
    assertEquals(3, instance.compareTo(getVersion("c0")));
    assertEquals(3, instance.compareTo(getVersion("c1")));
    assertEquals(3, instance.compareTo(getVersion("c2")));
    assertEquals(3, instance.compareTo(getVersion("c3")));
    assertEquals(1, instance.compareTo(getVersion("c4")));
    assertEquals(0, instance.compareTo(getVersion("c5")));
  }
}
