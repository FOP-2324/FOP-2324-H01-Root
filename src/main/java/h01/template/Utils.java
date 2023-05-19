package h01.template;

import fopbot.Coin;
import fopbot.World;

import java.util.Random;

/**
 * A collection of utility methods.
 */
public final class Utils {
    /**
     * Private constructor to prevent instantiation.
     */
    private Utils() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * The random number generator used by this exercise.
     */
    public static Random rnd = new Random();

    /**
     * Gets a random integer between min and max (both inclusive).
     *
     * @param min inclusive minimum (must be smaller than max)
     * @param max inclusive maximum (must be greater than min)
     * @return a random integer between min and max (both inclusive)
     */
    public static int getRandomInteger(final int min, final int max) {
        return min + rnd.nextInt(max - min + 1);
    }

    /**
     * Gets the amount of coins at the given position.
     *
     * @param x the x coordinate of field to check
     * @param y the y coordinate of field to check
     * @return the amount of coins at the given position
     */
    public static int getCoinAmount(final int x, final int y) {
        return World.getGlobalWorld().getAllFieldEntities().stream()
            .filter(e -> e.getX() == x && e.getY() == y)
            .filter(e -> e instanceof Coin)
            .map(e -> (Coin) e)
            .mapToInt(Coin::getCount)
            .sum();
    }
}
