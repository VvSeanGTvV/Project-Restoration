package classicMod.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.graphics.*;

import static mindustry.graphics.Pal.*;

public class ExtendedFx extends Fx {
    public static final Effect
            //v5 Effects
            shellEjectSmall = new Effect(30f, 400f, e -> {
        Draw.color(lightOrange, Color.lightGray, Pal.lightishGray, e.fin());
        float rot = Math.abs(e.rotation) + 90f;

        int i = Mathf.sign(e.rotation);

        float len = (2f + e.finpow() * 6f) * i;
        float lr = rot + e.fin() * 30f * i;
        Fill.rect(e.x + Angles.trnsx(lr, len) + Mathf.randomSeedRange(e.id + i + 7, 3f * e.fin()),
                e.y + Angles.trnsy(lr, len) + Mathf.randomSeedRange(e.id + i + 8, 3f * e.fin()),
                1f, 2f, rot + e.fin() * 50f * i);

    }),

    shellEjectMedium = new Effect(34f, 400f, e -> {
        Draw.color(lightOrange, Color.lightGray, Pal.lightishGray, e.fin());
        float rot = e.rotation + 90f;
        for(int i : Mathf.signs){
            float len = (2f + e.finpow() * 10f) * i;
            float lr = rot + e.fin() * 20f * i;
            Draw.rect(Core.atlas.find("restored-mind-casing-old"),
                    e.x + Angles.trnsx(lr, len) + Mathf.randomSeedRange(e.id + i + 7, 3f * e.fin()),
                    e.y + Angles.trnsy(lr, len) + Mathf.randomSeedRange(e.id + i + 8, 3f * e.fin()),
                    2f, 3f, rot);
        }

        Draw.color(Color.lightGray, Color.gray, e.fin());

        for(int i : Mathf.signs){
            float ex = e.x, ey = e.y, fout = e.fout();
            Angles.randLenVectors(e.id, 4, 1f + e.finpow() * 11f, e.rotation + 90f * i, 20f, (x, y) -> {
                Fill.circle(ex + x, ey + y, fout * 1.5f);
            });
        }

    }),

    shellEjectBig = new Effect(22f, 400f, e -> {
        Draw.color(lightOrange, Color.lightGray, Pal.lightishGray, e.fin());
        float rot = e.rotation + 90f;
        for(int i : Mathf.signs){
            float len = (4f + e.finpow() * 8f) * i;
            float lr = rot + Mathf.randomSeedRange(e.id + i + 6, 20f * e.fin()) * i;
            Draw.rect(Core.atlas.find("restored-mind-casingOld"),
                    e.x + Angles.trnsx(lr, len) + Mathf.randomSeedRange(e.id + i + 7, 3f * e.fin()),
                    e.y + Angles.trnsy(lr, len) + Mathf.randomSeedRange(e.id + i + 8, 3f * e.fin()),
                    2.5f, 4f,
                    rot + e.fin() * 30f * i + Mathf.randomSeedRange(e.id + i + 9, 40f * e.fin()));
        }

        Draw.color(Color.lightGray);

        for(int i : Mathf.signs){
            float ex = e.x, ey = e.y, fout = e.fout();
            Angles.randLenVectors(e.id, 4, -e.finpow() * 15f, e.rotation + 90f * i, 25f, (x, y) -> {
                Fill.circle(ex + x, ey + y, fout * 2f);
            });
        }

    }),

    //b41-b40 effects
    teleportActivate = new Effect(50, e -> {
        Draw.colorMul(e.color, 1.5f);

        e.scaled(8f, e2 -> {
            Lines.stroke(e2.fout() * 4f);
            Lines.circle(e2.x, e2.y, 4f + e2.fin() * 27f);
        });

        Lines.stroke(e.fout() * 2f);

        Angles.randLenVectors(e.id, 30, 4f + 40f * e.fin(), (x, y) -> {
            Lines.lineAngle(e.x + x, e.y + y, Mathf.atan2(x, y), e.fin() * 4f + 1f);
        });

        Draw.reset();
    }),

    teleport = new Effect(60, e -> {
        Draw.colorMul(e.color, 1.5f);
        Lines.stroke(e.fin() * 2f);
        Lines.circle(e.x, e.y, 7f + e.fout() * 8f);

        Angles.randLenVectors(e.id, 20, 6f + 20f * e.fout(), (x, y) -> {
            Lines.lineAngle(e.x + x, e.y + y, Mathf.atan2(x, y), e.fin() * 4f + 1f);
        });

        Draw.reset();
    }),

    teleportOut = new Effect(20, e -> {
        Draw.colorMul(e.color, 1.5f);
        Lines.stroke(e.fout() * 2f);
        Lines.circle(e.x, e.y, 7f + e.fin() * 8f);

        Angles.randLenVectors(e.id, 20, 4f + 20f * e.fin(), (x, y) -> {
            Lines.lineAngle(e.x + x, e.y + y, Mathf.atan2(x, y), e.fslope() * 4f + 1f);
        });

        Draw.reset();
    }),

    titanshot = new Effect(12f, e -> {
        Draw.color(Color.white, lightOrange, e.fin());
        Lines.stroke(e.fout()*7f);
        Lines.lineAngle(e.x, e.y, e.rotation, e.fout()*12f);
        Lines.stroke(e.fout()*4f);
        Lines.lineAngle(e.x, e.y, e.rotation, e.fout()*16f);
        Lines.stroke(e.fout()*2f);
        Lines.lineAngle(e.x, e.y, e.rotation, e.fout()*18f);
        Draw.reset();
    }),

    chainshot = new Effect(9f, e -> {
        Draw.color(Color.white, lightOrange, e.fin());
        Lines.stroke(e.fout()*4f);
        Lines.lineAngle(e.x, e.y, e.rotation, e.fout()*7f);
        Lines.stroke(e.fout()*2f);
        Lines.lineAngle(e.x, e.y, e.rotation, e.fout()*10f);
        Draw.reset();
    }),

    shockwaveSmall = new Effect(10f, e -> {
        Draw.color(Color.white, Color.lightGray, e.fin());
        Lines.stroke(e.fout()*2f + 0.1f);
        Lines.circle(e.x, e.y, e.fin()*15f);
        Draw.reset();
    }),

    shellsmoke = new Effect(20, e -> {
        Angles.randLenVectors(e.id, 8, 3f + e.fin()*17f, (x, y)->{
            float size = 2f+e.fout()*5f;
            Draw.color(Color.lightGray, Color.darkGray, e.fin());
            Draw.rect("restored-mind-circle", e.x + x, e.y + y, size, size);
            Draw.reset();
        });
    }),

    fuelburn = new Effect(23, e -> {
        Angles.randLenVectors(e.id, 5, e.fin()*9f, (x, y)->{
            float len = e.fout()*4f;
            Draw.color(Color.lightGray, Color.gray, e.fin());
            //Draw.alpha(e.fout());
            Draw.rect("restored-mind-circle", e.x + x, e.y + y, len, len);
            Draw.reset();
        });
    }),

    spark = new Effect(10, e -> {
        Lines.stroke(1f);
        Draw.color(Color.white, Color.gray, e.fin());
        Lines.spikes(e.x, e.y, e.fin() * 5f, 2, 8);
        Draw.reset();
    }),

    purifystone = new Effect(10, e -> {
        Draw.color(Color.orange, Color.gray, e.fin());
        Lines.stroke(1f);
        Lines.spikes(e.x, e.y, e.fin() * 4f, 2, 6);
        Draw.reset();
    }),

    nuclearcloud = new Effect(90, 200f, e -> {
        Angles.randLenVectors(e.id, 10, e.finpow() * 90f, (x, y) -> {
            float size = e.fout() * 14f;
            Draw.color(Color.lime, Color.gray, e.fin());
            Draw.rect("restored-mind-circle", e.x + x, e.y + y, size, size);
            Draw.reset();
        });
    }),

    nuclearsmoke = new Effect(40, e -> {
        Angles.randLenVectors(e.id, 4, e.fin() * 13f, (x, y) -> {
            float size = e.fslope() * 4f;
            Draw.color(Color.lightGray, Color.gray, e.fin());
            Draw.rect("circle", e.x + x, e.y + y, size, size);
            Draw.reset();
        });
    }),

    explosion = new Effect(30, e -> {
        e.scaled(7, i -> {
            Lines.stroke(3f * i.fout());
            Lines.circle(e.x, e.y, 3f + i.fin() * 10f);
        });

        Draw.color(Color.gray);

        Angles.randLenVectors(e.id, 6, 2f + 19f * e.finpow(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 3f + 0.5f);
            Fill.circle(e.x + x / 2f, e.y + y / 2f, e.fout() * 1f);
        });

        Draw.color(lighterOrange, lightOrange, Color.gray, e.fin());
        Lines.stroke(1.5f * e.fout());

        Angles.randLenVectors(e.id + 1, 8, 1f + 23f * e.finpow(), (x, y) -> {
            Lines.lineAngle(e.x + x, e.y + y, Mathf.atan2(x, y), 1f + e.fout() * 3f);
        });

        Draw.reset();
    }),
    smelt = new Effect(20, e -> {
        Lines.stroke(1f);
        Draw.color(Color.white, e.color, e.fin());
        Lines.spikes(e.x, e.y, e.fin() * 5f, 1f, 8);
        Draw.reset();
    }),

    hit = new Effect(10, e -> {
        Lines.stroke(1f);
        Draw.color(Color.white, Color.orange, e.fin());
        Lines.spikes(e.x, e.y, e.fin() * 3f, 2, 8);
        Draw.reset();
    }),

    laserhit = new Effect(10, e -> {
        Lines.stroke(1f);
        Draw.color(Color.white, Color.sky, e.fin());
        Lines.spikes(e.x, e.y, e.fin() * 2f, 2, 6);
        Draw.reset();
    }),

    nuclearShockwave = new Effect(10f, 200f, e -> {
        Draw.color(Color.white, Color.lightGray, e.fin());
        Lines.stroke(e.fout()*3f + 0.2f);
        Lines.poly(e.x, e.y, 40, e.fin()*140f);
        Draw.reset();
    })
    ;
}
