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

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;
import org.junit.rules.Timeout;

/**
 * Utilities to assist with building tests.
 */
public class TestUtil {

  // Utils class cannot be instatiated
  private TestUtil() {};

  /**
   * Creates a JUnit time out as long as the JVM is not being run inside of a debugger. When
   * debugging, we don't want timeouts.
   *
   * @param timeout the maximum time to allow the test to run before it should timeout
   * @param timeUnit the time unit for the {@code timeout}
   * @return a new Timeout as requested or {@code null} if running inside of a debugger session.
   */
  public static Timeout getTimeout(long timeout, TimeUnit timeUnit) {
    if (ManagementFactory.getRuntimeMXBean().getInputArguments().contains("-Xdebug")) {
      return null;
    } else {
      return new Timeout(timeout, timeUnit);
    }
  }

}
