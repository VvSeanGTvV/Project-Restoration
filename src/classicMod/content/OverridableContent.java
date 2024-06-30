package classicMod.content;

import arc.func.Prov;
import classicMod.library.blocks.snek.OverflowGateRevamp;
import classicMod.library.blocks.snek.SorterRevamp;
import mindustry.content.*;
import mindustry.gen.Building;
import mindustry.type.Category;
import mindustry.world.blocks.distribution.Sorter;

import static mindustry.type.ItemStack.with;

public class OverridableContent {
    public void loadOverride(){
        Blocks.stone.itemDrop = ClassicItems.stone;
        Blocks.stone.playerUnmineable = true;

        Blocks.craters.itemDrop = ClassicItems.stone;
        Blocks.craters.playerUnmineable = true;

        SectorPresets.onset.alwaysUnlocked = false;
        Blocks.overflowGate = new OverflowGateRevamp("overflow-gate"){{
            requirements(Category.distribution, with(Items.lead, 2, Items.copper, 4));
            buildCostMultiplier = 3f;
        }};

        Blocks.underflowGate = new OverflowGateRevamp("underflow-gate"){{
            requirements(Category.distribution, with(Items.lead, 2, Items.copper, 4));
            buildCostMultiplier = 3f;
            invert = true;
        }};

        Blocks.sorter = new SorterRevamp("sorter"){{
            requirements(Category.distribution, with(Items.lead, 2, Items.copper, 2));
            buildCostMultiplier = 3f;
        }};

        Blocks.invertedSorter = new SorterRevamp("inverted-sorter"){{
            requirements(Category.distribution, with(Items.lead, 2, Items.copper, 2));
            buildCostMultiplier = 3f;
            invert = true;
        }};
    }
}
