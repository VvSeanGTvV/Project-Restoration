package classicMod.content;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.graphics.*;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static mindustry.graphics.Pal.*;

public class RFx extends Fx {
    public static final Effect
    // Mantel
    shootMantel = new Effect(10, e -> {
        color(Pal.lightOrange, e.color, e.fin());
        float w = 2.3f + 10 * e.fout();
        Drawf.tri(e.x, e.y, w, 70f * e.fout(), e.rotation);
        Drawf.tri(e.x, e.y, w, 12f * e.fout(), e.rotation + 180f);
    }),
    shootSmokeMantel = new Effect(70f, e -> {
            rand.setSeed(e.id);
            float w = 20;
            for(int i = 0; i < w; i++){
                v.trns(e.rotation + rand.range(30f), rand.random(e.finpow() * 40f));
                Vec2 v0 = new Vec2(0, i - (w/2f)).rotate(e.rotation + rand.range(30f));
                e.scaled(e.lifetime * rand.random(0.3f, 1f), b -> {
                    color(e.color, Pal.lightishGray, b.fin());
                    Fill.circle(e.x + v.x + v0.x, e.y + v.y + v0.y, b.fout() * 3.4f + 0.3f);
                });
            }
    }),

    // Neoplasm
    missilePoreLaunch = new Effect(280.0F, (e) -> {
        Draw.color(neoplasm2);
        Draw.alpha(0.6F);
        rand.setSeed(e.id);

        for(int i = 0; i < 12; ++i) {
            float len = rand.random(25.0F);
            float rot = rand.range(120.0F) + e.rotation;
            e.scaled(e.lifetime * rand.random(0.3F, 1.0F), (b) -> {
                v.trns(rot, len * b.finpow());
                Fill.circle(e.x + v.x, e.y + v.y, 5.3F * b.fslope() + 0.2F);
            });
        }
    }).followParent(false),

    kalyxSmoke = new Effect(70.0F, (e) -> {
        Draw.color(neoplasm1, neoplasmMid, e.fout());
        Draw.alpha(0.6F);
        rand.setSeed(e.id);

        for(int i = 0; i < 12; ++i) {
            float len = rand.random(50.0F);
            float rot = (rand.range(30.0F) + e.rotation);
            e.scaled(e.lifetime * rand.random(0.3F, 1.0F), (b) -> {
                v.trns(rot, len * b.finpow());
                Fill.circle(e.x + v.x, e.y + v.y, 5.3F * b.foutpow() + 0.2F);
            });
        }
    }).followParent(false),

    kalyxShoot = new Effect(9.0F, (e) -> {
        Draw.color(neoplasm1, neoplasm2, e.fin());
        float w = 1.2F + 12.0F * e.fout();
        Drawf.tri(e.x, e.y, w, 50.0F * e.fout(), e.rotation);
        Drawf.tri(e.x, e.y, w, 8.0F * e.fout(), e.rotation + 180.0F);
    }),

    // Effects
    siloLaunchEffect = new Effect(20f, e -> {
        Draw.color(Color.white, Color.lightGray, e.fin()); //color goes from white to light gray
        Lines.stroke(e.fout() * 3); //line thickness goes from 3 to 0
        Lines.circle(e.x, e.y, e.fin() * 100); //draw a circle whose radius goes from 0 to 100
    }),

    hitYellowLaser = new Effect(8, e -> {
        color(Color.white, lightTrail, e.fin());
        stroke(0.5f + e.fout());
        Lines.circle(e.x, e.y, e.fin() * 5f);

        Drawf.light(e.x, e.y, 23f, lightTrail, e.fout() * 0.7f);
    }),

    dynamicSmallBomb = new Effect(40f, 100f, e -> {
        color(e.color);
        stroke(e.fout());
        float circleRad = 2f + e.finpow() * 16f;
        Lines.circle(e.x, e.y, circleRad);

        color(e.color);
        for(int i = 0; i < 4; i++){
            Drawf.tri(e.x, e.y, 1.5f, 25f * e.fout(), i*90);
        }

        color();
        for(int i = 0; i < 4; i++){
            Drawf.tri(e.x, e.y, 0.75f, 6.25f * e.fout(), i*90);
        }

        Drawf.light(e.x, e.y, circleRad * 0.8f, e.color, e.fout());
    }),

    dynamicWaveBig = new Effect(22, e -> {
        color(e.color, 0.7f);
        stroke(e.fout() * 2.5f);
        Lines.circle(e.x, e.y, 8f + e.finpow() * e.rotation);
    }),

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
            Draw.rect(Core.atlas.find("restored-mind-casing-old"),
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

    commandSend = new Effect(28, e -> {
        Draw.color(Pal.command);
        Lines.stroke(e.fout() * 2f);
        Lines.circle(e.x, e.y, 4f + e.finpow() * 120f);
    }),

    //Modified effects for Good Reason
    teslaBeam = new Effect(30f, 300f, e -> {

        float rand = 1f;

        //Draw the line if it has position data.
        if(e.data instanceof Position pos) {
            Draw.color(e.color, e.fout());
            Lines.stroke(2f * e.fout());
            Lines.line(e.x + Mathf.range(rand), e.y + Mathf.range(rand), pos.getX() + Mathf.range(rand), pos.getY() + Mathf.range(rand));
            Drawf.light(e.x + Mathf.range(rand), e.y + Mathf.range(rand), pos.getX() + Mathf.range(rand), pos.getY() + Mathf.range(rand), 20f, e.color, 0.6f * e.fout());
        }

        float rad = 7f * e.fout();
        Draw.rect(Core.atlas.find("restored-mind-circle"), e.x + Mathf.range(rand), e.y + Mathf.range(rand), rad, rad);
    }),

    laserBeam = new Effect(8f, 300f, e -> {
        if(!(e.data instanceof Position pos)) return;

        float lighten = (Mathf.sin(Time.time/1.2f) + 1f) / 10f;
        //Draw.color(e.color);
        Draw.colorMul(e.color, 1f + lighten);

        Draw.alpha(0.3f * e.fout());
        Lines.stroke(4f);
        Lines.line(e.x, e.y, pos.getX(), pos.getY());

        Draw.alpha(e.fout());
        Lines.stroke(2f);
        Lines.line(e.x, e.y, pos.getX(), pos.getY());
        Drawf.light(e.x, e.y, pos.getX(), pos.getY(), 24f, e.color, 0.6f);

        Draw.alpha(e.fout());
        float rad = 7f;
        Draw.rect(Core.atlas.find("restored-mind-circle"), e.x, e.y, rad, rad);
        Draw.rect(Core.atlas.find("restored-mind-circle"), pos.getX(), pos.getY(), rad - 2f, rad - 2f);
    }),

    //v4 Mindustry
    mortarshot = new Effect(10f, e -> {
        Draw.color(Color.white, Color.darkGray, e.fin());
        Lines.stroke(e.fout()*6f);
        Lines.lineAngle(e.x, e.y, e.rotation, e.fout()*10f);
        Lines.stroke(e.fout()*5f);
        Lines.lineAngle(e.x, e.y, e.rotation, e.fout()*14f);
        Lines.stroke(e.fout());
        Lines.lineAngle(e.x, e.y, e.rotation, e.fout()*16f);
        Draw.reset();
    }),
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

    railshot = new Effect(9f, e -> {
        Draw.color(Color.white, Color.darkGray, e.fin());
        Lines.stroke(e.fout()*5f);
        Lines.lineAngle(e.x, e.y, e.rotation, e.fout()*8f);
        Lines.stroke(e.fout()*4f);
        Lines.lineAngle(e.x, e.y, e.rotation, e.fout()*12f);
        Lines.stroke(e.fout());
        Lines.lineAngle(e.x, e.y, e.rotation, e.fout()*14f);
        Draw.reset();
    }),

    railsmoke = new Effect(30, e -> {
        Draw.color(Color.lightGray, Color.white, e.fin());
        float size = e.fout()*4f;
        Draw.rect("restored-mind-circle", e.x, e.y, size, size);
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
        Lines.spikes(e.x, e.y, e.fin() * 4f, 1f, 6);
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
            Fill.circle(e.x + x / 2f, e.y + y / 2f, e.fout());
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

    smeltsmoke = new Effect(15, e -> {
        Angles.randLenVectors(e.id, 6, 4f + e.fin() * 5f, (x, y) -> {
            Draw.color(Color.white, e.color, e.fin());
            Fill.square(e.x + x, e.y + y, 0.5f + e.fout() * 2f, 45);
            Draw.reset();
        });
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
