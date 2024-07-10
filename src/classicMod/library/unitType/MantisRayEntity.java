package classicMod.library.unitType;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.*;

public class MantisRayEntity {
    float lastRot = 0f;
    float lastRotEnd = 0f;
    float timer;

    public float getTimer(){
        this.timer += Time.delta / 20f;
        return this.timer;
    }

    public float setlastRot(Unit unit, float prog){
        this.lastRot = Mathf.slerpDelta(this.lastRot, unit.rotation, prog);
        return this.lastRot;
    }

    public float setlastRotEnd(Unit unit, float prog){
        this.lastRotEnd = Mathf.slerpDelta(this.lastRotEnd, unit.rotation, prog);
        return this.lastRot;
    }

    public static MantisRayEntity create() {
        return new MantisRayEntity() {
            @Override
            public float getTimer() {
                return super.getTimer();
            }

            @Override
            public float setlastRot(Unit unit, float prog) {
                return super.setlastRot(unit, prog);
            }

            @Override
            public float setlastRotEnd(Unit unit, float prog) {
                return super.setlastRotEnd(unit, prog);
            }
        };
    }
}
