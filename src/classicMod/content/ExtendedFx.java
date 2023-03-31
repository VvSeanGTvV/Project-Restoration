package classicMod.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.graphics.*;

import static mindustry.graphics.Pal.lightOrange;

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


        //Mindustry Classic
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
                Draw.rect("circle", e.x + x, e.y + y, size, size);
                Draw.reset();
            });
        })
    ;
}
