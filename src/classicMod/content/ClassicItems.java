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
        float leadCost = lead.cost;
        uranium = new Item("uranium", Color.valueOf("ace183")){{
            explosiveness = 0.5f;
            hardness = 5;
            radioactivity = 2f;
            cost = 1.3f;
            healthScaling = 0.15f;
        }};

        steel = new Item("steel", Color.valueOf("c5eae6")){{
            hardness = 2;
            cost = leadCost*1.25f;
        }};

        dirium = new Item("dirium", Color.valueOf("a7f3ca")){{
            hardness = titanium.hardness + steel.hardness;
            cost = 1f + steel.cost;
        }};

        iron = new Item("iron", Color.valueOf("c0a8a6")){{
            hardness = 3;
            cost = lead.cost*2;
        }};

        fissileMatter.hidden = false; //ok

        classicOnlyItems.addAll(uranium, dirium, steel, iron, titanium);
    }
}
