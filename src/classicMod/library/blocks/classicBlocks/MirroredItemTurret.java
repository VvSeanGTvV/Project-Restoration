package classicMod.library.blocks.classicBlocks;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.world.blocks.defense.turrets.*;

public class MirroredItemTurret extends ItemTurret { //This is meant for classic blocks not modern ones!
    public MirroredItemTurret(String name) { //Same style as Item Turret but has two barrels in use
        super(name);
    }

    public class MirroredTurretItemBuild extends ItemTurretBuild {
        

        float len = 8;
        float space = 3.5f;
        Vec2 tr = new Vec2(3,3); //idk what it was ok

        /*@Override
        protected void shoot(BulletType type) {

            for (int i = -1; i < 1; i++) {
                recoilOffset.trns(entity.rotation, len, Mathf.sign(i) * space);
                shoot.shoot(totalShots, (xOffset, yOffset, angle, delay, mover) -> {
                    queuedBullets++;
                    if (delay > 0f) {
                        Time.run(delay, () -> bullet(type, xOffset, yOffset, angle, mover));
                    } else {
                        bullet(type, xOffset, yOffset, angle, mover);
                    }
                    totalShots++;
                });
                shootEffect.at(tile.drawx() + tr.x, tile.drawy() + tr.y, entity.rotation);
            }
            Effect.shake(1f, 1f, this);

            if(consumeAmmoOnce){
                useAmmo();
            }
        }*/

        protected void bullet(BulletType type, float xOffset, float yOffset, float angleOffset, Mover mover){
            queuedBullets --;

            if(dead || (!consumeAmmoOnce && !hasAmmo())) return;

            float
                    xSpread = Mathf.range(xRand),
                    bulletX = x + Angles.trnsx(rotation - 90, shootX + xOffset + xSpread + space, shootY + yOffset + space),
                    bulletY = y + Angles.trnsy(rotation - 90, shootX + xOffset + xSpread + space, shootY + yOffset + space),
                    bulletCX = x + Angles.trnsx(rotation - 90, shootX + xOffset + xSpread - space, shootY + yOffset - space),
                    bulletCY = y + Angles.trnsy(rotation - 90, shootX + xOffset + xSpread - space, shootY + yOffset - space),
                    shootAngle = rotation + angleOffset + Mathf.range(inaccuracy + type.inaccuracy);

            float lifeScl = type.scaleLife ? Mathf.clamp(Mathf.dst(bulletX, bulletY, targetPos.x, targetPos.y) / type.range, minRange / type.range, range() / type.range) : 1f;

            //TODO aimX / aimY for multi shot turrets?
            handleBullet(type.create(this, team, bulletX, bulletY, shootAngle, -1f, (1f - velocityRnd) + Mathf.random(velocityRnd), lifeScl, null, mover, targetPos.x, targetPos.y), xOffset, yOffset, shootAngle - rotation);
            handleBullet(type.create(this, team, bulletCX, bulletY, shootAngle, -1f, (1f - velocityRnd) + Mathf.random(velocityRnd), lifeScl, null, mover, targetPos.x, targetPos.y), xOffset, yOffset, shootAngle - rotation);

            (shootEffect == null ? type.shootEffect : shootEffect).at(bulletX, bulletY, rotation + angleOffset, type.hitColor);
            (shootEffect == null ? type.shootEffect : shootEffect).at(bulletCX, bulletY, rotation + angleOffset, type.hitColor);
            (smokeEffect == null ? type.smokeEffect : smokeEffect).at(bulletX, bulletY, rotation + angleOffset, type.hitColor);
            (smokeEffect == null ? type.smokeEffect : smokeEffect).at(bulletCX, bulletY, rotation + angleOffset, type.hitColor);
            shootSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax));

            ammoUseEffect.at(
                    x - Angles.trnsx(rotation, ammoEjectBack),
                    y - Angles.trnsy(rotation, ammoEjectBack),
                    rotation * Mathf.sign(xOffset)
            );

            if(shake > 0){
                Effect.shake(shake, shake, this);
            }

            curRecoil = 1f;
            heat = 1f;

            if(!consumeAmmoOnce){
                useAmmo();
            }
        }

        @Override
        public void updateShooting(){
            if(reloadCounter >= reload && !charging() && shootWarmup >= minWarmup) {
                //Building entity = tile.build;
                reloadCounter %= reload;
                BulletType type = peekAmmo();

                for (int i = -1; i < 1; i++) {
                    //shoot(type);
                    //recoilOffset.trns(rotation, len, Mathf.sign(i) * space);
                    shoot.shoot(totalShots, (xOffset, yOffset, angle, delay, mover) -> {
                        queuedBullets ++;
                        if(delay > 0f){
                            Time.run(delay, () -> bullet(type, xOffset, yOffset, angle, mover));
                        }else{
                            bullet(type, xOffset, yOffset, angle, mover);
                        }
                        totalShots ++;
                    });
                    //bullet(tile.bullet, entity.rotation);
                    //shootEffect.at(tile.drawx() + len, tile.drawy() + space, rotation);
                    //shootEffect.at(tile.drawx() - len, tile.drawy() + space, rotation);


                    //Effects.effect(shootEffect, tile.drawx() + tr.x, tile.drawy() + tr.y, entity.rotation);
                }
                if(consumeAmmoOnce){
                    useAmmo();
                }

                Effect.shake(1f, 1f, this);
            }
        }
    }
}
