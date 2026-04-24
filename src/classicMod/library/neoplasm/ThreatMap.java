package classicMod.library.neoplasm;

import arc.struct.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.content.*;
import mindustry.Vars;

public class ThreatMap {

    public static short[] threat;
    public static int width, height;

    public static void init() {
        width = Vars.world.width();
        height = Vars.world.height();
        threat = new short[width * height];
    }

    public static void update() {
        if (threat == null) init();
        java.util.Arrays.fill(threat, (short)0);

        // enemy units
        Groups.unit.each(other -> {
            Groups.unit.each(u -> {
                if (u.team == other.team) return;
                int tx = u.tileX();
                int ty = u.tileY();
                addThreat(tx, ty, 40, 4);
            });
        });

        // enemy turrets
        Groups.build.each(other -> {
            Groups.build.each(u -> {
                if (!(u instanceof mindustry.world.blocks.defense.turrets.Turret.TurretBuild turret)) return;
                if (u.team == other.team) return;
                addThreat(turret.tileX(), turret.tileY(), (int)turret.range(), 12);
            });
        });
    }

    private static void addThreat(int x, int y, int radius, int amount) {
        int r2 = radius * radius;
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                if (dx*dx + dy*dy > r2) continue;
                Tile t = Vars.world.tile(x + dx, y + dy);
                if (t == null) continue;
                int idx = t.array();
                threat[idx] = (short)Math.min(Short.MAX_VALUE, threat[idx] + amount);
            }
        }
    }

    public static short get(Tile t) {
        return threat[t.array()];
    }
}
