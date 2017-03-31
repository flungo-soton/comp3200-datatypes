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

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

/**
 * Tests for the {@linkplain Dot} class.
 */
public final class DotTest {

  private static final Logger LOGGER = Logger.getLogger(DotTest.class.getName());

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private Dot getDot() {
    LogicalVersion version = Mockito.mock(LogicalVersion.class);
    return new Dot(new Object(), version);
  }

  /**
   * Test getIndentifier method of {@linkplain Dot}.
   */
  @Test
  public void testGetIdentifier() {
    LOGGER.log(Level.INFO, "testGetIdentifier: Testing getIndentifier method");

    Object identifier = new Object();
    LogicalVersion version = Mockito.mock(LogicalVersion.class);
    Dot dot = new Dot(identifier, version);

    assertSame(identifier, dot.getIdentifier());
  }

  /**
   * Test getLogicalVersion method of {@linkplain Dot}.
   */
  @Test
  public void testGetLogicalVersion() {
    LOGGER.log(Level.INFO, "testGetLogicalVersion: Testing getLogicalVersion method");

    LogicalVersion version = Mockito.mock(LogicalVersion.class);
    Dot dot = new Dot(new Object(), version);

    assertSame(version, dot.getLogicalVersion());
  }

  /**
   * Test get method of {@linkplain Dot}.
   */
  @Test
  public void testGet() {
    LOGGER.log(Level.INFO, "testGet: Testing get method");

    Dot dot = getDot();
    Comparable value = 123;
    Mockito.doReturn(value).when(dot.getLogicalVersion()).get();

    Object result = dot.get();

    assertEquals(value, result);
    Mockito.verify(dot.getLogicalVersion()).get();
    Mockito.verifyNoMoreInteractions(dot.getLogicalVersion());
  }

  /**
   * Test increment method of {@linkplain Dot}.
   */
  @Test
  public void testIncrement() {
    LOGGER.log(Level.INFO, "testIncrement: Testing increment method");

    Dot dot = getDot();

    dot.increment();

    Mockito.verify(dot.getLogicalVersion()).increment();
    Mockito.verifyNoMoreInteractions(dot.getLogicalVersion());
  }

  /**
   * Test successor method of {@linkplain Dot}.
   */
  @Test
  public void testSuccessor() {
    LOGGER.log(Level.INFO, "testSuccessor: Testing successor method");

    Dot dot = getDot();
    Comparable successor = 124;
    Mockito.doReturn(successor).when(dot.getLogicalVersion()).successor();

    Comparable result = dot.successor();

    assertEquals(successor, result);
    Mockito.verify(dot.getLogicalVersion()).successor();
    Mockito.verifyNoMoreInteractions(dot.getLogicalVersion());
  }

  /**
   * Test precedes method of {@linkplain Dot} which takes a {@linkplain Version}.
   */
  @Test
  public void testPrecedes_Version() {
    LOGGER.log(Level.INFO, "testPrecedes_Version: Testing precedes method with a Version");

    Dot dot = getDot();
    Version version = Mockito.mock(Version.class);
    boolean precedes = true;
    Mockito.doReturn(precedes).when(dot.getLogicalVersion()).precedes(version);

    Comparable result = dot.precedes(version);

    assertEquals(precedes, result);
    Mockito.verify(dot.getLogicalVersion()).precedes(version);
    Mockito.verifyNoMoreInteractions(dot.getLogicalVersion());
  }

  /**
   * Test precedes method of {@linkplain Dot} which takes a {@linkplain VersionVector}.
   */
  @Test
  public void testPrecedes_VersionVector() {
    LOGGER.log(Level.INFO,
        "testPrecedes_VersionVector: Testing precedes method with a VersionVector");

    Dot dot = getDot();
    VersionVector versionVector = Mockito.mock(VersionVector.class);
    Version version = Mockito.mock(LogicalVersion.class);
    Mockito.doReturn(version).when(versionVector).getLogicalVersion(dot.getIdentifier());
    boolean precedes = true;
    Mockito.doReturn(precedes).when(dot.getLogicalVersion()).precedes(version);

    Comparable result = dot.precedes(versionVector);

    assertEquals(precedes, result);
    Mockito.verify(versionVector).getLogicalVersion(dot.getIdentifier());
    Mockito.verifyNoMoreInteractions(versionVector);
    Mockito.verify(dot.getLogicalVersion()).precedes(version);
    Mockito.verifyNoMoreInteractions(dot.getLogicalVersion());
  }

  /**
   * Test identical method of {@linkplain Dot}.
   */
  @Test
  public void testIdentical() {
    LOGGER.log(Level.INFO, "testIdentical: Testing identical method");

    Dot dot = getDot();
    Version version = Mockito.mock(Version.class);
    boolean identical = true;
    Mockito.doReturn(identical).when(dot.getLogicalVersion()).identical(version);

    Comparable result = dot.identical(version);

    assertEquals(identical, result);
    Mockito.verify(dot.getLogicalVersion()).identical(version);
    Mockito.verifyNoMoreInteractions(dot.getLogicalVersion());
  }

  /**
   * Test happenedBefore method of {@linkplain Dot}.
   */
  @Test
  public void testHappenedBefore_Version() {
    LOGGER.log(Level.INFO, "testHappenedBefore: Testing happenedBefore_Version method");

    Dot dot = getDot();
    Version version = Mockito.mock(Version.class);
    boolean happenedBefore = true;
    Mockito.doReturn(happenedBefore).when(dot.getLogicalVersion()).happenedBefore(version);

    Comparable result = dot.happenedBefore(version);

    assertEquals(happenedBefore, result);
    Mockito.verify(dot.getLogicalVersion()).happenedBefore(version);
    Mockito.verifyNoMoreInteractions(dot.getLogicalVersion());
  }

  /**
   * Test happenedBefore method of {@linkplain Dot}.
   */
  @Test
  public void testHappenedBefore_VersionVector() {
    LOGGER.log(Level.INFO, "testHappenedBefore: Testing identical method");

    Dot dot = getDot();
    VersionVector versionVector = Mockito.mock(VersionVector.class);
    Version version = Mockito.mock(LogicalVersion.class);
    Mockito.doReturn(version).when(versionVector).getLogicalVersion(dot.getIdentifier());
    boolean happenedBefore = true;
    Mockito.doReturn(happenedBefore).when(dot.getLogicalVersion()).happenedBefore(version);

    Comparable result = dot.happenedBefore(versionVector);

    assertEquals(happenedBefore, result);
    Mockito.verify(versionVector).getLogicalVersion(dot.getIdentifier());
    Mockito.verifyNoMoreInteractions(versionVector);
    Mockito.verify(dot.getLogicalVersion()).happenedBefore(version);
    Mockito.verifyNoMoreInteractions(dot.getLogicalVersion());
  }

  /**
   * Test sync method of {@linkplain Dot} which takes a timestamp.
   */
  @Test
  public void testSync_T() {
    LOGGER.log(Level.INFO, "testSync_T: Testing sync method with a timestamp");

    Dot dot = getDot();
    Comparable version = Mockito.mock(Version.class);

    dot.sync(version);

    Mockito.verify(dot.getLogicalVersion()).sync(version);
    Mockito.verifyNoMoreInteractions(dot.getLogicalVersion());
  }

  /**
   * Test sync method of {@linkplain Dot} which takes a {@linkplain Version}.
   */
  @Test
  public void testSync_Version() {
    LOGGER.log(Level.INFO, "testSync_Version: Testing sync method with a Version");

    Dot dot = getDot();
    Version version = Mockito.mock(Version.class);

    dot.sync(version);

    Mockito.verify(dot.getLogicalVersion()).sync(version);
    Mockito.verifyNoMoreInteractions(dot.getLogicalVersion());
  }

  /**
   * Test sync method of {@linkplain Dot} which takes a {@linkplain VersionVector}.
   */
  @Test
  public void testSync_VersionVector() {
    LOGGER.log(Level.INFO, "testSync_Version: Testing sync method with a VersionVector");

    Dot dot = getDot();
    VersionVector versionVector = Mockito.mock(VersionVector.class);
    Comparable timestamp = 123;
    Mockito.doReturn(timestamp).when(versionVector).get(dot.getIdentifier());

    dot.sync(versionVector);

    Mockito.verify(dot.getLogicalVersion()).sync(timestamp);
    Mockito.verifyNoMoreInteractions(dot.getLogicalVersion());
  }

  /**
   * Test getDot method of {@linkplain Dot}.
   */
  @Test
  public void testCompareTo() {
    LOGGER.log(Level.INFO, "testCompareTo: Testing compareTo");

    Dot dot = getDot();
    Version version = Mockito.mock(Version.class);
    int comparison = -123;
    Mockito.doReturn(comparison).when(dot.getLogicalVersion()).compareTo(version);

    int result = dot.compareTo(version);

    assertEquals("", comparison, result);
    Mockito.verify(dot.getLogicalVersion()).compareTo(version);
    Mockito.verifyNoMoreInteractions(dot.getLogicalVersion());
  }

  /**
   * Test getDot method of {@linkplain Dot}.
   */
  @Test
  public void testCopy() {
    LOGGER.log(Level.INFO, "testCopy: Testing copy method");

    Dot<Object, Integer> dot = getDot();
    LogicalVersion copyVersion = Mockito.mock(LogicalVersion.class);
    Mockito.doReturn(copyVersion).when(dot.getLogicalVersion()).copy();

    Dot<Object, Integer> copy = dot.copy();

    assertSame("Identifier should be the same", dot.getIdentifier(), copy.getIdentifier());
    assertNotSame("The logical versions should not be the same", dot.getLogicalVersion(),
        copy.getLogicalVersion());
    assertSame("Logical version should be a copy", copyVersion, copy.getLogicalVersion());
  }

}
