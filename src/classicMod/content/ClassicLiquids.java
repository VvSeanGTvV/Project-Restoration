package classicMod.content;

import arc.graphics.Color;
import mindustry.content.*;
import mindustry.type.Liquid;

public class ClassicLiquids extends Liquids {
    public static Liquid
    lava
    ;
    public static void load(){
        lava = new Liquid("liquid-lava", Color.valueOf("ed5334")){{
            temperature = 2.3f;
            viscosity = 0.65f;
            effect = StatusEffects.melting;
            lightColor = Color.valueOf("f57b62").a(0.4f);
        }};
    }
}
