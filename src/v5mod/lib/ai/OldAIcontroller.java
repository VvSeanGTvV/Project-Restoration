package v5mod.lib.ai;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.meta.*;

import static mindustry.Vars.indexer;

public class OldAIcontroller extends AIController {
    protected static final Vec2 vec = new Vec2();
    protected static final int timerTarget = 0;
    //protected Unit unit;
    protected Teamc target;
    protected Interval timer = new Interval(4);

    {
        timer.reset(0, Mathf.random(40f));
    }

    @Override
    public boolean retarget(){
        return timer.get(timerTarget, 30);
    }

    protected void targetClosestEnemyFlag(BlockFlag flag){
        Building target = Geometry.findClosest(unit.x(), unit.y(), indexer.getEnemy(unit.team(), flag));
        if(target != null) this.target = target;
    }

    protected void targetClosest(){
        //TODO optimize!
        Teamc newTarget = Units.closestTarget(unit.team(), unit.x(), unit.y(), Math.max(unit.range(), unit.type().range), u -> (unit.type().targetAir && u.isFlying()) || (unit.type().targetGround && !u.isFlying()));
        if(newTarget != null){
            target = newTarget;
        }
    }
}
