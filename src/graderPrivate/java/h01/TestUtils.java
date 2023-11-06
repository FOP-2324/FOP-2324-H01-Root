package h01;

import fopbot.Direction;
import fopbot.World;
import h01.template.GameConstants;
import h01.template.Utils;
import org.mockito.MockedStatic;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.callable.Callable;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;

import javax.annotation.Nullable;
import java.awt.Point;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static h01.TestConstants.SHOW_WORLD;
import static h01.TestConstants.WORLD_DELAY;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

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
        //noinspection UnstableApiUsage
        World.getGlobalWorld().setActionLimit(1024);
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
     * property is not available
     */
    public static <T> T getPropertyOrDefault(final JsonParameterSet params, final String key, final T defaultValue) {
        return params.availableKeys().contains(key) ? params.get(key) : defaultValue;
    }

    /**
     * Runs a given runnable while mocking the {@link Utils} class.
     *
     * @param replaceRndLogic             the logic to replace the random number generator with
     * @param additionalMockingBeforeCall the additional mocking to do before the runnable is called
     * @param additionalMockingAfterCall  the additional mocking to do after the runnable is called
     * @param runnable                    the runnable to run
     * @param context                     the context to use for the assertions
     * @param maxAmountOfRandomCalls      the maximum amount of random calls to allow
     */
    public static void withMockedUtilsClass(
        @Nullable final BiFunction<Integer, Integer, Integer> replaceRndLogic,
        final Consumer<MockedStatic<Utils>> additionalMockingBeforeCall,
        final Consumer<MockedStatic<Utils>> additionalMockingAfterCall,
        final Callable runnable,
        final Context context,
        final int maxAmountOfRandomCalls
    ) {
        try (final var utilsMock = mockStatic(Utils.class, CALLS_REAL_METHODS)) {
            final AtomicInteger invocations = new AtomicInteger();
            utilsMock.when(() -> Utils.getRandomInteger(anyInt(), anyInt())).thenAnswer(invocation -> {
                if (invocations.getAndIncrement() >= maxAmountOfRandomCalls) {
                    throw new IllegalStateException(String.format(
                        "Too many random calls (%d were permitted). Likely an infinite loop.",
                        maxAmountOfRandomCalls
                    ));
                }
                final int min = invocation.getArgument(0);
                final int max = invocation.getArgument(1);
                return replaceRndLogic != null
                    ? replaceRndLogic.apply(min, max)
                    : Utils.rnd.nextInt(max - min + 1) + min;
            });
            additionalMockingBeforeCall.accept(utilsMock);
            Assertions2.call(
                runnable,
                context,
                r -> "The Method threw an exception"
            );
            additionalMockingAfterCall.accept(utilsMock);
        }
    }

    /**
     * Runs a given runnable while mocking the {@link Utils} class.
     *
     * @param runnable               the runnable to run
     * @param context                the context to use for the assertions
     * @param maxAmountOfRandomCalls the maximum amount of random calls to allow
     */
    public static void withMockedUtilsClass(
        final Callable runnable,
        final Context context,
        final int maxAmountOfRandomCalls
    ) {
        withMockedUtilsClass(null, mock -> {}, mock -> {}, runnable, context, maxAmountOfRandomCalls);
    }
}
