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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple {@linkplain LogicalVersion} where the timestamp is an incrementing integer. Uses an
 * {@link AtomicInteger} to ensure the thread safety of the version.
 */
public class IntegerVersion extends AbstractLogicalVersion<Integer> {

  private final AtomicInteger timestamp;

  /**
   * Construct a zero-initialised {@linkplain IntegerVersion}.
   */
  public IntegerVersion() {
    timestamp = new AtomicInteger();
  }

  /**
   * Construct an {@linkplain IntegerVersion} with a given initial timestamp. This is designed only
   * to be used for testing and cloning.
   *
   * @param timestamp the initial value of the {@linkplain IntegerVersion}.
   */
  IntegerVersion(int timestamp) {
    this.timestamp = new AtomicInteger(timestamp);
  }

  @Override
  public Integer get() {
    return timestamp.get();
  }

  @Override
  public void increment() {
    timestamp.incrementAndGet();
  }

  @Override
  public void sync(Integer other) {
    while (true) {
      Integer self = get();
      // Is the other clock ahead
      if (other <= self) {
        // Nothing to do
        return;
      }
      // Try to set, uses compare and set to ensure that the previous gaurd is still valid.
      if (timestamp.compareAndSet(self, other)) {
        return;
      }
    }
  }

  @Override
  public IntegerVersion copy() {
    return new IntegerVersion(get());
  }

}
