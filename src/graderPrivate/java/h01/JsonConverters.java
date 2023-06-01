package h01;

import com.fasterxml.jackson.databind.JsonNode;
import fopbot.Direction;

import java.awt.Point;

/**
 * Custom JSON converters for the {@link h01} exercise.
 */
public class JsonConverters extends org.tudalgo.algoutils.tutor.general.json.JsonConverters {
    /**
     * Converts a JSON node to a {@link Point}.
     *
     * @param jsonNode The JSON node to convert.
     * @return The converted {@link Point}.
     */
    public static Point toPoint(final JsonNode jsonNode) {
        return new Point(
            jsonNode.get("x").asInt(),
            jsonNode.get("y").asInt()
        );
    }

    /**
     * Converts a JSON node to a {@link Direction}.
     *
     * @param jsonNode The JSON node to convert.
     * @return The converted {@link Direction}.
     */
    public static Direction toDirection(final JsonNode jsonNode) {
        return Direction.valueOf(jsonNode.asText());
    }

    /**
     * Converts a JSON node to a {@link CleaningRobot}.
     *
     * @param jsonNode The JSON node to convert.
     * @return The converted {@link CleaningRobot}.
     */
    public static CleaningRobot toCleaningRobot(final JsonNode jsonNode) {
        return new CleaningRobot(
            jsonNode.get("x").asInt(),
            jsonNode.get("y").asInt(),
            toDirection(jsonNode.get("direction")),
            jsonNode.get("numberOfCoins").asInt()
        );
    }

    /**
     * Converts a JSON node to a {@link Contaminant1}.
     *
     * @param jsonNode The JSON node to convert.
     * @return The converted {@link Contaminant1}.
     */
    public static Contaminant1 toContaminant1(final JsonNode jsonNode) {
        return new Contaminant1(
            jsonNode.get("x").asInt(),
            jsonNode.get("y").asInt(),
            toDirection(jsonNode.get("direction")),
            jsonNode.get("numberOfCoins").asInt()
        );
    }

    /**
     * Converts a JSON node to a {@link Contaminant2}.
     *
     * @param jsonNode The JSON node to convert.
     * @return The converted {@link Contaminant2}.
     */
    public static Contaminant2 toContaminant2(final JsonNode jsonNode) {
        return new Contaminant2(
            jsonNode.get("x").asInt(),
            jsonNode.get("y").asInt(),
            toDirection(jsonNode.get("direction")),
            jsonNode.get("numberOfCoins").asInt()
        );
    }
}
