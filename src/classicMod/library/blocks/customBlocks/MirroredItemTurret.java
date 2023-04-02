package classicMod.library.blocks.customBlocks;

import arc.math.*;
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


        protected void bullet(BulletType type, float xOffset, float yOffset, float angleOffset, Mover mover){
            queuedBullets --;

            if(dead || (!consumeAmmoOnce && !hasAmmo())) return;

            float
                    xSpread = Mathf.range(xRand),
                    //LEFT
                    bulletX = x + Angles.trnsx(rotation - 90, shootX + xOffset + xSpread + space, shootY + yOffset - space),
                    bulletY = y + Angles.trnsy(rotation - 90, shootX + xOffset + xSpread + space, shootY + yOffset - space),
                    //RIGHT
                    bulletCX = x + Angles.trnsx(rotation - 90, shootX + xOffset + xSpread - space, shootY + yOffset - space),
                    bulletCY = y + Angles.trnsy(rotation - 90, shootX + xOffset + xSpread - space, shootY + yOffset - space),
                    shootAngle = rotation + angleOffset + Mathf.range(inaccuracy + type.inaccuracy),
                    CshootAngle = rotation + angleOffset + Mathf.range(-inaccuracy + type.inaccuracy);

            float lifeScl = type.scaleLife ? Mathf.clamp(Mathf.dst(bulletX, bulletY, targetPos.x, targetPos.y) / type.range, minRange / type.range, range() / type.range) : 1f;

            //TODO aimX / aimY for multi shot turrets?

            for (int i = -1; i < 1; i++) {
                if(i==0)handleBullet(type.create(this, team, bulletX, bulletY, shootAngle, -1f, (1f - velocityRnd) + Mathf.random(velocityRnd), lifeScl, null, mover, targetPos.x, targetPos.y), xOffset, yOffset, shootAngle - rotation); //LEFT
                if(i==-1)handleBullet(type.create(this, team, bulletCX, bulletCY, CshootAngle, -1f, (1f - velocityRnd) + Mathf.random(velocityRnd), lifeScl, null, mover, targetPos.x, targetPos.y), xOffset, yOffset, shootAngle - rotation); //RIGHT
            }
            //handleBullet(type.create(this, team, bulletCX, bulletCY, -shootAngle, -1f, (1f - velocityRnd) + Mathf.random(velocityRnd), lifeScl, null, mover, targetPos.x, targetPos.y), xOffset, yOffset, shootAngle - rotation); //RIGHT

            (shootEffect == null ? type.shootEffect : shootEffect).at(bulletX, bulletY, rotation + angleOffset, type.hitColor); //LEFT
            (shootEffect == null ? type.shootEffect : shootEffect).at(bulletCX, bulletCY, rotation + angleOffset, type.hitColor); //RIGHT
            (smokeEffect == null ? type.smokeEffect : smokeEffect).at(bulletX, bulletY, rotation + angleOffset, type.hitColor); //LEFT
            (smokeEffect == null ? type.smokeEffect : smokeEffect).at(bulletCX, bulletCY, rotation + angleOffset, type.hitColor); //RIGHT
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
                useAmmo();
            }
        }

        @Override
        public void updateShooting(){
            if(reloadCounter >= reload && !charging() && shootWarmup >= minWarmup) {
                //Building entity = tile.build;
                reloadCounter %= reload;
                BulletType type = peekAmmo();

                /*for (int i = -1; i < 1; i++) {
                    //shoot(type);
                    //recoilOffset.trns(rotation, len, Mathf.sign(i) * space);

                    //bullet(tile.bullet, entity.rotation);
                    //shootEffect.at(tile.drawx() + len, tile.drawy() + space, rotation);
                    //shootEffect.at(tile.drawx() - len, tile.drawy() + space, rotation);


                    //Effects.effect(shootEffect, tile.drawx() + tr.x, tile.drawy() + tr.y, entity.rotation);
                }*/
                shoot.shoot(totalShots, (xOffset, yOffset, angle, delay, mover) -> {
                    queuedBullets ++;
                    if(delay > 0f){
                        Time.run(delay, () -> bullet(type, xOffset, yOffset, angle, mover));
                    }else{
                        bullet(type, xOffset, yOffset, angle, mover);
                    }
                    totalShots ++;
                });
                if(consumeAmmoOnce){
                    useAmmo();
                    useAmmo();
                }

                Effect.shake(1f, 1f, this);
            }
        }
    }
}
