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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Abstract tests for grow-only {@linkplain Set} implementations.
 *
 * @param <E> the type of set value that the test uses.
 * @param <S> the type of the set being tested.
 */
public abstract class GrowOnlySetAbstractTest<E, S extends Set<E>>
    implements SetTestInterface<E, S> {

  private static final Logger LOGGER = Logger.getLogger(GrowOnlySetAbstractTest.class.getName());

  public static final int MAX_OPERATIONS = 10;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private final Class<E> elementClass;
  private final Class<E[]> elementArrayClass;

  public GrowOnlySetAbstractTest(Class<E> elementClass, Class<E[]> elementArrayClass) {
    this.elementClass = elementClass;
    this.elementArrayClass = elementArrayClass;
  }

  /**
   * Test adding single elements to the set.
   */
  @Test
  public void testAdd_Single() {
    LOGGER.log(Level.INFO, "testAdd_Single: Test adding single elements to the set.");

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      S set = getSet();
      E element = getElement(i);

      boolean result = set.add(element);

      assertTrue("Add should have been successful", result);
      assertTrue("Set should contain the element that was added", set.contains(element));
    }
  }

  /**
   * Test adding the same element to a set twice.
   */
  @Test
  public void testAdd_Duplicate() {
    LOGGER.log(Level.INFO, "testAdd_Duplicate: Test adding the same element to a set twice.");

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      S set = getSet();
      E element = getElement(i);

      assertTrue("First addition should succeed", set.add(element));
      assertFalse("First addition should fail", set.add(element));
    }
  }

  /**
   * Test adding elements to a set.
   */
  @Test
  public void testAdd() {
    LOGGER.log(Level.INFO, "testAdd: Test adding elements to a set.");

    S set = getSet();
    for (int i = 1; i <= MAX_OPERATIONS; i++) {
      E element = getElement(i);

      boolean result = set.add(element);

      assertTrue("Add should have been successful", result);
      assertTrue("Set should contain the element that was added", set.contains(element));
    }
  }

  /**
   * Test adding all elements of an empty collection.
   */
  @Test
  public void testAddAll_Empty() {
    LOGGER.log(Level.INFO, "testAddAll_Empty: Test adding all elements of an empty collection.");

    S set = getSet();
    for (int i = 1; i <= MAX_OPERATIONS; i++) {
      E element = getElement(i);
      set.add(element);

      boolean result = set.addAll(new ArrayList<E>());

      assertFalse("addAll should have been unsuccessful", result);
      assertEquals("No unexpected items should have been added", i, set.size());
    }
  }

  /**
   * Test adding single elements to the set using addAll.
   */
  @Test
  public void testAddAll_Single() {
    LOGGER.log(Level.INFO,
        "testAddAll_Single: Test adding single elements to the set using addAll.");

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      S set = getSet();
      Collection<E> elements = Arrays.asList(getElement(i));

      boolean result = set.addAll(elements);

      assertTrue("Add should have been successful", result);
      for (int j = 0; j < MAX_OPERATIONS; j++) {
        assertEquals("Set should only contain the element that was added", j == i,
            set.contains(getElement(j)));
      }
    }
  }

  /**
   * Test adding the same element to a set twice using addAll.
   */
  @Test
  public void testAddAll_Duplicate_Single() {
    LOGGER.log(Level.INFO,
        "testAddAll_Duplicate_Single: Test adding the same element to a set twice using addAll.");

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      S set = getSet();
      Collection<E> elements = Arrays.asList(getElement(i));
      set.addAll(elements);

      boolean result = set.addAll(elements);

      assertFalse("Add should have been unsuccessful", result);
      for (int j = 0; j < MAX_OPERATIONS; j++) {
        assertEquals("Set should only contain the element that was added", j == i,
            set.contains(getElement(j)));
      }
    }
  }

  /**
   * Test adding elements to the set using addAll.
   */
  @Test
  public void testAddAll() {
    LOGGER.log(Level.INFO, "testAddAll: Test adding elements to the set using addAll.");

    for (int start = 0; start < MAX_OPERATIONS; start++) {
      for (int stop = start; stop < MAX_OPERATIONS; stop++) {
        S set = getSet();
        Collection<E> elements = new ArrayList<>();
        for (int j = start; j <= stop; j++) {
          elements.add(getElement(j));
        }

        boolean result = set.addAll(elements);

        assertTrue("Add should have been successful", result);
        for (int j = 0; j < MAX_OPERATIONS; j++) {
          assertEquals("Set should only contain the element that was added",
              j >= start && j <= stop, set.contains(getElement(j)));
        }
      }
    }
  }

  /**
   * Test adding the same elements to a set twice.
   */
  @Test
  public void testAddAll_Duplicate() {
    LOGGER.log(Level.INFO, "testAddAll_Duplicate: Test adding the same elements to a set twice.");

    for (int start = 0; start < MAX_OPERATIONS; start++) {
      for (int stop = start; stop < MAX_OPERATIONS; stop++) {
        S set = getSet();
        Collection<E> elements = new ArrayList<>();
        for (int j = start; j <= stop; j++) {
          elements.add(getElement(j));
        }
        set.addAll(elements);

        boolean result = set.addAll(elements);

        assertFalse("Add should have been unsuccessful", result);
        for (int j = 0; j < MAX_OPERATIONS; j++) {
          assertEquals("Set should only contain the element that was added",
              j >= start && j <= stop, set.contains(getElement(j)));
        }
      }
    }
  }

  /**
   * Test addAll when elements already exist in the set.
   */
  @Test
  public void testAddAll_Overlap() {
    LOGGER.log(Level.INFO,
        "testAddAll_Overlap: Test addAll when elements already exist in the set.");


    for (int start = 0; start < MAX_OPERATIONS; start++) {
      for (int stop = start; stop < MAX_OPERATIONS; stop++) {
        for (int i = start; i <= stop; i++) {
          S set = getSet();
          for (int j = start; j <= stop; j++) {
            set.add(getElement(j));
          }

          Collection<E> elements = new ArrayList<>();
          for (int j = 0; j <= i; j++) {
            elements.add(getElement(j));
          }
          boolean result = set.addAll(elements);

          assertEquals("Add should succeed if new elements were added", start != 0 || stop < i,
              result);
          for (int j = 0; j < MAX_OPERATIONS; j++) {
            assertEquals("Set should only contain the elements that were added",
                j <= i || (start <= j && j <= stop), set.contains(getElement(j)));
          }
        }
      }
    }
  }

  @Test
  public void testClear() {
    LOGGER.log(Level.INFO, "testClear: Test that clear throws UnsupportedOperationException.");

    S set = getSet();
    thrown.expect(UnsupportedOperationException.class);
    set.clear();
  }

  /**
   * Test contains on the initial set.
   */
  @Test
  public void testContains_Initial() {
    LOGGER.log(Level.INFO, "testContains_Initial: Test contains on the initial set.");

    S set = getSet();

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      assertFalse("Set should only contain the element that was added",
          set.contains(getElement(i)));
    }
  }

  /**
   * Test contains on sets with a single element.
   */
  @Test
  public void testContains_Single() {
    LOGGER.log(Level.INFO, "testContains_Single: Test contains on sets with a single element.");

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      S set = getSet();
      E element = getElement(i);

      set.add(element);

      for (int j = 0; j < MAX_OPERATIONS; j++) {
        assertEquals("Set should only contain the element that was added", j == i,
            set.contains(getElement(j)));
      }
    }
  }

  /**
   * Test contains on sets.
   */
  @Test
  public void testContains() {
    LOGGER.log(Level.INFO, "testContains: Test contains on sets.");

    S set = getSet();
    for (int i = 0; i < MAX_OPERATIONS; i++) {
      E element = getElement(i);

      set.add(element);

      for (int j = 0; j < MAX_OPERATIONS; j++) {
        assertEquals("Set should only contain the element that was added", j <= i,
            set.contains(getElement(j)));
      }
    }
  }

  /**
   * Test containsAll on the initial set.
   */
  @Test
  public void testContainsAll_Initial() {
    LOGGER.log(Level.INFO, "testContainsAll_Initial: Test containsAll on the initial set.");

    S set = getSet();

    for (int start = 0; start < MAX_OPERATIONS; start++) {
      for (int stop = start; stop < MAX_OPERATIONS; stop++) {
        // Make a collection of elements to check
        Collection<E> collection = new ArrayList<>();
        for (int j = start; j < stop; j++) {
          collection.add(getElement(j));
        }

        // Should not contain any of the non-empty collections
        assertEquals("Set should only contain empty collection", start == stop,
            set.containsAll(collection));
      }
    }
  }

  /**
   * Test containsAll on sets containing a single element.
   */
  @Test
  public void testContainsAll_Single() {
    LOGGER.log(Level.INFO,
        "testContainsAll_Single: Test containsAll on sets containing a single element.");

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      S set = getSet();
      E element = getElement(i);

      set.add(element);

      // Make and test collections against containsAll
      for (int start = 0; start < MAX_OPERATIONS; start++) {
        for (int stop = start; stop < MAX_OPERATIONS; stop++) {
          // Make a collection of elements to check
          Collection<E> collection = new ArrayList<>();
          for (int j = start; j < stop; j++) {
            collection.add(getElement(j));
          }

          // Contains all should succeed for an empty collection and a collection containing the
          // single item that was added.
          assertEquals("Set should only contain the element that was added",
              start == stop || (start == i && stop == i + 1), set.containsAll(collection));
        }
      }
    }
  }

  /**
   * Test containsAll on sets.
   */
  @Test
  public void testContainsAll() {
    LOGGER.log(Level.INFO, "testContainsAll: Test containsAll on sets.");

    S set = getSet();
    for (int i = 0; i < MAX_OPERATIONS; i++) {
      E element = getElement(i);

      set.add(element);

      // Make and test collections against containsAll
      for (int start = 0; start < MAX_OPERATIONS; start++) {
        for (int stop = start; stop < MAX_OPERATIONS; stop++) {
          // Make a collection of elements to check
          Collection<E> collection = new ArrayList<>();
          for (int j = start; j < stop; j++) {
            collection.add(getElement(j));
          }

          // Contains all should succeed for an empty collection and a collection containing the
          // single item that was added.
          assertEquals("Set should only contain the element that was added",
              start == stop || stop <= i + 1, set.containsAll(collection));
        }
      }
    }
  }

  /**
   * Test equals of initial set with itself.
   */
  @Test
  public void testEquals_Initial_Self() {
    LOGGER.log(Level.INFO, "testEquals_Initial_Self: Test equals of initial set with itself.");

    S set = getSet();

    assertTrue("Initial set should equal itself", set.equals(set));
    assertTrue("Initial set should equal another initial set", set.equals(getSet()));
  }

  /**
   * Test equals of initial set with an empty set.
   */
  @Test
  public void testEquals_Initial_Empty() {
    LOGGER.log(Level.INFO,
        "testEquals_Initial_Empty: Test equals of initial set with an empty set.");

    S set = getSet();

    assertTrue("Initial set should equal an empty set.", set.equals(new HashSet<>()));
  }

  /**
   * Test equals with sets containing 1 element.
   */
  @Test
  public void testEquals_Single() {
    LOGGER.log(Level.INFO, "testEquals_Single: Test equals with sets containing 1 element.");

    List<Set<E>> comparisonSets = new ArrayList<>();
    for (int i = 0; i < MAX_OPERATIONS; i++) {
      Set<E> set = new HashSet<>();
      set.add(getElement(i));

      comparisonSets.add(set);
    }

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      S set = getSet();
      set.add(getElement(i));

      for (int j = 0; j < MAX_OPERATIONS; j++) {
        assertEquals("Set should only equal a set with the same items.", i == j,
            set.equals(comparisonSets.get(j)));
      }
    }
  }

  /**
   * Test equals with various sets.
   */
  @Test
  public void testEquals() {
    LOGGER.log(Level.INFO, "testEquals: Test equals with various sets.");

    List<Set<E>> comparisonSets = new ArrayList<>();
    HashSet<E> comparisonSet = new HashSet<>(MAX_OPERATIONS);
    for (int i = 0; i < MAX_OPERATIONS; i++) {
      comparisonSet.add(getElement(i));
      comparisonSets.add(new HashSet<E>(comparisonSet));
    }

    S set = getSet();
    for (int i = 0; i < MAX_OPERATIONS; i++) {
      set.add(getElement(i));

      for (int j = 0; j < MAX_OPERATIONS; j++) {
        assertEquals("Set should only equal a set with the same items.", i == j,
            set.equals(comparisonSets.get(j)));
      }
    }
  }

  /**
   * Test hashCode on the initial set.
   */
  @Test
  public void testHashCode_Initial() {
    LOGGER.log(Level.INFO, "testHashCode_Initial: Test hashCode on the initial set.");

    S set = getSet();

    assertEquals("Hashcode of initial set should be 0", 0, set.hashCode());
  }

  /**
   * Test hashCode on single element sets.
   */
  @Test
  public void testHashCode_Single() {
    LOGGER.log(Level.INFO, "testHashCode_Single: Test hashCode on single element sets.");

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      S set = getSet();
      E element = getElement(i);
      set.add(element);

      assertEquals("The hashcode should be equal to the hashcode of the set's element.",
          element.hashCode(), set.hashCode());
    }
  }

  /**
   * Test hashCode on a set containing null.
   */
  @Test
  public void testHashCode_Null() {
    LOGGER.log(Level.INFO, "testHashCode_Null: Test hashCode on a set containing null.");

    S set = getSet();
    set.add(null);

    assertEquals("Hashcode of set containing null should be 0", 0, set.hashCode());
  }

  /**
   * Test hashCode as elements are added to the set.
   */
  @Test
  public void testHashCode() {
    LOGGER.log(Level.INFO, "testHashCode: Test hashCode as elements are added to the set.");

    S set = getSet();
    int expectedHash = 0;
    for (int i = 0; i < MAX_OPERATIONS; i++) {
      E element = getElement(i);
      set.add(element);
      expectedHash += element.hashCode();

      assertEquals("The hashcode should be equal the sum of element hashcodes.", expectedHash,
          set.hashCode());
    }
  }

  /**
   * Test the initial set isEmpty.
   */
  @Test
  public void testIsEmpty_Initial() {
    LOGGER.log(Level.INFO, "testIsEmpty_Initial: Test the initial set isEmpty.");

    S set = getSet();
    assertTrue("Sets should be initially empty", set.isEmpty());
  }

  /**
   * Test sets with one element are not empty.
   */
  @Test
  public void testIsEmpty_Single() {
    LOGGER.log(Level.INFO, "testIsEmpty_Single: Test sets with one element are not empty.");

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      S set = getSet();
      E element = getElement(i);

      set.add(element);

      assertFalse("Sets should be not be empty when items are added", set.isEmpty());
    }
  }

  /**
   * Test a set with elements is not empty.
   */
  @Test
  public void testIsEmpty() {
    LOGGER.log(Level.INFO, "testIsEmpty: Test a set with elements is not empty.");

    S set = getSet();
    for (int i = 0; i < MAX_OPERATIONS; i++) {
      E element = getElement(i);

      set.add(element);

      assertFalse("Sets should be not be empty when items are added", set.isEmpty());
    }
  }

  /**
   * Test hasNext on empty set iterator.
   */
  @Test
  public void testIterator_hasNext_Empty() {
    LOGGER.log(Level.INFO, "testIterator_hasNext_Empty: Test hasNext on empty set iterator.");

    S set = getSet();

    Iterator<E> it = set.iterator();
    assertFalse("Iterator of empty set should not have a next element.", it.hasNext());
  }

  /**
   * Test hasNext on set iterators with a single item.
   */
  @Test
  public void testIterator_hasNext_Single() {
    LOGGER.log(Level.INFO,
        "testIterator_hasNext_Single: Test hasNext on set iterators with a single item.");

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      S set = getSet();
      E element = getElement(i);
      set.add(element);

      Iterator<E> it = set.iterator();
      assertTrue("Iterator should have next item", it.hasNext());
    }
  }

  /**
   * Test hasNext on set iterators.
   */
  @Test
  public void testIterator_hasNext() {
    LOGGER.log(Level.INFO, "testIterator_hasNext: Test hasNext on set iterators.");

    S set = getSet();
    for (int i = 1; i <= MAX_OPERATIONS; i++) {
      E element = getElement(i);
      set.add(element);

      Iterator<E> it = set.iterator();
      for (int j = 1; j <= i; j++) {
        assertTrue("Iterator should have next item", it.hasNext());
        it.next();
      }
      assertFalse("Iterator should not have next item", it.hasNext());
    }
  }

  /**
   * Test next on empty set iterator.
   */
  @Test
  public void testIterator_next_Empty() {
    LOGGER.log(Level.INFO, "testIterator_next_Empty: Test next on empty set iterator.");

    S set = getSet();

    Iterator<E> it = set.iterator();
    thrown.expect(NoSuchElementException.class);
    it.next();
  }

  /**
   * Test next on set iterators with a single item.
   */
  @Test
  public void testIterator_next_Single() {
    LOGGER.log(Level.INFO,
        "testIterator_next_Single: Test next on set iterators with a single item.");

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      S set = getSet();
      E element = getElement(i);
      set.add(element);

      Iterator<E> it = set.iterator();
      assertEquals("Next should be the expected element", element, it.next());
    }
  }

  /**
   * Test next on set iterators.
   */
  @Test
  public void testIterator_next() {
    LOGGER.log(Level.INFO, "testIterator_next: Test next on set iterators.");

    S set = getSet();
    for (int i = 1; i <= MAX_OPERATIONS; i++) {
      E element = getElement(i);
      set.add(element);

      Iterator<E> it = set.iterator();
      Set<E> acc = new HashSet<>(i + 1);
      for (int j = 1; j <= i; j++) {
        acc.add(it.next());
      }
      assertEquals("The correct number of unique elements should have been yeilded", i, acc.size());
      for (int j = 1; j <= i; j++) {
        assertTrue("The iterator should have yielded the expected elements",
            acc.contains(getElement(i)));
      }
      try {
        it.next();
        fail("NoSuchElementException should have been thrown");
      } catch (NoSuchElementException ex) {
      }
    }
  }

  /**
   * Test iterating through sets with a single item.
   */
  @Test
  public void testIterator_Single() {
    LOGGER.log(Level.INFO, "testIterator_Single: Test iterating through sets with a single item.");

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      S set = getSet();
      E element = getElement(i);
      set.add(element);

      Iterator<E> it = set.iterator();
      assertTrue("Iterator should have next item", it.hasNext());
      assertEquals("Next should be the expected element", element, it.next());
      assertFalse("Iterator should not have next item", it.hasNext());
      try {
        it.next();
        fail("NoSuchElementException should have been thrown");
      } catch (NoSuchElementException ex) {
      }
    }
  }

  /**
   * Test iterating through sets with a single item.
   */
  @Test
  public void testIterator() {
    LOGGER.log(Level.INFO, "testIterator: Test iterating through sets with a single item.");

    S set = getSet();
    for (int i = 1; i <= MAX_OPERATIONS; i++) {
      E element = getElement(i);
      set.add(element);

      Iterator<E> it = set.iterator();
      Set<E> acc = new HashSet<>(i + 1);
      for (int j = 1; j <= i; j++) {
        assertTrue("Iterator should have next item", it.hasNext());
        acc.add(it.next());
      }
      assertEquals("The correct number of unique elements should have been yeilded", i, acc.size());
      for (int j = 1; j <= i; j++) {
        assertTrue("The iterator should have yielded the expected elements",
            acc.contains(getElement(i)));
      }
      assertFalse("Iterator should not have next item", it.hasNext());
      try {
        it.next();
        fail("NoSuchElementException should have been thrown");
      } catch (NoSuchElementException ex) {
      }
    }
  }

  /**
   * Test that iterator remove throws UnsupportedOperationException.
   */
  @Test
  public void testIterator_remove() {
    LOGGER.log(Level.INFO,
        "testIterator_remove: Test that iterator remove throws UnsupportedOperationException.");

    S set = getSet();
    set.add(getElement(0));
    Iterator<E> it = set.iterator();
    it.next();
    thrown.expect(UnsupportedOperationException.class);
    it.remove();
  }

  /**
   * Test that remove throws UnsupportedOperationException.
   */
  @Test
  public void testRemove() {
    LOGGER.log(Level.INFO, "testRemove: Test that remove throws UnsupportedOperationException.");

    S set = getSet();
    E element = getElement(0);
    set.add(element);
    thrown.expect(UnsupportedOperationException.class);
    set.remove(element);
  }

  /**
   * Test that removeAll throws UnsupportedOperationException.
   */
  @Test
  public void testRemoveAll() {
    LOGGER.log(Level.INFO,
        "testRemoveAll: Test that removeAll throws UnsupportedOperationException.");

    S set = getSet();
    E element = getElement(0);
    set.add(element);
    thrown.expect(UnsupportedOperationException.class);
    set.removeAll(Arrays.asList(element));
  }

  /**
   * Test that retainAll throws UnsupportedOperationException.
   */
  @Test
  public void testRetainAll() {
    LOGGER.log(Level.INFO,
        "testRetainAll: Test that retainAll throws UnsupportedOperationException.");

    S set = getSet();
    E element = getElement(0);
    set.add(element);
    thrown.expect(UnsupportedOperationException.class);
    set.removeAll(Arrays.asList(element));
  }

  /**
   * Test the initial size of the set.
   */
  @Test
  public void testSize_Initial() {
    LOGGER.log(Level.INFO, "testSize_Initial: Test the initial size of the set.");

    S set = getSet();
    assertEquals("Initial size should be 0", 0, set.size());
  }

  /**
   * Test the size of the set as elements are added.
   */
  @Test
  public void testSize() {
    LOGGER.log(Level.INFO, "testSize: Test the size of the set as elements are added.");

    S set = getSet();
    for (int i = 1; i <= MAX_OPERATIONS; i++) {
      E element = getElement(i);
      set.add(element);

      assertEquals("Set size should be equal to the number of elements added", i, set.size());
    }
  }

  /**
   * Test toArray on a new set.
   */
  @Test
  public void testToArray_Initial() {
    LOGGER.log(Level.INFO, "testToArray_Initial: Test toArray on a new set.");

    S set = getSet();
    Object[] expected = new Object[0];

    Assert.assertArrayEquals(expected, set.toArray());
  }

  /**
   * Test toArray on sets with a single element.
   */
  @Test
  public void testToArray_Single() {
    LOGGER.log(Level.INFO, "testToArray_Single: Test toArray on sets with a single element.");

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      S set = getSet();
      E element = getElement(i);

      Object[] expected = new Object[] {element};
      set.add(element);

      Assert.assertArrayEquals(expected, set.toArray());
    }
  }

  /**
   * Test toArray on a set as elements are added.
   */
  @Test
  public void testToArray() {
    LOGGER.log(Level.INFO, "testToArray: Test toArray on a set as elements are added.");

    S set = getSet();
    Object[] elements = new Object[MAX_OPERATIONS];

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      E element = getElement(i);
      set.add(element);
      elements[i] = element;

      Object[] expected = Arrays.copyOf(elements, i + 1, Object[].class);

      Assert.assertArrayEquals(expected, set.toArray());
    }
  }

  /**
   * Test toArray (with generic array) on a new set.
   */
  @Test
  public void testToArray_GenericArray_Initial() {
    LOGGER.log(Level.INFO,
        "testToArray_GenericArray_Initial: Test toArray (with generic array) on a new set.");

    S set = getSet();
    E[] emptyArray = Arrays.copyOf(new Object[0], 0, elementArrayClass);
    Object[] expected = new Object[0];

    Assert.assertArrayEquals(expected, set.toArray(emptyArray));
  }

  /**
   * Test toArray (with generic array) on sets with a single element.
   */
  @Test
  public void testToArray_GenericArray_Single() {
    LOGGER.log(Level.INFO, "testToArray_GenericArray_Single: "
        + "Test toArray (with generic array) on sets with a single element.");

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      S set = getSet();
      E element = getElement(i);

      E[] emptyArray = Arrays.copyOf(new Object[0], 0, elementArrayClass);
      Object[] expected = new Object[] {element};
      set.add(element);

      Assert.assertArrayEquals(expected, set.toArray(emptyArray));
    }
  }

  /**
   * Test toArray (with generic array) on a set as elements are added when an empty E[] is given.
   */
  @Test
  public void testToArray_GenericArray_Empty() {
    LOGGER.log(Level.INFO,
        "testToArray_GenericArray_Empty: "
            + "Test toArray (with generic array) on a set as elements are added when an empty E[] "
            + "is given.");

    S set = getSet();
    Object[] elements = new Object[MAX_OPERATIONS];

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      E element = getElement(i);
      set.add(element);
      elements[i] = element;

      E[] emptyArray = Arrays.copyOf(new Object[0], 0, elementArrayClass);
      E[] expected = Arrays.copyOf(elements, i + 1, elementArrayClass);

      Assert.assertArrayEquals(expected, set.toArray(emptyArray));
    }
  }

  /**
   * Test toArray (with generic array) on a set as elements are added when an adequately sized E[]
   * is given.
   */
  @Test
  public void testToArray_GenericArray_Adequate() {
    LOGGER.log(Level.INFO,
        "testToArray_GenericArray_Adequate: "
            + "Test toArray (with generic array) on a set as elements are added when an adequately "
            + "sized E[] is given.");

    S set = getSet();
    Object[] elements = new Object[MAX_OPERATIONS];

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      E element = getElement(i);
      set.add(element);
      elements[i] = element;

      E[] adequateArray = Arrays.copyOf(new Object[i + 1], i + 1, elementArrayClass);
      E[] expected = Arrays.copyOf(elements, i + 1, elementArrayClass);

      Assert.assertArrayEquals(expected, set.toArray(adequateArray));
    }
  }

  /**
   * Test toArray (with generic array) on a set as elements are added when an oversized E[] is
   * given.
   */
  @Test
  public void testToArray_GenericArray_Oversized() {
    LOGGER.log(Level.INFO,
        "testToArray_GenericArray_Oversized: "
            + "Test toArray (with generic array) on a set as elements are added when an oversized "
            + "E[] is given.");

    S set = getSet();
    Object[] elements = new Object[MAX_OPERATIONS];

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      E element = getElement(i);
      set.add(element);
      elements[i] = element;

      int oversize = (i + 1) * 2;
      E[] oversizedArray = Arrays.copyOf(new Object[oversize], oversize, elementArrayClass);
      E[] expected = Arrays.copyOf(elements, oversize, elementArrayClass);

      Assert.assertArrayEquals(expected, set.toArray(oversizedArray));
    }
  }

  // TODO: more tests will null objects.
}
