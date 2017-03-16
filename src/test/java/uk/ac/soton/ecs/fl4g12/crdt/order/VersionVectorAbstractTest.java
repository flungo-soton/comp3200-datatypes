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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * Abstract test for implementations of {@linkplain VersionVector}.
 *
 * @param <K> the type of the key being used in the {@link VersionVector}s.
 * @param <V> the type of the version being tested.
 */
public abstract class VersionVectorAbstractTest<K, V extends VersionVector<K, Integer>>
    extends VersionAbstractTest<Map<K, Integer>, V> {

  /**
   * Get the {@linkplain VersionVector} from a set of examples.
   *
   * @param id the id of the version vector to get.
   * @return the {@link VersionVector} of the specified example.
   * @see #getTimestamp(java.lang.String) for details about the examples and id.
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
    V instance = getVersion(order);

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

  private void testGetIdentifiers(final int order) {
    // Get the version instance
    V instance = getVersion(order);

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
    // Setup the mock
    V instance = getVersion(order);

    // Get the timestamp which has the expected values
    Map<K, Integer> timestamp = getTimestamp(order);

    for (int i = 0; i <= 3; i++) {
      K id = getKey(i);

      // Test init on the initial version
      V initialInitted = getVersion(order);
      Map<K, Integer> expectedTimestamp = getTimestamp(order);
      if (!expectedTimestamp.containsKey(id)) {
        expectedTimestamp.put(id, 0);
      }

      initialInitted.init(id);

      assertTrue("Identifiers should contain the new identifier",
          expectedTimestamp.keySet().contains(id));
      assertEquals("Identifiers should match the expectation", expectedTimestamp.keySet(),
          initialInitted.getIdentifiers());

      assertEquals("Version timestamp should match the expectation", expectedTimestamp,
          initialInitted.get());

      // Test init cumulatively
      if (!timestamp.containsKey(id)) {
        timestamp.put(id, 0);
      }

      instance.init(id);

      assertTrue("Identifiers should contain the new identifier",
          expectedTimestamp.keySet().contains(id));
      assertEquals("Identifiers should match the expectation", timestamp.keySet(),
          instance.getIdentifiers());

      assertEquals("Version timestamp should match the expectation", timestamp, instance.get());
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
    V instance = getVersion(order);

    // Get the timestamp which has the expected values
    Map<K, Integer> timestamp = getTimestamp(order);

    // Test the method
    V previous;
    for (int i = 0; i <= 3; i++) {
      K id = getKey(i);

      if (timestamp.containsKey(id)) {
        // Increment initial
        V initialIncremented = getVersion(order);
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

  /**
   * Test happenedBefore against a example vector a0.
   */
  @Test
  public void testHappenedBefore_examples_a0() {
    V instance = getVersion("a0");

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
    V instance = getVersion("a1");

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
    V instance = getVersion("a2");

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
    V instance = getVersion("a3");

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
    V instance = getVersion("a4");

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
    V instance = getVersion("b0");

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
    V instance = getVersion("b1");

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
    V instance = getVersion("b2");

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
    V instance = getVersion("b3");

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
    V instance = getVersion("b4");

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
    V instance = getVersion("b5");

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
    V instance = getVersion("c0");

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
    V instance = getVersion("c1");

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
    V instance = getVersion("c2");

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
    V instance = getVersion("c3");

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
    V instance = getVersion("c4");

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
    V instance = getVersion("c5");

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
    V instance = getVersion("a0");

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
    V instance = getVersion("a1");

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
    V instance = getVersion("a2");

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
    V instance = getVersion("a3");

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
    V instance = getVersion("a4");

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
    V instance = getVersion("b0");

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
    V instance = getVersion("b1");

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
    V instance = getVersion("b2");

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
    V instance = getVersion("b3");

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
    V instance = getVersion("b4");

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
    V instance = getVersion("b5");

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
    V instance = getVersion("c0");

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
    V instance = getVersion("c1");

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
    V instance = getVersion("c2");

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
    V instance = getVersion("c3");

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
    V instance = getVersion("c4");

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
    V instance = getVersion("c5");

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
    V instance = getVersion("a0");

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
    V instance = getVersion("a1");

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
    V instance = getVersion("a2");

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
    V instance = getVersion("a3");

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
    V instance = getVersion("a4");

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
    V instance = getVersion("b0");

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
    V instance = getVersion("b1");

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
    V instance = getVersion("b2");

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
    V instance = getVersion("b3");

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
    V instance = getVersion("b4");

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
    V instance = getVersion("b5");

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
    V instance = getVersion("c0");

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
    V instance = getVersion("c1");

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
    V instance = getVersion("c2");

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
    V instance = getVersion("c3");

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
    V instance = getVersion("c4");

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
    V instance = getVersion("c5");

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
