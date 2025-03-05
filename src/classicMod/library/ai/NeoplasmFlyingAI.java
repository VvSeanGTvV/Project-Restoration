package classicMod.library.ai;

import arc.math.*;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;
import static mindustry.world.meta.BlockFlag.*;

public class NeoplasmFlyingAI extends NeoplasmAIController {
    final static Rand rand = new Rand();
    final static BlockFlag[] randomTargets = {core, storage, generator, launchPad, factory, repair, battery, reactor, drill};

    @Override
    public void updateMovement(){
        unloadPayloads();

        if(target != null && unit.hasWeapons()){
            if(unit.type.circleTarget){
                circleAttack(120f);
            }else{
                routeAir();
                //moveTo(target, unit.type.range * 0.8f);
                unit.lookAt(target);
            }
        }

        if(target == null && state.rules.waves && unit.team == state.rules.defaultTeam){
            moveTo(getClosestSpawner(), state.rules.dropZoneRadius + 130f);
        }
    }

    @Override
    public void circleAttack(float circleLength) {
        vec.set(this.target).sub(this.unit);
        float ang = this.unit.angleTo(this.target);
        float diff = Angles.angleDist(ang, this.unit.rotation());
        if (diff > 70.0F && vec.len() < circleLength) {
            vec.setAngle(this.unit.vel().angle());
        } else {
            vec.setAngle(Angles.moveToward(this.unit.vel().angle(), vec.angle(), 6.0F));
        }

        vec.setLength(this.unit.speed());
        routeAir();
    }

    @Override
    public Teamc targetFlag(float x, float y, BlockFlag flag, boolean enemy){
        return super.targetFlag(x, y, flag, enemy);
    }

    @Override
    public Teamc findTarget(float x, float y, float range, boolean air, boolean ground){
        var result = findMainTarget(x, y, range, air, ground);

        //if the main target is in range, use it, otherwise target whatever is closest
        return checkTarget(result, x, y, range) ? target(x, y, range, air, ground) : result;
    }

    @Override
    public Teamc findMainTarget(float x, float y, float range, boolean air, boolean ground){
        var core = targetFlag(x, y, BlockFlag.core, true);

        if(core != null && Mathf.within(x, y, core.getX(), core.getY(), range)){
            return core;
        }

        for(var flag : unit.type.targetFlags){
            if(flag == null){
                Teamc result = target(x, y, range, air, ground);
                if(result != null) return result;
            }else if(ground){
                Teamc result = targetFlag(x, y, flag, true);
                if(result != null) return result;
            }
        }

        return core;
    }
}
