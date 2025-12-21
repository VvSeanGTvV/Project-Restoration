package classicMod.library.blocks.customBlocks;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.Effect;
import mindustry.entities.Mover;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.heat.HeatProducer;
import mindustry.world.consumers.ConsumeLiquidFilter;

import static arc.Core.atlas;
import static mindustry.Vars.tilesize;

public class HeatedItemTurret extends ItemTurret {

    public float heatMaximum = 50f;
    public float heatPerShot = 0.15f;
    public float pitchIncrement = 3f;

    protected TextureRegion heatRegion;
    public HeatedItemTurret(String name) {
        super(name);
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("heat", (HeatedItemTurretBuild entity) -> new Bar("bar.heat", Pal.lightOrange, () -> entity.warmth / heatMaximum));
    }

    @Override
    public void load() {
        super.load();
        heatRegion = atlas.find(name + "-heat");
    }

    public class HeatedItemTurretBuild extends ItemTurretBuild implements HeatBlock {
        public float warmth, multiplier;
        public boolean isHot;

        @Override
        public void updateTile(){
            //heat approaches target at the same speed regardless of efficiency
            multiplier = 1f + ((coolant.consumes(liquids.current()) && this.liquids.get(liquids.current()) > 0f) ? coolant.amount * (this.liquids.get(liquids.current()) / this.block.liquidCapacity) * coolantMultiplier * liquids.current().heatCapacity : 0f);
            if (warmth > 0 && !isActive() || isHot) warmth = Mathf.approachDelta(warmth, 0f, (heatPerShot / 5f) * multiplier * delta());
            Log.info(multiplier + " | liq : " + liquids.current() + " | " + (liquids.current().heatCapacity / 0.4f));

            if (warmth >= heatMaximum) {
                isHot = true;
                warmth = heatMaximum;
            }
            if (warmth <= heatMaximum/2f) isHot = false;

            if (isHot){
                if(Mathf.chance(coolant.amount * 0.06 * (this.liquids.get(liquids.current()) / this.block.liquidCapacity))){
                    //Sounds.loopSteam.at(this);
                    coolEffect.at(x + Mathf.range(size * tilesize / 2f), y + Mathf.range(size * tilesize / 2f));
                    coolant.trigger(this);
                }
            }

            super.updateTile();
        }

        @Override
        protected void updateShooting() {
            if(reloadCounter >= reload && !charging() && shootWarmup >= minWarmup && !isHot){
                BulletType type = peekAmmo();

                shoot(type);

                reloadCounter %= reload;
            }
        }

        @Override
        protected void bullet(BulletType type, float xOffset, float yOffset, float angleOffset, Mover mover) {
            queuedBullets --;
            warmth += heatPerShot * delta(); //Mathf.approachDelta(heat, heatOutput * efficiency, warmupRate * delta());

            if(isHot || dead || (!consumeAmmoOnce && !hasAmmo())) return;

            float
                    xSpread = Mathf.range(xRand),
                    bulletX = x + Angles.trnsx(rotation - 90, shootX + xOffset + xSpread, shootY + yOffset),
                    bulletY = y + Angles.trnsy(rotation - 90, shootX + xOffset + xSpread, shootY + yOffset),
                    shootAngle = rotation + angleOffset + Mathf.range(inaccuracy + type.inaccuracy);

            float lifeScl = type.scaleLife ? Mathf.clamp((1 + scaleLifetimeOffset) * Mathf.dst(bulletX, bulletY, targetPos.x, targetPos.y) / type.range, minRange() / type.range, range() / type.range) : 1f;

            //TODO aimX / aimY for multi shot turrets?
            handleBullet(type.create(this, team, bulletX, bulletY, shootAngle, -1f, (1f - velocityRnd) + Mathf.random(velocityRnd), lifeScl, null, mover, targetPos.x, targetPos.y), xOffset, yOffset, shootAngle - rotation);

            (shootEffect == null ? type.shootEffect : shootEffect).at(bulletX, bulletY, rotation + angleOffset, type.hitColor);
            (smokeEffect == null ? type.smokeEffect : smokeEffect).at(bulletX, bulletY, rotation + angleOffset, type.hitColor);
            (type.shootSound != Sounds.none ? type.shootSound : shootSound).at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax) + (pitchIncrement * heatFrac()), shootSoundVolume);

            ammoUseEffect.at(
                    x - Angles.trnsx(rotation, ammoEjectBack),
                    y - Angles.trnsy(rotation, ammoEjectBack),
                    rotation * Mathf.sign(xOffset)
            );

            if(shake > 0){
                Effect.shake(shake, shake, this);
            }

            curRecoil = 1f;
            if(recoils > 0){
                curRecoils[barrelCounter % recoils] = 1f;
            }
            heat = 1f;
            totalShots++;

            if(!consumeAmmoOnce){
                useAmmo();
            }
        }

        @Override
        public float heatFrac(){
            return warmth / heatMaximum;
        }

        @Override
        public float heat(){
            return warmth;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(warmth);
            write.bool(isHot);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            warmth = read.f();
            isHot = read.bool();
        }
    }
}
