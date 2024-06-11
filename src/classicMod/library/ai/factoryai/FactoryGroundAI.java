package classicMod.library.ai.factoryai;

import arc.math.Mathf;
import classicMod.library.ai.RallyAI;
import classicMod.library.blocks.legacyBlocks.LegacyCommandCenter;
import classicMod.library.blocks.legacyBlocks.LegacyUnitFactory;
import mindustry.Vars;
import mindustry.ai.Pathfinder;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.BuildingTetherc;
import mindustry.world.Tile;

import static classicMod.content.ClassicVars.MaximumRangeCommand;
import static classicMod.content.ClassicVars.PublicState;
import static mindustry.Vars.tilesize;

public class FactoryGroundAI extends RallyAI {

    @Override
    public void updateMovement(){
        if(unit.team == Vars.state.rules.defaultTeam) {
            if (!(unit instanceof BuildingTetherc tether) || tether.building() == null) return;
            if (!(tether.building().block instanceof LegacyUnitFactory block)) return;
            if (!(tether.building() instanceof LegacyUnitFactory.LegacyUnitFactoryBuild build)) return;
        }

        if(PublicState == UnitState.attack) {
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
        if(PublicState == UnitState.rally){
            if(retarget()){
                NearbyCenter();

                if(target != null && !Units.invalidateTarget(target, unit.team, unit.x, unit.y)){
                    state = UnitState.attack;
                }

                //if(target == null) target = unit.closestEnemyCore();
            }
            if(target == null) {
                NearbyCenter();
                building = Units.closestBuilding(unit.team, unit.x, unit.y, MaximumRangeCommand, b -> (b instanceof LegacyCommandCenter.LegacyCommandCenterBuild) && b.isValid() && !(b.isNull()));
                if (building != null) {
                    /*if(!unit.within(building, unit.type.range * 0.5f)){
                        pathfind(Pathfinder.fieldCore);
                        target = building;
                    }*/
                    moveTo(building, 65f + Mathf.randomSeed(unit.id) * 100);
                }
            } else {
                state = UnitState.attack;
            }
        }
        if(target == null && state != PublicState && Vars.state.rules.defaultTeam == unit.team) state = PublicState;
    }
}
