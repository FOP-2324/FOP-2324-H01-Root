package h01;

import com.fasterxml.jackson.databind.JsonNode;
import fopbot.Direction;

import java.awt.*;

public class JsonConverters extends org.tudalgo.algoutils.tutor.general.json.JsonConverters {
    public static Point toPoint(final JsonNode jsonNode) {
        return new Point(
            jsonNode.get("x").asInt(),
            jsonNode.get("y").asInt()
        );
    }

    public static Direction toDirection(final JsonNode jsonNode) {
        return Direction.valueOf(jsonNode.asText());
    }

    public static CleaningRobot toCleaningRobot(final JsonNode jsonNode) {
        return new CleaningRobot(
            jsonNode.get("x").asInt(),
            jsonNode.get("y").asInt(),
            toDirection(jsonNode.get("direction")),
            jsonNode.get("numberOfCoins").asInt()
        );
    }

    public static Contaminant1 toContaminant1(final JsonNode jsonNode) {
        return new Contaminant1(
            jsonNode.get("x").asInt(),
            jsonNode.get("y").asInt(),
            toDirection(jsonNode.get("direction")),
            jsonNode.get("numberOfCoins").asInt()
        );
    }

    public static Contaminant2 toContaminant2(final JsonNode jsonNode) {
        return new Contaminant2(
            jsonNode.get("x").asInt(),
            jsonNode.get("y").asInt(),
            toDirection(jsonNode.get("direction")),
            jsonNode.get("numberOfCoins").asInt()
        );
    }
}
