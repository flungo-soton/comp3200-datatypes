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

package uk.ac.soton.ecs.fl4g12.crdt.datatypes.convergent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.IllegalInsertionException;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.AbstractVersionedUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryUpdateException;
import uk.ac.soton.ecs.fl4g12.crdt.order.HashVersionVector;
import uk.ac.soton.ecs.fl4g12.crdt.order.LogicalVersion;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Two-phase {@linkplain CvRDT} {@linkplain Set}. Elements can only be added to a set once and
 * cannot be re-added after removal.
 *
 * When an element that already exists in the set is added, it will succeed providing the element
 * has not yet been removed. When an element is removed it will succeed regardless of whether it has
 * been added or not. If the element is not a member of the set when removed, removing the element
 * by any means will mean that the element cannot be added to the set in the future and is effective
 * to an add followed by a remove. When adding, if the element has already been removed, an
 * {@link IllegalInsertionException}.
 *
 * @param <E> the type of values stored in the {@link GSet}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp stored in the {@link VersionVector}
 */
public final class TwoPhaseSet<E, K, T extends Comparable<T>>
    extends AbstractVersionedUpdatable<K, T, TwoPhaseSetState<E, K, T>>
    implements CvRDT<K, T, TwoPhaseSetState<E, K, T>>, Set<E> {

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
  public TwoPhaseSet(VersionVector<K, T> initialVersion, K identifier,
      DeliveryChannel<K, TwoPhaseSetState<E, K, T>> deliveryChannel) {
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
  public TwoPhaseSet(LogicalVersion<T> zero, K identifier,
      DeliveryChannel<K, TwoPhaseSetState<E, K, T>> deliveryChannel) {
    this(new HashVersionVector<K, T>(zero, false), identifier, deliveryChannel);
  }

  @Override
  public synchronized void update(TwoPhaseSetState<E, K, T> message)
      throws DeliveryUpdateException {
    additions.addAll(message.getAdditions());
    removals.addAll(message.getRemovals());
    version.sync(message.getVersionVector());
  }

  @Override
  public synchronized TwoPhaseSetState<E, K, T> snapshot() {
    return new TwoPhaseSetState<>(getIdentifier(), version, additions, removals);
  }

  @Override
  public synchronized boolean add(E element) {
    if (additions.add(element)) {
      version.increment();
      getDeliveryChannel().publish(snapshot());
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
    if (additions.addAll(collection)) {
      version.increment();
      getDeliveryChannel().publish(snapshot());
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

  protected boolean removeAllInternal(Collection<? extends E> collection) {
    if (collection.isEmpty()) {
      return false;
    }
    synchronized (this) {
      version.increment();
      boolean added = additions.addAll(collection);
      boolean removed = removals.addAll(collection);
      return !added && removed;
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

    version.increment();
    removals.addAll(additions);
    getDeliveryChannel().publish(snapshot());
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
    return new IteratorWrapper(getElements().iterator()); // TODO: Implement remove
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
      TwoPhaseSet.this.remove(current);
      hasNexted = false;
    }

  }

}
