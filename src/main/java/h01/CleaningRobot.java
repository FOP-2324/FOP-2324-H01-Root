package h01;

import fopbot.Direction;
import fopbot.Robot;

public class CleaningRobot extends Robot implements Cleaner {

    public CleaningRobot(final int x, final int y, final Direction direction, final int numberOfCoins) {
        super(x, y, direction, numberOfCoins);
    }

    @Override
    public void handleInput(final int direction, final boolean shouldPutCoins, final boolean shouldPickCoins) {
        // System.out.printf(
        //     "direction: %d, shouldPutCoins: %b, shouldPickCoins: %b\n",
        //     direction,
        //     shouldPutCoins,
        //     shouldPickCoins
        // );
        if (shouldPutCoins /*&& !isOnACoin()*/ && hasAnyCoins()) {
            putCoin();
        }
        if (shouldPickCoins && isOnACoin() && getNumberOfCoins() < 25) {
            pickCoin();
        }
        if (direction >= 0) {
            while (getDirection() != Direction.values()[direction]) {
                turnLeft();
            }
            if (isFrontClear()) {
                move();
            }
        }
    }
}
