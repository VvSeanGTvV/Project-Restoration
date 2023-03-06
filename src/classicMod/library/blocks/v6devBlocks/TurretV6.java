package classicMod.library.blocks.v6devBlocks;

import arc.*;
import arc.audio.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.entities.Units.*;
import mindustry.entities.bullet.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.world.blocks.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;
import static mindustry.gen.Nulls.*;

import mindustry.world.blocks.defense.turrets.*;

public class TurretV6 extends Turret { //v6's coding for v7
    /* Restitution modifies recoil */
    public float restitution = 0.02f;
    public float shootLength = -1;
    public TurretV6(String name) {
        super(name);
    }

    @Override
    public void init(){

        if(shootLength < 0) shootLength = size * tilesize / 2f;

        super.init();
    }

    public class TurretV6Build extends TurretBuild implements ControlBlock {
        @Override
        public void updateTile() {

            wasShooting = false;

            recoil = Mathf.lerpDelta(recoil, 0f, restitution);
            heat = Mathf.lerpDelta(heat, 0f, cooldownTime);

            unit.health(health);
            unit.rotation(rotation);
            unit.team(team);
            unit.set(x, y);

            if (logicControlTime > 0) {
                logicControlTime -= Time.delta;
            }

            if (hasAmmo()) {

                if (timer(timerTarget, targetInterval)) {
                    findTarget();
                }

                if (validateTarget()) {
                    boolean canShoot = true;

                    if (isControlled()) { //player behavior
                        targetPos.set(unit.aimX(), unit.aimY());
                        canShoot = unit.isShooting();
                    } else if (logicControlled()) { //logic behavior
                        canShoot = logicShooting;
                    } else { //default AI behavior
                        targetPosition(target);

                        if (Float.isNaN(rotation)) {
                            rotation = 0;
                        }
                    }

                    float targetRot = angleTo(targetPos);

                    if (shouldTurn()) {
                        turnToTarget(targetRot);
                    }

                    if (Angles.angleDist(rotation, targetRot) < shootCone && canShoot) {
                        wasShooting = true;
                        updateShooting();
                    }
                }
            }
        }

        @Override
        protected void shoot(BulletType type){
            Vec2 tr = new Vec2(); //TODO: fix some stuff here
            tr.trns(rotation, shootLength);
            float
                    bulletX = x + Angles.trnsx(rotation - 90, shootX, shootY),
                    bulletY = y + Angles.trnsy(rotation - 90, shootX, shootY);
            if(shoot.firstShotDelay > 0){
                chargeSound.at(bulletX + tr.x, bulletY + tr.y, Mathf.random(soundPitchMin, soundPitchMax));
                type.chargeEffect.at(bulletX + tr.x, bulletY + tr.y, rotation);
            }
            shoot.shoot(totalShots, (xOffset, yOffset, angle, delay, mover) -> {
                queuedBullets ++;
                if(delay > 0f){
                    Time.run(delay, () -> {
                                float thisDud = Mathf.range(inaccuracy + type.inaccuracy) + (queuedBullets - (int) (shoot.shots / 2f)) * xRand;
                                bullet(type, xOffset + tr.x, yOffset + tr.y, rotation + thisDud, mover);
                            }
                    );
                }else{
                    bullet(type, xOffset + tr.x, yOffset + tr.y, rotation + Mathf.range(inaccuracy), mover);
                }
                totalShots ++;
            });

            if(consumeAmmoOnce){
                useAmmo();
            }
        }
    }
}
