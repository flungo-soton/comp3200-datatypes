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

package uk.ac.soton.ecs.fl4g12.crdt.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Abstract tests for {@linkplain Arithmetic} implementations.
 */
public abstract class ArithmeticAbstractTest<T, A extends Arithmetic<T>> {

  private static final Logger LOGGER = Logger.getLogger(ArithmeticAbstractTest.class.getName());

  protected abstract Arithmetic<T> getInstance();

  /**
   * Get a value to be used as function argument in tests.
   *
   * @param testCase the test case that the value will be used on.
   * @param i the argument index that the value will be used as.
   * @return a value to be used as the {@code i}th index of the function being tested in the
   *         {@code testCase}.
   */
  protected abstract T getValue(TestCase testCase, int i);

  /**
   * Get a dataset to be used during a test.
   *
   * @param dataset the dataset to get for the test.
   * @return the dataset to be used for the test.
   */
  protected abstract T[] getData(Dataset dataset);

  /**
   * Get the expected result for the given {@linkplain TestCase} and {@linkplain Dataset}.
   *
   * @param testCase the test case to get the result of.
   * @param dataset the dataset that was used during the test.
   * @return the expected result for the given {@link TestCase} and {@linkplain Dataset}
   */
  protected abstract T getResult(TestCase testCase, Dataset dataset);

  /**
   * Test of add method, of the {@linkplain Arithmetic} implementation with the
   * {@linkplain Dataset#POSITIVE} {@linkplain Dataset}.
   */
  @Test
  public void testAdd_Positive() {
    LOGGER.log(Level.INFO, "testAdd_Positive");

    T[] dataset = getData(Dataset.POSITIVE);
    Arithmetic<T> instance = getInstance();

    assertEquals(getResult(TestCase.ADD, Dataset.POSITIVE), instance.add(dataset));
  }

  /**
   * Test of add method, of the {@linkplain Arithmetic} implementation with the
   * {@linkplain Dataset#NEGATIVE} {@linkplain Dataset}.
   */
  @Test
  public void testAdd_Negative() {
    LOGGER.log(Level.INFO, "testAdd_Negative");

    T[] dataset = getData(Dataset.NEGATIVE);
    Arithmetic<T> instance = getInstance();

    assertEquals(getResult(TestCase.ADD, Dataset.NEGATIVE), instance.add(dataset));
  }

  /**
   * Test of add method, of the {@linkplain Arithmetic} implementation with the
   * {@linkplain Dataset#MIXED} {@linkplain Dataset}.
   */
  @Test
  public void testAdd_Mixed() {
    LOGGER.log(Level.INFO, "testAdd_Mixed");

    T[] dataset = getData(Dataset.MIXED);
    Arithmetic<T> instance = getInstance();

    assertEquals(getResult(TestCase.ADD, Dataset.MIXED), instance.add(dataset));
  }

  /**
   * Test of sub method, of the {@linkplain Arithmetic} implementation with the
   * {@linkplain Dataset#POSITIVE} {@linkplain Dataset}.
   */
  @Test
  public void testSub_Positive() {
    LOGGER.log(Level.INFO, "testSub_Positive");

    T value = getValue(TestCase.SUB, 0);
    T[] dataset = getData(Dataset.POSITIVE);
    Arithmetic<T> instance = getInstance();

    assertEquals(getResult(TestCase.SUB, Dataset.POSITIVE), instance.sub(value, dataset));
  }

  /**
   * Test of sub method, of the {@linkplain Arithmetic} implementation with the
   * {@linkplain Dataset#NEGATIVE} {@linkplain Dataset}.
   */
  @Test
  public void testSub_Negative() {
    LOGGER.log(Level.INFO, "testSub_Negative");

    T value = getValue(TestCase.SUB, 0);
    T[] dataset = getData(Dataset.NEGATIVE);
    Arithmetic<T> instance = getInstance();

    assertEquals(getResult(TestCase.SUB, Dataset.NEGATIVE), instance.sub(value, dataset));
  }

  /**
   * Test of sub method, of the {@linkplain Arithmetic} implementation with the
   * {@linkplain Dataset#MIXED} {@linkplain Dataset}.
   */
  @Test
  public void testSub_Mixed() {
    LOGGER.log(Level.INFO, "testSub_Mixed");

    T value = getValue(TestCase.SUB, 0);
    T[] dataset = getData(Dataset.MIXED);
    Arithmetic<T> instance = getInstance();

    assertEquals(getResult(TestCase.SUB, Dataset.MIXED), instance.sub(value, dataset));
  }

  public enum Dataset {
    POSITIVE, NEGATIVE, MIXED
  }

  public enum TestCase {
    ADD, SUB
  }

}
