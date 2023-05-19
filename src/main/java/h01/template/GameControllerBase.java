package h01.template;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.World;
import h01.CleaningRobot;
import h01.Contaminant1;
import h01.Contaminant2;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A {@link GameControllerBase} controls the game loop and the {@link Robot}s and checks the win condition.
 */
public abstract class GameControllerBase {
    /**
     * The {@link Timer} that controls the game loop.
     */
    private final Timer gameLoopTimer = new Timer();
    /**
     * The {@link GameInputHandler} that handles the input of the user.
     */
    private final GameInputHandler inputHandler = new GameInputHandler();
    /**
     * The {@link Robot}s that are controlled by the {@link GameControllerBase}.
     */
    protected final Set<Robot> robots = new HashSet<>();
    /**
     * A {@link Map} that maps a {@link Robot} to the amount of ticks that have passed since the last tick action.
     */
    private final Map<Robot, Integer> robotTicks = new HashMap<>();
    /**
     * The {@link TimerTask} that is executed every tick.
     */
    private final TimerTask gameLoopTask = new TimerTask() {
        @Override
        public void run() {
            for (final Robot robot : GameControllerBase.this.robots) {
                if (!(robot instanceof final TickBased tb)) {
                    continue;
                }
                if (!GameControllerBase.this.robotTicks.containsKey(robot)) {
                    GameControllerBase.this.robotTicks.put(robot, 0);
                }
                if (GameControllerBase.this.robotTicks.get(robot) < tb.getUpdateDelay()) {
                    GameControllerBase.this.robotTicks.put(robot, GameControllerBase.this.robotTicks.get(robot) + 1);
                    continue;
                }
                GameControllerBase.this.robotTicks.put(robot, 0);
                // do tick action
                if (robot instanceof final Cleaner r) {
                    r.handleInput(
                        GameControllerBase.this.inputHandler.getDirection(),
                        GameControllerBase.this.inputHandler.getShouldPickCoins(),
                        GameControllerBase.this.inputHandler.getShouldPutCoins()
                    );
                } else if (robot instanceof final Contaminant r) {
                    r.doMove();
                }
            }
            // check win condition
            checkWinCondition();
        }
    };

    /**
     * Starts the game loop.
     */
    public void startGame() {
        this.gameLoopTimer.scheduleAtFixedRate(this.gameLoopTask, 0, 100);
    }

    /**
     * Stops the game loop.
     */
    public void stopGame() {
        this.gameLoopTimer.cancel();
    }

    /**
     * Sets up the game.
     */
    protected void setup() {
        setupWorld();
        setupRobots();
        this.inputHandler.install();
    }

    /**
     * Initializes the {@link World} and adds the {@link Robot}s to it.
     */
    public void setupWorld() {
        World.setSize(10, 10);
        World.setDelay(0);
        World.setVisible(true);
        MazeGenerator.generateMaze();
        World.getGlobalWorld().setFieldColor(0, World.getHeight() - 1, Color.YELLOW);
    }

    /**
     * Adds the {@link Robot}s to the {@link World}.
     */
    public void setupRobots() {
        this.robots.add(new CleaningRobot(
            0,
            0,
            Direction.UP,
            0)
        );
        this.robots.add(new Contaminant1(
            World.getWidth() - 1,
            0,
            Direction.UP,
            5 * World.getWidth() * World.getHeight())
        );
        this.robots.add(new Contaminant2(
            World.getWidth() - 1,
            World.getHeight() - 1,
            Direction.UP,
            2 * World.getWidth() * World.getHeight())
        );
    }

    /**
     * Checks the win condition.
     */
    public abstract void checkWinCondition();
}