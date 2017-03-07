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

package uk.ac.soton.ecs.fl4g12.crdt.order;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for the {@linkplain LocalVersionVector} class.
 */
public class LocalVersionVectorTest {

  private static final Logger LOGGER = Logger.getLogger(LocalVersionVectorTest.class.getName());

  private VersionVector version;
  private Object identifier;
  private LocalVersionVector instance;

  @Before
  public void setUp() {
    version = Mockito.mock(VersionVector.class, Mockito.CALLS_REAL_METHODS);
    identifier = Mockito.mock(Object.class);
    instance = new LocalVersionVector(version, identifier);
  }

  @After
  public void tearDown() {
    version = null;
    identifier = null;
    instance = null;
  }

  /**
   * Test the instantiation of the {@link LocalVersionVector}.
   */
  @Test
  public void testInstantiate() {
    LOGGER.log(Level.INFO, "testInstantiate");
    Mockito.verify(version).init(identifier);
    Mockito.verifyNoMoreInteractions(version);
  }

  /**
   * Test of getVersionVector method, of class LocalVersionVector.
   */
  @Test
  public void testGetVersionVector() {
    LOGGER.log(Level.INFO, "getVersionVector");
    Mockito.reset(version);
    assertSame(version, instance.getVersionVector());
  }

  /**
   * Test of getIdentifier method, of class LocalVersionVector.
   */
  @Test
  public void testGetIdentifier() {
    LOGGER.log(Level.INFO, "getIdentifier");
    Mockito.reset(version);
    assertSame(identifier, instance.getIdentifier());
  }

  /**
   * Test of get method, of class LocalVersionVector.
   */
  @Test
  public void testGet_0args() {
    LOGGER.log(Level.INFO, "get_0args");
    Mockito.reset(version);
    instance.get();
    Mockito.verify(version).get();
    Mockito.verifyNoMoreInteractions(version);
  }

  /**
   * Test of get method, of class LocalVersionVector.
   */
  @Test
  public void testGet_GenericType() {
    LOGGER.log(Level.INFO, "get_GenericType");
    Mockito.reset(version);
    Object id = new Object();
    instance.get(id);
    Mockito.verify(version).get(id);
    Mockito.verifyNoMoreInteractions(version);
  }

  /**
   * Test of getIdentifiers method, of class LocalVersionVector.
   */
  @Test
  public void testGetIdentifiers() {
    LOGGER.log(Level.INFO, "getIdentifiers");
    Mockito.reset(version);
    instance.getIdentifiers();
    Mockito.verify(version).getIdentifiers();
    Mockito.verifyNoMoreInteractions(version);
  }

  /**
   * Test of init method, of class LocalVersionVector.
   */
  @Test
  public void testInit() {
    LOGGER.log(Level.INFO, "init");
    Mockito.reset(version);
    Object id = new Object();
    instance.init(id);
    Mockito.verify(version).init(id);
    Mockito.verifyNoMoreInteractions(version);
  }

  /**
   * Test of increment method, of class LocalVersionVector.
   */
  @Test
  public void testIncrement_GenericType() {
    LOGGER.log(Level.INFO, "increment_GenericType");
    Mockito.reset(version);
    Object id = new Object();
    instance.increment(id);
    Mockito.verify(version).increment(id);
    Mockito.verifyNoMoreInteractions(version);
  }

  /**
   * Test of sync method, of class LocalVersionVector.
   */
  @Test
  public void testSync_GenericType_GenericType() {
    LOGGER.log(Level.INFO, "sync_GenericType_GenericType");
    Mockito.reset(version);
    Object id = new Object();
    Comparable value = Mockito.mock(Comparable.class);
    instance.sync(id, value);
    Mockito.verify(version).sync(id, value);
    Mockito.verifyNoMoreInteractions(version);
  }

  /**
   * Test of sync method, of class LocalVersionVector.
   */
  @Test
  public void testSync_Version() {
    LOGGER.log(Level.INFO, "sync_Version");
    Mockito.reset(version);
    Version syncVersion = Mockito.mock(Version.class);
    instance.sync(syncVersion);
    Mockito.verify(version).sync(syncVersion);
    Mockito.verifyNoMoreInteractions(version);
  }

  /**
   * Test of sync method, of class LocalVersionVector.
   */
  @Test
  public void testSync_Map() {
    LOGGER.log(Level.INFO, "sync_Map");
    Mockito.reset(version);
    Map map = Mockito.mock(Map.class);
    instance.sync(map);
    Mockito.verify(version).sync(map);
    Mockito.verifyNoMoreInteractions(version);
  }

  /**
   * Test of concurrentWith method, of class LocalVersionVector.
   */
  @Test
  public void testConcurrentWith() {
    LOGGER.log(Level.INFO, "concurrentWith");
    Mockito.reset(version);
    VersionVector vector = Mockito.mock(VersionVector.class);
    instance.concurrentWith(vector);
    Mockito.verify(version).concurrentWith(vector);
    Mockito.verifyNoMoreInteractions(version);
  }

  /**
   * Test of happenedBefore method, of class LocalVersionVector.
   */
  @Test
  public void testHappenedBefore() {
    LOGGER.log(Level.INFO, "happenedBefore");
    Mockito.reset(version);
    VersionVector vector = Mockito.mock(VersionVector.class);
    instance.happenedBefore(vector);
    Mockito.verify(version).happenedBefore(vector);
    Mockito.verifyNoMoreInteractions(version);
  }

  /**
   * Test of compareTo method, of class LocalVersionVector.
   */
  @Test
  public void testCompareTo() {
    LOGGER.log(Level.INFO, "compareTo");
    Mockito.reset(version);
    VersionVector vector = Mockito.mock(VersionVector.class);
    instance.compareTo(vector);
    Mockito.verify(version).compareTo(vector);
    Mockito.verifyNoMoreInteractions(version);
  }

  /**
   * Test of isDotted method, of class LocalVersionVector.
   */
  @Test
  public void testIsDotted() {
    LOGGER.log(Level.INFO, "isDotted");
    Mockito.reset(version);
    instance.isDotted();
    Mockito.verify(version).isDotted();
    Mockito.verifyNoMoreInteractions(version);
  }

  /**
   * Test of identical method, of class LocalVersionVector.
   */
  @Test
  public void testIdentical() {
    LOGGER.log(Level.INFO, "identical");
    Mockito.reset(version);
    Version vector = Mockito.mock(Version.class);
    instance.identical(vector);
    Mockito.verify(version).identical(vector);
    Mockito.verifyNoMoreInteractions(version);
  }

  /**
   * Test of increment method, of class LocalVersionVector.
   */
  @Test
  public void testIncrement_0args() {
    LOGGER.log(Level.INFO, "increment");
    Mockito.reset(version);
    instance.increment();
    Mockito.verify(version).increment(identifier);
    Mockito.verifyNoMoreInteractions(version);
  }

  /**
   * Test of copy method, of class LocalVersionVector.
   */
  @Test
  public void testCopy() {
    LOGGER.log(Level.INFO, "copy");
    Mockito.reset(version);

    // Setup the mock version
    VersionVector versionCopy = Mockito.mock(VersionVector.class, Mockito.CALLS_REAL_METHODS);
    Mockito.doReturn(versionCopy).when(version).copy();

    // Copy it
    LocalVersionVector copy = instance.copy();

    // Make assertions
    Mockito.verify(version).copy();
    Mockito.verifyNoMoreInteractions(version);
    Mockito.verify(versionCopy).init(identifier);
    Mockito.verifyNoMoreInteractions(versionCopy);
    assertSame("Copies should have the same identifier", instance.getIdentifier(),
        copy.getIdentifier());
    assertSame("The copied version vector should be the versionCopy", versionCopy,
        copy.getVersionVector());
    // assertEquals("The copy should be equal", instance, copy); // version != versionCopy
    assertNotSame("The copy should not be the same object", instance, copy);
  }

}
