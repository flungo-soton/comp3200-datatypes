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

/**
 * {@linkplain Arithmetic} implementation for {@linkplain Long} objects.
 */
public final class LongArithmetic extends AbstractArithmetic<Long> {

  private LongArithmetic() {}

  @Override
  public Long add(Iterable<Long> elements) {
    long accumulator = 0;
    for (Long i : elements) {
      accumulator += i;
    }
    return accumulator;
  }

  @Override
  public Long sub(Long value, Iterable<Long> elements) {
    long accumulator = value;
    for (Long i : elements) {
      accumulator -= i;
    }
    return accumulator;
  }

  /**
   * Get the instance of {@linkplain LongArithmetic}. {@linkplain LongArithmetic} is a singleton and
   * this method returns the single instance.
   *
   * @return the {@linkplain LongArithmetic} instance.
   */
  public static LongArithmetic getInstance() {
    return LongArithmeticHolder.INSTANCE;
  }

  private static class LongArithmeticHolder {
    private static final LongArithmetic INSTANCE = new LongArithmetic();
  }
}
