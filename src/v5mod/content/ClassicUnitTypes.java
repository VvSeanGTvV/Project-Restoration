package v5mod.content;

import arc.audio.Sound;
import arc.graphics.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.Vars;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;

import static arc.struct.SnapshotSeq.with;

import v5mod.lib.ability.*;

public class ClassicUnitTypes {
    public static Sound pew = Vars.tree.loadSound("v5_sounds_pew"); //just pew lol

    public static UnitType 
    
    //Mech Region
    omega, delta, alpha, tau, //Mech - Ground [v5]
    javelin, trident, glaive, dart, //Mech - Air [v5]

    //Normal Units
    wraith, ghoul, revenant, lich, reaper, //Unit - Air [v5]
    draug, phantom, spirit, //Unit - Air - Support [v5]

    oculon, //Unit - Ground - Prototype [v6]
    cix, //Unit - Legs - Prototype [v6]
    vanguard, //Unit - Naval - Prototype [v6]

    crawler, dagger, titan, fortress, eruptor, chaosArray, eradicator //Unit - Ground [v5]
    ;

    public static void load(){

        // --- v5 Zone ---
        // --- Mech Region ---
        // --- Ground Units Region ---
        alpha = new UnitType("alpha-mech"){{
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

            weapons.add(new Weapon("projectv5-mod-blaster-equip"){{
                outlines = false;
                shootSound = pew;
                x = 0f;
                y = 0f;
                top = true;

                reload = 7.5f;
                alternate = true;
                ejectEffect = Fx.casing1;
                shootX = -2.6f;
                mirror = true;
                bullet = new BasicBulletType(2.5f, 9f){{ //reformat v5 coding into v7
                    width = 7f;
                    height = 9f;
                    lifetime = 60f;
                    shootEffect = Fx.shootSmall;
                    smokeEffect = Fx.shootSmallSmoke;
                    ammoMultiplier = 2;
                }};
            }});
         }};

        delta = new UnitType("delta-mech"){{
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

            weapons.add(new Weapon("projectv5-mod-shockgun-equip"){{
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
                shootSound = Sounds.spark;;
                shootX = -2.6f;
                mirror = true;
                bullet = new LightningBulletType(){{ //reformat v5 coding into v7
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

         tau = new UnitType("tau-mech"){{
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

            weapons.add(new Weapon("projectv5-mod-heal-blaster-equip"){{
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
                bullet = new BasicBulletType(5.2f, 13f){{ //reformat v5 coding into v7
                    healPercent = 3f;
                    shootEffect = Fx.shootHeal;
                    smokeEffect = Fx.hitLaser;
                    hitEffect = Fx.hitLaser;
                    despawnEffect = Fx.hitLaser;
                    collidesTeam = true;
                    healEffect = Fx.healBlockFull;
                    sprite = "projectv5-mod-laser";
                    width = 7f;
                    height = 5f;
                    rotationOffset = 90f; //Sprite rotate cause it is way off lolz.
                    frontColor = Pal.heal;
                    backColor = Pal.heal;
                }};
            }});
         }};

         omega = new UnitType("omega-mech"){{
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
            
            weapons.add(new Weapon("projectv5-mod-swarmer-equip"){{
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

                bullet = new MissileBulletType(2.7f, 12){{ //adjust the format of v5 for v7
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
        dart = new UnitType("dart-ship"){{
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

            weapons.add(new Weapon("projectv5-mod-blaster-equip"){{
                x = 0.8f;
                y = -1f;
                top = true;

                reload = 7.5f;
                alternate = true;
                ejectEffect = Fx.casing1;
                mirror = true;
                shootSound = pew;
                shootX = -2.5f;


                bullet = new BasicBulletType(2.5f, 9f){{ //adjust the format of v5 for v7
                    width = 7f;
                    height = 9f;
                    lifetime = 60f;
                    shootEffect = Fx.shootSmall;
                    smokeEffect = Fx.shootSmallSmoke;
                    ammoMultiplier = 2;
                }};
            }});
        }};

        trident = new UnitType("trident-ship"){{
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
            
            weapons.add(new Weapon("projectv5-mod-nullTexture"){{
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
                

                bullet = new BombBulletType(16f, 25f){{ //adjust the format of v5 for v7
                    width = 10f;
                    height = 14f;
                    hitEffect = Fx.flakExplosion;
                    shootEffect = Fx.none;
                    smokeEffect = Fx.none;
                    shootSound = Sounds.artillery;
                }};
            }});
        }};

        glaive = new UnitType("glaive-ship"){{
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
            
            weapons.add(new Weapon("projectv5-mod-nullTexture"){{
                x = 2f;
                y = 0f;
                top = true;

                reload = 6.5f;
                inaccuracy = 2f;
                alternate = true;
                ejectEffect = Fx.casing1;
                shootSound = Sounds.shootSnap;
                mirror = true;
                

                bullet = ClassicBullets.standardGlaive;
            }});
        }};

        javelin = new UnitType("javelin-ship"){{
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
            
            weapons.add(new Weapon("projectv5-mod-nullTexture"){{
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
        wraith = new UnitType("wraith"){{
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
            controller = u -> new FlyingAI();
            
            weapons.add(new Weapon("projectv5-mod-nullTexture"){{
                x = 2f;
                y = 0f;
                top = true;

                reload = 14f;
                alternate = true;
                ejectEffect = Fx.casing1;
                shootSound = Sounds.shoot;
                mirror = true;
                

                bullet = new BasicBulletType(2.5f, 9f){{ //adjust the format of v5 for v7
                    width = 7f;
                    height = 9f;
                    lifetime = 60f;
                    shootEffect = Fx.shootSmall;
                    smokeEffect = Fx.shootSmallSmoke;
                    ammoMultiplier = 2;
                }};
            }});
        }};

        ghoul = new UnitType("ghoul"){{
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
            controller = u -> new FlyingAI();
            
            weapons.add(new Weapon("projectv5-mod-nullTexture"){{
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
                

                bullet = new  BombBulletType(10f, 20f){{ //adjust the format of v5 for v7
                    width = 9f;
                    height = 13f;
                    hitEffect = Fx.flakExplosion;
                    shootEffect = Fx.none;
                    smokeEffect = Fx.none;
                }};
            }});
        }};

        revenant = new UnitType("revenant"){{
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
            controller = u -> new FlyingAI();
            
            weapons.add(new Weapon("projectv5-mod-revenant-missiles-equip"){{
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

        lich = new UnitType("lich"){{
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
            controller = u -> new FlyingAI();
            
            weapons.add(new Weapon("projectv5-mod-lich-missiles-equip"){{
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
                
                bullet = new MissileBulletType(2.7f, 12f){{ //adjust the format of v5 for v7
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

        reaper = new UnitType("reaper"){{
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
            controller = u -> new FlyingAI();
            
            weapons.add(new Weapon("projectv5-mod-reaper-gun-equip"){{
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
                
                bullet = new BasicBulletType(7f, 42f){{ //adjust the format of v5 for v7
                    width = 15f;
                    height = 21f;
                    shootEffect = Fx.shootBig;
                    range = 165f;
                }};
            }});
        }};

        // --- Flying Units Region (Support) ---
        draug = new UnitType("draug"){{
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

        spirit = new UnitType("spirit"){{
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

            weapons.add(new Weapon("projectv5-mod-nullTexture"){{
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
                
                bullet = new BasicBulletType(5.2f, 15f){{ //adjust the format of v5 for v7
                    healPercent = 5.5f;
                    shootEffect = Fx.shootHeal;
                    smokeEffect = Fx.hitLaser;
                    hitEffect = Fx.hitLaser;
                    despawnEffect = Fx.hitLaser;
                    collidesTeam = true;
                    healEffect = Fx.healBlockFull;
                    sprite = "projectv5-mod-laser";
                    width = 7f;
                    height = 5f;
                    rotationOffset = 90f; //Sprite rotate cause it is way off lolz.
                    frontColor = Pal.heal;
                    backColor = Pal.heal;
                }};
            }});
        }};

        phantom = new UnitType("phantom"){{
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

            weapons.add(new Weapon("projectv5-mod-nullTexture"){{
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

                bullet = new BasicBulletType(5.2f, 13f){{ //adjust the format of v5 for v7
                    healPercent = 3f;
                    shootEffect = Fx.shootHeal;
                    smokeEffect = Fx.hitLaser;
                    hitEffect = Fx.hitLaser;
                    despawnEffect = Fx.hitLaser;
                    collidesTeam = true;
                    healEffect = Fx.healBlockFull;
                    sprite = "projectv5-mod-laser";
                    width = 7f;
                    height = 5f;
                    rotationOffset = 90f; //Sprite rotate cause it is way off lolz.
                    frontColor = Pal.heal;
                    backColor = Pal.heal;
                }};
            }});
        }};
        // --- Flying Units Region (Support) End ---
        // --- Flying Units Region End ---

        // --- Ground Units Region ---
        crawler = new UnitType("crawler"){{
            outlines = false;
            mechSideSway = 0f;
            mechFrontSway = 0f;
            hitSize = 8f;
            accel = 0.0285f *6;
            speed = 1.27f *3;
            drag = 0.4f;
            health = 120f;
            range = 50f;
            canBoost = false;
            constructor = MechUnit::create;
            controller = u -> new SuicideAI();

            weapons.add(new Weapon("projectv5-mod-nullTexture-equip"){{
                reload = 6f;
                ejectEffect = Fx.none;
                shootSound = Sounds.explosion;

                bullet = new BombBulletType(2f, 3f){{
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

        dagger = new UnitType("dagger"){{
            outlines = false;
            mechSideSway = 0f;
            mechFrontSway = 0f;
            hitSize = 8f;
            accel = 0.02f *6;
            speed = 1.1f *3;
            drag = 0.4f;
            health = 130f;
            canBoost = false;
            constructor = MechUnit::create;
            controller = u -> new GroundAI();

            weapons.add(new Weapon("projectv5-mod-chain-blaster-equip"){{
                shootSound = pew;
                outlines = false;
                x = -4.5f;
                y = 0f;
                top = true;

                reload = 14f;
                alternate = true;
                ejectEffect = Fx.casing1;
                shootX = 0f;
                mirror = true;

                bullet = new BasicBulletType(2.5f, 9f){{
                    width = 7f;
                    height = 9f;
                    lifetime = 60f;
                    shootEffect = Fx.shootSmall;
                    smokeEffect = Fx.shootSmallSmoke;
                    ammoMultiplier = 2;
                }};
            }});
        }};

        titan = new UnitType("titan"){{
            outlines = false;
            mechSideSway = 0f;
            mechFrontSway = 0f;
            hitSize = 10f;
            accel = 0.022f *6;
            speed = 0.8f *3;
            drag = 0.4f;
            health = 460f;
            rotateSpeed = 0.1f *30;
            targetAir = false;
            canBoost = false;
            immunities = ObjectSet.with(StatusEffects.burning);
            constructor = MechUnit::create;
            controller = u -> new GroundAI();

            weapons.add(new Weapon("projectv5-mod-flamethrower-equip"){{
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

                bullet = new BasicBulletType(3f, 6f){{
                    ammoMultiplier =3f;
                    hitSize =7f;
                    lifetime =42f;
                    pierce =true;
                    drag =0.05f;
                    statusDuration =60f*4;
                    shootEffect =Fx.shootSmallFlame;
                    hitEffect =Fx.hitFlameSmall;
                    despawnEffect =Fx.none;
                    status =StatusEffects.burning;
                    sprite = "projectv5-mod-nullTexture";
                }};
            }});
        }};

        fortress = new UnitType("fortress"){{
            outlines = false;
            mechSideSway = 0f;
            mechFrontSway = 0f;
            hitSize = 10f;
            accel = 0.015f *6;
            speed = 0.78f *3;
            drag = 0.4f;
            health = 750f;
            rotateSpeed = 0.06f *30;
            targetAir = false;
            canBoost = false;
            immunities = ObjectSet.with(StatusEffects.burning, StatusEffects.melting);
            constructor = MechUnit::create;
            controller = u -> new GroundAI();

            weapons.add(new Weapon("projectv5-mod-artillery-equip"){{
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

                bullet = new ArtilleryBulletType(2f, 0f){{
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

        eruptor = new UnitType("eruptor"){{
            outlines = false;
            mechSideSway = 0f;
            mechFrontSway = 0f;
            hitSize = 9f;
            accel = 0.016f *6;
            speed = 0.81f *3;
            drag = 0.4f;
            health = 600f;
            rotateSpeed = 0.05f *30;
            targetAir = false;
            canBoost = false;
            immunities = ObjectSet.with(StatusEffects.burning, StatusEffects.melting);
            constructor = MechUnit::create;
            controller = u -> new GroundAI();

            weapons.add(new Weapon("projectv5-mod-eruption-equip"){{
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

                bullet = new LiquidBulletType(Liquids.slag){{
                    damage = 2;
                    speed = 2.1f;
                    drag = 0.02f;;
                }};
            }});
        }};

        chaosArray = new UnitType("chaos-array"){{
            outlines = false;
            mechSideSway = 0f;
            mechFrontSway = 0f;
            hitSize = 20f;
            accel = 0.012f *6;
            speed = 0.68f *3;
            drag = 0.4f;
            health = 3000f;
            rotateSpeed = 0.06f *30;
            canBoost = false;
            constructor = MechUnit::create;
            controller = u -> new GroundAI();

            weapons.add(new Weapon("projectv5-mod-chaos-equip"){{
                shootSound = Sounds.shootBig;
                outlines = false;
                x = -17f;
                y = 0f;
                top = true;

                reload = 25f;
                alternate = true;
                ejectEffect = Fx.casing2;
                shootX = 0f;
                mirror = true;
                recoil = 3f;
                shake = 2f;
                inaccuracy = 3f;

                shoot.shots = 4;
                shoot.shotDelay = 5;

                bullet = new FlakBulletType(4f, 7f){{
                    splashDamage = 33f;
                    lightning = 2;
                    lightningLength = 12;
                    shootEffect = Fx.shootBig;
                }};
            }});
        }};

        eradicator = new UnitType("eradicator"){{
            outlines = false;
            mechSideSway = 0f;
            mechFrontSway = 0f;
            hitSize = 20f;
            accel = 0.012f *6;
            speed = 0.68f *3;
            drag = 0.4f;
            health = 9000f;
            rotateSpeed = 0.06f *30;
            canBoost = false;
            constructor = MechUnit::create;
            controller = u -> new GroundAI();

            weapons.add(new Weapon("projectv5-mod-eradication-equip"){{
                shootSound = Sounds.shootBig;
                outlines = false;
                x = -21.5f;
                y = 0f;
                top = true;

                reload = 15f;
                alternate = true;
                ejectEffect = Fx.casing2;
                shootX = 0f;
                mirror = true;
                recoil = 3f;
                shake = 2f;
                inaccuracy = 3f;

                shoot.shots = 4;
                shoot.shotDelay = 3;

                bullet = new BasicBulletType(8f, 65f){{
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
        cix = new UnitType("cix"){{
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


            for(boolean b : Mathf.booleans){
                weapons.add(
                        new Weapon("projectv5-mod-missiles-mount"){{
                            reload = 20f;
                            x = 4f * Mathf.sign(b);
                            rotate = true;
                            mirror = false;
                            flipSprite = !b;
                            shake = 1f;
                            bullet = ClassicBullets.missileSwarm;
                        }});
            }
        }};
        oculon = new UnitType("oculon"){{
            mineTier = 1;
            hitSize = 9f;
            boostMultiplier = 2f;
            itemCapacity = 20;
            health = 230f;
            buildSpeed = 1.5f;
            canBoost = true;

            speed = 0.4f;
            hitSize = 10f;
            constructor = MechUnit::create;

            weapons.add(new Weapon("projectv5-mod-beam-weapon"){{
                shake = 2f;
                shootY = 4f;
                x = 6.5f;
                reload = 50f;
                recoil = 4f;
                shootSound = Sounds.laser;

                bullet = new LaserBulletType(){{
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

        // --- Naval Units Region ---
        vanguard = new UnitType("vanguard"){{
                speed = 1.3f;
                drag = 0.1f;
                hitSize = 8f;
                health = 130;
                immunities = ObjectSet.with(StatusEffects.wet);
                constructor = UnitWaterMove::create;
                weapons.add(new Weapon("mount-weapon") {{
                    reload = 10f;
                    x = 1.25f;
                    rotate = true;
                    ejectEffect = Fx.casing1;
                    bullet = ClassicBullets.standardCopper;
                }});
        }};
       
    }
    
}
