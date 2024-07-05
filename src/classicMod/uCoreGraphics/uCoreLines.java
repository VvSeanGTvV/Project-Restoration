package classicMod.uCoreGraphics;

import arc.graphics.g2d.Lines;
import arc.math.geom.Vec2;
//re implemenation of uCore Graphics, can onky be used by this at the moment...
public class uCoreLines extends Lines {
    private static final Vec2 vector = new Vec2();
    public static void swirl(float x, float y, float radius, float finion){
        swirl(x, y, radius, finion, 0f);
    }

    /**
     * Creates a Swirl like effect from uCore's Graphics.
     * @param x X position.
     * @param y Y position.
     * @param radius How large is it.
     */
    public static void swirl(float x, float y, float radius, float finion, float angle){
        int sides = 50;
        int max = (int) (sides * (finion + 0.001f));
        vector.set(0, 0);

        for(int i = 0; i < max; i++){
            vector.set(radius, 0).setAngle(360f / sides * i + angle);
            float x1 = vector.x;
            float y1 = vector.y;

            vector.set(radius, 0).setAngle(360f / sides * (i + 1) + angle);

            line(x1 + x, y1 + y, vector.x + x, vector.y + y);
        }
    }
}
