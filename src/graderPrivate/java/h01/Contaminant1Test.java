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
import java.util.*;

import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mockStatic;

/**
 * Tests for the {@link Contaminant1} class.
 */
@TestForSubmission
public class Contaminant1Test extends ContaminantRobotTest {

    @Override
    public void testMovement(
        final JsonParameterSet params,
        final boolean verifyTurnaround,
        final boolean verifyMovement,
        final boolean verifyCoinAmount,
        final boolean verifyPowerStatus
    ) {
        final int worldWidth = params.getInt("worldWidth");
        final int worldHeight = params.getInt("worldHeight");
        setupWorld(worldWidth, worldHeight);
        final Contaminant1 contaminant1 = params.get("contaminant1");
        final Point initialRobotPosition = new Point(contaminant1.getX(), contaminant1.getY());
        final Direction initialRobotDirection = contaminant1.getDirection();
        final Set<Direction> walls = new HashSet<>(params.<List<Direction>>get("walls"));
        final var initialCoinsOnField = params.getInt("initialCoinsOnField");
        final var shouldTurnOff = params.getBoolean("shouldTurnOff");
        final var canMove = params.getBoolean("canMove");
        final int shouldPlaceCoins = params.get("amountOfCoinsToPlace");
        GameConstants.CONTAMINANT_ONE_MIN_PUT_COINS = TestUtils.getPropertyOrDefault(
            params,
            "CONTAMINANT_ONE_MIN_PUT_COINS",
            1
        );
        GameConstants.CONTAMINANT_ONE_MAX_PUT_COINS = TestUtils.getPropertyOrDefault(
            params,
            "CONTAMINANT_ONE_MAX_PUT_COINS",
            5
        );

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

        placeWalls(walls, contaminant1.getX(), contaminant1.getY());

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
                return Utils.rnd.nextInt(max - min + 1) + min;
            });
            Assertions2.call(
                contaminant1::doMove,
                context,
                r -> "The Method handleInput threw an exception"
            );
            if (verifyCoinAmount && !shouldTurnOff && initialRobotCoinAmount != 0) {
                utilsMock.verify(() -> Utils.getRandomInteger(
                    GameConstants.CONTAMINANT_ONE_MIN_PUT_COINS,
                    GameConstants.CONTAMINANT_ONE_MAX_PUT_COINS
                ), atLeastOnce().description(context.toString()));
            }
        }

        if (verifyPowerStatus) {
            verifyPowerStatus(shouldTurnOff, contaminant1, context);
        }

        if (verifyTurnaround) {
            verifyTurnaround(contaminant1, context);
        }

        if (verifyMovement) {
            if (shouldTurnOff || !canMove) {
                Assertions2.assertEquals(
                    initialRobotPosition,
                    new Point(contaminant1.getX(), contaminant1.getY()),
                    context,
                    r -> "invalid end position. The robot should not move."
                );
                Assertions2.assertEquals(
                    initialRobotDirection,
                    contaminant1.getDirection(),
                    context,
                    r -> "invalid end direction. The robot should not move."
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
            verifyCoinAmount(
                initialCoinsOnField,
                initialRobotCoinAmount,
                shouldPlaceCoins,
                contaminant1,
                context,
                initialRobotPosition
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
    public void testTheMovement(final JsonParameterSet params) {
        testMovement(params, false, true, false, false);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "Contaminant1TestMovement.json", customConverters = "customConverters")
    public void testRotation(final JsonParameterSet params) {
        testMovement(params, true, false, false, false);
    }
}
