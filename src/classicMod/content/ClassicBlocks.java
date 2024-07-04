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
import mindustry.world.blocks.payloads.BlockProducer;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static classicMod.content.ClassicBullets.*;
import static classicMod.content.ClassicSounds.*;
import static classicMod.content.ClassicUnitTypes.*;
import static mindustry.content.Blocks.coreBastion;
import static mindustry.type.ItemStack.*;

public class ClassicBlocks {
    public static Block
    coreSolo, //lonely Core - classic
    titanCannon, chainTurret, plasmaTurret, teslaTurret, sniperTurret, laserTurret, mortarTurret, flameTurret, gattlingTurret, shotgunTurret, doubleTurret, basicTurret, //Turret - classic
    nuclearReactor, //Power - classic
    crucible, steelSmelter, lavaSmelter, stoneFormer, purifierCoal, purifierTitanium, //Production - classic
    teleporter, tunnelBridge, //Distribution - classic //TODO conveyor belt
    pumpBasic, pumpFlux, //Liquids - classic
    stoneDrill, ironDrill, uraniumDrill, titaniumDrill, coalDrill, omniDrill, //SingleDrill - classic
    ironOre, uraniumOre, //Ore - classic
    lavaLiq, //Liquid - classic
    wallStone, wallIron, wallSteel, wallDirium, wallComposite, wallDiriumLarge, wallSteelLarge, //Wall - classic


    warpGate, //Distribution [v4]
    melter, denseSmelter, arcSmelter,  smolSeparator, //Production [v4]
    fuseMKII, fuseMKI, salvoAlpha, arcAir, cycloneb57, //Turret [v4]
    plasmaDrill, //Drills [v4]


    dartPad, omegaPad, deltaPad, alphaPad, tauPad, javelinPad, tridentPad, glaivePad, //Mech Pad [v5]
    wraithFactory, ghoulFactory, revenantFactory, //Air - Unit Factory [v5]
    crawlerFactory, daggerFactory, titanFactory, fortressFactory, //Ground - Unit Factory [v5]
    draugFactory, spiritFactory, phantomFactory, //Support - Unit Factory [v5]

    insulatorWall, insulatorWallLarge, //Wall - Insulator - Testing-candidate [v6-dev]
    launchPadLarge, coreSilo, //Launchpad - Campaign only - v6-dev Block

    dataProcessor, //Research Block - Campaign only - v6-dev Block

    thermalPump, //Pump - Old Content [v6]

    warheadAssembler, ballisticSilo, nuclearWarhead, //Nuclear - Prototype [v7-dev] TODO make it work
    shieldProjector, shieldBreaker, largeShieldProjector, barrierProjector, //Projectors - Erekir - Prototype [v7-dev]
    heatReactor, //Heat Producers - Erekir - Prototype [v7-dev]
    //cellSynthesisChamber, //Liquid Converter - Erekir - Prototype [v7-dev]
    slagCentrifuge, cellSynthesisChamber, //Generic Crafters - Erekir - Prototype [v7-dev]
    reinforcedSafe, coreAegis, //Storage - Erekir - Prototype [v7-dev]
    surgeDuct, //Distribution - Duct - Prototype [v7-dev]
    burstDrill, //Distribution - Duct - Prototype [v7-dev]

    droneCenter, payloadLaunchpad, commandCenter, //TEMPORARY TESTING

    fracture, horde, chrome, tinyBreach, //Turrets - Erekir - Prototype [v7-dev]

    interplanetaryAccelerator //Endgame - Mindustry
    ;



    public void loadClassic(){
        coreSolo = new CoreBlockClassic("core-solo"){{
            health = 120;
            unitType = alpha;
            isFirstTier = true;
            infinityCapacity = true;
            alwaysUnlocked = true;
        }};

        tunnelBridge = new ModifiedDuctBridge("conveyor-tunnel"){{
            requirements(Category.distribution, with(ClassicItems.iron, 2));
            health = 70;
            range = 3;
        }

            @Override
            public void load() {
                super.load();
                bridgeRegion = Core.atlas.find("restored-mind-nullTexture");
                region = Core.atlas.find("restored-mind-conveyor-tunnel");
                bridgeBotRegion = Core.atlas.find("restored-mind-nullTexture");
                bridgeLiquidRegion = Core.atlas.find("restored-mind-nullTexture");
                arrowRegion = Core.atlas.find("restored-mind-nullTexture");
                dirRegion = Core.atlas.find("restored-mind-nullTexture");
            }

            @Override
            public TextureRegion[] icons() {
                return new TextureRegion[]{region};
            }
        };

        purifierCoal = new Purifier("coal-extractor"){{
            requirements(Category.production, with(ClassicItems.iron, 10, ClassicItems.steel, 10));
            health = 65;
            craftTime = 50f;
            consumeLiquid(Liquids.water, 18.99f/60);
            consumeItems(with(ClassicItems.stone, 5));
            outputItem = new ItemStack(Items.coal, 1);
        }};

        purifierTitanium = new Purifier("titanium-extractor"){{
            requirements(Category.production, with(ClassicItems.iron, 30, ClassicItems.steel, 30));
            health = 80;
            craftTime = 60f;
            consumeLiquid(Liquids.water, 40f/60);
            consumeItems(with(ClassicItems.iron, 7));
            outputItem = new ItemStack(Items.titanium, 1);
        }};

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

        pumpBasic = new Pump("basic-pump"){{
            requirements(Category.production, with(ClassicItems.steel, 10));
            health = 60;
            pumpAmount = 0.2f;
        }};
        pumpFlux = new Pump("flux-pump"){{
            requirements(Category.production, with(ClassicItems.steel, 10, ClassicItems.dirium, 10));
            health = 100;
            pumpAmount = 0.5f;
        }};

        stoneDrill = new SingleDrill("stone-drill"){{
            requirements(Category.production, with(ClassicItems.stone, 12));
            drillTime = 4*60;
            health = 40;
            requiredItem = ClassicItems.stone;
        }};

        ironDrill = new SingleDrill("iron-drill"){{
            requirements(Category.production, with(ClassicItems.stone, 25));
            drillTime = 5*60;
            health = 40;
            requiredItem = ClassicItems.iron;
            drawIconItem = true;
        }};

        uraniumDrill = new SingleDrill("uranium-drill"){{
            requirements(Category.production, with(ClassicItems.iron, 40, ClassicItems.steel, 40));
            drillTime = 7*60;
            health = 40;
            requiredItem = ClassicItems.uranium;
            drawIconItem = true;
        }};

        titaniumDrill = new SingleDrill("titanium-drill"){{
            requirements(Category.production, with(ClassicItems.iron, 50, ClassicItems.steel, 50));
            drillTime = 7*60;
            health = 40;
            requiredItem = Items.titanium;
            drawIconItem = true;
        }};

        coalDrill = new SingleDrill("coal-drill"){{
            requirements(Category.production, with(ClassicItems.stone, 25, ClassicItems.iron, 40));
            drillTime = 6*60;
            health = 40;
            requiredItem = Items.coal;
            drawIconItem = true;
        }};

        omniDrill = new Drill("omni-drill"){
            {
                requirements(Category.production, with(Items.titanium, 40, ClassicItems.dirium, 40));
                health = 40;
                drillTime = 4*60;
                drillEffect = ExtendedFx.spark;
                //acceptedItems = new Item[]{Items.titanium, Items.coal, ClassicItems.iron, ClassicItems.uranium, ClassicItems.stone};
                tier = ClassicItems.uranium.hardness;
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
                topRegion = Core.atlas.find("restored-mind-omni-rim");
            }
        };



        titanCannon = new ItemTurret("titan-cannon"){{
            requirements(Category.turret, with(Items.titanium, 50 * size, ClassicItems.dirium, 55 * size, ClassicItems.steel, 70 * size));
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
            requirements(Category.turret, with(Items.titanium, 25 * size, ClassicItems.dirium, 40 * size, ClassicItems.steel, 50 * size));
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
            health = 280;
            outlineColor = Color.valueOf("ffd86c");
            outlineRadius = 4;
        }};

        teslaTurret = new PowerTurret("tesla-turret"){{
            requirements(Category.turret, with(Items.titanium, 25, ClassicItems.dirium, 15, ClassicItems.steel, 20));
            range = 85.5f;
            shootType = new NewTeslaOrbType(56f,13, 5);
            shootSound = tesla;
            shootEffect = Fx.none;
            smokeEffect = Fx.none;
            reload = 15f;
            health = 240;
            outlineColor = Color.valueOf("ffd86c");
            outlineRadius = 4;
            consumePower(0.5f * 4f);
            playerControllable = false;
        }};

        mortarTurret = new ItemTurret("mortar-turret"){{
            requirements(Category.turret, with(Items.titanium, 25, ClassicItems.steel, 15));
            range = 180f;
            rotateSpeed = 0.2f*45;
            ammo(Items.coal, flakClassic);
            shootSound = bigshot;
            shootEffect = ExtendedFx.mortarshot;
            smokeEffect = Fx.none;
            reload = 55f;
            shoot.shots = 3;
            health = 170;
            outlineColor = Color.valueOf("6d5bec");
        }};

        laserTurret = new PowerTurret("laser-turret"){{
            requirements(Category.turret, with(Items.titanium, 12, ClassicItems.steel, 12));
            range = 74.5f;
            shootType = new LaserOnTargetType(range, 10, Color.sky, 60f * 1.5f);
            shootSound = lazerShot;
            shootEffect = Fx.none;
            smokeEffect = Fx.none;
            reload = 8f;
            health = 150;
            consumePower(0.2f * 4f);
            outlineColor = Color.valueOf("6d5bec");
            playerControllable = false;
        }};

        flameTurret = new ItemTurret("flame-turret"){{
            requirements(Category.turret, with(ClassicItems.iron, 12, ClassicItems.steel, 8));
            range = 65f;
            ammo(Items.coal, flameClassic);
            shootSound = shootDefault;
            shootEffect = Fx.none;
            smokeEffect = Fx.none;
            reload = 1f;
            health = 135;
            outlineColor = Color.valueOf("8b4aa9");
        }};

        sniperTurret = new ItemTurret("sniper-turret"){{
            requirements(Category.turret, with(ClassicItems.iron, 15, ClassicItems.steel, 15));
            range = 175f;
            ammo(ClassicItems.steel, sniper);
            shootSound = railshot;
            shootEffect = ExtendedFx.railshot;
            smokeEffect = Fx.none;
            reload = 50f;
            health = 115;
            outlineColor = Color.valueOf("8b4aa9");
        }};

        shotgunTurret = new ItemTurret("shotgun-turret"){{
            shoot = new ShootSpread(5, 15f);
            inaccuracy = 15f;
            requirements(Category.turret, with(ClassicItems.iron, 12, ClassicItems.stone, 10));
            range = 105f;
            ammo(ClassicItems.iron, iron);
            shootSound = shootDefault;
            shootEffect = Fx.none;
            smokeEffect = Fx.none;
            reload = 30f;
            health = 100;
            outlineColor = Color.valueOf("c4593b");
        }};

        gattlingTurret = new ItemTurret("machine-turret"){{
            requirements(Category.turret, with(ClassicItems.iron, 8, ClassicItems.stone, 10));
            range = 97.5f;
            ammo(ClassicItems.iron, iron);
            shootSound = shootDefault;
            shootEffect = Fx.none;
            smokeEffect = Fx.none;
            reload = 7f;
            health = 90;
            outlineColor = Color.valueOf("c4593b");
        }};

        doubleTurret = new ItemTurret("double-turret"){{
            requirements(Category.turret, with(ClassicItems.stone, 7));
            shootSound = shootDefault;
            shootEffect = Fx.none;
            smokeEffect = Fx.none;
            range = 95.5f;
            reload = 9.15f;
            health = 75;
            outlineColor = Color.valueOf("1869a7");
            outlineRadius = 4;
            shoot = new ShootAlternate(1.5f);
            ammo(ClassicItems.stone, stone);
        }};

        basicTurret = new ItemTurret("basic-turret"){{
            requirements(Category.turret, with(ClassicItems.stone, 4));
            shootSound = shootDefault;
            shootEffect = Fx.none;
            smokeEffect = Fx.none;
            range = 103f;
            reload = 15f;
            health = 60;
            outlineColor = Color.valueOf("1869a7");
            outlineRadius = 4;
            ammo(ClassicItems.stone, stone);
        }};

        nuclearReactor = new NuclearReactor("nuclear-reactor"){{
            requirements(Category.power, with(Items.titanium, 40 * size, ClassicItems.dirium, 40 * size, ClassicItems.steel, 50 * size));
            ambientSound = Sounds.hum;
            ambientSoundVolume = 0.24f;
            explodeEffect = ExtendedFx.nuclearShockwave;
            size = 3;
            health = 600 * size;
            itemDuration = 130f;
            powerProduction = 0.45f*60;
            //power = 80f;
            liquidCapacity = 50;

            coolColor = new Color(1, 1, 1, 0f);
            hotColor = Color.valueOf("ff9575a3");

            heating = 0.007f;
            coolantPower = 0.007f*size;
            flashThreshold = 0.46f;

            explosionRadius = 19*size;;
            explosionDamage = 135*size*size;

            fuelItem = ClassicItems.uranium;
            consumeItem(ClassicItems.uranium);
            consumeLiquid(Liquids.water, heating / coolantPower).update(false);
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
            requirements(Category.crafting, with(ClassicItems.stone, 30, ClassicItems.iron, 30, ClassicItems.steel, 50));
            health = 85;
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
            requirements(Category.crafting, with(ClassicItems.stone, 30, ClassicItems.iron, 30, ClassicItems.steel, 55));
            consumeLiquid(ClassicLiquids.lava, 15f/60f);
            outputItem = new ItemStack(ClassicItems.stone, 1);
            health = 80;
            craftTime = 12;
            craftEffect = ExtendedFx.purifystone;
        }};

        steelSmelter = new GenericSmelter("steel-smelter"){{
            requirements(Category.crafting, with(ClassicItems.stone, 40, ClassicItems.iron, 40));
            health = 70;
            outputItem = new ItemStack(ClassicItems.steel, 1);
            consumeItems(with(ClassicItems.iron, 1));
            craftTime = 20f;
            itemCapacity = 20;
        }};

        int wallHealthMultiplier = 4;
        wallStone = new Wall("stone-wall"){{
            requirements(Category.defense, with(ClassicItems.stone, 12));
            health = 40 * wallHealthMultiplier;
        }};
        wallIron = new Wall("iron-wall"){{
            requirements(Category.defense, with(ClassicItems.iron, 12));
            health = 80 * wallHealthMultiplier;
        }};
        wallSteel = new Wall("steel-wall"){{
            requirements(Category.defense, with(ClassicItems.steel, 12));
            health = 110 * wallHealthMultiplier;
        }};
        wallDirium = new Wall("dirium-wall"){{
            requirements(Category.defense, with(ClassicItems.dirium, 12));
            health = 190 * wallHealthMultiplier;
        }};
        wallComposite = new Wall("composite-wall"){{
            requirements(Category.defense, with(ClassicItems.dirium, 12, Items.titanium, 12, ClassicItems.steel, 12));
            health = 270 * wallHealthMultiplier;
        }};

        wallDiriumLarge = new Wall("dirium-wall-large"){{
            requirements(Category.defense, ItemStack.mult(wallDirium.requirements, 4));
            health = 110 * wallHealthMultiplier * 4;
            size = 2;
        }};

        wallSteelLarge = new Wall("steel-wall-large"){{
            requirements(Category.defense, ItemStack.mult(wallSteel.requirements, 4));
            health = 80 * wallHealthMultiplier * 4;
            size = 2;
        }};
    }

    public void loadv4(){
        warpGate = new WarpGate("warp-gate"){
            {
                requirements(Category.distribution, with(ClassicItems.steel, 30, ClassicItems.dirium, 40));
                size = 3;
            }
        };

        /*salvoAlpha = new ItemTurretV6("alpha-salvo"){{
            requirements(Category.turret, with(Items.tungsten, 210, Items.carbide, 190, Items.thorium, 130));
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
        }};*/

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

        smolSeparator= new Separator("small-separator"){{
            requirements(Category.crafting, with(Items.copper, 10, Items.titanium, 5));
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
            consumeLiquid(Liquids.water, 0.3f / 60f);

            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(), new DrawRegion("-spinner", 3, true), new DrawDefault());
        }};

        arcAir = new PowerTurret("arc-air"){{
            requirements(Category.turret, with(Items.copper, 50, Items.lead, 50));
            shootType = arcOld; /*new LightningBulletType(){{
                damage = 20;
                lightningLength = 25;
                collidesAir = false;
                ammoMultiplier = 1f;

                //for visual stats only.
                buildingDamageMultiplier = 0.25f;

                lightningType = arcOld;
            }};*/
            shake = 1f;
            reload = 30f;
            shootCone = 30f;
            rotateSpeed = 7f;
            targetAir = true;
            targetGround = false;
            range = 70f;
            shootEffect = Fx.lightningShoot;
            heatColor = Color.red;
            recoil = 1f;
            size = 1;
            health = 260;
            shootSound = Sounds.spark;
            consumePower(3.3f);
            coolant = consumeCoolant(0.1f);
        }};

        cycloneb57 = new ItemTurret("cyclone-b57"){{
            requirements(Category.turret, with(Items.copper, 200, Items.titanium, 125, Items.plastanium, 80));
            ammo(
                    Items.lead, new BasicBulletType(3f, 5){{
                        width = 7f;
                        height = 9f;
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

    }

    public void load() {

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

        //Pump
        thermalPump = new Pump("thermal-pump"){{
            requirements(Category.liquid, with(Items.copper, 85, Items.metaglass, 90, Items.silicon, 35, Items.titanium, 50, Items.thorium, 45));
            pumpAmount = 0.25f;
            consumePower(1.45f);
            liquidCapacity = 50f;
            hasPower = true;
            size = 3;
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

        shieldBreaker = new ShieldBreaker("shield-breaker"){{ //TODO fix break block bugs
            requirements(Category.effect, with(Items.tungsten, 700, Items.graphite, 620, Items.silicon, 250));
            envEnabled |= Env.space;
            toDestroy = new Block[]{Blocks.shieldProjector, Blocks.largeShieldProjector};

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

        largeShieldProjector = new BaseShield("large-shield-projector"){{
            requirements(Category.effect, ItemStack.mult(shieldProjector.requirements,2));

            size = 4;
            radius = 400f;
            generatedIcons = new TextureRegion[]{Core.atlas.find(name), Core.atlas.find(name + "-team")};

            consumePower(5f);
        }

            @Override
            public TextureRegion[] icons() {
                return new TextureRegion[]{region, Core.atlas.find(name + "-team-" + "sharded")};
            }

        };

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
                    new DrawLiquidTile(Liquids.cyanogen),
                    new DrawLiquidTile(Liquids.neoplasm),
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
        warheadAssembler = new SingleProducer("warhead-assembler") {{
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
        }};

        //Distribution
        surgeDuct = new DuctOvercharge("surge-duct"){{
            requirements(Category.distribution, with(Items.beryllium, 1, Items.surgeAlloy, 1));
            health = 90;
            speed = 3.5f;

            hasPower = true;
            consumesPower = true;
            conductivePower = true;
            baseEfficiency = 1f;
            consumePower(1f / 60f);

            researchCost = with(Items.beryllium, 5, Items.surgeAlloy, 5);
        }};

        //Turrets
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
                            //useReload = false;
                            under = true;
                            heatColor = Pal.berylShot.cpy().mul(1.1f);
                            //useProgressHeat = true;
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

        //Campaign
        droneCenter = new DroneCenterNew("drone-center"){{
            requirements(Category.units, with(Items.tungsten, 150, Items.phaseFabric, 100));

            size = 3;
            statusDuration = 60f * 10f;
            consumePower(3f);

            droneType = effectDrone;
        }};

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
        }};

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

        reinforcedSafe = new StorageBlock("reinforced-safe"){
            {
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
    }
}
