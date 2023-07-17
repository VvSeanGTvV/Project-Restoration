package classicMod.library.ai;

import arc.math.*;
import arc.util.*;
import classicMod.library.blocks.*;
import classicMod.library.blocks.DroneCenterNew.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;

public class EffectDroneAI extends AIController {

    @Override
    public void updateMovement() {
        if(!(unit instanceof BuildingTetherc tether) || tether.building() == null) return;
        if(!(tether.building().block instanceof DroneCenterNew block)) return;
        if(!(tether.building() instanceof DroneCenterNewBuild build)) return;

        target = build.target;
        if(target != null) {
            moveTo(target, build.target.hitSize / 1.8f + block.droneRange - 10f);

            if (unit.within(target, block.droneRange + build.target.hitSize) && unit.within(build, block.droneRange)) {
                build.target.apply(block.status, block.statusDuration);
            }
        }



        /*if(!(unit instanceof BuildingTetherc tether)) return;
        if(!(tether.building() instanceof DroneCenterNewBuild build)) return;
        target = Units.closest(unit.team(), unit.x(), unit.y(), block.droneRange, u -> !u.spawnedByCore && u.type != unit.type);
        if (unit.within(target, block.droneRange + build.target.hitSize)) {
            build.target.apply(block.status, block.statusDuration);
         } else {
            moveTo(build.target.hitSize / 1.8f + block.droneRange - 10f);
        }
        //moveTo(build.target.hitSize / 1.8f + block.droneRange - 10f);


        moveTo(build.target.hitSize / 1.8f + block.droneRange - 10f);

        //TODO what angle?

        unit.lookAt(target);
        //unit.angleTo(target);
        //unit.moveAt(TarVector, build.target.hitSize / 1.8f + droneRange - 10f);

        //TODO low power? status effects may not be the best way to do this...
            /*if(unit.within(target, droneRange + build.target.hitSize)){
                build.target.apply(status, statusDuration);
            }*/
    }

    protected void targetClosest() {
        Teamc newTarget = Units.closestTarget(unit.team(), unit.x(), unit.y(), Math.max(unit.range(), unit.type().range), u -> (unit.type().targetAir && u.isFlying()) || (unit.type().targetGround && !u.isFlying()));
        if (newTarget != null) {
            target = newTarget;
        }
    }
}