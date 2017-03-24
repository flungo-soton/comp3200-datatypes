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

import java.util.Set;

/**
 * Interface providing method for what should be tested in growable {@linkplain CRDT}
 * {@linkplain Set}s.
 *
 * @param <E> the type of values stored in the {@link Set}.
 * @param <S> the type of {@link Set} being tested.
 */
public interface GrowableConflictFreeSetTestInterface<E, S extends Set<E>> extends SetTestInterface<E, S> {

  /**
   * Test update with no changes.
   *
   * @throws Exception if the test fails.
   */
  void testUpdate_NoChange() throws Exception;

  /**
   * Test update with a local addition.
   *
   * @throws Exception if the test fails.
   */
  void testUpdate_LocalAdd() throws Exception;

  /**
   * Test update with a remote addition.
   *
   * @throws Exception if the test fails.
   */
  void testUpdate_RemoteAdd() throws Exception;

  /**
   * Test update with concurrent additions.
   *
   * @throws Exception if the test fails.
   */
  void testUpdate_BothAdd() throws Exception;

  /**
   * Test update with concurrent additions of the same element.
   *
   * @throws Exception if the test fails.
   */
  void testUpdate_BothAdd_Same() throws Exception;
}
