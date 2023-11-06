package h01;

import fopbot.Direction;
import fopbot.World;
import h01.template.GameConstants;
import h01.template.Utils;
import org.junit.jupiter.params.ParameterizedTest;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tests for the {@link CleaningRobot} class.
 */
@TestForSubmission
public class CleaningRobotTest extends RobotTest {

    /**
     * Tests the {@link CleaningRobot#handleKeyInput(int, boolean, boolean)} method.
     *
     * @param params           The {@link JsonParameterSet} to use for the test.
     * @param verifyMovement   Whether to verify the movement.
     * @param verifyRotation   Whether to verify the rotation.
     * @param verifyCoinAmount Whether to verify the coin amount.
     */
    public void testMovement(
        final JsonParameterSet params,
        final boolean verifyMovement,
        final boolean verifyRotation,
        final boolean verifyCoinAmount
    ) {
        final int worldWidth = params.getInt("worldWidth");
        final int worldHeight = params.getInt("worldHeight");
        TestUtils.setupWorld(worldWidth, worldHeight);
        final CleaningRobot cleaningRobot = params.get("cleaningRobot");
        final Point initialRobotPosition = new Point(cleaningRobot.getX(), cleaningRobot.getY());
        final int direction = params.get("direction");
        final Set<Direction> walls = new HashSet<>(params.<List<Direction>>get("walls"));
        final var initialCoinsOnField = params.getInt("initialCoinsOnField");
        final var shouldPutCoins = params.getBoolean("shouldPutCoins");
        final var shouldPickCoins = params.getBoolean("shouldPickCoins");
        final var shouldMove = params.getBoolean("shouldMove");
        final var canMove = params.getBoolean("canMove");
        final Point expectedEndPosition = params.get("expectedEndPosition");
        final Direction expectedEndDirection = params.get("expectedEndDirection");
        final int expectedRobotCoinDelta = params.get("expectedRobotCoinDelta");
        GameConstants.CLEANER_CAPACITY = TestUtils.getPropertyOrDefault(params, "CLEANER_CAPACITY", 25);
        final List<String> ignoreParams = new ArrayList<>();
        if (!verifyMovement) {
            ignoreParams.add("expectedEndPosition");
        }
        if (!verifyRotation) {
            ignoreParams.add("expectedEndDirection");
        }
        if (!verifyCoinAmount) {
            ignoreParams.add("expectedRobotCoinDelta");
        }
        ignoreParams.add("CLEANER_CAPACITY");
        ignoreParams.add("cleaningRobot");

        final var ParamsContext = params.toContext(ignoreParams.toArray(String[]::new));
        final var cb = Assertions2
            .contextBuilder()
            .add(ParamsContext)
            .add("cleaningRobot (before call)", cleaningRobot.toString())
            .add("cleaningRobot (after call)", cleaningRobot);
        if (verifyCoinAmount) {
            cb.add("CLEANER_CAPACITY", GameConstants.CLEANER_CAPACITY);
        }
        final var context = cb.build();

        final var initialRobotCoinAmount = cleaningRobot.getNumberOfCoins();

        placeWalls(walls, cleaningRobot.getX(), cleaningRobot.getY());

        // Set Coins
        if (initialCoinsOnField > 0) {
            World.getGlobalWorld().putCoins(cleaningRobot.getX(), cleaningRobot.getY(), initialCoinsOnField);
        }

        TestUtils.withMockedUtilsClass(
            () -> cleaningRobot.handleKeyInput(direction, shouldPutCoins, shouldPickCoins),
            context,
            128
        );

        if (verifyMovement) {
            Assertions2.assertEquals(
                expectedEndPosition,
                new Point(cleaningRobot.getX(), cleaningRobot.getY()),
                context,
                r -> "invalid end position."
            );
        }
        if (verifyRotation) {
            Assertions2.assertEquals(
                expectedEndDirection,
                cleaningRobot.getDirection(),
                context,
                r -> String.format("The robot should face %s.", Direction.values()[direction])
            );
        }
        if (verifyCoinAmount) {
            // Check Robot Coin Amount
            Assertions2.assertEquals(
                initialRobotCoinAmount + expectedRobotCoinDelta,
                cleaningRobot.getNumberOfCoins(),
                context,
                r -> String.format("The robot should have %d coins.", initialRobotCoinAmount + expectedRobotCoinDelta)
            );
            // Check Field Coin Amount
            Assertions2.assertEquals(
                initialCoinsOnField - expectedRobotCoinDelta,
                Utils.getCoinAmount(initialRobotPosition.x, initialRobotPosition.y),
                context,
                r -> String.format(
                    "The field at (%d, %d) should have %d coins.",
                    initialRobotPosition.x,
                    initialRobotPosition.y,
                    initialCoinsOnField - expectedRobotCoinDelta
                )
            );
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "CleaningRobotTestMovementInvalidDirection.json", customConverters = "customConverters")
    public void testMovementInvalidDirection(final JsonParameterSet params) {
        testMovement(params, true, true, false);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "CleaningRobotTestMovementValidDirection.json", customConverters = "customConverters")
    public void testMovementValidDirectionHasMoved(final JsonParameterSet params) {
        testMovement(params, true, false, false);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "CleaningRobotTestMovementValidDirection.json", customConverters = "customConverters")
    public void testMovementValidDirectionRotation(final JsonParameterSet params) {
        testMovement(params, false, true, false);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "CleaningRobotTestMovementCoins.json", customConverters = "customConverters")
    public void testMovementCoins(final JsonParameterSet params) {
        testMovement(params, false, false, true);
    }
}
