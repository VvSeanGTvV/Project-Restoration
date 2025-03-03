package classicMod.library.ai;

import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.*;
import classicMod.content.RUnitTypes;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.UnitPayload;

import static arc.util.Time.delta;

public class HelperBlobAI extends NeoplasmAIController {

    int stage;
    float timer = 0;

    Seq<Unit> queue = new Seq<>();

    @Override
    public void updateUnit() {
        stage = 0;
        if(unit instanceof PayloadUnit payloadUnit){
            for (var payload : payloadUnit.payloads()){
                if (payload instanceof UnitPayload unitPayload) {
                    if (unitPayload.content() == RUnitTypes.blob) stage = 1;
                }
            }
        }
        if (stage == 0) stage = 2;
        super.updateUnit();
    }

    @Nullable
    public Tile getEmptyLand(int range) {

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
        return Geometry.findClosest(this.unit.x, this.unit.y, avaliableLand);
    }

    @Override
    public void updateMovement() {
        Unit blob = Units.closest(unit.team, unit.x, unit.y, u -> u.controller() instanceof SteamHugAI hug && hug.stucked);
        Tile targetTile = getClosestVent(true);
        if(unit instanceof PayloadUnit payloadUnit && timer <= 0) {
            int range = 160;
            if (stage == 2 && blob != null){
                unit.movePref(vec.trns(this.unit.angleTo(blob.x(), blob.y()), this.unit.speed()));
                if (unit.within(blob, unit.hitSize)) {
                    payloadUnit.pickup(blob);
                }
            }
            if (stage == 1 && targetTile != null){

                if (!unit.within(targetTile, range)) unit.movePref(vec.trns(this.unit.angleTo(targetTile.worldx(), targetTile.worldy()), this.unit.speed()));
                else stage = 3;
                //if (unit.within(vent, unit.hitSize)) payloadUnit.pickup(vent);
            }
            if (stage == 3 && payloadUnit.payloads.size > 0 && payloadUnit.payloads.first() instanceof UnitPayload blobPayload){
                if (unit.tileOn() != null
                        && unit.tileOn().block() == Blocks.air
                        && !unit.tileOn().floor().isLiquid
                ){
                    payloadUnit.dropLastPayload();
                    timer = 10f;
                } else {
                    targetTile = getEmptyLand(range);
                    if (targetTile != null) stage = 4;
                }
            }
            if (stage == 4 && targetTile != null){
                if (!unit.within(targetTile, unit.hitSize)) unit.movePref(vec.trns(this.unit.angleTo(targetTile.worldx(), targetTile.worldy()), this.unit.speed()));
                else stage = 3;
                //if (unit.within(vent, unit.hitSize)) payloadUnit.pickup(vent);
            }
            //payloadUnit.addPayload(new UnitPayload(blobNear));
        } else {
            timer -= delta;
        }
        super.updateMovement();
    }
}
