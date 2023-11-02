package h01;

import com.fasterxml.jackson.databind.JsonNode;
import fopbot.Direction;
import fopbot.World;
import h01.template.Utils;
import org.junit.jupiter.params.ParameterizedTest;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static h01.TestConstants.SHOW_WORLD;
import static h01.TestConstants.WORLD_DELAY;

/**
 * Tests for the {@link CleaningRobot} class.
 */
@TestForSubmission
public class CleaningRobotTest {

    /**
     * The custom converters for this test class.
     */
    @SuppressWarnings("unused")
    public static final Map<String, Function<JsonNode, ?>> customConverters = Map.ofEntries(
        Map.entry("worldWidth", JsonNode::asInt),
        Map.entry("worldHeight", JsonNode::asInt),
        Map.entry("cleaningRobot", JsonConverters::toCleaningRobot),
        Map.entry("walls", n -> JsonConverters.toList(n, JsonConverters::toDirection)),
        Map.entry("initialCoinsOnField", JsonNode::asInt),
        Map.entry("contaminant1", JsonConverters::toContaminant1),
        Map.entry("contaminant2", JsonConverters::toContaminant2),
        Map.entry("direction", JsonNode::asInt),
        Map.entry("shouldMove", JsonNode::asBoolean),
        Map.entry("canMove", JsonNode::asBoolean),
        Map.entry("shouldPutCoins", JsonNode::asBoolean),
        Map.entry("shouldPickCoins", JsonNode::asBoolean),
        Map.entry("expectedEndPosition", JsonConverters::toPoint),
        Map.entry("expectedEndDirection", JsonConverters::toDirection),
        Map.entry("expectedRobotCoinDelta", JsonNode::asInt)
    );

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
        World.setSize(worldWidth, worldHeight);
        if (SHOW_WORLD) {
            World.setDelay(WORLD_DELAY);
            World.setVisible(true);
        }
        final CleaningRobot cleaningRobot = params.get("cleaningRobot");
        final Point initialRobotPosition = new Point(cleaningRobot.getX(), cleaningRobot.getY());
        final int direction = params.get("direction");
        final List<Direction> walls = params.get("walls");
        final var initialCoinsOnField = params.getInt("initialCoinsOnField");
        final var shouldPutCoins = params.getBoolean("shouldPutCoins");
        final var shouldPickCoins = params.getBoolean("shouldPickCoins");
        final var shouldMove = params.getBoolean("shouldMove");
        final var canMove = params.getBoolean("canMove");
        final Point expectedEndPosition = params.get("expectedEndPosition");
        final Direction expectedEndDirection = params.get("expectedEndDirection");
        final int expectedRobotCoinDelta = params.get("expectedRobotCoinDelta");

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

        final var context = params.toContext(ignoreParams.toArray(String[]::new));

        final var initialRobotCoinAmount = cleaningRobot.getNumberOfCoins();

        // Set Walls
        for (Direction wall : walls) {
            final var directionVector = TestUtils.toUnitVector(wall);
            if (directionVector.x == 0) {
                World.placeHorizontalWall(cleaningRobot.getX(), cleaningRobot.getY() + Math.min(0, directionVector.y));
            } else {
                World.placeVerticalWall(cleaningRobot.getX() + Math.min(0, directionVector.x), cleaningRobot.getY());
            }
        }

        // Set Coins
        if (initialCoinsOnField > 0) {
            World.getGlobalWorld().putCoins(cleaningRobot.getX(), cleaningRobot.getY(), initialCoinsOnField);
        }

        Assertions2.call(
            () -> cleaningRobot.handleKeyInput(direction, shouldPutCoins, shouldPickCoins),
            context,
            r -> "The Method handleInput threw an exception"
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
