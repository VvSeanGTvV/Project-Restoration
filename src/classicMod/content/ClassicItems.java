package classicMod.content;

import arc.graphics.*;
import arc.struct.*;
import mindustry.content.*;
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

        dirium = new Item("dirium", Color.valueOf("ace183")){{
            hardness = Items.titanium.hardness + 3;
            cost = 1.3f;
        }};

        classicOnlyItems.addAll(uranium, dirium, steel, stone, iron, titanium);
    }
}
