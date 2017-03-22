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

import java.util.Collection;
import java.util.HashSet;
import static org.junit.Assert.assertNotNull;
import org.mockito.Mockito;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.CausalDeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.Updatable;
import uk.ac.soton.ecs.fl4g12.crdt.idenitifier.IncrementalIntegerIdentifierFactory;
import uk.ac.soton.ecs.fl4g12.crdt.order.IntegerVersion;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Tests for the {@linkplain CommutativeGSetUpdate} implementation.
 */
public final class CommutativeGSetCommutativityTest extends
    GrowableSetCommutativityAbstractTest<Integer, Integer, Integer, CommutativeGSetUpdate<Integer, Integer, Integer>, CommutativeGSet<Integer, Integer, Integer>> {

  private static final IncrementalIntegerIdentifierFactory ID_FACTORY =
      new IncrementalIntegerIdentifierFactory();

  @Override
  protected CommutativeGSet<Integer, Integer, Integer> getSet() {
    CausalDeliveryChannel<Integer, Integer, CommutativeGSetUpdate<Integer, Integer, Integer>> deliveryChannel =
        Mockito.mock(CausalDeliveryChannel.class);
    Mockito.doReturn(ID_FACTORY.create()).doThrow(IllegalStateException.class).when(deliveryChannel)
        .register(Mockito.any(Updatable.class));
    return new CommutativeGSet<>(new IntegerVersion(), null, deliveryChannel);
  }

  @Override
  protected Integer getElement(int i) {
    return i;
  }

  @Override
  protected CommutativeGSetUpdate<Integer, Integer, Integer> getAddUpdate(
      CommutativeGSet<Integer, Integer, Integer> set, Integer identifier,
      VersionVector<Integer, Integer> version, Collection<Integer> elements) {
    return new CommutativeGSetUpdate<>(identifier, version, new HashSet<>(elements));
  }

  @Override
  protected void assertAddUpdate(CommutativeGSetUpdate<Integer, Integer, Integer> updateMessage) {
    // All CommutativeGSetUpdate are additions. Just check that the message is not null.
    assertNotNull("Update message should not be null", updateMessage);
  }

}
