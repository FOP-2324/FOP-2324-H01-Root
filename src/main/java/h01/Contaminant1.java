package h01;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.RobotFamily;

import java.util.ArrayList;
import java.util.List;

public class Contaminant1 extends Robot implements Contaminant {

    private final int updateDelay = 5;
    private int lastUpdate = 0;

    public Contaminant1(final int x, final int y, final Direction direction, final int numberOfCoins) {
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
        if (this.lastUpdate < this.updateDelay) {
            this.lastUpdate++;
            return;
        } else {
            this.lastUpdate = 0;
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
        final List<Direction> validPaths = new ArrayList<>();
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
        final Direction randomPath = validPaths.get(Utils.getRandomInteger(0, validPaths.size() - 1));
        while (getDirection() != randomPath) {
            turnLeft();
        }
        move();
    }
}
