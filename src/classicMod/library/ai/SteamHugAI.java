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
            pathfind(PathfinderCustom.fieldVent);
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

        int costType = this.unit.type.flowfieldPathType;
        Tile tile = this.unit.tileOn();
        if (tile == null) return;

        // Try to find a normal path target avoiding DodgeTile
        Tile targetTile = RVars.pathfinderCustom.getTargetTileDodge(
                tile,
                RVars.pathfinderCustom.getField(this.unit.team, costType, pathTarget),
                DodgeTile
        );

        // Fallback logic if path is blocked or null
        if (targetTile == null || tile == targetTile) {
            // Try escaping to the edge of the danger zone
            targetTile = getEdgeEscapeTile();

            // Still stuck? Mark as stucked
            if (targetTile == null || tile == targetTile) {
                stucked = true;
                return;
            }
        }

        // Move toward target if valid
        if (costType != 2 || targetTile.floor().isLiquid) {
            this.unit.movePref(vec.trns(
                    this.unit.angleTo(targetTile.worldx(), targetTile.worldy()),
                    this.unit.speed()
            ));
        }
    }
}
