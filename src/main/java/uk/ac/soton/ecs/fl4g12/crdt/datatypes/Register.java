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

/**
 * Interface for basic registers. Registers are used to store a value.
 *
 * @param <T> the type of values stored by the register.
 */
public interface Register<T> {

  /**
   * Set the value of the register.
   *
   * @param value the new value of the register.
   */
  void assign(T value);

  /**
   * Get the value of the register.
   *
   * @return the value of the register.
   */
  T value();

  /**
   * A comparator for comparing the value of {@linkplain Register}s which contain
   * {@linkplain Comparable} objects.
   *
   * @param <T> the type of values stored by the register.
   */
  class RegisterValueComparator<T extends Comparable<T>>
      implements Comparator<Register<T>>, Serializable {

    @Override
    public int compare(Register<T> o1, Register<T> o2) {
      return o1.value().compareTo(o2.value());
    }
  }
}
