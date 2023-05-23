package h01;

import fopbot.Robot;
import fopbot.World;
import h01.template.Contaminant;
import h01.template.GameControllerBase;
import h01.template.Utils;

/**
 * A {@link GameController} controls the game loop and the {@link Robot}s and checks the win condition.
 */
public class GameController extends GameControllerBase {

    /**
     * Creates a new {@link GameControllerBase}.
     */
    public GameController() {
        setup();
    }

    @Override
    public void checkWinCondition() {
        // <solution H3>
        // If all Offenders are turned off, the game is won
        if (this.robots.stream().filter(r -> r instanceof Contaminant).allMatch(Robot::isTurnedOff)) {
            getContaminant1().turnOff();
            getContaminant2().turnOff();
            System.out.println("Cleaning robot won!");
            stopGame();
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
            getCleaner().turnOff();
            System.out.println("Contaminants won!");
            stopGame();
        }
        // </solution H3>
    }
}
