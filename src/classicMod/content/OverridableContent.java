package classicMod.content;

import mindustry.content.*;
import mindustry.type.PayloadStack;
import mindustry.world.blocks.units.UnitAssembler;

public class OverridableContent {

    public void loadOverride(){
        Blocks.stone.itemDrop = ClassicItems.stone;
        Blocks.stone.playerUnmineable = true;

        Blocks.craters.itemDrop = ClassicItems.stone;
        Blocks.craters.playerUnmineable = true;

        SectorPresets.onset.alwaysUnlocked = false;
        if (Blocks.tankAssembler instanceof UnitAssembler unitAssembler){
            unitAssembler.plans.add(
                    new UnitAssembler.AssemblerUnitPlan(ClassicUnitTypes.mantel, 60f * 60f * 4f, PayloadStack.list(UnitTypes.locus, 3, Blocks.tungstenWall, 30, Blocks.carbideWallLarge, 30)),
                    new UnitAssembler.AssemblerUnitPlan(ClassicUnitTypes.howit, 60f * 60f * 2f, PayloadStack.list(UnitTypes.stell, 2, Blocks.tungstenWall, 10))
            );
        }
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

        //Blocks.overflowGate.buildVisibility = Blocks.underflowGate.buildVisibility = BuildVisibility.hidden;
        /*Blocks.overflowGate = new OverflowGateRevamp("overflow-gate"){{
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
