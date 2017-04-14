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

package uk.ac.soton.ecs.fl4g12.crdt.datatypes;

/**
 * Interface providing methods that can be used for creating abstract {@link Counter} tests.
 *
 * @param <E> the type of counter value that the test uses.
 * @param <C> the type of the counter being tested.
 */
public interface CounterTestInterface<E, C extends Counter<E>> {

  /**
   * Get the counter instance for testing.
   *
   * @return a counter to be tested.
   */
  C getCounter();

  /**
   *
   * @param count the number of increments.
   * @return the value the counter should have after the specified number of increments.
   */
  E getValue(int count);

  /**
   * Get the value that a counter should have after a given number of increments and decrements.
   *
   * @param increments the number of increments.
   * @param decrements the number of decrements.
   * @return the expected value of the counter
   */
  E getValue(int increments, int decrements);

}
