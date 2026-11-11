
package mx.unison.pong;

import java.awt.Rectangle;

public class Paddle {

    public static final int WIDTH = 90;
    public static final int HEIGHT = 15;

    private int x;
    private final int y;

    private final int boardWidth;

    public Paddle(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.x = (boardWidth - WIDTH) / 2;
        this.y = boardHeight - 55;
    }

    public void moveLeft() {
        x -= 8;

        if (x < 0) {
            x = 0;
        }
    }

    public void moveRight() {
        x += 8;

        if (x + WIDTH > boardWidth) {
            x = boardWidth - WIDTH;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
