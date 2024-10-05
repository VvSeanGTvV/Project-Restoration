package classicMod.library.bullets;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.pooling.Pool;
import classicMod.content.ExtendedFx;
import mindustry.Vars;
import mindustry.ai.BlockIndexer;
import mindustry.content.*;
import mindustry.core.World;
import mindustry.entities.*;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;
import mindustry.type.StatusEffect;
import mindustry.world.*;

import java.util.concurrent.atomic.AtomicReference;

import static mindustry.Vars.*;

public class NewTeslaOrbType extends BulletType {

    float rangeB;
    public int hitCap;
    Seq<Teamc> TargetList;

    /**
     * Creates a Tesla orb that jumps other enemy's unit/block.
     * @param maxRange The maximum range that the arc can jump to other team's unit/block. (tilesize)
     * @param damage Damage per tick
     **/
    public NewTeslaOrbType(float maxRange, int damage){
        this.damage = damage;
        this.rangeB = maxRange;
        hitEffect = Fx.hitLancer; //ExtendedFx.laserhit;
        despawnEffect = Fx.none;
        drawSize = 200f;
        this.lifetime = Float.MAX_VALUE;
    }

    Vec2 interpolate(Vec2 start, Vec2 end, float div, float range) {
        Vec2 between = ((end.sub(start).div(new Vec2(div,div))).add(start));
        return new Vec2(between.x + Mathf.range(range), between.y + Mathf.range(range));
    }

    void taserTarget(Bullet b){
        if(TargetList.size > 0){
            Vec2 lastVec = new Vec2(b.x, b.y);
            for (var blasted : TargetList){
                if (!(b.within(blasted, range / tilesize))) {
                    continue;
                }
                Vec2 blastPos = new Vec2(blasted.x(), blasted.y());
                Seq<Vec2> lData = new Seq<>(new Vec2[]{
                        new Vec2(lastVec.x, lastVec.y),
                        interpolate(lastVec, blastPos, 1.25f, lightningLength + Mathf.random(lightningLengthRand)),
                        interpolate(lastVec, blastPos, 2.25f, lightningLength + Mathf.random(lightningLengthRand)),
                        new Vec2(blasted.x(), blasted.y())
                });
                Fx.lightning.at(lastVec.x, lastVec.y, b.rotation(), lightningColor, lData);
                lastVec = new Vec2(blasted.x(), blasted.y());
                hitEffect.at(blasted.x(), blasted.y(), lightningColor);

                if(blasted instanceof Unit unit) {
                    unit.damage(b.damage);
                    unit.apply(status, statusDuration);
                }

                if(blasted instanceof Building building) building.damage(b.damage * buildingDamageMultiplier);
            }
            //beamEffect.at(lastVec.x, lastVec.y, b.rotation(), Color.white);
            hitEffect.at(lastVec.x, lastVec.y, lightningColor);
            b.time = b.lifetime + 1f;
            TargetList.clear();
        }
    }

    @Override
    public void update(Bullet b) {
        b.vel.setZero();
        TargetList = AutoTargetList(this.hitCap, b);
        if(TargetList.size > 0){
            taserTarget(b);
        } else {
            Vec2 bulletPosition = new Vec2(b.x, b.y);
            Vec2 orientated = new Vec2().trns(b.rotation(), rangeB + (hitCap * 5));
            Vec2 movePosition = new Vec2(b.x + orientated.x, b.y + orientated.y);

            Seq<Vec2> lData = new Seq<>(new Vec2[]{
                    new Vec2(bulletPosition.x, bulletPosition.y),
                    interpolate(bulletPosition, movePosition, 1.25f, lightningLength + Mathf.random(lightningLengthRand)),
                    interpolate(bulletPosition, movePosition, 2.25f, lightningLength + Mathf.random(lightningLengthRand)),
                    new Vec2(movePosition.x, movePosition.y)
            });
            Fx.lightning.at(bulletPosition.x, bulletPosition.y, b.rotation(), lightningColor, lData);
            b.set(movePosition);
            TargetList = AutoTargetList(this.hitCap, b);
            if(TargetList.size <= 0) {
                b.time = b.lifetime + 1f;
            }
        }
    }


    float isBuild (Teamc teamc, Seq<Teamc> buildingSeq) {
        return (buildingSeq.contains(teamc)) ? 1f : 0f;
    }

    /**
     * Creates a list closest to the bullet.
     * @param Amount The maximum amount that it can auto target
     * @param b bullet
     * @return List of targets (Enemy side)
     **/
    public Seq<Teamc> AutoTargetList(int Amount, Bullet b){
        var teamcSeq = new Seq<Teamc>();
        var buildingSeq = new Seq<Teamc>();
        for (int i = 0; i < Amount - 1; i++) {
            var x = b.x;
            var y = b.y;
            var currentRange = rangeB;

            if(teamcSeq.size > 0){
                var current = teamcSeq.get(teamcSeq.size - 1);
                x = current.x();
                y = current.y();
                currentRange = rangeB * b.fout();
            }

            Teamc target = Units.closestTarget(b.team, x, y, currentRange * b.fout(),
                    e -> e.isValid() && e.checkTarget(collidesAir, collidesGround) && !teamcSeq.contains(e),
                    t -> false);

            Building build = indexer.findEnemyTile(b.team, x, y, currentRange * b.fout(),
                    t -> t.isValid() && !teamcSeq.contains(t.buildOn()));

            if (build != null && target != null) {
                if ((b.dst2(target) / tilesize) < rangeB * rangeB) teamcSeq.add(target);
                if (build.dst2(b) / tilesize < rangeB * rangeB) {
                    teamcSeq.add(build);
                    buildingSeq.add(build);
                }
            } else {
                if (build != null) {
                    teamcSeq.add(build);
                    buildingSeq.add(build);
                }
                if (target != null) teamcSeq.add(target);
            }
            if (build != null) if (build.buildOn().isInsulated()) break;
        }

        teamcSeq.sort(t -> isBuild(t, buildingSeq));
        return teamcSeq;
    }
}
