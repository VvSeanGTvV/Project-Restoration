package classicMod.library.ai;

import arc.math.Mathf;
import arc.util.Time;
import classicMod.library.blocks.legacyBlocks.LegacyCommandCenter;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.gen.Teamc;
import mindustry.world.meta.BlockFlag;

import static classicMod.content.ClassicVars.*;

public class ReplacementFlyingAI extends RallyAI {
    @Override
    public void updateMovement() {
        if (state == UnitState.attack) {
            unloadPayloads();

            if (target != null && unit.hasWeapons()) {
                if (unit.type.circleTarget) {
                    circleAttack(120f);
                } else {
                    moveTo(target, unit.type.range * 0.8f);
                    unit.lookAt(target);
                }
            }

            if (target == null && Vars.state.rules.waves && unit.team == Vars.state.rules.defaultTeam) {
                moveTo(getClosestSpawner(), Vars.state.rules.dropZoneRadius + 130f);
            }
        }
        if (state == UnitState.rally) {
            if (retarget()) {
                NearbyCenter();

                if (target != null && !Units.invalidateTarget(target, unit.team, unit.x, unit.y)) {
                    state = UnitState.attack;
                }

                //if(target == null) target = unit.closestEnemyCore();
            }
            if (target == null) {
                NearbyCenter();
                building = Units.closestBuilding(unit.team, unit.x, unit.y, MaximumRangeCommand, b -> (b instanceof LegacyCommandCenter.LegacyCommandCenterBuild) && b.isValid() && !(b.isNull()));
                if (building != null) {
                    circleBlock(65f + Mathf.randomSeed(unit.id) * 100);
                }
            } else {
                moveTo(target, unit.type.range * 0.8f);
                unit.lookAt(target);
            }
        }
        //if (target == null && state != PublicState) state = PublicState;
    }

    @Override
    public Teamc findTarget(float x, float y, float range, boolean air, boolean ground) {
        var result = findMainTarget(x, y, range, air, ground);

        //if the main target is in range, use it, otherwise target whatever is closest
        return checkTarget(result, x, y, range) ? target(x, y, range, air, ground) : result;
    }

    @Override
    public Teamc findMainTarget(float x, float y, float range, boolean air, boolean ground) {
        var core = targetFlag(x, y, BlockFlag.core, true);

        if (core != null && Mathf.within(x, y, core.getX(), core.getY(), range)) {
            return core;
        }

        for (var flag : unit.type.targetFlags) {
            if (flag == null) {
                Teamc result = target(x, y, range, air, ground);
                if (result != null) return result;
            } else if (ground) {
                Teamc result = targetFlag(x, y, flag, true);
                if (result != null) return result;
            }
        }

        return core;
    }

    public void circleBlock(float circleLength) {
        circleBlock(circleLength, unit.type().speed);
    }

    public void circleBlock(float circleLength, float speed) {
        if (building == null) return;

        vec.set(building).sub(unit);

        if (vec.len() < circleLength) {
            vec.rotate((circleLength - vec.len()) / circleLength * 180f);
        }

        vec.setLength(speed * Time.delta);

        unit.moveAt(vec);
    }
}
