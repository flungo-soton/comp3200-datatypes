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
 * Tests for {@linkplain LongArithmetic}.
 */
public class LongArithmeticTest extends ArithmeticAbstractTest<Long, LongArithmetic> {

  @Override
  protected Arithmetic<Long> getInstance() {
    return LongArithmetic.getInstance();
  }

  @Override
  protected Long getValue(TestCase testCase, int i) {
    switch (testCase) {
      case SUB:
        return 951l;
      default:
        throw new UnsupportedOperationException("TestCase not supported.");
    }
  }

  @Override
  protected Long[] getData(Dataset dataset) {
    switch (dataset) {
      case POSITIVE:
        return new Long[] {123l, 456l, 789l};
      case NEGATIVE:
        return new Long[] {-123l, -456l, -789l};
      case MIXED:
        return new Long[] {123l, -321l, -456l, 654l, 789l, -987l};
      default:
        throw new UnsupportedOperationException("Dataset not implemented.");
    }
  }

  @Override
  protected Long getResult(TestCase testCase, Dataset dataset) {
    switch (testCase) {
      case ADD:
        switch (dataset) {
          case POSITIVE:
            return 1368l;
          case NEGATIVE:
            return -1368l;
          case MIXED:
            return -198l;
          default:
            throw new UnsupportedOperationException("Dataset not implemented.");
        }
      case SUB:
        switch (dataset) {
          case POSITIVE:
            return -417l;
          case NEGATIVE:
            return 2319l;
          case MIXED:
            return 1149l;
          default:
            throw new UnsupportedOperationException("Dataset not implemented.");
        }
      default:
        throw new UnsupportedOperationException("TestCase not supported.");
    }
  }

}
