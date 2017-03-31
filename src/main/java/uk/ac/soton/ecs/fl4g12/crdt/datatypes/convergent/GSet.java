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
import uk.ac.soton.ecs.fl4g12.crdt.delivery.AbstractVersionedUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryUpdateException;
import uk.ac.soton.ecs.fl4g12.crdt.order.HashVersionVector;
import uk.ac.soton.ecs.fl4g12.crdt.order.LogicalVersion;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Grow-only {@linkplain CvRDT} {@linkplain Set}.
 *
 * @param <E> the type of values stored in the {@link GSet}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp stored in the {@link VersionVector}
 */
public final class GSet<E, K, T extends Comparable<T>>
    extends AbstractVersionedUpdatable<K, T, GSetState<E, K, T>>
    implements CvRDT<K, T, GSetState<E, K, T>>, Set<E> {

  private final Set<E> state = new HashSet<>();

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
  public GSet(VersionVector<K, T> initialVersion, K identifier,
      DeliveryChannel<K, GSetState<E, K, T>> deliveryChannel) {
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
  public GSet(LogicalVersion<T> zero, K identifier,
      DeliveryChannel<K, GSetState<E, K, T>> deliveryChannel) {
    this(new HashVersionVector<K, T>(zero), identifier, deliveryChannel);
  }

  @Override
  public synchronized void update(GSetState<E, K, T> message) throws DeliveryUpdateException {
    state.addAll(message.getState());
    version.sync(message.getVersionVector());
  }

  @Override
  public synchronized GSetState<E, K, T> snapshot() {
    return new GSetState<>(getIdentifier(), version, state);
  }

  @Override
  public synchronized boolean add(E element) {
    if (state.add(element)) {
      version.increment();
      getDeliveryChannel().publish(snapshot());
      return true;
    }
    return false;
  }

  @Override
  public synchronized boolean addAll(Collection<? extends E> collection) {
    if (state.addAll(collection)) {
      version.increment();
      getDeliveryChannel().publish(snapshot());
      return true;
    }
    return false;
  }

  @Override
  public boolean contains(Object o) {
    return state.contains(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return state.containsAll(c);
  }

  @Override
  public int size() {
    return state.size();
  }

  @Override
  public boolean isEmpty() {
    return state.isEmpty();
  }

  @Override
  public Iterator<E> iterator() {
    return new IteratorWrapper(state.iterator());
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException("Cannot remove from a Grow-only set.");
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException("Cannot removeAll from a Grow-only set.");
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException("Cannot retainAll from a Grow-only set.");
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException("Cannot clear a Grow-only set.");
  }

  @Override
  public Object[] toArray() {
    return state.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return state.toArray(a);
  }

  @Override
  public boolean equals(Object o) {
    return state.equals(o);
  }

  @Override
  public int hashCode() {
    return state.hashCode();
  }

  @Override
  public String toString() {
    return state.toString();
  }

  /**
   * Wrapper for a {@linkplain Set} {@link Iterator} for grow-only set implementations.
   */
  public class IteratorWrapper implements Iterator<E> {

    private final Iterator<E> iterator;

    private IteratorWrapper(Iterator<E> iterator) {
      this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override
    public E next() {
      return iterator.next();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("Cannot remove from a Grow-only set.");
    }

  }

}
