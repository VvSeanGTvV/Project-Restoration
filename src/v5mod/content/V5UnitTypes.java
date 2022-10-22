package v5mod.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.ai.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.ammo.*;
import mindustry.type.unit.*;
import mindustry.type.weapons.*;
import mindustry.world.meta.*;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;
import static mindustry.Vars.*;

public class V5UnitTypes {
    public static UnitType
    //Mech
    omega;

    public static void load(){

        omega = new UnitType("omega-mech"){{
            hitSize = 8f;
            mineTier = 2;
            mineSpeed = 1.5f;
            itemCapacity = 80;
            speed = 0.36f;
            boostMultiplier = 0.6f;
            mechLandShake = 4f;
            health = 350f;
            buildSpeed = 1.5f;
            engineColor = Color.valueOf("feb380");
            canBoost = true;
            constructor = MechUnit::create;
            
            weapons.add(new Weapon("swarmer-equip"){{
                region = Core.atlas.find("swarmer-equip");
                x = 1f;
                y = 0f;
                top = true;

                recoil = 4f;
                reload = 38f;
                shoot.shots = 4;
                inaccuracy = 8f;
                alternate = true;
                ejectEffect = Fx.none;
                shake = 3f;
                shootSound = Sounds.shootBig;
                mirror = true;

                bullet = new MissileBulletType(2.7f, 12){{
                    width = 8f;
                    height = 8f;
                    drag = -0.003f;
                    //homingRange = 60f;
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
    }
    
}
