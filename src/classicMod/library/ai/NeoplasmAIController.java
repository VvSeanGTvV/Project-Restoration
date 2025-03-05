package classicMod.library.ai;

import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.*;
import classicMod.library.blocks.neoplasiaBlocks.CausticHeart;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.Turret;

public class NeoplasmAIController extends AIController {

    public Seq<Tile> DodgeTile = new Seq<>();
    public Seq<Unit> groups = new Seq<>();

    public boolean ignore;

    @Override
    public void updateUnit() {
        Unit neo = Units.closest(unit.team, unit.x, unit.y, u -> u.controller() instanceof NeoplasmAIController && !groups.contains(u) && u != this.unit);
        if (neo != null) groups.add(neo);

        for (var neoplasm : groups){
            if (neoplasm == null) continue;
            if (neoplasm.tileOn() != null && neoplasm.dead){
                if (neoplasm.controller() instanceof NeoplasmAIController neoplasmAIController && !neoplasmAIController.ignore && !DodgeTile.contains(neoplasm.tileOn())) DodgeTile.add(neoplasm.tileOn());
                groups.remove(neoplasm);
            }
        }
        super.updateUnit();
    }

    @Nullable
    public Tile getClosestVent(boolean dontPlaceNearDangerous) {
        Seq<Tile> avaliableVents = PathfinderExtended.SteamVents.copy().removeAll(tile -> tile.build instanceof CausticHeart.HeartBuilding);
        Tile vent = Geometry.findClosest(this.unit.x, this.unit.y, avaliableVents);

        Building nearbyEnemyTile = Units.findEnemyTile(this.unit.team, vent.getX(), vent.getY(), 240f, building -> !building.dead);
        if (dontPlaceNearDangerous && nearbyEnemyTile != null) avaliableVents = avaliableVents.copy().removeAll(tile -> tile.dst(nearbyEnemyTile) <= 80f);
        vent = Geometry.findClosest(this.unit.x, this.unit.y, avaliableVents);
        return (vent != null && !(vent.build instanceof CausticHeart.HeartBuilding)) ? vent : null;
    }

    @Nullable
    public Tile getClosestVent() {
        return getClosestVent(false);
    }

    public Tile closestDanger(Tile tile){
        Tile lowestTile = null;
        float lowest = Float.MAX_VALUE;
        for (var danger : DodgeTile){
            if (danger.dst(tile) < lowest){
                lowest = danger.dst(tile);
                lowestTile = danger;
            }
        }
        return lowestTile;
    }

    public Tile getClosestTarget(int range, Tile closestDanger, Tile targetTile, Unit unit){
        int mid = Mathf.floor((float) range / 2);
        Seq<Tile> avaliableLand = new Seq<>();

        for (int y = -mid; y < range; y++){
            for (int x = -mid; x < range; x++){
                Tile tile = Vars.world.tile(unit.tileX() + x, unit.tileY() + y);
                if (
                        tile != null
                                && tile.block() == Blocks.air
                                && tile.floor() != null
                                && !tile.floor().isLiquid
                ) avaliableLand.add(tile);
            }
        }

        if (avaliableLand.size <= 0) return null;
        avaliableLand.removeAll(tile -> closestDanger.dst(tile) < 80);
        avaliableLand.removeAll(tile -> this.unit.dst(tile) < 10 && this.unit.dst(tile) > 100);
        avaliableLand.sort(tile -> tile.dst(targetTile));

        if (avaliableLand.size <= 0) return null;
        return avaliableLand.first();
    }

    public void routeAir(){
        Tile tile = this.unit.tileOn();
        Tile targetTile = target.tileOn();
        Tile nearDanger = closestDanger(tile);


        if (nearDanger != null && targetTile != null) {
            float dstance = nearDanger.dst(tile);
            if (dstance < 80f){
                targetTile = getClosestTarget(15, nearDanger, targetTile, unit);
            }
        }
        if (targetTile != null && tile != targetTile) unit.movePref(vec.set(targetTile).sub(unit).limit(unit.speed()));
    }

    @Override
    public void pathfind(int pathTarget) {
        int costType = this.unit.pathType();
        Tile tile = this.unit.tileOn();
        if (tile != null) {
            Tile targetTile = Vars.pathfinder.getTargetTile(tile, Vars.pathfinder.getField(this.unit.team, costType, pathTarget));
            Tile nearDanger = closestDanger(tile);


            if (nearDanger != null) {
                float dstance = nearDanger.dst(tile);
                if (dstance < 80f){
                    targetTile = getClosestTarget(15, nearDanger, targetTile, unit);
                    if (targetTile == null) DodgeTile.remove(nearDanger);
                }
            }

            if (targetTile != null && tile != targetTile && (costType != 2 || targetTile.floor().isLiquid)) {
                this.unit.movePref(vec.trns(this.unit.angleTo(targetTile.worldx(), targetTile.worldy()), this.unit.speed()));
            }
        }
    }
}
