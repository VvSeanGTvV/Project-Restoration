package classicMod.library.ai;

import arc.util.*;
import classicMod.content.RBlocks;
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
            pathfind(PathfinderExtended.fieldVent);
        } else {

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
        int costType = this.unit.pathType();
        Tile tile = this.unit.tileOn();
        if (tile != null) {
            Tile targetTile = Vars.pathfinder.getTargetTile(tile, Vars.pathfinder.getField(this.unit.team, costType, pathTarget));
            Tile nearDanger = closestDanger(tile);


            if (nearDanger != null) {
                float dstance = nearDanger.dst(this.unit);
                Log.info(DodgeTile + "|" + nearDanger.dst(targetTile));
                if (dstance < 80f){
                    targetTile = getClosestTarget(30, nearDanger, targetTile);
                }
            }

            if (tile != targetTile && (costType != 2 || targetTile.floor().isLiquid)) {
                this.unit.movePref(vec.trns(this.unit.angleTo(targetTile.worldx(), targetTile.worldy()), this.unit.speed()));
            } else {
                stucked = true;
            }
        }
    }
}
