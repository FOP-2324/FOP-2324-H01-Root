package h01;

import fopbot.World;
import h01.template.GameConstants;
import h01.template.MazeGenerator;
import h01.template.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junitpioneer.jupiter.cartesian.CartesianTest;
import org.junitpioneer.jupiter.params.IntRangeSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.annotation.SkipAfterFirstFailedTest;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.assertions.Context;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Tests for the {@link GameController} class.
 */
@TestForSubmission
@Timeout(
    value = TestConstants.TEST_TIMEOUT_IN_SECONDS,
    threadMode = Timeout.ThreadMode.SEPARATE_THREAD
)
@SkipAfterFirstFailedTest(TestConstants.SKIP_AFTER_FIRST_FAILED_TEST)
public class GameControllerTest {

    /**
     * The possible winners of the game.
     */
    private enum Winner {
        /**
         * The cleaning robot won.
         */
        CLEANING_ROBOT,
        /**
         * The contaminant robots won.
         */
        CONTAMINANTS,
        /**
         * No one won (game is still running).
         */
        NONE
    }

    /**
     * Tests the {@link GameController#checkWinCondition()} method.
     *
     * @param worldWidth  The width of the world.
     * @param worldHeight The height of the world.
     * @param setupWorld  A function to setup the world.
     */
    public void testGameController(
        final int worldWidth,
        final int worldHeight,
        final Consumer<GameController> setupWorld
    ) {
        GameConstants.WORLD_HEIGHT = worldHeight;
        GameConstants.WORLD_WIDTH = worldWidth;

        // init gameController, only showing world if needed
        final var gc = new GameController() {
            @Override
            public void setupWorld() {
                World.setSize(GameConstants.WORLD_WIDTH, GameConstants.WORLD_HEIGHT);
                World.setDelay(0);
                if (TestConstants.SHOW_WORLD) {
                    World.setVisible(true);
                    MazeGenerator.generateMaze();
                    World.getGlobalWorld().setFieldColor(0, World.getHeight() - 1, Color.YELLOW);
                }
            }

            @Override
            protected void setup() {
                setupWorld();
                setupRobots();
            }
        };

        setupWorld.accept(gc);

        final boolean initialCleanerTurnedOff = gc.getCleaningRobot().isTurnedOff();
        final boolean initialContaminant1TurnedOff = gc.getContaminant1().isTurnedOff();
        final boolean initialContaminant2TurnedOff = gc.getContaminant2().isTurnedOff();

        var context = makeContext(gc);

        final var expectedWinner = getExpectedWinner(gc);

        // change system.out and system.err to a collector
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final PrintStream systemOutCollector = new PrintStream(os);

        final PrintStream originalSystemOut = System.out;
        final PrintStream originalSystemErr = System.err;

        System.setOut(systemOutCollector);
        System.setErr(systemOutCollector);

        // call
        Assertions2.call(
            gc::checkWinCondition,
            context,
            r -> "The Method handleInput threw an exception"
        );

        // reset system.out and system.err
        System.setOut(originalSystemOut);
        System.setErr(originalSystemErr);

        //close collector and get output
        systemOutCollector.close();
        final String output = os.toString(StandardCharsets.UTF_8);

        final Map<Winner, Pattern> winnerMessages = Map.ofEntries(
            Map.entry(Winner.CLEANING_ROBOT, Pattern.compile(
                "Cleaning\\s*robot\\s*won!?",
                Pattern.CASE_INSENSITIVE
            )),
            Map.entry(Winner.CONTAMINANTS, Pattern.compile(
                "Contaminants\\s*won!?",
                Pattern.CASE_INSENSITIVE
            ))
        );

        // get winner from output
        final List<Winner> actualWinners = winnerMessages.entrySet().stream()
            .filter(e -> {
                // check if the output contains the winner message
                final var pattern = e.getValue();
                return pattern.matcher(output).find();
            })
            .map(Map.Entry::getKey)
            .toList();

        context = Assertions2.contextBuilder()
            .add(context)
            .add("actualWinners", actualWinners)
            .add("console output", output)
            .build();

        if (actualWinners.size() > 1) {
            Assertions2.fail(
                context,
                r -> "The output contains multiple winner messages"
            );
        }

        final Winner actualWinner = actualWinners.isEmpty()
            ? Winner.NONE
            : actualWinners.get(0);


        // check winner
        Assertions2.assertEquals(
            expectedWinner,
            actualWinner,
            context,
            r -> "The winner is not correct"
        );

        // check other party turned off
        if (actualWinner == Winner.CONTAMINANTS) {
            Assertions2.assertTrue(
                gc.getCleaningRobot().isTurnedOff(),
                context,
                r -> "The cleaning robot is not turned off even though the contaminants won."
            );
        }

        if (actualWinner == Winner.CLEANING_ROBOT) {
            Assertions2.assertTrue(
                gc.getContaminant1().isTurnedOff(),
                context,
                r -> "The contaminant 1 is not turned off even though the cleaning robot won."
            );
            Assertions2.assertTrue(
                gc.getContaminant2().isTurnedOff(),
                context,
                r -> "The contaminant 2 is not turned off even though the cleaning robot won."
            );
        }

        if (actualWinner == Winner.NONE) {
            final Context finalContext = context;
            Map.ofEntries(
                Map.entry(
                    gc.getCleaningRobot(),
                    initialCleanerTurnedOff
                ),
                Map.entry(
                    gc.getContaminant1(),
                    initialContaminant1TurnedOff
                ),
                Map.entry(
                    gc.getContaminant2(),
                    initialContaminant2TurnedOff
                )
            ).forEach(
                (robot, initialTurnedOff) -> Assertions2.assertEquals(
                    initialTurnedOff,
                    robot.isTurnedOff(),
                    finalContext,
                    r -> String.format(
                        "The cleaning robot was turned %s before the call and is turned %s after the call even though"
                            + " the game is still running.",
                        initialCleanerTurnedOff ? "off" : "on",
                        gc.getCleaningRobot().isTurnedOff() ? "off" : "on"
                    )
                )
            );
        }
    }

    /**
     * Returns the ratio of fields covered with coins. 0 means no fields are covered, 1.0 means all fields are covered.
     *
     * @return The ratio of fields covered with coins.
     */
    private static double getRatioOfCoveredFields() {
        int dirtyFields = 0;
        for (int y = 0; y < GameConstants.WORLD_HEIGHT; y++) {
            for (int x = 0; x < GameConstants.WORLD_WIDTH; x++) {
                if (Utils.getCoinAmount(x, y) > 0) {
                    dirtyFields++;
                }
            }
        }
        return (double) dirtyFields / (GameConstants.WORLD_HEIGHT * GameConstants.WORLD_WIDTH);
    }

    /**
     * Places coins on a given amount of fields.
     *
     * @param amountOfDirtyFields The amount of coins to place on the world. Must be between 0 and Width*Height.
     */
    private static void setAmountOfDirtyFields(final double amountOfDirtyFields) {
        if (amountOfDirtyFields < 0 || amountOfDirtyFields > GameConstants.WORLD_HEIGHT * GameConstants.WORLD_WIDTH) {
            throw new IllegalArgumentException("amountOfDirtyFields must be between 0 and Width*Height");
        }
        // assume no dirty fields
        int dirtyFields = 0;
        for (int y = 0; y < GameConstants.WORLD_HEIGHT; y++) {
            for (int x = 0; x < GameConstants.WORLD_WIDTH; x++) {
                if (dirtyFields >= amountOfDirtyFields) {
                    return;
                }
                World.putCoins(x, y, 1);
                dirtyFields++;
            }
        }
    }

    /**
     * Gets the number of coins in the dumping area.
     *
     * @return The number of coins in the dumping area.
     */
    private static int getNumberOfCoinsInDumpingArea() {
        return Utils.getCoinAmount(0, World.getHeight() - 1);
    }

    /**
     * Gets the expected winner of the game.
     *
     * @param gc The game controller.
     * @return The expected winner of the game.
     */
    private static Winner getExpectedWinner(final GameController gc) {
        final var contaminant1 = gc.getContaminant1();
        final var contaminant2 = gc.getContaminant2();

        // If all Offenders are turned off, or if the dumping area contains at least 200 coins, the game is won
        if (contaminant1.isTurnedOff() && contaminant2.isTurnedOff() || getNumberOfCoinsInDumpingArea() >= 200) {
            return Winner.CLEANING_ROBOT;
        }
        // if more than 50% of all fields are dirty, the game is lost
        if (getRatioOfCoveredFields() >= 0.5) {
            return Winner.CONTAMINANTS;
        }

        // No one won (game is still running).
        return Winner.NONE;
    }

    /**
     * Creates a new {@link Context} for the given {@link GameController}.
     *
     * @param gc The game controller.
     * @return A new {@link Context} for the given {@link GameController}.
     */
    final Context makeContext(final GameController gc) {
        return Assertions2.contextBuilder()
            .add("worldWidth", GameConstants.WORLD_WIDTH)
            .add("worldHeight", GameConstants.WORLD_HEIGHT)
            .add("cleaningRobot", gc.getCleaningRobot())
            .add("contaminant1", gc.getContaminant1())
            .add("contaminant2", gc.getContaminant2())
            .add("ratioOfCoveredFields", getRatioOfCoveredFields())
            .add("numberOfCoinsInDumpingArea", getNumberOfCoinsInDumpingArea())
            .add("expectedWinner", getExpectedWinner(gc))
            .build();
    }

    /**
     * Tests the {@link GameController#checkWinCondition()} method.
     *
     * @param contaminant1TurnedOff Whether the contaminant 1 is turned off.
     * @param contaminant2TurnedOff Whether the contaminant 2 is turned off.
     */
    @CartesianTest(name = "[{index}] contaminant1TurnedOff={0}, contaminant2TurnedOff={1}")
    public void testCleaningRobotWinByEndurance(
        @CartesianTest.Values(booleans = {true, false}) final boolean contaminant1TurnedOff,
        @CartesianTest.Values(booleans = {true, false}) final boolean contaminant2TurnedOff
    ) {
        testGameController(3, 3, gc -> {
                if (contaminant1TurnedOff) {
                    gc.getContaminant1().setNumberOfCoins(0);
                    gc.getContaminant1().turnOff();
                }
                if (contaminant2TurnedOff) {
                    gc.getContaminant2().setNumberOfCoins(0);
                    gc.getContaminant2().turnOff();
                }
            }
        );
    }

    /**
     * Tests that the cleaning robot wins if the dumping area contains at least 200 coins.
     *
     * @param coinsInDumpingArea The number of coins in the dumping area.
     */
    @CartesianTest(name = "[{index}] coinsInDumpingArea={0}")
    public void testCleaningRobotWinByDumpingArea(
        @IntRangeSource(from = 0, to = 300, step = 50, closed = true) final int coinsInDumpingArea
    ) {
        testGameController(3, 3, gc -> {
            if (coinsInDumpingArea > 0) {
                World.getGlobalWorld().putCoins(0, World.getHeight() - 1, coinsInDumpingArea);
            }
        });
    }

    /**
     * Tests that the contaminants win if at least 50% of all fields are dirty.
     *
     * @param worldWidth  The width of the world.
     * @param worldHeight The height of the world.
     */
    @CartesianTest(name = "[{index}] worldWidth={0}, worldHeight={1}")
    public void testContaminantsWin(
        @CartesianTest.Values(ints = {1, 3, 5}) final int worldWidth,
        @CartesianTest.Values(ints = {1, 3, 10}) final int worldHeight
    ) {
        final int amountOfFields = worldWidth * worldHeight;
        for (int i = 0; i <= amountOfFields; i++) {
            final int finalI = i;
            testGameController(worldWidth, worldHeight, gc -> setAmountOfDirtyFields(finalI));
        }
    }

    @Test
    public void testBothPartiesWin() {
        testGameController(1, 1, gc -> World.putCoins(0, 0, 200));
    }
}
