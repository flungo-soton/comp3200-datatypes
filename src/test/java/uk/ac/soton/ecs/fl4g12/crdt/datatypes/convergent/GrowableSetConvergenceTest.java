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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.GrowableConflictFreeSetTestInterface;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.StatefulUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Tests to ensure that two growable {@linkplain StatefulUpdatable} {@linkplain Set}s converge under
 * various operations.
 *
 * @param <E> the type of values stored in the {@link Set}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp within the {@link VersionVector}
 * @param <U> the type of snapshot made from this state.
 * @param <S> the type of {@link Set} being tested.
 */
public abstract class GrowableSetConvergenceTest<E, K, T extends Comparable<T>, U extends SetState<E, K, T>, S extends Set<E> & StatefulUpdatable<K, T, U>>
    implements GrowableConflictFreeSetTestInterface<E, S> {

  private static final Logger LOGGER = Logger.getLogger(GrowableSetConvergenceTest.class.getName());

  @Test
  @Override
  public void testUpdate_NoChange() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_NoChange: Test update with no changes.");

    final S set1 = getSet();
    final S set2 = getSet();

    assertTrue("The version vectors should be identical to start with",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The set should be identical to start with", set1.snapshot().getState(),
        set2.snapshot().getState());

    set1.update(set2.snapshot());
    assertTrue("The version vectors should be identical after update",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The sets should be identical after update", set1.snapshot().getState(),
        set2.snapshot().getState());

    set2.update(set1.snapshot());
    assertTrue("The version vectors should be identical after bi-directional update",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The sets should be identical after update", set1.snapshot().getState(),
        set2.snapshot().getState());
  }

  @Test
  @Override
  public void testUpdate_LocalAdd() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_LocalAdd: Test update with a local addition.");

    final S set1 = getSet();
    final S set2 = getSet();

    final HashSet<E> comparison0 = new HashSet<>();
    final HashSet<E> comparison1 = new HashSet<>(Arrays.asList(getElement(1)));

    assertTrue("The version vectors should be identical to start with",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The sets should be identical to start with", set1.snapshot().getState(),
        set2.snapshot().getState());
    assertEquals("set1 should have seen 0 elements", comparison0, set1.snapshot().getState());
    assertEquals("set2 should have seen 0 elements", comparison0, set2.snapshot().getState());

    set1.add(getElement(1));
    assertTrue("set2 should have happenedBefore set1",
        set2.getVersion().happenedBefore(set1.getVersion()));

    set1.update(set2.snapshot());
    assertTrue("set2 should have happenedBefore set1",
        set2.getVersion().happenedBefore(set1.getVersion()));
    assertEquals("set1 should have seen 1 element", comparison1, set1.snapshot().getState());
    assertEquals("set2 should have seen 0 elements", comparison0, set2.snapshot().getState());

    set2.update(set1.snapshot());
    assertTrue("The version vectors should be identical after bi-directional update",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The sets should be identical after bi-directional update",
        set1.snapshot().getState(), set2.snapshot().getState());
    assertEquals("set1 should have seen 1 element", comparison1, set1.snapshot().getState());
    assertEquals("set2 should have seen 1 element", comparison1, set2.snapshot().getState());
  }

  @Test
  @Override
  public void testUpdate_RemoteAdd() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_RemoteAdd: Test update with a remote addition.");

    final S set1 = getSet();
    final S set2 = getSet();

    final HashSet<E> comparison0 = new HashSet<>();
    final HashSet<E> comparison1 = new HashSet<>(Arrays.asList(getElement(1)));

    assertTrue("The version vectors should be identical to start with",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The sets should be identical to start with", set1.snapshot().getState(),
        set2.snapshot().getState());
    assertEquals("set1 should have seen 0 elements", comparison0, set1.snapshot().getState());
    assertEquals("set2 should have seen 0 elements", comparison0, set2.snapshot().getState());

    set2.add(getElement(1));
    assertTrue("set1 should have happenedBefore set2",
        set1.getVersion().happenedBefore(set2.getVersion()));

    set1.update(set2.snapshot());
    assertTrue("The version vectors should be identical after update",
        set2.getVersion().identical(set1.getVersion()));
    assertEquals("set1 should have seen 1 element", comparison1, set1.snapshot().getState());
    assertEquals("set2 should have seen 1 element", comparison1, set2.snapshot().getState());

    set2.update(set1.snapshot());
    assertTrue("The version vectors should be identical after bi-directional update",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The sets should be identical after bi-directional update",
        set1.snapshot().getState(), set2.snapshot().getState());
    assertEquals("set1 should have seen 1 element", comparison1, set1.snapshot().getState());
    assertEquals("set2 should have seen 1 element", comparison1, set2.snapshot().getState());
  }

  @Test
  @Override
  public void testUpdate_BothAdd() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_BothAdd: Test update with concurrent additions.");

    final S set1 = getSet();
    final S set2 = getSet();

    final HashSet<E> comparison0 = new HashSet<>();
    final HashSet<E> comparison1 = new HashSet<>(Arrays.asList(getElement(2)));
    final HashSet<E> comparison2 = new HashSet<>(Arrays.asList(getElement(1), getElement(2)));

    assertTrue("The version vectors should be identical to start with",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The sets should be identical to start with", set1.snapshot().getState(),
        set2.snapshot().getState());
    assertEquals("set1 should have seen 0 elements", comparison0, set1.snapshot().getState());
    assertEquals("set2 should have seen 0 elements", comparison0, set2.snapshot().getState());

    set1.add(getElement(1));
    set2.add(getElement(2));
    assertTrue("set1 should be concurrent with set2",
        set1.getVersion().concurrentWith(set2.getVersion()));

    set1.update(set2.snapshot());
    assertTrue("set2 should have happenedBefore set1",
        set2.getVersion().happenedBefore(set1.getVersion()));
    assertEquals("set1 should have seen 2 elements", comparison2, set1.snapshot().getState());
    assertEquals("set2 should have seen 1 element", comparison1, set2.snapshot().getState());

    set2.update(set1.snapshot());
    assertTrue("The sets should be identical after bi-directional update",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The sets should be identical after bi-directional update",
        set1.snapshot().getState(), set2.snapshot().getState());
    assertEquals("set1 should have seen 2 elements", comparison2, set1.snapshot().getState());
    assertEquals("set2 should have seen 2 elements", comparison2, set2.snapshot().getState());
  }

  @Test
  @Override
  public void testUpdate_BothAdd_Same() throws Exception {
    LOGGER.log(Level.INFO,
        "testUpdate_BothSame: Test update with concurrent additions of the same element.");

    final S set1 = getSet();
    final S set2 = getSet();

    final HashSet<E> comparison0 = new HashSet<>();
    final HashSet<E> comparison1 = new HashSet<>(Arrays.asList(getElement(1)));

    assertTrue("The version vectors should be identical to start with",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The sets should be identical to start with", set1.snapshot().getState(),
        set2.snapshot().getState());
    assertEquals("set1 should have seen 0 elements", comparison0, set1.snapshot().getState());
    assertEquals("set2 should have seen 0 elements", comparison0, set2.snapshot().getState());

    set1.add(getElement(1));
    set2.add(getElement(1));
    assertTrue("set1 should be concurrent with set2",
        set1.getVersion().concurrentWith(set2.getVersion()));

    set1.update(set2.snapshot());
    assertTrue("set2 should have happenedBefore set1",
        set2.getVersion().happenedBefore(set1.getVersion()));
    assertEquals("set1 should have seen 2 elements", comparison1, set1.snapshot().getState());
    assertEquals("set2 should have seen 1 element", comparison1, set2.snapshot().getState());

    set2.update(set1.snapshot());
    assertTrue("The sets should be identical after bi-directional update",
        set1.getVersion().identical(set2.getVersion()));
    assertEquals("The sets should be identical after bi-directional update",
        set1.snapshot().getState(), set2.snapshot().getState());
    assertEquals("set1 should have seen 2 elements", comparison1, set1.snapshot().getState());
    assertEquals("set2 should have seen 2 elements", comparison1, set2.snapshot().getState());
  }

}
