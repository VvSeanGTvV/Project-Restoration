package classicMod.library.blocks.classicBlocks;

import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.world.blocks.defense.turrets.*;

public class MirroredItemTurret extends ItemTurret {
    public MirroredItemTurret(String name) {
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

        @Override
        public void updateShooting(){
            if(reloadCounter >= reload && !charging() && shootWarmup >= minWarmup) {
                Building entity = tile.build;
                reloadCounter %= reload;
                BulletType type = peekAmmo();

                for (int i = -1; i < 1; i++) {
                    //shoot(type);
                    recoilOffset.trns(entity.rotation, len, Mathf.sign(i) * space);
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
                    shootEffect.at(tile.drawx() + len, tile.drawy() + space, entity.rotation);

                    //Effects.effect(shootEffect, tile.drawx() + tr.x, tile.drawy() + tr.y, entity.rotation);
                }

                Effect.shake(1f, 1f, tile.worldx(), tile.worldy());
            }
        }
    }
}
