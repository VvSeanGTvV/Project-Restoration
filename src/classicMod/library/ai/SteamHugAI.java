package classicMod.library.ai;

import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.Nullable;
import classicMod.content.ClassicBlocks;
import mindustry.Vars;
import mindustry.entities.units.AIController;
import mindustry.world.Tile;
import mindustry.world.meta.Attribute;

public class SteamHugAI extends AIController {

    @Nullable
    public Tile getClosestVent() {
        Seq<Tile> avaliableVents = PathfinderExtended.SteamVents;
        Tile vent = Geometry.findClosest(this.unit.x, this.unit.y, avaliableVents.removeAll(tile -> tile.build != null));
        return (vent != null && vent.build == null) ? vent : null;
    }

    @Override
    public void updateMovement() {
        Tile closestVent = getClosestVent();
        if (closestVent != null){
            pathfind(PathfinderExtended.fieldVent);
        } else {
            unit.kill();
        }
        faceMovement();
        Tile tile = unit.tileOn();
        if (tile != null && tile.floor().attributes.get(Attribute.steam) >= 1f && tile.build == null) {
            float steam = 0f;
            for (int dy = -1; dy < 2; dy++) {
                for (int dx = -1; dx < 2; dx++) {
                    Tile vents = Vars.world.tile(tile.x + dx, tile.y + dy);
                    if (vents != null && vents.build == null)
                        steam += vents.floor().attributes.get(Attribute.steam);
                }
            }
            if (steam >= 9f) {
                tile.setBlock(ClassicBlocks.cord, unit.team);
                unit.kill();
            }
        }
    }
}
