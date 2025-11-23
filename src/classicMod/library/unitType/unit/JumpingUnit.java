package classicMod.library.unitType.unit;

import arc.math.Mathf;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import classicMod.library.ai.JumpingAI;
import mindustry.gen.*;

import static classicMod.library.ui.UIExtended.fdelta;


public class JumpingUnit extends MechUnit  {
    public float timing;
    public float timingY;
    public boolean stopMoving;
    public boolean hit;
    public float hitDelay;
    public float lastHealth;

    public float healPercent = 0f;
    public float healRange = 0f;

    public JumpingUnit() {
        super();
    }

    @Override
    public void write(Writes write) {
        write.bool(hit);
        write.f(hitDelay);
        write.f(lastHealth);
    }

    @Override
    public void read(Reads read) {
        hit = read.bool();
        hitDelay = read.f();
        lastHealth = read.f();
    }

    @Override
    public void update() {

        timing += 0.15f * Time.delta;
        if (Mathf.sin(timing) > 0f) {
            timingY -= 0.275f * Time.delta;
        }
        if (lastHealth != health) {
            hitDelay += fdelta(100f, 60f);
            if (hitDelay >= 2f) {
                hit = false;
                lastHealth = health;
                hitDelay = 0;
            }
        }
    }
}
