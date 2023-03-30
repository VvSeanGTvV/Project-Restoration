package classicMod.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.graphics.*;

public class ExtendedFx extends Fx {
    public static final Effect

        shellEjectSmall = new Effect(30f, 400f, e -> {
            Draw.color(Pal.lightOrange, Color.lightGray, Pal.lightishGray, e.fin());
            float rot = Math.abs(e.rotation) + 90f;

            int i = Mathf.sign(e.rotation);

            float len = (2f + e.finpow() * 6f) * i;
            float lr = rot + e.fin() * 30f * i;
            Fill.rect(e.x + Angles.trnsx(lr, len) + Mathf.randomSeedRange(e.id + i + 7, 3f * e.fin()),
                    e.y + Angles.trnsy(lr, len) + Mathf.randomSeedRange(e.id + i + 8, 3f * e.fin()),
                    1f, 2f, rot + e.fin() * 50f * i);

        }),

        shellEjectMedium = new Effect(34f, 400f, e -> {
            Draw.color(Pal.lightOrange, Color.lightGray, Pal.lightishGray, e.fin());
            float rot = e.rotation + 90f;
            for(int i : Mathf.signs){
                float len = (2f + e.finpow() * 10f) * i;
                float lr = rot + e.fin() * 20f * i;
                Draw.rect(Core.atlas.find("restored-mind-casingOld"),
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
            Draw.color(Pal.lightOrange, Color.lightGray, Pal.lightishGray, e.fin());
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

        })
    ;
}
