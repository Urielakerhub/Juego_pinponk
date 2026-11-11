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

    public void reset() {
        x = (boardWidth - WIDTH) / 2;
    }

    public void moveLeft() {
        x = Math.max(0, x - 7);
    }

    public void moveRight() {
        x = Math.min(boardWidth - WIDTH, x + 7);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
