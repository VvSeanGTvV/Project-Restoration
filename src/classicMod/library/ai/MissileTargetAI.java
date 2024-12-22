package classicMod.library.ai;

import arc.math.*;
import arc.math.geom.Position;
import arc.util.*;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.*;

public class MissileTargetAI extends AIController {

    float targetRot;
    public boolean canCollideAir, canCollideGround;

    public MissileTargetAI() {
    }

    protected void targetClosest(){
        Teamc newTarget = Units.closestTarget(unit.team(), unit.x(), unit.y(), Math.max(unit.range(), unit.type().range), u -> (unit.type().targetAir && u.isFlying()) || (unit.type().targetGround && !u.isFlying()));
        if(newTarget != null){
            target = newTarget;
        }
    }

    float timeDelta = 0;

    @Override
    public void updateUnit() {
        targetClosest();
        super.updateUnit();
    }

    float angleTo(Position other, Position self) {
        return Angles.angle(self.getX(), self.getY(), other.getX(), other.getY());
    }

    @Override
    public void updateVisuals() {
        if (this.unit.isFlying()) {
            this.unit.wobble();
            this.unit.lookAt(targetRot);
        }
    }

    @Override
    public void updateMovement() {
        unloadPayloads();
        Unit var3 = unit;
        float var10000;
        if (var3 instanceof TimedKillc) {
            TimedKillc t = (TimedKillc)var3;
            var10000 = t.time();
        } else {
            var10000 = 1000000.0F;
        }

        float time = var10000;
        timeDelta += Time.delta;
        targetRot = unit.rotation;
        if (timeDelta >= unit.type.homingDelay && target != null) {
            targetRot = angleTo(target, unit);
        }

        unit.moveAt(vec.trns(unit.rotation, unit.type.missileAccelTime <= 0.0F ? unit.speed() : Mathf.pow(Math.min(time / unit.type.missileAccelTime, 1.0F), 2.0F) * unit.speed()));
        Building build = unit.buildOn();

        if (target instanceof Unit targetUnit) {
            if (canCollideAir && unit.within(target, 1f) && targetUnit.isFlying()) {
                unit.kill();
            }
            if (canCollideGround && unit.within(target, 1f) && !targetUnit.isFlying()) {
                unit.kill();
            }
        }
        if (build != null && build.team != unit.team && (build == target || !build.block.underBullets)) {
            unit.kill();
        }

    }

    public boolean retarget() {
        return timer.get(0, 4.0F);
    }
}
