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

package uk.ac.soton.ecs.fl4g12.crdt.order;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * {@linkplain VersionVector} backed by an {@linkplain ArrayList}. This is particularly useful where
 * the identifiers are incrementally assigned integers. This will perform inefficiently where IDs
 * are sparse.
 *
 * @param <T> the type of the timestamps stored in this version vector.
 */
public class ArrayVersionVector<T extends Comparable<T>> extends AbstractVersionVector<Integer, T> {

  private final LogicalVersion<T, ?> zero;

  private final List<LogicalVersion<T, ?>> vector;
  private final Set<Integer> identifiers;

  private int size;

  /**
   * Construct an {@linkplain ArrayVersionVector}. The {@link LogicalVersion} provided as
   * {@code zero} will be cloned when initialising a new identifier.
   *
   * @param zero a {@link LogicalVersion} representing the zero value of the type wanted for the
   *        timestamps.
   */
  public ArrayVersionVector(LogicalVersion<T, ?> zero) {
    this(zero, new ArrayList<LogicalVersion<T, ?>>(), new HashSet<Integer>());
  }

  /**
   * Construct an {@linkplain ArrayVersionVector} with all fields specified. For internal use only.
   *
   * @param zero a {@link LogicalVersion} representing the zero value of the type wanted for the
   *        timestamps.
   * @param vector the vector to initialise with. This value is not copied.
   * @param identifiers the set of identifiers to initialise with. This value is not copied.
   */
  private ArrayVersionVector(LogicalVersion<T, ?> zero, List<LogicalVersion<T, ?>> vector,
      Set<Integer> identifiers) {
    super(zero);
    this.zero = zero.copy();
    this.vector = vector;
    this.identifiers = identifiers;
    this.size = vector.size();
  }

  @Override
  public LogicalVersion<T, ?> getLogicalVersion(Integer id) {
    try {
      return vector.get(id);
    } catch (IndexOutOfBoundsException ex) {
      return null;
    }
  }

  @Override
  public HashSet<Integer> getIdentifiers() {
    return new HashSet<>(identifiers);
  }

  @Override
  public synchronized LogicalVersion<T, ?> init(Integer id) {
    if (identifiers.contains(id)) {
      return vector.get(id);
    }
    LogicalVersion<T, ?> version = zero.copy();
    if (id > size - 1) {
      for (int i = size; i < id; i++) {
        vector.add(i, null);
      }
      vector.add(id, version);
      size = id + 1;
    } else {
      vector.set(id, version);
    }
    identifiers.add(id);
    return version;
  }

  @Override
  public void sync(Integer id, T value) {
    if (!identifiers.contains(id)) {
      init(id);
    }
    getLogicalVersion(id).sync(value);
  }

  @Override
  public ArrayVersionVector<T> copy() {
    ArrayVersionVector<T> copy = new ArrayVersionVector<>(zero);
    copy.sync(this);
    return copy;
  }

}
