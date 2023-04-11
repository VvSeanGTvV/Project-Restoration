package classicMod.content;

import arc.graphics.*;
import mindustry.content.*;
import mindustry.type.*;

public class ClassicLiquids extends Liquids {
    public static Liquid
    lava
    ;
    public static void load(){
        lava = new Liquid("liquid-lava", Color.valueOf("ed5334")){{
            temperature = 2.3f;
            viscosity = 1.6f;
            effect = StatusEffects.melting;
            lightColor = Color.valueOf("f57b62").a(0.4f);
        }};
    }
}
