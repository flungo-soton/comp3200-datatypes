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
import uk.ac.soton.ecs.fl4g12.crdt.order.HashVersionVector;
import uk.ac.soton.ecs.fl4g12.crdt.order.IntegerVersion;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 *
 */
public class AbstractVersionedUpdatableTest {

  private static final Logger LOGGER =
      Logger.getLogger(AbstractVersionedUpdatableTest.class.getName());

  private void testGetVersion(VersionVector version) {
    AbstractVersionedUpdatable instance = new AbstractVersionedUpdatableImpl(version);

    VersionVector result1 = instance.getVersion();
    assertTrue("The version should be identical to the one provided", result1.identical(version));
    assertNotSame("The given vector should have been cloned", version, result1);

    // Changing the initial version should not change the one inside the AbstractVersionedUpdatable
    VersionVector originalVersion = version.copy();
    Object identifier = new Object();
    version.init(identifier);
    version.increment(identifier);

    VersionVector result2 = instance.getVersion();
    assertTrue("The version should be identical to the one provided",
        result2.identical(originalVersion));
    assertFalse("The version shouldn't be identical to the incremented one",
        result2.identical(version));
    assertTrue("The version should be identical to the previous version",
        result2.identical(result1));
  }

  /**
   * Test of getVersion method, of class AbstractVersionedUpdatable.
   */
  @Test
  public void testGetVersion_Zero() {
    LOGGER.log(Level.INFO,
        "testGetVersion_Zero: Test getVersion when initialised with a zero vector.");
    testGetVersion(new HashVersionVector(new IntegerVersion()));
  }

  /**
   * Test of getVersion method, of class AbstractVersionedUpdatable.
   */
  @Test
  public void testGetVersion_NonZero() {
    LOGGER.log(Level.INFO,
        "testGetVersion_Zero: Test getVersion when initialised with a non-zero vector.");
    VersionVector version = new HashVersionVector(new IntegerVersion());
    Object identifier = new Object();
    version.init(identifier);
    version.increment(identifier);

    testGetVersion(version);
  }

  public class AbstractVersionedUpdatableImpl extends AbstractVersionedUpdatable {

    public AbstractVersionedUpdatableImpl(VersionVector initialVersion) {
      super(initialVersion, new Object(), Mockito.mock(DeliveryChannel.class));
    }

    @Override
    public void update(UpdateMessage message) throws DeliveryUpdateException {
      throw new UnsupportedOperationException("Not in test scope.");
    }

  }

}
