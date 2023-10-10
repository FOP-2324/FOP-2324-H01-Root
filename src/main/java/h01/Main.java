package h01;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.World;

import static org.tudalgo.algoutils.student.test.StudentTestUtils.*;

/**
 * Main entry point in executing the program.
 */
public class Main {
    /**
     * Main entry point in executing the program.
     *
     * @param args program arguments, currently ignored
     */
    public static void main(final String[] args) {
//        testEquals(1, 1);
//        testEquals(1, m());
//        testEquals(1, 1);
//
//
//        printTestResults();
        new GameController().startGame();
    }
}
