package h01;

import fopbot.Direction;

import java.awt.*;
import java.util.Map;

public class TestUtils {

    private static final Map<Direction, Point> directionToPoint = Map.ofEntries(
        Map.entry(Direction.UP, new Point(0, 1)),
        Map.entry(Direction.RIGHT, new Point(1, 0)),
        Map.entry(Direction.DOWN, new Point(0, -1)),
        Map.entry(Direction.LEFT, new Point(-1, 0))
    );

    public static Point toUnitVector(Direction d) {
        return directionToPoint.get(d);
    }

    public static Direction fromUnitVector(Point p) {
        return directionToPoint.entrySet().stream()
            .filter(e -> e.getValue().equals(p))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElseThrow();
    }

    public static Direction opposite(Direction d) {
        final var p = toUnitVector(d);
        return fromUnitVector(new Point(-p.x, -p.y));
    }
}
