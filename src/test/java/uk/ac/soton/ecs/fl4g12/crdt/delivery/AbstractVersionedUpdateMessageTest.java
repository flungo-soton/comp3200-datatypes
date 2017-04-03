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
import uk.ac.soton.ecs.fl4g12.crdt.order.Version;

/**
 * Test for the {@linkplain AbstractVersionedUpdateMessage} class.
 */
public class AbstractVersionedUpdateMessageTest {

  private static final Logger LOGGER =
      Logger.getLogger(AbstractVersionedUpdateMessageTest.class.getName());

  private Object identifier;
  private Version version;
  private Version versionCopy;
  private AbstractVersionedUpdateMessage instance;

  @Before
  public void setUp() {
    identifier = Mockito.spy(new Object());
    version = Mockito.mock(Version.class);
    versionCopy = Mockito.mock(Version.class);
    Mockito.doReturn(versionCopy).when(version).copy();
    instance = new AbstractVersionedUpdatableImpl(identifier, version);
  }

  @After
  public void tearDown() {
    identifier = null;
    version = null;
    versionCopy = null;
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
    Mockito.verify(version).copy();
    Mockito.verifyNoMoreInteractions(version);
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
   * Test that getVersion returns the same object as constructed with.
   */
  @Test
  public void testGetVersion() {
    LOGGER.log(Level.INFO, "testGetVersion: "
        + "Test that testGetVersion returns the same object as constructed with.");

    final Version expected = Mockito.mock(Version.class);
    Mockito.doReturn(expected).when(versionCopy).copy();
    final Version result = instance.getVersion();

    Mockito.verify(versionCopy).copy();
    Mockito.verifyNoMoreInteractions(versionCopy);

    assertSame(
        "testGetVersion should return a copy of the object the instance was constructed with",
        expected, result);
    assertNotSame(
        "testGetVersion should return a copy of the object the instance was constructed with",
        versionCopy, result);
  }

  /**
   * Test of compareTo method, of class AbstractVersionedUpdateMessage.
   */
  @Test
  public void testCompareTo() {
    LOGGER.log(Level.INFO,
        "testCompareTo: " + "Test that compareTo compares the internal version vectors.");
    Version otherVersion = Mockito.mock(Version.class);
    VersionedUpdateMessage otherMessage = Mockito.mock(VersionedUpdateMessage.class);
    Mockito.doReturn(otherVersion).when(otherMessage).getVersion();

    final int expected = 123;
    Mockito.doReturn(expected).when(versionCopy).compareTo(otherVersion);

    int result = instance.compareTo(otherMessage);

    Mockito.verify(otherMessage).getVersion();
    Mockito.verifyNoMoreInteractions(otherMessage);
    Mockito.verify(versionCopy).compareTo(otherVersion);
    Mockito.verifyNoMoreInteractions(versionCopy);

    assertEquals(expected, result);
  }

  public class AbstractVersionedUpdatableImpl extends AbstractVersionedUpdateMessage {

    public AbstractVersionedUpdatableImpl(Object identifier, Version version) {
      super(identifier, version);
    }

  }

}
