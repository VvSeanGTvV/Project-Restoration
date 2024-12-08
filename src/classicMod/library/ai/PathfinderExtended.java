package classicMod.library.ai;

import arc.struct.*;
import mindustry.Vars;
import mindustry.ai.Pathfinder;
import mindustry.world.Tile;
import mindustry.world.meta.*;

import java.util.Iterator;

public class PathfinderExtended extends Pathfinder {
    public static final int fieldVent = 1;

    Seq<Tile> SteamVent = new Seq<>();

    public static class SteamVents extends Flowfield {
        public SteamVents() {
        }

        protected void getPositions(IntSeq out) {

            for (Tile tile : Vars.world.tiles) {
                if (tile.floor().attributes.get(Attribute.steam) <= 0f) continue;
                float steam = 0f;
                for (int dy = -1; dy < 2; dy++) {
                    for (int dx = -1; dx < 2; dx++) {
                        Tile vents = Vars.world.tile(tile.x + dx, tile.y + dy);
                        if (vents != null && vents.build == null)
                            steam += vents.floor().attributes.get(Attribute.steam);
                    }
                }
                if (steam >= 9f) out.add(tile.array());
            }
        }
    }

    public static void addonFieldTypes(){
        fieldTypes.add(SteamVents::new);
    }
}
