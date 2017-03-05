/*
 * The MIT License
 *
 * Copyright 2016 Fabrizio Lungo <fl4g12@ecs.soton.ac.uk>.
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

import java.io.Serializable;
import java.util.Comparator;
import javax.naming.OperationNotSupportedException;

/**
 * An interface for simple counters. Counters can be incremented and decremented.
 *
 * An increment-only counter can be created by throwing an {@link OperationNotSupportedException}
 * when attempting to {@link #decrement()}. decrement-only counters should not be implemented as the
 * value of an increment only counter can be
 *
 * @param <E> the type of the countable element.
 */
public interface Counter<E> {

  /**
   * Increment the counter by one unit.
   */
  void increment();

  /**
   * Decrement the counter by one unit.
   */
  void decrement();

  /**
   * Get the current value of the counter.
   *
   * @return the current value of the counter.
   */
  E value();

  static class CounterValueComparator<T extends Comparable<T>>
      implements Comparator<Counter<T>>, Serializable {

    @Override
    public int compare(Counter<T> o1, Counter<T> o2) {
      return o1.value().compareTo(o2.value());
    }
  }

}
