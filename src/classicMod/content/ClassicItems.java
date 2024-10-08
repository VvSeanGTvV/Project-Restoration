package classicMod.content;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.type.Item;

public class ClassicItems extends Items {
    public static Item
            //v4 items
            denseAlloy,

            //v4 & Classic Hybrid Items
            stone,

            //Classic
            uranium, dirium, steel, iron
    ;

    public static final Seq<Item> classicItems = new Seq<>(), classicOnlyItems = new Seq<>();

    public static void load(){

        denseAlloy = new Item("dense-alloy", Color.valueOf("b2c6d2")){{
            cost = 1f;
        }};

        float leadCost = lead.cost;
        uranium = new Item("uranium", Color.valueOf("ace183")){{
            explosiveness = thorium.explosiveness*1.5f;
            hardness = 5;
            radioactivity = 2f;
            cost = 1.3f;
            healthScaling = 0.15f;
        }};

        stone = new Item("stone", Color.valueOf("8c8c8c")){{
            cost = copper.cost * 0.85f;
        }};

        iron = new Item("iron", Color.valueOf("c0a8a6")){{
            hardness = 3;
            cost = leadCost * 1.25f;
        }};

        steel = new Item("steel", Color.valueOf("c5eae6")){{
            hardness = 2;
            cost = iron.cost * 1.25f;
        }};

        dirium = new Item("dirium", Color.valueOf("a7f3ca")){{
            hardness = titanium.hardness + lead.hardness;
            cost = lead.cost * 1.25f;
        }};

        fissileMatter.hidden = false; //ok

        classicItems.addAll(stone, uranium, dirium, steel, iron, titanium);
        erekirItems.add(fissileMatter, scrap);

        erekirOnlyItems.add(fissileMatter, scrap);
        classicOnlyItems.add(classicItems).removeAll(i -> !(classicItems.contains(i)));
    }
}
