package classicMod.library.neoplasm;

import arc.math.Mathf;
import mindustry.Vars;
import mindustry.world.Tile;

public class PheromoneMap {

    public static float[] pheromone;
    public static int width, height;

    public static void init() {
        width = Vars.world.width();
        height = Vars.world.height();
        pheromone = new float[width * height];
    }

    public static void deposit(Tile tile, float amount) {
        if (pheromone == null) init();
        int idx = tile.array();
        pheromone[idx] = Mathf.clamp(pheromone[idx] + amount, 0f, 20f);
    }

    public static float get(Tile tile) {
        return pheromone[tile.array()];
    }

    public static void decay(float rate) {
        if (pheromone == null) return;
        for (int i = 0; i < pheromone.length; i++) {
            pheromone[i] = Mathf.clamp(pheromone[i] - rate, 0f, 20f);
        }
    }
}