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

package uk.ac.soton.ecs.fl4g12.crdt.datatypes.convergent;

import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.SetTestInterface;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.StatefulUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Abstract tests for growable {@linkplain StatefulUpdatable} {@linkplain Set}.
 *
 * @param <E> the type of values stored in the {@link Set}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp within the {@link VersionVector}
 * @param <U> the type of snapshot made from this state.
 * @param <S> the type of {@link Set} being tested.
 */
public abstract class GrowableStatefulSetAbstractTest<E, K, T extends Comparable<T>, U extends SetState<E, K, T>, S extends Set<E> & StatefulUpdatable<K, T, U>>
    implements SetTestInterface<E, S> {

  private static final Logger LOGGER =
      Logger.getLogger(GrowableStatefulSetAbstractTest.class.getName());

  /**
   * Test snapshot of the initial {@linkplain GSet} state.
   */
  @Test
  public void testSnapshot_Initial() {
    LOGGER.log(Level.INFO, "testSnapshot_Initial: Test snapshot of the initial set state.");
    final S set = getSet();

    final VersionVector<K, T> expectedVersionVector = set.getVersion().copy();

    U state = set.snapshot();

    assertEquals("state identifier should be the same as the set", set.getIdentifier(),
        state.getIdentifier());
    assertTrue("The VersionVector should match the expectation",
        state.getVersionVector().identical(expectedVersionVector));
    assertTrue("The state should be empty", state.getState().isEmpty());

    // Check that the state snapshot is immutable by changes to the set.
    set.add(getElement(0));

    assertEquals("state identifier should be the same as the set", set.getIdentifier(),
        state.getIdentifier());
    assertTrue("The VersionVector should still match the expectation",
        state.getVersionVector().identical(expectedVersionVector));
    assertTrue("The state should still be empty", state.getState().isEmpty());
  }

  /**
   * Test snapshot of a set with an element added.
   */
  @Test
  public void testSnapshot_Add() {
    LOGGER.log(Level.INFO, "testSnapshot_Zero: Test snapshot of a set with an element added.");
    final S set = getSet();

    final VersionVector<K, T> expectedVersionVector = set.getVersion().copy();

    set.add(getElement(0));
    expectedVersionVector.increment(set.getIdentifier());
    U state = set.snapshot();

    assertEquals("state identifier should be the same as the set", set.getIdentifier(),
        state.getIdentifier());
    assertTrue("The VersionVector should match the expectation",
        state.getVersionVector().identical(expectedVersionVector));

    // Check that the state snapshot is immutable by changes to the set.
    set.add(getElement(1));

    assertEquals("state identifier should be the same as the set", set.getIdentifier(),
        state.getIdentifier());
    assertTrue("The VersionVector should still match the expectation",
        state.getVersionVector().identical(expectedVersionVector));
  }

  /**
   * Test snapshot of a set with an element added.
   */
  @Test
  public void testSnapshot_AddAll() {
    LOGGER.log(Level.INFO, "testSnapshot_Zero: Test snapshot of a set with an element added.");
    final S set = getSet();

    final VersionVector<K, T> expectedVersionVector = set.getVersion().copy();

    set.addAll(Arrays.asList(getElement(0), getElement(1), getElement(2)));
    expectedVersionVector.increment(set.getIdentifier());
    U state = set.snapshot();

    assertEquals("state identifier should be the same as the set", set.getIdentifier(),
        state.getIdentifier());
    assertTrue("The VersionVector should match the expectation",
        state.getVersionVector().identical(expectedVersionVector));

    // Check that the state snapshot is immutable by changes to the set.
    set.addAll(Arrays.asList(getElement(3), getElement(4), getElement(5)));

    assertEquals("state identifier should be the same as the set", set.getIdentifier(),
        state.getIdentifier());
    assertTrue("The VersionVector should still match the expectation",
        state.getVersionVector().identical(expectedVersionVector));
  }
}
