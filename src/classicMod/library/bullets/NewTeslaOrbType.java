package classicMod.library.bullets;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.pooling.Pool;
import classicMod.content.ExtendedFx;
import mindustry.Vars;
import mindustry.ai.BlockIndexer;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.entities.*;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;
import mindustry.world.Tile;

import java.util.concurrent.atomic.AtomicReference;

import static mindustry.Vars.*;

public class NewTeslaOrbType extends BulletType {

    float max;
    int hitCap;
    Seq<Teamc> TargetList;
    public Effect beamEffect = ExtendedFx.teslaBeam;

    /**
     * Creates a Tesla orb that jumps other enemy's unit/block.
     * @param maxRange The maximum range that the arc can jump to other team's unit/block. (tilesize)
     * @param damage Damage per tick
     * @param maxHits Maximum hits before despawning immediately.
     **/
    public NewTeslaOrbType(float maxRange, int damage, int maxHits){
        this.damage = damage;
        this.range = maxRange;
        this.max = maxRange;
        hitEffect = Fx.hitLancer; //ExtendedFx.laserhit;
        despawnEffect = Fx.none;
        drawSize = 200f;
        this.hitCap = maxHits;
        this.lifetime = Float.MAX_VALUE;
    }

    Vec2 interpolate(Vec2 start, Vec2 end, float div, float range) {
        Vec2 between = ((end.sub(start).div(new Vec2(div,div))).add(start));
        return new Vec2(between.x + Mathf.range(range), between.y + Mathf.range(range));
    }

    @Override
    public void update(Bullet b) {
        b.vel.setZero();
        TargetList = AutoTargetList(this.hitCap, b);
        if(TargetList.size > 0){
            Vec2 lastVec = new Vec2(b.x, b.y);
            for (var blasted : TargetList){
                Log.info(b.within(blasted, range / tilesize));
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
                //beamEffect.at(lastVec.x, lastVec.y, b.rotation(), Color.white, new Vec2().set(new Vec2(blasted.x(), blasted.y())));
                lastVec = new Vec2(blasted.x(), blasted.y());
                hitEffect.at(blasted.x(), blasted.y(), lightningColor);

                if(blasted instanceof Unit unit) unit.damage(b.damage);
                if(blasted instanceof Building building) building.damage(b.damage);
            }
            //beamEffect.at(lastVec.x, lastVec.y, b.rotation(), Color.white);
            hitEffect.at(lastVec.x, lastVec.y, lightningColor);
            b.time = b.lifetime + 1f;
            TargetList.clear();
        } else {
            b.time = b.lifetime + 1f;
        }
    }

    /**
     * Creates a list closest to the bullet.
     * @param Amount The maximum amount that it can auto target
     * @param b bullet
     * @return List of targets (Enemy side)
     **/
    public Seq<Teamc> AutoTargetList(int Amount, Bullet b){
        var tlist = new Seq<Teamc>();
        for (int i = 0; i < Amount - 1; i++) {
            var x = b.x;
            var y = b.y;
            var currentRange = range;
            Vec2 offset = new Vec2().trns(b.rotation(), currentRange);
            /*AtomicReference<Building> building = new AtomicReference<>();

            World.raycastEach(World.toTile(b.getX()), World.toTile(b.getY()), World.toTile(b.getX() + offset.getX()), World.toTile(b.getY() + offset.getY()), (wx, wy) -> {

                Tile tile = world.tile(wx, wy);
                if (tile != null && (tile.build != null)) building.set(tile.build);
                if (tile != null && (tile.build != null && tile.build.isInsulated()) && tile.team() != b.team) {
                    return true;
                }
                return false;
            });
            b.rotation(offset.sub(new Vec2(b.x, b.y)).angleRad());
            b.set(b.x + offset.x, b.y + offset.y);*/
            var target = Damage.linecast(b, x, y, b.rotation(), currentRange / Vars.tilesize);

            if(tlist.size > 0){
                var current = tlist.get(tlist.size - 1);
                x = current.x();
                y = current.y();
                currentRange = max;
            }

            Teamc target = Units.closestTarget(b.team, x, y, (currentRange / tilesize),
                    e -> e.isValid() && e.checkTarget(collidesAir, collidesGround) && !tlist.contains(e),
                    t -> false);

            Building build = indexer.findEnemyTile(b.team, x, y, (currentRange / tilesize) * b.fout(),
                    t -> t.isValid() && !tlist.contains(t.buildOn()));

            Log.info(target);
            Log.info(build);

            if (build != null && target != null) {
                if (build.dst2(b) < target.dst2(b)) tlist.add(target);
                if (build.dst2(b) > target.dst2(b)) tlist.add(build);
            } else {
                if (build != null) tlist.add(build);
                if (target != null) tlist.add(target);
            }


            /*Log.info(target);
            if(target != null){
                if (b.within(target, currentRange * b.fout())) tlist.add(target);
            }

            Teamc target = Units.closestTarget(b.team, x, y, currentRange * b.fout(),
                    e -> e.isValid() && e.checkTarget(collidesAir, collidesGround) && !tlist.contains(e),
                    t -> !t.dead && !tlist.contains(t));
            Log.info(targetC);
            if(targetC != null){
                if (b.within(target, currentRange * b.fout())) tlist.add(target);
            }*/
        }

        tlist.sort(t -> t.dst2(b));
        return tlist;
    }
}
