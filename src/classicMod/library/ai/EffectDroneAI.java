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
            unit.lookAt(target);
            moveTo(target, build.target.hitSize / 5f + block.droneRange - 10f);

            if (unit.within(target, block.droneRange + build.target.hitSize) && unit.within(build, block.droneRange)) {
                build.target.apply(block.status, block.statusDuration);
            }
        } else {
            moveTo(build, 5f); //Return back to Base
        }
    }
}