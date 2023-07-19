package classicMod.library.ai;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import classicMod.library.blocks.legacyBlocks.LegacyCommandCenter;
import mindustry.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.meta.*;

import static arc.math.Mathf.*;
import static arc.scene.actions.Actions.*;
import static mindustry.Vars.*;

public class OldFlyingAI extends RallyAI {
    protected float[] weaponAngles = {0,0}; //it's old lolz

    @Override
    public void updateMovement(){
        if(state == UnitState.attack) {

            if (unit.isFlying()) {
                wobble(); //old wobble
            }

            if (Units.invalidateTarget(target, unit.team(), unit.x, unit.y)) {
                target = null;
            }

            if (retarget()) {
                targetClosest();

                if (target == null) targetClosestEnemyFlag(BlockFlag.factory);
                if (target == null) targetClosestEnemyFlag(BlockFlag.turret);
            }

            if (getClosestSpawner() == null && getClosestSpawner() != null && target == null) { //&& getSpawner() != null
                target = unit.closestEnemyCore();
                circle(80f + Mathf.randomSeed(unit.id) * 120);
            } else if (target != null) {
                attack(unit.range());

                float rotation = unit.rotation;
                if ((Angles.near(unit.angleTo(target), rotation, getWeapon().shootCone) || getWeapon().ignoreRotation) && dst(target.x(), target.y()) < getWeapon().bullet.range) { //bombers and such don't care about rotation
                    BulletType ammo = getWeapon().bullet;

                    if (unit.isRotate()) {
                        for (boolean left : Mathf.booleans) {
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
                    } else {
                        Vec2 to = Predict.intercept(unit, target, ammo.speed);
                        unit.aim(to.x, to.y);
                        getWeapon().update(unit, new WeaponMount(getWeapon()));
                    }
                }
            } else {
                target = unit.closestEnemyCore();
                moveTo(Vars.state.rules.dropZoneRadius + 120f);
            }
        }
        if(state == UnitState.rally){
            if(retarget()){
                NearbyCenter();
                targetClosest();

                if(target != null && !Units.invalidateTarget(target, unit.team, unit.x, unit.y)){
                    state = UnitState.attack;
                }

                //if(target == null) target = unit.closestEnemyCore();
            }
            NearbyCenter();
            building = Units.closestBuilding(unit.team, unit.x, unit.y, Float.MAX_VALUE, b -> (b instanceof LegacyCommandCenter.LegacyCommandCenterBuild) && b.isValid() && !(b.isNull()));
            Log.info(building);
            if(building != null){
                circleBlock(65f + Mathf.randomSeed(unit.id) * 100);
            }


            /*if(target != null){
                circle(65f + Mathf.randomSeed(unit.id) * 100);
            }*/
        }
    }

    public Weapon getWeapon() { //hehe updated getweapon() to modernize list
        for(WeaponMount w : unit.mounts){
            return w.weapon;
        }
        return unit.mounts[1].weapon;

        /*if(unit.mounts.length > 0) {
            for (int i = 1; i <= unit.mounts.length; i++) {
                return unit.mounts[i].weapon;
            }
        }else {
            return null;
        }
        return unit.mounts[1].weapon;*/
    }

    protected void wobble(){
        if(net.client()) return;

        unit.x += Mathf.sin(Time.time + unit.id * 999, 25f, 0.05f) * Time.delta;
        unit.y += Mathf.cos(Time.time + unit.id * 999, 25f, 0.05f) * Time.delta;

        if(unit.vel.len() <= 0.05f){
            //rotation += Mathf.sin(Time.time() + id * 99, 10f, 2f * type.speed)*Time.delta(); uh why this exist
        }
    }

    protected void updateRotation(){
        unit.rotation = unit.vel.angle();
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

    protected void circleBlock(float circleLength){
        circleBlock(circleLength, unit.type().speed);
    }

    protected void circleBlock(float circleLength, float speed){
        if(building == null) return;

        vec.set(building).sub(unit);

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
        if(target == null) return;

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
        Teamc newTarget = Units.closestTarget(unit.team(), unit.x(), unit.y(), Math.max(unit.range(), unit.type().range), u -> (unit.type().targetAir && u.isFlying()) || (unit.type().targetGround && !u.isFlying()));
        if(newTarget != null){
            target = newTarget;
        }
    }

    /*Tile getSpawner(){ //TODO somehow get the tile of the spawner but its too old lolz
        return world.tile(spawner.getFirstSpawn().pos());
    }*/
}
