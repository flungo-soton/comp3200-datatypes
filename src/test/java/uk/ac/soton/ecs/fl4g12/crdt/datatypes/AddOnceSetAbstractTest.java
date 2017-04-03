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
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Abstract tests for {@linkplain Set} implementations that cannot have elements re-added.
 *
 * @param <E> the type of set value that the test uses.
 * @param <S> the type of the set being tested.
 */
public abstract class AddOnceSetAbstractTest<E, S extends Set<E>>
    extends GrowableSetAbstractTest<E, S> {

  private static final Logger LOGGER = Logger.getLogger(AddOnceSetAbstractTest.class.getName());

  public AddOnceSetAbstractTest(Class<E> elementClass, Class<E[]> elementArrayClass) {
    super(elementClass, elementArrayClass);
  }

  /**
   * Test clear on the initial set.
   */
  @Test
  public void testClear_Initial() {
    LOGGER.log(Level.INFO, "testClear_Initial: Test clear on the initial set.");

    S set = getSet();
    set.clear();
    assertEquals("Size should be 0", 0, set.size());
    assertTrue("Set should be empty", set.isEmpty());
  }

  /**
   * Test clear on sets with a single element.
   */
  @Test
  public void testClear_Single() {
    LOGGER.log(Level.INFO, "testClear_Single: Test clear on sets with a single element.");

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      S set = getSet();
      E element = getElement(i);

      set.add(element);
      set.clear();

      assertEquals("Size should be 0", 0, set.size());
      assertTrue("Set should be empty", set.isEmpty());
    }
  }

  /**
   * Test clear on sets of increasing size.
   */
  @Test
  public void testClear() {
    LOGGER.log(Level.INFO, "testClear: Test clear on sets of increasing size.");

    for (int i = 1; i <= MAX_OPERATIONS; i++) {
      S set = getSet();

      for (int j = 1; j <= MAX_OPERATIONS; j++) {
        set.add(getElement(j));
      }
      set.clear();

      assertEquals("Size should be 0", 0, set.size());
      assertTrue("Set should be empty", set.isEmpty());
    }
  }

  /**
   * Test remove on empty set iterator.
   */
  @Test
  public void testIterator_remove_Empty() {
    LOGGER.log(Level.INFO, "testIterator_remove_Empty: Test remove on empty set iterator.");

    S set = getSet();

    Iterator<E> it = set.iterator();
    thrown.expect(IllegalStateException.class);
    it.remove();
  }

  /**
   * Test remove on sets with a single item.
   */
  @Test
  public void testIterator_remove_Single() {
    LOGGER.log(Level.INFO, "testIterator_remove_Single: Test remove on sets with a single item.");

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      S set = getSet();
      E element = getElement(i);
      set.add(element);

      Iterator<E> it = set.iterator();
      it.next();
      it.remove();

      assertFalse("Set should no longer contain the removed element", set.contains(element));
    }
  }

  /**
   * Test iterator remove on some elements.
   */
  @Test
  public void testIterator_remove() {
    LOGGER.log(Level.INFO, "testIterator_remove: Test iterator remove on some elements.");

    // How many elements to add
    for (int i = 1; i <= MAX_OPERATIONS; i++) {
      // How many elements to remove
      for (int j = 1; j <= MAX_OPERATIONS; j++) {
        S set = getSet();
        // Populate the set
        for (int k = 1; k <= i; k++) {
          set.add(getElement(k));
        }

        // Remove elements
        Iterator<E> it = set.iterator();
        while (it.hasNext()) {
          E element = it.next();
          for (int k = 1; k <= j; k++) {
            if (getElement(k).equals(element)) {
              it.remove();
              break;
            }
          }
        }

        // Check which elements remain
        for (int k = 1; k <= MAX_OPERATIONS; k++) {
          assertEquals("Set should only contain elements that have been added but not removed",
              k <= i && k > j, set.contains(getElement(k)));
        }
      }
    }
  }

  /**
   * Test removing elements from the empty initial set.
   */
  @Test
  public void testRemove_Initial() {
    LOGGER.log(Level.INFO,
        "testRemove_Initial: Test removing elements from the empty initial set.");

    S set = getSet();
    for (int i = 1; i <= MAX_OPERATIONS; i++) {
      E element = getElement(i);

      boolean result = set.remove(element);

      assertFalse("Remove should have failed", result);
      assertFalse("Set should not contain the element that was removed", set.contains(element));
    }
  }

  /**
   * Test adding and removing an element from the set.
   */
  @Test
  public void testRemove_Single() {
    LOGGER.log(Level.INFO, "testRemove_Single: Test adding and removing an element from the set.");

    S set = getSet();
    for (int i = 1; i <= MAX_OPERATIONS; i++) {
      E element = getElement(i);
      set.add(element);

      boolean result = set.remove(element);

      assertTrue("Remove should have been successful", result);
      assertFalse("Set should not contain the element that was removed", set.contains(element));
    }
  }

  /**
   * Test adding and removing elements from the set.
   */
  @Test
  public void testRemove() {
    LOGGER.log(Level.INFO, "testRemove: Test adding and removing elements from the set.");

    // How many elements to add
    for (int i = 1; i <= MAX_OPERATIONS; i++) {
      // How many elements to remove
      for (int j = 1; j <= MAX_OPERATIONS; j++) {
        S set = getSet();
        // Populate the set
        for (int k = 1; k <= i; k++) {
          set.add(getElement(k));
        }

        // Remove elements
        for (int k = 1; k <= j; k++) {
          boolean result = set.remove(getElement(k));
          assertEquals("Remove should succeed, only if the element was added", k <= i, result);
        }

        // Check which elements remain
        for (int k = 1; k <= MAX_OPERATIONS; k++) {
          assertEquals("Set should only contain elements that have been added but not removed",
              k <= i && k > j, set.contains(getElement(k)));
        }
      }
    }
  }

  /**
   * Test removing all of an empty collection.
   */
  @Test
  public void testRemoveAll_Empty() {
    LOGGER.log(Level.INFO, "testRemoveAll_Empty: Test removing all of an empty collection.");

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
   * Test removeAll from the set with a single element.
   */
  @Test
  public void testRemoveAll_Single() {
    LOGGER.log(Level.INFO,
        "testRemoveAll_Single: Test removeAll from the set with a single element.");

    for (int start = 0; start < MAX_OPERATIONS; start++) {
      for (int stop = start; stop < MAX_OPERATIONS; stop++) {
        for (int i = 0; i < MAX_OPERATIONS; i++) {
          S set = getSet();
          set.add(getElement(i));

          // Make a collection of elements to remove
          Collection<E> elements = new ArrayList<>();
          for (int j = start; j < stop; j++) {
            elements.add(getElement(j));
          }

          set.removeAll(elements);

          // Check which elements remain
          for (int j = 0; j < MAX_OPERATIONS; j++) {
            assertEquals("Should only include the single element unless it was removed.",
                i == j && !(start <= j && j < stop), set.contains(getElement(j)));
          }
        }
      }
    }
  }

  /**
   * Test removeAll from the various sets.
   */
  @Test
  public void testRemoveAll() {
    LOGGER.log(Level.INFO, "testRemoveAll: Test removeAll from the various sets.");

    for (int start = 0; start < MAX_OPERATIONS; start++) {
      for (int stop = start; stop < MAX_OPERATIONS; stop++) {
        for (int i = 0; i < MAX_OPERATIONS; i++) {
          S set = getSet();
          for (int j = 0; j < i; j++) {
            set.add(getElement(j));
          }

          // Make a collection of elements to remove
          Collection<E> elements = new ArrayList<>();
          for (int j = start; j < stop; j++) {
            elements.add(getElement(j));
          }

          set.removeAll(elements);

          // Check which elements remain
          for (int j = 0; j < MAX_OPERATIONS; j++) {
            assertEquals("Should only include the element that were not removed.",
                j < i && !(start <= j && j < stop), set.contains(getElement(j)));
          }
        }
      }
    }
  }

  /**
   * Test retaining no elements.
   */
  @Test
  public void testRetainAll_Empty() {
    LOGGER.log(Level.INFO, "testRetainAll_Empty: Test retaining no elements.");

    for (int i = 1; i <= MAX_OPERATIONS; i++) {
      S set = getSet();
      E element = getElement(i);

      for (int j = 0; j < MAX_OPERATIONS; j++) {
        set.add(element);
      }

      boolean result = set.retainAll(new ArrayList<>());

      assertTrue("retainAll should have been successful", result);
      assertEquals("No unexpected items should have been added", 0, set.size());
    }
  }

  /**
   * Test retaining elements from sets with a single element.
   */
  @Test
  public void testRetainAll_Single() {
    LOGGER.log(Level.INFO,
        "testRetainAll_Single: Test retaining elements from sets with a single element.");

    for (int start = 0; start < MAX_OPERATIONS; start++) {
      for (int stop = start; stop < MAX_OPERATIONS; stop++) {
        for (int i = 0; i < MAX_OPERATIONS; i++) {
          S set = getSet();
          set.add(getElement(i));

          // Make a collection of elements to remove
          Collection<E> elements = new ArrayList<>();
          for (int j = start; j < stop; j++) {
            elements.add(getElement(j));
          }

          set.retainAll(elements);

          // Check which elements remain
          for (int j = 0; j < MAX_OPERATIONS; j++) {
            assertEquals("Should only include the single element if it was retained.",
                i == j && (start <= j && j < stop), set.contains(getElement(j)));
          }
        }
      }
    }
  }

  /**
   * Test retaining elements from various sets.
   */
  @Test
  public void testRetainAll() {
    LOGGER.log(Level.INFO, "testRetainAll: Test retaining elements from various sets.");

    for (int start = 0; start < MAX_OPERATIONS; start++) {
      for (int stop = start; stop < MAX_OPERATIONS; stop++) {
        for (int i = 0; i < MAX_OPERATIONS; i++) {
          S set = getSet();
          for (int j = 0; j < i; j++) {
            set.add(getElement(j));
          }

          // Make a collection of elements to remove
          Collection<E> elements = new ArrayList<>();
          for (int j = start; j < stop; j++) {
            elements.add(getElement(j));
          }

          set.retainAll(elements);

          // Check which elements remain
          for (int j = 0; j < MAX_OPERATIONS; j++) {
            assertEquals("Should only include the element that were retained.",
                j < i && (start <= j && j < stop), set.contains(getElement(j)));
          }
        }
      }
    }
  }

  /**
   * Test the size of the set as elements are added and removed.
   */
  @Test
  public void testSize_AddRemove() {
    LOGGER.log(Level.INFO,
        "testSize_AddRemove: Test the size of the set as elements are added and removed.");

    // How many elements to add
    for (int i = 1; i <= MAX_OPERATIONS; i++) {
      // How many elements to remove
      for (int j = 1; j <= MAX_OPERATIONS; j++) {
        S set = getSet();

        // Populate the set
        for (int k = 1; k <= i; k++) {
          set.add(getElement(k));
          assertEquals(k, set.size());
        }

        // Remove elements
        for (int k = 1; k <= j; k++) {
          set.remove(getElement(k));
          assertEquals(i < k ? 0 : i - k, set.size());
        }
      }
    }
  }

  // TODO: more tests will null objects.

}
