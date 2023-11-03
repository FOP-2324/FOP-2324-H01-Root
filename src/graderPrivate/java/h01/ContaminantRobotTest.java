package h01;

import fopbot.Robot;
import fopbot.Transition;
import fopbot.World;
import h01.template.Utils;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;

import java.awt.Point;
import java.util.List;

/**
 * Base class for tests for the {@linkplain h01.template.Contaminant contaminants}.
 */
public abstract class ContaminantRobotTest extends RobotTest {

    /**
     * Tests the {@link Contaminant1#doMove()} method.
     *
     * @param params            The {@link JsonParameterSet} to use for the test.
     * @param verifyTurnaround  Whether to verify that the robot turns around to scan for blocked paths.
     * @param verifyMovement    Whether to verify the movement.
     * @param verifyCoinAmount  Whether to verify the coin amount.
     * @param verifyPowerStatus Whether to verify whether the robot is turned on or off.
     */
    public abstract void testMovement(
        final JsonParameterSet params,
        final boolean verifyTurnaround,
        final boolean verifyMovement,
        final boolean verifyCoinAmount,
        final boolean verifyPowerStatus
    );

    /**
     * Sets up the world for the tests.
     *
     * @param initialCoinsOnField    The initial amount of coins on the field.
     * @param initialRobotCoinAmount The initial amount of coins the robot has.
     * @param shouldPlaceCoins       The amount of coins the robot should place.
     * @param robot                  The {@link Robot} to test.
     * @param context                The {@link Context} to use for assertions.
     * @param initialRobotPosition   The initial position of the robot.
     */
    static void verifyCoinAmount(
        final int initialCoinsOnField,
        final int initialRobotCoinAmount,
        final int shouldPlaceCoins,
        final Robot robot,
        final Context context,
        final Point initialRobotPosition
    ) {
        final int maxCoinAmount = robot instanceof Contaminant1 ? 20 : 2;
        final int expectedCoinAmount = Math.min(
            Math.min(
                initialCoinsOnField + initialRobotCoinAmount,
                initialCoinsOnField + shouldPlaceCoins
            ),
            Math.max(maxCoinAmount, initialCoinsOnField)
        );
        final int expectedPlaceCoinAmount = expectedCoinAmount - initialCoinsOnField;
        // Check Robot Coin Amount
        Assertions2.assertEquals(
            initialRobotCoinAmount - expectedPlaceCoinAmount,
            robot.getNumberOfCoins(),
            context,
            r -> String.format("The robot should have %d coins.", initialRobotCoinAmount - expectedPlaceCoinAmount)
        );
        // Check Field Coin Amount
        Assertions2.assertEquals(
            expectedCoinAmount,
            Utils.getCoinAmount(initialRobotPosition.x, initialRobotPosition.y),
            context,
            r -> String.format(
                "The field at (%d, %d) should have %d coins.",
                initialRobotPosition.x,
                initialRobotPosition.y,
                expectedCoinAmount
            )
        );
    }

    /**
     * Verifies the power status of the robot.
     *
     * @param shouldTurnOff Whether the robot should be turned off.
     * @param contaminant   The robot to test.
     * @param context       The context to use for assertions.
     */
    static void verifyPowerStatus(final boolean shouldTurnOff, final Robot contaminant, final Context context) {
        Assertions2.assertEquals(
            shouldTurnOff,
            contaminant.isTurnedOff(),
            context,
            r -> String.format("The robot should be turned %s.", shouldTurnOff ? "off" : "on")
        );
    }

    /**
     * Verifies that the robot turns around to scan for blocked paths.
     *
     * @param contaminant The robot to test.
     * @param context     The context to use for assertions.
     */
    static void verifyTurnaround(final Robot contaminant, final Context context) {
        final var firstFourMovements = World.getGlobalWorld()
            .getTrace(contaminant)
            .getTransitions()
            .stream()
            .filter(t -> List.of(
                    Transition.RobotAction.MOVE,
                    Transition.RobotAction.TURN_LEFT,
                    Transition.RobotAction.SET_X,
                    Transition.RobotAction.SET_Y
                ).contains(t.action)
            )
            .limit(4)
            .toList();
        Assertions2.assertTrue(
            firstFourMovements.stream().allMatch(t -> t.action == Transition.RobotAction.TURN_LEFT),
            context,
            r -> String.format(
                "The first four movements should be turning left. Actual first four movements are: %s",
                firstFourMovements.stream().map(t -> t.action).toList()
            )
        );
    }
}
