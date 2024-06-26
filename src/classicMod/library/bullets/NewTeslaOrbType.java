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
import mindustry.gen.Bullet;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

public class NewTeslaOrbType extends BulletType {

    int hitCap;
    Seq<Teamc> TargetList;
    public Effect beamEffect = ExtendedFx.teslaBeam;
    /**
     * Creates a Tesla orb that jumps other enemy's unit/block.
     * @param range The maximum range that the arc can jump to other team's unit/block.
     * @param damage Damage per tick
     * @param maxHits Maximum hits before despawning immediately.
     **/
    public NewTeslaOrbType(float range, int damage, int maxHits){
        this.damage = damage;
        this.range = range;
        hitEffect = ExtendedFx.laserhit;
        despawnEffect = Fx.none;
        drawSize = 200f;
        hitCap = maxHits;
        this.lifetime = Float.MAX_VALUE;
    }

    @Override
    public void update(Bullet b) {
        b.vel = new Vec2();
        TargetList = AutoTargetList(hitCap, b);
        if(TargetList.size > 0){
            Vec2 lastVec = new Vec2(b.x, b.y);
            for (var blasted : TargetList){
                beamEffect.at(lastVec.x, lastVec.y, b.rotation(), Color.white, new Vec2().set(new Vec2(blasted.x(), blasted.y())));
                lastVec = new Vec2(blasted.x(), blasted.y());
                if(blasted instanceof Unit u){
                    u.damage(b.damage);
                }
            }
            TargetList.clear();
        }
    }

    public Seq<Teamc> AutoTargetList(int Amount, Bullet b){
        var tlist = new Seq<Teamc>();
        while (tlist.size < Amount){
            Teamc valid = Units.closestTarget(b.team, b.x, b.y, (range / Vars.tilesize) * b.fout(),
                    e -> e.isValid() && e.checkTarget(collidesAir, collidesGround) && !b.collided.contains(e.id) && !tlist.contains(e),
                    t -> false);
            if(valid != null){
                if(valid instanceof Unit) tlist.add(valid);
            }
        }
        return tlist;
    }
}
