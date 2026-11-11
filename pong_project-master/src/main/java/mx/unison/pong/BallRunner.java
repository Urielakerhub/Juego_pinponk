package mx.unison.pong;

import java.awt.geom.Ellipse2D;

/**
 * Controla la posición y velocidad de la pelota.
 * El temporizador Swing de Board llama a move() en cada actualización.
 */
public class BallRunner {
    public static final int SIZE = 16;

    private double x;
    private double y;
    private double previousX;
    private double previousY;
    private double velocityX = 3.4;
    private double velocityY = -4.2;

    private final Ellipse2D.Double bounds =
            new Ellipse2D.Double(0, 0, SIZE, SIZE);

    public BallRunner(int startX, int startY) {
        reset(startX, startY);
    }

    public void move() {
        previousX = x;
        previousY = y;
        x += velocityX;
        y += velocityY;
        updateBounds();
    }

    public void reset(int startX, int startY) {
        x = startX;
        y = startY;
        previousX = x;
        previousY = y;
        velocityX = 3.4;
        velocityY = -4.2;
        updateBounds();
    }

    public void bounceX() { velocityX = -velocityX; }
    public void bounceY() { velocityY = -velocityY; }

    public void bounceFromPaddle(double hitPosition) {
        // hitPosition va de -1 (extremo izquierdo) a 1 (extremo derecho).
        velocityX = hitPosition * 5.0;
        velocityY = -Math.max(3.5, Math.abs(velocityY));
    }

    public void setPosition(double newX, double newY) {
        x = newX;
        y = newY;
        updateBounds();
    }

    private void updateBounds() {
        bounds.setFrame(x, y, SIZE, SIZE);
    }

    public Ellipse2D.Double getBounds() { return bounds; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getPreviousX() { return previousX; }
    public double getPreviousY() { return previousY; }
    public double getVelocityY() { return velocityY; }
}
