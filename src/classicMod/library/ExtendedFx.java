package classicMod.library;

import arc.graphics.g2d.*;
import mindustry.content.*;
import mindustry.entities.*;

import static arc.graphics.g2d.Draw.*;

public class ExtendedFx extends Fx {
    public static final Effect
    shootSmokeRavage = new Effect(70f, e -> {
        rand.setSeed(e.id);
        for(int i = 0; i < 13; i++){
            float a = e.rotation + rand.range(30f);
            v.trns(a, rand.random(e.finpow() * 50f));
            e.scaled(e.lifetime * rand.random(0.3f, 1f), b -> {
                color(e.color);
                Lines.stroke(b.fout() * 3f + 0.5f);
                Lines.lineAngle(e.x + v.x, e.y + v.y, a, b.fout() * 8f + 0.4f);
            });
        }
    })
    ;
}
