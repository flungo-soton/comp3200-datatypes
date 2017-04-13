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

package uk.ac.soton.ecs.fl4g12.crdt.datatypes.commutative;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.openimaj.citation.annotation.Reference;
import org.openimaj.citation.annotation.ReferenceType;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.IllegalInsertionException;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.commutative.SetUpdateMessage.Operation;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.convergent.GSet;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.order.HashVersionVector;
import uk.ac.soton.ecs.fl4g12.crdt.order.LogicalVersion;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.ReliableDeliveryChannel;

/**
 * A commutative grow-only set. As a commutative implementation, the update messages only contain
 * the new elements lowering the network overhead of updates but it requires a
 * {@link ReliableDeliveryChannel}.
 *
 * When a concurrent add/remove occurs, this implementation is designed as a remove-wins
 * implementation.
 *
 * The ordering enforced by the {@link AbstractDottedCmRDT} ensures that the state of the local object
 * cannot be changed by an update message that is out of order for the node it is being delivered
 * from and as such, that no messages have been missed.
 *
 * @param <E> the type of values stored in the {@link GSet}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp stored in the {@link VersionVector}
 */
@Reference(type = ReferenceType.Techreport,
    author = {"Shapiro, Marc", "Preguica, Nuno", "Baquero, Carlos", "Zawirski, Marek"},
    title = "A comprehensive study of Convergent and Commutative Replicated Data Types",
    institution = "inria", year = "2011", url = "https://hal.inria.fr/inria-00555588",
    pages = {"22", "23"})
public final class CommutativeTwoPhaseSet<E, K, T extends Comparable<T>>
    extends AbstractDottedCmRDT<K, T, CommutativeTwoPhaseSetUpdate<E, K, T>> implements Set<E> {

  private final Set<E> additions = new HashSet<>();
  private final Set<E> removals = new HashSet<>();

  /**
   * Construct a {@linkplain GSet}, grow-only set.
   *
   * @param initialVersion the initial {@link VersionVector} value to use for the {@code version},
   *        {@code p} and {@code n}. This should be a zero version for a new counter as the sum of
   *        the timestamps is used as the value of the counter.
   * @param identifier the identifier of this instance or {@code null} for it to be assigned by the
   *        {@link DeliveryChannel}.
   * @param deliveryChannel the {@link DeliveryChannel} which this object should communicate changes
   *        over.
   */
  public CommutativeTwoPhaseSet(VersionVector<K, T> initialVersion, K identifier,
      ReliableDeliveryChannel<K, CommutativeTwoPhaseSetUpdate<E, K, T>> deliveryChannel) {
    super(initialVersion, identifier, deliveryChannel);
  }

  /**
   * Construct a {@linkplain GSet}, grow-only set, using a {@linkplain HashVersionVector}.
   *
   * @param zero the {@link LogicalVersion} representing {@code zero} to use when initialising
   *        identifiers in the {@link HashVersionVector}.
   * @param identifier the identifier of this instance or {@code null} for it to be assigned by the
   *        {@link DeliveryChannel}.
   * @param deliveryChannel the {@link DeliveryChannel} which this object should communicate changes
   *        over.
   */
  public CommutativeTwoPhaseSet(LogicalVersion<T, ?> zero, K identifier,
      ReliableDeliveryChannel<K, CommutativeTwoPhaseSetUpdate<E, K, T>> deliveryChannel) {
    this(new HashVersionVector<K, T>(zero), identifier, deliveryChannel);
  }

  @Override
  protected synchronized void applyUpdate(CommutativeTwoPhaseSetUpdate<E, K, T> message) {
    // Add to the add set regardless of if this is an add or remove
    additions.addAll(message.getElements());
    // Add to the remove set if the operation was a remove operation
    if (message.getOperation() == Operation.REMOVE) {
      removals.addAll(message.getElements());
    }
  }

  private CommutativeTwoPhaseSetUpdate<E, K, T> createUpdateMessage(Operation operation,
      E... elements) {
    return createUpdateMessage(operation, new HashSet<>(Arrays.asList(elements)));
  }

  private CommutativeTwoPhaseSetUpdate<E, K, T> createUpdateMessage(Operation operation,
      Set<? extends E> elements) {
    return new CommutativeTwoPhaseSetUpdate<E, K, T>(version.getDot(identifier), operation,
        elements);
  }

  @Override
  public synchronized boolean add(E element) {
    if (additions.add(element)) {
      version.increment();
      getDeliveryChannel().publish(createUpdateMessage(Operation.ADD, element));
      return true;
    } else if (removals.contains(element)) {
      throw new IllegalInsertionException("Can't add an element that has already been removed.",
          element);
    }
    return false;
  }

  @Override
  public synchronized boolean addAll(Collection<? extends E> collection) {
    // If there are no elements to add, return early.
    if (collection.isEmpty()) {
      return false;
    }

    // Determine if attempting to add elements that have already been removed.
    HashSet<? extends E> failedElements = new HashSet<>(collection);
    failedElements.retainAll(removals);
    if (!failedElements.isEmpty()) {
      throw new IllegalInsertionException(failedElements);
    }

    // Add the elements
    Set<E> elements = new HashSet<>(collection.size());
    for (E element : collection) {
      if (additions.add(element)) {
        elements.add(element);
      }
    }
    if (!elements.isEmpty()) {
      version.increment();
      getDeliveryChannel().publish(createUpdateMessage(Operation.ADD, elements));
      return true;
    }
    return false;
  }

  @Override
  public boolean remove(Object object) {
    try {
      E element = (E) object;
      synchronized (this) {
        version.increment();
        boolean added = additions.add(element);
        boolean removed = removals.add(element);
        if (removed) {
          getDeliveryChannel().publish(createUpdateMessage(Operation.REMOVE, element));
        }
        return !added && removed;
      }
    } catch (ClassCastException ex) {
      // Catch the exception, element can't have been an element of removals and so false will be
      // returned.
      return false;
    }
  }

  @Override
  public boolean removeAll(Collection<?> collection) {
    final Collection<E> elements = new HashSet<>();
    for (Object obj : collection) {
      try {
        elements.add((E) obj);
      } catch (ClassCastException ex) {
        // Catch the exception, element can't have been an element of removals and so we won't try
        // to remove it.
      }
    }
    return removeAllInternal(elements);
  }

  /**
   * Do the operation of removing elements from the set.
   *
   * @param collection the collection of elements to remove.
   * @return true if an existing element was removed.
   */
  protected boolean removeAllInternal(Collection<? extends E> collection) {
    if (collection.isEmpty()) {
      return false;
    }
    synchronized (this) {
      Set<E> elements = new HashSet<>();
      boolean modified = false;
      for (E element : collection) {
        // If its already in the remove set, assume it doesn't need to be added to the additions set
        if (removals.add(element)) {
          if (!additions.add(element)) {
            // If the element already existed then the set can be considered modified.
            modified = true;
          }
          elements.add(element);
        }
      }
      if (!elements.isEmpty()) {
        version.increment();
        getDeliveryChannel().publish(createUpdateMessage(Operation.REMOVE, elements));
      }
      return modified;
    }
  }

  @Override
  public synchronized boolean retainAll(Collection<?> c) {
    final Collection<E> remove = new HashSet<>();
    for (E element : this) {
      if (!c.contains(element)) {
        remove.add(element);
      }
    }
    return removeAllInternal(remove);
  }

  @Override
  public synchronized void clear() {
    // Avoid publishing to the network if there are no changes to make.
    if (isEmpty()) {
      return;
    }

    Set<E> elements = new HashSet<>();
    for (E element : additions) {
      if (removals.add(element)) {
        elements.add(element);
      }
    }
    if (!elements.isEmpty()) {
      version.increment();
      getDeliveryChannel().publish(createUpdateMessage(Operation.REMOVE, elements));
    }
  }

  @Override
  public boolean contains(Object o) {
    return additions.contains(o) && !removals.contains(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return getElements().containsAll(c);
  }

  @Override
  public int size() {
    return additions.size() - removals.size();
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  protected HashSet<E> getElements() {
    HashSet<E> elements;
    HashSet<E> notElements;
    synchronized (this) {
      elements = new HashSet<>(additions);
      notElements = new HashSet<>(removals);
    }
    elements.removeAll(notElements);
    return elements;
  }

  @Override
  public Iterator<E> iterator() {
    return new CommutativeTwoPhaseSet.IteratorWrapper(getElements().iterator());
  }

  @Override
  public Object[] toArray() {
    return getElements().toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return getElements().toArray(a);
  }

  @Override
  public boolean equals(Object o) {
    return getElements().equals(o);
  }

  @Override
  public int hashCode() {
    return getElements().hashCode();
  }

  @Override
  public String toString() {
    return getElements().toString();
  }

  /**
   * Wrapper for a {@linkplain Set} {@link Iterator} for grow-only set implementations.
   */
  public class IteratorWrapper implements Iterator<E> {

    private final Iterator<E> iterator;
    private boolean hasNexted = false;
    private E current = null;

    private IteratorWrapper(Iterator<E> iterator) {
      this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override
    public synchronized E next() {
      current = iterator.next();
      hasNexted = true;
      return current;
    }

    @Override
    public synchronized void remove() {
      if (!hasNexted) {
        throw new IllegalStateException();
      }
      CommutativeTwoPhaseSet.this.remove(current);
      hasNexted = false;
    }

  }

}
