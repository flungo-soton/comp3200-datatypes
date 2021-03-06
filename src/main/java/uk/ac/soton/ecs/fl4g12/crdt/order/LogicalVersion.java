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

package uk.ac.soton.ecs.fl4g12.crdt.order;

/**
 * A {@linkplain LogicalVersion} is a simple {@linkplain Version} which can be incremented. The type
 * of the timestamp is comparable and the ordering of the timestamp values provides the partial
 * order of the timestamps.
 *
 * @param <T> the type of the timestamp.
 * @param <V> the type of {@linkplain LogicalVersion} which this {@linkplain LogicalVersion} can
 *        perform operations with.
 */
public interface LogicalVersion<T, V extends LogicalVersion<T, V>>
    extends Version<T, LogicalVersion<T, ?>, V> {

  /**
   * Increment the {@linkplain LogicalVersion}'s timestamp. Typically increments should be of the
   * same amount each time and of the smallest incrementable amount.
   *
   * @throws ArithmeticException when incremented beyond the maximum value for the version.
   */
  void increment() throws ArithmeticException;

  /**
   * Get what the value of the {@linkplain LogicalVersion} would be after an increment. Does not
   * alter the state of the {@linkplain LogicalVersion}.
   *
   * @return the value of the {@linkplain LogicalVersion} after an increment.
   */
  T successor();

  V getZero();

}
