package h01;

import com.fasterxml.jackson.databind.JsonNode;
import fopbot.Direction;
import fopbot.World;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Base class for all robot tests.
 */
public abstract class RobotTest {
    /**
     * The custom converters for this test class.
     */
    @SuppressWarnings("unused")
    public static final Map<String, Function<JsonNode, ?>> customConverters = Map.ofEntries(
        Map.entry("worldWidth", JsonNode::asInt),
        Map.entry("worldHeight", JsonNode::asInt),
        Map.entry("cleaningRobot", JsonConverters::toCleaningRobot),
        Map.entry("walls", n -> JsonConverters.toList(n, JsonConverters::toDirection)),
        Map.entry("initialCoinsOnField", JsonNode::asInt),
        Map.entry("contaminant1", JsonConverters::toContaminant1),
        Map.entry("contaminant2", JsonConverters::toContaminant2),
        Map.entry("direction", JsonNode::asInt),
        Map.entry("shouldMove", JsonNode::asBoolean),
        Map.entry("canMove", JsonNode::asBoolean),
        Map.entry("shouldPutCoins", JsonNode::asBoolean),
        Map.entry("shouldPickCoins", JsonNode::asBoolean),
        Map.entry("expectedEndPosition", JsonConverters::toPoint),
        Map.entry("expectedEndDirection", JsonConverters::toDirection),
        Map.entry("expectedRobotCoinDelta", JsonNode::asInt)
    );

    /**
     * Places Walls in the given directions at the given position.
     *
     * @param walls The directions to place walls in.
     * @param x     The x coordinate of the position to place the walls at.
     * @param y     The y coordinate of the position to place the walls at.
     */
    protected static void placeWalls(final Set<Direction> walls, final int x, final int y) {
        // Set Walls
        for (final Direction wall : walls) {
            final var directionVector = TestUtils.toUnitVector(wall);
            if (directionVector.x == 0) {
                World.placeHorizontalWall(x, y + Math.min(0, directionVector.y));
            } else {
                World.placeVerticalWall(x + Math.min(0, directionVector.x), y);
            }
        }
    }

}
