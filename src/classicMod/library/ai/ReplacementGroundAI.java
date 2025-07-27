package classicMod.library.ai;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Log;
import classicMod.library.blocks.legacyBlocks.LegacyCommandCenter;
import mindustry.Vars;
import mindustry.ai.Pathfinder;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.world.Tile;

import static classicMod.content.RVars.*;
import static mindustry.Vars.tilesize;

public class ReplacementGroundAI extends RallyAI {

    @Override
    public void updateMovement(){
        if(state == UnitState.attack) {
            Building core = unit.closestEnemyCore();

            if (core != null && unit.within(core, unit.range() / 1.3f + core.block.size * tilesize / 2f)) {
                target = core;
                for (var mount : unit.mounts) {
                    if (mount.weapon.controllable && mount.weapon.bullet.collidesGround) {
                        mount.target = core;
                    }
                }
            }

            if ((core == null || !unit.within(core, unit.type.range * 0.5f))) {
                boolean move = true;

                if (Vars.state.rules.waves && unit.team == Vars.state.rules.defaultTeam) {
                    Tile spawner = getClosestSpawner();
                    if (spawner != null && unit.within(spawner, Vars.state.rules.dropZoneRadius + 120f)) move = false;
                    if (spawner == null && core == null) move = false;
                }

                //no reason to move if there's nothing there
                if (core == null && (!Vars.state.rules.waves || getClosestSpawner() == null)) {
                    move = false;
                }

                if (move) pathfind(Pathfinder.fieldCore);
            }

            if (unit.type.canBoost && unit.elevation > 0.001f && !unit.onSolid()) {
                unit.elevation = Mathf.approachDelta(unit.elevation, 0f, unit.type.riseSpeed);
            }

            faceTarget();
        }
        if(state == UnitState.rally){
            if(retarget()){
                NearbyCenter();

                if(target != null && !Units.invalidateTarget(target, unit.team, unit.x, unit.y)){
                    state = UnitState.attack;
                }

                //if(target == null) target = unit.closestEnemyCore();
            }
            if(target == null) {
                NearbyCenter();
                building = Units.closestBuilding(unit.team, unit.x, unit.y, MaximumRangeCommand, b -> (b instanceof LegacyCommandCenter.LegacyCommandCenterBuild lccb) && b.isValid() && RallyAI.UnitState.all[lccb.config()] == this.state);
                if (building != null) {
                    if(!unit.within(building, unit.type.range * 0.5f)){
                        pathfind(PathfinderExtended.fieldCommandCenter);
                        unit.lookAt(building);
                        target = building;
                    }
                    //moveTo(building, 65f + Mathf.randomSeed(unit.id) * 100, 25f, true, Vec2.ZERO, true);
                }
            } else {
                state = UnitState.attack;
            }
        }
        if(target == null && state != defaultState) state = defaultState;
    }
}
