package classicMod.library.ai;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.units.AIController;

public class MantisRayAI extends AIController {
    float lastRot = 0f;
    float lastRotEnd = 0f;
    float timer;

    @Override
    public void updateUnit() {
        super.updateUnit();
        timer += Time.delta / 20f;
        lastRot = Mathf.slerpDelta(this.lastRot, unit.rotation, 0.35f);
        lastRotEnd = Mathf.slerpDelta(this.lastRotEnd, unit.rotation, 0.15f);
    }

    public float getLastRot() {
        return lastRot;
    }

    public float getTimer() {
        return timer;
    }

    public float getLastRotEnd() {
        return lastRotEnd;
    }
}
