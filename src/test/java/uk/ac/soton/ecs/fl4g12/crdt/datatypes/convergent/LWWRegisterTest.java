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

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.mockito.Mockito;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.Register;
import uk.ac.soton.ecs.fl4g12.crdt.datatypes.RegisterAbstractTest;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.DeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.StateDeliveryChannel;
import uk.ac.soton.ecs.fl4g12.crdt.delivery.StatefulUpdatable;
import uk.ac.soton.ecs.fl4g12.crdt.idenitifier.IncrementalIntegerIdentifierFactory;
import uk.ac.soton.ecs.fl4g12.crdt.order.HashVersionVector;
import uk.ac.soton.ecs.fl4g12.crdt.order.IntegerVersion;
import uk.ac.soton.ecs.fl4g12.crdt.order.VersionVector;

/**
 *
 */
public class LWWRegisterTest
    extends RegisterAbstractTest<Integer, LWWRegister<Integer, Integer, Integer>> {

  private static final Logger LOGGER = Logger.getLogger(LWWRegisterTest.class.getName());

  private static final IncrementalIntegerIdentifierFactory ID_FACTORY =
      new IncrementalIntegerIdentifierFactory();

  @Override
  protected LWWRegister<Integer, Integer, Integer> getRegister() {
    StateDeliveryChannel<Integer, LWWRegisterState<Integer, Integer, Integer>> deliveryChannel =
        Mockito.mock(StateDeliveryChannel.class);
    Mockito.doReturn(ID_FACTORY.create()).doThrow(IllegalStateException.class).when(deliveryChannel)
        .register(Mockito.any(StatefulUpdatable.class));
    return new LWWRegister<>(new HashVersionVector<Integer, Integer>(new IntegerVersion()), null,
        deliveryChannel);
  }

  @Override
  protected Integer getValue(int i) {
    return i;
  }

  /**
   * Ensure that when the {@linkplain Register} is assigned to, that the change is published to the
   * {@linkplain DeliveryChannel}.
   */
  @Test
  public void testAssign_Publish() {
    LOGGER.log(Level.INFO, "testIncrement_Publish: "
        + "Ensure that when the register is incremented, that the change is published to the DeliveryChannel");
    final LWWRegister<Integer, Integer, Integer> register = getRegister();

    final VersionVector<Object, Integer> expectedVersionVector =
        new HashVersionVector<>(new IntegerVersion());
    expectedVersionVector.init(register.getIdentifier());

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      expectedVersionVector.increment(register.getIdentifier());
      final StateDeliveryChannel<Integer, LWWRegisterState<Integer, Integer, Integer>> deliveryChannel =
          register.getDeliveryChannel();

      final Integer value = getValue(i);

      Mockito.reset(deliveryChannel);
      final long timestampBefore = System.currentTimeMillis();
      register.assign(value);
      final long timestampAfter = System.currentTimeMillis();

      Mockito.verify(deliveryChannel).publish();
      Mockito.verifyNoMoreInteractions(deliveryChannel);

      LWWRegisterState updateMessage = register.snapshot();

      assertEquals("Update message identifier should be the same as the set's",
          register.getIdentifier(), updateMessage.getIdentifier());
      assertTrue("Update version should be as expected",
          updateMessage.getVersion().identical(expectedVersionVector));
      assertEquals("Element value should equal the assigned value", value,
          updateMessage.getElement().getValue());
      assertFalse("Timestamp shouldn't be earlier than before the call was made",
          updateMessage.getElement().getTimestamp() < timestampBefore);
      assertFalse("Timestamp shouldn't be after the call completed",
          updateMessage.getElement().getTimestamp() > timestampAfter);
    }
  }

  /**
   * Test snapshot of the {@link Register}s initial state.
   */
  @Test
  public void testSnapshot_Initial() {
    LOGGER.log(Level.INFO, "testSnapshot_Initial: Test snapshot of the register's initial state.");
    final LWWRegister<Integer, Integer, Integer> register = getRegister();

    final VersionVector<Integer, Integer> expectedVersionVector =
        new HashVersionVector<>(new IntegerVersion());

    LWWRegisterState<Integer, Integer, Integer> state = register.snapshot();

    assertEquals("state identifier should be the same as the register", register.getIdentifier(),
        state.getIdentifier());
    assertTrue("The VersionVector should match the expectation",
        state.getVersion().identical(expectedVersionVector));
    assertNull("Initial value should be null", state.getElement().getValue());
    assertEquals("Initial timestamp should be 0", 0l, state.getElement().getTimestamp());

    // Check that the state snapshot is immutable by changes to the register.
    register.assign(getValue(0));

    assertEquals("state identifier should be the same as the register", register.getIdentifier(),
        state.getIdentifier());
    assertTrue("The VersionVector should still match the expectation",
        state.getVersion().identical(expectedVersionVector));
    assertNull("State value should still be null", state.getElement().getValue());
    assertEquals("State timestamp should still be 0", 0l, state.getElement().getTimestamp());
  }

  /**
   * Test snapshot as new values are assigned.
   */
  @Test
  public void testSnapshot_Assignment() {
    LOGGER.log(Level.INFO, "testSnapshot_Assignment: Test snapshot as new values are assigned.");
    final LWWRegister<Integer, Integer, Integer> register = getRegister();

    final VersionVector<Integer, Integer> expectedVersionVector =
        new HashVersionVector<>(new IntegerVersion());
    expectedVersionVector.init(register.getIdentifier());

    LWWRegisterState<Integer, Integer, Integer> previousState = register.snapshot();

    VersionVector<Integer, Integer> previousVersion = previousState.getVersion();
    Integer previousValue = previousState.getElement().getValue();
    long previousTimestamp = previousState.getElement().getTimestamp();

    Integer value;

    for (int i = 0; i < MAX_OPERATIONS; i++) {
      // Get the next value
      value = getValue(i);
      expectedVersionVector.increment(register.getIdentifier());

      // Assign to the register
      long timestampBefore = System.currentTimeMillis();
      register.assign(value);
      long timestampAfter = System.currentTimeMillis();

      // Take a snapshot
      LWWRegisterState<Integer, Integer, Integer> state = register.snapshot();

      // Make asertions
      assertEquals("state identifier should be the same as the register", register.getIdentifier(),
          state.getIdentifier());
      assertTrue("The VersionVector should match the expectation",
          state.getVersion().identical(expectedVersionVector));
      assertEquals("The value should be the one assigned", value, state.getElement().getValue());
      long timestamp = state.getElement().getTimestamp();
      assertFalse("Timestamp should not be before assignment was made",
          timestamp < timestampBefore);
      assertFalse("Timestamp should not be after assignment was made", timestamp > timestampAfter);

      // Verify that the previousState has remained unchanged.
      assertEquals("state identifier should be the same as the register", register.getIdentifier(),
          previousState.getIdentifier());
      assertTrue("The VersionVector should still match the expectation",
          previousState.getVersion().identical(previousVersion));
      assertEquals("The value should be the one assigned", previousValue,
          previousState.getElement().getValue());
      assertEquals("State timestamp should not have changed", previousTimestamp,
          previousState.getElement().getTimestamp());

      // Move current results into previous reesults
      previousState = state;
      previousVersion = expectedVersionVector.copy();
      previousValue = value;
      previousTimestamp = timestamp;
    }
  }

  /**
   * Test update with no changes.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_NoChange() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_NoChange: Test update with no changes.");

    final LWWRegister<Integer, Integer, Integer> register1 = getRegister();
    final LWWRegister<Integer, Integer, Integer> register2 = getRegister();

    assertTrue("The registers should be identical to start with",
        register1.getVersion().identical(register2.getVersion()));
    assertEquals("The registers should be identical to start with", register1.value(),
        register2.value());

    register1.update(register2.snapshot());
    assertTrue("The registers should be identical after update",
        register1.getVersion().identical(register2.getVersion()));
    assertEquals("The registers should be identical after update", register1.value(),
        register2.value());

    register2.update(register1.snapshot());
    assertTrue("The registers should be identical after bi-directional update",
        register1.getVersion().identical(register2.getVersion()));
    assertEquals("The registers should be identical after update", register1.value(),
        register2.value());
  }

  /**
   * Test update with local assignment.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_LocalAssign() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_LocalAssign: Test update with local assignment.");

    final LWWRegister<Integer, Integer, Integer> register1 = getRegister();
    final LWWRegister<Integer, Integer, Integer> register2 = getRegister();

    final Integer value0 = null;
    final Integer value1 = getValue(1);

    assertTrue("The registers should be identical to start with",
        register1.getVersion().identical(register2.getVersion()));
    assertEquals("The registers should be identical to start with", register1.value(),
        register2.value());
    assertEquals("register1's value should be value0", value0, register1.value());
    assertEquals("register2's value should be value0", value0, register2.value());

    register1.assign(value1);
    assertTrue("register2 should have happenedBefore register1",
        register2.getVersion().happenedBefore(register1.getVersion()));

    register1.update(register2.snapshot());
    assertTrue("register2 should have happenedBefore register1",
        register2.getVersion().happenedBefore(register1.getVersion()));
    assertEquals("register1's value should be value1", value1, register1.value());
    assertEquals("register2's value should be value0", value0, register2.value());

    register2.update(register1.snapshot());
    assertTrue("The registers should be identical after bi-directional update",
        register1.getVersion().identical(register2.getVersion()));
    assertEquals("The registers should be identical after bi-directional update", register1.value(),
        register2.value());
    assertEquals("register1's value should be value1", value1, register1.value());
    assertEquals("register2's value should be value1", value1, register2.value());
  }

  /**
   * Test update with no changes.
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_RemoteAssign() throws Exception {
    LOGGER.log(Level.INFO, "testUpdate_RemoteAssign: Test update with remote assignment.");

    final LWWRegister<Integer, Integer, Integer> register1 = getRegister();
    final LWWRegister<Integer, Integer, Integer> register2 = getRegister();

    final Integer value0 = null;
    final Integer value1 = getValue(1);

    assertTrue("The registers should be identical to start with",
        register1.getVersion().identical(register2.getVersion()));
    assertEquals("The registers should be identical to start with", register1.value(),
        register2.value());
    assertEquals("register1's value should be value0", value0, register1.value());
    assertEquals("register2's value should be value0", value0, register2.value());

    register2.assign(value1);
    assertTrue("register1 should have happenedBefore register2",
        register1.getVersion().happenedBefore(register2.getVersion()));

    register1.update(register2.snapshot());
    assertTrue("The registers should be identical after update",
        register2.getVersion().identical(register1.getVersion()));
    assertEquals("register1's value should be value1", value1, register1.value());
    assertEquals("register2's value should be value1", value1, register2.value());

    register2.update(register1.snapshot());
    assertTrue("The registers should be identical after bi-directional update",
        register1.getVersion().identical(register2.getVersion()));
    assertEquals("The registers should be identical after bi-directional update", register1.value(),
        register2.value());
    assertEquals("register1's value should be value1", value1, register1.value());
    assertEquals("register2's value should be value1", value1, register2.value());
  }

  /**
   * Test update with concurrent assignment (local first).
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_BothAssign_LocalFirst() throws Exception {
    LOGGER.log(Level.INFO,
        "testUpdate_BothAssign_LocalFirst: Test update with concurrent assignment (local first).");

    final LWWRegister<Integer, Integer, Integer> register1 = getRegister();
    final LWWRegister<Integer, Integer, Integer> register2 = getRegister();

    final Integer value0 = null;
    final Integer value1 = getValue(1);
    final Integer value2 = getValue(2);

    assertTrue("The registers should be identical to start with",
        register1.getVersion().identical(register2.getVersion()));
    assertEquals("The registers should be identical to start with", register1.value(),
        register2.value());
    assertEquals("register1's value should be value0", value0, register1.value());
    assertEquals("register2's value should be value0", value0, register2.value());

    register1.assign(value1);
    Thread.sleep(1); // Wait at least 1ms
    register2.assign(value2);
    assertTrue("register1 should be concurrent with register2",
        register1.getVersion().concurrentWith(register2.getVersion()));

    register1.update(register2.snapshot());
    assertTrue("register2 should have happenedBefore register1",
        register2.getVersion().happenedBefore(register1.getVersion()));
    assertEquals("register1's value should be value2", value2, register1.value());
    assertEquals("register2's value should be value2", value2, register2.value());

    register2.update(register1.snapshot());
    assertTrue("The registers should be identical after bi-directional update",
        register1.getVersion().identical(register2.getVersion()));
    assertEquals("The registers should be identical after bi-directional update", register1.value(),
        register2.value());
    assertEquals("register1's value should be value2", value2, register1.value());
    assertEquals("register2's value should be value2", value2, register2.value());
  }

  /**
   * Test update with concurrent assignment (remote first).
   *
   * @throws Exception if the test fails.
   */
  @Test
  public void testUpdate_BothAssign_RemoteFirst() throws Exception {
    LOGGER.log(Level.INFO,
        "testUpdate_BothAssign_LocalFirst: Test update with concurrent assignment (remote first).");

    final LWWRegister<Integer, Integer, Integer> register1 = getRegister();
    final LWWRegister<Integer, Integer, Integer> register2 = getRegister();

    final Integer value0 = null;
    final Integer value1 = getValue(1);
    final Integer value2 = getValue(2);

    assertTrue("The registers should be identical to start with",
        register1.getVersion().identical(register2.getVersion()));
    assertEquals("The registers should be identical to start with", register1.value(),
        register2.value());
    assertEquals("register1's value should be value0", value0, register1.value());
    assertEquals("register2's value should be value0", value0, register2.value());

    register2.assign(value2);
    Thread.sleep(1); // Wait at least 1ms
    register1.assign(value1);
    assertTrue("register1 should be concurrent with register2",
        register1.getVersion().concurrentWith(register2.getVersion()));

    register1.update(register2.snapshot());
    assertTrue("register2 should have happenedBefore register1",
        register2.getVersion().happenedBefore(register1.getVersion()));
    assertEquals("register1's value should be value1", value1, register1.value());
    assertEquals("register2's value should be value2", value2, register2.value());

    register2.update(register1.snapshot());
    assertTrue("The registers should be identical after bi-directional update",
        register1.getVersion().identical(register2.getVersion()));
    assertEquals("The registers should be identical after bi-directional update", register1.value(),
        register2.value());
    assertEquals("register1's value should be value1", value1, register1.value());
    assertEquals("register2's value should be value1", value1, register2.value());
  }

}
