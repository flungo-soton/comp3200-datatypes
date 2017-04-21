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
import java.util.Iterator;

/**
 * String Utilities.
 */
public class StringUtils {

  public static final int MAX_ELEMENTS = 3;

  // Utils class cannot be constructed
  private StringUtils() {}

  /**
   * Get a reasonable length string to represent a {@link Collection}. Always includes the type and
   * size of the collection with up to {@link #MAX_ELEMENTS} elements listed.
   *
   * @param collection the collection to get the representation of.
   * @return a string representation of the {@link Collection}.
   */
  public static String getCollectionString(Collection collection) {
    StringBuilder sb = new StringBuilder(collection.getClass().getSimpleName());
    final int size = collection.size();
    sb.append("{size=").append(size).append(", elements=[");
    Iterator it = collection.iterator();
    if (size > MAX_ELEMENTS) {
      for (int i = 0; i < MAX_ELEMENTS; i++) {
        sb.append(it.next()).append(", ");
      }
      sb.append("...");
    } else if (size > 0) {
      for (int i = 1; i < size; i++) {
        sb.append(it.next()).append(", ");
      }
      sb.append(it.next());
    }
    sb.append("]}");
    return sb.toString();
  }

}
