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

package uk.ac.soton.ecs.fl4g12.crdt.delivery;

import uk.ac.soton.ecs.fl4g12.crdt.order.LocalVersionVector;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Abstract {@linkplain VersionedUpdatable} extending the {@link AbstractUpdatable} and providing
 * the required functionality for versioning. Registers the {@link Updatable} with a
 * {@link DeliveryChannel} through the {@link AbstractUpdatable} and uses the provided identifier to
 * create a {@link LocalVersionVector}.
 *
 * @param <K> the type of identifier used to identify nodes.
 * @param <T> the type of the timestamp within the {@link VersionVector}.
 * @param <U> the type of {@link UpdateMessage} sent via the {@link DeliveryChannel}.
 */
public abstract class AbstractVersionedUpdatable<K, T extends Comparable<T>, U extends UpdateMessage<K, ?>>
    extends AbstractUpdatable<K, U> implements VersionedUpdatable<K, VersionVector<K, T>, U> {

  /**
   * The version used by the {@linkplain Updatable}. This should be used instead of
   * {@link #getVersion()} internally as it does not clone the version allowing increments to be
   * made.
   */
  protected final LocalVersionVector<K, T> version;

  /**
   * Instantiate the {@linkplain AbstractVersionedUpdatable} by wrapping a
   * {@linkplain LocalVersionVector} using the identifier assigned by the
   * {@linkplain AbstractUpdatable}.
   *
   * @param initialVersion the initial value of the version vector.
   * @param identifier the identifier for this instance or null if a new ID should be assigned by
   *        the {@link DeliveryChannel}.
   * @param deliveryChannel the {@link DeliveryChannel} to register with.
   */
  public AbstractVersionedUpdatable(VersionVector<K, T> initialVersion, K identifier,
      DeliveryChannel<K, U> deliveryChannel) {
    super(identifier, deliveryChannel);
    this.version = new LocalVersionVector<>(initialVersion.copy(), getIdentifier());
  }

  @Override
  public final VersionVector<K, T> getVersion() {
    return version.copy();
  }

}
