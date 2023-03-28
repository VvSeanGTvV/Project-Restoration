package classicMod.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import classicMod.library.blocks.*;
import classicMod.library.blocks.legacyBlocks.*;
import classicMod.library.blocks.v6devBlocks.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.campaign.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.production.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static classicMod.content.ClassicUnitTypes.*;
import static mindustry.type.ItemStack.*;

public class ClassicBlocks {
    public static Block

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

    droneCenter, payloadLaunchpad,

    fractureSingle, fracture, horde, titanold, //Turrets - Erekir - Prototype [v7-dev]

    interplanetaryAccelerator //Endgame - Mindustry
    ;

    public void load() {
        //--- Mech Pad Region ---
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
        //--- Mech Pad Region End ---

        //--- Unit Factory Region ---
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
        //--- Unit Factory Region ---

        //--- Unit Support Factory Region ---
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
        //--- Unit Support Factory Region End ---

        // --- Unit Ground Factory Region ---
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
        //--- Unit Ground Factory Region End ---

        //--- Wall ---
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
        //--- Wall Region End ---

        //--- Nuclear Region --- //TODO: do this later and work on some missing stuff
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
        //--- Nuclear Region End ---

        //--- Projectors Region ---
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

        //--- Projectors Region End ---

        //--- Heat Producers Region ---
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
        //--- Heat Producers Region End ---

        //--- Converter Region ---
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
        //--- Converter Region End---

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

        //--- Turrets Region ---
        horde = new ItemTurretV6("horde"){{
            requirements(Category.turret, with(Items.tungsten, 35, Items.silicon, 35));
            shootSound = Sounds.missile;
            ammo(
                    Items.scrap, new MissileBulletType(4.5f, 30){{
                        inaccuracy = 0.2f;
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
            shoot.shotDelay = 4f;
            shoot.firstShotDelay = 5f;

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
                    moves.add(new PartMove(PartProgress.recoil, 0f, -2f, 0f));

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

        fracture = new ItemTurretV6("fracture-single") {
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
                shootSound = Sounds.shootBig;
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
        //--- Turrets Region End ---

        //--- Drone Center Region ---
        droneCenter = new DroneCenterNew("drone-center"){{
            requirements(Category.units, with(Items.graphite, 10)); //TODO make effect drone work lolz

            size = 3;
            consumePower(3f);

            droneType = effectDrone;
        }};
        //--- Drone Center Region End ---

        //--- Launchpad Region ---
        /*payloadLaunchpad = new PayloadLaunchPad("payload-launch-pad"){{ //TODO keep it? or scrap it?
            requirements(Category.effect, BuildVisibility.campaignOnly, with(Items.titanium, 200, Items.silicon, 150, Items.lead, 250, Items.plastanium, 75));
            alwaysUnlocked = true;
            size = 5;
            itemCapacity = 300;
            launchTime = 60f * 35;
            hasPower = true;
            consumePower(6f);
        }};*/
        launchPadLarge = new LaunchPad("launch-pad-large"){{
            requirements(Category.effect, BuildVisibility.campaignOnly, with(Items.titanium, 200, Items.silicon, 150, Items.lead, 250, Items.plastanium, 75));
            size = 4;
            itemCapacity = 300;
            launchTime = 60f * 35;
            hasPower = true;
            consumePower(6f);
        }};

        /*coreSilo = new CoreLauncher("core-silo"){{ //TODO make somethin out of this or scrap it
            alwaysUnlocked = true;
            requirements(Category.effect, BuildVisibility.campaignOnly, ItemStack.with(Items.copper, 350, Items.silicon, 140, Items.lead, 200, Items.titanium, 150)); //Items.copper, 350, Items.silicon, 140, Items.lead, 200, Items.titanium, 150
            size = 5;
            itemCapacity = 1000;
            hasPower = true;
            consumePower(4f);
        }};*/

        dataProcessor = new ResearchBlock("data-processor"){{
            alwaysUnlocked = true;
            requirements(Category.effect, BuildVisibility.campaignOnly, ItemStack.with(Items.copper, 350, Items.silicon, 140, Items.lead, 200, Items.titanium, 150));

            size = 3;
        }};
        //--- Launchpad Region End ---

        //--- Endgame ---
        interplanetaryAccelerator = new NewAccelerator("interplanetary-accelerator"){{
            requirements(Category.effect, BuildVisibility.campaignOnly, with(Items.copper, 16000, Items.silicon, 11000, Items.thorium, 13000, Items.titanium, 12000, Items.surgeAlloy, 6000, Items.phaseFabric, 5000));
            researchCostMultiplier = 0.1f;
            size = 7;
            hasPower = true;
            consumePower(10f);
            buildCostMultiplier = 0.5f;
            scaledHealth = 80;
        }};
    }
}
