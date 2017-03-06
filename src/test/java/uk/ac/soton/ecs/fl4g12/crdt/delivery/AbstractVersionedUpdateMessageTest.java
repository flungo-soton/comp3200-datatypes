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
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 * Test for the {@linkplain AbstractVersionedUpdateMessage} class.
 */
public class AbstractVersionedUpdateMessageTest {

  private static final Logger LOGGER =
      Logger.getLogger(AbstractVersionedUpdateMessageTest.class.getName());

  private Object identifier;
  private VersionVector versionVector;
  private VersionVector versionVectorCopy;
  private AbstractVersionedUpdateMessage instance;

  @Before
  public void setUp() {
    identifier = Mockito.spy(new Object());
    versionVector = Mockito.mock(VersionVector.class);
    versionVectorCopy = Mockito.mock(VersionVector.class);
    Mockito.doReturn(versionVectorCopy).when(versionVector).copy();
    instance = new AbstractVersionedUpdatableImpl(identifier, versionVector);
  }

  @After
  public void tearDown() {
    identifier = null;
    versionVector = null;
    versionVectorCopy = null;
    instance = null;
  }

  /**
   * Test the constructors interaction with the provided arguments.
   */
  @Test
  public void testInstantiate() {
    LOGGER.log(Level.INFO,
        "testInstantiate: Test the constructors interaction with the provided arguments.");

    Mockito.verifyZeroInteractions(identifier);
    Mockito.verify(versionVector).copy();
    Mockito.verifyNoMoreInteractions(versionVector);
  }

  /**
   * Test that getIdentifier returns the same object as constructed with.
   */
  @Test
  public void testGetIdentifier() {
    LOGGER.log(Level.INFO, "testGetIdentifier: "
        + "Test that getIdentifier returns the same object as constructed with.");

    assertSame("getIdentifier should return the same object the instance was constructed with",
        identifier, instance.getIdentifier());
    Mockito.verifyZeroInteractions(identifier);
  }

  /**
   * Test that testGetVersionVector returns the same object as constructed with.
   */
  @Test
  public void testGetVersionVector() {
    LOGGER.log(Level.INFO, "testGetVersionVector: "
        + "Test that testGetVersionVector returns the same object as constructed with.");

    final VersionVector expected = Mockito.mock(VersionVector.class);
    Mockito.doReturn(expected).when(versionVectorCopy).copy();
    final VersionVector result = instance.getVersionVector();

    Mockito.verify(versionVectorCopy).copy();
    Mockito.verifyNoMoreInteractions(versionVectorCopy);

    assertSame(
        "testGetVersionVector should return a copy of the object the instance was constructed with",
        expected, result);
    assertNotSame(
        "testGetVersionVector should return a copy of the object the instance was constructed with",
        versionVectorCopy, result);
  }

  /**
   * Test of compareTo method, of class AbstractVersionedUpdateMessage.
   */
  @Test
  public void testCompareTo() {
    LOGGER.log(Level.INFO,
        "testCompareTo: " + "Test that compareTo compares the internal version vectors.");
    VersionVector otherVersion = Mockito.mock(VersionVector.class);
    VersionedUpdateMessage otherMessage = Mockito.mock(VersionedUpdateMessage.class);
    Mockito.doReturn(otherVersion).when(otherMessage).getVersionVector();

    final int expected = 123;
    Mockito.doReturn(expected).when(versionVectorCopy).compareTo(otherVersion);

    int result = instance.compareTo(otherMessage);

    Mockito.verify(otherMessage).getVersionVector();
    Mockito.verifyNoMoreInteractions(otherMessage);
    Mockito.verify(versionVectorCopy).compareTo(otherVersion);
    Mockito.verifyNoMoreInteractions(versionVectorCopy);

    assertEquals(expected, result);
  }

  public class AbstractVersionedUpdatableImpl extends AbstractVersionedUpdateMessage {

    public AbstractVersionedUpdatableImpl(Object identifier, VersionVector versionVector) {
      super(identifier, versionVector);
    }

  }

}
