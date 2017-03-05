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

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests of the
 */
public class AbstractUpdatableTest {

  private static final Logger LOGGER = Logger.getLogger(AbstractUpdatableTest.class.getName());

  /**
   * Test that the {@linkplain AbstractUpdatable} registers with the {@linkplain DeliveryChannel}
   * when an identifier is given.
   */
  @Test
  public void testInstatiate_Identifer() {
    DeliveryChannel deliveryChannel = Mockito.mock(DeliveryChannel.class);
    AbstractUpdatable instance = new AbstractUpdatableImpl(new Object(), deliveryChannel);

    Mockito.verify(deliveryChannel).register(instance);
    Mockito.verifyNoMoreInteractions(deliveryChannel);
  }

  /**
   * Test that the {@linkplain AbstractUpdatable} registers with the {@linkplain DeliveryChannel}
   * when no identifier is given.
   */
  @Test
  public void testInstatiate_NullIdentifer() {
    DeliveryChannel deliveryChannel = Mockito.mock(DeliveryChannel.class);
    AbstractUpdatable instance = new AbstractUpdatableImpl(null, deliveryChannel);

    Mockito.verify(deliveryChannel).register(instance);
    Mockito.verifyNoMoreInteractions(deliveryChannel);
  }

  /**
   * Test that an {@linkplain AbstractUpdatable} constructed with an identifier returns that
   * identifier.
   */
  @Test
  public void testGetIdentifier_Given() {
    LOGGER.log(Level.INFO,
        "testGetIdentifier_Given: Test that the instance its constructed with is returned.");
    Object identifier = new Object();
    DeliveryChannel deliveryChannel = Mockito.mock(DeliveryChannel.class);
    AbstractUpdatable instance = new AbstractUpdatableImpl(identifier, deliveryChannel);

    assertSame("The instance should return the identifier it was constructed with", identifier,
        instance.getIdentifier());
  }

  /**
   * Test that an {@linkplain AbstractUpdatable} constructed without an identifier returns the one
   * assigned by the {@linkplain DeliveryChannel}.
   */
  @Test
  public void testGetIdentifier_Assigned() {
    LOGGER.log(Level.INFO, "testGetIdentifier_Assigned: "
        + "Test that the identfier assigned by registeration is returned.");
    Object identifier = new Object();
    DeliveryChannel deliveryChannel = Mockito.mock(DeliveryChannel.class);
    Mockito.doReturn(identifier).doThrow(IllegalStateException.class).when(deliveryChannel)
        .register(Mockito.any(Updatable.class));
    AbstractUpdatable instance = new AbstractUpdatableImpl(null, deliveryChannel);

    assertSame("The instance should return the identifer it was assigned", identifier,
        instance.getIdentifier());
  }

  /**
   * Test of getDeliveryChannel method, of class AbstractUpdatable.
   */
  @Test
  public void testGetDeliveryChannel() {
    LOGGER.log(Level.INFO,
        "testGetDeliveryChannel: Test that the expected DeliveryChannel is returned.");
    DeliveryChannel deliveryChannel = Mockito.mock(DeliveryChannel.class);
    AbstractUpdatable instance = new AbstractUpdatableImpl(new Object(), deliveryChannel);

    assertSame("The instance should return the DeliveryChannel it was constructed with",
        deliveryChannel, instance.getDeliveryChannel());
  }

  public class AbstractUpdatableImpl extends AbstractUpdatable {

    public AbstractUpdatableImpl(Object identifier, DeliveryChannel deliveryChannel) {
      super(identifier, deliveryChannel);
    }

    @Override
    public void update(UpdateMessage message) throws DeliveryUpdateException {
      throw new UnsupportedOperationException("Not in test scope.");
    }

  }

}
