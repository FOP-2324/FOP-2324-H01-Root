package h01;

public class TestConstants {
    public static final boolean SHOW_WORLD = java.lang.management.ManagementFactory
        .getRuntimeMXBean()
        .getInputArguments()
        .toString()
        .contains("-agentlib:jdwp"); // true if debugger is attached
    public static final int WORLD_DELAY = 500;

    public static final int TEST_TIMEOUT_IN_SECONDS = 2;

    public static final boolean SKIP_AFTER_FIRST_FAILED_TEST = true;
}
