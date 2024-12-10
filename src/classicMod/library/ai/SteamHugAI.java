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
        Tile vent = Geometry.findFurthest(this.unit.x, this.unit.y, PathfinderExtended.SteamVents);
        return (vent != null && vent.build == null) ? vent : null;
    }

    @Override
    public void pathfind(int pathTarget) {
        int costType = this.unit.pathType();
        Tile tile = this.unit.tileOn();
        if (tile != null) {
            Tile targetTile = Vars.pathfinder.getTargetTile(tile, Vars.pathfinder.getField(this.unit.team, costType, pathTarget));
            if (tile != targetTile && (costType != 2 || targetTile.floor().isLiquid)) {
                this.unit.movePref(vec.trns(this.unit.angleTo(targetTile.worldx(), targetTile.worldy()), this.unit.speed()));
            }
        }
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
