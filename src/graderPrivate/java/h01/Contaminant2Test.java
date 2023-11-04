package h01;

import fopbot.Direction;
import fopbot.World;
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
 * Tests for the {@link Contaminant2} class.
 */
@TestForSubmission
public class Contaminant2Test extends ContaminantRobotTest {

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
        TestUtils.setupWorld(worldWidth, worldHeight);
        final Contaminant2 contaminant2 = params.get("contaminant2");
        final Point initialRobotPosition = new Point(contaminant2.getX(), contaminant2.getY());
        final Direction initialRobotDirection = contaminant2.getDirection();
        final Set<Direction> walls = new HashSet<>(params.<List<Direction>>get("walls"));
        final var initialCoinsOnField = params.getInt("initialCoinsOnField");
        final var shouldTurnOff = params.getBoolean("shouldTurnOff");
        final var canMove = params.getBoolean("canMove");
        final Point expectedEndPosition = params.get("expectedEndPosition");
        final Direction expectedEndDirection = params.get("expectedEndDirection");
        final int shouldPlaceCoins = params.get("amountOfCoinsToPlace");

        final List<String> ignoreParams = new ArrayList<>();

        if (!verifyPowerStatus) {
            ignoreParams.add("shouldTurnOff");
        }
        if (!verifyCoinAmount) {
            ignoreParams.add("amountOfCoinsToPlace");
        }

        ignoreParams.add("contaminant2");

        final var paramsContext = params.toContext(ignoreParams.toArray(String[]::new));
        final var cb = Assertions2
            .contextBuilder()
            .add(paramsContext)
            .add("contaminant2 (before call)", contaminant2.toString())
            .add("contaminant2 (after call)", contaminant2);
        final var context = cb.build();

        final var initialRobotCoinAmount = contaminant2.getNumberOfCoins();

        placeWalls(walls, contaminant2.getX(), contaminant2.getY());

        // Set Coins
        if (initialCoinsOnField > 0) {
            World.getGlobalWorld().putCoins(contaminant2.getX(), contaminant2.getY(), initialCoinsOnField);
        }

        Assertions2.call(
            contaminant2::doMove,
            context,
            r -> "The Method handleInput threw an exception"
        );

        if (verifyPowerStatus) {
            verifyPowerStatus(shouldTurnOff, contaminant2, context);
        }

        if (verifyTurnaround) {
            verifyTurnaround(contaminant2, context);
        }

        if (verifyMovement) {
            if (shouldTurnOff || !canMove) {
                Assertions2.assertEquals(
                    initialRobotPosition,
                    new Point(contaminant2.getX(), contaminant2.getY()),
                    context,
                    r -> "invalid end position. The robot should not move."
                );
                Assertions2.assertEquals(
                    initialRobotDirection,
                    contaminant2.getDirection(),
                    context,
                    r -> "invalid end direction. The robot should not move."
                );
            } else {
                Assertions2.assertEquals(
                    expectedEndPosition,
                    new Point(contaminant2.getX(), contaminant2.getY()),
                    context,
                    r -> "invalid end position."
                );
                Assertions2.assertEquals(
                    expectedEndDirection,
                    contaminant2.getDirection(),
                    context,
                    r -> "invalid end direction."
                );
            }
        }
        if (verifyCoinAmount) {
            verifyCoinAmount(
                initialCoinsOnField,
                initialRobotCoinAmount,
                shouldPlaceCoins,
                contaminant2,
                context,
                initialRobotPosition
            );
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "Contaminant2TestTurnOff.json", customConverters = "customConverters")
    public void testTurnOff(final JsonParameterSet params) {
        testMovement(params, false, true, false, true);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "Contaminant2TestCoins.json", customConverters = "customConverters")
    public void testCoins(final JsonParameterSet params) {
        testMovement(params, false, false, true, false);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "Contaminant2TestMovement.json", customConverters = "customConverters")
    public void testTheMovement(final JsonParameterSet params) {
        testMovement(params, false, true, false, false);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "Contaminant2TestMovement.json", customConverters = "customConverters")
    public void testRotation(final JsonParameterSet params) {
        testMovement(params, true, false, false, false);
    }
}
