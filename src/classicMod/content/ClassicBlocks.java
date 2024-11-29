package classicMod.content;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.Seq;
import classicMod.library.blocks.*;
import classicMod.library.blocks.customBlocks.*;
import classicMod.library.blocks.legacyBlocks.*;
import classicMod.library.blocks.neoplasiaBlocks.*;
import classicMod.library.blocks.v6devBlocks.*;
import classicMod.library.bullets.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.campaign.LaunchPad;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.heat.HeatProducer;
import mindustry.world.blocks.payloads.PayloadMassDriver;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static classicMod.content.ClassicBullets.*;
import static classicMod.content.ClassicSounds.*;
import static classicMod.content.ClassicUnitTypes.*;
import static mindustry.content.Blocks.coreBastion;
import static mindustry.type.ItemStack.with;

public class ClassicBlocks {
    public static Block
    crucible, //Production - classic
    wallDirium, wallComposite, wallDiriumLarge, wallShieldedTitanium,//Wall - classic

    rtgGenerator, //Power [Classic-Hybrid]

    warpGate, laserConveyor, // Transportation [v4]
    stoneMelter, stoneFormer, denseSmelter, arcSmelter, alloySmelter,  stoneSeparator, centrifuge, //Production [v4]
    fuseMKII, fuseMKI, salvoAlpha, teslaTurret, arcAir, chainTurret, cycloneb57, rippleb41, //Turret [v4]
    plasmaDrill, nuclearDrill, poweredDrill, //Drills [v4]
    wallDense, wallDenseLarge, //Wall [v4]

    electraPad, chromeWraithFactory, // Unit/MechPad [v5 - Example-mod]
    electrumDrill, //Drills [v5 - Example-mod]
    shineGenerator, //Power [v5 - Example-mod]
    electrumForge, //Production [v5 - Example-mod]
    scatterSilo, //Special [v5 - Example-mod]
    electrumVault, //Vaults [v5 - Example-mod]
    electrumConveyor, //Transportation [v5 - Example-mod]
    goldSand, silverOre, silverPlate, //Enviroment [v5 - Example-mod]

    aptrgangrFactory, // Unit [v5 - Example-mod]
    batteryMedium, //Battery [v5 - Advanced Content]
    advanceCompressor, fusion, //Production [v5 - Advanced Content]
    areaExtractor, //Drills [v5 - Advanced Content]
    halberdPad, //Mech Pad [v5 - Advanced Content]
    massRepository, //Storage [v5 - Advanced Content]

    dartPad, omegaPad, deltaPad, alphaPad, tauPad, javelinPad, tridentPad, glaivePad, //Mech Pad [v5]
    wraithFactory, ghoulFactory, revenantFactory, //Air - Unit Factory [v5]
    crawlerFactory, daggerFactory, titanFactory, fortressFactory, //Ground - Unit Factory [v5]
    draugFactory, spiritFactory, phantomFactory, //Support - Unit Factory [v5]

    insulatorWall, insulatorWallLarge, //Wall - Insulator - Testing-candidate [v6-dev]
    launchPadLarge, coreSilo, //Launchpad - Campaign only - Block [v6-dev]

    yellowCoral, glowBlob, // Enviroment - Tantros

    dataProcessor, //Research Block - Campaign only - Block [v6-dev]

    thermalPump, //Pump - Old Content [v6]

    warheadAssembler, ballisticSilo, nuclearWarhead, //Nuclear - Prototype [v7-dev] TODO make it work
    shieldProjector, shieldBreaker, largeShieldProjector, barrierProjector, //Projectors - Erekir - Prototype [v7-dev]
    heatReactor, //Heat Producers - Erekir - Prototype [v7-dev]

    slagCentrifuge, cellSynthesisChamber, //Generic Crafters - Erekir - Prototype [v7-dev]
    reinforcedSafe, coreAegis, //Storage - Erekir - Prototype [v7-dev]
    surgeDuct, //Distribution - Erekir - Prototype [v7-dev]
    burstDrill, //Drill - Erekir - Prototype [v7-dev]

    droneCenter, payloadLaunchpad, commandCenter, payloadPropulsionTower, //TEMPORARY TESTING

    fracture, horde, chrome, tinyBreach, //Turrets - Erekir - Prototype [v7-dev]

    interplanetaryAccelerator, //Endgame - Mindustry

    ductJunction, cord, heart //Erekir - Prototype [v8-dev]
    ;


    public void load() {

        //Enviroment
        /*yellowCoral = new SeaBush("yellowcoral") {
            {
                this.lobesMin = 2;
                this.lobesMax = 3;
                this.magMax = 8.0F;
                this.magMin = 2.0F;
                this.origin = 0.3F;
                this.spread = 40.0F;
                this.sclMin = 60.0F;
                this.sclMax = 100.0F;
            }
        };*/

        glowBlob = new ReactiveBlob("glow-blob"){{
            variants = 3;
        }};

        silverOre = new OreBlock(ClassicItems.silver);
        silverPlate = new Floor("silver-plating", 0);

        goldSand = new Floor("gold-sand"){{
            variants = 3;
            itemDrop = ClassicItems.goldPowder;
            playerUnmineable = true;
        }};

        //Mechpad
        electraPad = new MechPad("electra-mech-pad") {{
            requirements(Category.effect, with(ClassicItems.silver, 70, ClassicItems.electrum, 40, Items.silicon, 40, Items.graphite, 50));
            size = 2;
            hasPower = true;
            unitType = electra;

            //mechReqs = with(mindustry.content.Items.copper, 0);
            consumePower(0.7f);
        }};

        omegaPad = new MechPad("omega-pad") {{
            requirements(Category.effect, with(Items.lead, 225, Items.graphite, 275, Items.silicon, 325, Items.thorium, 300, Items.surgeAlloy, 120));
            size = 3;
            hasPower = true;
            unitType = ClassicUnitTypes.omega;

            //mechReqs = with(mindustry.content.Items.copper, 0);
            consumePower(1.2f);
        }};

        deltaPad = new MechPad("delta-pad") {{
            requirements(Category.effect, with(Items.lead, 175, Items.titanium, 175, Items.copper, 200, Items.silicon, 225, Items.thorium, 150));
            size = 2;
            hasPower = true;
            unitType = ClassicUnitTypes.delta;

            consumePower(0.7f);
        }};

        javelinPad = new MechPad("javelin-pad") {{
            requirements(Category.effect, with(Items.lead, 175, Items.silicon, 225, Items.titanium, 250, Items.plastanium, 200, Items.phaseFabric, 100));
            size = 2;
            hasPower = true;
            unitType = ClassicUnitTypes.javelin;

            consumePower(0.8f);
        }};

        tridentPad = new MechPad("trident-pad") {{
            requirements(Category.effect, with(Items.lead, 125, Items.copper, 125, Items.silicon, 125, Items.titanium, 150, Items.plastanium, 100));
            size = 2;
            hasPower = true;
            unitType = ClassicUnitTypes.trident;

            consumePower(1f);
        }};

        dartPad = new MechPad("dart-pad") {{
            requirements(Category.effect, with(Items.lead, 100, Items.graphite, 50, Items.copper, 75));
            size = 2;
            hasPower = true;
            unitType = ClassicUnitTypes.dart;

            consumePower(0.5f);
        }};

        alphaPad = new MechPad("alpha-pad") {{
            requirements(Category.effect, with(Items.lead, 100, Items.graphite, 50, Items.copper, 75));
            size = 2;
            hasPower = true;
            unitType = ClassicUnitTypes.alpha;

            consumePower(0.5f);
        }};

        tauPad = new MechPad("tau-pad") {{
            requirements(Category.effect, with(Items.lead, 125, Items.titanium, 125, Items.copper, 125, Items.silicon, 125));
            size = 2;
            hasPower = true;
            unitType = ClassicUnitTypes.tau;

            consumePower(1f);
        }};

        glaivePad = new MechPad("glaive-pad") {{
            requirements(Category.effect, with(Items.lead, 225, Items.silicon, 325, Items.titanium, 350, Items.plastanium, 300, Items.surgeAlloy, 100));
            size = 3;
            hasPower = true;
            unitType = ClassicUnitTypes.glaive;

            consumePower(1.2f);
        }};

        halberdPad = new MechPad("halberd-ship-pad") {{
            requirements(Category.effect, with(Items.lead, 250, Items.silicon, 350, Items.graphite, 350, Items.titanium, 320, Items.plastanium, 300, Items.surgeAlloy, 120));
            size = 3;
            hasPower = true;
            unitType = halberd;

            consumePower(1.3f);
        }};

        //UnitFactory
        wraithFactory = new LegacyUnitFactory("wraith-factory") {{
            requirements(Category.units, ItemStack.with(Items.titanium, 30, Items.lead, 40, Items.silicon, 45));
            size = 2;
            produceTime = 700;
            maxSpawn = 4;

            consumePower(0.5f);
            requirement = with(Items.silicon, 10, Items.titanium, 5);
            unitType = ClassicUnitTypes.wraith;
        }};

        chromeWraithFactory = new LegacyUnitFactory("chrome-wraith-factory") {{
            requirements(Category.units, ItemStack.with(ClassicItems.silver, 70, ClassicItems.electrum, 40, Items.silicon, 40, Items.titanium, 20));
            size = 2;
            produceTime = 350;
            maxSpawn = 4;

            consumePower(0.5f);
            requirement = with(Items.silicon, 10, ClassicItems.silver, 5);
            unitType = chromeWraith;
        }};

        ghoulFactory = new LegacyUnitFactory("ghoul-factory") {{
            requirements(Category.units, ItemStack.with(Items.titanium, 75, Items.lead, 65, Items.silicon, 110));
            size = 3;
            produceTime = 1150;
            maxSpawn = 4;

            consumePower(1.2f);
            requirement = with(Items.silicon, 15, Items.titanium, 10);
            unitType = ClassicUnitTypes.ghoul;
        }};

        revenantFactory = new LegacyUnitFactory("revenant-factory") {{
            requirements(Category.units, ItemStack.with(Items.plastanium, 50, Items.titanium, 150, Items.lead, 150, Items.silicon, 200));
            size = 4;
            produceTime = 2000;
            maxSpawn = 4;

            consumePower(3f);
            requirement = with(Items.silicon, 40, Items.titanium, 30);
            unitType = ClassicUnitTypes.revenant;
        }};

        draugFactory = new LegacyUnitFactory("draug-factory") {{
            requirements(Category.units, ItemStack.with(Items.copper, 30, Items.lead, 70));
            size = 2;
            produceTime = 2500;

            consumePower(1.2f);
            //requirement = with(Items.silicon, 40, Items.titanium, 30);
            unitType = ClassicUnitTypes.draug;
        }};

        aptrgangrFactory = new LegacyUnitFactory("aptrgangr-factory") {{
            requirements(Category.units, ItemStack.with(Items.graphite, 200, Items.lead, 160, Items.titanium, 120, Items.thorium, 80));
            size = 3;
            produceTime = 3500;
            maxSpawn = 1;

            consumePower(1.2f);
            requirement = with(Items.thorium, 5);
            unitType = aptrgangr;
        }};

        spiritFactory = new LegacyUnitFactory("spirit-factory") {{
            requirements(Category.units, ItemStack.with(Items.metaglass, 45, Items.lead, 55, Items.silicon, 45));
            size = 2;
            produceTime = 4000;
            //maxSpawn = 1;

            consumePower(1.2f);
            requirement = with(Items.silicon, 30, Items.lead, 30);
            unitType = ClassicUnitTypes.spirit;
        }};

        phantomFactory = new LegacyUnitFactory("phantom-factory") {{
            requirements(Category.units, ItemStack.with(Items.titanium, 50, Items.thorium, 60, Items.lead, 65, Items.silicon, 105));
            size = 2;
            produceTime = 4400;
            //maxSpawn = 1;

            consumePower(2.5f);
            requirement = with(Items.silicon, 50, Items.lead, 30, Items.titanium, 20);
            unitType = ClassicUnitTypes.phantom;
        }};

        crawlerFactory = new LegacyUnitFactory("crawler-factory") {{
            requirements(Category.units, ItemStack.with(Items.lead, 45, Items.silicon, 30));
            size = 2;
            produceTime = 300;
            maxSpawn = 6;

            consumePower(0.5f);
            requirement = with(Items.coal, 10);
            unitType = ClassicUnitTypes.crawler;
        }};

        daggerFactory = new LegacyUnitFactory("dagger-factory") {{
            requirements(Category.units, ItemStack.with(Items.lead, 55, Items.silicon, 35));
            size = 2;
            produceTime = 850;
            maxSpawn = 4;

            consumePower(0.5f);
            requirement = with(Items.silicon, 6);
            unitType = ClassicUnitTypes.dagger;
        }};

        titanFactory = new LegacyUnitFactory("titan-factory") {{
            requirements(Category.units, ItemStack.with(Items.graphite, 50, Items.lead, 50, Items.silicon, 45));
            size = 3;
            produceTime = 1050;
            maxSpawn = 4;

            consumePower(0.6f);
            requirement = with(Items.silicon, 12);
            unitType = ClassicUnitTypes.titan;
        }};

        fortressFactory = new LegacyUnitFactory("fortress-factory") {{
            requirements(Category.units, ItemStack.with(Items.thorium, 40, Items.lead, 110, Items.silicon, 75));
            size = 3;
            produceTime = 2000;
            maxSpawn = 3;

            consumePower(1.2f);
            requirement = with(Items.silicon, 20, Items.graphite, 10);
            unitType = ClassicUnitTypes.fortress;
        }};

        droneCenter = new DroneCenterNew("drone-center"){{
            requirements(Category.units, with(Items.tungsten, 150, Items.phaseFabric, 100));

            size = 3;
            statusDuration = 60f * 10f;
            consumePower(3f);

            droneType = effectDrone;
        }};

        batteryMedium = new Battery("battery-medium"){{
            requirements(Category.power, with(Items.lead, 25, Items.graphite, 20, Items.silicon, 20));
            size = 2;
            consumePowerBuffered(27000f);
            baseExplosiveness = 3f;
        }};

        //Pump
        thermalPump = new ThermalPump("thermal-pump"){{
            requirements(Category.liquid, with(Items.copper, 90, Items.metaglass, 90, Items.silicon, 45, Items.titanium, 50, Items.thorium, 45));
            pumpAmount = 0.20f;
            divisionMultiplierPump = 3f;
            consumePower(1.4f);
            liquidCapacity = 50f;
            hasPower = true;
            size = 3;
            squareSprite = false;
        }};

        //Wall
        int wallHealthMultiplier = 4;
        insulatorWall = new Wall("insulator-wall") {{
            requirements(Category.defense, ItemStack.with(Items.graphite, 10, Items.lead, 4));
            health = 95 * wallHealthMultiplier;
            insulated = true;
        }};

        insulatorWallLarge = new Wall("insulator-wall-large") {{
            requirements(Category.defense, ItemStack.mult(insulatorWall.requirements, 4));
            health = 95 * wallHealthMultiplier * 4;
            insulated = true;
            size = 2;
        }};

        wallDense = new Wall("dense-wall"){{
            requirements(Category.defense, with(ClassicItems.denseAlloy, 12));
            health = 110 * wallHealthMultiplier;
            size = 1;
        }};

        wallDenseLarge = new Wall("dense-wall-large"){{
            requirements(Category.defense, ItemStack.mult(wallDense.requirements, 4));
            health = 110 * wallHealthMultiplier * 4;
            size = 2;
        }};

        wallShieldedTitanium = new ShieldWallColor("titanium-shieldwall"){{
            requirements(Category.defense, with(ClassicItems.titanium, 6, ClassicItems.lead, 6));
            glowColor = Items.titanium.color.a(0.5f);
            shieldColor = Items.titanium.color.a(1f);
            shieldHealth = 400f;

            consumePower(1f / 60f);
            health = 40 * wallHealthMultiplier * 4;
            size = 1;
        }};

        wallDirium = new Wall("dirium-wall"){{
            requirements(Category.defense, with(ClassicItems.dirium, 12));
            health = 190 * wallHealthMultiplier;
        }};

        wallComposite = new Wall("composite-wall"){{
            requirements(Category.defense, with(ClassicItems.dirium, 12, Items.titanium, 12, Items.lead, 12));
            health = 270 * wallHealthMultiplier;
        }};

        wallDiriumLarge = new Wall("dirium-wall-large"){{
            requirements(Category.defense, ItemStack.mult(wallDirium.requirements, 4));
            health = 110 * wallHealthMultiplier * 4;
            size = 2;
        }};

        //Projectors
        barrierProjector = new DirectionalForceProjector("barrier-projector"){{
            requirements(Category.effect, with(Items.tungsten, 100, Items.silicon, 125));
            size = 3;
            width = 50f;
            length = 36;
            shieldHealth = 2000f;
            cooldownNormal = 3f;
            cooldownBrokenBase = 0.35f;
            envEnabled |= Env.space;

            consumePower(4f);
        }

            @Override
            public TextureRegion[] icons() {
                return new TextureRegion[]{region, Core.atlas.find(name + "-team-" + "sharded")};
            }

        };

        shieldProjector = new BaseShield("shield-projector"){{
            category = Category.effect;
            requirements(Category.effect, with(Items.tungsten, 130,Items.surgeAlloy, 75, Items.silicon, 110));

            size = 3;
            generatedIcons = new TextureRegion[]{Core.atlas.find(name), Core.atlas.find(name + "-team")};

            consumePower(5f);
        }

            @Override
            public TextureRegion[] icons() {
                return new TextureRegion[]{region, Core.atlas.find(name + "-team-" + "sharded")};
            }

        };

        largeShieldProjector = new BaseShield("large-shield-projector"){{
            requirements(Category.effect, ItemStack.mult(shieldProjector.requirements,2f));

            size = 4;
            radius = 400f;
            generatedIcons = new TextureRegion[]{Core.atlas.find(name), Core.atlas.find(name + "-team")};

            consumePower(12.5f);
        }

            @Override
            public TextureRegion[] icons() {
                return new TextureRegion[]{region, Core.atlas.find(name + "-team-" + "sharded")};
            }

        };

        shieldBreaker = new EMP("shield-breaker"){{
            squareSprite = false;
            requirements(Category.effect, with(Items.tungsten, 700, Items.graphite, 620, Items.silicon, 250));
            envEnabled |= Env.space;
            toDestroy = new Block[]{Blocks.shieldProjector, Blocks.largeShieldProjector, shieldProjector, largeShieldProjector};

            cooldownTime = 600f;
            lightningEffectEnabled = true;
            lightningLengthRand = 15;

            size = 5;
            itemCapacity = 100;
            scaledHealth = 120f;

            consumeItem(Items.tungsten, 100);
        }

            @Override
            public TextureRegion[] icons() {
                return new TextureRegion[]{region, Core.atlas.find(name + "-team-" + "sharded")};
            }

        };

        //Crafting
        stoneFormer = new GenericCrafter("stone-former"){{
            requirements(Category.crafting, with(ClassicItems.stone, 30, Items.lead, 30, Items.copper, 55));
            consumeLiquid(Liquids.slag, 18f/60f);
            outputItem = new ItemStack(ClassicItems.stone, 1);
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(), new DrawDefault());
            health = 80;
            craftTime = 12;
            consumePower(30f/60f);
            craftEffect = ExtendedFx.purifystone;
        }};

        stoneMelter = new GenericCrafter("stone-melter"){{
            requirements(Category.crafting, with(ClassicItems.stone, 30, Items.graphite, 35, Items.copper, 50));
            health = 85;
            outputLiquid = new LiquidStack(Liquids.slag, 4f/60f);
            consumeItems(with(ClassicItems.stone, 1));
            consumePower(62.5f/60f);
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(), new DrawDefault());
            craftTime = 10f;
            itemCapacity = 20;
        }};

        advanceCompressor = new GenericCrafter("advance-compressor"){{
            requirements(Category.crafting, with(Items.silicon, 160, Items.lead, 240, Items.graphite, 120, Items.titanium, 150, Items.plastanium, 40));
            hasItems = true;
            liquidCapacity = 100f;
            craftTime = 45f;
            outputItem = new ItemStack(Items.plastanium, 2);
            size = 3;
            health = 700;
            hasPower = hasLiquids = true;
            craftEffect = Fx.formsmoke;
            updateEffect = Fx.plasticburn;
            drawer = new DrawMulti(new DrawDefault(), new DrawFade());

            consumeLiquid(Liquids.oil, 0.52f);
            consumePower(6.5f);
            consumeItem(Items.titanium, 2);
        }};

        fusion = new GenericFusion("fusion"){
            {
                requirements(Category.crafting, with(Items.silicon, 340, Items.titanium, 300, Items.thorium, 200, Items.surgeAlloy, 75, Items.phaseFabric, 50));
                hasItems = true;
                liquidCapacity = 100f;
                craftTime = 135f;
                burnTime = 170f;
                outputItem = new ItemStack(Items.thorium, 1);
                size = 3;
                health = 700;
                hasPower = hasLiquids = true;
                craftEffect = Fx.formsmoke;
                updateEffect = Fx.plasticburn;

                flameColor = Color.valueOf("ffffff");

                drawer = new DrawMulti(
                        new DrawDefault(),
                        new DrawFade(){{
                            alpha = 1f;
                        }}
                );

                consumeFuels(with(Items.blastCompound, 1));
                consumePower(4.1f);
                consumeItems(with(Items.titanium, 5, Items.silicon, 1));
            }
        };

        denseSmelter = new GenericSmelter("dense-smelter"){{
            health = 70;
            requirements(Category.crafting, with(Items.copper, 100));
            outputItem = new ItemStack(ClassicItems.denseAlloy, 1);
            consumeItems(with(Items.copper, 1, Items.lead, 2));
            consumeFuels(with(Items.coal, 1));

            burnEffect = Fx.coalSmeltsmoke;
            updateEffect = ExtendedFx.smeltsmoke;
            craftEffect = ExtendedFx.smeltsmoke;
            craftTime = 45f;
            burnTime = 46f;
        }};

        electrumForge = new GenericCrafter("electrum-forge"){{
            requirements(Category.crafting, with(Items.thorium, 80, Items.silicon, 60, ClassicItems.denseAlloy, 50, Items.lead, 50));
            craftEffect = Fx.smeltsmoke;
            liquidCapacity = 50f;

            outputItem = new ItemStack(ClassicItems.electrum, 1);
            consumeLiquids(LiquidStack.with(Liquids.slag, 4f / 60f));
            consumeItems(with(Items.titanium, 2, ClassicItems.goldPowder, 2));
            consumePower(0.7f);
            drawer = new DrawMulti(new DrawDefault(), new DrawFlame(Color.valueOf("ffc999")));
            craftTime = 60f * 10;
            size = 2;
            health = 90 * size;
        }};

        arcSmelter = new GenericCrafter("arc-smelter"){{
            requirements(Category.crafting, with(Items.copper, 110, ClassicItems.denseAlloy, 70, Items.lead, 50));
            craftEffect = Fx.smeltsmoke;
            outputItem = new ItemStack(ClassicItems.denseAlloy, 2);
            consumeItems(with(Items.copper, 2, Items.lead, 4));
            consumePower(0.125f);
            drawer = new DrawMulti(new DrawDefault(), new DrawFlame(Color.valueOf("ffc999")));
            craftTime = 55f;
            size = 2;
            health = 90 * size;
        }};

        crucible = new GenericSmelter("crucible"){{
            requirements(Category.crafting, with(Items.titanium, 50, Items.lead, 50));
            health = 90;
            outputItem = new ItemStack(ClassicItems.dirium, 1);
            consumeItems(with(Items.titanium, 1, Items.lead, 1));
            consumeFuels(with(Items.coal, 1));

            burnEffect = Fx.coalSmeltsmoke;
            updateEffect = ExtendedFx.smeltsmoke;
            craftEffect = ExtendedFx.smeltsmoke;
            burnTime = 40f;
            craftTime = 20f;
            itemCapacity = 20;
        }};

        stoneSeparator = new Separator("stone-separator"){{
            requirements(Category.crafting, with(Items.copper, 15, ClassicItems.denseAlloy, 15));
            results = with(
                    Items.sand, 10,
                    ClassicItems.stone, 9,
                    Items.copper, 4,
                    Items.lead, 2,
                    Items.coal, 2,
                    Items.titanium, 1
            );
            hasPower = false;
            craftTime = 40f;
            size = 1;

            consumeItems(with(ClassicItems.stone, 2));
            consumeLiquid(Liquids.water, 0.3f / 40f);

            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(), new DrawRegion("-spinner", 3, true), new DrawDefault());
        }};

        centrifuge = new Separator("centrifuge"){{
            requirements(Category.crafting, with(Items.copper, 50, ClassicItems.denseAlloy, 50, Items.titanium, 25));
            results = with(
                    Items.sand, 12,
                    ClassicItems.stone, 11,
                    Items.copper, 5,
                    Items.lead, 3,
                    Items.coal, 3,
                    Items.titanium, 2,
                    Items.thorium, 1
            );
            hasPower = true;
            craftTime = 15f;
            size = 2;

            consumeItems(with(ClassicItems.stone, 2));
            consumeLiquid(Liquids.water, 0.5f / 40f);
            consumePower(0.2f);

            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(), new DrawRegion("-spinner", 3, true), new DrawDefault());
        }};

        heatReactor = new HeatProducer("heat-reactor"){{
            requirements(Category.crafting, with(Items.oxide, 70, Items.graphite, 20, Items.carbide, 10, Items.thorium, 80));
            size = 3;
            craftTime = 60f * 10f;

            craftEffect = new RadialEffect(Fx.heatReactorSmoke, 4, 90f, 7f);

            itemCapacity = 20;
            consumeItem(Items.thorium, 3);
            consumeLiquid(Liquids.nitrogen, 1f / 60f);
            outputItem = new ItemStack(Items.fissileMatter, 1);
        }};


        cellSynthesisChamber = new GenericCrafter("cell-synthesis-chamber") {{
            requirements(Category.crafting, with(Items.thorium, 75, Items.phaseFabric, 95, Items.tungsten, 155, Items.surgeAlloy, 65));
            outputLiquid = new LiquidStack(Liquids.neoplasm, 0.4f);

            size = 3;
            hasPower = true;
            hasItems = true;
            hasLiquids = true;
            rotate = false;
            solid = true;
            outputsLiquid = true;
            squareSprite = false;

            liquidCapacity = 30f;

            consumePower(2f);
            consumeItems(with(Items.carbide, 3, Items.phaseFabric, 1));
            consumeLiquids(LiquidStack.with(Liquids.cyanogen, 0.8f));

            var Cells = new DrawCells() {{
                color = Color.valueOf("9e172c");
                particleColorFrom = Color.valueOf("9e172c");
                particleColorTo = Color.valueOf("f98f4a");
                radius = 2.5f;
                lifetime = 1400f;
                recurrence = 2f;
                particles = 20;
                range = 3f;
            }};

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(Liquids.cyanogen, 5f),
                    new DrawLiquidTile(Liquids.neoplasm, 5f),
                    Cells,
                    new DrawDefault()
            );
        }};

        slagCentrifuge = new GenericCrafter("slag-centrifuge"){{
            requirements(Category.crafting, with(Items.carbide, 70, Items.graphite, 60, Items.silicon, 40, Items.oxide, 40));

            consumePower(40f / 60f);

            size = 3;
            consumeItem(Items.sand, 1);
            consumeLiquid(Liquids.slag, 40f / 60f);
            liquidCapacity = 80f;

            hasLiquids = true;
            hasItems = true;
            hasPower = true;
            squareSprite = false;

            var drawers = Seq.with(new DrawRegion("-bottom"), new DrawLiquidRegion(Liquids.slag){{ alpha = 0.7f; }});

            for(int i = 0; i < 5; i++){
                int fi = i;
                drawers.add(new DrawGlowRegion(-1f){{
                    glowIntensity = 0.3f;
                    rotateSpeed = 3f / (1f + fi/1.4f);
                    alpha = 0.4f;
                    color = new Color(1f, 0.5f, 0.5f, 1f);
                }});
            }

            drawer = new DrawMulti(drawers.add(new DrawDefault()));
            //icons() = new String[]{"-bottom", ""};

            craftTime = 60f * 2f;

            outputLiquid = new LiquidStack(Liquids.gallium, 2f/60f);
            outputItem = new ItemStack(Items.scrap, 1);
        }};

        //Nuclear //TODO make it work??? & SPRITE OVERHAUL needed
        /*warheadAssembler = new SingleProducer("warhead-assembler") {{
            requirements(Category.crafting, with(Items.thorium, 100));
            produce = nuclearWarhead;

            size = 3;
            buildSpeed = 0.3f;
        }};

        ballisticSilo = new BallisticSilo("ballistic-silo") {{
            requirements(Category.crafting, with(Items.thorium, 100));
            size = 5;
        }};

        nuclearWarhead = new NuclearWarhead("nuclear-warhead") {{
            requirements(Category.crafting, with(Items.thorium, 40));
            size = 2;
        }};*/

        //Distribution
        surgeDuct = new DuctOvercharge("surge-duct"){{
            requirements(Category.distribution, with(Items.beryllium, 1, Items.surgeAlloy, 1));
            health = 90;
            speed = 3.75f;

            hasPower = true;
            consumesPower = true;
            conductivePower = true;
            baseEfficiency = 1f;

            consumePower(1f / 60f);

            researchCost = with(Items.beryllium, 5, Items.surgeAlloy, 5);
        }};

        electrumConveyor = new Conveyor("electrum-conveyor"){{
            requirements(Category.distribution, with(Items.titanium, 3, ClassicItems.electrum, 3, Items.lead, 3, Items.graphite, 3));
            health = 85;
            buildCostMultiplier = 0.5f;

            speed = 0.12f;
            displayedSpeed = 17.0F;
        }};

        laserConveyor = new InstantBridge("laser-conveyor"){{
            requirements(Category.distribution, with(Items.phaseFabric, 5, ClassicItems.electrum, 4, Items.silicon, 10, Items.graphite, 10));
            range = 18;
            arrowPeriod = 0.9f;
            arrowTimeScl = 2.75f;
            hasPower = true;
            pulse = true;
            envEnabled |= Env.space;
            consumePower(24f/60f);
        }};

        warpGate = new WarpGate("warp-gate"){{
            requirements(Category.distribution, with(Items.titanium, 125, ClassicItems.dirium, 40, Items.silicon, 80, Items.thorium, 50));
            size = 3;
            squareSprite = false;
            powerUse = 1.825f;
        }};

        ductJunction = new DuctJunction("duct-junction"){{
            requirements(Category.distribution, with(Items.beryllium, 2));

            health = 75;
            speed = 4.0F;

            researchCost = ItemStack.with(Items.beryllium, 10);
        }};

        //Neoplasia
        cord = new Cord("cord"){{
            requirements(Category.distribution, with(Items.beryllium, 2));
        }};

        heart = new Heart("heart"){{
            requirements(Category.distribution, with(Items.beryllium, 2));
            source = true;
            size = 3;
            minEfficiency = 8.9999F;
            attribute = Attribute.steam;
            squareSprite = false;

            //buildVisibility = BuildVisibility.hidden;
        }};

        // Drills
        electrumDrill = new Drill("electrum-drill"){{
            requirements(Category.production, with(ClassicItems.electrum, 80, Items.silicon, 40, Items.graphite, 20));
            drillTime = 250;
            size = 3;
            consumePower(2.5f);
            drawRim = true;
            hasPower = true;
            tier = 5;
            updateEffect = Fx.pulverizeRed;
            updateEffectChance = 0.03f;
            drillEffect = Fx.mineHuge;
            rotateSpeed = 6f;
            warmupSpeed = 0.01f;
            
            liquidBoostIntensity = 1.65f;
            consumeLiquid(Liquids.slag, 0.2f).boost();
        }};

        areaExtractor = new Drill("area-extractor"){{
            requirements(Category.production, with(Items.copper, 250, Items.graphite, 180, ClassicItems.denseAlloy, 100, Items.silicon, 90, Items.titanium, 200, Items.thorium, 70, Items.surgeAlloy, 25));
            tier = 5;
            drillTime = 200;
            drawMineItem = false;
            size = 8;
            warmupSpeed = 0.005f;
            updateEffectChance = 0.03f;
            consumePower(4.15f);

            liquidBoostIntensity = 1.6f;
            consumeLiquid(Liquids.water, 0.12f).boost();
        }};

        poweredDrill = new Drill("powered-drill"){{
            requirements(Category.production, with(Items.copper, 22, Items.titanium, 5, ClassicItems.denseAlloy, 5));
            tier = 4;
            drawMineItem = false;
            drillTime = 300;
            size = 2;
            consumePower(0.45f);

            consumeLiquid(Liquids.water, 0.06f).boost();
        }};

        burstDrill = new ImpactDrill("burst-drill"){{
            requirements(Category.production, with(Items.silicon, 60, Items.beryllium, 90, Items.graphite, 50));
            drillTime = 60f * 10f;
            size = 4;
            drawRim = false;
            hasPower = true;
            tier = 6;
            drillEffect = Fx.mineHuge;
            itemCapacity = 30;

            consumePower(3f);
            consumeLiquid(Liquids.water, 0.2f);
        }};

        //Turrets
        scatterSilo = new ScatterSilo("scatter-silo"){{
            requirements(Category.turret, with(ClassicItems.electrum, 30, Items.graphite, 75));
            size = 2;
            range = 120f;
            scaledHealth = 180;

            update = true;
            solid = true;
            hasItems = true;
            configurable = true;
            warmupSpeed = 0.034f * 2.25f;
            ammo(
                    new ItemStack(Items.scrap, 20), new BasicBulletType(8f, 8){{
                        width = 7f;
                        height = 9f;
                        ammoMultiplier = 5f;
                    }},
                    new ItemStack(Items.lead, 10), new BasicBulletType(8f, 12){{
                        width = 7f;
                        height = 9f;
                        ammoMultiplier = 4f;
                    }},
                    new ItemStack(ClassicItems.electrum, 2), new FlakBulletType(8f, 20){{
                        ammoMultiplier = 5f;
                        splashDamage = 50f * 1.5f;
                        splashDamageRadius = 38f;
                        lightning = 2;
                        lightningLength = 7;
                        shootEffect = Fx.shootBig;
                        collidesGround = true;
                        explodeRange = 20f;
                    }}
            );
        }};

        rippleb41 = new ItemTurret("ripple-b41"){{
            requirements(Category.turret, with(ClassicItems.denseAlloy, 300, Items.titanium, 220, Items.thorium, 120));
            ammo(
                    ClassicItems.denseAlloy, new ArtilleryBulletType(3f, 0, "shell"){{
                        hitEffect = Fx.flakExplosion;
                        knockback = 0.8f;
                        lifetime = 50f;
                        width = height = 11f;
                        collidesTiles = false;
                        splashDamageRadius = 25f;
                        splashDamage = 33f;
                    }},

                    Items.silicon, new ArtilleryBulletType(3f, 0, "shell"){{
                        hitEffect = Fx.flakExplosion;
                        knockback = 0.8f;
                        lifetime = 45f;
                        width = height = 11f;
                        collidesTiles = false;
                        splashDamageRadius = 25f;
                        splashDamage = 33f;
                        homingPower = 2f * 0.08f;
                        homingRange = 50f;
                        reloadMultiplier = 0.9f;
                    }},
                    Items.pyratite, new ArtilleryBulletType(3f, 0, "shell"){{
                        hitEffect = Fx.blastExplosion;
                        knockback = 0.8f;
                        lifetime = 60f;
                        width = height = 13f;
                        collidesTiles = false;
                        splashDamageRadius = 25f;
                        splashDamage = 30f;
                        incendAmount = 4;
                        incendSpread = 11f;
                        frontColor = Pal.lightishOrange;
                        backColor = Pal.lightOrange;
                        trailEffect = Fx.incendTrail;
                        reloadMultiplier = 1.2f;
                    }},
                    Items.blastCompound, new ArtilleryBulletType(2f, 20, "shell"){{
                        hitEffect = Fx.blastExplosion;
                        knockback = 0.8f;
                        lifetime = 70f;
                        width = height = 14f;
                        collidesTiles = false;
                        splashDamageRadius = 45f;
                        splashDamage = 50f;
                        backColor = Pal.missileYellowBack;
                        frontColor = Pal.missileYellow;
                        reloadMultiplier = 1.6f;

                        status = StatusEffects.blasted;
                    }},
                    Items.plastanium, new ArtilleryBulletType(3.3f, 0, "shell"){{
                        hitEffect = Fx.plasticExplosion;
                        knockback = 1f;
                        lifetime = 55f;
                        width = height = 13f;
                        collidesTiles = false;
                        splashDamageRadius = 35f;
                        splashDamage = 35f;
                        fragBullets = 9;
                        fragBullet = new BasicBulletType(2.5f, 10, "bullet"){{
                            width = 10f;
                            height = 12f;
                            shrinkX = shrinkY = 1f;
                            lifetime = 15f;
                            backColor = Pal.plastaniumBack;
                            frontColor = Pal.plastaniumFront;
                        }};
                        backColor = Pal.plastaniumBack;
                        frontColor = Pal.plastaniumFront;
                        reloadMultiplier = 1.9f;
                    }}
            );

            targetAir = false;
            size = 3;
            shoot.shots = 4;
            inaccuracy = 12f;
            reload = 60f;
            ammoEjectBack = 5f;
            ammoUseEffect = ExtendedFx.shellEjectBig;
            ammoPerShot = 2;
            velocityRnd = 0.2f;



            recoil = 6f;
            shake = 2f;

            range = 300f;
            minRange = 50f;
            coolant = consumeCoolant(0.3f);

            scaledHealth = 130;
            shootSound = Sounds.artillery;
        }};

        cycloneb57 = new ItemTurret("cyclone-b57"){{
            requirements(Category.turret, with(Items.copper, 200, Items.titanium, 125, Items.plastanium, 80));
            ammo(
                    Items.lead, new BasicBulletType(3f, 5){{
                        width = 7f;
                        height = 9f;
                    }},

                    ClassicItems.denseAlloy, new BasicBulletType(5f, 5){{
                        ammoMultiplier = 0.8f;
                        width = 7f;
                        height = 9f;
                        reloadMultiplier = 1.5f;
                    }},

                    Items.surgeAlloy, new FlakBulletType(4.5f, 13){{
                        ammoMultiplier = 5f;
                        splashDamage = 50f * 1.5f;
                        splashDamageRadius = 38f;
                        lightning = 2;
                        lightningLength = 7;
                        shootEffect = Fx.shootBig;
                        collidesGround = true;
                        explodeRange = 20f;
                    }}
            );
            shootY = 10f;

            recoils = 3;

            reload = 8.25f;
            range = 210f;
            size = 3;
            recoil = 1.6f;
            recoilTime = 10;
            rotateSpeed = 10f;
            inaccuracy = 11f;
            shootCone = 30f;
            shootSound = Sounds.shootSnap;
            coolant = consumeCoolant(0.3f);

            scaledHealth = 145;
            limitRange();
        }};

        fuseMKII = new ItemTurretV6("fuse-surge"){{
            requirements(Category.turret, ItemStack.with(Items.copper, 450, Items.graphite, 450, Items.surgeAlloy, 220));

            float brange = range + 10f; //TODO ammo
            ammo(
                    Items.surgeAlloy, fuseShot
            );
            shoot = new ShootSpread(3, 20.0F);
            shootSound = Sounds.shotgun;

            coolant = consumeCoolant(0.35f);
            reload = 40f;
            shake = 4f;
            range = 110f;
            recoil = 5f;
            restitution = 0.1f;
            size = 3;

            health = 165 * size * size;
        }};

        fuseMKI = new ItemTurretV6("fuse-b40"){{
            requirements(Category.turret, with(ClassicItems.copper, 210, ClassicItems.denseAlloy, 190, Items.surgeAlloy, 130));
            ammo(
                    ClassicItems.denseAlloy, fuseShot
            );
            shootSound = Sounds.shotgun;
            reload = 50f;
            shake = 4f;
            range = 80f;
            recoil = 5f;
            restitution = 0.1f;
            size = 3;
        }};

        arcAir = new PowerTurret("arc-air"){{
            requirements(Category.turret, with(Items.copper, 50, Items.lead, 50));
            shootType = arcOld;
            shake = 1f;
            reload = 30f;
            shootCone = 30f;
            rotateSpeed = 7f;
            targetAir = true;
            targetGround = false;
            range = 80f;
            shootEffect = Fx.lightningShoot;
            heatColor = Color.red;
            recoil = 1f;
            size = 1;
            health = 260;
            shootSound = Sounds.spark;
            consumePower(3.3f);
            coolant = consumeCoolant(0.1f);
        }};

        teslaTurret = new PowerTurret("tesla-turret"){{
            requirements(Category.turret, with(Items.lead, 50, Items.titanium, 35, Items.thorium, 15, ClassicItems.dirium, 15));
            range = 92.5f;
            shootCone = 45f;
            shootType = new NewTeslaOrbType(range,26){{
                lightningColor = Pal.lancerLaser;

                hitCap = 5;
                buildingDamageMultiplier = 0.25f;
                status = StatusEffects.shocked;
                statusDuration = 60 * 8f;
            }};
            shootSound = Sounds.spark; //tesla
            smokeEffect = Fx.none;

            shootEffect = Fx.lightningShoot;
            heatColor = Color.red;

            reload = 40f;
            health = 320;
            recoil = 1f;
            consumePower(0.5f * 4f);
            coolant = consumeCoolant(0.1f);
            playerControllable = true;
        }};

        chainTurret = new MirroredItemTurret("chain-turret"){{
            requirements(Category.turret, with(Items.titanium, 50, ClassicItems.dirium, 80, Items.lead, 100));
            //ammo(Items.thorium, chain);
            ammo(
                    Items.thorium, new BasicBulletType(8f, 8){{
                        width = 7f;
                        height = 9f;
                        ammoMultiplier = 2f;
                    }},

                    Items.surgeAlloy, new FlakBulletType(8f, 13){{
                        width = 7f;
                        height = 9f;
                        ammoMultiplier = 5f;
                        splashDamage = 50f * 1.5f;
                        splashDamageRadius = 38f;
                        lightning = 2;
                        lightningLength = 7;
                        shootEffect = Fx.shootBig;
                        collidesGround = true;
                        explodeRange = 20f;

                        reloadMultiplier = 0.85f;
                    }}
            );
            size = 2;
            shootSound = bigshot;
            shootEffect = ExtendedFx.chainshot;
            smokeEffect = Fx.none;
            health = 430;
            shootCone = 9f;
            inaccuracy = 8f;
            range = 208f;
            reload = 5f;

            coolant = consumeCoolant(0.1f);
            //outlineColor = Color.valueOf("ffd86c");
            //outlineRadius = 5;
        }};

        tinyBreach = new ItemTurretV6("tiny-breach"){{
            requirements(Category.turret, with(Items.beryllium, 35, Items.silicon, 20));
            ammo(
                    Items.beryllium, new BasicBulletType(7f, 40){{
                        width = 8f;
                        height = 14f;
                        shootEffect = Fx.colorSpark;
                        smokeEffect = Fx.shootBigSmoke;
                        ammoMultiplier = 1;
                        pierce = true;
                        pierceBuilding = true;
                        hitColor = backColor = trailColor = Pal.berylShot;
                        frontColor = Color.white;
                        trailWidth = 1.5f;
                        trailLength = 10;
                        hitEffect = despawnEffect = Fx.hitBulletColor;
                    }},
                    Items.tungsten, new BasicBulletType(6.6f, 55){{
                        width = 9f;
                        height = 14f;
                        shootEffect = Fx.colorSpark;
                        smokeEffect = Fx.shootBigSmoke;
                        ammoMultiplier = 1;
                        reloadMultiplier = 0.7f;
                        pierce = true;
                        pierceBuilding = true;
                        hitColor = backColor = trailColor = Pal.tungstenShot;
                        frontColor = Color.white;
                        trailWidth = 1.6f;
                        trailLength = 10;
                        hitEffect = despawnEffect = Fx.hitBulletColor;
                        rangeChange = 40f;
                    }}
            );

            drawer = new DrawTurret("reinforced-");
            shootLength = 0f;
            outlineColor = Pal.darkOutline;
            size = 2;
            envEnabled |= Env.space;
            reload = 35f;
            restitution = 0.03f;
            range = 180;
            shootCone = 3f;
            health = 350 * size * size;
            rotateSpeed = 1.6f;
            squareSprite = false;

            limitRange();
        }};

        horde = new ItemTurretV6("horde"){{
            requirements(Category.turret, with(Items.tungsten, 35, Items.silicon, 35));
            shootSound = Sounds.missile;
            ammo(
                    Items.scrap, new MissileBulletType(4.5f, 30){{
                        inaccuracy = 0.25f;
                        shootEffect = Fx.colorSpark;
                        smokeEffect = Fx.shootBigSmoke;
                        ammoMultiplier = 1;
                        hitColor = backColor = trailColor = Color.valueOf("ea8878");
                        frontColor = Color.valueOf("feb380");
                        trailWidth = 2f;
                        trailLength = 12;

                        splashDamage = 30f;
                        splashDamageRadius = 30f;

                        weaveMag = 5;
                        weaveScale = 4;
                        //Inaccuracy = 0.1f;
                        ammoMultiplier = 3f;

                        //TODO different effect?
                        hitEffect = despawnEffect = Fx.blastExplosion;
                    }}
            );

            //acceptCoolant = false;
            consumeLiquid(Liquids.hydrogen, 2.5f / 60f);
            shoot.shots = 9;
            shoot.shotDelay = 4f*1.5f;
            shoot.firstShotDelay = 5f*1.5f;

            //TODO this works but looks bad
            //spread = 0f;
            shootLength = 6.5f;
            shootY = 4f;
            xRand = 11f;
            recoil = 2f;
            squareSprite = false;

            drawer = new DrawTurret("reinforced-"){{
                parts.addAll(new RegionPart("-blade"){{
                    //drawRegion = false;
                    progress = PartProgress.warmup;
                    heatProgress = PartProgress.warmup.blend(PartProgress.recoil, 0.2f);
                    heatColor = Color.valueOf("ff6214");
                    mirror = true;
                    under = false;
                    moveX = -0.5f;
                    moveY = 5f;
                    moves.add(new PartMove(PartProgress.recoil, 0f, -3f, 0f));

                }}, new RegionPart("-mid"){{
                    //drawRegion = false;
                    progress = PartProgress.warmup;
                    heatProgress = PartProgress.warmup.blend(PartProgress.recoil, 0.2f);
                    heatColor = Color.valueOf("ff6214");
                    mirror = false;
                    under = false;
                }}, new RegionPart("-inner"){{
                    drawRegion = true;
                    mirror = true;
                    under = true;
                    moveX = -0.5f;
                    moves.add(new PartMove(PartProgress.recoil, 0f, -3f, 0f));
                }});
            }};
            outlineColor = Pal.darkOutline;
            size = 3;
            envEnabled |= Env.space;
            reload = 60f * 1.5f;
            range = 190;
            shootCone = 15f;
            inaccuracy = 20f;
            health = 300 * size * size;
            rotateSpeed = 3f;

            //???
            //limitRange();
        }};

        fracture = new ItemTurretV6("fracture") {
            {
                requirements(Category.turret, with(Items.tungsten, 35, Items.silicon, 35));
                ammo(
                        Items.tungsten, new ContinuousFlameBulletType(85f) {
                            {
                                length = 105f;
                                shootEffect = Fx.randLifeSpark;
                                width = 4.5f;
                                colors = new Color[]{Color.valueOf("e8e6ff").a(0.55f), Color.valueOf("819aeb").a(0.7f), Color.valueOf("786bed").a(0.8f), Color.valueOf("c3cdfa"), Color.white};
                                smokeEffect = Fx.shootBigSmoke;
                                continuous = false;
                                ammoMultiplier = 3;
                                pierce = true;
                                knockback = 4f;
                                status = StatusEffects.slow;
                                hitColor = Items.tungsten.color;
                                lifetime = 19f;
                                despawnEffect = Fx.none;
                                drawFlare = false;
                                collidesAir = true;
                                Interp in = new Interp.PowIn(1.6f);
                                lengthInterp = f -> in.apply(1f - f);
                                hitEffect = Fx.hitBulletColor;
                            }
                        }
                );

                //acceptCoolant = false; this is old
                coolantMultiplier = 4f;
                coolant = consumeLiquid(Liquids.hydrogen, 3f / 60f);
                shoot.shots = 1;

                //TODO cool reload animation
                drawer = new DrawTurret("reinforced-"){{
                    parts.addAll(new RegionPart("-glow"){{
                        drawRegion = false;
                        heatColor = Color.valueOf("768a9a");
                        //useReload = false;
                        //useProgressHeat = true;
                        heatProgress = PartProgress.heat.blend(PartProgress.warmup, 0.5f);
                    }});
                }};
                shootY = -3f;
                shootSound = Sounds.shootAltLong;
                shake = 1f;
                shootLength = 5f;
                outlineColor = Pal.darkOutline;
                size = 2;
                envEnabled |= Env.scorching;
                reload = 25f/1.6f;
                restitution = 0.1f;
                recoil = 2.5f;
                range = 90;
                shootCone = 15f;
                inaccuracy = 0f;
                health = 300 * size * size;
                rotateSpeed = 3f;
                squareSprite = false;
            }
        };

        chrome = new ItemTurretV6("chrome"){{
            //TODO requirements
            requirements(Category.turret, with(Items.carbide, 120, Items.surgeAlloy, 80, Items.silicon, 80, Items.beryllium, 120));

            ammo(
                    //TODO ammo types to be defined later
                    Items.fissileMatter, new ArtilleryBulletType(3f, 315, "shell"){{
                        hitEffect = new MultiEffect(Fx.titanExplosion, Fx.titanSmoke);
                        despawnEffect = Fx.none;
                        knockback = 1.5f;
                        lifetime = 140f;
                        height = 16f;
                        width = 14.2f;
                        ammoMultiplier = 4f;
                        splashDamageRadius = 60f;
                        splashDamage = 315f;
                        backColor = hitColor = trailColor = Pal.berylShot;
                        frontColor = Color.valueOf("f0ffde");
                        hitSound = Sounds.titanExplosion;

                        status = StatusEffects.blasted;

                        trailLength = 32;
                        trailWidth = 2.64f;
                        trailSinScl = 2.5f;
                        trailSinMag = 1f;
                        trailEffect = Fx.none;
                        trailColor = backColor;
                        despawnShake = 7f;

                        //TODO better shoot
                        shootEffect = Fx.shootTitan;
                        smokeEffect = Fx.shootSmokeTitan;

                        //does the trail need to shrink?
                        trailInterp = v -> Math.max(Mathf.slope(v), 0.8f);
                        shrinkX = 0.2f;
                        shrinkY = 0.1f;
                        buildingDamageMultiplier = 0.7f;
                    }}
                    /*Items.thorium, new ArtilleryBulletType(2.2f, 300, "shell"){{
                        hitEffect = new MultiEffect(Fx.titanExplosion, Fx.titanSmoke);
                        despawnEffect = Fx.none;
                        knockback = 2f;
                        lifetime = 140f;
                        height = 19f;
                        width = 17f;
                        splashDamageRadius = 50f;
                        splashDamage = 300f;
                        scaledSplashDamage = true;
                        backColor = hitColor = trailColor = Color.valueOf("ea8878").lerp(Pal.redLight, 0.5f);
                        frontColor = Color.white;
                        ammoMultiplier = 1f;
                        hitSound = Sounds.titanExplosion;

                        status = StatusEffects.blasted;

                        trailLength = 32;
                        trailWidth = 3.35f;
                        trailSinScl = 2.5f;
                        trailSinMag = 0.5f;
                        trailEffect = Fx.none;
                        despawnShake = 7f;

                        shootEffect = Fx.shootTitan;
                        smokeEffect = Fx.shootSmokeTitan;

                        trailInterp = v -> Math.max(Mathf.slope(v), 0.8f);
                        shrinkX = 0.2f;
                        shrinkY = 0.1f;
                        buildingDamageMultiplier = 0.6f;
                    }}*/
            );
            shootSound = Sounds.mediumCannon;

            targetAir = false;
            squareSprite = false;
            shake = 4f;
            recoil = 1f;
            reload = 60f * 2f;
            shootLength = 7f;
            rotateSpeed = 2.5f;

            //acceptCoolant = false;

            drawer = new DrawTurret("reinforced-"){{

                parts.addAll(
                        new RegionPart("-barrel"){{
                            moveY = -5f;
                            heatColor = Color.valueOf("f03b0e");
                            mirror = false;
                            progress = PartProgress.recoil.curve(Interp.pow2In);
                            mirror = false;
                        }},
                        new RegionPart("-side"){{
                            moveY = -1f;
                            moveRot = -40f;
                            moveX = 2f;
                            under = true;
                            heatColor = Pal.berylShot.cpy().mul(1.1f);
                            PartProgress.heat.blend(PartProgress.warmup, 1f);
                            PartProgress.recoil.curve(Interp.pow2Out);
                            mirror = true;

                            heatProgress = PartProgress.warmup;
                            progress = PartProgress.warmup;
                        }}
                );
            }};

            restitution = 0.02f;
            shootWarmupSpeed = 0.08f;

            outlineColor = Pal.darkOutline;

            //coolantMultiplier = 2f;
            consumeLiquid(Liquids.hydrogen, 10f / 60f);

            range = 360f;
            size = 3;
            health = 380 * size * size;
        }};

        // Power
        shineGenerator = new ConsumeGenerator("shine-generator"){{
            requirements(Category.power, with(Items.metaglass, 80, Items.silicon, 60, ClassicItems.electrum, 50));
            size = 2;
            powerProduction = 3f;
            itemDuration = 80f;

            ambientSound = Sounds.smelter;
            ambientSoundVolume = 0.03f;
            generateEffect = Fx.generatespark;

            consume(new ConsumeItemCharged());

            drawer = new DrawMulti(new DrawDefault(), new DrawWarmupRegion());
        }};

        rtgGenerator = new ConsumeGenerator("compacted-rtg-generator"){{
            requirements(Category.power, with(Items.lead, 50, Items.silicon, 15, Items.phaseFabric, 5, ClassicItems.thorium, 10));
            size = 1;
            powerProduction = 4.15f;
            itemDuration = 60 * 20f;
            envEnabled = Env.any;
            generateEffect = Fx.generatespark;

            drawer = new DrawMulti(new DrawDefault(), new DrawWarmupRegion());
            consume(new ConsumeItemRadioactive(0.5f));
        }};

        // Campaign

        /*dataProcessor = new ResearchBlock("data-processor") {{ //Exclusive only to PC TODO compatible Mobile
            alwaysUnlocked = true;
            requirements(Category.effect, BuildVisibility.campaignOnly, ItemStack.with(Items.copper, 350, Items.silicon, 140, Items.lead, 200, Items.titanium, 150));

            size = 3;
        }};*/

        interplanetaryAccelerator = new NewAccelerator("interplanetary-accelerator"){{
            requirements(Category.effect, BuildVisibility.campaignOnly, with(Items.copper, 16000, Items.silicon, 11000, Items.thorium, 13000, Items.titanium, 12000, Items.surgeAlloy, 6000, Items.phaseFabric, 5000));
            researchCostMultiplier = 0.1f;
            size = 7;
            hasPower = true;
            consumePower(10f);
            buildCostMultiplier = 0.5f;
            scaledHealth = 80;
            squareSprite = false;
        }
            @Override
            public TextureRegion[] icons() {
                return new TextureRegion[]{region, Core.atlas.find(name + "-team-" + "sharded")};
            }
        };

        launchPadLarge = new LaunchPad("launch-pad-large"){{
            requirements(Category.effect, BuildVisibility.campaignOnly, with(Items.titanium, 200, Items.silicon, 150, Items.lead, 250, Items.plastanium, 75));
            size = 4;
            itemCapacity = 300;
            launchTime = 60f * 35;
            hasPower = true;
            consumePower(6f);
        }};

        commandCenter = new LegacyCommandCenter("command-center"){{
            requirements(Category.units, with(Items.copper, 200, Items.lead, 250, Items.silicon, 250, Items.graphite, 100));
            size = 2;
            health = size * size * 55;
            }

            @Override
            public TextureRegion[] icons() {
                return new TextureRegion[]{region, Core.atlas.find(name + "-team-" + "sharded")};
            }
        };

        /*payloadLaunchpad = new PayloadLaunchPad("payload-launch-pad"){{ //TODO keep it? or scrap it?
            requirements(Category.effect, BuildVisibility.campaignOnly, with(Items.titanium, 200, Items.silicon, 150, Items.lead, 250, Items.plastanium, 75));
            alwaysUnlocked = true;
            size = 5;
            itemCapacity = 300;
            launchTime = 60f * 35;
            hasPower = true;
            consumePower(6f);
        }};*/

        /*coreSilo = new CoreLauncher("core-silo"){{ //TODO remove
            alwaysUnlocked = true;
            requirements(Category.effect, BuildVisibility.campaignOnly, ItemStack.with(Items.copper, 350, Items.silicon, 140, Items.lead, 200, Items.titanium, 150)); //Items.copper, 350, Items.silicon, 140, Items.lead, 200, Items.titanium, 150
            size = 5;
            itemCapacity = 1000;
            hasPower = true;
            consumePower(4f);
        }};*/

        payloadPropulsionTower = new PayloadMassDriver("payload-propulsion-tower"){{
            requirements(Category.units, with(Items.lead, 1));
            size = 5;
            range = 400f;

            consumePower(3f);
        }};

        // Storage
        massRepository = new StorageBlock("mass-repository"){
            {
                requirements(Category.effect, with(Items.titanium, 340, Items.thorium, 160));
                size = 4;
                itemCapacity = 2200;
                health = 750;
            }

            @Override
            public TextureRegion[] icons() {
                return new TextureRegion[]{region, Core.atlas.find(name + "-team-" + "sharded")};
            }
        };

        electrumVault = new StorageBlock("electrum-vault"){{
                requirements(Category.effect, with(Items.titanium, 60, Items.thorium, 40, ClassicItems.electrum, 20));
                size = 3;
                itemCapacity = 2500;
                health = 2000;
        }};

        reinforcedSafe = new StorageBlock("reinforced-safe"){
            {
                squareSprite = false;
                requirements(Category.effect, with(Items.tungsten, 250, Items.carbide, 125, Items.beryllium, 100));
                size = 4;
                itemCapacity = 1200;
                scaledHealth = 120;
                coreMerge = false;
            }

            @Override
            public TextureRegion[] icons() {
                return new TextureRegion[]{region, Core.atlas.find(name + "-team-" + "sharded")};
            }

        };

        coreAegis = new CoreBlock("core-aegis"){{

            requirements(Category.effect, ItemStack.mult(coreBastion.requirements, 0.5f));

            squareSprite = false;
            unitType = UnitTypes.evoke;
            health = coreBastion.health / 2;
            itemCapacity = coreBastion.itemCapacity / 2; //TODO more or less?
            size = 3;

            unitCapModifier = 10;
        }

            @Override
            public TextureRegion[] icons() {
                return new TextureRegion[]{region, Core.atlas.find(name + "-team-" + "sharded")};
            }

        };
    }
}
