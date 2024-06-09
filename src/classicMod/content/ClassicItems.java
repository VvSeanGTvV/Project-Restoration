package classicMod.content;

import arc.graphics.*;
import arc.struct.*;
import mindustry.content.*;
import mindustry.type.*;

public class ClassicItems extends Items {
    public static Item
            //v4 items
            denseAlloy, tungstenOld,

            //v4 & Classic Hybrid Items
            stone,

            //Classic
            uranium, dirium, steel, iron
    ;

    public static final Seq<Item> classicOnlyItems = new Seq<>();

    public static void load(){

        tungstenOld = new Item("tungsten-old", Color.valueOf("a0b0c8")) {{
            hardness = 1;
        }};

        denseAlloy = new Item("dense-alloy", Color.valueOf("b2c6d2")){{
            cost = 1.2f;
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
            hardness = titanium.hardness + steel.hardness;
            cost = 1 + steel.cost * 1.25f;
        }};

        fissileMatter.hidden = false; //ok

        classicOnlyItems.addAll(uranium, dirium, steel, iron, titanium).removeAll(serpuloItems);
        classicOnlyItems.addAll(uranium, dirium, steel, iron, titanium).removeAll(erekirItems);

        erekirItems.addAll( //Override the erekir stuff
                graphite, thorium, silicon, phaseFabric, surgeAlloy, sand,
                beryllium, tungsten, oxide, carbide, fissileMatter, scrap, dormantCyst
        );

        erekirOnlyItems.addAll(erekirItems).removeAll(serpuloItems);
    }
}
