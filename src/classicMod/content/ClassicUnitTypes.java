package classicMod.content;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import classicMod.library.ability.*;
import classicMod.library.ai.*;
import classicMod.library.animdustry.JumpingUnitType;
import classicMod.library.ai.factoryai.FactoryFlyingAI;
import classicMod.library.ai.factoryai.FactoryGroundAI;
import classicMod.library.advanceContent.TentacleUnitType;
import mindustry.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.entities.part.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.unit.*;
import mindustry.type.weapons.*;

import static arc.struct.SnapshotSeq.*;
import static classicMod.content.ClassicSounds.*;
import static mindustry.Vars.*;

public class ClassicUnitTypes {
    //public static Sound pew = Vars.tree.loadSound("v5_sounds_pew"); //just pew lol

    public static UnitType
    //Classic Old Units
    enemyStandardT1, enemyStandardT2, enemyStandardT3, //TODO should add?
    
    //Mech Region
    halberd, //Mech - Air [v4]
    omega, delta, alpha, tau, //Mech - Ground [v5]
    javelin, trident, glaive, dart, //Mech - Air [v5]

    //Normal Units
    wraith, ghoul, revenant, lich, reaper, //Unit - Air [v5]
    draug, phantom, spirit, //Unit - Air - Support [v5]

    crawler, dagger, titan, fortress, eruptor, chaosArray, eradicator, //Unit - Ground [v5]

    oculon, //Unit - Ground - Prototype [v6-dev]
    cix, //Unit - Legs - Prototype [v6-dev]
    vanguard, //Unit - Naval - Prototype [v7-dev]

    bulwark,
    Oldincite, Oldemanate, //Unit - Core Units - Prototype [v7-dev]
    osc, //Unit - Flying - [v7-dev]

    effectDrone, //Unit - Effect - Prototype [v7-dev]

    azathoth, //Unit - Custom - Old Content [v5]

    alphaChan, crawlerChan, boulderChan, monoChan, octChan, oxynoeChan, quadChan, seiChan, zenithChan //Unit - Old Content [Animdustry]
    ;

    public static void load() {

        alphaChan = new JumpingUnitType("alphachan"){{
            health = 200f;
            hitSize = 10f;

            constructor = MechUnit::create;
        }};

        crawlerChan = new JumpingUnitType("crawlerchan"){{
            health = 150f;
            hitSize = 10f;

            StompColor = Color.valueOf("edadff");

            constructor = MechUnit::create;
        }};

        boulderChan = new JumpingUnitType("boulderchan"){{
            health = 500f;
            hitSize = 10f;

            onlySlide = true; //hehehehehehehehehehheheheheheheheehehehehehehehehehehhehehehehehehehehhehehehehaw
            constructor = MechUnit::create;
        }};

        monoChan = new JumpingUnitType("monochan"){{
            health = 200f;
            hitSize = 10f;

            StompExplosion = true;
            StompColor = Pal.heal;

            healPercent = 10f;
            healRange = 30f;

            constructor = MechUnit::create;
        }};

        octChan = new JumpingUnitType("octchan"){{
            health = 200f;
            hitSize = 10f;

            StompExplosion = true;
            StompColor = Pal.heal;

            healPercent = 12f;
            healRange = 40f;

            constructor = MechUnit::create;
        }};

        oxynoeChan = new JumpingUnitType("oxynoechan"){{
            health = 200f;
            hitSize = 10f;

            StompExplosion = true;
            StompColor = Pal.heal;

            healPercent = 15f;
            healRange = 50f;

            constructor = MechUnit::create;
        }};

        quadChan = new JumpingUnitType("quadchan"){{
            health = 200f;
            hitSize = 10f;

            StompExplosion = true;
            StompColor = Pal.heal;

            healPercent = 12f;
            healRange = 60f;

            constructor = MechUnit::create;
        }};

        seiChan = new JumpingUnitType("seichan"){{
            health = 200f;
            hitSize = 10f;

            StompColor = Color.valueOf("ffa665");

            constructor = MechUnit::create;
        }};

        zenithChan = new JumpingUnitType("zenithchan"){{
            health = 200f;
            hitSize = 10f;

            StompColor = Color.valueOf("ffcc8a");

            constructor = MechUnit::create;
        }};

        azathoth = new TentacleUnitType("azathoth"){{
            outlines = true;
            flying = true;

            accel = 0.1f * 3f;
            rotateSpeed = 2.25f * 3f;
            speed = 3f;
            drag = 2f;

            health = Float.MAX_VALUE;
            hitSize = 50f;

            mineTier = 0;
            buildSpeed = 0f;
            itemCapacity = 0;

            engineColor = Color.valueOf("ffd37f");

            constructor = UnitEntity::create;

            weapons.add(new Weapon("restored-mind-nullTexture"){{
                y = 0f;
                x = 0f;
                mirror = false;
                reload = 12f;

                ejectEffect = ExtendedFx.none;

                bullet = ClassicBullets.modifierBullet;
            }});
        }};

        /*enemyStandardT1 = new ClassicUnitType("standard-enemy-1"){{
            spriteName = "standard-enemy";
            Color.valueOf("ffe451");
        }};

        enemyStandardT2 = new ClassicUnitType("standard-enemy-2"){{
            spriteName = "standard-enemy";
            Color.valueOf("f48e20");
            tier = 2;
        }};

        enemyStandardT3 = new ClassicUnitType("standard-enemy-3"){{
            spriteName = "standard-enemy";
            Color.valueOf("ff6757");
            tier = 3;
        }};*/

        // TODO need more researching with this dude
        halberd = new UnitType("halberd-ship"){{
            outlines = false;
            hitSize = 8f;
            mineTier = 2;
            accel = 0.4f * 3f;
            speed = 3f;
            drag = 0.1f;
            health = 200f;
            flying = true;
            itemCapacity = 10;
            engineColor = Color.valueOf("8582e7");
            buildSpeed = 1f;
            constructor = UnitEntity::create;

            armor = 30f;
            trailColor = Color.valueOf("6e6bcf");

            weapons.add(new Weapon("restored-mind-nullTexture"){{
                y = 3f;
                x = 3f;
                mirror = true;
                layerOffset = -0.0001f;
                reload = 12f;

                ejectEffect = ExtendedFx.shellEjectSmall;

                bullet = ClassicBullets.standardHalberd;
            }});
        }};

        // --- v5 Zone ---
        // --- Mech Region ---
        // --- Ground Units Region ---
        alpha = new UnitType("alpha-mech") {{
            outlines = false;
            mechSideSway = 0f;
            mechFrontSway = 0f;
            hitSize = 8f;
            mineTier = 1;
            mineSpeed = 3f;
            speed = 1.2f;
            drag = 0.09f;
            health = 200f;
            boostMultiplier = 2f;
            engineColor = Pal.lightTrail;
            buildSpeed = 1.1f;
            canBoost = true;
            constructor = MechUnit::create;
            abilities.add(new RegenerationAbility(Time.delta * 0.09f));

            weapons.add(new Weapon("restored-mind-blaster-equip") {{
                outlines = false;
                shootSound = pew;
                x = 0f;
                y = 0f;
                top = true;

                reload = 7.5f;
                alternate = true;
                ejectEffect = ExtendedFx.shellEjectSmall;
                shootX = -2.6f;
                mirror = true;
                bullet = new BasicBulletType(2.5f, 9f) {{ //reformat v5 coding into v7
                    width = 7f;
                    height = 9f;
                    lifetime = 60f;
                    shootEffect = Fx.shootSmall;
                    smokeEffect = Fx.shootSmallSmoke;
                    ammoMultiplier = 2;
                }};
            }});
        }};

        delta = new UnitType("delta-mech") {{
            outlines = false;
            hitSize = 8f;
            mineTier = -1;
            speed = 2f;
            boostMultiplier = 1.4f;
            itemCapacity = 15;
            mechSideSway = 0f;
            mechFrontSway = 0f;
            health = 150f;
            buildSpeed = 0.9f;
            engineColor = Color.valueOf("d3ddff");
            canBoost = true;
            constructor = MechUnit::create;
            abilities.add(new LightLandingAbility(17f)); //Since it doesn't exist in vanilla, so i created it for one.

            weapons.add(new Weapon("restored-mind-shockgun-equip") {{
                outlines = false;
                x = 1.2f;
                y = -1f;
                top = true;

                shake = 2f;
                reload = 25f;
                shoot.shotDelay = 3f;
                alternate = true;
                shoot.shots = 2;
                inaccuracy = 0f;
                ejectEffect = Fx.none;
                shootSound = Sounds.spark;
                ;
                shootX = -2.6f;
                mirror = true;
                bullet = new LightningBulletType() {{ //reformat v5 coding into v7
                    damage = 12f;
                    lifetime = 1f;
                    shootEffect = Fx.hitLancer;
                    smokeEffect = Fx.none;
                    despawnEffect = Fx.none;
                    hitEffect = Fx.hitLancer;
                    keepVelocity = false;
                    lightningColor = Pal.lancerLaser;
                    lightningLengthRand = 30;
                    range = 70f;
                }};
            }});
        }};

        tau = new UnitType("tau-mech") {{
            outlines = false;
            mechSideSway = 0f;
            mechFrontSway = 0f;
            hitSize = 8f;
            mineTier = 4;
            mineSpeed = 3f;
            itemCapacity = 70;
            speed = 1.22f;
            drag = 0.35f;
            boostMultiplier = 1.8f;
            canHeal = true;
            health = 200f;
            buildSpeed = 1.6f;
            engineColor = Pal.heal;
            canBoost = true;
            constructor = MechUnit::create;
            abilities.add(new SurroundRegenAbility(10f, 160f, 60f)); //Just reformat of the v5

            weapons.add(new Weapon("restored-mind-heal-blaster-equip") {{
                outlines = false;
                x = -1f;
                y = 0f;
                top = true;

                reload = 12f;
                recoil = 2f;
                alternate = false;
                inaccuracy = 0f;
                ejectEffect = Fx.none;
                shootSound = pew;
                shootX = -2.6f;
                mirror = true;
                bullet = new BasicBulletType(5.2f, 13f) {{ //reformat v5 coding into v7
                    healPercent = 3f;
                    shootEffect = Fx.shootHeal;
                    smokeEffect = Fx.hitLaser;
                    hitEffect = Fx.hitLaser;
                    despawnEffect = Fx.hitLaser;
                    collidesTeam = true;
                    healEffect = Fx.healBlockFull;
                    sprite = "restored-mind-laser";
                    width = 7f;
                    height = 5f;
                    rotationOffset = 90f; //Sprite rotate cause it is way off lolz.
                    frontColor = Pal.heal;
                    backColor = Pal.heal;
                }};
            }});
        }};

        omega = new UnitType("omega-mech") {{
            outlines = false;
            hitSize = 8f;
            mineTier = 2;
            mineSpeed = 1.5f;
            itemCapacity = 80;
            speed = 0.76f;
            boostMultiplier = 1.7f;
            mechSideSway = 0f;
            mechFrontSway = 0f;
            health = 350f;
            buildSpeed = 1.5f;
            engineColor = Color.valueOf("feb380");
            canBoost = true;
            constructor = MechUnit::create;
            abilities.add(new ArmorAbility(30f)); //Modify armor abilty for 2nd texture (static)

            weapons.add(new Weapon("restored-mind-swarmer-equip") {{
                outlines = false;
                x = -1f;
                y = 0f;
                top = true;

                recoil = 4f;
                reload = 17f;
                shoot.shots = 4;
                inaccuracy = 8f;
                alternate = true;
                ejectEffect = Fx.none;
                shake = 3f;
                shootX = -6f;
                shootSound = Sounds.shootBig;
                mirror = true;

                bullet = new MissileBulletType(2.7f, 12) {{ //adjust the format of v5 for v7
                    despawnSound = Vars.tree.loadSound("v5_sounds_boom");
                    width = 8f;
                    height = 8f;
                    drag = -0.003f;
                    homingRange = 0f;
                    keepVelocity = false;
                    splashDamageRadius = 25f;
                    splashDamage = 10f;
                    lifetime = 120f;
                    trailColor = Color.gray;
                    backColor = Pal.bulletYellowBack;
                    frontColor = Pal.bulletYellow;
                    hitEffect = Fx.blastExplosion;
                    despawnEffect = Fx.blastExplosion;
                    weaveScale = 8f;
                    weaveMag = 2f;
                }};
            }});
        }};
        // --- Ground Units Region End ---

        // --- Air Units Region ---
        dart = new UnitType("dart-ship") {{
            outlines = false;
            hitSize = 8f;
            mineTier = 2;
            speed = 3f;
            accel = 0.05f * 3f;
            drag = 0.034f;
            health = 200f;
            flying = true;
            itemCapacity = 30;
            engineColor = Pal.lightTrail;
            buildSpeed = 1.1f;
            constructor = UnitEntity::create;

            weapons.add(new Weapon("restored-mind-blaster-equip") {{
                x = 0.8f;
                y = -1f;
                top = true;

                reload = 7.5f;
                alternate = true;
                ejectEffect = ExtendedFx.shellEjectSmall;
                mirror = true;
                shootSound = pew;
                shootX = -2.5f;


                bullet = new BasicBulletType(2.5f, 9f) {{ //adjust the format of v5 for v7
                    width = 7f;
                    height = 9f;
                    lifetime = 60f;
                    shootEffect = Fx.shootSmall;
                    smokeEffect = Fx.shootSmallSmoke;
                    ammoMultiplier = 2;
                }};
            }});
        }};

        trident = new UnitType("trident-ship") {{
            outlines = false;
            hitSize = 8f;
            mineTier = 2;
            speed = 4.5f;
            accel = 0.015f * 3f;
            drag = 0.034f;
            health = 250f;
            flying = true;
            itemCapacity = 30;
            engineColor = Color.valueOf("84f491");
            buildSpeed = 2.5f;
            constructor = UnitEntity::create;
            faceTarget = false;

            weapons.add(new Weapon("restored-mind-nullTexture") {{
                x = 0f;
                y = 2f;
                top = true;

                reload = 15f;
                shoot.shotDelay = 1f;
                shoot.shots = 10;
                inaccuracy = 2f;
                alternate = true;
                ejectEffect = Fx.none;
                velocityRnd = 1f;
                inaccuracy = 20f;
                ignoreRotation = true;
                mirror = true;


                bullet = new BombBulletType(16f, 25f) {{ //adjust the format of v5 for v7
                    width = 10f;
                    height = 14f;
                    hitEffect = Fx.flakExplosion;
                    shootEffect = Fx.none;
                    smokeEffect = Fx.none;
                    shootSound = Sounds.artillery;
                }};
            }});
        }};

        glaive = new UnitType("glaive-ship") {{
            outlines = false;
            hitSize = 8f;
            mineTier = 4;
            mineSpeed = 1.3f;
            speed = 6f;
            accel = 0.032f * 3f;
            drag = 0.06f;
            health = 240f;
            itemCapacity = 60;
            engineColor = Color.valueOf("feb380");
            buildSpeed = 1.2f;
            flying = true;
            constructor = UnitEntity::create;

            weapons.add(new Weapon("restored-mind-nullTexture") {{
                x = 2f;
                y = 0f;
                top = true;

                reload = 6.5f;
                inaccuracy = 2f;
                alternate = true;
                ejectEffect = ExtendedFx.shellEjectSmall;
                shootSound = Sounds.shootSnap;
                mirror = true;


                bullet = ClassicBullets.standardGlaive;
            }});
        }};

        javelin = new UnitType("javelin-ship") {{
            outlines = false;
            hitSize = 8f;
            mineTier = -1;
            speed = 6f;
            accel = 0.011f * 3f;
            drag = 0.01f;
            health = 170f;
            engineColor = Color.valueOf("d3ddff");
            flying = true;
            constructor = UnitEntity::create;
            abilities.add(new LightSpeedAbility(10f, 3.6f, 6f)); //Modify armor abilty for 2nd texture (static)

            weapons.add(new Weapon("restored-mind-nullTexture") {{
                x = 2f;
                y = 0.2f;
                top = true;

                reload = 35f;
                shoot.shots = 4;
                inaccuracy = 2f;
                alternate = true;
                ejectEffect = Fx.none;
                velocityRnd = 0.2f;
                shootSound = Sounds.missile;
                mirror = true;

                bullet = ClassicBullets.missileJavelin;
            }});
        }};
        // --- Air Units Region End ---
        // --- Mech Region End ---

        // --- Flying Units Region ---
        wraith = new UnitType("wraith") {{
            outlines = false;
            speed = 1.9f;
            accel = 0.03f * 3f;
            drag = 0.01f;
            flying = true;
            health = 75;
            engineOffset = 5.5f;
            range = 140f;
            circleTarget = true;
            constructor = UnitEntity::create;
            controller = u -> new OldFlyingAI();

            weapons.add(new Weapon("restored-mind-nullTexture") {{
                x = 2f;
                y = 0f;
                top = true;

                reload = 14f;
                alternate = true;
                ejectEffect = ExtendedFx.shellEjectSmall;
                shootSound = Sounds.shoot;
                mirror = true;


                bullet = new BasicBulletType(2.5f, 9f) {{ //adjust the format of v5 for v7
                    width = 7f;
                    height = 9f;
                    lifetime = 60f;
                    shootEffect = Fx.shootSmall;
                    smokeEffect = Fx.shootSmallSmoke;
                    ammoMultiplier = 2;
                }};
            }});
        }};

        ghoul = new UnitType("ghoul") {{
            outlines = false;
            speed = 2.4f;
            accel = 0.02f * 3f;
            drag = 0.01f;
            health = 220;
            engineOffset = 7.8f;
            range = 140f;
            flying = true;
            circleTarget = true;
            targetAir = false;
            constructor = UnitEntity::create;
            controller = u -> new OldFlyingAI();

            weapons.add(new Weapon("restored-mind-nullTexture") {{
                x = 0f;
                y = 0f;
                top = true;

                reload = 6f;

                alternate = true;
                ejectEffect = Fx.none;
                velocityRnd = 1f;
                inaccuracy = 40f;
                ignoreRotation = true;
                shootSound = Sounds.none;
                mirror = true;


                bullet = new BombBulletType(10f, 20f) {{ //adjust the format of v5 for v7
                    width = 9f;
                    height = 13f;
                    hitEffect = Fx.flakExplosion;
                    shootEffect = Fx.none;
                    smokeEffect = Fx.none;
                }};
            }});
        }};

        revenant = new UnitType("revenant") {{
            outlines = false;
            speed = 2f;
            accel = 0.01f * 3f;
            drag = 0.01f;
            hitSize = 20f;
            health = 1000;
            range = 80f;
            flying = true;
            engineOffset = 12f;
            engineSize = 3f;
            constructor = UnitEntity::create;
            controller = u -> new OldFlyingAI();

            weapons.add(new Weapon("restored-mind-revenant-missiles-equip") {{
                x = 9f;
                y = 0f;
                top = true;

                reload = 14f;
                shootCone = 40f;
                rotate = true;
                rotateSpeed = 0.06f * 12f;
                shootSound = Sounds.missile;
                mirror = true;
                inaccuracy = 2f;
                alternate = true;
                ejectEffect = Fx.none;
                velocityRnd = 0.2f;
                shoot.shots = 2;
                reload = 35f;


                bullet = ClassicBullets.OldmissileSwarm;
            }});
        }};

        lich = new UnitType("lich") {{
            outlines = false;
            speed = 0.6f;
            accel = 0.01f * 2f;
            drag = 0.02f;
            hitSize = 20f;
            health = 6000;
            range = 80f;
            flying = true;
            engineOffset = 21;
            engineSize = 5.3f;
            rotateSpeed = 0.06f * 22f;
            constructor = UnitEntity::create;
            controller = u -> new OldFlyingAI();

            weapons.add(new Weapon("restored-mind-lich-missiles-equip") {{
                x = 21f;
                y = 0f;
                top = true;

                reload = 80f;
                alternate = true;
                ejectEffect = Fx.none;
                shootCone = 100f;
                shoot.shots = 16;
                shoot.shotDelay = 2f;
                inaccuracy = 10f;
                velocityRnd = 0.2f;
                rotateSpeed = 0.06f * 12f;
                rotate = true;
                shootSound = Sounds.artillery;
                mirror = true;

                bullet = new MissileBulletType(2.7f, 12f) {{ //adjust the format of v5 for v7
                    width = 8f;
                    height = 8f;
                    drag = -0.003f;
                    homingRange = 60f;
                    homingDelay = 5f;
                    keepVelocity = false;
                    splashDamageRadius = 25f;
                    splashDamage = 10f;
                    lifetime = 60f;
                    trailColor = Pal.unitBack;
                    backColor = Pal.unitBack;
                    frontColor = Pal.unitFront;
                    hitEffect = Fx.blastExplosion;
                    despawnEffect = Fx.blastExplosion;
                    weaveScale = 6f;
                    weaveMag = 1f;
                }};
            }});
        }};

        reaper = new UnitType("reaper") {{
            outlines = false;
            speed = 0.6f;
            accel = 0.01f * 2f;
            drag = 0.02f;
            hitSize = 56f;
            health = 11000;
            range = 80f;
            flying = true;
            engineOffset = 40;
            engineSize = 7.3f;
            rotateSpeed = 0.04f * 22f;
            constructor = UnitEntity::create;
            controller = u -> new OldFlyingAI();

            weapons.add(new Weapon("restored-mind-reaper-gun-equip") {{
                x = 31f;
                y = 0f;
                top = true;

                reload = 5f;
                alternate = true;
                ejectEffect = Fx.none;
                shootCone = 100f;
                rotateSpeed = 0.06f * 12f;
                shake = 1f;
                inaccuracy = 3f;
                rotate = true;
                shootSound = Sounds.shootBig;
                mirror = true;

                bullet = new BasicBulletType(7f, 42f) {{ //adjust the format of v5 for v7
                    width = 15f;
                    height = 21f;
                    shootEffect = Fx.shootBig;
                    range = 165f;
                }};
            }});
        }};

        // --- Flying Units Region (Support) ---
        draug = new UnitType("draug") {{
            outlines = false;
            speed = 1.2f;
            accel = 0.03f * 3f;
            drag = 0.01f;
            health = 80;
            flying = true;
            engineSize = 1.8f;
            engineOffset = 5.7f;
            mineTier = 1;
            constructor = UnitEntity::create;
            controller = u -> new MinerAI();
            mineItems = with(Items.copper, Items.lead);

            //place weapon called: "you have incurred my wrath. prepare to die." plz he need one, he poor
        }};

        spirit = new UnitType("spirit") {{
            outlines = false;
            speed = 1.6f;
            accel = 0.042f * 3f;
            drag = 0.01f;
            flying = true;
            range = 50f;
            health = 100;
            engineSize = 1.8f;
            engineOffset = 5.7f;
            constructor = UnitEntity::create;
            controller = u -> new RepairAI();

            weapons.add(new Weapon("restored-mind-nullTexture") {{
                x = 0f;
                y = 0f;
                top = true;

                reload = 20f;
                alternate = true;
                ejectEffect = Fx.none;
                shootCone = 100f;
                rotateSpeed = 0.06f * 12f;
                recoil = 2f;
                inaccuracy = 3f;
                shootSound = pew;
                mirror = true;

                bullet = new BasicBulletType(5.2f, 15f) {{ //adjust the format of v5 for v7
                    healPercent = 5.5f;
                    shootEffect = Fx.shootHeal;
                    smokeEffect = Fx.hitLaser;
                    hitEffect = Fx.hitLaser;
                    despawnEffect = Fx.hitLaser;
                    collidesTeam = true;
                    healEffect = Fx.healBlockFull;
                    sprite = "restored-mind-laser";
                    width = 7f;
                    height = 5f;
                    rotationOffset = 90f; //Sprite rotate cause it is way off lolz.
                    frontColor = Pal.heal;
                    backColor = Pal.heal;
                }};
            }});
        }};

        phantom = new UnitType("phantom") {{
            outlines = false;
            speed = 1.9f;
            accel = 0.045f * 3f;
            drag = 0.01f;
            flying = true;
            range = 70f;
            itemCapacity = 70;
            health = 100;
            engineSize = 1.8f;
            engineOffset = 5.7f;
            constructor = UnitEntity::create;
            buildSpeed = 0.4f;
            mineItems = with(Items.copper, Items.lead, Items.titanium);
            controller = u -> new BuilderAI();

            weapons.add(new Weapon("restored-mind-nullTexture") {{
                x = 0f;
                y = 0f;
                top = true;

                reload = 10f;
                alternate = true;
                ejectEffect = Fx.none;
                shootCone = 100f;
                rotateSpeed = 0.06f * 12f;
                recoil = 2f;
                inaccuracy = 3f;
                shootSound = pew;
                mirror = true;

                bullet = new BasicBulletType(5.2f, 13f) {{ //adjust the format of v5 for v7
                    healPercent = 3f;
                    shootEffect = Fx.shootHeal;
                    smokeEffect = Fx.hitLaser;
                    hitEffect = Fx.hitLaser;
                    despawnEffect = Fx.hitLaser;
                    collidesTeam = true;
                    healEffect = Fx.healBlockFull;
                    sprite = "restored-mind-laser";
                    width = 7f;
                    height = 5f;
                    rotationOffset = 90f;
                    frontColor = Pal.heal;
                    backColor = Pal.heal;
                }};
            }});
        }};
        // --- Flying Units Region (Support) End ---
        // --- Flying Units Region End ---

        // --- Ground Units Region ---
        crawler = new UnitType("crawler") {{
            outlines = false;
            mechSideSway = 0f;
            mechFrontSway = 0f;
            hitSize = 8f;
            accel = 0.0285f * 6;
            speed = 1.27f * 3;
            drag = 0.4f;
            health = 120f;
            range = 50f;
            canBoost = false;
            constructor = MechUnit::create;
            controller = u -> new SuicideAI();

            weapons.add(new Weapon("restored-mind-nullTexture-equip") {{
                reload = 6f;
                ejectEffect = Fx.none;
                shootSound = Sounds.explosion;

                bullet = new BombBulletType(2f, 3f) {{
                    hitEffect = Fx.pulverize;
                    lifetime = 30f;
                    speed = 1.1f;
                    splashDamageRadius = 55f;
                    instantDisappear = true;
                    splashDamage = 30f;
                    collidesAir = true;
                    collidesGround = true;

                    killShooter = true;
                }};
            }});
        }};

        dagger = new UnitType("dagger") {{
            outlines = false;
            mechSideSway = 0f;
            mechFrontSway = 0f;
            hitSize = 8f;
            accel = 0.02f * 6;
            speed = 1.1f * 3;
            drag = 0.4f;
            health = 130f;
            canBoost = false;
            constructor = MechUnit::create;
            controller = u -> new ReplacementGroundAI();

            weapons.add(new Weapon("restored-mind-chain-blaster-equip") {{
                shootSound = pew;
                outlines = false;
                x = -4.5f;
                y = 0f;
                top = true;

                reload = 14f;
                alternate = true;
                ejectEffect = ExtendedFx.shellEjectSmall;
                shootX = 0f;
                mirror = true;

                bullet = new BasicBulletType(2.5f, 9f) {{
                    width = 7f;
                    height = 9f;
                    lifetime = 60f;
                    shootEffect = Fx.shootSmall;
                    smokeEffect = Fx.shootSmallSmoke;
                    ammoMultiplier = 2;
                }};
            }});
        }};

        titan = new UnitType("titan") {{
            outlines = false;
            mechSideSway = 0f;
            mechFrontSway = 0f;
            hitSize = 10f;
            accel = 0.022f * 6;
            speed = 0.8f * 3;
            drag = 0.4f;
            health = 460f;
            rotateSpeed = 0.1f * 30;
            targetAir = false;
            canBoost = false;
            immunities = ObjectSet.with(StatusEffects.burning);
            constructor = MechUnit::create;
            controller = u -> new ReplacementGroundAI();

            weapons.add(new Weapon("restored-mind-flamethrower-equip") {{
                shootSound = Sounds.flame;
                outlines = false;
                x = -4f;
                y = 0f;
                top = true;

                reload = 7f;
                alternate = true;
                ejectEffect = Fx.none;
                shootX = 0f;
                recoil = 1f;
                mirror = true;

                bullet = new BasicBulletType(3f, 6f) {{
                    ammoMultiplier = 3f;
                    hitSize = 7f;
                    lifetime = 42f;
                    pierce = true;
                    drag = 0.05f;
                    statusDuration = 60f * 4;
                    shootEffect = Fx.shootSmallFlame;
                    hitEffect = Fx.hitFlameSmall;
                    despawnEffect = Fx.none;
                    status = StatusEffects.burning;
                    sprite = "restored-mind-nullTexture";
                }};
            }});
        }};

        fortress = new UnitType("fortress") {{
            outlines = false;
            mechSideSway = 0f;
            mechFrontSway = 0f;
            hitSize = 10f;
            accel = 0.015f * 6;
            speed = 0.78f * 3;
            drag = 0.4f;
            health = 750f;
            rotateSpeed = 0.06f * 30;
            targetAir = false;
            canBoost = false;
            immunities = ObjectSet.with(StatusEffects.burning, StatusEffects.melting);
            constructor = MechUnit::create;
            controller = u -> new ReplacementGroundAI();

            weapons.add(new Weapon("restored-mind-artillery-equip") {{
                shootSound = Sounds.artillery;
                outlines = false;
                x = -10f;
                y = 0f;
                top = true;

                reload = 30f;
                alternate = true;
                ejectEffect = Fx.none;
                shootX = 0f;
                mirror = true;
                shake = 2f;
                recoil = 4f;

                bullet = new ArtilleryBulletType(2f, 0f) {{
                    hitEffect = Fx.blastExplosion;
                    knockback = 0.8f;
                    lifetime = 90f;
                    width = height = 14f;
                    collides = true;
                    collidesTiles = true;
                    splashDamageRadius = 20f;
                    splashDamage = 38f;
                    backColor = Pal.bulletYellowBack;
                    frontColor = Pal.bulletYellow;
                }};
            }});
        }};

        eruptor = new UnitType("eruptor") {{
            outlines = false;
            mechSideSway = 0f;
            mechFrontSway = 0f;
            hitSize = 9f;
            accel = 0.016f * 6;
            speed = 0.81f * 3;
            drag = 0.4f;
            health = 600f;
            rotateSpeed = 0.05f * 30;
            targetAir = false;
            canBoost = false;
            immunities = ObjectSet.with(StatusEffects.burning, StatusEffects.melting);
            constructor = MechUnit::create;
            controller = u -> new ReplacementGroundAI();

            weapons.add(new Weapon("restored-mind-eruption-equip") {{
                shootSound = Sounds.flame;
                outlines = false;
                x = -7f;
                y = 0f;
                top = true;

                reload = 5f;
                alternate = true;
                ejectEffect = Fx.none;
                shootX = 0f;
                mirror = true;
                recoil = 1f;

                bullet = new LiquidBulletType(Liquids.slag) {{
                    damage = 2;
                    speed = 2.1f;
                    drag = 0.02f;
                    ;
                }};
            }});
        }};

        chaosArray = new UnitType("chaos-array") {{
            outlines = false;
            mechSideSway = 0f;
            mechFrontSway = 0f;
            hitSize = 20f;
            accel = 0.012f * 6;
            speed = 0.68f * 3;
            drag = 0.4f;
            health = 3000f;
            rotateSpeed = 0.06f * 30;
            canBoost = false;
            constructor = MechUnit::create;
            controller = u -> new ReplacementGroundAI();

            weapons.add(new Weapon("restored-mind-chaos-equip") {{
                shootSound = Sounds.shootBig;
                outlines = false;
                x = -17f;
                y = 0f;
                top = true;

                reload = 25f;
                alternate = true;
                ejectEffect = ExtendedFx.shellEjectMedium;
                shootX = 0f;
                mirror = true;
                recoil = 3f;
                shake = 2f;
                inaccuracy = 3f;

                shoot.shots = 4;
                shoot.shotDelay = 5;

                bullet = new FlakBulletType(4f, 7f) {{
                    splashDamage = 33f;
                    lightning = 2;
                    lightningLength = 12;
                    shootEffect = Fx.shootBig;
                }};
            }});
        }};

        eradicator = new UnitType("eradicator") {{
            outlines = false;
            mechSideSway = 0f;
            mechFrontSway = 0f;
            hitSize = 20f;
            accel = 0.012f * 6;
            speed = 0.68f * 3;
            drag = 0.4f;
            health = 9000f;
            rotateSpeed = 0.06f * 30;
            canBoost = false;
            constructor = MechUnit::create;
            controller = u -> new ReplacementGroundAI();

            weapons.add(new Weapon("restored-mind-eradication-equip") {{
                shootSound = Sounds.shootBig;
                outlines = false;
                x = -21.5f;
                y = 0f;
                top = true;

                reload = 15f;
                alternate = true;
                ejectEffect = ExtendedFx.shellEjectMedium;
                shootX = 0f;
                mirror = true;
                recoil = 3f;
                shake = 2f;
                inaccuracy = 3f;

                shoot.shots = 4;
                shoot.shotDelay = 3;

                bullet = new BasicBulletType(8f, 65f) {{
                    width = 16f;
                    height = 23f;
                    shootEffect = Fx.shootBig;
                }};
            }});
        }};
        // --- Ground Units Region End ---
        // --- v5 Zone End ---

        // --- v6 Zone ---
        // --- Ground Units Region ---
        cix = new UnitType("cix") {{
            constructor = LegsUnit::create;
            drag = 0.1f;
            speed = 0.5f;
            hitSize = 9f;
            health = 140;
            //baseElevation = 0.51f;

            legCount = 6;
            legMoveSpace = 1f;
            legPairOffset = 3;
            legLength = 34f;
            //rotateShooting = false;
            legExtension = -15;
            legBaseOffset = 10f;
            //landShake = 0f;
            legSpeed = 0.1f;

            weapons.add(
                    new Weapon("restored-mind-missiles-mount") {{
                        reload = 20f;
                        x = 4f;
                        rotate = true;
                        mirror = true;
                        //flipSprite = !b;
                        shake = 1f;
                        bullet = ClassicBullets.missileSwarm;
                    }});
        }};
        oculon = new UnitType("oculon") {{
            outlines = false;
            constructor = MechUnit::create;
            mineTier = 1;
            hitSize = 9f;
            boostMultiplier = 2f;
            itemCapacity = 20;
            health = 230f;
            buildSpeed = 1.5f;
            canBoost = true;
            speed = 0.4f;
            hitSize = 10f;
            weapons.add(new Weapon("restored-mind-beam-weapon") {{
                shake = 2f;
                shootY = 4f;
                x = 6.5f;
                reload = 50f;
                recoil = 4f;
                shootSound = Sounds.laser;
                bullet = new LaserBulletType() {{
                    damage = 20f;
                    recoil = 1f;
                    sideAngle = 45f;
                    sideWidth = 1f;
                    sideLength = 70f;
                    colors = new Color[]{Pal.heal.cpy().a(0.4f), Pal.heal, Color.white};
                }};
            }});
        }};
        // --- Ground Units Region End ---
        // --- v6 Zone End ---

        // --- v7 Zone ---
        // --- Flying Units Region ---
        osc = new ErekirUnitType("osc"){{
            constructor = UnitEntity::create;
            hovering = true;
            //visualElevation = 0.1f;


            drag = 0.07f;
            speed = 2f;
            rotateSpeed = 5f;

            accel = 0.09f;
            health = 600f;
            armor = 3f;
            hitSize = 7f;
            engineOffset = 7f;
            engineSize = 2f;
            itemCapacity = 0;
            useEngineElevation = false;

            for(float f : new float[]{-3f, 3f}){
                parts.add(new HoverPart(){{
                    x = 3.9f;
                    y = f;
                    mirror = true;
                    radius = 6f;
                    phase = 90f;
                    stroke = 2f;
                    layerOffset = -0.001f;
                    color = Color.valueOf("bf92f9");
                }});
            }

            weapons.add(new Weapon("restored-mind-osc-weapon"){{
                y = 3f;
                x = 3f;
                mirror = true;
                layerOffset = -0.0001f;
                reload = 40f;

                bullet = new BasicBulletType(5f, 20){{
                    pierceCap = 2;
                    pierceBuilding = false;
                    width = 7f;
                    height = 12f;
                    lifetime = 25f;
                    shootEffect = Fx.sparkShoot;
                    smokeEffect = Fx.shootBigSmoke;
                    hitColor = backColor = trailColor = Pal.suppress;
                    frontColor = Color.white;
                    trailWidth = 1.5f;
                    trailLength = 5;
                    hitEffect = despawnEffect = Fx.hitBulletColor;
                }};
            }});
        }};
        // --- Flying Units Region Ends ---

        // --- Core Units Region ---
        Oldincite = new UnitType("incite") {{
            constructor = UnitEntity::create;
            controller = u -> new BuilderAI();
            //isCounted = false;
            envDisabled = 0;

            outlineColor = Pal.darkOutline;
            lowAltitude = false;
            flying = true;
            targetAir = false;
            mineSpeed = 8f;
            mineTier = 2;
            buildSpeed = 1.4f;
            drag = 0.06f;
            speed = 2.6f;
            rotateSpeed = 5f;
            accel = 0.11f;
            itemCapacity = 70;
            health = 600f;
            armor = 2f;
            hitSize = 18f;
            //commandLimit = 7;
            buildBeamOffset = 10f;
            engineSize = 0;
            payloadCapacity = Mathf.sqr(2f) * tilePayload;

            setEnginesMirror(
                    new UnitEngine(34 / 4f, 31 / 4f, 3f, 45f),
                    new UnitEngine(35 / 4f, -38 / 4f, 3f, 315f)
            );

            weapons.add(new Weapon("restored-mind-incite-weapon") {{
                reload = 30f;
                x = 4f;
                y = 6.25f;
                shootY = 5.75f;
                recoil = 2f;
                top = false;
                layerOffset = -0.01f;
                rotate = false;

                bullet = new BasicBulletType(5f, 15) {{
                    width = 7f;
                    height = 12f;
                    shootEffect = Fx.sparkShoot;
                    smokeEffect = Fx.shootBigSmoke;
                    pierceCap = 2;
                    pierce = true;
                    pierceBuilding = true;
                    hitColor = backColor = trailColor = Pal.bulletYellowBack;
                    frontColor = Color.white;
                    trailWidth = 1.5f;
                    trailLength = 7;
                    hitEffect = despawnEffect = Fx.hitBulletColor;
                }};
            }});
        }};

        Oldemanate = new UnitType("emanate") {
            {
                constructor = UnitEntity::create;
                controller = u -> new BuilderAI();
                //isCounted = false;
                envDisabled = 0;

                outlineColor = Pal.darkOutline;
                lowAltitude = false;
                flying = true;
                targetAir = false;
                mineSpeed = 8f;
                mineTier = 3;
                buildSpeed = 2f;
                drag = 0.06f;
                speed = 2.6f;
                rotateSpeed = 3f;
                accel = 0.11f;
                itemCapacity = 140;
                health = 1300f;
                armor = 3f;
                hitSize = 36f;
                //commandLimit = 9;
                buildBeamOffset = 72f / 4f;
                engineSize = 0;
                payloadCapacity = Mathf.sqr(3f) * tilePayload;

                drawBuildBeam = false;
                rotateToBuilding = false;

                float es = 3.8f;

                setEnginesMirror(
                        new UnitEngine(49 / 4f, 51 / 4f, es, 45f),
                        new UnitEngine(67 / 4f, -30 / 4f, es, 315f),
                        new UnitEngine(49 / 4f, -62 / 4f, es, 315f)
                );

                //TODO repair weapon
                Vec2[] positions = {/*new Vec2(30f, 50f), */new Vec2(60f, -15f)};
                int i = 0;

                for (var pos : positions) {
                    int fi = i;
                    //TODO change to BuildWeapon properly, remove standard build beam and rotation
                    weapons.add(new BuildWeapon("restored-mind-incite-weapon") {{
                        outlines = true;
                        rotate = true;
                        reload = fi == 0 ? 25f : 35f;
                        rotateSpeed = 7f;
                        x = pos.x / 4f;
                        y = pos.y / 4f;
                        shootY = 5.75f;
                        recoil = 2f;

                        bullet = new BasicBulletType(5f, 17) {{
                            width = 7f;
                            height = 12f;
                            shootEffect = Fx.sparkShoot;
                            smokeEffect = Fx.shootBigSmoke;
                            hitColor = backColor = trailColor = Pal.bulletYellowBack;
                            frontColor = Color.white;
                            trailWidth = 1.5f;
                            trailLength = 7;
                            hitEffect = despawnEffect = Fx.hitBulletColor;
                        }};
                    }});
                    i++;
                }
            }
        };

        // --- Naval Units Region ---
        vanguard = new UnitType("vanguard"){{
            outlines = false;
                speed = 1.3f;
                drag = 0.1f;
                hitSize = 8f;
                health = 130;
                immunities = ObjectSet.with(StatusEffects.wet);
                constructor = UnitWaterMove::create;
                weapons.add(new Weapon("restored-mind-mount-weapon") {{
                    reload = 10f;
                    x = 1.25f;
                    rotate = true;
                    ejectEffect = ExtendedFx.shellEjectSmall;
                    bullet = ClassicBullets.standardCopper;
                }});
        }};
        // --- Naval Units Region End ---

        effectDrone = new ErekirUnitType("effect-drone"){{
            constructor = BuildingTetherPayloadUnit::create;
            controller = u -> new EffectDroneAI();
            payloadCapacity = 0f;

            flying = true;
            targetable = false;
            bounded = false; //Map push unit
            drag = 0.08f;
            speed = 3f;
            lowAltitude = drawCell = isEnemy = logicControllable = playerControllable = allowedInPayloads = false;
            hidden = true;
            range = 30f; //TODO range testing


            health = 100f;
            hitSize = 7.5f;

            engineSize = 0f;
            engineColor = Color.valueOf("d1efff");
            float es = 2.5f, ew = 14.5f / 4f;

            setEnginesMirror(
                    new UnitEngine(ew, ew, es, 45f),
                    new UnitEngine(ew, -ew, es, 315f)
            );
        }};
    }
    
}
