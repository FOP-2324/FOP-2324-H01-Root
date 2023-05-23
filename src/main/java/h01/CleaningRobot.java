package h01;

import fopbot.Direction;
import fopbot.Robot;
import h01.template.Cleaner;
import h01.template.TickBased;

/**
 * A robot that can clean the floor.
 */
public class CleaningRobot extends Robot implements Cleaner, TickBased {

    /**
     * Creates a new {@link CleaningRobot}.
     *
     * @param x             the initial x coordinate of the robot
     * @param y             the initial y coordinate of the robot
     * @param direction     the initial direction of the robot
     * @param numberOfCoins the initial number of coins of the robot
     */
    public CleaningRobot(final int x, final int y, final Direction direction, final int numberOfCoins) {
        super(x, y, direction, numberOfCoins);
    }

    @Override
    public void handleInput(final int direction, final boolean shouldPutCoins, final boolean shouldPickCoins) {
        // <solution H1>
        if (shouldPutCoins /*&& !isOnACoin()*/ && hasAnyCoins()) {
            putCoin();
        }
        if (shouldPickCoins && isOnACoin() && getNumberOfCoins() < 25) {
            pickCoin();
        }
        if (direction >= 0 && direction < Direction.values().length) {
            while (getDirection() != Direction.values()[direction]) {
                turnLeft();
            }
            if (isFrontClear()) {
                move();
            }
        }
        // </solution>
    }
}
