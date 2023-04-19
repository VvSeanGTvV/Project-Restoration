package classicMod.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import classicMod.library.blocks.*;
import classicMod.library.blocks.classicBlocks.*;
import classicMod.library.blocks.customBlocks.*;
import classicMod.library.blocks.legacyBlocks.*;
import classicMod.library.blocks.v6devBlocks.*;
import classicMod.library.bullets.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.campaign.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static classicMod.content.ClassicBullets.*;
import static classicMod.content.ClassicSounds.*;
import static classicMod.content.ClassicUnitTypes.*;
import static classicMod.content.ClassicVars.*;
import static mindustry.type.ItemStack.*;

public class ClassicBlocks {
    public static Block
    titanCannon, chainTurret, plasmaTurret, teslaTurret,//Turret - classic
    nuclearReactor, //Power - classic
    crucible, steelSmelter, lavaSmelter, stoneFormer, //Production - classic
    teleporter, //Distribution - classic
    stoneDrill, ironDrill, uraniumDrill, titaniumDrill, coalDrill, omniDrill, //SingleDrill - classic
    ironOre, uraniumOre, //Ore - classic
    lavaLiq, //Liquid - classic

    warpGate, //Distribution [v4]

    melter, denseSmelter, arcSmelter, //Production [v4]

    fuseMKII, fuseMKI, salvoAlpha, //Turret [v4]

    dartPad, omegaPad, deltaPad, alphaPad, tauPad, javelinPad, tridentPad, glaivePad, //Mech Pad [v5]
    wraithFactory, ghoulFactory, revenantFactory, //Air - Unit Factory [v5]
    crawlerFactory, daggerFactory, titanFactory, fortressFactory, //Ground - Unit Factory [v5]
    draugFactory, spiritFactory, phantomFactory, //Support - Unit Factory [v5]

    insulatorWall, insulatorWallLarge, //Wall - Insulator - Testing-candidate [v6-dev]
    launchPadLarge, coreSilo, //Launchpad - Campaign only - v6-dev Block

    dataProcessor, //Research Block - Campaign only - v6-dev Block

    warheadAssembler, ballisticSilo, nuclearWarhead, //Nuclear - Prototype [v7-dev] TODO what to do with this
    shieldProjector, shieldBreaker, largeShieldProjector, barrierProjector, //Projectors - Erekir - Prototype [v7-dev]
    heatReactor, //Heat Producers - Erekir - Prototype [v7-dev]
    cellSynthesisChamber, //Liquid Converter - Erekir - Prototype [v7-dev]
    slagCentrifuge, //Generic Crafters - Erekir - Prototype [v7-dev]

    droneCenter, payloadLaunchpad, //TEMPORARY TESTING

    fracture, horde, titanold, //Turrets - Erekir - Prototype [v7-dev]

    interplanetaryAccelerator //Endgame - Mindustry
    ;

    public void loadOverride(){
        Blocks.stone.itemDrop = ClassicItems.stone;
        Blocks.stone.playerUnmineable = true;
        Blocks.craters.itemDrop = ClassicItems.stone;
        Blocks.craters.playerUnmineable = true;
    }

    public void load() {
        warpGate = new WarpGate("warp-gate"){
            {
                requirements(Category.distribution, with(ClassicItems.steel, 30, ClassicItems.dirium, 40));
            size = 3;
            }
        };

        lavaLiq = new Floor("molten-lava"){{
            speedMultiplier = 0.15f;
            liquidDrop = ClassicLiquids.lava;
            liquidMultiplier = 0.8f;
            isLiquid = true;
            status = StatusEffects.melting;
            statusDuration = 260f;
            drownTime = 170f;
            cacheLayer = CacheLayer.slag;
            variants = 0;

            attributes.set(Attribute.heat, 0.95f);
        }};
        teleporter = new Teleporter("teleporter"){{
            requirements(Category.distribution, with(ClassicItems.steel, 30, ClassicItems.dirium, 40));
        }};
        ironOre = new OreBlock("iron-ore"){{
           variants = 3;
           itemDrop = ClassicItems.iron;

        }};

        uraniumOre = new OreBlock("uranium-ore"){{
            variants = 3;
            itemDrop = ClassicItems.uranium;
        }};

        stoneDrill = new SingleDrill("stone-drill"){{
            requirements(Category.production, with(ClassicItems.stone, 12));
            drillTime = 4*60;
            health = 40;
            requiredItem = ClassicItems.stone;
            alwaysUnlocked = true;
        }};

        ironDrill = new SingleDrill("iron-drill"){{
            requirements(Category.production, with(ClassicItems.stone, 25));
            drillTime = 5*60;
            health = 40;
            requiredItem = ClassicItems.iron;
        }};

        uraniumDrill = new SingleDrill("uranium-drill"){{
            requirements(Category.production, with(ClassicItems.iron, 40, ClassicItems.steel, 40));
            drillTime = 7*60;
            health = 40;
            requiredItem = ClassicItems.uranium;
        }};

        titaniumDrill = new SingleDrill("titanium-drill"){{
            requirements(Category.production, with(ClassicItems.iron, 50, ClassicItems.steel, 50));
            drillTime = 7*60;
            health = 40;
            requiredItem = Items.titanium;
        }};

        coalDrill = new SingleDrill("coal-drill"){{
            requirements(Category.production, with(ClassicItems.stone, 25, ClassicItems.iron, 40));
            drillTime = 6*60;
            health = 40;
            requiredItem = Items.coal;
        }};

        omniDrill = new Drill("omni-drill"){
            {
                requirements(Category.production, with(Items.titanium, 40, ClassicItems.dirium, 40));
                health = 40;
                drillTime = 4*60;
                drillEffect = ExtendedFx.spark;
                //acceptedItems = new Item[]{Items.titanium, Items.coal, ClassicItems.iron, ClassicItems.uranium, ClassicItems.stone};
                tier = 4;
                drawRim = false;
                drawMineItem = true;
                drawSpinSprite = true;
            }

            @Override
            public void load() {
                super.load();
                itemRegion = Core.atlas.find("restored-mind-drill-middle");
                region = Core.atlas.find("restored-mind-drill-bottom");
                rotatorRegion = Core.atlas.find("restored-mind-drill-rotator");
                topRegion = Core.atlas.find("restored-mind-default-rim");
            }
        };

        steelSmelter = new GenericSmelter("steel-smelter"){{
            requirements(Category.crafting, with(ClassicItems.stone, 40, ClassicItems.iron, 40));
            health = 70;
            outputItem = new ItemStack(ClassicItems.steel, 1);
            consumeItems(with(ClassicItems.iron, 1));
            craftTime = 20f;
            itemCapacity = 20;
        }};

        denseSmelter = new GenericSmelter("dense-smelter"){{
            health = 70;
            requirements(Category.crafting, with(Items.copper, 100));
            outputItem = new ItemStack(ClassicItems.denseAlloy, 1);
            consumeItems(with(Items.copper, 1, Items.lead, 2));
            craftTime = 45f;
            burnTime = 46f;
        }};

        arcSmelter = new GenericCrafter("arc-smelter"){{
            health = 90*size;
            requirements(Category.crafting, with(Items.copper, 110, ClassicItems.denseAlloy, 70, Items.lead, 50));
            craftEffect = ExtendedFx.smeltsmoke;
            outputItem = new ItemStack(ClassicItems.denseAlloy, 1);
            consumeItems(with(Items.copper, 1, Items.lead, 2, Items.sand, 1));
            consumePower(0.1f);
            drawer = new DrawMulti(new DrawDefault(), new DrawFlame(Color.valueOf("ffc999")));
            craftTime = 30f;
            size = 2;
        }};

        crucible = new GenericSmelter("crucible"){{
            requirements(Category.crafting, with(Items.titanium, 50, ClassicItems.steel, 50));
            health = 90;
            outputItem = new ItemStack(ClassicItems.dirium, 1);
            consumeItems(with(Items.titanium, 1, ClassicItems.steel, 1));
            burnTime = 40f;
            craftTime = 20f;
            itemCapacity = 20;
        }};

        melter = new GenericCrafter("melter"){{
            requirements(Category.crafting, with(Items.copper, 30, Items.lead, 30, ClassicItems.denseAlloy, 50));
            health = 200;
            outputLiquid = new LiquidStack(ClassicLiquids.lava, 10f/60f);
            consumeItems(with(ClassicItems.stone, 1));
            consumePower(0.1f);
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(), new DrawDefault());
            craftTime = 10f;
            itemCapacity = 20;
        }};

        lavaSmelter = new GenericCrafter("lava-smelter"){{
            requirements(Category.crafting, with(Items.titanium, 15, ClassicItems.steel, 30));
            consumeLiquid(ClassicLiquids.lava, 35f/60f);
            consumeItem(ClassicItems.iron, 1);
            outputItem = new ItemStack(ClassicItems.steel, 1);
            health = 80;
            craftTime = 30;
            craftEffect = ExtendedFx.purifystone;
        }};
        stoneFormer = new GenericCrafter("stone-former"){{
            requirements(Category.crafting, with(Items.lead, 30, ClassicItems.steel, 30, ClassicItems.denseAlloy, 55));
            consumeLiquid(ClassicLiquids.lava, 15f/60f);
            outputItem = new ItemStack(ClassicItems.stone, 1);
            health = 80;
            craftTime = 12;
            craftEffect = ExtendedFx.purifystone;
        }};

        titanCannon = new ItemTurret("titan-cannon"){{
            requirements(Category.turret, with(Items.titanium, 50*ClassicRequirementsMulti, ClassicItems.dirium, 55*ClassicRequirementsMulti, ClassicItems.steel, 70*ClassicRequirementsMulti));
            ammo(ClassicItems.uranium, titanshell);
            size = 3;
            recoil = 3;
            shake = recoil;
            shootSound = blast;
            shootEffect = ExtendedFx.titanshot;
            smokeEffect = Fx.none;
            health = 800;
            rotateSpeed = 0.07f*45;
            shootCone = 9f;
            range = 300f;
            reload = 23f;
            outlineColor = Color.valueOf("ffd86c");
        }};

        chainTurret = new MirroredItemTurret("chain-turret"){{
            requirements(Category.turret, with(Items.titanium, 25*size, ClassicItems.dirium, 40*size, ClassicItems.steel, 50*size));
            ammo(ClassicItems.uranium, chain);
            size = 2;
            shootSound = bigshot;
            shootEffect = ExtendedFx.chainshot;
            smokeEffect = Fx.none;
            health = 430;
            shootCone = 9f;
            inaccuracy = 8f;
            range = 208f;
            reload = 5f;
            outlineColor = Color.valueOf("ffd86c");
            outlineRadius = 5;
        }};

        plasmaTurret = new ItemTurret("plasma-turret"){{
            requirements(Category.turret, with(Items.titanium, 20, ClassicItems.dirium, 15, ClassicItems.steel, 10));
            shootSound = flame2;
            shootEffect = Fx.none;
            smokeEffect = Fx.none;
            size = 1;
            recoil = 0;
            inaccuracy = 7f;
            range = 69f;
            reload = 3f;
            ammo(Items.coal, plasmaflame);
            health = 180;
            outlineColor = Color.valueOf("ffd86c");
            outlineRadius = 4;
        }};

        teslaTurret = new PowerTurret("tesla-turret"){{ //TODO fix bugs
            requirements(Category.turret, with(Items.titanium, 25, ClassicItems.dirium, 15, Items.metaglass, 20));
            range = 80.5f;
            shootType = new TeslaOrbType(range,13, 0.029f);
            shootSound = tesla;
            shootEffect = Fx.none;
            smokeEffect = Fx.none;
            reload = 15f;
            health = 140;
            outlineColor = Color.valueOf("ffd86c");
            outlineRadius = 4;
            playerControllable = false;
        }};

        nuclearReactor = new NuclearReactor("nuclear-reactor"){{
                requirements(Category.power, with(Items.titanium, 40 * ClassicRequirementsMulti, ClassicItems.dirium, 40 * ClassicRequirementsMulti, Items.metaglass, 50 * ClassicRequirementsMulti));
                ambientSound = Sounds.hum;
                ambientSoundVolume = 0.24f;
                explodeEffect = ExtendedFx.nuclearShockwave;
                size = 3;
                health = 600*ClassicBuff;
                itemDuration = 130f;
                powerProduction = 0.45f*60;
                //power = 80f;
                liquidCapacity = 50;

                coolColor = new Color(1, 1, 1, 0f);
                hotColor = Color.valueOf("ff9575a3");

                heating = 0.007f;
                coolantPower = 0.007f*ClassicBuff;
                flashThreshold = 0.46f;

                explosionRadius = 19*ClassicBuff;;
                explosionDamage = 135*ClassicBuff*ClassicBuff*ClassicBuff;
            
                fuelItem = ClassicItems.uranium;
                consumeItem(ClassicItems.uranium);
                consumeLiquid(Liquids.water, heating / coolantPower).update(false);
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

        salvoAlpha = new ItemTurretV6("alpha-salvo"){{
            requirements(Category.turret, with(ClassicItems.tungstenOld, 210, Items.carbide, 190, Items.thorium, 130));
            ammo(
                    Items.copper,  new BasicBulletType(2.5f, 11){{
                        width = 7f;
                        height = 9f;
                        lifetime = 60f;
                        ammoMultiplier = 2;
                    }},
                    Items.graphite, new BasicBulletType(3.5f, 20){{
                        width = 9f;
                        height = 12f;
                        reloadMultiplier = 0.6f;
                        ammoMultiplier = 4;
                        lifetime = 60f;
                    }},
                    Items.pyratite, new BasicBulletType(3.2f, 18){{
                        width = 10f;
                        height = 12f;
                        frontColor = Pal.lightishOrange;
                        backColor = Pal.lightOrange;
                        status = StatusEffects.burning;
                        hitEffect = new MultiEffect(Fx.hitBulletSmall, Fx.fireHit);

                        ammoMultiplier = 5;

                        splashDamage = 12f;
                        splashDamageRadius = 22f;

                        makeFire = true;
                        lifetime = 60f;
                    }},
                    Items.silicon, new BasicBulletType(3f, 15, "bullet"){{
                        width = 7f;
                        height = 9f;
                        homingPower = 0.1f;
                        reloadMultiplier = 1.5f;
                        ammoMultiplier = 5;
                        lifetime = 60f;
                    }},
                    Items.thorium, new BasicBulletType(4f, 29, "bullet"){{
                        width = 10f;
                        height = 13f;
                        shootEffect = Fx.shootBig;
                        smokeEffect = Fx.shootBigSmoke;
                        ammoMultiplier = 4;
                        lifetime = 60f;
                    }}
            );
            size = 2;
            range = 110f;
            reload = 40f;
            restitution = 0.03f;
            ammoEjectBack = 3f;
            recoil = 3f;
            shake = 2f;
            shootX = -2f;
            shoot.shotDelay = 4;
            shoot.shots = 3;
            ammoUseEffect = ExtendedFx.shellEjectBig;
        }};

        //Mechpad
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
            //maxSpawn = 1;

            consumePower(1.2f);
            //requirement = with(Items.silicon, 40, Items.titanium, 30);
            unitType = ClassicUnitTypes.draug;
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

        //Projectors
        barrierProjector = new DirectionalForceProjector("barrier-projector"){{
            //TODO
            requirements(Category.effect, with(Items.surgeAlloy, 100, Items.silicon, 125));
            size = 3;
            width = 50f;
            length = 36;
            shieldHealth = 2000f;
            cooldownNormal = 3f;
            cooldownBrokenBase = 0.35f;

            consumePower(4f);
        }};

        shieldProjector = new BaseShield("shield-projector"){{
            category = Category.effect;
            requirements(Category.effect, with(Items.tungsten, 130,Items.surgeAlloy, 75, Items.silicon, 110));

            size = 3;
            generatedIcons = new TextureRegion[]{Core.atlas.find(name), Core.atlas.find(name + "-team")};

            consumePower(5f);
        }};

        shieldBreaker = new ShieldBreaker("shield-breaker"){{
            requirements(Category.effect, with(Items.tungsten, 700, Items.graphite, 620, Items.silicon, 250));
            envEnabled |= Env.space;
            toDestroy = new Block[]{Blocks.shieldProjector, Blocks.largeShieldProjector};

            size = 5;
            itemCapacity = 100;
            scaledHealth = 120f;

            consumeItem(Items.tungsten, 100);
        }};

        largeShieldProjector = new BaseShield("large-shield-projector"){{
            requirements(Category.effect, ItemStack.mult(shieldProjector.requirements,2));

            size = 4;
            radius = 400f;
            generatedIcons = new TextureRegion[]{Core.atlas.find(name), Core.atlas.find(name + "-team")};

            consumePower(5f);
        }};

        //Crafting
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

        cellSynthesisChamber = new LiquidConverter("cell-synthesis-chamber") {{
            //TODO find the original requirement and fix, because it is not compatible with erekir!
            requirements(Category.crafting, with(Items.thorium, 100, Items.phaseFabric, 120, Items.titanium, 150, Items.surgeAlloy, 70));
            outputLiquid = new LiquidStack(Liquids.neoplasm, 0.4f);
            ConvertTime = 200f;
            size = 3;
            hasPower = true;
            hasItems = true;
            hasLiquids = true;
            rotate = false;
            solid = true;
            outputsLiquid = true;
            envEnabled |= Env.space; //because it was on erekir lolz
            drawer = new DrawCells() {{
                color = Color.valueOf("9e172c");
                particleColorFrom = Color.valueOf("9e172c");
                particleColorTo = Color.valueOf("f98f4a");
                radius = 2.5f;
                lifetime = 1400f;
                recurrence = 2f;
                particles = 20;
                range = 3f;
            }};
            liquidCapacity = 30f;

            ConvertLiquid = Liquids.water;
            ConvertLiquidAmount = 0.8f;

            //consumeLiquid(Liquids.water, 8f);
            consumePower(2f);
            consumeItems(with(Items.fissileMatter, 3, Items.phaseFabric, 1));

            /* consumes.power(2f);
            consumes.items(with(Items.sporePod, 3, Items.phaseFabric, 1));
            consumes.liquid(Liquids.water, 0.8f);*/
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

        //Nuclear //TODO make it work???
        /*warheadAssembler = new SingleBlockProducer("warhead-assembler") {{
            requirements(Category.crafting, with(Items.thorium, 100));
            result = nuclearWarhead;
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

        //Turrets
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

        horde = new ItemTurretV6("horde"){{
            requirements(Category.turret, with(Items.tungsten, 35, Items.silicon, 35));
            shootSound = Sounds.missile;
            ammo(
                    Items.silicon, new MissileBulletType(4.5f, 30){{
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
                shootSound = Sounds.shootSnap;
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
            }
        };

        titanold = new ItemTurretV6("titan-old"){{
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
                    }},
                    Items.thorium, new ArtilleryBulletType(2.2f, 300, "shell"){{
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
                    }}
            );
            shootSound = Sounds.mediumCannon;

            targetAir = false;
            shake = 4f;
            recoil = 1f;
            reload = 60f * 2f;
            shootLength = 7f;
            rotateSpeed = 2.5f;

            //acceptCoolant = false;

            drawer = new DrawTurret("reinforced-"){{
                Color heatc = Color.valueOf("f03b0e");
                Interp in = Interp.pow2In;

                parts.addAll(
                        new RegionPart("-barrel"){{
                            moveY = -5f;
                            heatColor = heatc;
                            mirror = false;
                            PartProgress.recoil.curve(in);
                            mirror = false;
                        }},
                        new RegionPart("-side"){{
                            moveY = -1f;
                            moveRot = -40f;
                            moveX = 2f;
                            //useReload = false;
                            under = true;
                            heatColor = Pal.berylShot.cpy().mul(1.1f);
                            //useProgressHeat = true;
                            PartProgress.heat.blend(PartProgress.warmup, 1f);
                            PartProgress.recoil.curve(Interp.pow2Out);
                            mirror = true;
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

        //Campaign
        droneCenter = new DroneCenterNew("drone-center"){{
            requirements(Category.units, with(Items.graphite, 10)); //TODO make effect drone work lolz

            size = 3;
            consumePower(3f);

            droneType = effectDrone;
        }};

        if(!Vars.mobile) {
            dataProcessor = new ResearchBlock("data-processor") {{ //Exclusive only to PC TODO compatible Mobile
                alwaysUnlocked = true;
                requirements(Category.effect, BuildVisibility.campaignOnly, ItemStack.with(Items.copper, 350, Items.silicon, 140, Items.lead, 200, Items.titanium, 150));

                size = 3;
            }};
        }

        interplanetaryAccelerator = new NewAccelerator("interplanetary-accelerator"){{
            requirements(Category.effect, BuildVisibility.campaignOnly, with(Items.copper, 16000, Items.silicon, 11000, Items.thorium, 13000, Items.titanium, 12000, Items.surgeAlloy, 6000, Items.phaseFabric, 5000));
            researchCostMultiplier = 0.1f;
            size = 7;
            hasPower = true;
            consumePower(10f);
            buildCostMultiplier = 0.5f;
            scaledHealth = 80;
        }};

        launchPadLarge = new LaunchPad("launch-pad-large"){{
            requirements(Category.effect, BuildVisibility.campaignOnly, with(Items.titanium, 200, Items.silicon, 150, Items.lead, 250, Items.plastanium, 75));
            size = 4;
            itemCapacity = 300;
            launchTime = 60f * 35;
            hasPower = true;
            consumePower(6f);
        }};

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
    }
}
