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

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openimaj.citation.annotation.Reference;
import org.openimaj.citation.annotation.ReferenceType;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.Register;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryUpdateException;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.StateDeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Last-Writer-Wins {@linkplain Register}.
 *
 * Last-writer is determines by the first of the following to provide a total order:
 * <ul>
 * <li>Order of the {@link VersionVector};
 * <li>Order of the UTC timestamp stored with the element;
 * <li>Order of the unique identifiers (lowest wins).
 * </ul>
 *
 * Ordering by UTC timestamp requires that clock synchronisation is used between nodes to ensure
 * that the correct last writer is chosen.
 *
 * Sub-millisecond local reassignment will block until enough time has passed to distinguish the
 * values. Only the successful assignment will be replicated to other nodes.
 *
 * @param <E> the type of value stored in the {@link Register}.
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp stored in the {@link VersionVector}
 */
@Reference(type = ReferenceType.Techreport,
    author = {"Shapiro, Marc", "Preguica, Nuno", "Baquero, Carlos", "Zawirski, Marek"},
    title = "A comprehensive study of Convergent and Commutative Replicated Data Types",
    institution = "inria", year = "2011", url = "https://hal.inria.fr/inria-00555588",
    pages = {"17", "18", "19"})
public final class LWWRegister<E extends Serializable, K extends Comparable<K>, T extends Comparable<T>>
    extends AbstractCvRDT<K, T, LWWRegisterState<E, K, T>> implements Register<E> {

  private static final Logger LOGGER = Logger.getLogger(LWWRegister.class.getName());

  private final AtomicReference<Element<E>> element;

  /**
   * Construct a new Last-Writer-Wins {@linkplain Register}.
   *
   * @param initialVersion the initial {@link VersionVector} value to use. This should be a zero
   *        version.
   * @param identifier the identifier of this instance or {@code null} for it to be assigned by the
   *        {@link DeliveryChannel}.
   * @param deliveryChannel the {@link DeliveryChannel} which this object should communicate changes
   *        over.
   */
  public LWWRegister(VersionVector<K, T> initialVersion, K identifier,
      StateDeliveryChannel<K, LWWRegisterState<E, K, T>> deliveryChannel) {
    super(initialVersion, identifier, deliveryChannel);
    this.element = new AtomicReference<>(new Element<E>(null, 0));
  }

  @Override
  public synchronized void assign(E value) {
    version.increment();
    assign(new Element<>(value), identifier);
    getDeliveryChannel().publish();
  }

  /**
   * Assign a value to the internal {@linkplain AtomicReference} providing. It is assumed that
   * {@link VersionVector}s have been checked before performing this assignment.
   *
   * @param elem the element to assign
   * @param id the identifier of the node where the assignment was made.
   */
  private synchronized void assign(Element<E> elem, K id) {
    while (true) {
      Element<E> current = element.get();
      if (elem.getTimestamp() < current.getTimestamp()) {
        return;
      }
      if (current.getTimestamp() == elem.getTimestamp()) {
        // If the elements values are equal, nothing to do
        if (current.getValue().equals(elem.getValue())) {
          return;
        }
        // If a very fast local re-assignment is taking place, wait a 100 nanoseconds and retry
        // Sleep theoretically caps the max recursion depth at 10
        // Blocking is required to ensure the correctness of programs using this datatype.
        if (identifier.equals(id)) {
          try {
            Thread.sleep(0, 100);
          } catch (InterruptedException ex) {
            LOGGER.log(Level.WARNING,
                "Interupted exception while waiting between fast re-assignment. "
                    + "This will not cause a problem, retry will occur.",
                ex);
          }
          assign(new Element<>(elem.getValue()), id);
          return;
        }
        // Only continue to assigment if the new ID is less than the current ID
        if (identifier.compareTo(id) > 0) {
          return;
        }
      }
      if (element.compareAndSet(current, elem)) {
        return;
      }
    }
  }

  @Override
  public synchronized void update(LWWRegisterState<E, K, T> message)
      throws DeliveryUpdateException {
    if (message.getVersion().happenedBefore(version)) {
      return;
    }
    // If message is either concurrent on in future (not identical) perform assignment
    if (!message.getVersion().identical(version)) {
      version.sync(message.getVersion());
      assign(message.getElement(), message.getIdentifier());
    }
  }

  @Override
  public E value() {
    return element.get().getValue();
  }

  @Override
  public synchronized LWWRegisterState<E, K, T> snapshot() {
    return new LWWRegisterState<>(identifier, version, element.get());
  }

  @Override
  public String toString() {
    return "LWWRegister{" + value() + '}';
  }

  /**
   * A timestamped element wrapper. Contains an element and the time that the value was assigned.
   *
   * @param <E> the type of the value being wrapped.
   */
  public static class Element<E extends Serializable> implements Serializable {

    private final E value;
    private final long timestamp;

    /**
     * Construct a new {@linkplain Element}. The {@code timestamp} should be a UTC timestamp from
     * {@link System#currentTimeMillis()} at the time the element was first created.
     *
     * @param value the element value.
     * @param timestamp a UTC timestamp of when the value was assigned.
     */
    private Element(E value, long timestamp) {
      this.value = value;
      this.timestamp = timestamp;
    }

    /**
     * Construct a new {@linkplain Element} using the time of instantiation as the timestamp.
     *
     * @param value the element value.
     */
    private Element(E value) {
      this(value, System.currentTimeMillis());
    }

    /**
     * Get the value stores in the element.
     *
     * @return the value of the element.
     */
    public E getValue() {
      return value;
    }

    /**
     * Get the timestamp that the element was first created.
     *
     * @return the timestamp that the element was first created.
     */
    public long getTimestamp() {
      return timestamp;
    }

    @Override
    public int hashCode() {
      int hash = 7;
      hash = 97 * hash + Objects.hashCode(this.value);
      hash = 97 * hash + (int) (this.timestamp ^ (this.timestamp >>> 32));
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final Element<?> other = (Element<?>) obj;
      if (this.timestamp != other.timestamp) {
        return false;
      }
      if (!Objects.equals(this.value, other.value)) {
        return false;
      }
      return true;
    }

  }

}
