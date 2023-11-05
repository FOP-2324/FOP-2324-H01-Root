package h01;

import fopbot.Direction;
import fopbot.World;
import h01.template.GameConstants;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;

import java.awt.Point;
import java.util.Map;

import static h01.TestConstants.SHOW_WORLD;
import static h01.TestConstants.WORLD_DELAY;

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
     * Creates a world with the given width and height and sets the GameConstants accordingly.
     *
     * @param worldWidth  The width of the world.
     * @param worldHeight The height of the world.
     */
    protected static void setupWorld(final int worldWidth, final int worldHeight) {
        World.setSize(worldWidth, worldHeight);
        World.setDelay(0);
        GameConstants.WORLD_WIDTH = worldWidth;
        GameConstants.WORLD_HEIGHT = worldHeight;
        if (SHOW_WORLD) {
            World.setDelay(WORLD_DELAY);
            World.setVisible(true);
        } else {
            World.setDelay(0);
        }
    }

    /**
     * Converts a {@link Direction} to a unit vector.
     *
     * @param d the direction to convert to a unit vector
     * @return the unit vector corresponding to the given direction
     */
    public static Point toUnitVector(final Direction d) {
        return directionToPoint.get(d);
    }

    /**
     * Converts a unit vector to a {@link Direction}.
     *
     * @param p the unit vector to convert to a direction
     * @return the direction corresponding to the given unit vector
     */
    public static Direction fromUnitVector(final Point p) {
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
    public static Direction opposite(final Direction d) {
        final var p = toUnitVector(d);
        return fromUnitVector(new Point(-p.x, -p.y));
    }

    /**
     * Returns the property with the given key from the given {@link JsonParameterSet} or the given default value if the
     * property is not available.
     *
     * @param params       the {@link JsonParameterSet} to get the property from
     * @param key          the key of the property to get
     * @param defaultValue the default value to return if the property is not available
     * @param <T>          the type of the property
     * @return the property with the given key from the given {@link JsonParameterSet} or the given default value if the
     *     property is not available
     */
    public static <T> T getPropertyOrDefault(final JsonParameterSet params, final String key, final T defaultValue) {
        return params.availableKeys().contains(key) ? params.get(key) : defaultValue;
    }
}
