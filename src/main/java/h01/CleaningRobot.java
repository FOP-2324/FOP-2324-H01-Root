package h01;

import fopbot.Direction;
import fopbot.Robot;

public class CleaningRobot extends Robot implements Cleaner {

    public CleaningRobot(int x, int y, Direction direction, int numberOfCoins) {
        super(x, y, direction, numberOfCoins);
    }

    @Override
    public void handleInput(int direction, boolean shouldPutCoins, boolean shouldPickCoins) {
        // System.out.printf(
        //     "direction: %d, shouldPutCoins: %b, shouldPickCoins: %b\n",
        //     direction,
        //     shouldPutCoins,
        //     shouldPickCoins
        // );
        if (shouldPutCoins /*&& !isOnACoin()*/ && hasAnyCoins()) {
            putCoin();
        }
        if (shouldPickCoins && isOnACoin()) {
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
