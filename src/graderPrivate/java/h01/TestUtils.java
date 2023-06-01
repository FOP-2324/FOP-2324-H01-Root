package h01;

import fopbot.Direction;

import java.awt.Point;
import java.util.Map;

/**
 * Utility methods for the private tests.
 */
public class TestUtils {

    /**
     * Maps a {@link Direction} to a unit vector.
     */
    private static final Map<Direction, Point> directionToPoint = Map.ofEntries(
        Map.entry(Direction.UP, new Point(0, 1)),
        Map.entry(Direction.RIGHT, new Point(1, 0)),
        Map.entry(Direction.DOWN, new Point(0, -1)),
        Map.entry(Direction.LEFT, new Point(-1, 0))
    );

    /**
     * Converts a {@link Direction} to a unit vector.
     *
     * @param d the direction to convert to a unit vector
     * @return the unit vector corresponding to the given direction
     */
    public static Point toUnitVector(Direction d) {
        return directionToPoint.get(d);
    }

    /**
     * Converts a unit vector to a {@link Direction}.
     *
     * @param p the unit vector to convert to a direction
     * @return the direction corresponding to the given unit vector
     */
    public static Direction fromUnitVector(Point p) {
        return directionToPoint.entrySet().stream()
            .filter(e -> e.getValue().equals(p))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElseThrow();
    }

    /**
     * Returns the opposite direction of the given direction.
     *
     * @param d the direction to get the opposite of
     * @return the opposite direction of the given direction
     */
    public static Direction opposite(Direction d) {
        final var p = toUnitVector(d);
        return fromUnitVector(new Point(-p.x, -p.y));
    }
}
