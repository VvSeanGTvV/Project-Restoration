package classicMod.library.bullets;

import arc.graphics.Color;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import classicMod.content.ExtendedFx;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

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
        hitEffect = ExtendedFx.laserhit;
        despawnEffect = Fx.none;
        drawSize = 200f;
        hitCap = maxHits;
        this.lifetime = Float.MAX_VALUE;
    }

    @Override
    public void update(Bullet b) {
        b.vel.setZero();
        TargetList = AutoTargetList(hitCap, b);
        if(TargetList.size > 0){
            Vec2 lastVec = new Vec2(b.x, b.y);
            for (var blasted : TargetList){
                beamEffect.at(lastVec.x, lastVec.y, b.rotation(), Color.white, new Vec2().set(new Vec2(blasted.x(), blasted.y())));
                lastVec = new Vec2(blasted.x(), blasted.y());

                if(blasted instanceof Unit unit) unit.damage(b.damage);
                if(blasted instanceof Building building) building.damage(b.damage);
            }
            beamEffect.at(lastVec.x, lastVec.y, b.rotation(), Color.white);
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
        while (tlist.size < Amount - 1){
            var x = b.x;
            var y = b.y;
            var currentRange = range;

            if(tlist.size > 0){
                var current = tlist.get(tlist.size - 1);
                x = current.x();
                y = current.y();
                currentRange = max;
            }
            Teamc valid = Units.closestTarget(b.team, x, y, currentRange * b.fout(),
                    e -> e.isValid() && e.checkTarget(collidesAir, collidesGround) && !tlist.contains(e),
                    t -> t.isValid() && !tlist.contains(t));
            if(valid != null){
                if(valid instanceof Unit || valid instanceof Building) tlist.add(valid);
            } else {
                break;
            }
        }
        return tlist;
    }
}
