package h01;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.RobotFamily;
import h01.template.Contaminant;
import h01.template.TickBased;
import h01.template.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Contaminant}-{@link Robot} that moves randomly and contaminates the floor.
 */
public class Contaminant1 extends Robot implements Contaminant, TickBased {

    /**
     * Creates a new {@link Contaminant1}.
     *
     * @param x             the initial x coordinate of the robot
     * @param y             the initial y coordinate of the robot
     * @param direction     the initial direction of the robot
     * @param numberOfCoins the initial number of coins of the robot
     */
    public Contaminant1(final int x, final int y, final Direction direction, final int numberOfCoins) {
        super(x, y, direction, numberOfCoins, RobotFamily.SQUARE_ORANGE);
    }

    @Override
    public int getUpdateDelay() {
        return 10;
    }

    @Override
    public void doMove() {
        // <solution H2.1>
        if (getNumberOfCoins() == 0) {
            turnOff();
            return;
        }
        if (isTurnedOff()) {
            return;
        }
        // lay random amount of coins between 1 and 5
        if (!isOnACoin() || Utils.getCoinAmount(getX(), getY()) < 10) {
            for (int i = 0; i < Utils.getRandomInteger(1, 5); i++) {
                if (!hasAnyCoins() || Utils.getCoinAmount(getX(), getY()) >= 10) {
                    break;
                }
                putCoin();
            }
        }
        // get valid paths
        Direction path0 = null;
        Direction path1 = null;
        Direction path2 = null;
        Direction path3 = null;
        int validPathsCount = 0;

        for (int i = 0; i < 4; i++) {
            turnLeft();
            if (isFrontClear()) {
                validPathsCount++;
                if (path0 == null){
                    path0 = getDirection();
                } else if (path1 == null) {
                    path1 = getDirection();
                } else if (path2 == null) {
                    path2 = getDirection();
                } else {
                    path3 = getDirection();
                }
            }
        }
        // get random path
        if (path0 == null && path1 == null && path2 == null && path3 == null) {
            return;
        }

        final int randomPathIndex = Utils.getRandomInteger(0, validPathsCount-1);
        Direction path = null;
        if (randomPathIndex == 0) {
            path = path0;
        } else if (randomPathIndex == 1) {
            path = path1;
        } else if (randomPathIndex == 2) {
            path = path2;
        } else {
            path = path3;
        }
        while (getDirection() != path) {
            turnLeft();
        }
        move();
        // </solution>
    }
}
