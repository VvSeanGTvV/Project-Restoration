package classicMod.library.bullets;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import classicMod.content.ExtendedFx;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;

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
        this.hitCap = maxHits;
        this.lifetime = Float.MAX_VALUE;
    }

    boolean hasReachedTarget(Vec2 position, Vec2 targetPosition) {
        float distanceX = Math.abs(position.x - targetPosition.x);
        float distanceY = Math.abs(position.y - targetPosition.y);
        return distanceX < 1 && distanceY < 1;  // Consider the target reached if within 1 unit
    }

    private Vec2 calculateDirectionFromRotation(float rotationDegrees) {
        // Convert degrees to radians for trigonometric functions
        double radians = Math.toRadians(rotationDegrees);
        return new Vec2((float)Math.cos(radians), (float)Math.sin(radians));
    }

    Seq<Vec2> createLightning(Bullet b, Vec2 targetPosition){
        int stepCount = 0;
        Seq<Vec2> temporaryData = new Seq<>();
        Vec2 bPos = new Vec2(b.x, b.y);

        float startRot = b.rotation();
        while (!hasReachedTarget(bPos, targetPosition)) {
            stepCount++;
            b.rotation(Mathf.rand.nextFloat() * 360);
            Vec2 rotPos = calculateDirectionFromRotation(b.rotation());

            b.x += rotPos.x;
            b.y += rotPos.y;
            bPos = new Vec2(b.x, b.y);

            if (stepCount % 10 == 0){
                temporaryData.add(bPos);
            }
        }

        b.rotation(startRot);
        b.set(targetPosition);
        return temporaryData;
    }

    @Override
    public void update(Bullet b) {
        b.vel.setZero();
        TargetList = AutoTargetList(this.hitCap, b);
        if(TargetList.size > 0){
            Vec2 lastVec = new Vec2(b.x, b.y);
            for (var blasted : TargetList){
                var lData = createLightning(b, new Vec2(blasted.x(), blasted.y()));
                Fx.lightning.at(lastVec.x, lastVec.y, b.rotation(), lightningColor, lData);
                //beamEffect.at(lastVec.x, lastVec.y, b.rotation(), Color.white, new Vec2().set(new Vec2(blasted.x(), blasted.y())));
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
