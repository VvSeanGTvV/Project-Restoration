package v5mod.lib.ai.type;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.Vars;
import mindustry.ai.types.*;
import mindustry.entities.*;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.units.*;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.type.Weapon;
import mindustry.world.meta.*;
import v5mod.lib.ai.*;


import static arc.math.Mathf.dst;
import static mindustry.Vars.indexer;

public class testAI extends AIController {
    protected float[] weaponAngles = {0,0};

    @Override
    public void updateMovement(){
        if(unit.isFlying()){
            unit.wobble();
        }
        if(Units.invalidateTarget(target,unit.team(),unit.x,unit.y)){
            target = null;
        }

        if(retarget()){
            targetClosest();

            if(target == null) targetClosestEnemyFlag(BlockFlag.factory);
            if(target == null) targetClosestEnemyFlag(BlockFlag.turret);
        }

        if(getClosestSpawner() == null && getClosestSpawner() != null && target == null){
            target = (Teamc) getClosestSpawner().team();
            circle(80f + Mathf.randomSeed(unit.id) * 120);
        }else if(target != null){
            attack(unit.range());

            float rotation = unit.rotation;
            if((Angles.near(unit.angleTo(target), rotation, getWeapon().shootCone) || getWeapon().ignoreRotation) //bombers and such don't care about rotation
                    && dst(target.x(), target.y()) < getWeapon().bullet.range){
                BulletType ammo = getWeapon().bullet;

                if(unit.isRotate()){
                    for(boolean left : Mathf.booleans){
                        int wi = Mathf.num(left);
                        float x = unit.x;
                        float y = unit.y;
                        float wx = x + Angles.trnsx(rotation - 90, getWeapon().x * Mathf.sign(left));
                        float wy = y + Angles.trnsy(rotation - 90, getWeapon().x * Mathf.sign(left));

                        weaponAngles[wi] = Mathf.slerpDelta(weaponAngles[wi], Angles.angle(wx, wy, target.getX(), target.getY()), 0.1f);

                        Tmp.v2.trns(weaponAngles[wi], getWeapon().y);
                        unit.aim(Tmp.v2);
                        getWeapon().update(unit, new WeaponMount(getWeapon()));
                    }
                }else{
                    Vec2 to = Predict.intercept(unit, target, ammo.speed);
                    unit.aim(to);
                }
            }
        }else{
            target = (Teamc) getClosestSpawner().team();
            moveTo(Vars.state.rules.dropZoneRadius + 120f);
        }
    }

    public Weapon getWeapon() {
        if(unit.mounts.length > 0) {
            for (int i = 1; i <= unit.mounts.length; i++) {
                return unit.mounts[i].weapon;
            }
        }else {
            return null;
        }
        return unit.mounts[1].weapon;
    }

    protected void circle(float circleLength){
        circle(circleLength, unit.type().speed);
    }

    protected void circle(float circleLength, float speed){
        if(target == null) return;

        vec.set(target).sub(unit);

        if(vec.len() < circleLength){
            vec.rotate((circleLength - vec.len()) / circleLength * 180f);
        }

        vec.setLength(speed * Time.delta);

        unit.moveAt(vec);
    }

    protected void moveTo(float circleLength){
        if(target == null) return;

        vec.set(target).sub(unit);

        float length = circleLength <= 0.001f ? 1f : Mathf.clamp((unit.dst(target) - circleLength) / 100f, -1f, 1f);

        vec.setLength(unit.type().speed * Time.delta * length);
        if(length < -0.5f){
            vec.rotate(180f);
        }else if(length < 0){
            vec.setZero();
        }

        unit.moveAt(vec);
    }

    protected void attack(float circleLength){
        vec.set(target).sub(unit);

        float ang = unit.angleTo(target);
        float diff = Angles.angleDist(ang, unit.rotation());

        if(diff > 100f && vec.len() < circleLength){
            vec.setAngle(unit.vel().angle());
        }else{
            vec.setAngle(Mathf.slerpDelta(unit.vel().angle(), vec.angle(), 0.6f));
        }

        vec.setLength(unit.type().speed * Time.delta);

        unit.moveAt(vec);
    }

    protected void targetClosestEnemyFlag(BlockFlag flag){
        Teamc target = Geometry.findClosest(unit.x(), unit.y(), indexer.getEnemy(unit.team(), flag));
        if(target != null) this.target = target;
    }

    protected void targetClosest(){
        //TODO optimize!
        Teamc newTarget = Units.closestTarget(unit.team(), unit.x(), unit.y(), Math.max(unit.range(), unit.type().range), u -> (unit.type().targetAir && u.isFlying()) || (unit.type().targetGround && !u.isFlying()));
        if(newTarget != null){
            target = newTarget;
        }
    }
}
