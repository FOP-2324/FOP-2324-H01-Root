package h01;

import java.awt.*;

public class MethodReplacements {

    /**
     * Intercepts {@link Math#random()} and throws an exception.
     */
    public static double random() {
        throw new RuntimeException("The use of Math.random() is not allowed.");
    }

    /**
     * Intercepts {@link Thread#sleep(long)} and throws an exception.
     */
    public static void sleep(final long millis) {
        throw new RuntimeException("The use of Thread.sleep() is not allowed.");
    }

    /**
     * Intercepts {@link javax.swing.JOptionPane#showMessageDialog(java.awt.Component, Object)} and throws an exception.
     */
    public static void showMessageDialog(final Component parentComponent, final Object message) {
        throw new RuntimeException("The use of JOptionPane.showMessageDialog() is not allowed.");
    }
}
