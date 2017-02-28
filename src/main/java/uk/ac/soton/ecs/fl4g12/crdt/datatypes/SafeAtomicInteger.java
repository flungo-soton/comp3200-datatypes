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

package uk.ac.soton.ecs.fl4g12.crdt.datatypes;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Extension of {@linkplain AtomicInteger} providing safe methods.
 *
 * @see AtomicInteger for basic information and other.
 */
public class SafeAtomicInteger extends AtomicInteger {

  public SafeAtomicInteger(int initialValue) {
    super(initialValue);
  }

  public SafeAtomicInteger() {}

  /**
   * Atomically and safely increments by one the current value.
   *
   * @return the updated value.
   */
  public final int safeIncrementAndGet() {
    while (true) {
      int current = get();
      int next = current + 1;
      // Detect overflow
      if (next == Integer.MIN_VALUE) {
        throw new ArithmeticException("Increment overflow");
      }
      if (compareAndSet(current, next)) {
        return next;
      }
    }
  }

  /**
   * Atomically and safely decrements by one the current value.
   *
   * @return the updated value.
   */
  public final int safeDecrementAndGet() {
    while (true) {
      int current = get();
      int next = current + 1;
      // Detect overflow
      if (next == Integer.MAX_VALUE) {
        throw new ArithmeticException("Decrement underflow");
      }
      if (compareAndSet(current, next)) {
        return next;
      }
    }
  }

  /**
   * Atomically and safely increments by one the current value.
   *
   * @return the previous value.
   */
  public final int safeGetAndIncrement() {
    while (true) {
      int current = get();
      int next = current + 1;
      // Detect overflow
      if (next == Integer.MIN_VALUE) {
        throw new ArithmeticException("Increment overflow");
      }
      if (compareAndSet(current, next)) {
        return current;
      }
    }
  }

  /**
   * Atomically and safely decrements by one the current value.
   *
   * @return the previous value.
   */
  public final int safeGetAndDecrement() {
    while (true) {
      int current = get();
      int next = current + 1;
      // Detect overflow
      if (next == Integer.MAX_VALUE) {
        throw new ArithmeticException("Decrement underflow");
      }
      if (compareAndSet(current, next)) {
        return current;
      }
    }
  }
}
