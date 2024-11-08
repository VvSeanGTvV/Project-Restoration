package classicMod.content;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.Time;
import classicMod.library.ability.*;
import classicMod.library.ai.*;
import classicMod.library.bullets.*;
import classicMod.library.drawCustom.CircleForceDraw;
import classicMod.library.unitType.*;
import mindustry.Vars;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.entities.part.*;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.unit.*;
import mindustry.type.weapons.BuildWeapon;

import static arc.struct.SnapshotSeq.with;
import static classicMod.ClassicMod.internalMod;
import static classicMod.content.ClassicSounds.pew;
import static classicMod.content.ClassicVars.empty;
import static classicMod.content.ExtendedFx.*;
import static mindustry.Vars.tilePayload;

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

    vanguardShip, //Mech - Air - Prototype [v6-dev]
    oculon, //Unit - Ground - Prototype [v6-dev]
    cix, //Unit - Legs - Prototype [v6-dev]
    vanguard, //Unit - Naval - Prototype [v7-dev]

    bulwark,
    Oldincite, Oldemanate, spark, //Unit - Core Units - Prototype [v7-dev]
    osc, //Unit - Flying - [v7-dev]
    mantel, howit, //Unit - Tankk - [v7-dev]

    effectDrone, //Unit - Effect - Prototype [v7-dev]

    azathoth, //Unit - Advancedcontent - Old Content [v5]

    electra, chromeWraith, //Unit - Example-mod - Old content [v5]

    mantis, // TESTING

    alphaChan, crawlerChan, boulderChan, monoChan, octChan, oxynoeChan, quadChan, seiChan, zenithChan //Unit - Old Content [Animdustry]
    ;

    public static void load() {

        mantis = new MantisRayType("skat"){{
            constructor = UnitEntity::create;
            health = 1000f;

            flying = true;
        }};

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

            weapons.add(new Weapon(){{
                y = 0f;
                x = 0f;
                mirror = false;
                reload = 12f;
                showStatSprite = false;

                ejectEffect = ExtendedFx.none;

                bullet = ClassicBullets.modifierBullet;
            }});
        }};

        vanguardShip = new UnitType("vanguard-ship"){{
            mineTier = 1;
            mineSpeed = 4f;
            speed = 0.49f;
            drag = 0.09f;
            health = 200f;
            engineSize = 2.3f;
            engineColor = Pal.lightTrail;
            buildSpeed = 1.2f;
            outlines = false;

            constructor = UnitEntity::create;
            alwaysUnlocked = true;
            weapons.add(new Weapon(){{
                x = -1 + 1.5f;
                y = -1;
                //length = 1.5f;
                reload = 30f;
                //roundrobin = true;
                showStatSprite = false;
                inaccuracy = 6f;
                velocityRnd = 0.1f;
                ejectEffect = Fx.none;
                bullet = new HealBulletType(){{
                    backColor = engineColor;
                    homingPower = 20f;
                    height = 4f;
                    width = 1.5f;
                    damage = 3f;
                    speed = 4f;
                    lifetime = 40f;
                    shootEffect = Fx.shootHealYellow;
                    smokeEffect = hitEffect = despawnEffect = ExtendedFx.hitYellowLaser;
                }};
            }});
        }
        };

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
            abilities.add(
                    new RegenerationAbility(){{
                        healby = 0.09f;
                    }}
            );

            weapons.add(new Weapon(internalMod + "-blaster-equip") {{
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

            weapons.add(new Weapon(internalMod + "-shockgun-equip") {{
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

            weapons.add(new Weapon(internalMod + "-heal-blaster-equip") {{
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
                    sprite = internalMod + "-laser";
                    width = 7f;
                    height = 5f;
                    rotationOffset = 90f; //Sprite rotate cause it is way off lolz.
                    frontColor = Pal.heal;
                    backColor = Pal.heal;
                }};
            }});
        }};

        electra = new UnitType("electra-mech") {{
            speed = 0.6f;
            boostMultiplier = 1.5f;
            buildSpeed = 2f;
            engineColor = Color.valueOf("666495");
            health = 300;

            outlines = false;
            flying = false;

            canBoost = true;
            constructor = MechUnit::create;

            weapons.add(new Weapon(internalMod + "-chrome-blaster") {{
                alternate = false;
                x = 4;
                reload = 4;
                ejectEffect = Fx.none;
                shootSound = Sounds.spark;
                bullet = new LightningBulletType(){{
                    damage = 4f;
                    lightningLength = 15;
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
            abilities.add(new ArmorAbility(){{
                healthMultiplier = 30f;
            }}); //Modify armor abilty for 2nd texture (static)

            weapons.add(new Weapon(internalMod + "-swarmer-equip") {{
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

            weapons.add(new Weapon(internalMod + "-blaster-equip") {{
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

        halberd = new UnitType("halberd-ship"){{
            outlines = false;
            hitSize = 8f;
            accel = 0.64f * 3f;
            speed = 3f;
            drag = 0.07f;
            health = 180f;
            flying = true;
            itemCapacity = 75;
            engineOffset = 6.75f;
            engineColor = Color.valueOf("feb380");
            buildSpeed = 1.7f;
            mineTier = 5;
            mineSpeed = 4f;
            constructor = UnitEntity::create;

            weapons.add(new Weapon(internalMod + "-halberd-equip"){{
                x = 5.75f;
                y = 1f;
                mirror = true;
                reload = 12f;

                shootSound = Sounds.shootSnap;
                ejectEffect = ExtendedFx.shellEjectSmall;

                bullet = ClassicBullets.standardGlaive;
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


            weapons.add(new Weapon() {{
                x = 0f;
                y = 2f;
                top = true;
                showStatSprite = false;

                reload = 15f;
                shoot.shotDelay = 1f;
                shoot.shots = 10;
                inaccuracy = 2f;
                alternate = true;
                ejectEffect = Fx.none;
                velocityRnd = 1f;
                inaccuracy = 20f;
                mirror = true;

                ignoreRotation = true;
                shootCone = 180f;


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

            weapons.add(new Weapon() {{
                x = 2f;
                y = 0f;
                top = true;

                reload = 6.5f;
                inaccuracy = 2f;
                alternate = true;
                ejectEffect = ExtendedFx.shellEjectSmall;
                shootSound = Sounds.shootSnap;
                mirror = true;
                showStatSprite = false;


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

            weapons.add(new Weapon() {{
                x = 2f;
                y = 0.2f;
                top = true;
                showStatSprite = false;

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

            weapons.add(new Weapon() {{
                x = 2f;
                y = 0f;
                top = true;
                showStatSprite = false;

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

        chromeWraith = new UnitType("chrome-wraith") {{
            outlines = false;
            speed = 1.5f;
            accel = 0.03f * 3f;
            drag = 0.01f;
            flying = true;
            health = 90;
            engineOffset = 5.5f;
            range = 160f;
            circleTarget = true;
            constructor = UnitEntity::create;
            controller = u -> new OldFlyingAI();

            weapons.add(new Weapon(internalMod + "-chrome-blaster") {{
                alternate = true;
                reload = 10;
                ejectEffect = Fx.none;
                shootSound = Sounds.spark;
                bullet = new LightningBulletType(){{
                    damage = 8f;
                    lightningLength = 15;
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
            faceTarget = false;

            weapons.add(new Weapon() {{
                x = 0f;
                y = 0f;
                top = true;
                showStatSprite = false;

                reload = 6f;

                alternate = true;
                ejectEffect = Fx.none;
                velocityRnd = 1f;
                inaccuracy = 40f;
                shootSound = Sounds.none;
                mirror = true;

                ignoreRotation = true;
                shootCone = 180f;


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

            weapons.add(new Weapon(internalMod + "-revenant-missiles-equip") {{
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

            weapons.add(new Weapon(internalMod + "-lich-missiles-equip") {{
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

            weapons.add(new Weapon(internalMod + "-reaper-gun-equip") {{
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

            weapons.add(new Weapon() {{
                x = 0f;
                y = 0f;
                top = true;
                showStatSprite = false;

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
                    sprite = internalMod + "-laser";
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

            weapons.add(new Weapon() {{
                x = 0f;
                y = 0f;
                top = true;
                showStatSprite = false;

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
                    sprite = internalMod + "-laser";
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

            weapons.add(new Weapon(internalMod + "-nullTexture-equip") {{
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

            weapons.add(new Weapon(internalMod + "-chain-blaster-equip") {{
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

            weapons.add(new Weapon(internalMod + "-flamethrower-equip") {{
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
                    sprite = empty;
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

            weapons.add(new Weapon(internalMod + "-artillery-equip") {{
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

            weapons.add(new Weapon(internalMod + "-eruption-equip") {{
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

            weapons.add(new Weapon(internalMod + "-chaos-equip") {{
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

            weapons.add(new Weapon(internalMod + "-eradication-equip") {{
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
            //legPairOffset = 3f;
            legLength = 15f;
            //rotateShooting = false;
            legExtension = -10;
            legBaseOffset = 5f;
            stepShake = 1f;
            legLengthScl = 1f;
            rippleScale = 2f;
            legSpeed = 0.2f / 2f;
            legSplashDamage = 32;
            legSplashRange = 30;

            weapons.add(
                    new Weapon(internalMod + "-missiles-mount") {{
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
            boostMultiplier = 2f;
            itemCapacity = 20;
            health = 230f;
            buildSpeed = 1.5f;
            canBoost = true;
            speed = 0.4f;
            hitSize = 10f;
            weapons.add(new Weapon(internalMod + "-beam-weapon") {{
                shake = 2f;
                shootY = 4f;
                shootX = -4.75f;
                x = 0.75f;
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

            weapons.add(new Weapon(internalMod + "-osc-weapon"){{
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


        // --- Tank Units Region ---
        howit = new TankUnitType("howit"){{
            constructor = TankUnit::create;
            hitSize = 28f;
            treadPullOffset = 4;
            speed = 0.6f;
            health = 10000;
            armor = 20f;
            treadRects = new Rect[]{new Rect(22f - 154f/2f, 16f - 154f/2, 28f, 130f)};

            weapons.add(new Weapon(internalMod + "-howit-weapon"){{
                shootSound = Sounds.bolt;
                layerOffset = 0.0001f;
                reload = 120f;
                shootY = 7f;
                shake = 2f;
                recoil = 4f;
                rotate = true;
                rotateSpeed = 1f;
                mirror = false;
                x = 0f;
                y = -4;
                shadow = 30f;

                //TODO better bullet / weapon
                bullet = new ArtilleryBulletType(4f, 20, "shell"){{
                    hitEffect = Fx.massiveExplosion;
                    knockback = 0.8f;
                    lifetime = 100f;
                    width = height = 14f;
                    collidesTiles = false;
                    splashDamageRadius = 60f;
                    splashDamage = 60f;
                    backColor = Color.valueOf("feb380");
                    frontColor = Color.white;

                    width = 9f;
                    height = 15f;

                    status = StatusEffects.blasted;
                    lightning = 5;

                    trailLength = 28;
                    trailWidth = 3f;
                    trailEffect = Fx.none;
                    trailColor = backColor;

                    shrinkX = 0.1f;
                    shrinkY = 0.5f;

                    fragBullets = 6;
                    fragVelocityMin = 0.7f;
                    fragLifeMin = 0.1f;
                    fragBullet = new BasicBulletType(5f, 15){{
                        width = 7f;
                        height = 9f;
                        lifetime = 20f;
                        backColor = Color.valueOf("feb380");
                        frontColor = Color.white;
                    }};
                }};
            }});

            int i = 0;
            for(float f : new float[]{-24f / 4f, -36f / 4f}){
                int fi = i ++;
                weapons.add(new Weapon(internalMod + "-howit-point-weapon"){{
                    reload = 35f + fi * 5;
                    x = 48f / 4f + (fi * 2f);
                    y = f;
                    shootY = 5.5f;
                    recoil = 2f;
                    rotate = true;
                    rotateSpeed = 2f;

                    bullet = new BasicBulletType(6f, 12){{
                        width = 6.5f;
                        height = 11f;
                        shootEffect = Fx.sparkShoot;
                        smokeEffect = Fx.shootBigSmoke;
                        hitColor = backColor = trailColor = Color.valueOf("feb380");
                        frontColor = Color.white;
                        trailWidth = 1.5f;
                        trailLength = 6;
                        hitEffect = despawnEffect = Fx.hitBulletColor;
                    }};
                }});
            }
        }};

        mantel = new TankUnitType("mantel"){{
            constructor = TankUnit::create;
            hitSize = 44f;
            treadPullOffset = 1;
            speed = 0.48f;
            health = 20000;
            armor = 25f;
            crushDamage = 22f;
            rotateSpeed = 0.9f;
            float xo = 231f/2f, yo = 231f/2f;
            treadRects = new Rect[]{new Rect(27 - xo, 152 - yo, 56, 73), new Rect(24 - xo, 51 - yo, 29, 17), new Rect(59 - xo, 18 - yo, 39, 19)};

            //TODO maybe different shoot
            weapons.add(new Weapon(internalMod + "-mantel-weapon"){{
                shootSound = Sounds.largeCannon;
                layerOffset = 0.0001f;
                reload = 120f;
                shootY = (71f / 4f) - 2f;
                shake = 5f;
                recoil = 4f;
                rotate = true;
                rotateSpeed = 0.6f;
                mirror = false;
                x = 0f;
                shadow = 32f;
                y = -5f;
                heatColor = Color.valueOf("f9350f");
                cooldownTime = 80f;

                parts.addAll(
                        new RegionPart("-side"){{
                            outlineLayerOffset = 0f;
                            progress = PartProgress.heat;
                            mirror = true;
                            under = true;
                            moveY = -4.25f;
                            moveX = 2.25f;
                            moveRot = -10f;
                            x = 10.5f;
                            y = 8.85f;
                        }}
                );

                bullet = new BallBulletType(8f, 110){{
                    float orbRad = 7f, partRad = 3f;
                    int parts = 10;

                    orbRadius = orbRad;
                    particleSize = partRad;
                    particles = parts;

                    lifetime = 50f;
                    hitSize = 6f;
                    shootEffect = shootMantel;
                    smokeEffect = shootSmokeMantel;
                    pierceCap = 2;
                    pierce = true;
                    pierceBuilding = true;
                    //hitColor = backColor = trailColor = Color.valueOf("feb380");
                    particleColor = ballColor = hitColor = trailColor = Color.valueOf("feb380");
                    //frontColor = Color.white;
                    trailWidth = 3.1f;
                    trailLength = 8;
                    hitEffect = despawnEffect = Fx.blastExplosion;
                    hitSound = Sounds.explosion;

                    fragBullets = 8;
                    fragBullet = new FlakBulletType(5f, 15){{
                        width = 10f;
                        height = 12f;
                        shrinkX = shrinkY = 1f;
                        lifetime = 15f;
                        backColor = ballColor;
                        frontColor = ballColor;

                        splashDamage = 20f * 1.5f;
                        splashDamageRadius = 18f;
                        lightning = 2;
                        lightningLength = 7;
                    }};
                }};

                parts.addAll(
                        new CircleForceDraw(){{
                            float orbRad = 7f, partRad = 3f;
                            int parts = 10;

                            color = Color.valueOf("feb380");
                            particleColor = Color.valueOf("b17d59");

                            x = 8f;
                            under = true;

                            orbRadius = orbRad;
                            particleSize = partRad;
                            particles = parts;
                        }}
                );
            }});

            parts.addAll(
                    new RegionPart("-glow"){{
                        color = Pal.turretHeat.cpy();
                        blending = Blending.additive;
                        layer = -1f;
                        outline = mirror = false;
                    }}
            );
        }};

        // --- Core Units Region ---
        spark = new UnitType("spark"){{
            outlineColor = Pal.darkOutline;
            constructor = UnitEntity::create;
            controller = u -> new BuilderAI();
            //isCounted = false;

            lowAltitude = false;
            flying = true;
            targetAir = false;
            mineSpeed = 6.5f;
            mineTier = 1;
            buildSpeed = 0.8f;
            drag = 0.06f;
            speed = 2.5f;
            rotateSpeed = 9f;
            accel = 0.1f;
            itemCapacity = 40;
            health = 300f;
            armor = 1f;
            hitSize = 9f;
            //commandLimit = 5;
            engineSize = 0;

            engines = new Seq<>(
                    new UnitEngine[]{
                        new UnitEngine(21 / 4f, 19 / 4f, 2.2f, 45f),
                        new UnitEngine(-21 / 4f, 19 / 4f, 2.2f, 135f),

                        new UnitEngine(23 / 4f, -22 / 4f, 2.2f, 315f),
                        new UnitEngine(-23 / 4f, -22 / 4f, 2.2f, 225f)
                    }
            );

            weapons.add(new Weapon(){{
                reload = 55f;
                x = 0f;
                y = 1f;
                top = false;
                mirror = false;
                showStatSprite = false;

                bullet = new ArtilleryBulletType(3f, 11){{
                    trailLength = 8;
                    trailWidth = 2.4f;
                    collidesTiles = true;
                    collides = true;
                    trailEffect = Fx.none;
                    trailColor = Pal.bulletYellowBack;
                    homingPower = 0.01f;
                    splashDamage = 10;
                    splashDamageRadius = 20f;
                    weaveMag = 2f;
                    weaveScale = 4f;
                    width = 10f;
                    height = 13f;

                    lifetime = 50f;
                    hitEffect = Fx.blastExplosion;
                    shootEffect = Fx.shootBig;
                    smokeEffect = Fx.shootBigSmoke;
                    buildingDamageMultiplier = 0.4f;
                }};
            }});
        }};

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

            weapons.add(new Weapon(internalMod + "-incite-weapon") {{
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
                    weapons.add(new BuildWeapon(internalMod + "-incite-weapon") {{
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
                weapons.add(new Weapon(internalMod + "-mount-weapon") {{
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
