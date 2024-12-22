package classicMod.library.ai;

import arc.math.Mathf;
import arc.math.geom.*;
import arc.util.Time;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.*;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.state;

public class SuicideBomberAI extends AIController {

    @Override
    public void updateUnit(){
        if(Units.invalidateTarget(target, unit.team, unit.x, unit.y, Float.MAX_VALUE)){
            target = null;
        }

        if(retarget()){
            target = target(unit.x, unit.y, unit.range(), unit.type.targetAir, unit.type.targetGround);
        }

        Building core = unit.closestEnemyCore();

        boolean rotate = false, shoot = false;

        if(target == null){
            target = core;
        }

        if (!Units.invalidateTarget(target, unit, unit.range()) && unit.hasWeapons()){
            boolean move = true;
            rotate = true;
            shoot = unit.within(target, unit.type.weapons.first().bullet.range +
                    (target instanceof Building b ? b.block.size * Vars.tilesize / 2f : ((Hitboxc)target).hitSize() / 2f));

            //stop moving toward the drop zone if applicable
            if(core == null && state.rules.waves && unit.team == state.rules.defaultTeam){
                Tile spawner = getClosestSpawner();
                if(spawner != null && unit.within(spawner, state.rules.dropZoneRadius + 120f)){
                    move = false;
                }
            }

            if(move){
                unit.movePref(vec.set(target).sub(unit).limit(unit.speed()));
            }
        }

        unit.controlWeapons(rotate, shoot);

        faceTarget();
    }

    @Override
    public Teamc target(float x, float y, float range, boolean air, boolean ground){
        return Units.closestTarget(unit.team, x, y, range, u -> u.checkTarget(air, ground), t -> ground &&
                !(t.block instanceof Conveyor || t.block instanceof Conduit)); //do not target conveyors/conduits
    }
}
