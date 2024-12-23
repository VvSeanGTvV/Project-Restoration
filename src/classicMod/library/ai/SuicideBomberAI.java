package classicMod.library.ai;

import arc.math.Mathf;
import arc.math.geom.*;
import arc.util.*;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.*;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class SuicideBomberAI extends AIController {

    @Override
    public void updateUnit(){
        if(Units.invalidateTarget(target, unit.team, unit.x, unit.y, Float.MAX_VALUE)){
            target = null;
        }

        if(retarget()){
            targetClosestEnemyFlag(BlockFlag.turret);
            //target = target(unit.x, unit.y, unit.range(), unit.type.targetAir, unit.type.targetGround);
        }

        Building core = unit.closestEnemyCore();
        boolean rotate = false, shoot = false;
        if(target == null) {
            target = core;
        }
        if (target != null){
            boolean move = true;
            rotate = true;
            shoot = (unit.tileOn() != null && unit.tileOn().build != null && unit.tileOn().build.team != unit.team);

            //stop moving toward the drop zone if applicable
            if(core == null && state.rules.waves && unit.team == state.rules.defaultTeam){
                Tile spawner = getClosestSpawner();
                if(spawner != null && unit.within(spawner, state.rules.dropZoneRadius + 120f)){
                    move = false;
                }
            }

            unit.movePref(vec.set(target).sub(unit).limit(unit.speed()));
        }

        unit.controlWeapons(rotate, shoot);

        faceTarget();
    }

    protected void targetClosestEnemyFlag(BlockFlag flag){
        Teamc target = Geometry.findClosest(unit.x(), unit.y(), indexer.getEnemy(unit.team(), flag));
        if(target != null) this.target = target;
    }

    @Override
    public Teamc target(float x, float y, float range, boolean air, boolean ground){
        return Units.closestTarget(unit.team, x, y, range, u -> u.checkTarget(air, ground), t -> ground &&
                !(t.block instanceof Conveyor || t.block instanceof Conduit)); //do not target conveyors/conduits
    }
}
