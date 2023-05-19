package h01.template;

import fopbot.Direction;
import fopbot.World;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * A {@link MazeGenerator} generates a maze using a maze generation algorithm and ensures that every field is reachable.
 */
public final class MazeGenerator {
    private MazeGenerator() {
    }

    /**
     * Generates a maze using a maze generation algorithm and ensures that every field is reachable.
     * Walls are placed using World.placeHorizontalWall and World.placeVerticalWall.
     */
    public static void generateMaze() {
        // Create a 2D array to keep track of visited fields
        final var visited = new boolean[World.getWidth()][World.getHeight()];

        // Create a stack to track the visited cells
        final var stack = new ArrayDeque<Point>();

        // Start the maze generation from the top-left corner (0, 0)
        var current = new Point(0, 0);
        visited[current.x][current.y] = true;
        stack.push(current);

        // Continue until all cells have been visited
        while (!stack.isEmpty()) {
            // Get the unvisited neighboring cells of the current cell
            final var neighbours = getNeighbours(current)
                .stream()
                .filter(p -> !visited[p.x][p.y])
                .toList();

            if (!neighbours.isEmpty()) {
                // Choose a random neighboring cell
                final var next = neighbours.get(new Random().nextInt(neighbours.size()));
                visited[next.x][next.y] = true;

                // Place walls inside the maze grid
                if (next.x == current.x) {
                    // If the next cell is in the same column, place a vertical wall
                    // The wall should be placed one cell below the current cell to prevent blocking the path
                    World.placeVerticalWall(current.x, Math.min(current.y, next.y) + 1);
                } else {
                    // If the next cell is in the same row, place a horizontal wall
                    // The wall should be placed one cell to the right of the current cell to prevent blocking the path
                    World.placeHorizontalWall(Math.min(current.x, next.x) + 1, current.y);
                }

                stack.push(next);
                current = next;
            } else {
                // Backtrack to the previous cell
                current = stack.pop();
            }
        }
    }

    /**
     * Parses the inputs to a direction.
     *
     * @param keysPressed the keys pressed
     * @return the direction or null if no direction is pressed
     */
    public static Direction getDirection(final Set<Integer> keysPressed) {
        final Map<Direction, List<Integer>> directionKeys = Map.of(
            Direction.UP, List.of(java.awt.event.KeyEvent.VK_UP, java.awt.event.KeyEvent.VK_W),
            Direction.LEFT, List.of(java.awt.event.KeyEvent.VK_LEFT, java.awt.event.KeyEvent.VK_A),
            Direction.DOWN, List.of(java.awt.event.KeyEvent.VK_DOWN, java.awt.event.KeyEvent.VK_S),
            Direction.RIGHT, List.of(java.awt.event.KeyEvent.VK_RIGHT, java.awt.event.KeyEvent.VK_D)
        );
        final Set<Direction> pressedDirections = new HashSet<>();
        for (final Direction direction : directionKeys.keySet()) {
            for (final Integer key : directionKeys.get(direction)) {
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

    /**
     * Returns a list of neighboring cells for the given cell.
     *
     * @param p The cell coordinates
     * @return List of neighboring cells
     */
    private static List<Point> getNeighbours(final Point p) {
        final var neighbours = new ArrayList<Point>();
        int dx = 1;
        int dy = 0;

        // Iterate through all four directions (right, down, left, up)
        for (int i = 0; i < 4; i++) {
            // Check if the neighboring cell is within the maze grid
            if (isValidCoordinate(p.x + dx, p.y + dy)) {
                neighbours.add(new Point(p.x + dx, p.y + dy));
            }

            // Rotate 90 degrees clockwise for the next direction
            final int tmp = dx;
            dx = -dy;
            dy = tmp;
        }

        return neighbours;
    }

    /**
     * Returns {@code true} if the given coordinate is a valid coordinate in the current world.
     *
     * @param x the x coordinate to check
     * @param y the y coordinate to check
     * @return {@code true} if the given coordinate is a valid coordinate in the current world
     */
    public static boolean isValidCoordinate(final int x, final int y) {
        return x >= 0 && x < World.getWidth() && y >= 0 && y < World.getHeight();
    }
}
