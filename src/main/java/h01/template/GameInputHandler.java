package h01.template;

import fopbot.World;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@link GameInputHandler} handles the input of the user.
 */
public class GameInputHandler {
    /**
     * The {@link fopbot.Direction} to turn to. If {@code -1} the robot should not turn.
     */
    private final AtomicInteger direction = new AtomicInteger(-1);
    /**
     * If {@code true} the robot should put a coin on the current field.
     */
    private final AtomicBoolean shouldPutCoins = new AtomicBoolean(false);
    /**
     * If {@code true} the robot should pick a coin from the current field.
     */
    private final AtomicBoolean shouldPickCoins = new AtomicBoolean(false);

    /**
     * Installs the {@link GameInputHandler} to the {@link World}.
     */
    public void install() {
        World.getGlobalWorld().getInputHandler().addListener(new KeyAdapter() {
            @Override
            public void keyTyped(final KeyEvent e) {
                updateKeysPressed();
            }

            @Override
            public void keyPressed(final KeyEvent e) {
                updateKeysPressed();
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                updateKeysPressed();
            }
        });
    }

    /**
     * Updates the {@link #direction}, {@link #shouldPutCoins} and {@link #shouldPickCoins} based on the pressed keys.
     */
    protected void updateKeysPressed() {
        this.direction.set(
            Optional.ofNullable(MazeGenerator.getDirectionFromKeysPressed(World.getGlobalWorld().getInputHandler().getKeysPressed()))
                .map(Enum::ordinal)
                .orElse(-1)
        );
        this.shouldPickCoins.set(
            World.getGlobalWorld().getInputHandler().getKeysPressed().contains(java.awt.event.KeyEvent.VK_SPACE)
        );
        this.shouldPutCoins.set(
            World.getGlobalWorld().getInputHandler().getKeysPressed().contains(java.awt.event.KeyEvent.VK_R)
        );
    }

    /**
     * Returns the current {@link fopbot.Direction} to turn to. If {@code -1} the robot should not turn.
     *
     * @return the current {@link fopbot.Direction} to turn to. If {@code -1} the robot should not turn.
     */
    public int getDirection() {
        return this.direction.get();
    }

    /**
     * Returns whether the robot should put a coin on the current field.
     *
     * @return whether the robot should put a coin on the current field.
     */
    public boolean getShouldPickCoins() {
        return this.shouldPickCoins.get();
    }

    /**
     * Returns whether the robot should pick a coin from the current field.
     *
     * @return whether the robot should pick a coin from the current field.
     */
    public boolean getShouldPutCoins() {
        return this.shouldPutCoins.get();
    }
}
