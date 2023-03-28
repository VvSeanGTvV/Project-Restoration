package classicMod.library.ai;

import arc.math.*;
import arc.util.*;
import classicMod.library.blocks.*;
import classicMod.library.blocks.DroneCenterNew.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;

public class EffectDroneAI extends AIController {
    protected DroneCenterNewBuild build;
    protected DroneCenterNew block;

    @Override
    public void updateMovement() {
        //if(!(unit instanceof BuildingTetherc tether)) return;
        //if(!(tether.building() instanceof DroneCenterNewBuild build)) return;
        //if(build.target == null) unit.remove(); //TODO fix the ai because it is ded :I
        if (build.target != null) {
            target = build.target;
            if (unit.within(target, block.droneRange + build.target.hitSize)) {
                build.target.apply(block.status, block.statusDuration);
            } else {
                moveTo(build.target.hitSize / 1.8f + block.droneRange - 10f);
            }
        }

        //TODO what angle?

        unit.lookAt(target);
        //unit.angleTo(target);
        //unit.moveAt(TarVector, build.target.hitSize / 1.8f + droneRange - 10f);

        //TODO low power? status effects may not be the best way to do this...
            /*if(unit.within(target, droneRange + build.target.hitSize)){
                build.target.apply(status, statusDuration);
            }*/
    }

    protected void moveTo(float circleLength) {
        if (target == null) return;

        vec.set(target).sub(unit);

        float length = circleLength <= 0.001f ? 1f : Mathf.clamp((unit.dst(target) - circleLength) / 100f, -1f, 1f);

        vec.setLength(unit.type().speed * Time.delta * length);
        if (length < -0.5f) {
            vec.rotate(180f);
        } else if (length < 0) {
            vec.setZero();
        }

        unit.moveAt(vec);
    }

    protected void targetClosest() {
        Teamc newTarget = Units.closestTarget(unit.team(), unit.x(), unit.y(), Math.max(unit.range(), unit.type().range), u -> (unit.type().targetAir && u.isFlying()) || (unit.type().targetGround && !u.isFlying()));
        if (newTarget != null) {
            target = newTarget;
        }
    }
}
