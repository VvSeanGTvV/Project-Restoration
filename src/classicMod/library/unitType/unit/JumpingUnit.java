package classicMod.library.unitType.unit;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Log;
import arc.util.Structs;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import classicMod.content.RFx;
import classicMod.library.ai.JumpingAI;
import mindustry.entities.Effect;
import mindustry.gen.*;

import java.util.Objects;

import static classicMod.content.RVars.idcMap;
import static classicMod.library.ui.UIExtended.fdelta;


public class JumpingUnit extends MechUnit implements Jumperc {
    public float timing;
    public float timingY;
    public boolean hit;
    public float hitDelay;
    public float lastHealth;

    public Effect StompEffect = RFx.dynamicWaveBig;
    public Effect StompExplosionEffect = RFx.dynamicSmallBomb;
    public Color StompColor = Color.valueOf("ffd27e");

    public JumpingUnit() {
        super();
    }

    public static JumpingUnit create() {
        return new JumpingUnit();
    }

    @Override
    public void write(Writes write) {
        super.write(write);
        write.bool(hit);
        write.f(hitDelay);
        write.f(lastHealth);
    }

    @Override
    public void read(Reads read) {
        super.read(read);
        hit = read.bool();
        hitDelay = read.f();
        lastHealth = read.f();
    }

    @Override
    public int classId() {
        return 1;
    }

    @Override
    public <T extends Entityc> T self() {
        return (T)this;
    }

    @Override
    public <T> T as() {
        return (T)this;
    }

    @Override
    public void update() {
        super.update();
        hit = (lastHealth != health);
        if (hit){
            timing = 2f;
            timingY = 0.5f;

            hitDelay += Time.delta;
            if (hitDelay >= 60f) {
                hit = false;
                lastHealth = health;
                hitDelay = 0;
            }
        } else {
            timing += 0.15f * Time.delta;
            if (Mathf.sin(timing) > 0f) {
                timingY -= 0.275f * Time.delta;
            }

            var sine = Mathf.sin(timing);
            if (sine < -0.85f) {
                timing = 2f;
                timingY = 0.5f;
            }
        }

    }

    @Override
    public Effect stompEffect() {
        return this.StompEffect;
    }

    @Override
    public void stompEffect(Effect stomp) {
        this.StompEffect = stomp;
    }

    @Override
    public Effect stompEffectExplosion() {
        return this.StompExplosionEffect;
    }

    @Override
    public void stompEffectExplosion(Effect explosion) {
        this.StompExplosionEffect = explosion;
    }

    @Override
    public Color stompColor() {
        return this.StompColor;
    }

    @Override
    public void stompColor(Color stompColor) {
        this.StompColor = stompColor;
    }

    @Override
    public boolean stompExplosion() {
        return false;
    }

    @Override
    public float timing() {
        return this.timing;
    }

    @Override
    public float timingY() {
        return this.timingY;
    }

    @Override
    public boolean hit() {
        return this.hit;
    }
    @Override
    public void hit(boolean hit) {
        this.hit = hit;
    }

    @Override
    public float hitDelay() {
        return this.hitDelay;
    }

    @Override
    public float lastHealth() {
        return this.lastHealth;
    }
}
