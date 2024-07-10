package classicMod.library.ai;

import arc.math.Mathf;
import arc.util.Time;
import classicMod.library.unitType.MantisRayType;
import mindustry.entities.units.AIController;

public class MantisRayAI extends AIController {
    @Override
    public void updateVisuals() {
        super.updateVisuals();
        if(unit.type instanceof MantisRayType type){
            type.timer += Time.delta / 20f;
            type.lastRot = Mathf.slerpDelta(type.lastRot, unit.rotation, 0.35f);
            type.lastRotEnd = Mathf.slerpDelta(type.lastRotEnd, unit.rotation, 0.15f);
        }
    }
}
