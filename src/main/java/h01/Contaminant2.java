package h01;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.RobotFamily;

public class Contaminant2 extends Robot implements Contaminant {

    private final int updateDelay = 3;
    private int lastUpdate = 0;

    public Contaminant2(final int x, final int y, final Direction direction, final int numberOfCoins) {
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
        Direction left = null, back = null, right = null, front = null;
        int validPathsCount = 0;
        for (int i = 0; i < 4; i++) {
            turnLeft();
            if (isFrontClear()) {
                if (i == 0) {
                    left = getDirection();
                } else if (i == 1) {
                    back = getDirection();
                } else if (i == 2) {
                    right = getDirection();
                } else {
                    front = getDirection();
                }
                validPathsCount++;
            }
        }
        // check if there are any valid paths
        if (validPathsCount == 0) {
            return;
        }
        // orient on left wall
        Direction direction = null;
        if(left!=null){
            direction = left;
        } else if(front!=null){
            direction = front;
        } else if(right!=null){
            direction = right;
        } else if(back!=null){
            direction = back;
        }
        while (getDirection() != direction) {
            turnLeft();
        }
        move();
    }
}
