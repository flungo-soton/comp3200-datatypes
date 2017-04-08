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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.VersionedUpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.order.Version;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Abstract tests for ensuring the conflict-free replicability in growable {@linkplain CRDT}
 * {@linkplain Set}s using a {@linkplain DeliveryChannel}.
 *
 * @param <E> the type of values stored in the {@link Set}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp within the {@link VersionVector}
 * @param <U> the type of snapshot made from this state.
 * @param <S> the type of {@link Set} being tested.
 */
public abstract class GrowableConflictFreeSetAbstractIT<E, K, T extends Comparable<T>, U extends VersionedUpdateMessage<K, ? extends Version>, S extends Set<E> & VersionedUpdatable<K, VersionVector<K, T>, U>>
    implements SetTestInterface<E, S> {

  private static final Logger LOGGER =
      Logger.getLogger(GrowableConflictFreeSetAbstractIT.class.getName());

  public static final int MAX_SETS = 10;

  public static final int MAX_ELEMENTS = 1000;

  @Rule
  public Timeout timeout = new Timeout(MAX_SETS * MAX_ELEMENTS * 2 + 5000, TimeUnit.MILLISECONDS);

  /**
   * Get the {@linkplain Set} instance for testing. Sets should be configured with a delivery
   * channel that will deliver between all other sets in the test. It is expected that the delivery
   * channel will be reset and shutdown between tests using {@code @Before} and {@code @After}
   * methods.
   *
   * @return a {@link Set} to be tested.
   */
  @Override
  public abstract S getSet();

  public abstract DeliveryChannel<K, U> getDeliveryChannel();

  /**
   * Wait until there are no pending deliveries from the source to the destination.
   *
   * @param source the source of updates.
   * @param destination the destination.
   */
  public abstract void waitForDelivery(S source, S destination);

  @Test
  public void test_Add_TwoSets_OneElement() throws Exception {
    LOGGER.log(Level.INFO,
        "test_Add_TwoSets_OneElement: Test an add being delivered to another set.");

    final S set1 = getSet();
    final S set2 = getSet();
    final Set<E> comparison = new HashSet<>();

    E element = getElement(1);
    set1.add(element);
    comparison.add(element);

    waitForDelivery(set1, set2);
    Assert.assertEquals(comparison, set2);
  }

  @Test
  public void test_Add_TwoSets_OneElementEach() throws Exception {
    LOGGER.log(Level.INFO,
        "test_Add_TwoSets_OneElementEach: Test an add on each set being delivered to another set.");

    final S set1 = getSet();
    final S set2 = getSet();
    final Set<E> comparison = new HashSet<>();

    E element = getElement(1);
    set1.add(element);
    comparison.add(element);

    element = getElement(2);
    set2.add(element);
    comparison.add(element);

    waitForDelivery(set1, set2);
    waitForDelivery(set2, set1);
    Assert.assertEquals(comparison, set1);
    Assert.assertEquals(comparison, set2);
  }

  @Test
  public void test_Add_TwoSets_SameElement() throws Exception {
    LOGGER.log(Level.INFO,
        "test_BothAdd_Same: Test update with additions of the same element by both.");

    final S set1 = getSet();
    final S set2 = getSet();
    final Set<E> comparison = new HashSet<>();

    E element = getElement(1);
    set1.add(element);
    set2.add(element);
    comparison.add(element);

    waitForDelivery(set1, set2);
    waitForDelivery(set2, set1);
    Assert.assertEquals(comparison, set1);
    Assert.assertEquals(comparison, set2);
  }

  @Test
  public void test_Add_TwoSets_MultipleElements() throws Exception {
    LOGGER.log(Level.INFO, "test_Add_TwoSets_MultipleElements: "
        + "Test elements added to one set are delivered to the other set.");

    final S set1 = getSet();
    final S set2 = getSet();
    final Set<E> comparison = new HashSet<>();

    for (int i = 0; i < MAX_ELEMENTS; i++) {
      E element = getElement(i);
      set1.add(element);
      comparison.add(element);
    }

    waitForDelivery(set1, set2);
    Assert.assertEquals(comparison, set2);
  }



  @Test
  public void test_Add_TwoSets_MultipleElementsEach() throws Exception {
    LOGGER.log(Level.INFO, "test_Add_TwoSets_MultipleElementsEach: "
        + "Test an adds on each set being delivered to the other set.");

    final S set1 = getSet();
    final S set2 = getSet();
    final Set<E> comparison = new HashSet<>();

    final Thread thread1 = new Thread(new SetAddRunable(set1, MAX_ELEMENTS * 0, MAX_ELEMENTS * 1));
    final Thread thread2 = new Thread(new SetAddRunable(set1, MAX_ELEMENTS * 1, MAX_ELEMENTS * 2));

    thread1.start();
    thread2.start();

    for (int i = 0; i < MAX_ELEMENTS * 2; i++) {
      comparison.add(getElement(i));
    }

    thread1.join();
    thread2.join();

    waitForDelivery(set1, set2);
    waitForDelivery(set2, set1);
    Assert.assertEquals(comparison, set1);
    Assert.assertEquals(comparison, set2);
  }

  @Test
  public void test_Add_MultipleSets_OneElement() {
    LOGGER.log(Level.INFO,
        "test_Add_MultipleSets_OneElement: Test an add being delivered to multiple sets.");

    final S source = getSet();
    final Set<E> comparison = new HashSet<>();

    final Set<S> destinations = new HashSet<>();
    for (int i = 1; i < MAX_SETS; i++) {
      destinations.add(getSet());
    }

    E element = getElement(1);
    source.add(element);
    comparison.add(element);

    for (S destination : destinations) {
      waitForDelivery(source, destination);
      Assert.assertEquals(comparison, destination);
    }
  }

  @Test
  public void test_Add_MultipleSets_MultipleElements() {
    LOGGER.log(Level.INFO,
        "test_Add_MultipleSets_MultipleElements: Test an add being delivered to multiple sets.");

    final S source = getSet();
    final Set<E> comparison = new HashSet<>();

    final Set<S> destinations = new HashSet<>();
    for (int i = 1; i < MAX_SETS; i++) {
      destinations.add(getSet());
    }

    for (int i = 0; i < MAX_ELEMENTS; i++) {
      E element = getElement(i);
      source.add(element);
      comparison.add(element);
    }

    for (S destination : destinations) {
      waitForDelivery(source, destination);
      Assert.assertEquals(comparison, destination);
    }
  }

  @Test
  public void test_Add_MultipleSets_OneElementEach() {
    LOGGER.log(Level.INFO,
        "test_Add_MultipleSets_MultipleElements: Test an add being delivered to multiple sets.");

    final Set<E> comparison = new HashSet<>();

    final Set<S> sets = new HashSet<>();
    for (int i = 0; i < MAX_SETS; i++) {
      sets.add(getSet());
    }

    int i = 0;
    for (S source : sets) {
      E element = getElement(i++);
      source.add(element);
      comparison.add(element);
    }

    for (S destination : sets) {
      for (S source : sets) {
        if (source != destination) {
          waitForDelivery(source, destination);
        }
      }
      Assert.assertEquals(comparison, destination);
    }
  }

  @Test
  public void test_Add_MultipleSets_MultipleElementsEach() {
    LOGGER.log(Level.INFO, "test_Add_MultipleSets_MultipleElementsEach: "
        + "Test adds to all the sets being delivered to the other sets.");

    final Set<E> comparison = new HashSet<>();

    final Set<S> sets = new HashSet<>();
    final Set<Thread> threads = new HashSet<>();
    for (int i = 0; i < MAX_SETS; i++) {
      S destination = getSet();
      sets.add(destination);
      Thread thread =
          new Thread(new SetAddRunable(destination, i * MAX_ELEMENTS, (i + 1) * MAX_ELEMENTS));
      threads.add(thread);
      thread.start();
    }

    for (int i = 0; i < MAX_SETS * MAX_ELEMENTS; i++) {
      comparison.add(getElement(i));
    }

    for (Thread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException ex) {
        LOGGER.log(Level.SEVERE, "Interupted while joining thread.", ex);
      }
    }

    for (S destination : sets) {
      for (S source : sets) {
        if (source != destination) {
          waitForDelivery(source, destination);
        }
      }
      Assert.assertEquals(comparison, destination);
    }
  }

  public class SetAddRunable implements Runnable {

    private final int stop;
    private final int start;
    private final S set;

    public SetAddRunable(S set, int start, int stop) {
      this.set = set;
      this.stop = stop;
      this.start = start;
    }

    @Override
    public void run() {
      for (int i = start; i < stop; i++) {
        set.add(getElement(i));
      }
    }

  }

}
