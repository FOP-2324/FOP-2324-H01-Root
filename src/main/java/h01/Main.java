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
    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        setupWorld();
        handleKeyboardEvents();
        startGameLoop();
    }

    private void startGameLoop() {
        TimerTask gameLoopTask = new TimerTask() {
            @Override
            public void run() {
                for (Robot robot : robots) {
                    if (robot instanceof Cleaner r) {
                        r.handleInput(direction.get(), shouldPutCoins.get(), shouldPickCoins.get());
                    } else if (robot instanceof Offender r) {
                        r.doMove();
                    }
                }
                // check win condition
                checkWinCondition();
            }
        };
        gameLoopTimer.scheduleAtFixedRate(gameLoopTask, 0, 100);
    }

    private void checkWinCondition() {
        // If all Offenders are turned off, the game is won
        if (robots.stream().filter(r -> r instanceof Offender).allMatch(Robot::isTurnedOff)) {
            System.out.println("Cleaning robot won!");
            gameLoopTimer.cancel();
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
            gameLoopTimer.cancel();
        }
    }

    private void setupWorld() {
        World.setSize(10, 10);
        World.setDelay(0);
        World.setVisible(true);
        robots.add(new CleaningRobot(0, 0, Direction.UP, 0));
        robots.add(new Offender1(World.getWidth() - 1, 0, Direction.UP, 5 * World.getWidth() * World.getHeight()));
        MazeGenerator.generateMaze();
        World.getGlobalWorld().setFieldColor(0, World.getHeight() - 1, Color.YELLOW);
    }

    private void handleKeyboardEvents() {
        World.getGlobalWorld().getInputHandler().addListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                updateKeysPressed();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                updateKeysPressed();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                updateKeysPressed();
            }
        });
    }

    private void updateKeysPressed() {
        direction.set(Optional.ofNullable(Utils.getDirection(World.getGlobalWorld().getInputHandler().getKeysPressed()))
            .map(Enum::ordinal)
            .orElse(-1));
        shouldPutCoins.set(World.getGlobalWorld().getInputHandler().getKeysPressed().contains(java.awt.event.KeyEvent.VK_SPACE));
        shouldPickCoins.set(World.getGlobalWorld().getInputHandler().getKeysPressed().contains(java.awt.event.KeyEvent.VK_R));
    }
}
