package classicMod.content;

import arc.struct.ObjectMap;
import mindustry.world.meta.Attribute;

import static mindustry.world.meta.Attribute.add;

public class RAttribute {
    public static Attribute[] all = {};
    public static ObjectMap<String, Attribute> map = new ObjectMap<>();

    /** Heat content. Used for thermal generator yield. */
    public static final Attribute
    /** Biomatter content. Used for old school cultivator/growth chamber yield. */
    grass = add("grass");
}
