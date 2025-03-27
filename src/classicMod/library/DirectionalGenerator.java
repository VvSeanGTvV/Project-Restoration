package classicMod.library;

import arc.math.geom.Point2;
import arc.struct.Seq;

import java.util.*;

public class DirectionalGenerator {
    /**
     * Generates all directions within a square of given size (Chebyshev distance)
     * @param size The radius of the square (size=1 gives 8 directions, size=2 gives 24, etc.)
     * @param includeCenter Whether to include the center point (0,0)
     * @return Array of Point2 objects representing all directions
     */
    public static Point2[] generateDirections(int size, boolean includeCenter) {
        List<Point2> directions = new ArrayList<>();

        for (int x = -size; x <= size; x++) {
            for (int y = -size; y <= size; y++) {
                // Skip center if not included
                if (!includeCenter && x == 0 && y == 0) {
                    continue;
                }
                directions.add(new Point2(x, y));
            }
        }

        return directions.toArray(new Point2[0]);
    }
}
