package classicMod.content;

import arc.func.Prov;
import classicMod.library.blocks.snek.OverflowGateRevamp;
import classicMod.library.blocks.snek.SorterRevamp;
import mindustry.content.*;
import mindustry.gen.Building;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.Sorter;
import mindustry.world.meta.BuildVisibility;

import static mindustry.type.ItemStack.with;

public class OverridableContent {

    public void loadOverride(){
        Blocks.stone.itemDrop = ClassicItems.stone;
        Blocks.stone.playerUnmineable = true;

        Blocks.craters.itemDrop = ClassicItems.stone;
        Blocks.craters.playerUnmineable = true;

        SectorPresets.onset.alwaysUnlocked = false;
       /* Blocks.overflowGate.update =
                Blocks.underflowGate.update =
                        true;

        Blocks.overflowGate.instantTransfer =
                Blocks.underflowGate.instantTransfer =
                        false;

        Blocks.overflowGate.itemCapacity =
                Blocks.underflowGate.itemCapacity =
                        1;

        /*Prov<Building> buildingProv = OverflowGateRevamp.OverflowGateRevampBuild::create;
        Blocks.overflowGate.buildType = buildingProv;
        Blocks.underflowGate.buildType = buildingProv;*/

        Blocks.overflowGate.buildVisibility = Blocks.underflowGate.buildVisibility = BuildVisibility.hidden;
        Blocks.overflowGate = new OverflowGateRevamp("overflow-gate"){{
                requirements(Category.distribution, with(Items.lead, 2, Items.copper, 4));
                buildCostMultiplier = 3f;
        }};

        Blocks.underflowGate = new OverflowGateRevamp("underflow-gate"){{
            requirements(Category.distribution, with(Items.lead, 2, Items.copper, 4));
            buildCostMultiplier = 3f;
            invert = true;
        }};

        /*Blocks.underflowGate = new OverflowGateRevamp("underflow-gate"){{
            requirements(Category.distribution, with(Items.lead, 2, Items.copper, 4));
            buildCostMultiplier = 3f;
            invert = true;
        }};*/
    }
}
