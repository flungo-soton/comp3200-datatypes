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

package uk.ac.soton.ecs.fl4g12.crdt.util;

import java.util.Collection;

/**
 * Utilities for testing concurrent operations.
 */
public class ConcurrencyTestUtil {

  // Util class cannot be instatiated
  private ConcurrencyTestUtil() {};

  /**
   * Start all threads contained within the {@linkplain Collection}.
   *
   * @param threads the {@linkplain Collection} of threads to be started.
   */
  public static void startAll(Collection<Thread> threads) {
    for (Thread thread : threads) {
      thread.start();
    }
  }

  /**
   * Join all threads contained within the {@linkplain Collection}.
   *
   * @param threads the {@linkplain Collection} of threads to join with.
   */
  public static void joinAll(Collection<Thread> threads) throws InterruptedException {
    for (Thread thread : threads) {
      thread.join();
    }
  }
}
