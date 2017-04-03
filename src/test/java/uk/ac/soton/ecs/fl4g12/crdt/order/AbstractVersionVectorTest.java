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
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for {@linkplain AbstractVersionTest}.
 */
public class AbstractVersionVectorTest
    extends VersionVectorAbstractTest<Integer, VersionVector<Integer, Integer>> {

  private static final Logger LOGGER = Logger.getLogger(AbstractVersionTest.class.getName());

  public AbstractVersionVectorTest() {
    super(false);
  }

  @Override
  protected VersionVector<Integer, Integer> getVersion(String id) {
    return new ImmutableMapVersionVector<>(getTimestamp(id), new IntegerVersion());
  }

  @Override
  public VersionVector<Integer, Integer> getVersion(int order) {
    return new ImmutableMapVersionVector<>(getTimestamp(order), new IntegerVersion());
  }

  @Override
  protected Integer getKey(int index) {
    return index;
  }

  /**
   * Test that get uses the getLogicalVersion method.
   */
  @Test
  public void testGet_Initialised() {
    LOGGER.log(Level.INFO, "testGet_Initialised: Test that get uses the getLogicalVersion method");

    // Constants for the test
    final Object id = new Object();
    final Integer value = 1;

    // Setup the mock
    AbstractVersionVector instance = Mockito.spy(new TestVersionVector());
    LogicalVersion logicalVersion = Mockito.mock(LogicalVersion.class);
    Mockito.doReturn(logicalVersion).when(instance).getLogicalVersion(id);
    Mockito.doReturn(value).when(logicalVersion).get();

    // Test the get method
    Object result = instance.get(id);
    Mockito.verify(instance).getLogicalVersion(id);
    Mockito.verify(logicalVersion).get();
    assertEquals("The value returned should be the expected value.", value, result);
  }

  /**
   * Test that get returns zero when uninitialised.
   */
  @Test
  public void testGet_Uninitialised() {
    LOGGER.log(Level.INFO, "testGet_Uninitialised: Test that get returns zero when uninitialised");

    // Constants for the test
    final Object id = new Object();
    final Integer value = 0;

    // Setup the mock
    AbstractVersionVector instance = Mockito.spy(new TestVersionVector());
    Mockito.doReturn(null).when(instance).getLogicalVersion(Mockito.any());

    // Test the get method
    Object result = instance.get(id);
    Mockito.verify(instance).getLogicalVersion(id);
    assertEquals("The value returned should be the expected value.", value, result);
  }

  /**
   * Test that get with no-args returns a constructed map.
   */
  @Test
  public void testGet_0args() {
    LOGGER.log(Level.INFO,
        "testGet_GenericType: Test that get with no-args returns a constructed map");

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
    AbstractVersionVector instance = Mockito.spy(new TestVersionVector());
    LogicalVersion logicalVersion1 = Mockito.mock(LogicalVersion.class);
    LogicalVersion logicalVersion2 = Mockito.mock(LogicalVersion.class);
    LogicalVersion logicalVersion3 = Mockito.mock(LogicalVersion.class);
    Mockito.doReturn(logicalVersion1).when(instance).getLogicalVersion(id1);
    Mockito.doReturn(logicalVersion2).when(instance).getLogicalVersion(id2);
    Mockito.doReturn(logicalVersion3).when(instance).getLogicalVersion(id3);
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

    // Constants for the test
    final Object id = new Object();

    // Setup the mock
    AbstractVersionVector instance = Mockito.spy(new TestVersionVector());
    Mockito.doReturn(new HashSet(0)).when(instance).getIdentifiers();
    Mockito.doReturn(null).when(instance).getLogicalVersion(id);

    // Test the method
    thrown.expect(IllegalArgumentException.class);
    instance.increment(id);
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
    AbstractVersionVector instance = Mockito.spy(new TestVersionVector());
    LogicalVersion logicalVersion1 = Mockito.mock(LogicalVersion.class);
    LogicalVersion logicalVersion2 = Mockito.mock(LogicalVersion.class);
    LogicalVersion logicalVersion3 = Mockito.mock(LogicalVersion.class);
    Mockito.doReturn(logicalVersion1).when(instance).getLogicalVersion(id1);
    Mockito.doReturn(logicalVersion2).when(instance).getLogicalVersion(id2);
    Mockito.doReturn(logicalVersion3).when(instance).getLogicalVersion(id3);
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
    AbstractVersionVector instance = Mockito.spy(new TestVersionVector());
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
   * Test happenedBefore with identical vectors.
   */
  @Test
  public void testHappenedBefore_Identical() {
    LOGGER.log(Level.INFO,
        "testHappenedBefore_Identical: Test happenedBefore with identical vectors");

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

    // Compare
    boolean expResult = true;
    boolean result1 = abstractVersionVector1.happenedBefore(abstractVersionVector2);
    boolean result2 = abstractVersionVector2.happenedBefore(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(!expResult, result2);
  }

  /**
   * Test happenedBefore with a identical vectors (one with an implicit zero value).
   */
  @Test
  public void testHappenedBefore_ImplicitZeroIdentical() {
    LOGGER.log(Level.INFO, "testHappenedBefore_ImplicitZeroIdentical: "
        + "Test happenedBefore with a identical vectors (one with an implicit zero value)");

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

    // Compare
    boolean expResult = true;
    boolean result1 = abstractVersionVector1.happenedBefore(abstractVersionVector2);
    boolean result2 = abstractVersionVector2.happenedBefore(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(!expResult, result2);
  }

  /**
   * Test concurrentWith for identical vectors.
   */
  @Test
  public void testConcurrentWith_Identical() {
    LOGGER.log(Level.INFO,
        "testConcurrentWith_Identical: Test concurrentWith for identical vectors");

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

    // Compare
    boolean expResult = false;
    boolean result1 = abstractVersionVector1.concurrentWith(abstractVersionVector2);
    boolean result2 = abstractVersionVector2.concurrentWith(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(expResult, result2);
  }

  /**
   * Test concurrentWith with a identical vectors (one with an implicit zero value).
   */
  @Test
  public void testConcurrentWith_ImplicitZeroIdentical() {
    LOGGER.log(Level.INFO, "testConcurrentWith_ImplicitZeroIdentical: "
        + "Test concurrentWith with a identical vectors (one with an implicit zero value)");

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

    // Compare
    boolean expResult = false;
    boolean result1 = abstractVersionVector1.concurrentWith(abstractVersionVector2);
    boolean result2 = abstractVersionVector2.concurrentWith(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(expResult, result2);
  }

  /**
   * Test compareTo with identical vectors.
   */
  @Test
  public void testCompareTo_Identical() {
    LOGGER.log(Level.INFO, "testCompareTo_Identical: Test compareTo with identical vectors");

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

    // Compare
    int expResult = -3;
    int result1 = abstractVersionVector1.compareTo(abstractVersionVector2);
    int result2 = abstractVersionVector2.compareTo(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(-expResult, result2);
  }

  /**
   * Test compareTo with a identical vectors (one with an implicit zero value).
   */
  @Test
  public void testCompareTo_ImplicitZeroIdentical() {
    LOGGER.log(Level.INFO, "testCompareTo_ImplicitZeroIdentical: "
        + "Test compareTo with a identical vectors (one with an implicit zero value)");

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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

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
    LOGGER.log(Level.INFO, "testCompareTo_ImplicitZeroIdentical: "
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
        new ImmutableMapVersionVector<>(vector1, new IntegerVersion());
    AbstractVersionVector<Object, Integer> abstractVersionVector2 =
        new ImmutableMapVersionVector<>(vector2, new IntegerVersion());

    // Compare
    int expResult = 1;
    int result1 = abstractVersionVector1.compareTo(abstractVersionVector2);
    int result2 = abstractVersionVector2.compareTo(abstractVersionVector1);

    // Make assertions
    assertEquals(expResult, result1);
    assertEquals(-expResult, result2);
  }

  public static class TestVersionVector extends AbstractVersionVector<Object, Integer> {

    public TestVersionVector() {
      super(new IntegerVersion());
    }

    @Override
    public LogicalVersion<Integer, ?> getLogicalVersion(Object id) {
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
    public LogicalVersion<Integer, ?> init(Object id) {
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
    private final LamportTimestamp<T> zero;

    public ImmutableMapVersionVector(Map<K, T> vector, LamportTimestamp<T> zero) {
      super(zero);
      this.vector = new HashMap<>(vector);
      this.zero = zero.copy();
    }

    @Override
    public LogicalVersion<T, ?> getLogicalVersion(final K id) {
      if (!vector.containsKey(id)) {
        return null;
      }
      return new AbstractLamportTimestamp<T>(zero) {
        @Override
        public LamportTimestamp<T> copy() {
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
        public void increment() {
          throw new UnsupportedOperationException("Cannot be mutated.");
        }

        @Override
        public T successor() {
          LogicalVersion<T, ?> successor = zero.copy();
          successor.sync(vector.get(id));
          return successor.successor();
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
    public LogicalVersion<T, ?> init(K id) {
      throw new UnsupportedOperationException("Cannot be mutated.");
    }

    @Override
    public void sync(K id, T value) {
      throw new UnsupportedOperationException("Cannot be mutated.");
    }
  }

  /*
   * Disable the out of scope tests that have been inherited.
   */

  @Override
  public void testSync_Version_0() {}

  @Override
  public void testSync_Version_1() {}

  @Override
  public void testSync_Version_2() {}

  @Override
  public void testSync_Version_3() {}

  @Override
  public void testSync_Version_4() {}

  @Override
  public void testSync_Version_5() {}

  @Override
  public void testSync_Version_6() {}

  @Override
  public void testSync_Version_7() {}

  @Override
  public void testSync_Version_8() {}

  @Override
  public void testSync_Version_9() {}

  @Override
  public void testSync_Timestamp_0() {}

  @Override
  public void testSync_Timestamp_1() {}

  @Override
  public void testSync_Timestamp_2() {}

  @Override
  public void testSync_Timestamp_3() {}

  @Override
  public void testSync_Timestamp_4() {}

  @Override
  public void testSync_Timestamp_5() {}

  @Override
  public void testSync_Timestamp_6() {}

  @Override
  public void testSync_Timestamp_7() {}

  @Override
  public void testSync_Timestamp_8() {}

  @Override
  public void testSync_Timestamp_9() {}

  @Override
  public void testSync_Dot_0() {}

  @Override
  public void testSync_Dot_1() {}

  @Override
  public void testSync_Dot_2() {}

  @Override
  public void testSync_Dot_3() {}

  @Override
  public void testSync_Dot_4() {}

  @Override
  public void testSync_Dot_5() {}

  @Override
  public void testSync_Dot_6() {}

  @Override
  public void testSync_Dot_7() {}

  @Override
  public void testSync_Dot_8() {}

  @Override
  public void testSync_Dot_9() {}

  @Override
  public void testCopy_0() {}

  @Override
  public void testCopy_1() {}

  @Override
  public void testCopy_2() {}

  @Override
  public void testCopy_3() {}

  @Override
  public void testCopy_4() {}

  @Override
  public void testCopy_5() {}

  @Override
  public void testCopy_6() {}

  @Override
  public void testCopy_7() {}

  @Override
  public void testCopy_8() {}

  @Override
  public void testCopy_9() {}

  @Override
  public void testInit_0() {}

  @Override
  public void testInit_1() {}

  @Override
  public void testInit_2() {}

  @Override
  public void testInit_3() {}

  @Override
  public void testInit_4() {}

  @Override
  public void testInit_5() {}

  @Override
  public void testInit_6() {}

  @Override
  public void testInit_7() {}

  @Override
  public void testInit_8() {}

  @Override
  public void testInit_9() {}

  @Override
  public void testIncrement_0() {}

  @Override
  public void testIncrement_1() {}

  @Override
  public void testIncrement_2() {}

  @Override
  public void testIncrement_3() {}

  @Override
  public void testIncrement_4() {}

  @Override
  public void testIncrement_5() {}

  @Override
  public void testIncrement_6() {}

  @Override
  public void testIncrement_7() {}

  @Override
  public void testIncrement_8() {}

  @Override
  public void testIncrement_9() {}

}
