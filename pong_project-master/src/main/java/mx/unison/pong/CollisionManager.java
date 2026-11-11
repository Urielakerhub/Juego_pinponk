
package mx.unison.pong;

import java.awt.geom.Ellipse2D;
import java.util.Iterator;

public class CollisionManager {

    public static boolean checkBlockCollision(
            Ellipse2D ball,
            BlockManager manager) {

        Iterator<Block> iterator =
                manager.getBlocks().iterator();

        while (iterator.hasNext()) {

            Block block = iterator.next();

            if (ball.intersects(block.getBounds())) {

                iterator.remove();

                return true;
            }
        }

        return false;
    }
}
