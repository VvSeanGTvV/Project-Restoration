package v5mod.content;

import arc.graphics.*;
import arc.math.*;
import arc.struct.*;
import mindustry.*;
import mindustry.content.UnitTypes;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.DrawPart.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.unit.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.campaign.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.legacy.*;
import mindustry.world.blocks.liquid.*;
import mindustry.world.blocks.logic.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.sandbox.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.blocks.units.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;
import mindustry.content.*;

import v5mod.lib.blocks.*; //library contents for blocks extended
import v5mod.lib.blocks.LegacyUnitFactory;

import static mindustry.Vars.*;
import static mindustry.type.ItemStack.*;

public class V5Blocks{
    public static Block

    dartPad, omegaPad, deltaPad, alphaPad, tauPad, javelinPad, tridentPad, glaivePad, //Mech Pad

    wraithFactory, ghoulFactory, revenantFactory, //Air Unit Factory
    crawlerFactory,daggerFactory, titanFactory, fortressFactory, //Ground Unit Factory
    draugFactory, spiritFactory, phantomFactory, //Unit Support Factory

    insulatorWall, insulatorWallLarge
    ;
    public void load(){
        //--- Mech Pad Region ---
        omegaPad = new MechPad("omega-pad"){{
            requirements(Category.effect, with(Items.lead, 225, Items.graphite, 275, Items.silicon, 325, Items.thorium, 300, Items.surgeAlloy, 120));
            size = 3;
            hasPower = true;
            unitType = V5UnitTypes.omega;

            //mechReqs = with(mindustry.content.Items.copper, 0);
            consumePower(1.2f);
        }};

        deltaPad = new MechPad("delta-pad"){{
            requirements(Category.effect, with(Items.lead, 175, Items.titanium, 175, Items.copper, 200, Items.silicon, 225, Items.thorium, 150));
            size = 2;
            hasPower = true;
            unitType = V5UnitTypes.delta;

            consumePower(0.7f);
        }};

        javelinPad = new MechPad("javelin-pad"){{
            requirements(Category.effect, with(Items.lead, 175, Items.silicon, 225, Items.titanium, 250, Items.plastanium, 200, Items.phaseFabric, 100));
            size = 2;
            hasPower = true;
            unitType = V5UnitTypes.javelin;

            consumePower(0.8f);
        }};

        tridentPad = new MechPad("trident-pad"){{
            requirements(Category.effect, with(Items.lead, 125, Items.copper, 125, Items.silicon, 125, Items.titanium, 150, Items.plastanium, 100));
            size = 2;
            hasPower = true;
            unitType = V5UnitTypes.trident;

            consumePower(1f);
        }};

        dartPad = new MechPad("dart-pad"){{
            requirements(Category.effect, with(Items.lead, 100, Items.graphite, 50, Items.copper, 75));
            size = 2;
            hasPower = true;
            unitType = V5UnitTypes.dart;

            consumePower(0.5f);
        }};

        alphaPad = new MechPad("alpha-pad"){{
            requirements(Category.effect, with(Items.lead, 100, Items.graphite, 50, Items.copper, 75));
            size = 2;
            hasPower = true;
            unitType = V5UnitTypes.alpha;

            consumePower(0.5f);
        }};

        tauPad = new MechPad("tau-pad"){{
            requirements(Category.effect, with(Items.lead, 125, Items.titanium, 125, Items.copper, 125, Items.silicon, 125));
            size = 2;
            hasPower = true;
            unitType = V5UnitTypes.tau;

            consumePower(1f);
        }};

        glaivePad = new MechPad("glaive-pad"){{
            requirements(Category.effect, with(Items.lead, 225, Items.silicon, 325, Items.titanium, 350, Items.plastanium, 300, Items.surgeAlloy, 100));
            size = 3;
            hasPower = true;
            unitType = V5UnitTypes.glaive;

            consumePower(1.2f);
        }};
        //--- Mech Pad Region End ---

        //--- Unit Factory Region ---
        wraithFactory = new LegacyUnitFactory("wraith-factory"){{
            requirements(Category.units, ItemStack.with(Items.titanium, 30, Items.lead, 40, Items.silicon, 45));
            size = 2;
            produceTime = 700;
            maxSpawn = 4;

            consumePower(0.5f);
            requirement = with(Items.silicon, 10, Items.titanium, 5);
            unitType = V5UnitTypes.wraith;
        }};

        ghoulFactory = new LegacyUnitFactory("ghoul-factory"){{
            requirements(Category.units, ItemStack.with(Items.titanium, 75, Items.lead, 65, Items.silicon, 110));
            size = 3;
            produceTime = 1150;
            maxSpawn = 4;

            consumePower(1.2f);
            requirement = with(Items.silicon, 15, Items.titanium, 10);
            unitType = V5UnitTypes.ghoul;
        }};

        revenantFactory = new LegacyUnitFactory("revenant-factory"){{
            requirements(Category.units, ItemStack.with(Items.plastanium, 50, Items.titanium, 150, Items.lead, 150, Items.silicon, 200));
            size = 4;
            produceTime = 2000;
            maxSpawn = 4;

            consumePower(3f);
            requirement = with(Items.silicon, 40, Items.titanium, 30);
            unitType = V5UnitTypes.revenant;
        }};
        //--- Unit Factory Region ---

        //--- Unit Support Factory Region ---
        draugFactory = new LegacyUnitFactory("draug-factory"){{
            requirements(Category.units, ItemStack.with(Items.copper, 30, Items.lead, 70));
            size = 2;
            produceTime = 2500;
            //maxSpawn = 1;

            consumePower(1.2f);
            //requirement = with(Items.silicon, 40, Items.titanium, 30);
            unitType = V5UnitTypes.draug;
        }};

        spiritFactory = new LegacyUnitFactory("spirit-factory"){{
            requirements(Category.units, ItemStack.with(Items.metaglass, 45, Items.lead, 55, Items.silicon, 45));
            size = 2;
            produceTime = 4000;
            //maxSpawn = 1;

            consumePower(1.2f);
            requirement = with(Items.silicon, 30, Items.lead, 30);
            unitType = V5UnitTypes.spirit;
        }};

        phantomFactory = new LegacyUnitFactory("phantom-factory"){{
            requirements(Category.units, ItemStack.with(Items.titanium, 50, Items.thorium, 60, Items.lead, 65, Items.silicon, 105));
            size = 2;
            produceTime = 4400;
            //maxSpawn = 1;

            consumePower(2.5f);
            requirement = with(Items.silicon, 50, Items.lead, 30, Items.titanium, 20);
            unitType = V5UnitTypes.phantom;
        }};
        //--- Unit Support Factory Region End ---

        // --- Unit Ground Factory Region ---
        crawlerFactory = new LegacyUnitFactory("crawler-factory"){{
            requirements(Category.units, ItemStack.with(Items.lead, 45, Items.silicon, 30));
            size = 2;
            produceTime = 300;
            maxSpawn = 6;

            consumePower(0.5f);
            requirement = with(Items.coal, 10);
            unitType = V5UnitTypes.crawler;
        }};

        daggerFactory = new LegacyUnitFactory("dagger-factory"){{
            requirements(Category.units, ItemStack.with(Items.lead, 55, Items.silicon, 35));
            size = 2;
            produceTime = 850;
            maxSpawn = 4;

            consumePower(0.5f);
            requirement = with(Items.silicon, 6);
            unitType = V5UnitTypes.dagger;
        }};

        titanFactory = new LegacyUnitFactory("titan-factory"){{
            requirements(Category.units, ItemStack.with(Items.graphite, 50, Items.lead, 50, Items.silicon, 45));
            size = 3;
            produceTime = 1050;
            maxSpawn = 4;

            consumePower(0.6f);
            requirement = with(Items.silicon, 12);
            unitType = V5UnitTypes.titan;
        }};

        fortressFactory = new LegacyUnitFactory("fortress-factory"){{
            requirements(Category.units, ItemStack.with(Items.thorium, 40, Items.lead, 110, Items.silicon, 75));
            size = 3;
            produceTime = 2000;
            maxSpawn = 3;

            consumePower(1.2f);
            requirement = with(Items.silicon, 20, Items.graphite, 10);
            unitType = V5UnitTypes.fortress;
        }};
        //--- Unit Ground Factory Region End ---

        //--- Wall ---
        int wallHealthMultiplier = 4;
        insulatorWall = new Wall("insulator-wall"){{
            requirements(Category.defense, ItemStack.with(Items.graphite, 10, Items.lead, 4));
            health = 90 * wallHealthMultiplier / 3;
            insulated = true;
        }};

        insulatorWallLarge = new Wall("insulator-wall-large"){{
            requirements(Category.defense, ItemStack.mult(insulatorWall.requirements, 4));
            health = 90 * wallHealthMultiplier * 4 / 3;
            insulated = true;
            size = 2;
        }};
    }
}
