package classicMod.library.ai;

import classicMod.content.ClassicBlocks;
import classicMod.library.blocks.neoplasiaBlocks.CausticHeart;
import mindustry.Vars;
import mindustry.world.Tile;
import mindustry.world.meta.Attribute;

public class SteamHugAI extends NeoplasmAIController {

    boolean stucked = false;

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
        if (tile != null && tile.floor().attributes.get(Attribute.steam) >= 1f && !(tile.build instanceof CausticHeart.HeartBuilding)) {
            tile.setBlock(ClassicBlocks.cord, unit.team);
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
            if (tile != targetTile && (costType != 2 || targetTile.floor().isLiquid)) {
                this.unit.movePref(vec.trns(this.unit.angleTo(targetTile.worldx(), targetTile.worldy()), this.unit.speed()));
            } else {
                stucked = true;
            }
        }
    }
}
