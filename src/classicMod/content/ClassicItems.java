package classicMod.content;

import arc.graphics.*;
import arc.struct.*;
import mindustry.type.*;

import static mindustry.content.Items.*;

public class ClassicItems {
    public static Item
            //v4 items
            denseAlloy,

            //Classic
            uranium, dirium, steel, stone, iron
    ;

    public static final Seq<Item> classicOnlyItems = new Seq<>();
    public static void load(){
        uranium = new Item("uranium", Color.valueOf("ace183")){{
            explosiveness = 0.5f;
            hardness = 5;
            radioactivity = 2f;
            cost = 1.3f;
            healthScaling = 0.15f;
        }};

        dirium = new Item("dirium", Color.valueOf("a7f3ca")){{
            hardness = titanium.hardness + 2;
            cost = 1f + 0.7f*1.25f;
        }};

        steel = new Item("steel", Color.valueOf("0093a6")){{
            hardness = 2;
            cost = 0.7f*1.25f;
        }};

        iron = new Item("iron", Color.valueOf("c0a8a6")){{
            hardness = 3;
            cost = lead.cost*2;
        }};

        fissileMatter.hidden = false; //ok

        classicOnlyItems.addAll(uranium, dirium, steel, iron, titanium);
    }
}
