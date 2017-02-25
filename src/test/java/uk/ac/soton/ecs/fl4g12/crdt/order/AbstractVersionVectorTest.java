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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

/**
 *
 */
public class AbstractVersionVectorTest {

  private static final Logger LOGGER = Logger.getLogger(AbstractVersionTest.class.getName());

  public AbstractVersionVectorTest() {}

  private HashVersionVector<Integer, Long> hashVersionVector;
  private ArrayVersionVector<Long> arrayVersionVector;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  /**
   * Create example vectors based on the WikiPedia example for vector clocks.
   * 
   * https://upload.wikimedia.org/wikipedia/commons/thumb/5/55/Vector_Clock.svg/500px-Vector_Clock.svg.png
   * 
   * @return a map of vector clocks.
   */
  private Map<String, AbstractVersionVector<String, Integer>> createExamples() {
    Map<String, AbstractVersionVector<String, Integer>> examples = new HashMap<>();

    // Vector a0
    Map a0 = new HashMap(3);
    a0.put("a", 0);
    examples.put("a0", new ImmutableMapVersionVector<>(a0, 0, false));

    // Vector a1
    Map a1 = new HashMap(3);
    a1.put("a", 1);
    a1.put("b", 2);
    a1.put("c", 1);
    examples.put("a1", new ImmutableMapVersionVector<>(a1, 0, false));

    // Vector a2
    Map a2 = new HashMap(3);
    a2.put("a", 2);
    a2.put("b", 2);
    a2.put("c", 1);
    examples.put("a2", new ImmutableMapVersionVector<>(a2, 0, false));

    // Vector a3
    Map a3 = new HashMap(3);
    a3.put("a", 3);
    a3.put("b", 3);
    a3.put("c", 3);
    examples.put("a3", new ImmutableMapVersionVector<>(a3, 0, false));

    // Vector a4
    Map a4 = new HashMap(3);
    a4.put("a", 4);
    a4.put("b", 5);
    a4.put("c", 5);
    examples.put("a4", new ImmutableMapVersionVector<>(a4, 0, false));

    // Vector b0
    Map b0 = new HashMap(3);
    b0.put("b", 0);
    examples.put("b0", new ImmutableMapVersionVector<>(b0, 0, false));

    // Vector b1
    Map b1 = new HashMap(3);
    b1.put("b", 1);
    b1.put("c", 1);
    examples.put("b1", new ImmutableMapVersionVector<>(b1, 0, false));

    // Vector b2
    Map b2 = new HashMap(3);
    b2.put("b", 2);
    b2.put("c", 1);
    examples.put("b2", new ImmutableMapVersionVector<>(b2, 0, false));

    // Vector b3
    Map b3 = new HashMap(3);
    b3.put("b", 3);
    b3.put("c", 1);
    examples.put("b3", new ImmutableMapVersionVector<>(b3, 0, false));

    // Vector b4
    Map b4 = new HashMap(3);
    b4.put("a", 2);
    b4.put("b", 4);
    b4.put("c", 1);
    examples.put("b4", new ImmutableMapVersionVector<>(b4, 0, false));


    // Vector b5
    Map b5 = new HashMap(3);
    b5.put("a", 2);
    b5.put("b", 5);
    b5.put("c", 1);
    examples.put("b5", new ImmutableMapVersionVector<>(b5, 0, false));

    // Vector c0
    Map c0 = new HashMap(3);
    c0.put("c", 0);
    examples.put("c0", new ImmutableMapVersionVector<>(c0, 0, false));

    // Vector c1
    Map c1 = new HashMap(3);
    c1.put("c", 1);
    examples.put("c1", new ImmutableMapVersionVector<>(c1, 0, false));

    // Vector c2
    Map c2 = new HashMap(3);
    c2.put("b", 3);
    c2.put("c", 2);
    examples.put("c2", new ImmutableMapVersionVector<>(c2, 0, false));

    // Vector c3
    Map c3 = new HashMap(3);
    c3.put("b", 3);
    c3.put("c", 3);
    examples.put("c3", new ImmutableMapVersionVector<>(c3, 0, false));

    // Vector c4
    Map c4 = new HashMap(3);
    c4.put("a", 2);
    c4.put("b", 5);
    c4.put("c", 4);
    examples.put("c4", new ImmutableMapVersionVector<>(c4, 0, false));

    // Vector c5
    Map c5 = new HashMap(3);
    c5.put("a", 2);
    c5.put("b", 5);
    c5.put("c", 5);
    examples.put("c5", new ImmutableMapVersionVector<>(c5, 0, false));

    // return the examples
    return examples;
  }

  /**
   * Test that get uses the getInternal method.
   */
  @Test
  public void testGet_GenericType() {
    LOGGER.log(Level.INFO, "testGet_GenericType: Test that get uses the getInternal method.");

    // Constants for the test
    final Object id = new Object();
    final Integer value = 1;

    // Setup the mock
    AbstractVersionVector instance =
        Mockito.mock(TestVersionVector.class, Mockito.CALLS_REAL_METHODS);
    LogicalVersion logicalVersion = Mockito.mock(LogicalVersion.class);
    Mockito.doReturn(logicalVersion).when(instance).getInternal(id);
    Mockito.doReturn(value).when(logicalVersion).get();

    // Test the get method
    Object result = instance.get(id);
    Mockito.verify(instance).getInternal(id);
    Mockito.verify(logicalVersion).get();
    assertEquals("The value returned should be the expected value.", value, result);
  }

  /**
   * Test that get with no-args returns a constructed map.
   */
  @Test
  public void testGet_0args() {
    LOGGER.log(Level.INFO,
        "testGet_GenericType: Test that get with no-args returns a constructed map.");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value1 = 1;
    final Integer value2 = 2;
    final Integer value3 = 3;

    // Setup the expected response
    Map expResult = new HashMap(3);
    expResult.put(id1, value1);
    expResult.put(id2, value2);
    expResult.put(id3, value3);

    // Setup the mock
    AbstractVersionVector instance =
        Mockito.mock(TestVersionVector.class, Mockito.CALLS_REAL_METHODS);
    LogicalVersion logicalVersion1 = Mockito.mock(LogicalVersion.class);
    LogicalVersion logicalVersion2 = Mockito.mock(LogicalVersion.class);
    LogicalVersion logicalVersion3 = Mockito.mock(LogicalVersion.class);
    Mockito.doReturn(logicalVersion1).when(instance).getInternal(id1);
    Mockito.doReturn(logicalVersion2).when(instance).getInternal(id2);
    Mockito.doReturn(logicalVersion3).when(instance).getInternal(id3);
    Mockito.doReturn(value1).when(logicalVersion1).get();
    Mockito.doReturn(value2).when(logicalVersion2).get();
    Mockito.doReturn(value3).when(logicalVersion3).get();
    Mockito.doReturn(expResult.keySet()).when(instance).getIdentifiers();

    // Get the response and makr assertions
    Map result = instance.get();
    assertEquals("The map should be constructed", expResult, result);
  }

  /**
   * Test increment for an uninitialised id in the vector.
   */
  @Test
  public void testIncrement_Uninitialised() {
    LOGGER.log(Level.INFO,
        "testIncrement_Uninitialised: Test increment for an uninitialised id in the vector");

    // Setup the mock
    AbstractVersionVector instance =
        Mockito.mock(TestVersionVector.class, Mockito.CALLS_REAL_METHODS);
    Mockito.doReturn(new HashSet(0)).when(instance).getIdentifiers();

    // Test the method
    thrown.expect(IllegalArgumentException.class);
    instance.increment(1);
  }

  /**
   * Test increment of a valid id in the vector.
   */
  @Test
  public void testIncrement() {
    LOGGER.log(Level.INFO, "testIncrement: Test increment of a valid id in the vector");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value2 = 2;

    // Setup the mock
    AbstractVersionVector instance =
        Mockito.mock(TestVersionVector.class, Mockito.CALLS_REAL_METHODS);
    LogicalVersion logicalVersion1 = Mockito.mock(LogicalVersion.class);
    LogicalVersion logicalVersion2 = Mockito.mock(LogicalVersion.class);
    LogicalVersion logicalVersion3 = Mockito.mock(LogicalVersion.class);
    Mockito.doReturn(logicalVersion1).when(instance).getInternal(id1);
    Mockito.doReturn(logicalVersion2).when(instance).getInternal(id2);
    Mockito.doReturn(logicalVersion3).when(instance).getInternal(id3);
    Mockito.doReturn(value2).when(logicalVersion2).get();
    Mockito.doReturn(new HashSet(Arrays.asList(new Object[] {id1, id2, id3}))).when(instance)
        .getIdentifiers();

    // Increment
    instance.increment(id2);

    // Verify the interactions
    Mockito.verify(logicalVersion2).increment();
    Mockito.verifyZeroInteractions(logicalVersion1);
    Mockito.verifyZeroInteractions(logicalVersion3);
  }

  /**
   * Test sync with a given vector. All components of the vector should be synced with the local
   * vector.
   */
  @Test
  public void testSync() {
    LOGGER.log(Level.INFO, "testSync: Test sync with a given vector.");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value1 = 1;
    final Integer value2 = 2;
    final Integer value3 = 3;

    // Setup the expected response
    Map expResult = new HashMap(3);
    expResult.put(id1, value1);
    expResult.put(id2, value2);
    expResult.put(id3, value3);

    // Setup the mock
    AbstractVersionVector instance =
        Mockito.mock(TestVersionVector.class, Mockito.CALLS_REAL_METHODS);
    Mockito.doNothing().when(instance).sync(Mockito.any(), Mockito.any(Integer.class));

    // Do the sync
    instance.sync(expResult);

    // Verify that sync was called on each element
    Mockito.verify(instance).sync(id1, value1);
    Mockito.verify(instance).sync(id2, value2);
    Mockito.verify(instance).sync(id3, value3);
    // If only three calls were made then the 3 calls are exactly the ones we tested for
    Mockito.verify(instance, Mockito.times(3)).sync(Mockito.any(), Mockito.any(Integer.class));
  }

  /**
   * Test happenedBefore with equal vectors.
   */
  @Test
  public void testHappenedBefore_Equal() {
    LOGGER.log(Level.INFO, "testHappenedBefore_Equal: Test happenedBefore with equal vectors");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value1 = 1;
    final Integer value2 = 2;
    final Integer value3 = 3;

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, value1);
    vector1.put(id2, value2);
    vector1.put(id3, value3);
    Map vector2 = new HashMap(3);
    vector2.put(id1, value1);
    vector2.put(id2, value2);
    vector2.put(id3, value3);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    boolean expResult = false;
    boolean result1 = abstractVersionVector1.happenedBefore(abstractVersionVector2);
    boolean result2 = abstractVersionVector2.happenedBefore(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(expResult, result2);
  }

  /**
   * Test happenedBefore with concurrent vectors.
   */
  @Test
  public void testHappenedBefore_Concurrent() {
    LOGGER.log(Level.INFO,
        "testHappenedBefore_Concurrent: Test happenedBefore with concurrent vectors");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value1 = 1;
    final Integer value2 = 2;
    final Integer value3 = 3;

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, value1);
    vector1.put(id2, value2);
    vector1.put(id3, value3 + 1);
    Map vector2 = new HashMap(3);
    vector2.put(id1, value1 + 1);
    vector2.put(id2, value2);
    vector2.put(id3, value3);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    boolean expResult = false;
    boolean result1 = abstractVersionVector1.happenedBefore(abstractVersionVector2);
    boolean result2 = abstractVersionVector2.happenedBefore(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(expResult, result2);
  }

  /**
   * Test happenedBefore with a sequential vector.
   */
  @Test
  public void testHappenedBefore_SequentialSingle() {
    LOGGER.log(Level.INFO,
        "testHappenedBefore_SequentialSingle: Test happenedBefore with a sequential vector");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value1 = 1;
    final Integer value2 = 2;
    final Integer value3 = 3;

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, value1);
    vector1.put(id2, value2);
    vector1.put(id3, value3);
    Map vector2 = new HashMap(3);
    vector2.put(id1, value1);
    vector2.put(id2, value2 + 1);
    vector2.put(id3, value3);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    boolean expResult = true;
    boolean result1 = abstractVersionVector1.happenedBefore(abstractVersionVector2);
    boolean result2 = abstractVersionVector2.happenedBefore(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(!expResult, result2);
  }

  /**
   * Test happenedBefore with a sequential vector that's been incremented twice.
   */
  @Test
  public void testHappenedBefore_SequentialDouble() {
    LOGGER.log(Level.INFO, "testHappenedBefore_SequentialDouble: "
        + "Test happenedBefore with a sequential vector that's been incremented twice");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value1 = 1;
    final Integer value2 = 2;
    final Integer value3 = 3;

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, value1);
    vector1.put(id2, value2);
    vector1.put(id3, value3);
    Map vector2 = new HashMap(3);
    vector2.put(id1, value1 + 1);
    vector2.put(id2, value2);
    vector2.put(id3, value3 + 1);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    boolean expResult = true;
    boolean result1 = abstractVersionVector1.happenedBefore(abstractVersionVector2);
    boolean result2 = abstractVersionVector2.happenedBefore(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(!expResult, result2);
  }

  /**
   * Test happenedBefore with a sequential vector that's been incremented thrice.
   */
  @Test
  public void testHappenedBefore_SequentialTripple() {
    LOGGER.log(Level.INFO, "testHappenedBefore_SequentialTripple: "
        + "Test happenedBefore with a sequential vector that's been incremented thrice");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value1 = 1;
    final Integer value2 = 2;
    final Integer value3 = 3;

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, value1);
    vector1.put(id2, value2);
    vector1.put(id3, value3);
    Map vector2 = new HashMap(3);
    vector2.put(id1, value1 + 1);
    vector2.put(id2, value2 + 1);
    vector2.put(id3, value3 + 1);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    boolean expResult = true;
    boolean result1 = abstractVersionVector1.happenedBefore(abstractVersionVector2);
    boolean result2 = abstractVersionVector2.happenedBefore(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(!expResult, result2);
  }

  /**
   * Test of happenedBefore method, of class AbstractVersionVector.
   */
  @Test
  public void testHappenedBefore_LargeIncrement() {
    LOGGER.log(Level.INFO, "testHappenedBefore_LargeIncrement: "
        + "Test happenedBefore with a sequential vector that's been incremented a lot");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value1 = 1;
    final Integer value2 = 2;
    final Integer value3 = 3;

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, value1);
    vector1.put(id2, value2);
    vector1.put(id3, value3);
    Map vector2 = new HashMap(3);
    vector2.put(id1, value1 + 7);
    vector2.put(id2, value2 + 5);
    vector2.put(id3, value3 + 3);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    boolean expResult = true;
    boolean result1 = abstractVersionVector1.happenedBefore(abstractVersionVector2);
    boolean result2 = abstractVersionVector2.happenedBefore(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(!expResult, result2);
  }

  /**
   * Test happenedBefore with a equal vectors (one with an implicit zero value).
   */
  @Test
  public void testHappenedBefore_ImplicitZeroEqual() {
    LOGGER.log(Level.INFO, "testHappenedBefore_ImplicitZeroEqual: "
        + "Test happenedBefore with a equal vectors (one with an implicit zero value)");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, 0);
    vector1.put(id2, 1);
    vector1.put(id3, 2);
    Map vector2 = new HashMap(3);
    vector2.put(id2, 1);
    vector2.put(id3, 2);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    boolean expResult = false;
    boolean result1 = abstractVersionVector1.happenedBefore(abstractVersionVector2);
    boolean result2 = abstractVersionVector2.happenedBefore(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(expResult, result2);
  }

  /**
   * Test happenedBefore with a sequential vector (with an implicit zero value).
   */
  @Test
  public void testHappenedBefore_ImplicitZeroSequential() {
    LOGGER.log(Level.INFO, "testHappenedBefore_ImplicitZeroSequential: "
        + "Test happenedBefore with a sequential vector (with an implicit zero value)");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id2, 2);
    vector1.put(id3, 3);
    Map vector2 = new HashMap(3);
    vector2.put(id1, 1);
    vector2.put(id2, 2);
    vector2.put(id3, 3);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    boolean expResult = true;
    boolean result1 = abstractVersionVector1.happenedBefore(abstractVersionVector2);
    boolean result2 = abstractVersionVector2.happenedBefore(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(!expResult, result2);
  }

  /**
   * Test happenedBefore against a example vector a0.
   */
  @Test
  public void testHappenedBefore_examples_a0() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("a0");

    assertEquals(false, instance.happenedBefore(examples.get("a0")));
    assertEquals(true, instance.happenedBefore(examples.get("a1")));
    assertEquals(true, instance.happenedBefore(examples.get("a2")));
    assertEquals(true, instance.happenedBefore(examples.get("a3")));
    assertEquals(true, instance.happenedBefore(examples.get("a4")));
    assertEquals(false, instance.happenedBefore(examples.get("b0")));
    assertEquals(false, instance.happenedBefore(examples.get("b1")));
    assertEquals(false, instance.happenedBefore(examples.get("b2")));
    assertEquals(false, instance.happenedBefore(examples.get("b3")));
    assertEquals(true, instance.happenedBefore(examples.get("b4")));
    assertEquals(true, instance.happenedBefore(examples.get("b5")));
    assertEquals(false, instance.happenedBefore(examples.get("c0")));
    assertEquals(false, instance.happenedBefore(examples.get("c1")));
    assertEquals(false, instance.happenedBefore(examples.get("c2")));
    assertEquals(false, instance.happenedBefore(examples.get("c3")));
    assertEquals(true, instance.happenedBefore(examples.get("c4")));
    assertEquals(true, instance.happenedBefore(examples.get("c5")));
  }

  /**
   * Test happenedBefore against a example vector a1.
   */
  @Test
  public void testHappenedBefore_examples_a1() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("a1");

    assertEquals(false, instance.happenedBefore(examples.get("a0")));
    assertEquals(false, instance.happenedBefore(examples.get("a1")));
    assertEquals(true, instance.happenedBefore(examples.get("a2")));
    assertEquals(true, instance.happenedBefore(examples.get("a3")));
    assertEquals(true, instance.happenedBefore(examples.get("a4")));
    assertEquals(false, instance.happenedBefore(examples.get("b0")));
    assertEquals(false, instance.happenedBefore(examples.get("b1")));
    assertEquals(false, instance.happenedBefore(examples.get("b2")));
    assertEquals(false, instance.happenedBefore(examples.get("b3")));
    assertEquals(true, instance.happenedBefore(examples.get("b4")));
    assertEquals(true, instance.happenedBefore(examples.get("b5")));
    assertEquals(false, instance.happenedBefore(examples.get("c0")));
    assertEquals(false, instance.happenedBefore(examples.get("c1")));
    assertEquals(false, instance.happenedBefore(examples.get("c2")));
    assertEquals(false, instance.happenedBefore(examples.get("c3")));
    assertEquals(true, instance.happenedBefore(examples.get("c4")));
    assertEquals(true, instance.happenedBefore(examples.get("c5")));
  }

  /**
   * Test happenedBefore against a example vector a2.
   */
  @Test
  public void testHappenedBefore_examples_a2() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("a2");

    assertEquals(false, instance.happenedBefore(examples.get("a0")));
    assertEquals(false, instance.happenedBefore(examples.get("a1")));
    assertEquals(false, instance.happenedBefore(examples.get("a2")));
    assertEquals(true, instance.happenedBefore(examples.get("a3")));
    assertEquals(true, instance.happenedBefore(examples.get("a4")));
    assertEquals(false, instance.happenedBefore(examples.get("b0")));
    assertEquals(false, instance.happenedBefore(examples.get("b1")));
    assertEquals(false, instance.happenedBefore(examples.get("b2")));
    assertEquals(false, instance.happenedBefore(examples.get("b3")));
    assertEquals(true, instance.happenedBefore(examples.get("b4")));
    assertEquals(true, instance.happenedBefore(examples.get("b5")));
    assertEquals(false, instance.happenedBefore(examples.get("c0")));
    assertEquals(false, instance.happenedBefore(examples.get("c1")));
    assertEquals(false, instance.happenedBefore(examples.get("c2")));
    assertEquals(false, instance.happenedBefore(examples.get("c3")));
    assertEquals(true, instance.happenedBefore(examples.get("c4")));
    assertEquals(true, instance.happenedBefore(examples.get("c5")));
  }

  /**
   * Test happenedBefore against a example vector a3.
   */
  @Test
  public void testHappenedBefore_examples_a3() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("a3");

    assertEquals(false, instance.happenedBefore(examples.get("a0")));
    assertEquals(false, instance.happenedBefore(examples.get("a1")));
    assertEquals(false, instance.happenedBefore(examples.get("a2")));
    assertEquals(false, instance.happenedBefore(examples.get("a3")));
    assertEquals(true, instance.happenedBefore(examples.get("a4")));
    assertEquals(false, instance.happenedBefore(examples.get("b0")));
    assertEquals(false, instance.happenedBefore(examples.get("b1")));
    assertEquals(false, instance.happenedBefore(examples.get("b2")));
    assertEquals(false, instance.happenedBefore(examples.get("b3")));
    assertEquals(false, instance.happenedBefore(examples.get("b4")));
    assertEquals(false, instance.happenedBefore(examples.get("b5")));
    assertEquals(false, instance.happenedBefore(examples.get("c0")));
    assertEquals(false, instance.happenedBefore(examples.get("c1")));
    assertEquals(false, instance.happenedBefore(examples.get("c2")));
    assertEquals(false, instance.happenedBefore(examples.get("c3")));
    assertEquals(false, instance.happenedBefore(examples.get("c4")));
    assertEquals(false, instance.happenedBefore(examples.get("c5")));
  }

  /**
   * Test happenedBefore against a example vector a4.
   */
  @Test
  public void testHappenedBefore_examples_a4() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("a4");

    assertEquals(false, instance.happenedBefore(examples.get("a0")));
    assertEquals(false, instance.happenedBefore(examples.get("a1")));
    assertEquals(false, instance.happenedBefore(examples.get("a2")));
    assertEquals(false, instance.happenedBefore(examples.get("a3")));
    assertEquals(false, instance.happenedBefore(examples.get("a4")));
    assertEquals(false, instance.happenedBefore(examples.get("b0")));
    assertEquals(false, instance.happenedBefore(examples.get("b1")));
    assertEquals(false, instance.happenedBefore(examples.get("b2")));
    assertEquals(false, instance.happenedBefore(examples.get("b3")));
    assertEquals(false, instance.happenedBefore(examples.get("b4")));
    assertEquals(false, instance.happenedBefore(examples.get("b5")));
    assertEquals(false, instance.happenedBefore(examples.get("c0")));
    assertEquals(false, instance.happenedBefore(examples.get("c1")));
    assertEquals(false, instance.happenedBefore(examples.get("c2")));
    assertEquals(false, instance.happenedBefore(examples.get("c3")));
    assertEquals(false, instance.happenedBefore(examples.get("c4")));
    assertEquals(false, instance.happenedBefore(examples.get("c5")));
  }

  /**
   * Test happenedBefore against a example vector b0.
   */
  @Test
  public void testHappenedBefore_examples_b0() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("b0");

    assertEquals(false, instance.happenedBefore(examples.get("a0")));
    assertEquals(true, instance.happenedBefore(examples.get("a1")));
    assertEquals(true, instance.happenedBefore(examples.get("a2")));
    assertEquals(true, instance.happenedBefore(examples.get("a3")));
    assertEquals(true, instance.happenedBefore(examples.get("a4")));
    assertEquals(false, instance.happenedBefore(examples.get("b0")));
    assertEquals(true, instance.happenedBefore(examples.get("b1")));
    assertEquals(true, instance.happenedBefore(examples.get("b2")));
    assertEquals(true, instance.happenedBefore(examples.get("b3")));
    assertEquals(true, instance.happenedBefore(examples.get("b4")));
    assertEquals(true, instance.happenedBefore(examples.get("b5")));
    assertEquals(false, instance.happenedBefore(examples.get("c0")));
    assertEquals(false, instance.happenedBefore(examples.get("c1")));
    assertEquals(true, instance.happenedBefore(examples.get("c2")));
    assertEquals(true, instance.happenedBefore(examples.get("c3")));
    assertEquals(true, instance.happenedBefore(examples.get("c4")));
    assertEquals(true, instance.happenedBefore(examples.get("c5")));
  }

  /**
   * Test happenedBefore against a example vector b1.
   */
  @Test
  public void testHappenedBefore_examples_b1() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("b1");

    assertEquals(false, instance.happenedBefore(examples.get("a0")));
    assertEquals(true, instance.happenedBefore(examples.get("a1")));
    assertEquals(true, instance.happenedBefore(examples.get("a2")));
    assertEquals(true, instance.happenedBefore(examples.get("a3")));
    assertEquals(true, instance.happenedBefore(examples.get("a4")));
    assertEquals(false, instance.happenedBefore(examples.get("b0")));
    assertEquals(false, instance.happenedBefore(examples.get("b1")));
    assertEquals(true, instance.happenedBefore(examples.get("b2")));
    assertEquals(true, instance.happenedBefore(examples.get("b3")));
    assertEquals(true, instance.happenedBefore(examples.get("b4")));
    assertEquals(true, instance.happenedBefore(examples.get("b5")));
    assertEquals(false, instance.happenedBefore(examples.get("c0")));
    assertEquals(false, instance.happenedBefore(examples.get("c1")));
    assertEquals(true, instance.happenedBefore(examples.get("c2")));
    assertEquals(true, instance.happenedBefore(examples.get("c3")));
    assertEquals(true, instance.happenedBefore(examples.get("c4")));
    assertEquals(true, instance.happenedBefore(examples.get("c5")));
  }

  /**
   * Test happenedBefore against a example vector b2.
   */
  @Test
  public void testHappenedBefore_examples_b2() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("b2");

    assertEquals(false, instance.happenedBefore(examples.get("a0")));
    assertEquals(true, instance.happenedBefore(examples.get("a1")));
    assertEquals(true, instance.happenedBefore(examples.get("a2")));
    assertEquals(true, instance.happenedBefore(examples.get("a3")));
    assertEquals(true, instance.happenedBefore(examples.get("a4")));
    assertEquals(false, instance.happenedBefore(examples.get("b0")));
    assertEquals(false, instance.happenedBefore(examples.get("b1")));
    assertEquals(false, instance.happenedBefore(examples.get("b2")));
    assertEquals(true, instance.happenedBefore(examples.get("b3")));
    assertEquals(true, instance.happenedBefore(examples.get("b4")));
    assertEquals(true, instance.happenedBefore(examples.get("b5")));
    assertEquals(false, instance.happenedBefore(examples.get("c0")));
    assertEquals(false, instance.happenedBefore(examples.get("c1")));
    assertEquals(true, instance.happenedBefore(examples.get("c2")));
    assertEquals(true, instance.happenedBefore(examples.get("c3")));
    assertEquals(true, instance.happenedBefore(examples.get("c4")));
    assertEquals(true, instance.happenedBefore(examples.get("c5")));
  }

  /**
   * Test happenedBefore against a example vector b3.
   */
  @Test
  public void testHappenedBefore_examples_b3() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("b3");

    assertEquals(false, instance.happenedBefore(examples.get("a0")));
    assertEquals(false, instance.happenedBefore(examples.get("a1")));
    assertEquals(false, instance.happenedBefore(examples.get("a2")));
    assertEquals(true, instance.happenedBefore(examples.get("a3")));
    assertEquals(true, instance.happenedBefore(examples.get("a4")));
    assertEquals(false, instance.happenedBefore(examples.get("b0")));
    assertEquals(false, instance.happenedBefore(examples.get("b1")));
    assertEquals(false, instance.happenedBefore(examples.get("b2")));
    assertEquals(false, instance.happenedBefore(examples.get("b3")));
    assertEquals(true, instance.happenedBefore(examples.get("b4")));
    assertEquals(true, instance.happenedBefore(examples.get("b5")));
    assertEquals(false, instance.happenedBefore(examples.get("c0")));
    assertEquals(false, instance.happenedBefore(examples.get("c1")));
    assertEquals(true, instance.happenedBefore(examples.get("c2")));
    assertEquals(true, instance.happenedBefore(examples.get("c3")));
    assertEquals(true, instance.happenedBefore(examples.get("c4")));
    assertEquals(true, instance.happenedBefore(examples.get("c5")));
  }

  /**
   * Test happenedBefore against a example vector b4.
   */
  @Test
  public void testHappenedBefore_examples_b4() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("b4");

    assertEquals(false, instance.happenedBefore(examples.get("a0")));
    assertEquals(false, instance.happenedBefore(examples.get("a1")));
    assertEquals(false, instance.happenedBefore(examples.get("a2")));
    assertEquals(false, instance.happenedBefore(examples.get("a3")));
    assertEquals(true, instance.happenedBefore(examples.get("a4")));
    assertEquals(false, instance.happenedBefore(examples.get("b0")));
    assertEquals(false, instance.happenedBefore(examples.get("b1")));
    assertEquals(false, instance.happenedBefore(examples.get("b2")));
    assertEquals(false, instance.happenedBefore(examples.get("b3")));
    assertEquals(false, instance.happenedBefore(examples.get("b4")));
    assertEquals(true, instance.happenedBefore(examples.get("b5")));
    assertEquals(false, instance.happenedBefore(examples.get("c0")));
    assertEquals(false, instance.happenedBefore(examples.get("c1")));
    assertEquals(false, instance.happenedBefore(examples.get("c2")));
    assertEquals(false, instance.happenedBefore(examples.get("c3")));
    assertEquals(true, instance.happenedBefore(examples.get("c4")));
    assertEquals(true, instance.happenedBefore(examples.get("c5")));
  }

  /**
   * Test happenedBefore against a example vector b5.
   */
  @Test
  public void testHappenedBefore_examples_b5() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("b5");

    assertEquals(false, instance.happenedBefore(examples.get("a0")));
    assertEquals(false, instance.happenedBefore(examples.get("a1")));
    assertEquals(false, instance.happenedBefore(examples.get("a2")));
    assertEquals(false, instance.happenedBefore(examples.get("a3")));
    assertEquals(true, instance.happenedBefore(examples.get("a4")));
    assertEquals(false, instance.happenedBefore(examples.get("b0")));
    assertEquals(false, instance.happenedBefore(examples.get("b1")));
    assertEquals(false, instance.happenedBefore(examples.get("b2")));
    assertEquals(false, instance.happenedBefore(examples.get("b3")));
    assertEquals(false, instance.happenedBefore(examples.get("b4")));
    assertEquals(false, instance.happenedBefore(examples.get("b5")));
    assertEquals(false, instance.happenedBefore(examples.get("c0")));
    assertEquals(false, instance.happenedBefore(examples.get("c1")));
    assertEquals(false, instance.happenedBefore(examples.get("c2")));
    assertEquals(false, instance.happenedBefore(examples.get("c3")));
    assertEquals(true, instance.happenedBefore(examples.get("c4")));
    assertEquals(true, instance.happenedBefore(examples.get("c5")));
  }

  /**
   * Test happenedBefore against a example vector c0.
   */
  @Test
  public void testHappenedBefore_examples_c0() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("c0");

    assertEquals(false, instance.happenedBefore(examples.get("a0")));
    assertEquals(true, instance.happenedBefore(examples.get("a1")));
    assertEquals(true, instance.happenedBefore(examples.get("a2")));
    assertEquals(true, instance.happenedBefore(examples.get("a3")));
    assertEquals(true, instance.happenedBefore(examples.get("a4")));
    assertEquals(false, instance.happenedBefore(examples.get("b0")));
    assertEquals(true, instance.happenedBefore(examples.get("b1")));
    assertEquals(true, instance.happenedBefore(examples.get("b2")));
    assertEquals(true, instance.happenedBefore(examples.get("b3")));
    assertEquals(true, instance.happenedBefore(examples.get("b4")));
    assertEquals(true, instance.happenedBefore(examples.get("b5")));
    assertEquals(false, instance.happenedBefore(examples.get("c0")));
    assertEquals(true, instance.happenedBefore(examples.get("c1")));
    assertEquals(true, instance.happenedBefore(examples.get("c2")));
    assertEquals(true, instance.happenedBefore(examples.get("c3")));
    assertEquals(true, instance.happenedBefore(examples.get("c4")));
    assertEquals(true, instance.happenedBefore(examples.get("c5")));
  }

  /**
   * Test happenedBefore against a example vector c1.
   */
  @Test
  public void testHappenedBefore_examples_c1() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("c1");

    assertEquals(false, instance.happenedBefore(examples.get("a0")));
    assertEquals(true, instance.happenedBefore(examples.get("a1")));
    assertEquals(true, instance.happenedBefore(examples.get("a2")));
    assertEquals(true, instance.happenedBefore(examples.get("a3")));
    assertEquals(true, instance.happenedBefore(examples.get("a4")));
    assertEquals(false, instance.happenedBefore(examples.get("b0")));
    assertEquals(true, instance.happenedBefore(examples.get("b1")));
    assertEquals(true, instance.happenedBefore(examples.get("b2")));
    assertEquals(true, instance.happenedBefore(examples.get("b3")));
    assertEquals(true, instance.happenedBefore(examples.get("b4")));
    assertEquals(true, instance.happenedBefore(examples.get("b5")));
    assertEquals(false, instance.happenedBefore(examples.get("c0")));
    assertEquals(false, instance.happenedBefore(examples.get("c1")));
    assertEquals(true, instance.happenedBefore(examples.get("c2")));
    assertEquals(true, instance.happenedBefore(examples.get("c3")));
    assertEquals(true, instance.happenedBefore(examples.get("c4")));
    assertEquals(true, instance.happenedBefore(examples.get("c5")));
  }

  /**
   * Test happenedBefore against a example vector c2.
   */
  @Test
  public void testHappenedBefore_examples_c2() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("c2");

    assertEquals(false, instance.happenedBefore(examples.get("a0")));
    assertEquals(false, instance.happenedBefore(examples.get("a1")));
    assertEquals(false, instance.happenedBefore(examples.get("a2")));
    assertEquals(true, instance.happenedBefore(examples.get("a3")));
    assertEquals(true, instance.happenedBefore(examples.get("a4")));
    assertEquals(false, instance.happenedBefore(examples.get("b0")));
    assertEquals(false, instance.happenedBefore(examples.get("b1")));
    assertEquals(false, instance.happenedBefore(examples.get("b2")));
    assertEquals(false, instance.happenedBefore(examples.get("b3")));
    assertEquals(false, instance.happenedBefore(examples.get("b4")));
    assertEquals(false, instance.happenedBefore(examples.get("b5")));
    assertEquals(false, instance.happenedBefore(examples.get("c0")));
    assertEquals(false, instance.happenedBefore(examples.get("c1")));
    assertEquals(false, instance.happenedBefore(examples.get("c2")));
    assertEquals(true, instance.happenedBefore(examples.get("c3")));
    assertEquals(true, instance.happenedBefore(examples.get("c4")));
    assertEquals(true, instance.happenedBefore(examples.get("c5")));
  }

  /**
   * Test happenedBefore against a example vector c3.
   */
  @Test
  public void testHappenedBefore_examples_c3() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("c3");

    assertEquals(false, instance.happenedBefore(examples.get("a0")));
    assertEquals(false, instance.happenedBefore(examples.get("a1")));
    assertEquals(false, instance.happenedBefore(examples.get("a2")));
    assertEquals(true, instance.happenedBefore(examples.get("a3")));
    assertEquals(true, instance.happenedBefore(examples.get("a4")));
    assertEquals(false, instance.happenedBefore(examples.get("b0")));
    assertEquals(false, instance.happenedBefore(examples.get("b1")));
    assertEquals(false, instance.happenedBefore(examples.get("b2")));
    assertEquals(false, instance.happenedBefore(examples.get("b3")));
    assertEquals(false, instance.happenedBefore(examples.get("b4")));
    assertEquals(false, instance.happenedBefore(examples.get("b5")));
    assertEquals(false, instance.happenedBefore(examples.get("c0")));
    assertEquals(false, instance.happenedBefore(examples.get("c1")));
    assertEquals(false, instance.happenedBefore(examples.get("c2")));
    assertEquals(false, instance.happenedBefore(examples.get("c3")));
    assertEquals(true, instance.happenedBefore(examples.get("c4")));
    assertEquals(true, instance.happenedBefore(examples.get("c5")));
  }

  /**
   * Test happenedBefore against a example vector c4.
   */
  @Test
  public void testHappenedBefore_examples_c4() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("c4");

    assertEquals(false, instance.happenedBefore(examples.get("a0")));
    assertEquals(false, instance.happenedBefore(examples.get("a1")));
    assertEquals(false, instance.happenedBefore(examples.get("a2")));
    assertEquals(false, instance.happenedBefore(examples.get("a3")));
    assertEquals(true, instance.happenedBefore(examples.get("a4")));
    assertEquals(false, instance.happenedBefore(examples.get("b0")));
    assertEquals(false, instance.happenedBefore(examples.get("b1")));
    assertEquals(false, instance.happenedBefore(examples.get("b2")));
    assertEquals(false, instance.happenedBefore(examples.get("b3")));
    assertEquals(false, instance.happenedBefore(examples.get("b4")));
    assertEquals(false, instance.happenedBefore(examples.get("b5")));
    assertEquals(false, instance.happenedBefore(examples.get("c0")));
    assertEquals(false, instance.happenedBefore(examples.get("c1")));
    assertEquals(false, instance.happenedBefore(examples.get("c2")));
    assertEquals(false, instance.happenedBefore(examples.get("c3")));
    assertEquals(false, instance.happenedBefore(examples.get("c4")));
    assertEquals(true, instance.happenedBefore(examples.get("c5")));
  }

  /**
   * Test happenedBefore against a example vector c5.
   */
  @Test
  public void testHappenedBefore_examples_c5() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("c5");

    assertEquals(false, instance.happenedBefore(examples.get("a0")));
    assertEquals(false, instance.happenedBefore(examples.get("a1")));
    assertEquals(false, instance.happenedBefore(examples.get("a2")));
    assertEquals(false, instance.happenedBefore(examples.get("a3")));
    assertEquals(true, instance.happenedBefore(examples.get("a4")));
    assertEquals(false, instance.happenedBefore(examples.get("b0")));
    assertEquals(false, instance.happenedBefore(examples.get("b1")));
    assertEquals(false, instance.happenedBefore(examples.get("b2")));
    assertEquals(false, instance.happenedBefore(examples.get("b3")));
    assertEquals(false, instance.happenedBefore(examples.get("b4")));
    assertEquals(false, instance.happenedBefore(examples.get("b5")));
    assertEquals(false, instance.happenedBefore(examples.get("c0")));
    assertEquals(false, instance.happenedBefore(examples.get("c1")));
    assertEquals(false, instance.happenedBefore(examples.get("c2")));
    assertEquals(false, instance.happenedBefore(examples.get("c3")));
    assertEquals(false, instance.happenedBefore(examples.get("c4")));
    assertEquals(false, instance.happenedBefore(examples.get("c5")));
  }

  /**
   * Test concurrentWith for equal vectors.
   */
  @Test
  public void testConcurrentWith_Equal() {
    LOGGER.log(Level.INFO, "testConcurrentWith_Equal: Test concurrentWith for equal vectors");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value1 = 1;
    final Integer value2 = 2;
    final Integer value3 = 3;

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, value1);
    vector1.put(id2, value2);
    vector1.put(id3, value3);
    Map vector2 = new HashMap(3);
    vector2.put(id1, value1);
    vector2.put(id2, value2);
    vector2.put(id3, value3);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    boolean expResult = false;
    boolean result1 = abstractVersionVector1.concurrentWith(abstractVersionVector2);
    boolean result2 = abstractVersionVector2.concurrentWith(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(expResult, result2);
  }

  /**
   * Test concurrentWith with concurrent vectors.
   */
  @Test
  public void testConcurrentWith_Concurrent() {
    LOGGER.log(Level.INFO,
        "testConcurrentWith_Concurrent: Test concurrentWith with concurrent vectors");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value1 = 1;
    final Integer value2 = 2;
    final Integer value3 = 3;

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, value1);
    vector1.put(id2, value2);
    vector1.put(id3, value3 + 1);
    Map vector2 = new HashMap(3);
    vector2.put(id1, value1 + 1);
    vector2.put(id2, value2);
    vector2.put(id3, value3);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    boolean expResult = true;
    boolean result1 = abstractVersionVector1.concurrentWith(abstractVersionVector2);
    boolean result2 = abstractVersionVector2.concurrentWith(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(expResult, result2);
  }

  /**
   * Test concurrentWith with a sequential vector.
   */
  @Test
  public void testConcurrentWith_SequentialSingle() {
    LOGGER.log(Level.INFO,
        "testConcurrentWith_SequentialSingle: Test concurrentWith with a sequential vector");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value1 = 1;
    final Integer value2 = 2;
    final Integer value3 = 3;

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, value1);
    vector1.put(id2, value2);
    vector1.put(id3, value3);
    Map vector2 = new HashMap(3);
    vector2.put(id1, value1);
    vector2.put(id2, value2 + 1);
    vector2.put(id3, value3);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    boolean expResult = false;
    boolean result1 = abstractVersionVector1.concurrentWith(abstractVersionVector2);
    boolean result2 = abstractVersionVector2.concurrentWith(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(expResult, result2);
  }

  /**
   * Test concurrentWith with a sequential vector that's been incremented twice.
   */
  @Test
  public void testConcurrentWith_SequentialDouble() {
    LOGGER.log(Level.INFO, "testConcurrentWith_SequentialDouble: "
        + "Test concurrentWith with a sequential vector that's been incremented twice");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value1 = 1;
    final Integer value2 = 2;
    final Integer value3 = 3;

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, value1);
    vector1.put(id2, value2);
    vector1.put(id3, value3);
    Map vector2 = new HashMap(3);
    vector2.put(id1, value1 + 1);
    vector2.put(id2, value2);
    vector2.put(id3, value3 + 1);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    boolean expResult = false;
    boolean result1 = abstractVersionVector1.concurrentWith(abstractVersionVector2);
    boolean result2 = abstractVersionVector2.concurrentWith(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(expResult, result2);
  }

  /**
   * Test concurrentWith with a sequential vector that's been incremented thrice.
   */
  @Test
  public void testConcurrentWith_SequentialTripple() {
    LOGGER.log(Level.INFO, "testConcurrentWith_SequentialTripple: "
        + "Test concurrentWith with a sequential vector that's been incremented thrice");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value1 = 1;
    final Integer value2 = 2;
    final Integer value3 = 3;

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, value1);
    vector1.put(id2, value2);
    vector1.put(id3, value3);
    Map vector2 = new HashMap(3);
    vector2.put(id1, value1 + 1);
    vector2.put(id2, value2 + 1);
    vector2.put(id3, value3 + 1);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    boolean expResult = false;
    boolean result1 = abstractVersionVector1.concurrentWith(abstractVersionVector2);
    boolean result2 = abstractVersionVector2.concurrentWith(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(expResult, result2);
  }

  /**
   * Test of concurrentWith method, of class AbstractVersionVector.
   */
  @Test
  public void testConcurrentWith_LargeIncrement() {
    LOGGER.log(Level.INFO, "testConcurrentWith_LargeIncrement: "
        + "Test concurrentWith with a sequential vector that's been incremented a lot");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value1 = 1;
    final Integer value2 = 2;
    final Integer value3 = 3;

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, value1);
    vector1.put(id2, value2);
    vector1.put(id3, value3);
    Map vector2 = new HashMap(3);
    vector2.put(id1, value1 + 7);
    vector2.put(id2, value2 + 5);
    vector2.put(id3, value3 + 3);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    boolean expResult = false;
    boolean result1 = abstractVersionVector1.concurrentWith(abstractVersionVector2);
    boolean result2 = abstractVersionVector2.concurrentWith(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(expResult, result2);
  }

  /**
   * Test concurrentWith with a equal vectors (one with an implicit zero value).
   */
  @Test
  public void testConcurrentWith_ImplicitZeroEqual() {
    LOGGER.log(Level.INFO, "testConcurrentWith_ImplicitZeroEqual: "
        + "Test concurrentWith with a equal vectors (one with an implicit zero value)");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, 0);
    vector1.put(id2, 1);
    vector1.put(id3, 2);
    Map vector2 = new HashMap(3);
    vector2.put(id2, 1);
    vector2.put(id3, 2);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    boolean expResult = false;
    boolean result1 = abstractVersionVector1.concurrentWith(abstractVersionVector2);
    boolean result2 = abstractVersionVector2.concurrentWith(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(expResult, result2);
  }

  /**
   * Test concurrentWith with a sequential vector (with an implicit zero value).
   */
  @Test
  public void testConcurrentWith_ImplicitZeroSequential() {
    LOGGER.log(Level.INFO, "testConcurrentWith_ImplicitZeroSequential: "
        + "Test concurrentWith with a sequential vector (with an implicit zero value)");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, 1);
    vector1.put(id2, 2);
    vector1.put(id3, 3);
    Map vector2 = new HashMap(3);
    vector2.put(id2, 2);
    vector2.put(id3, 3);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    boolean expResult = false;
    boolean result1 = abstractVersionVector1.concurrentWith(abstractVersionVector2);
    boolean result2 = abstractVersionVector2.concurrentWith(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(expResult, result2);
  }

  /**
   * Test concurrentWith against a example vector a0.
   */
  @Test
  public void testConcurrentWith_examples_a0() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("a0");

    assertEquals(false, instance.concurrentWith(examples.get("a0")));
    assertEquals(false, instance.concurrentWith(examples.get("a1")));
    assertEquals(false, instance.concurrentWith(examples.get("a2")));
    assertEquals(false, instance.concurrentWith(examples.get("a3")));
    assertEquals(false, instance.concurrentWith(examples.get("a4")));
    assertEquals(false, instance.concurrentWith(examples.get("b0")));
    assertEquals(false, instance.concurrentWith(examples.get("b1")));
    assertEquals(false, instance.concurrentWith(examples.get("b2")));
    assertEquals(false, instance.concurrentWith(examples.get("b3")));
    assertEquals(false, instance.concurrentWith(examples.get("b4")));
    assertEquals(false, instance.concurrentWith(examples.get("b5")));
    assertEquals(false, instance.concurrentWith(examples.get("c0")));
    assertEquals(false, instance.concurrentWith(examples.get("c1")));
    assertEquals(false, instance.concurrentWith(examples.get("c2")));
    assertEquals(false, instance.concurrentWith(examples.get("c3")));
    assertEquals(false, instance.concurrentWith(examples.get("c4")));
    assertEquals(false, instance.concurrentWith(examples.get("c5")));
  }

  /**
   * Test concurrentWith against a example vector a1.
   */
  @Test
  public void testConcurrentWith_examples_a1() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("a1");

    assertEquals(false, instance.concurrentWith(examples.get("a0")));
    assertEquals(false, instance.concurrentWith(examples.get("a1")));
    assertEquals(false, instance.concurrentWith(examples.get("a2")));
    assertEquals(false, instance.concurrentWith(examples.get("a3")));
    assertEquals(false, instance.concurrentWith(examples.get("a4")));
    assertEquals(false, instance.concurrentWith(examples.get("b0")));
    assertEquals(false, instance.concurrentWith(examples.get("b1")));
    assertEquals(false, instance.concurrentWith(examples.get("b2")));
    assertEquals(true, instance.concurrentWith(examples.get("b3")));
    assertEquals(false, instance.concurrentWith(examples.get("b4")));
    assertEquals(false, instance.concurrentWith(examples.get("b5")));
    assertEquals(false, instance.concurrentWith(examples.get("c0")));
    assertEquals(false, instance.concurrentWith(examples.get("c1")));
    assertEquals(true, instance.concurrentWith(examples.get("c2")));
    assertEquals(true, instance.concurrentWith(examples.get("c3")));
    assertEquals(false, instance.concurrentWith(examples.get("c4")));
    assertEquals(false, instance.concurrentWith(examples.get("c5")));
  }

  /**
   * Test concurrentWith against a example vector a2.
   */
  @Test
  public void testConcurrentWith_examples_a2() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("a2");

    assertEquals(false, instance.concurrentWith(examples.get("a0")));
    assertEquals(false, instance.concurrentWith(examples.get("a1")));
    assertEquals(false, instance.concurrentWith(examples.get("a2")));
    assertEquals(false, instance.concurrentWith(examples.get("a3")));
    assertEquals(false, instance.concurrentWith(examples.get("a4")));
    assertEquals(false, instance.concurrentWith(examples.get("b0")));
    assertEquals(false, instance.concurrentWith(examples.get("b1")));
    assertEquals(false, instance.concurrentWith(examples.get("b2")));
    assertEquals(true, instance.concurrentWith(examples.get("b3")));
    assertEquals(false, instance.concurrentWith(examples.get("b4")));
    assertEquals(false, instance.concurrentWith(examples.get("b5")));
    assertEquals(false, instance.concurrentWith(examples.get("c0")));
    assertEquals(false, instance.concurrentWith(examples.get("c1")));
    assertEquals(true, instance.concurrentWith(examples.get("c2")));
    assertEquals(true, instance.concurrentWith(examples.get("c3")));
    assertEquals(false, instance.concurrentWith(examples.get("c4")));
    assertEquals(false, instance.concurrentWith(examples.get("c5")));
  }

  /**
   * Test concurrentWith against a example vector a3.
   */
  @Test
  public void testConcurrentWith_examples_a3() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("a3");

    assertEquals(false, instance.concurrentWith(examples.get("a0")));
    assertEquals(false, instance.concurrentWith(examples.get("a1")));
    assertEquals(false, instance.concurrentWith(examples.get("a2")));
    assertEquals(false, instance.concurrentWith(examples.get("a3")));
    assertEquals(false, instance.concurrentWith(examples.get("a4")));
    assertEquals(false, instance.concurrentWith(examples.get("b0")));
    assertEquals(false, instance.concurrentWith(examples.get("b1")));
    assertEquals(false, instance.concurrentWith(examples.get("b2")));
    assertEquals(false, instance.concurrentWith(examples.get("b3")));
    assertEquals(true, instance.concurrentWith(examples.get("b4")));
    assertEquals(true, instance.concurrentWith(examples.get("b5")));
    assertEquals(false, instance.concurrentWith(examples.get("c0")));
    assertEquals(false, instance.concurrentWith(examples.get("c1")));
    assertEquals(false, instance.concurrentWith(examples.get("c2")));
    assertEquals(false, instance.concurrentWith(examples.get("c3")));
    assertEquals(true, instance.concurrentWith(examples.get("c4")));
    assertEquals(true, instance.concurrentWith(examples.get("c5")));
  }

  /**
   * Test concurrentWith against a example vector a4.
   */
  @Test
  public void testConcurrentWith_examples_a4() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("a4");

    assertEquals(false, instance.concurrentWith(examples.get("a0")));
    assertEquals(false, instance.concurrentWith(examples.get("a1")));
    assertEquals(false, instance.concurrentWith(examples.get("a2")));
    assertEquals(false, instance.concurrentWith(examples.get("a3")));
    assertEquals(false, instance.concurrentWith(examples.get("a4")));
    assertEquals(false, instance.concurrentWith(examples.get("b0")));
    assertEquals(false, instance.concurrentWith(examples.get("b1")));
    assertEquals(false, instance.concurrentWith(examples.get("b2")));
    assertEquals(false, instance.concurrentWith(examples.get("b3")));
    assertEquals(false, instance.concurrentWith(examples.get("b4")));
    assertEquals(false, instance.concurrentWith(examples.get("b5")));
    assertEquals(false, instance.concurrentWith(examples.get("c0")));
    assertEquals(false, instance.concurrentWith(examples.get("c1")));
    assertEquals(false, instance.concurrentWith(examples.get("c2")));
    assertEquals(false, instance.concurrentWith(examples.get("c3")));
    assertEquals(false, instance.concurrentWith(examples.get("c4")));
    assertEquals(false, instance.concurrentWith(examples.get("c5")));
  }

  /**
   * Test concurrentWith against a example vector b0.
   */
  @Test
  public void testConcurrentWith_examples_b0() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("b0");

    assertEquals(false, instance.concurrentWith(examples.get("a0")));
    assertEquals(false, instance.concurrentWith(examples.get("a1")));
    assertEquals(false, instance.concurrentWith(examples.get("a2")));
    assertEquals(false, instance.concurrentWith(examples.get("a3")));
    assertEquals(false, instance.concurrentWith(examples.get("a4")));
    assertEquals(false, instance.concurrentWith(examples.get("b0")));
    assertEquals(false, instance.concurrentWith(examples.get("b1")));
    assertEquals(false, instance.concurrentWith(examples.get("b2")));
    assertEquals(false, instance.concurrentWith(examples.get("b3")));
    assertEquals(false, instance.concurrentWith(examples.get("b4")));
    assertEquals(false, instance.concurrentWith(examples.get("b5")));
    assertEquals(false, instance.concurrentWith(examples.get("c0")));
    assertEquals(false, instance.concurrentWith(examples.get("c1")));
    assertEquals(false, instance.concurrentWith(examples.get("c2")));
    assertEquals(false, instance.concurrentWith(examples.get("c3")));
    assertEquals(false, instance.concurrentWith(examples.get("c4")));
    assertEquals(false, instance.concurrentWith(examples.get("c5")));
  }

  /**
   * Test concurrentWith against a example vector b1.
   */
  @Test
  public void testConcurrentWith_examples_b1() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("b1");

    assertEquals(false, instance.concurrentWith(examples.get("a0")));
    assertEquals(false, instance.concurrentWith(examples.get("a1")));
    assertEquals(false, instance.concurrentWith(examples.get("a2")));
    assertEquals(false, instance.concurrentWith(examples.get("a3")));
    assertEquals(false, instance.concurrentWith(examples.get("a4")));
    assertEquals(false, instance.concurrentWith(examples.get("b0")));
    assertEquals(false, instance.concurrentWith(examples.get("b1")));
    assertEquals(false, instance.concurrentWith(examples.get("b2")));
    assertEquals(false, instance.concurrentWith(examples.get("b3")));
    assertEquals(false, instance.concurrentWith(examples.get("b4")));
    assertEquals(false, instance.concurrentWith(examples.get("b5")));
    assertEquals(false, instance.concurrentWith(examples.get("c0")));
    assertEquals(false, instance.concurrentWith(examples.get("c1")));
    assertEquals(false, instance.concurrentWith(examples.get("c2")));
    assertEquals(false, instance.concurrentWith(examples.get("c3")));
    assertEquals(false, instance.concurrentWith(examples.get("c4")));
    assertEquals(false, instance.concurrentWith(examples.get("c5")));
  }

  /**
   * Test concurrentWith against a example vector b2.
   */
  @Test
  public void testConcurrentWith_examples_b2() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("b2");

    assertEquals(false, instance.concurrentWith(examples.get("a0")));
    assertEquals(false, instance.concurrentWith(examples.get("a1")));
    assertEquals(false, instance.concurrentWith(examples.get("a2")));
    assertEquals(false, instance.concurrentWith(examples.get("a3")));
    assertEquals(false, instance.concurrentWith(examples.get("a4")));
    assertEquals(false, instance.concurrentWith(examples.get("b0")));
    assertEquals(false, instance.concurrentWith(examples.get("b1")));
    assertEquals(false, instance.concurrentWith(examples.get("b2")));
    assertEquals(false, instance.concurrentWith(examples.get("b3")));
    assertEquals(false, instance.concurrentWith(examples.get("b4")));
    assertEquals(false, instance.concurrentWith(examples.get("b5")));
    assertEquals(false, instance.concurrentWith(examples.get("c0")));
    assertEquals(false, instance.concurrentWith(examples.get("c1")));
    assertEquals(false, instance.concurrentWith(examples.get("c2")));
    assertEquals(false, instance.concurrentWith(examples.get("c3")));
    assertEquals(false, instance.concurrentWith(examples.get("c4")));
    assertEquals(false, instance.concurrentWith(examples.get("c5")));
  }

  /**
   * Test concurrentWith against a example vector b3.
   */
  @Test
  public void testConcurrentWith_examples_b3() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("b3");

    assertEquals(false, instance.concurrentWith(examples.get("a0")));
    assertEquals(true, instance.concurrentWith(examples.get("a1")));
    assertEquals(true, instance.concurrentWith(examples.get("a2")));
    assertEquals(false, instance.concurrentWith(examples.get("a3")));
    assertEquals(false, instance.concurrentWith(examples.get("a4")));
    assertEquals(false, instance.concurrentWith(examples.get("b0")));
    assertEquals(false, instance.concurrentWith(examples.get("b1")));
    assertEquals(false, instance.concurrentWith(examples.get("b2")));
    assertEquals(false, instance.concurrentWith(examples.get("b3")));
    assertEquals(false, instance.concurrentWith(examples.get("b4")));
    assertEquals(false, instance.concurrentWith(examples.get("b5")));
    assertEquals(false, instance.concurrentWith(examples.get("c0")));
    assertEquals(false, instance.concurrentWith(examples.get("c1")));
    assertEquals(false, instance.concurrentWith(examples.get("c2")));
    assertEquals(false, instance.concurrentWith(examples.get("c3")));
    assertEquals(false, instance.concurrentWith(examples.get("c4")));
    assertEquals(false, instance.concurrentWith(examples.get("c5")));
  }

  /**
   * Test concurrentWith against a example vector b4.
   */
  @Test
  public void testConcurrentWith_examples_b4() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("b4");

    assertEquals(false, instance.concurrentWith(examples.get("a0")));
    assertEquals(false, instance.concurrentWith(examples.get("a1")));
    assertEquals(false, instance.concurrentWith(examples.get("a2")));
    assertEquals(true, instance.concurrentWith(examples.get("a3")));
    assertEquals(false, instance.concurrentWith(examples.get("a4")));
    assertEquals(false, instance.concurrentWith(examples.get("b0")));
    assertEquals(false, instance.concurrentWith(examples.get("b1")));
    assertEquals(false, instance.concurrentWith(examples.get("b2")));
    assertEquals(false, instance.concurrentWith(examples.get("b3")));
    assertEquals(false, instance.concurrentWith(examples.get("b4")));
    assertEquals(false, instance.concurrentWith(examples.get("b5")));
    assertEquals(false, instance.concurrentWith(examples.get("c0")));
    assertEquals(false, instance.concurrentWith(examples.get("c1")));
    assertEquals(true, instance.concurrentWith(examples.get("c2")));
    assertEquals(true, instance.concurrentWith(examples.get("c3")));
    assertEquals(false, instance.concurrentWith(examples.get("c4")));
    assertEquals(false, instance.concurrentWith(examples.get("c5")));
  }

  /**
   * Test concurrentWith against a example vector b5.
   */
  @Test
  public void testConcurrentWith_examples_b5() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("b5");

    assertEquals(false, instance.concurrentWith(examples.get("a0")));
    assertEquals(false, instance.concurrentWith(examples.get("a1")));
    assertEquals(false, instance.concurrentWith(examples.get("a2")));
    assertEquals(true, instance.concurrentWith(examples.get("a3")));
    assertEquals(false, instance.concurrentWith(examples.get("a4")));
    assertEquals(false, instance.concurrentWith(examples.get("b0")));
    assertEquals(false, instance.concurrentWith(examples.get("b1")));
    assertEquals(false, instance.concurrentWith(examples.get("b2")));
    assertEquals(false, instance.concurrentWith(examples.get("b3")));
    assertEquals(false, instance.concurrentWith(examples.get("b4")));
    assertEquals(false, instance.concurrentWith(examples.get("b5")));
    assertEquals(false, instance.concurrentWith(examples.get("c0")));
    assertEquals(false, instance.concurrentWith(examples.get("c1")));
    assertEquals(true, instance.concurrentWith(examples.get("c2")));
    assertEquals(true, instance.concurrentWith(examples.get("c3")));
    assertEquals(false, instance.concurrentWith(examples.get("c4")));
    assertEquals(false, instance.concurrentWith(examples.get("c5")));
  }

  /**
   * Test concurrentWith against a example vector c0.
   */
  @Test
  public void testConcurrentWith_examples_c0() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("c0");

    assertEquals(false, instance.concurrentWith(examples.get("a0")));
    assertEquals(false, instance.concurrentWith(examples.get("a1")));
    assertEquals(false, instance.concurrentWith(examples.get("a2")));
    assertEquals(false, instance.concurrentWith(examples.get("a3")));
    assertEquals(false, instance.concurrentWith(examples.get("a4")));
    assertEquals(false, instance.concurrentWith(examples.get("b0")));
    assertEquals(false, instance.concurrentWith(examples.get("b1")));
    assertEquals(false, instance.concurrentWith(examples.get("b2")));
    assertEquals(false, instance.concurrentWith(examples.get("b3")));
    assertEquals(false, instance.concurrentWith(examples.get("b4")));
    assertEquals(false, instance.concurrentWith(examples.get("b5")));
    assertEquals(false, instance.concurrentWith(examples.get("c0")));
    assertEquals(false, instance.concurrentWith(examples.get("c1")));
    assertEquals(false, instance.concurrentWith(examples.get("c2")));
    assertEquals(false, instance.concurrentWith(examples.get("c3")));
    assertEquals(false, instance.concurrentWith(examples.get("c4")));
    assertEquals(false, instance.concurrentWith(examples.get("c5")));
  }

  /**
   * Test concurrentWith against a example vector c1.
   */
  @Test
  public void testConcurrentWith_examples_c1() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("c1");

    assertEquals(false, instance.concurrentWith(examples.get("a0")));
    assertEquals(false, instance.concurrentWith(examples.get("a1")));
    assertEquals(false, instance.concurrentWith(examples.get("a2")));
    assertEquals(false, instance.concurrentWith(examples.get("a3")));
    assertEquals(false, instance.concurrentWith(examples.get("a4")));
    assertEquals(false, instance.concurrentWith(examples.get("b0")));
    assertEquals(false, instance.concurrentWith(examples.get("b1")));
    assertEquals(false, instance.concurrentWith(examples.get("b2")));
    assertEquals(false, instance.concurrentWith(examples.get("b3")));
    assertEquals(false, instance.concurrentWith(examples.get("b4")));
    assertEquals(false, instance.concurrentWith(examples.get("b5")));
    assertEquals(false, instance.concurrentWith(examples.get("c0")));
    assertEquals(false, instance.concurrentWith(examples.get("c1")));
    assertEquals(false, instance.concurrentWith(examples.get("c2")));
    assertEquals(false, instance.concurrentWith(examples.get("c3")));
    assertEquals(false, instance.concurrentWith(examples.get("c4")));
    assertEquals(false, instance.concurrentWith(examples.get("c5")));
  }

  /**
   * Test concurrentWith against a example vector c2.
   */
  @Test
  public void testConcurrentWith_examples_c2() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("c2");

    assertEquals(false, instance.concurrentWith(examples.get("a0")));
    assertEquals(true, instance.concurrentWith(examples.get("a1")));
    assertEquals(true, instance.concurrentWith(examples.get("a2")));
    assertEquals(false, instance.concurrentWith(examples.get("a3")));
    assertEquals(false, instance.concurrentWith(examples.get("a4")));
    assertEquals(false, instance.concurrentWith(examples.get("b0")));
    assertEquals(false, instance.concurrentWith(examples.get("b1")));
    assertEquals(false, instance.concurrentWith(examples.get("b2")));
    assertEquals(false, instance.concurrentWith(examples.get("b3")));
    assertEquals(true, instance.concurrentWith(examples.get("b4")));
    assertEquals(true, instance.concurrentWith(examples.get("b5")));
    assertEquals(false, instance.concurrentWith(examples.get("c0")));
    assertEquals(false, instance.concurrentWith(examples.get("c1")));
    assertEquals(false, instance.concurrentWith(examples.get("c2")));
    assertEquals(false, instance.concurrentWith(examples.get("c3")));
    assertEquals(false, instance.concurrentWith(examples.get("c4")));
    assertEquals(false, instance.concurrentWith(examples.get("c5")));
  }

  /**
   * Test concurrentWith against a example vector c3.
   */
  @Test
  public void testConcurrentWith_examples_c3() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("c3");

    assertEquals(false, instance.concurrentWith(examples.get("a0")));
    assertEquals(true, instance.concurrentWith(examples.get("a1")));
    assertEquals(true, instance.concurrentWith(examples.get("a2")));
    assertEquals(false, instance.concurrentWith(examples.get("a3")));
    assertEquals(false, instance.concurrentWith(examples.get("a4")));
    assertEquals(false, instance.concurrentWith(examples.get("b0")));
    assertEquals(false, instance.concurrentWith(examples.get("b1")));
    assertEquals(false, instance.concurrentWith(examples.get("b2")));
    assertEquals(false, instance.concurrentWith(examples.get("b3")));
    assertEquals(true, instance.concurrentWith(examples.get("b4")));
    assertEquals(true, instance.concurrentWith(examples.get("b5")));
    assertEquals(false, instance.concurrentWith(examples.get("c0")));
    assertEquals(false, instance.concurrentWith(examples.get("c1")));
    assertEquals(false, instance.concurrentWith(examples.get("c2")));
    assertEquals(false, instance.concurrentWith(examples.get("c3")));
    assertEquals(false, instance.concurrentWith(examples.get("c4")));
    assertEquals(false, instance.concurrentWith(examples.get("c5")));
  }

  /**
   * Test concurrentWith against a example vector c4.
   */
  @Test
  public void testConcurrentWith_examples_c4() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("c4");

    assertEquals(false, instance.concurrentWith(examples.get("a0")));
    assertEquals(false, instance.concurrentWith(examples.get("a1")));
    assertEquals(false, instance.concurrentWith(examples.get("a2")));
    assertEquals(true, instance.concurrentWith(examples.get("a3")));
    assertEquals(false, instance.concurrentWith(examples.get("a4")));
    assertEquals(false, instance.concurrentWith(examples.get("b0")));
    assertEquals(false, instance.concurrentWith(examples.get("b1")));
    assertEquals(false, instance.concurrentWith(examples.get("b2")));
    assertEquals(false, instance.concurrentWith(examples.get("b3")));
    assertEquals(false, instance.concurrentWith(examples.get("b4")));
    assertEquals(false, instance.concurrentWith(examples.get("b5")));
    assertEquals(false, instance.concurrentWith(examples.get("c0")));
    assertEquals(false, instance.concurrentWith(examples.get("c1")));
    assertEquals(false, instance.concurrentWith(examples.get("c2")));
    assertEquals(false, instance.concurrentWith(examples.get("c3")));
    assertEquals(false, instance.concurrentWith(examples.get("c4")));
    assertEquals(false, instance.concurrentWith(examples.get("c5")));
  }

  /**
   * Test concurrentWith against a example vector c5.
   */
  @Test
  public void testConcurrentWith_examples_c5() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("c5");

    assertEquals(false, instance.concurrentWith(examples.get("a0")));
    assertEquals(false, instance.concurrentWith(examples.get("a1")));
    assertEquals(false, instance.concurrentWith(examples.get("a2")));
    assertEquals(true, instance.concurrentWith(examples.get("a3")));
    assertEquals(false, instance.concurrentWith(examples.get("a4")));
    assertEquals(false, instance.concurrentWith(examples.get("b0")));
    assertEquals(false, instance.concurrentWith(examples.get("b1")));
    assertEquals(false, instance.concurrentWith(examples.get("b2")));
    assertEquals(false, instance.concurrentWith(examples.get("b3")));
    assertEquals(false, instance.concurrentWith(examples.get("b4")));
    assertEquals(false, instance.concurrentWith(examples.get("b5")));
    assertEquals(false, instance.concurrentWith(examples.get("c0")));
    assertEquals(false, instance.concurrentWith(examples.get("c1")));
    assertEquals(false, instance.concurrentWith(examples.get("c2")));
    assertEquals(false, instance.concurrentWith(examples.get("c3")));
    assertEquals(false, instance.concurrentWith(examples.get("c4")));
    assertEquals(false, instance.concurrentWith(examples.get("c5")));
  }

  /**
   * Test compareTo with equal vectors.
   */
  @Test
  public void testCompareTo_Equal() {
    LOGGER.log(Level.INFO, "testCompareTo_Equal: Test compareTo with equal vectors");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value1 = 1;
    final Integer value2 = 2;
    final Integer value3 = 3;

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, value1);
    vector1.put(id2, value2);
    vector1.put(id3, value3);
    Map vector2 = new HashMap(3);
    vector2.put(id1, value1);
    vector2.put(id2, value2);
    vector2.put(id3, value3);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    int expResult = 0;
    int result1 = abstractVersionVector1.compareTo(abstractVersionVector2);
    int result2 = abstractVersionVector2.compareTo(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(-expResult, result2);
  }

  /**
   * Test compareTo with concurrent vectors.
   */
  @Test
  public void testCompareTo_Concurrent() {
    LOGGER.log(Level.INFO, "testCompareTo_Concurrent: Test compareTo with concurrent vectors");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value1 = 1;
    final Integer value2 = 2;
    final Integer value3 = 3;

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, value1);
    vector1.put(id2, value2);
    vector1.put(id3, value3 + 1);
    Map vector2 = new HashMap(3);
    vector2.put(id1, value1 + 1);
    vector2.put(id2, value2);
    vector2.put(id3, value3);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    int expResult = 0;
    int result1 = abstractVersionVector1.compareTo(abstractVersionVector2);
    int result2 = abstractVersionVector2.compareTo(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(-expResult, result2);
  }

  /**
   * Test compareTo with a sequential vector.
   */
  @Test
  public void testCompareTo_SequentialSingle() {
    LOGGER.log(Level.INFO,
        "testCompareTo_SequentialSingle: Test compareTo with a sequential vector");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value1 = 1;
    final Integer value2 = 2;
    final Integer value3 = 3;

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, value1);
    vector1.put(id2, value2);
    vector1.put(id3, value3);
    Map vector2 = new HashMap(3);
    vector2.put(id1, value1);
    vector2.put(id2, value2 + 1);
    vector2.put(id3, value3);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    int expResult = -1;
    int result1 = abstractVersionVector1.compareTo(abstractVersionVector2);
    int result2 = abstractVersionVector2.compareTo(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(-expResult, result2);
  }

  /**
   * Test compareTo with a sequential vector that's been incremented twice.
   */
  @Test
  public void testCompareTo_SequentialDouble() {
    LOGGER.log(Level.INFO, "testCompareTo_SequentialDouble: "
        + "Test compareTo with a sequential vector that's been incremented twice");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value1 = 1;
    final Integer value2 = 2;
    final Integer value3 = 3;

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, value1);
    vector1.put(id2, value2);
    vector1.put(id3, value3);
    Map vector2 = new HashMap(3);
    vector2.put(id1, value1 + 1);
    vector2.put(id2, value2);
    vector2.put(id3, value3 + 1);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    int expResult = -2;
    int result1 = abstractVersionVector1.compareTo(abstractVersionVector2);
    int result2 = abstractVersionVector2.compareTo(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(-expResult, result2);
  }

  /**
   * Test compareTo with a sequential vector that's been incremented thrice.
   */
  @Test
  public void testCompareTo_SequentialTripple() {
    LOGGER.log(Level.INFO, "testCompareTo_SequentialTripple: "
        + "Test compareTo with a sequential vector that's been incremented thrice");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value1 = 1;
    final Integer value2 = 2;
    final Integer value3 = 3;

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, value1);
    vector1.put(id2, value2);
    vector1.put(id3, value3);
    Map vector2 = new HashMap(3);
    vector2.put(id1, value1 + 1);
    vector2.put(id2, value2 + 1);
    vector2.put(id3, value3 + 1);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    int expResult = -3;
    int result1 = abstractVersionVector1.compareTo(abstractVersionVector2);
    int result2 = abstractVersionVector2.compareTo(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(-expResult, result2);
  }

  /**
   * Test of compareTo method, of class AbstractVersionVector.
   */
  @Test
  public void testCompareTo_LargeIncrement() {
    LOGGER.log(Level.INFO, "testCompareTo_LargeIncrement: "
        + "Test compareTo with a sequential vector that's been incremented a lot");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();
    final Integer value1 = 1;
    final Integer value2 = 2;
    final Integer value3 = 3;

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, value1);
    vector1.put(id2, value2);
    vector1.put(id3, value3);
    Map vector2 = new HashMap(3);
    vector2.put(id1, value1 + 7);
    vector2.put(id2, value2 + 5);
    vector2.put(id3, value3 + 3);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    int expResult = -3;
    int result1 = abstractVersionVector1.compareTo(abstractVersionVector2);
    int result2 = abstractVersionVector2.compareTo(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(-expResult, result2);
  }

  /**
   * Test compareTo with a equal vectors (one with an implicit zero value).
   */
  @Test
  public void testCompareTo_ImplicitZeroEqual() {
    LOGGER.log(Level.INFO, "testCompareTo_ImplicitZeroEqual: "
        + "Test compareTo with a equal vectors (one with an implicit zero value)");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, 0);
    vector1.put(id2, 1);
    vector1.put(id3, 2);
    Map vector2 = new HashMap(3);
    vector2.put(id2, 1);
    vector2.put(id3, 2);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    int expResult = 0;
    int result1 = abstractVersionVector1.compareTo(abstractVersionVector2);
    int result2 = abstractVersionVector2.compareTo(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(-expResult, result2);
  }

  /**
   * Test compareTo with a sequential vector (with an implicit zero value).
   */
  @Test
  public void testCompareTo_ImplicitZeroSequential() {
    LOGGER.log(Level.INFO, "testCompareTo_ImplicitZeroEqual: "
        + "Test compareTo with a sequential vector (with an implicit zero value)");

    // Constants for the test
    final Object id1 = new Object();
    final Object id2 = new Object();
    final Object id3 = new Object();

    // Setup the expected response
    Map vector1 = new HashMap(3);
    vector1.put(id1, 1);
    vector1.put(id2, 2);
    vector1.put(id3, 3);
    Map vector2 = new HashMap(3);
    vector2.put(id2, 2);
    vector2.put(id3, 3);

    // Setup the Vectors
    AbstractVersionVector<Object, Integer> abstractVersionVector1 =
        new ImmutableMapVersionVector<>(vector1, 0, false);
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, 0, false);

    // Compare
    int expResult = 1;
    int result1 = abstractVersionVector1.compareTo(abstractVersionVector2);
    int result2 = abstractVersionVector2.compareTo(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(-expResult, result2);
  }

  /**
   * Test compareTo against a example vector a0.
   */
  @Test
  public void testCompareTo_examples_a0() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("a0");

    assertEquals(0, instance.compareTo(examples.get("a0")));
    assertEquals(-3, instance.compareTo(examples.get("a1")));
    assertEquals(-3, instance.compareTo(examples.get("a2")));
    assertEquals(-3, instance.compareTo(examples.get("a3")));
    assertEquals(-3, instance.compareTo(examples.get("a4")));
    assertEquals(0, instance.compareTo(examples.get("b0")));
    assertEquals(-2, instance.compareTo(examples.get("b1")));
    assertEquals(-2, instance.compareTo(examples.get("b2")));
    assertEquals(-2, instance.compareTo(examples.get("b3")));
    assertEquals(-3, instance.compareTo(examples.get("b4")));
    assertEquals(-3, instance.compareTo(examples.get("b5")));
    assertEquals(-0, instance.compareTo(examples.get("c0")));
    assertEquals(-1, instance.compareTo(examples.get("c1")));
    assertEquals(-2, instance.compareTo(examples.get("c2")));
    assertEquals(-2, instance.compareTo(examples.get("c3")));
    assertEquals(-3, instance.compareTo(examples.get("c4")));
    assertEquals(-3, instance.compareTo(examples.get("c5")));
  }

  /**
   * Test compareTo against a example vector a1.
   */
  @Test
  public void testCompareTo_examples_a1() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("a1");

    assertEquals(3, instance.compareTo(examples.get("a0")));
    assertEquals(0, instance.compareTo(examples.get("a1")));
    assertEquals(-1, instance.compareTo(examples.get("a2")));
    assertEquals(-3, instance.compareTo(examples.get("a3")));
    assertEquals(-3, instance.compareTo(examples.get("a4")));
    assertEquals(3, instance.compareTo(examples.get("b0")));
    assertEquals(2, instance.compareTo(examples.get("b1")));
    assertEquals(1, instance.compareTo(examples.get("b2")));
    assertEquals(0, instance.compareTo(examples.get("b3")));
    assertEquals(-2, instance.compareTo(examples.get("b4")));
    assertEquals(-2, instance.compareTo(examples.get("b5")));
    assertEquals(3, instance.compareTo(examples.get("c0")));
    assertEquals(2, instance.compareTo(examples.get("c1")));
    assertEquals(0, instance.compareTo(examples.get("c2")));
    assertEquals(0, instance.compareTo(examples.get("c3")));
    assertEquals(-3, instance.compareTo(examples.get("c4")));
    assertEquals(-3, instance.compareTo(examples.get("c5")));
  }

  /**
   * Test compareTo against a example vector a2.
   */
  @Test
  public void testCompareTo_examples_a2() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("a2");

    assertEquals(3, instance.compareTo(examples.get("a0")));
    assertEquals(1, instance.compareTo(examples.get("a1")));
    assertEquals(0, instance.compareTo(examples.get("a2")));
    assertEquals(-3, instance.compareTo(examples.get("a3")));
    assertEquals(-3, instance.compareTo(examples.get("a4")));
    assertEquals(3, instance.compareTo(examples.get("b0")));
    assertEquals(2, instance.compareTo(examples.get("b1")));
    assertEquals(1, instance.compareTo(examples.get("b2")));
    assertEquals(0, instance.compareTo(examples.get("b3")));
    assertEquals(-1, instance.compareTo(examples.get("b4")));
    assertEquals(-1, instance.compareTo(examples.get("b5")));
    assertEquals(3, instance.compareTo(examples.get("c0")));
    assertEquals(2, instance.compareTo(examples.get("c1")));
    assertEquals(0, instance.compareTo(examples.get("c2")));
    assertEquals(0, instance.compareTo(examples.get("c3")));
    assertEquals(-2, instance.compareTo(examples.get("c4")));
    assertEquals(-2, instance.compareTo(examples.get("c5")));
  }

  /**
   * Test compareTo against a example vector a3.
   */
  @Test
  public void testCompareTo_examples_a3() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("a3");

    assertEquals(3, instance.compareTo(examples.get("a0")));
    assertEquals(3, instance.compareTo(examples.get("a1")));
    assertEquals(3, instance.compareTo(examples.get("a2")));
    assertEquals(0, instance.compareTo(examples.get("a3")));
    assertEquals(-3, instance.compareTo(examples.get("a4")));
    assertEquals(3, instance.compareTo(examples.get("b0")));
    assertEquals(3, instance.compareTo(examples.get("b1")));
    assertEquals(3, instance.compareTo(examples.get("b2")));
    assertEquals(2, instance.compareTo(examples.get("b3")));
    assertEquals(0, instance.compareTo(examples.get("b4")));
    assertEquals(0, instance.compareTo(examples.get("b5")));
    assertEquals(3, instance.compareTo(examples.get("c0")));
    assertEquals(3, instance.compareTo(examples.get("c1")));
    assertEquals(2, instance.compareTo(examples.get("c2")));
    assertEquals(1, instance.compareTo(examples.get("c3")));
    assertEquals(0, instance.compareTo(examples.get("c4")));
    assertEquals(0, instance.compareTo(examples.get("c5")));
  }

  /**
   * Test compareTo against a example vector a4.
   */
  @Test
  public void testCompareTo_examples_a4() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("a4");

    assertEquals(3, instance.compareTo(examples.get("a0")));
    assertEquals(3, instance.compareTo(examples.get("a1")));
    assertEquals(3, instance.compareTo(examples.get("a2")));
    assertEquals(3, instance.compareTo(examples.get("a3")));
    assertEquals(0, instance.compareTo(examples.get("a4")));
    assertEquals(3, instance.compareTo(examples.get("b0")));
    assertEquals(3, instance.compareTo(examples.get("b1")));
    assertEquals(3, instance.compareTo(examples.get("b2")));
    assertEquals(3, instance.compareTo(examples.get("b3")));
    assertEquals(3, instance.compareTo(examples.get("b4")));
    assertEquals(2, instance.compareTo(examples.get("b5")));
    assertEquals(3, instance.compareTo(examples.get("c0")));
    assertEquals(3, instance.compareTo(examples.get("c1")));
    assertEquals(3, instance.compareTo(examples.get("c2")));
    assertEquals(3, instance.compareTo(examples.get("c3")));
    assertEquals(2, instance.compareTo(examples.get("c4")));
    assertEquals(1, instance.compareTo(examples.get("c5")));
  }

  /**
   * Test compareTo against a example vector b0.
   */
  @Test
  public void testCompareTo_examples_b0() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("b0");

    assertEquals(0, instance.compareTo(examples.get("a0")));
    assertEquals(-3, instance.compareTo(examples.get("a1")));
    assertEquals(-3, instance.compareTo(examples.get("a2")));
    assertEquals(-3, instance.compareTo(examples.get("a3")));
    assertEquals(-3, instance.compareTo(examples.get("a4")));
    assertEquals(0, instance.compareTo(examples.get("b0")));
    assertEquals(-2, instance.compareTo(examples.get("b1")));
    assertEquals(-2, instance.compareTo(examples.get("b2")));
    assertEquals(-2, instance.compareTo(examples.get("b3")));
    assertEquals(-3, instance.compareTo(examples.get("b4")));
    assertEquals(-3, instance.compareTo(examples.get("b5")));
    assertEquals(0, instance.compareTo(examples.get("c0")));
    assertEquals(-1, instance.compareTo(examples.get("c1")));
    assertEquals(-2, instance.compareTo(examples.get("c2")));
    assertEquals(-2, instance.compareTo(examples.get("c3")));
    assertEquals(-3, instance.compareTo(examples.get("c4")));
    assertEquals(-3, instance.compareTo(examples.get("c5")));
  }

  /**
   * Test compareTo against a example vector b1.
   */
  @Test
  public void testCompareTo_examples_b1() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("b1");

    assertEquals(2, instance.compareTo(examples.get("a0")));
    assertEquals(-2, instance.compareTo(examples.get("a1")));
    assertEquals(-2, instance.compareTo(examples.get("a2")));
    assertEquals(-3, instance.compareTo(examples.get("a3")));
    assertEquals(-3, instance.compareTo(examples.get("a4")));
    assertEquals(2, instance.compareTo(examples.get("b0")));
    assertEquals(0, instance.compareTo(examples.get("b1")));
    assertEquals(-1, instance.compareTo(examples.get("b2")));
    assertEquals(-1, instance.compareTo(examples.get("b3")));
    assertEquals(-2, instance.compareTo(examples.get("b4")));
    assertEquals(-2, instance.compareTo(examples.get("b5")));
    assertEquals(2, instance.compareTo(examples.get("c0")));
    assertEquals(1, instance.compareTo(examples.get("c1")));
    assertEquals(-2, instance.compareTo(examples.get("c2")));
    assertEquals(-2, instance.compareTo(examples.get("c3")));
    assertEquals(-3, instance.compareTo(examples.get("c4")));
    assertEquals(-3, instance.compareTo(examples.get("c5")));
  }

  /**
   * Test compareTo against a example vector b2.
   */
  @Test
  public void testCompareTo_examples_b2() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("b2");

    assertEquals(2, instance.compareTo(examples.get("a0")));
    assertEquals(-1, instance.compareTo(examples.get("a1")));
    assertEquals(-1, instance.compareTo(examples.get("a2")));
    assertEquals(-3, instance.compareTo(examples.get("a3")));
    assertEquals(-3, instance.compareTo(examples.get("a4")));
    assertEquals(2, instance.compareTo(examples.get("b0")));
    assertEquals(1, instance.compareTo(examples.get("b1")));
    assertEquals(0, instance.compareTo(examples.get("b2")));
    assertEquals(-1, instance.compareTo(examples.get("b3")));
    assertEquals(-2, instance.compareTo(examples.get("b4")));
    assertEquals(-2, instance.compareTo(examples.get("b5")));
    assertEquals(2, instance.compareTo(examples.get("c0")));
    assertEquals(1, instance.compareTo(examples.get("c1")));
    assertEquals(-2, instance.compareTo(examples.get("c2")));
    assertEquals(-2, instance.compareTo(examples.get("c3")));
    assertEquals(-3, instance.compareTo(examples.get("c4")));
    assertEquals(-3, instance.compareTo(examples.get("c5")));
  }

  /**
   * Test compareTo against a example vector b3.
   */
  @Test
  public void testCompareTo_examples_b3() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("b3");

    assertEquals(2, instance.compareTo(examples.get("a0")));
    assertEquals(0, instance.compareTo(examples.get("a1")));
    assertEquals(0, instance.compareTo(examples.get("a2")));
    assertEquals(-2, instance.compareTo(examples.get("a3")));
    assertEquals(-3, instance.compareTo(examples.get("a4")));
    assertEquals(2, instance.compareTo(examples.get("b0")));
    assertEquals(1, instance.compareTo(examples.get("b1")));
    assertEquals(1, instance.compareTo(examples.get("b2")));
    assertEquals(0, instance.compareTo(examples.get("b3")));
    assertEquals(-2, instance.compareTo(examples.get("b4")));
    assertEquals(-2, instance.compareTo(examples.get("b5")));
    assertEquals(2, instance.compareTo(examples.get("c0")));
    assertEquals(1, instance.compareTo(examples.get("c1")));
    assertEquals(-1, instance.compareTo(examples.get("c2")));
    assertEquals(-1, instance.compareTo(examples.get("c3")));
    assertEquals(-3, instance.compareTo(examples.get("c4")));
    assertEquals(-3, instance.compareTo(examples.get("c5")));
  }

  /**
   * Test compareTo against a example vector b4.
   */
  @Test
  public void testCompareTo_examples_b4() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("b4");

    assertEquals(3, instance.compareTo(examples.get("a0")));
    assertEquals(2, instance.compareTo(examples.get("a1")));
    assertEquals(1, instance.compareTo(examples.get("a2")));
    assertEquals(0, instance.compareTo(examples.get("a3")));
    assertEquals(-3, instance.compareTo(examples.get("a4")));
    assertEquals(3, instance.compareTo(examples.get("b0")));
    assertEquals(2, instance.compareTo(examples.get("b1")));
    assertEquals(2, instance.compareTo(examples.get("b2")));
    assertEquals(2, instance.compareTo(examples.get("b3")));
    assertEquals(0, instance.compareTo(examples.get("b4")));
    assertEquals(-1, instance.compareTo(examples.get("b5")));
    assertEquals(3, instance.compareTo(examples.get("c0")));
    assertEquals(2, instance.compareTo(examples.get("c1")));
    assertEquals(0, instance.compareTo(examples.get("c2")));
    assertEquals(0, instance.compareTo(examples.get("c3")));
    assertEquals(-2, instance.compareTo(examples.get("c4")));
    assertEquals(-2, instance.compareTo(examples.get("c5")));
  }

  /**
   * Test compareTo against a example vector b5.
   */
  @Test
  public void testCompareTo_examples_b5() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("b5");

    assertEquals(3, instance.compareTo(examples.get("a0")));
    assertEquals(2, instance.compareTo(examples.get("a1")));
    assertEquals(1, instance.compareTo(examples.get("a2")));
    assertEquals(0, instance.compareTo(examples.get("a3")));
    assertEquals(-2, instance.compareTo(examples.get("a4")));
    assertEquals(3, instance.compareTo(examples.get("b0")));
    assertEquals(2, instance.compareTo(examples.get("b1")));
    assertEquals(2, instance.compareTo(examples.get("b2")));
    assertEquals(2, instance.compareTo(examples.get("b3")));
    assertEquals(1, instance.compareTo(examples.get("b4")));
    assertEquals(0, instance.compareTo(examples.get("b5")));
    assertEquals(3, instance.compareTo(examples.get("c0")));
    assertEquals(2, instance.compareTo(examples.get("c1")));
    assertEquals(0, instance.compareTo(examples.get("c2")));
    assertEquals(0, instance.compareTo(examples.get("c3")));
    assertEquals(-1, instance.compareTo(examples.get("c4")));
    assertEquals(-1, instance.compareTo(examples.get("c5")));
  }

  /**
   * Test compareTo against a example vector c0.
   */
  @Test
  public void testCompareTo_examples_c0() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("c0");

    assertEquals(0, instance.compareTo(examples.get("a0")));
    assertEquals(-3, instance.compareTo(examples.get("a1")));
    assertEquals(-3, instance.compareTo(examples.get("a2")));
    assertEquals(-3, instance.compareTo(examples.get("a3")));
    assertEquals(-3, instance.compareTo(examples.get("a4")));
    assertEquals(0, instance.compareTo(examples.get("b0")));
    assertEquals(-2, instance.compareTo(examples.get("b1")));
    assertEquals(-2, instance.compareTo(examples.get("b2")));
    assertEquals(-2, instance.compareTo(examples.get("b3")));
    assertEquals(-3, instance.compareTo(examples.get("b4")));
    assertEquals(-3, instance.compareTo(examples.get("b5")));
    assertEquals(0, instance.compareTo(examples.get("c0")));
    assertEquals(-1, instance.compareTo(examples.get("c1")));
    assertEquals(-2, instance.compareTo(examples.get("c2")));
    assertEquals(-2, instance.compareTo(examples.get("c3")));
    assertEquals(-3, instance.compareTo(examples.get("c4")));
    assertEquals(-3, instance.compareTo(examples.get("c5")));
  }

  /**
   * Test compareTo against a example vector c1.
   */
  @Test
  public void testCompareTo_examples_c1() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("c1");

    assertEquals(1, instance.compareTo(examples.get("a0")));
    assertEquals(-2, instance.compareTo(examples.get("a1")));
    assertEquals(-2, instance.compareTo(examples.get("a2")));
    assertEquals(-3, instance.compareTo(examples.get("a3")));
    assertEquals(-3, instance.compareTo(examples.get("a4")));
    assertEquals(1, instance.compareTo(examples.get("b0")));
    assertEquals(-1, instance.compareTo(examples.get("b1")));
    assertEquals(-1, instance.compareTo(examples.get("b2")));
    assertEquals(-1, instance.compareTo(examples.get("b3")));
    assertEquals(-2, instance.compareTo(examples.get("b4")));
    assertEquals(-2, instance.compareTo(examples.get("b5")));
    assertEquals(1, instance.compareTo(examples.get("c0")));
    assertEquals(0, instance.compareTo(examples.get("c1")));
    assertEquals(-2, instance.compareTo(examples.get("c2")));
    assertEquals(-2, instance.compareTo(examples.get("c3")));
    assertEquals(-3, instance.compareTo(examples.get("c4")));
    assertEquals(-3, instance.compareTo(examples.get("c5")));
  }

  /**
   * Test compareTo against a example vector c2.
   */
  @Test
  public void testCompareTo_examples_c2() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("c2");

    assertEquals(2, instance.compareTo(examples.get("a0")));
    assertEquals(0, instance.compareTo(examples.get("a1")));
    assertEquals(0, instance.compareTo(examples.get("a2")));
    assertEquals(-2, instance.compareTo(examples.get("a3")));
    assertEquals(-3, instance.compareTo(examples.get("a4")));
    assertEquals(2, instance.compareTo(examples.get("b0")));
    assertEquals(2, instance.compareTo(examples.get("b1")));
    assertEquals(2, instance.compareTo(examples.get("b2")));
    assertEquals(1, instance.compareTo(examples.get("b3")));
    assertEquals(0, instance.compareTo(examples.get("b4")));
    assertEquals(0, instance.compareTo(examples.get("b5")));
    assertEquals(2, instance.compareTo(examples.get("c0")));
    assertEquals(2, instance.compareTo(examples.get("c1")));
    assertEquals(0, instance.compareTo(examples.get("c2")));
    assertEquals(-1, instance.compareTo(examples.get("c3")));
    assertEquals(-3, instance.compareTo(examples.get("c4")));
    assertEquals(-3, instance.compareTo(examples.get("c5")));
  }

  /**
   * Test compareTo against a example vector c3.
   */
  @Test
  public void testCompareTo_examples_c3() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("c3");

    assertEquals(2, instance.compareTo(examples.get("a0")));
    assertEquals(0, instance.compareTo(examples.get("a1")));
    assertEquals(0, instance.compareTo(examples.get("a2")));
    assertEquals(-1, instance.compareTo(examples.get("a3")));
    assertEquals(-3, instance.compareTo(examples.get("a4")));
    assertEquals(2, instance.compareTo(examples.get("b0")));
    assertEquals(2, instance.compareTo(examples.get("b1")));
    assertEquals(2, instance.compareTo(examples.get("b2")));
    assertEquals(1, instance.compareTo(examples.get("b3")));
    assertEquals(0, instance.compareTo(examples.get("b4")));
    assertEquals(0, instance.compareTo(examples.get("b5")));
    assertEquals(2, instance.compareTo(examples.get("c0")));
    assertEquals(2, instance.compareTo(examples.get("c1")));
    assertEquals(1, instance.compareTo(examples.get("c2")));
    assertEquals(0, instance.compareTo(examples.get("c3")));
    assertEquals(-3, instance.compareTo(examples.get("c4")));
    assertEquals(-3, instance.compareTo(examples.get("c5")));
  }

  /**
   * Test compareTo against a example vector c4.
   */
  @Test
  public void testCompareTo_examples_c4() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("c4");

    assertEquals(3, instance.compareTo(examples.get("a0")));
    assertEquals(3, instance.compareTo(examples.get("a1")));
    assertEquals(2, instance.compareTo(examples.get("a2")));
    assertEquals(0, instance.compareTo(examples.get("a3")));
    assertEquals(-2, instance.compareTo(examples.get("a4")));
    assertEquals(3, instance.compareTo(examples.get("b0")));
    assertEquals(3, instance.compareTo(examples.get("b1")));
    assertEquals(3, instance.compareTo(examples.get("b2")));
    assertEquals(3, instance.compareTo(examples.get("b3")));
    assertEquals(2, instance.compareTo(examples.get("b4")));
    assertEquals(1, instance.compareTo(examples.get("b5")));
    assertEquals(3, instance.compareTo(examples.get("c0")));
    assertEquals(3, instance.compareTo(examples.get("c1")));
    assertEquals(3, instance.compareTo(examples.get("c2")));
    assertEquals(3, instance.compareTo(examples.get("c3")));
    assertEquals(0, instance.compareTo(examples.get("c4")));
    assertEquals(-1, instance.compareTo(examples.get("c5")));
  }

  /**
   * Test compareTo against a example vector c5.
   */
  @Test
  public void testCompareTo_examples_c5() {
    Map<String, AbstractVersionVector<String, Integer>> examples = createExamples();

    AbstractVersionVector<String, Integer> instance = examples.get("c5");

    assertEquals(3, instance.compareTo(examples.get("a0")));
    assertEquals(3, instance.compareTo(examples.get("a1")));
    assertEquals(2, instance.compareTo(examples.get("a2")));
    assertEquals(0, instance.compareTo(examples.get("a3")));
    assertEquals(-1, instance.compareTo(examples.get("a4")));
    assertEquals(3, instance.compareTo(examples.get("b0")));
    assertEquals(3, instance.compareTo(examples.get("b1")));
    assertEquals(3, instance.compareTo(examples.get("b2")));
    assertEquals(3, instance.compareTo(examples.get("b3")));
    assertEquals(2, instance.compareTo(examples.get("b4")));
    assertEquals(1, instance.compareTo(examples.get("b5")));
    assertEquals(3, instance.compareTo(examples.get("c0")));
    assertEquals(3, instance.compareTo(examples.get("c1")));
    assertEquals(3, instance.compareTo(examples.get("c2")));
    assertEquals(3, instance.compareTo(examples.get("c3")));
    assertEquals(1, instance.compareTo(examples.get("c4")));
    assertEquals(0, instance.compareTo(examples.get("c5")));
  }

  /**
   * Test if a vector constructed as dotted, is dotted.
   */
  @Test
  public void testIsDotted_True() {
    LOGGER.log(Level.INFO, "testIsDotted_True: Test if a vector constructed as dotted, is dotted");

    AbstractVersionVector<Object, Integer> instance = new TestVersionVector(true);

    assertTrue("Vector constructed as dotted, should be dotted.", instance.isDotted());
  }

  /**
   * Test if a vector constructed as not dotted, is not dotted.
   */
  @Test
  public void testIsDotted_False() {
    LOGGER.log(Level.INFO,
        "testIsDotted_True: Test if a vector constructed as not dotted, is not dotted");

    AbstractVersionVector<Object, Integer> instance = new TestVersionVector(false);

    assertFalse("Vector constructed as not dotted, should not be dotted.", instance.isDotted());
  }

  public static class TestVersionVector extends AbstractVersionVector<Object, Integer> {

    public TestVersionVector(boolean dotted) {
      super(0, dotted);
    }

    public TestVersionVector() {
      this(false);
    }

    @Override
    protected LogicalVersion<Integer> getInternal(Object id) {
      throw new UnsupportedOperationException("Out of test scope.");
    }

    @Override
    public AbstractVersionVector<Object, Integer> copy() {
      throw new UnsupportedOperationException("Out of test scope.");
    }

    @Override
    public Set<Object> getIdentifiers() {
      throw new UnsupportedOperationException("Out of test scope.");
    }

    @Override
    public void init(Object id) {
      throw new UnsupportedOperationException("Out of test scope.");
    }

    @Override
    public void sync(Object id, Integer value) {
      throw new UnsupportedOperationException("Out of test scope.");
    }
  };

  public static class ImmutableMapVersionVector<K, T extends Comparable<T>>
      extends AbstractVersionVector<K, T> {

    private final Map<K, T> vector;

    public ImmutableMapVersionVector(Map<K, T> vector, T zero, boolean dotted) {
      super(zero, dotted);
      this.vector = new HashMap<>(vector);
    }

    @Override
    protected LogicalVersion<T> getInternal(final K id) {
      return new AbstractLogicalVersion<T>() {
        @Override
        public AbstractLogicalVersion<T> copy() {
          // This is immutable, just use the same object.
          return this;
        }

        @Override
        public T get() {
          return vector.get(id);
        }

        @Override
        public void sync(T timestamp) {
          throw new UnsupportedOperationException("Cannot be mutated.");
        }

        @Override
        public void increment() throws VersionOverflowException {
          throw new UnsupportedOperationException("Cannot be mutated.");
        }
      };
    }

    @Override
    public Set<K> getIdentifiers() {
      return vector.keySet();
    }

    @Override
    public AbstractVersionVector<K, T> copy() {
      // This is immutable, just use the same object.
      return this;
    }

    @Override
    public void init(K id) {
      throw new UnsupportedOperationException("Cannot be mutated.");
    }

    @Override
    public void sync(K id, T value) {
      throw new UnsupportedOperationException("Cannot be mutated.");
    }

  }

}
