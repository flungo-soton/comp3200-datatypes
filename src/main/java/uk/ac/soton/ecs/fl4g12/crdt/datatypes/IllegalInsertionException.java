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

import java.util.Arrays;
import java.util.Collection;

/**
 * Exception for insertions into a {@linkplain Collection} which failed due to a semantic of the
 * {@linkplain Collection}. Contains an array of elements that caused the exception. If the
 * exception is thrown then no change should have happened to the {@linkplain Collection}.
 */
public class IllegalInsertionException extends IllegalArgumentException {

  private final Object[] elements;

  /**
   * Creates a new instance of <code>IllegalInsertionException</code> without detail message.
   *
   * @param elements the elements that failed to be inserted.
   */
  public IllegalInsertionException(Object... elements) {
    this.elements = elements;
  }

  /**
   * Constructs an instance of <code>IllegalInsertionException</code> with the specified detail
   * message.
   *
   * @param msg the detail message.
   * @param elements the elements that failed to be inserted.
   */
  public IllegalInsertionException(String msg, Object... elements) {
    super(msg);
    this.elements = elements;
  }

  /**
   * Get an array of elements that failed insertion. The array returned is a copy of the one stored
   * internally in order to maintain immutability of the exception.
   *
   * @return an array of elements that failed insertion.
   */
  public Object[] getElements() {
    return Arrays.copyOf(elements, elements.length);
  }

}
