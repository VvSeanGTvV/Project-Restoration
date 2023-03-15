package classicMod.content;

import arc.graphics.*;
import arc.math.*;
import classicMod.library.blocks.*;
import classicMod.library.blocks.legacyBlocks.*;
import classicMod.library.blocks.v6devBlocks.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.units.*;
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

    warheadAssembler, ballisticSilo, nuclearWarhead, //Nuclear - Prototype [v7-dev]
    barrierProjector, //Projectors - Erekir - Prototype [v7-dev]
    heatReactor, //Heat Producers - Erekir - Prototype [v7-dev]
    cellSynthesisChamber, //Liquid Converter - Erekir - Prototype [v7-dev]

    droneCenter,

    fracture, horde, titanold, //Turrets - Erekir - Prototype [v7-dev]
    shieldProjector, shieldBreaker, largeShieldProjector, //Shield - Erekir - Prototype [v7-dev]

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
        //--- Projectors Region End ---

        //--- Heat Producers Region ---
        heatReactor = new HeatProducer("heat-reactor"){{
            //TODO gas/liquid requirement?
            requirements(Category.crafting, with(Items.oxide, 70, Items.graphite, 20, Items.carbide, 10, Items.thorium, 80));
            size = 3;
            craftTime = 60f * 10f;

            craftEffect = new RadialEffect(Fx.heatReactorSmoke, 4, 90f, 7f);

            itemCapacity = 20;
            consumeItem(Items.thorium, 2);
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
            consumeItems(with(Items.sporePod, 3, Items.phaseFabric, 1));

            /* consumes.power(2f);
            consumes.items(with(Items.sporePod, 3, Items.phaseFabric, 1));
            consumes.liquid(Liquids.water, 0.8f);*/
        }};
        //--- Converter Region End---

        //--- Turrets Region ---
        horde = new ItemTurretV6("horde"){{
            requirements(Category.turret, with(Items.tungsten, 35, Items.silicon, 35));
            ammo(
                    Items.scrap, new MissileBulletType(4.2f, 15){{
                        //velocityInaccuracy = 0.2f;
                        shootEffect = Fx.colorSpark;
                        smokeEffect = Fx.shootBigSmoke;
                        ammoMultiplier = 1;
                        hitColor = backColor = trailColor = Color.valueOf("ea8878");
                        frontColor = Color.valueOf("feb380");
                        trailWidth = 2f;
                        trailLength = 12;

                        splashDamage = 15f;
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
            //TODO
            consumeLiquid(Liquids.hydrogen, 1.5f / 60f);
            shoot.shots = 9;
            shoot.shotDelay = 2f;

            //TODO this works but looks bad
            //spread = 0f;
            shootLength = 6.5f;
            xRand = 13f;
            recoil = 0f;

            drawer = new DrawTurret("reinforced-");
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
                        Items.tungsten, new ContinuousFlameBulletType(45f) {
                            {
                                length = 105f;
                                shootEffect = Fx.randLifeSpark;
                                width = 4.5f;
                                colors = new Color[]{Color.valueOf("e8e6ff").a(0.55f), Color.valueOf("819aeb").a(0.7f), Color.valueOf("786bed").a(0.8f), Color.valueOf("c3cdfa"), Color.white};
                                smokeEffect = Fx.shootBigSmoke;
                                continuous = false;
                                ammoMultiplier = 2;
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
                consumeLiquid(Liquids.hydrogen, 1.5f / 60f);
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
                shake = 1f;
                shootLength = 5f;
                outlineColor = Pal.darkOutline;
                size = 2;
                envEnabled |= Env.scorching;
                reload = 25f;
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
                    Items.fissileMatter, new ArtilleryBulletType(2f, 40, "shell"){{
                        //TODO FX; smoke, shockwave, not green bomb
                        hitEffect = new MultiEffect(Fx.titanExplosion, Fx.titanSmoke);
                        despawnEffect = Fx.none;
                        knockback = 1.5f;
                        lifetime = 140f;
                        height = 16f;
                        width = 14.2f;
                        ammoMultiplier = 4f;
                        splashDamageRadius = 60f;
                        splashDamage = 100f;
                        backColor = hitColor = trailColor = Pal.berylShot;
                        frontColor = Color.valueOf("f0ffde");

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
                    }}
            );

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
                        }},
                        new RegionPart("-side"){{
                            moveY = -1f;
                            rotation = -40f;
                            moveX = 2f;
                            //useReload = false;
                            under = true;
                            heatColor = Pal.berylShot.cpy().mul(1.1f);
                            //useProgressHeat = true;
                            PartProgress.heat.blend(PartProgress.warmup, 1f);
                            PartProgress.recoil.curve(Interp.pow2Out);
                        }}
                );
            }};

            restitution = 0.02f;
            shootWarmupSpeed = 0.08f;

            outlineColor = Pal.darkOutline;

            consumeLiquids(LiquidStack.with(Liquids.hydrogen, 1f / 60f));

            range = 360f;
            size = 3;
        }};

        /*ravage = new ItemTurret("ravage"){{
            requirements(Category.turret, with(Items.beryllium, 150, Items.silicon, 150, Items.carbide, 250, Items.phaseFabric, 100));

            ammo(
                    //this is really lazy
                    Items.surgeAlloy, new BasicBulletType(7f, 250){{
                        width = 16f;
                        hitSize = 7f;
                        height = 20f;
                        shootEffect = new MultiEffect(Fx.shootTitan, Fx.colorSparkBig, new WaveEffect(){{
                            colorFrom = colorTo = Pal.accent;
                            lifetime = 12f;
                            sizeTo = 20f;
                            strokeFrom = 3f;
                            strokeTo = 0.3f;
                        }});
                        smokeEffect = ExtendedFx.shootSmokeRavage;
                        ammoMultiplier = 1;
                        pierceCap = 4;
                        pierce = true;
                        pierceBuilding = true;
                        hitColor = backColor = trailColor = Pal.accent;
                        frontColor = Color.white;
                        trailWidth = 2.8f;
                        trailLength = 9;
                        hitEffect = despawnEffect = Fx.hitBulletBig;
                        buildingDamageMultiplier = 0.3f;

                        //TODO
                        intervalBullet = new LightningBulletType(){{
                            damage = 30;
                            collidesAir = false;
                            ammoMultiplier = 1f;
                            lightningColor = Pal.accent;
                            lightningLength = 5;
                            lightningLengthRand = 10;

                            //for visual stats only.
                            buildingDamageMultiplier = 0.25f;

                            lightningType = new BulletType(0.0001f, 0f){{
                                lifetime = Fx.lightning.lifetime;
                                hitEffect = Fx.hitLancer;
                                despawnEffect = Fx.none;
                                status = StatusEffects.shocked;
                                statusDuration = 10f;
                                hittable = false;
                                lightColor = Color.white;
                                buildingDamageMultiplier = 0.25f;
                            }};
                        }};

                        bulletInterval = 3f;
                    }}
            );

            shoot = new ShootAlternate(){{
                spread = 3.3f;
                barrels = 9;
                shots = 9;
            }};

            minWarmup = 0.99f;
            coolantMultiplier = 6f;

            shake = 2f;
            ammoPerShot = 2;
            drawer = new DrawTurret("reinforced-"){{
                parts.addAll(

                        new RegionPart("-mid"){{
                            heatProgress = PartProgress.heat.blend(PartProgress.warmup, 0.5f);
                            mirror = false;
                        }},
                        new RegionPart("-blade"){{
                            progress = PartProgress.warmup;
                            heatProgress = PartProgress.warmup;
                            mirror = true;
                            moveX = 5.5f;
                            moves.add(new PartMove(PartProgress.recoil, 0f, -3f, 0f));
                        }},
                        new RegionPart("-front"){{
                            progress = PartProgress.warmup;
                            heatProgress = PartProgress.recoil;
                            mirror = true;
                            under = true;
                            moveY = 4f;
                            moveX = 6.5f;
                            moves.add(new PartMove(PartProgress.recoil, 0f, -5.5f, 0f));
                        }},
                        new RegionPart("-back"){{
                            progress = PartProgress.warmup;
                            heatProgress = PartProgress.warmup;
                            mirror = true;
                            under = true;
                            moveX = 5.5f;
                        }},
                        new ShapePart(){{
                            progress = PartProgress.warmup.delay(0.5f);
                            color = Pal.accent;
                            sides = 6;
                            hollow = true;
                            stroke = 0f;
                            strokeTo = 3f;
                            radius = 10f;
                            layer = Layer.effect;
                            y = -15f;
                            rotateSpeed = 2f;
                        }}
                );

                for(int i = 0; i < 3; i++){
                    int fi = i;
                    parts.add(new RegionPart("-blade-bar"){{
                        progress = PartProgress.warmup;
                        heatProgress = PartProgress.warmup;
                        mirror = true;
                        under = true;
                        outline = false;
                        layerOffset = -0.3f;
                        turretHeatLayer = Layer.turret - 0.2f;
                        y = 44f / 4f - fi * 38f / 4f;
                        moveX = 2f;

                        color = Pal.accent;
                    }});
                }

                for(int i = 0; i < 4; i++){
                    int fi = i;
                    parts.add(new RegionPart("-spine"){{
                        progress = PartProgress.warmup.delay(fi / 5f);
                        heatProgress = PartProgress.warmup;
                        mirror = true;
                        under = true;
                        layerOffset = -0.3f;
                        turretHeatLayer = Layer.turret - 0.2f;
                        moveY = -22f / 4f - fi * 3f;
                        moveX = 52f / 4f - fi * 1f + 2f;
                        moveRot = -fi * 30f;

                        color = Pal.accent;
                        moves.add(new PartMove(PartProgress.recoil.delay(fi / 5f), 0f, 0f, 35f));
                    }});
                }
            }};

            shootWarmupSpeed = 0.05f;
            shootY = 15f;
            outlineColor = Pal.darkOutline;
            size = 5;
            envEnabled |= Env.space;
            reload = 100f;
            recoil = 2f;
            range = 300;
            shootCone = 7f;
            scaledHealth = 350;
            rotateSpeed = 1.5f;

            coolant = consume(new ConsumeLiquid(Liquids.water, 15f / 60f));
            limitRange();
        }};*/
        //--- Turrets Region End ---

        //--- Shield Blocks Region ---
        shieldProjector = new BaseShield("shield-projector"){{
            category = Category.effect;
            requirements(Category.effect, with(Items.tungsten, 10));
            //buildVisibility = BuildVisibility.editorOnly;

            size = 3;
            //icons() = TextureRegion[]{Core.atlas.find(name), Core.atlas.find(name + "-team")};

            consumePower(5f);
        }};

        shieldBreaker = new ShieldBreaker("shield-breaker"){{
            requirements(Category.effect, with());
            envEnabled |= Env.space;
            toDestroy = new Block[]{Blocks.shieldProjector, Blocks.largeShieldProjector};

            size = 5;
            itemCapacity = 100;
            scaledHealth = 120f;

            consumeItem(Items.tungsten, 100);
        }};

        largeShieldProjector = new BaseShield("large-shield-projector"){{
            requirements(Category.effect, with());

            size = 4;
            radius = 400f;

            consumePower(5f);
        }};
        //--- Shield Blocks Region End ---

        //--- Drone Center Region ---
        droneCenter = new DroneCenter("drone-center"){{
            requirements(Category.units, with(Items.graphite, 10));

            size = 3;
            consumePower(3f);

            droneType = effectDrone;
        }};
        //--- Drone Center Region End ---

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
