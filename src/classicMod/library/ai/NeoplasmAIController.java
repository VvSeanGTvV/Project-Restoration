package classicMod.library.ai;

import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.*;
import classicMod.library.blocks.neoplasiaBlocks.CausticHeart;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Unit;
import mindustry.world.Tile;

public class NeoplasmAIController extends AIController {

    public Seq<Tile> DodgeTile = new Seq<>(); // TODO thus

    public boolean update;

    @Override
    public void updateUnit() {
        
        Unit uNeo = Units.closest(unit.team, unit.x, unit.y, u -> u.controller() instanceof NeoplasmAIController);

        super.updateUnit();
    }

    @Nullable
    public Tile getClosestVent() {
        Seq<Tile> avaliableVents = PathfinderExtended.SteamVents.copy().removeAll(tile -> tile.build instanceof CausticHeart.HeartBuilding);
        Tile vent = Geometry.findClosest(this.unit.x, this.unit.y, avaliableVents);
        return (vent != null && !(vent.build instanceof CausticHeart.HeartBuilding)) ? vent : null;
    }

    public Tile getClosestTarget(int range){
        int r = (range % 2 == 1) ? range + 1 : range;
        int mid = r/2;
        Seq<Tile> avaliableLand = new Seq<>();

        for (int y = -mid; y < r; y++){
            for (int x = -mid; x < r; x++){
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
        avaliableLand.sort(tile -> tile.dst(target));

        return avaliableLand.first();
    }

    @Override
    public void pathfind(int pathTarget) {
        int costType = this.unit.pathType();
        Tile tile = this.unit.tileOn();
        if (tile != null) {
            Tile targetTile = Vars.pathfinder.getTargetTile(tile, Vars.pathfinder.getField(this.unit.team, costType, pathTarget));
            if (tile != targetTile && (costType != 2 || targetTile.floor().isLiquid)) {
                if (DodgeTile.contains(targetTile)) targetTile = getClosestTarget(240);
                this.unit.movePref(vec.trns(this.unit.angleTo(targetTile.worldx(), targetTile.worldy()), this.unit.speed()));
            }
        }
    }
}
