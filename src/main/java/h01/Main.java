package h01;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.World;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Main entry point in executing the program.
 */
public class Main {

    private final AtomicInteger direction = new AtomicInteger(-1);
    private final AtomicBoolean shouldPutCoins = new AtomicBoolean(false);
    private final AtomicBoolean shouldPickCoins = new AtomicBoolean(false);

    private final Timer gameLoopTimer = new Timer();
    private final Set<Robot> robots = new HashSet<>();

    /**
     * Main entry point in executing the program.
     *
     * @param args program arguments, currently ignored
     */
    public static void main(final String[] args) {
        new Main().run();
    }

    public void run() {
        setupWorld();
        handleKeyboardEvents();
        startGameLoop();
    }

    private void startGameLoop() {
        final TimerTask gameLoopTask = new TimerTask() {
            @Override
            public void run() {
                for (final Robot robot : Main.this.robots) {
                    if (robot instanceof final Cleaner r) {
                        r.handleInput(Main.this.direction.get(), Main.this.shouldPutCoins.get(), Main.this.shouldPickCoins.get());
                    } else if (robot instanceof final Contaminant r) {
                        r.doMove();
                    }
                }
                // check win condition
                checkWinCondition();
            }
        };
        this.gameLoopTimer.scheduleAtFixedRate(gameLoopTask, 0, 100);
    }

    private void checkWinCondition() {
        // If all Offenders are turned off, the game is won
        if (this.robots.stream().filter(r -> r instanceof Contaminant).allMatch(Robot::isTurnedOff)) {
            System.out.println("Cleaning robot won!");
            this.gameLoopTimer.cancel();
        }
        // if more than 50% of all fields are dirty, the game is lost
        int dirtyFields = 0;
        for (int x = 0; x < World.getWidth(); x++) {
            for (int y = 0; y < World.getHeight(); y++) {
                if (Utils.getCoinAmount(x, y) > 0) {
                    dirtyFields++;
                }
            }
        }
        if (dirtyFields > World.getWidth() * World.getHeight() / 2) {
            System.out.println("Offenders won!");
            this.gameLoopTimer.cancel();
        }
    }

    private void setupWorld() {
        World.setSize(10, 10);
        World.setDelay(0);
        World.setVisible(true);
        this.robots.add(new CleaningRobot(0, 0, Direction.UP, 0));
        this.robots.add(new Contaminant1(World.getWidth() - 1, 0, Direction.UP, 5 * World.getWidth() * World.getHeight()));
        this.robots.add(new Contaminant2(World.getWidth() - 1, World.getHeight() - 1, Direction.UP, 2 * World.getWidth() * World.getHeight()));
        MazeGenerator.generateMaze();
        World.getGlobalWorld().setFieldColor(0, World.getHeight() - 1, Color.YELLOW);
    }

    private void handleKeyboardEvents() {
        World.getGlobalWorld().getInputHandler().addListener(new KeyAdapter() {
            @Override
            public void keyTyped(final KeyEvent e) {
                updateKeysPressed();
            }

            @Override
            public void keyPressed(final KeyEvent e) {
                updateKeysPressed();
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                updateKeysPressed();
            }
        });
    }

    private void updateKeysPressed() {
        this.direction.set(Optional.ofNullable(Utils.getDirection(World.getGlobalWorld().getInputHandler().getKeysPressed()))
            .map(Enum::ordinal)
            .orElse(-1));
        this.shouldPutCoins.set(World.getGlobalWorld().getInputHandler().getKeysPressed().contains(java.awt.event.KeyEvent.VK_SPACE));
        this.shouldPickCoins.set(World.getGlobalWorld().getInputHandler().getKeysPressed().contains(java.awt.event.KeyEvent.VK_R));
    }
}
