package h01.template;

import fopbot.Direction;
import fopbot.World;

import java.awt.Point;
import java.util.*;

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
        // Create a 2D array to keep track of the walls
        final WallBlock[][] walls = new WallBlock[World.getWidth()][World.getHeight()];
        for (var x = 0; x < World.getWidth(); x++) {
            for (var y = 0; y < World.getHeight(); y++) {
                walls[x][y] = new WallBlock(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);
            }
        }

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
                final var next = neighbours.get(Utils.rnd.nextInt(neighbours.size()));
                visited[next.x][next.y] = true;

                walls[current.x][current.y].removeWall(Utils.getRelativeDirection(current, next));
                walls[next.x][next.y].removeWall(Utils.getRelativeDirection(next, current));

                stack.push(next);
                current = next;
            } else {
                // Backtrack to the previous cell
                current = stack.pop();
            }
        }

        // Place the walls
        for (var x = 0; x < World.getWidth(); x++) {
            for (var y = 0; y < World.getHeight(); y++) {
                final var wallBlock = walls[x][y];
                if (wallBlock != null) {
                    for (final var wall : wallBlock.getWalls()) {
                        switch (wall) {
                            case UP -> World.placeHorizontalWall(x, y);
                            //case DOWN -> World.placeHorizontalWall(x, y - 1); // Not needed, will lead to an exception
                            //case LEFT -> World.placeVerticalWall(x - 1, y); // Not needed, will lead to an exception
                            case RIGHT -> World.placeVerticalWall(x, y);
                        }
                    }
                }
            }
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
            if (Utils.isValidCoordinate(p.x + dx, p.y + dy)) {
                neighbours.add(new Point(p.x + dx, p.y + dy));
            }

            // Rotate 90 degrees clockwise for the next direction
            final int tmp = dx;
            dx = -dy;
            dy = tmp;
        }

        return neighbours;
    }
}
