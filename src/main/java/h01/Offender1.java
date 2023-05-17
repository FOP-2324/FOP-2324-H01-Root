package h01;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.RobotFamily;

import java.util.ArrayList;
import java.util.List;

public class Offender1 extends Robot implements Offender {

    private final int updateDelay = 5;
    private int lastUpdate = 0;

    public Offender1(int x, int y, Direction direction, int numberOfCoins) {
        super(x, y, direction, numberOfCoins, RobotFamily.SQUARE_ORANGE);
    }

    @Override
    public void doMove() {
        if (getNumberOfCoins() == 0) {
            turnOff();
            return;
        }
        if (isTurnedOff()) {
            return;
        }
        if (lastUpdate < updateDelay) {
            lastUpdate++;
            return;
        } else {
            lastUpdate = 0;
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
        List<Direction> validPaths = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            turnLeft();
            if (isFrontClear()) {
                validPaths.add(getDirection());
            }
        }
        // get random path
        if (validPaths.isEmpty()) {
            return;
        }
        Direction randomPath = validPaths.get(Utils.getRandomInteger(0, validPaths.size() - 1));
        while (getDirection() != randomPath) {
            turnLeft();
        }
        move();
    }
}