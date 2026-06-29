package classicMod.library.ai;

import arc.util.*;
import classicMod.content.*;
import classicMod.library.blocks.neoplasiaBlocks.CausticHeart;
import mindustry.Vars;
import mindustry.world.Tile;
import mindustry.world.meta.Attribute;

public class SteamHugAI extends NeoplasmAIController {

    boolean stucked = false;

    @Nullable
    Tile targetDestination;

    @Override
    public void updateMovement() {
        Tile closestVent = getClosestVent();
        if (closestVent != null){
            targetDestination = closestVent;
            pathfind(PathfinderV2.fieldVent);
        } else {
            ignore = true;
            unit.kill();
        }
        faceMovement();
        Tile tile = unit.tileOn();
        if (tile != null && tile.floor().attributes.get(Attribute.steam) >= 1f && !(tile.build instanceof CausticHeart.HeartBuilding)) {
            ignore = true;
            tile.setBlock(RBlocks.cord, unit.team);
            unit.kill();
        }
    }

    @Override
    public void pathfind(int pathTarget) {
        stucked = false;

        Tile tile = unit.tileOn();
        if (tile == null) {
            stucked = true;
            return;
        }

        int costType = unit.type.flowfieldPathType;

        // 1. Get flowfield target (ThreatMap + PheromoneMap aware)
        PathfinderV2.Flowfield field =
                classicMod.content.RVars.pathfinderCustom.getField(unit.team, costType, pathTarget);

        Tile targetTile =
                classicMod.content.RVars.pathfinderCustom.getTargetTile(tile, field, true);

        // 2. If the pathfinder returns null or self, try danger‑edge escape
        if (targetTile == null || targetTile == tile) {
            targetTile = getEdgeEscapeTile();
        }

        // 3. If still null, we are stuck
        if (targetTile == null || targetTile == tile) {
            stucked = true;
            return;
        }

        // 4. Move toward target tile
        if (costType != 2 || targetTile.floor().isLiquid) {
            float angle = unit.angleTo(targetTile.worldx(), targetTile.worldy());
            unit.movePref(vec.trns(angle, unit.speed()));
        }
    }
}
