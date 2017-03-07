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
 * Tests for {@linkplain IntegerArithmetic}.
 */
public class IntegerArithmeticTest extends ArithmeticAbstractTest<Integer, IntegerArithmetic> {

  @Override
  protected Arithmetic<Integer> getInstance() {
    return IntegerArithmetic.getInstance();
  }

  @Override
  protected Integer getValue(TestCase testCase, int i) {
    switch (testCase) {
      case SUB:
        return 951;
      default:
        throw new UnsupportedOperationException("TestCase not supported.");
    }
  }

  @Override
  protected Integer[] getData(Dataset dataset) {
    switch (dataset) {
      case POSITIVE:
        return new Integer[] {123, 456, 789};
      case NEGATIVE:
        return new Integer[] {-123, -456, -789};
      case MIXED:
        return new Integer[] {123, -321, -456, 654, 789, -987};
      default:
        throw new UnsupportedOperationException("Dataset not implemented.");
    }
  }

  @Override
  protected Integer getResult(TestCase testCase, Dataset dataset) {
    switch (testCase) {
      case ADD:
        switch (dataset) {
          case POSITIVE:
            return 1368;
          case NEGATIVE:
            return -1368;
          case MIXED:
            return -198;
          default:
            throw new UnsupportedOperationException("Dataset not implemented.");
        }
      case SUB:
        switch (dataset) {
          case POSITIVE:
            return -417;
          case NEGATIVE:
            return 2319;
          case MIXED:
            return 1149;
          default:
            throw new UnsupportedOperationException("Dataset not implemented.");
        }
      default:
        throw new UnsupportedOperationException("TestCase not supported.");
    }
  }

}
