package h01;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.RobotFamily;

import java.util.ArrayList;
import java.util.List;

public class Offender2 extends Robot implements Offender {

    private final int updateDelay = 3;
    private int lastUpdate = 0;

    public Offender2(int x, int y, Direction direction, int numberOfCoins) {
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
        // lay 2 coins
        if (!isOnACoin() || Utils.getCoinAmount(getX(), getY()) < 2) {
            for (int i = 0; i < 2; i++) {
                if (!hasAnyCoins() || Utils.getCoinAmount(getX(), getY()) >= 2) {
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
        if (validPaths.isEmpty()) {
            return;
        }
        // orient on right wall
        var direction = getDirection();
        while (getDirection() != validPaths.get(validPaths.size()-1)) {
            turnLeft();
        }
        move();
        while (getDirection() != direction) {
            turnLeft();
        }
    }
}
