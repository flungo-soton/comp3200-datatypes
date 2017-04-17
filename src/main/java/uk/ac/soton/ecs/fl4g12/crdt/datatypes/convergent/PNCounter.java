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

import org.openimaj.citation.annotation.Reference;
import org.openimaj.citation.annotation.ReferenceType;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.CRDT;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.Counter;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryUpdateException;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.StateDeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.UpdateMessage;
import uk.ac.soton.ecs.fl4g12.crdt.order.HashVersionVector;
import uk.ac.soton.ecs.fl4g12.crdt.order.IntegerVersion;
import uk.ac.soton.ecs.fl4g12.crdt.order.LocalVersionVector;
import uk.ac.soton.ecs.fl4g12.crdt.order.LongVersion;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;
import uk.ac.soton.ecs.fl4g12.crdt.util.Arithmetic;
import uk.ac.soton.ecs.fl4g12.crdt.util.IntegerArithmetic;
import uk.ac.soton.ecs.fl4g12.crdt.util.LongArithmetic;

/**
 * A {@linkplain CRDT} {@linkplain Counter} using a positive and negative vector that are used to
 * count increments and decrements at each node.
 *
 * @param <E> the type of the counter value.
 * @param <K> the type of identifier used to identify nodes.
 */
@Reference(type = ReferenceType.Techreport,
    author = {"Shapiro, Marc", "Preguica, Nuno", "Baquero, Carlos", "Zawirski, Marek"},
    title = "A comprehensive study of Convergent and Commutative Replicated Data Types",
    institution = "inria", year = "2011", url = "https://hal.inria.fr/inria-00555588",
    pages = {"15", "16"})
public final class PNCounter<E extends Comparable<E>, K>
    extends AbstractCvRDT<K, E, PNCounterState<E, K>> implements Counter<E> {

  private final Arithmetic<E> arithmetic;
  private final LocalVersionVector<K, E> p;
  private final LocalVersionVector<K, E> n;

  /**
   * Construct a pn-counter that uses its two {@linkplain VersionVector}'s as state.
   *
   * @param arithmetic an {@link Arithmetic} instance which can add/subtract values of the same type
   *        as the counter.
   * @param initialVersion the initial {@link VersionVector} value to use for the {@code version},
   *        {@code p} and {@code n}. This should be a zero version for a new counter as the sum of
   *        the timestamps is used as the value of the counter.
   * @param identifier the identifier of this instance or {@code null} for it to be assigned by the
   *        {@link DeliveryChannel}.
   * @param deliveryChannel the {@link DeliveryChannel} which this object should communicate changes
   *        over.
   */
  public PNCounter(Arithmetic<E> arithmetic, VersionVector<K, E> initialVersion, K identifier,
      StateDeliveryChannel<K, PNCounterState<E, K>> deliveryChannel) {
    super(initialVersion, identifier, deliveryChannel);
    this.arithmetic = arithmetic;
    this.p = new LocalVersionVector<>(initialVersion.copy(), this.identifier);
    this.n = new LocalVersionVector<>(initialVersion.copy(), this.identifier);
  }

  @Override
  public synchronized void increment() {
    version.increment();
    p.increment();
    getDeliveryChannel().publish();
  }

  @Override
  public synchronized void decrement() {
    version.increment();
    n.increment();
    getDeliveryChannel().publish();
  }

  @Override
  public E value() {
    return arithmetic.sub(arithmetic.add(p.get().values()), n.get().values());
  }

  @Override
  public synchronized void update(PNCounterState<E, K> message) throws DeliveryUpdateException {
    version.sync(message.getVersion());
    p.sync(message.getP());
    n.sync(message.getN());
  }

  @Override
  public synchronized PNCounterState<E, K> snapshot() {
    return new PNCounterState<>(identifier, version, p, n);
  }

  @Override
  public String toString() {
    return "PNCounter{" + value() + '}';
  }

  /**
   * Construct a new {@linkplain Integer} value {@linkplain PNCounter}. It is expected that the
   * {@link DeliveryChannel} provided will assign the instance its identifier.
   *
   * @param <K> the type of the identifiers used for instances.
   * @param deliveryChannel the {@link DeliveryChannel} which can be used to deliver
   *        {@link UpdateMessage}s between instances of the same {@link PNCounter}.
   * @return a {@link PNCounter} with an {@link Integer} value.
   */
  public static <K> PNCounter<Integer, K> newIntegerPNCounter(
      StateDeliveryChannel<K, PNCounterState<Integer, K>> deliveryChannel) {
    return new PNCounter<>(IntegerArithmetic.getInstance(),
        new HashVersionVector<K, Integer>(new IntegerVersion()), null, deliveryChannel);
  }

  /**
   * Construct a new {@linkplain Long} value {@linkplain PNCounter}. It is expected that the
   * {@link DeliveryChannel} provided will assign the instance its identifier.
   *
   * @param <K> the type of the identifiers used for instances.
   * @param deliveryChannel the {@link DeliveryChannel} which can be used to deliver
   *        {@link UpdateMessage}s between instances of the same {@link PNCounter}.
   * @return a {@link PNCounter} with an {@link Long} value.
   */
  public static <K> PNCounter<Long, K> newLongPNCounter(
      StateDeliveryChannel<K, PNCounterState<Long, K>> deliveryChannel) {
    return new PNCounter<>(LongArithmetic.getInstance(),
        new HashVersionVector<K, Long>(new LongVersion()), null, deliveryChannel);
  }
}
