package classicMod.library.neoplasm;

import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.type.Item;
import mindustry.world.*;
import mindustry.content.*;
import mindustry.Vars;

public class CordHeuristics {

    public static boolean shouldBuildCloseDefense(Building b) {
        return Units.closestEnemy(b.team, b.x, b.y, 30f, u -> u.type.killable) != null;
    }

    public static boolean shouldBuildMidDefense(Building b) {
        return Units.closestEnemy(b.team, b.x, b.y, 120f, u -> u.type.killable) != null;
    }

    public static boolean shouldBuildLongDefense(Building b) {
        return Units.closestEnemy(b.team, b.x, b.y, 240f, u -> u.type.killable) != null;
    }

    public static boolean shouldBuildArtillery(Building b) {
        return Units.closestEnemy(b.team, b.x, b.y, 640f, u -> u.type.killable) != null;
    }

    public static boolean shouldSpawnUnit(Building b, Item item, int countNearby) {
        if (countNearby < 3) return false;
        if (Units.closestEnemy(b.team, b.x, b.y, 200f, u -> u.type.killable) == null) return false;
        return true;
    }

    public static boolean isSafe(Tile tile) {
        return ThreatMap.get(tile) < 20;
    }

    public static boolean isDangerous(Tile tile) {
        return ThreatMap.get(tile) > 80;
    }
}

