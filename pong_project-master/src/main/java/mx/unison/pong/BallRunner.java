
package mx.unison.pong;

import java.awt.geom.Ellipse2D;

public class BallRunner implements Runnable {

    public static final int MAX_X = 640;
    public static final int MAX_Y = 480;
    public static final int SIZE = 16;

    private final Ellipse2D.Double ball;
    private final Paddle paddle;
    private final BlockManager blockManager;

    private int ballX;
    private int ballY;

    private int directionX = 1;
    private int directionY = -1;

    private volatile boolean isPaused = false;
    private volatile boolean stop = false;

    private int score = 0;

    public BallRunner(
            Ellipse2D.Double ball,
            Paddle paddle,
            BlockManager blockManager) {

        this.ball = ball;
        this.paddle = paddle;
        this.blockManager = blockManager;

        ballX = 312;
        ballY = 350;

        ball.setFrame(ballX, ballY, SIZE, SIZE);
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    public void stop() {
        stop = true;
    }

    public int getScore() {
        return score;
    }

    public synchronized void reset() {
        ballX = 312;
        ballY = 350;

        directionX = 1;
        directionY = -1;

        ball.setFrame(ballX, ballY, SIZE, SIZE);
    }

    @Override
    public void run() {

        while (!stop) {

            if (isPaused) {
                sleep();
                continue;
            }

            int nextX = ballX + (20 * directionX);
            int nextY = ballY + (20 * directionY);

            // Pared izquierda
            if (nextX <= 0) {
                directionX = 1;
            }

            // Pared derecha
            if (nextX + SIZE >= MAX_X) {
                directionX = -1;
            }

            // Techo
            if (nextY <= 0) {
                directionY = 1;
            }

            nextX = ballX + (20 * directionX);
            nextY = ballY + (20 * directionY);

            // Colisión con la raqueta
            Ellipse2D.Double nextBall =
                    new Ellipse2D.Double(
                            nextX, nextY, SIZE, SIZE);

            if (directionY > 0 &&
                    paddle.intersects(nextBall)) {

                directionY = -1;

                nextY = (int) paddle.getPaddle().getMinY() - SIZE;
            }

            // Colisión con bloques
            if (CollisionManager.checkBlockCollision(
                    nextBall, blockManager)) {

                directionY *= -1;
                score += 10;
            }

            // Actualizar posición
            ballX = nextX;
            ballY = nextY;

            // Detectar pérdida de vida
            if (ballY + SIZE >= MAX_Y) {
                reset();
            }

            ball.setFrame(ballX, ballY, SIZE, SIZE);

            sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            stop = true;
        }
    }
}
