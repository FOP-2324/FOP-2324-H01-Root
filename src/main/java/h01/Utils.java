package h01;

import fopbot.Coin;
import fopbot.Direction;
import fopbot.World;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Utils {
    private Utils() {
    }

    /**
     * Parses the inputs to a direction.
     *
     * @param keysPressed the keys pressed
     * @return the direction or null if no direction is pressed
     */
    public static Direction getDirection(Set<Integer> keysPressed) {
        Map<Direction, List<Integer>> directionKeys = Map.of(
            Direction.UP, List.of(java.awt.event.KeyEvent.VK_UP, java.awt.event.KeyEvent.VK_W),
            Direction.LEFT, List.of(java.awt.event.KeyEvent.VK_LEFT, java.awt.event.KeyEvent.VK_A),
            Direction.DOWN, List.of(java.awt.event.KeyEvent.VK_DOWN, java.awt.event.KeyEvent.VK_S),
            Direction.RIGHT, List.of(java.awt.event.KeyEvent.VK_RIGHT, java.awt.event.KeyEvent.VK_D)
        );
        Set<Direction> pressedDirections = new HashSet<>();
        for (Direction direction : directionKeys.keySet()) {
            for (Integer key : directionKeys.get(direction)) {
                if (keysPressed.contains(key)) {
                    pressedDirections.add(direction);
                }
            }
        }
        if (pressedDirections.size() == 1) {
            return pressedDirections.iterator().next();
        } else {
            return null;
        }
    }

    public static Random rnd = new Random();

    /**
     * Gets a random integer between min and max (both inclusive).
     *
     * @param min inclusive minimum (must be smaller than max)
     * @param max inclusive maximum (must be greater than min)
     * @return a random integer between min and max (both inclusive)
     */
    public static int getRandomInteger(int min, int max) {
        return min + rnd.nextInt(max - min + 1);
    }

    /**
     * Gets the amount of coins at the given position.
     *
     * @param x the x coordinate of field to check
     * @param y the y coordinate of field to check
     * @return the amount of coins at the given position
     */
    public static int getCoinAmount(int x, int y) {
        return (int) World.getGlobalWorld().getAllFieldEntities().stream()
            .filter(e -> e.getX() == x && e.getY() == y)
            .filter(e -> e instanceof fopbot.Coin)
            .map(e -> (Coin) e)
            .mapToInt(Coin::getCount)
            .sum();
    }

    /**
     * Returns {@code true} if the given coordinate is a valid coordinate in the current world.
     *
     * @param x the x coordinate to check
     * @param y the y coordinate to check
     * @return {@code true} if the given coordinate is a valid coordinate in the current world
     */
    public static boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < World.getWidth() && y >= 0 && y < World.getHeight();
    }
}
