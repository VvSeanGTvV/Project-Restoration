package classicMod.library;

import arc.math.Mathf;
import arc.math.geom.Vec2;

public class MathE {

    /**
     * In between two lines.
     * @param div between two point, creates a new point.
     * @param range random offset of two points, where the new point is created
     * @return a Vec2 of the point between the two Vec2 point in a 2D plane.
     **/
    public static Vec2 interpolate(Vec2 start, Vec2 end, float div, float range) {
        Vec2 between = ((end.sub(start).div(new Vec2(div,div))).add(start));
        return new Vec2(between.x + Mathf.range(range), between.y + Mathf.range(range));
    }
}
