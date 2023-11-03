package h01;

import com.fasterxml.jackson.databind.JsonNode;
import fopbot.Direction;
import fopbot.Transition;
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
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import static h01.TestConstants.SHOW_WORLD;
import static h01.TestConstants.WORLD_DELAY;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mockStatic;

/**
 * Tests for the {@link Contaminant1} class.
 */
@TestForSubmission
public class Contaminant1Test {

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
     * Tests the {@link Contaminant1#doMove()} method.
     *
     * @param params            The {@link JsonParameterSet} to use for the test.
     * @param verifyTurnaround  Whether to verify that the robot turns around to scan for blocked paths.
     * @param verifyMovement    Whether to verify the movement.
     * @param verifyCoinAmount  Whether to verify the coin amount.
     * @param verifyPowerStatus Whether to verify whether the robot is turned on or off.
     */
    public void testMovement(
        final JsonParameterSet params,
        final boolean verifyTurnaround,
        final boolean verifyMovement,
        final boolean verifyCoinAmount,
        final boolean verifyPowerStatus
    ) {
        final int worldWidth = params.getInt("worldWidth");
        final int worldHeight = params.getInt("worldHeight");
        GameConstants.WORLD_WIDTH = worldWidth;
        GameConstants.WORLD_HEIGHT = worldHeight;
        World.setSize(worldWidth, worldHeight);
        if (SHOW_WORLD) {
            World.setDelay(WORLD_DELAY);
            World.setVisible(true);
        }
        final Contaminant1 contaminant1 = params.get("contaminant1");
        final Point initialRobotPosition = new Point(contaminant1.getX(), contaminant1.getY());
        final List<Direction> walls = params.get("walls");
        final var initialCoinsOnField = params.getInt("initialCoinsOnField");
        final var shouldTurnOff = params.getBoolean("shouldTurnOff");
        final var canMove = params.getBoolean("canMove");
        final int shouldPlaceCoins = params.get("amountOfCoinsToPlace");
        GameConstants.CONTAMINANT_ONE_MIN_PUT_COINS = params.availableKeys().contains("CONTAMINANT_ONE_MIN_PUT_COINS")
            ? params.getInt("CONTAMINANT_ONE_MIN_PUT_COINS")
            : 1;
        GameConstants.CONTAMINANT_ONE_MAX_PUT_COINS = params.availableKeys().contains("CONTAMINANT_ONE_MAX_PUT_COINS")
            ? params.getInt("CONTAMINANT_ONE_MAX_PUT_COINS")
            : 1;

        final List<String> ignoreParams = new ArrayList<>();

        if (!verifyPowerStatus) {
            ignoreParams.add("shouldTurnOff");
        }
        if (!verifyCoinAmount) {
            ignoreParams.add("amountOfCoinsToPlace");
        }

        ignoreParams.add("contaminant1");
        ignoreParams.add("CONTAMINANT_ONE_MIN_PUT_COINS");
        ignoreParams.add("CONTAMINANT_ONE_MAX_PUT_COINS");

        final var paramsContext = params.toContext(ignoreParams.toArray(String[]::new));
        final var cb = Assertions2
            .contextBuilder()
            .add(paramsContext)
            .add("contaminant1 (before call)", contaminant1.toString())
            .add("contaminant1 (after call)", contaminant1);
        if (verifyCoinAmount) {
            cb.add("CONTAMINANT_ONE_MIN_PUT_COINS", GameConstants.CONTAMINANT_ONE_MIN_PUT_COINS);
            cb.add("CONTAMINANT_ONE_MAX_PUT_COINS", GameConstants.CONTAMINANT_ONE_MAX_PUT_COINS);
        }
        final var context = cb.build();

        final var initialRobotCoinAmount = contaminant1.getNumberOfCoins();

        // Set Walls
        for (final Direction wall : walls) {
            final var directionVector = TestUtils.toUnitVector(wall);
            if (directionVector.x == 0) {
                World.placeHorizontalWall(contaminant1.getX(), contaminant1.getY() + Math.min(0, directionVector.y));
            } else {
                World.placeVerticalWall(contaminant1.getX() + Math.min(0, directionVector.x), contaminant1.getY());
            }
        }

        // Set Coins
        if (initialCoinsOnField > 0) {
            World.getGlobalWorld().putCoins(contaminant1.getX(), contaminant1.getY(), initialCoinsOnField);
        }

        try (final var utilsMock = mockStatic(Utils.class, CALLS_REAL_METHODS)) {
            utilsMock.when(() -> Utils.getRandomInteger(anyInt(), anyInt())).thenAnswer(invocation -> {
                final int min = invocation.getArgument(0);
                final int max = invocation.getArgument(1);
                if (min == GameConstants.CONTAMINANT_ONE_MIN_PUT_COINS && max == GameConstants.CONTAMINANT_ONE_MAX_PUT_COINS) {
                    return shouldPlaceCoins;
                }
                return new Random().nextInt(max - min + 1) + min;
            });
            Assertions2.call(
                contaminant1::doMove,
                context,
                r -> "The Method handleInput threw an exception"
            );
            if (verifyCoinAmount) {
                utilsMock.verify(() -> Utils.getRandomInteger(
                    GameConstants.CONTAMINANT_ONE_MIN_PUT_COINS,
                    GameConstants.CONTAMINANT_ONE_MAX_PUT_COINS
                ), atLeastOnce().description(context.toString()));
            }
        }

        if (verifyPowerStatus) {
            Assertions2.assertEquals(
                shouldTurnOff,
                contaminant1.isTurnedOff(),
                context,
                r -> String.format("The robot should be turned %s.", shouldTurnOff ? "off" : "on")
            );
        }

        if (verifyTurnaround) {
            final var firstFourMovements = World.getGlobalWorld()
                .getTrace(contaminant1)
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

        if (verifyMovement) {
            if (shouldTurnOff || !canMove) {
                Assertions2.assertEquals(
                    initialRobotPosition,
                    new Point(contaminant1.getX(), contaminant1.getY()),
                    context,
                    r -> "invalid end position. The robot should not move."
                );
            } else {
                final double movementDistance = Math.sqrt(
                    Math.pow(contaminant1.getX() - initialRobotPosition.x, 2)
                        + Math.pow(contaminant1.getY() - initialRobotPosition.y, 2)
                );
                Assertions2.assertTrue(
                    movementDistance == 1,
                    context,
                    r -> "invalid end position. The robot should move exactly one field in a valid direction."
                );
            }
        }
        if (verifyCoinAmount) {
            final int maxCoinAmount = 20;
            final int expectedCoinAmount = Math.min(
                Math.min(
                    initialCoinsOnField + initialRobotCoinAmount,
                    initialCoinsOnField + shouldPlaceCoins
                ),
                maxCoinAmount
            );
            final int expectedPlaceCoinAmount = expectedCoinAmount - initialCoinsOnField;
            // Check Robot Coin Amount
            Assertions2.assertEquals(
                initialRobotCoinAmount - expectedPlaceCoinAmount,
                contaminant1.getNumberOfCoins(),
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
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "Contaminant1TestTurnOff.json", customConverters = "customConverters")
    public void testTurnOff(final JsonParameterSet params) {
        testMovement(params, false, true, false, true);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "Contaminant1TestCoins.json", customConverters = "customConverters")
    public void testCoins(final JsonParameterSet params) {
        testMovement(params, false, false, true, false);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "Contaminant1TestMovement.json", customConverters = "customConverters")
    public void testMovement(final JsonParameterSet params) {
        testMovement(params, false, true, false, false);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "Contaminant1TestMovement.json", customConverters = "customConverters")
    public void testRotation(final JsonParameterSet params) {
        testMovement(params, true, false, false, false);
    }
}
