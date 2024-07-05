package classicMod.library.ai;

import classicMod.library.blocks.DroneCenterNew;
import classicMod.library.blocks.DroneCenterNew.DroneCenterNewBuild;
import mindustry.entities.units.AIController;
import mindustry.gen.BuildingTetherc;

public class EffectDroneAI extends AIController {

    public boolean nullify = false;

    @Override
    public void updateMovement() {
        if (!(unit instanceof BuildingTetherc tether) || tether.building() == null) return;
        if (!(tether.building().block instanceof DroneCenterNew block)) return;
        if (!(tether.building() instanceof DroneCenterNewBuild build)) return;
        if (nullify) unit.remove();

        if (unit != null) {
            target = build.target;
            if (target != null) {
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

    public void Nullify(boolean yes) {
        nullify = yes;
    }
}