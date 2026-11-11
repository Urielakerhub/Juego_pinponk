package mx.unison.pong;

import java.awt.Color;
import java.awt.Rectangle;

public class Block {

    private final Rectangle bounds;
    private final Color color;

    private boolean destroyed;

    public Block(int x, int y, int width, int height, Color color) {

        bounds = new Rectangle(x, y, width, height);
        this.color = color;

        destroyed = false;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Color getColor() {
        return color;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void destroy() {
        destroyed = true;
    }
}
