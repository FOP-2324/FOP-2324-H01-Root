package h01;

import fopbot.World;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MazeGenerator {
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
