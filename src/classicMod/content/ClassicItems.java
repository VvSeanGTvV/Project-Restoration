package classicMod.content;

import arc.graphics.Color;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.content.*;
import mindustry.type.Item;

public class ClassicItems extends Items {
    public static Item
            // Example-mod
            electrum, goldPowder, silver,

            //v4 items
            denseAlloy,

            //v4 & Classic Hybrid Items
            stone,

            //Classic
            uranium, dirium, steel, iron
    ;

    public static final Seq<Item> classicItems = new Seq<>(), classicOnlyItems = new Seq<>();
    public static final Seq<Item> erekirNewItems = new Seq<>(), erekirNewOnlyItems = new Seq<>();

    public static void load(){

        electrum = new Item("electrum", Color.valueOf("dcd16b")){{
            hardness = 3;
            cost = 2.5f;
            charge = 1.15f;
        }};

        goldPowder = new Item("gold-powder", Color.valueOf("f3e979")){{
            lowPriority = true;
            buildable = false;
            //needed to show up as requirement
        }};

        silver = new Item("silver", Color.valueOf("a4a2bd")){{
            hardness = 2;
            cost = 2f;
        }};


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
            cost = iron.cost * 1.15f;
        }};

        dirium = new Item("dirium", Color.valueOf("a7f3ca")){{
            hardness = titanium.hardness + lead.hardness;
            cost = lead.cost * 1.15f;
        }};

        fissileMatter.hidden = false; //ok
        Liquids.gallium.hidden = false;

        for (var item : erekirItems){
            erekirNewItems.add(item);
        }
        erekirNewItems.add(Items.scrap);
        Planets.erekir.hiddenItems.clear();
        for (var item : serpuloItems){
            if (!erekirNewItems.contains(item)){
                Planets.erekir.hiddenItems.add(item);
            }
        }

        classicItems.addAll(stone, uranium, dirium, steel, iron, titanium);
        classicOnlyItems.add(classicItems).removeAll(i -> !(classicItems.contains(i)));
    }
}
