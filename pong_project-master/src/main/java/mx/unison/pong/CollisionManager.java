package mx.unison.pong;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.Iterator;

public final class CollisionManager {
    private CollisionManager() { }

    public static void checkWallCollision(BallRunner ball, int width, int height) {
        Ellipse2D.Double bounds = ball.getBounds();

        if (bounds.getMinX() <= 0) {
            ball.setPosition(0, ball.getY());
            ball.bounceX();
        } else if (bounds.getMaxX() >= width) {
            ball.setPosition(width - BallRunner.SIZE, ball.getY());
            ball.bounceX();
        }

        if (bounds.getMinY() <= 0) {
            ball.setPosition(ball.getX(), 0);
            ball.bounceY();
        }
    }

    public static void checkPaddleCollision(BallRunner ball, Paddle paddle) {
        Rectangle paddleBounds = paddle.getBounds();
        Ellipse2D.Double ballBounds = ball.getBounds();

        if (ball.getVelocityY() > 0
                && ballBounds.intersects(paddleBounds)
                && ball.getPreviousY() + BallRunner.SIZE <= paddleBounds.getMinY()) {
            double center = ball.getX() + BallRunner.SIZE / 2.0;
            double offset = (center - paddleBounds.getCenterX())
                    / (paddleBounds.width / 2.0);
            offset = Math.max(-1.0, Math.min(1.0, offset));
            ball.setPosition(ball.getX(), paddleBounds.y - BallRunner.SIZE);
            ball.bounceFromPaddle(offset);
        }
    }

    public static boolean checkBlockCollision(BallRunner ball, BlockManager manager) {
        Ellipse2D.Double current = ball.getBounds();
        Rectangle previous = new Rectangle(
                (int) Math.round(ball.getPreviousX()),
                (int) Math.round(ball.getPreviousY()),
                BallRunner.SIZE, BallRunner.SIZE);

        Iterator<Block> iterator = manager.getBlocks().iterator();
        while (iterator.hasNext()) {
            Block block = iterator.next();
            Rectangle blockBounds = block.getBounds();

            if (current.intersects(blockBounds)) {
                // Determina si el golpe fue principalmente vertical u horizontal.
                if (previous.getMaxY() <= blockBounds.getMinY()
                        || previous.getMinY() >= blockBounds.getMaxY()) {
                    ball.bounceY();
                } else {
                    ball.bounceX();
                }
                iterator.remove();
                return true;
            }
        }
        return false;
    }
}
