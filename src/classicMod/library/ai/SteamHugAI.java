package classicMod.library.ai;

import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.*;
import classicMod.content.ClassicBlocks;
import classicMod.library.blocks.neoplasiaBlocks.Heart;
import mindustry.Vars;
import mindustry.entities.units.AIController;
import mindustry.world.Tile;
import mindustry.world.meta.Attribute;

public class SteamHugAI extends AIController {

    @Nullable
    public Tile getClosestVent() {
        Seq<Tile> avaliableVents = PathfinderExtended.SteamVents.copy().removeAll(tile -> tile.build instanceof Heart.HeartBuilding);
        Tile vent = Geometry.findClosest(this.unit.x, this.unit.y, avaliableVents);
        return (vent != null && !(vent.build instanceof Heart.HeartBuilding)) ? vent : null;
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
        if (tile != null && tile.floor().attributes.get(Attribute.steam) >= 1f) {
            tile.setBlock(ClassicBlocks.cord, unit.team);
            unit.kill();
        }
    }
}
