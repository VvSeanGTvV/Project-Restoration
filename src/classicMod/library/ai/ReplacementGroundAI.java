package classicMod.library.ai;

import arc.math.*;
import classicMod.library.blocks.legacyBlocks.LegacyCommandCenter;
import mindustry.Vars;
import mindustry.ai.*;
import mindustry.entities.Units;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.*;

import static classicMod.content.ClassicVars.MaximumRangeCommand;
import static classicMod.content.ClassicVars.PublicState;
import static mindustry.Vars.*;

public class ReplacementGroundAI extends RallyAI {

    @Override
    public void updateMovement(){
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
                NearbyCenter();
                building = Units.closestBuilding(unit.team, unit.x, unit.y, MaximumRangeCommand, b -> (b instanceof LegacyCommandCenter.LegacyCommandCenterBuild) && b.isValid() && !(b.isNull()));
                if (building != null) {
                    if(!unit.within(building, unit.type.range * 0.5f)){
                        pathfind(Pathfinder.fieldCore);
                        target = building;
                    }
                    //circle(building, unit.type.range * 0.8f);
                    //moveTo(building, unit.type.range * 0.8f);
                    unit.lookAt(building);
                }
        }
        if(target == null && state != PublicState && Vars.state.rules.defaultTeam == unit.team) state = PublicState;
    }
}
