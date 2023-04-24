package classicMod.library.bullets;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import classicMod.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class LaserOnTargetType extends BulletType {

    public Color beamColor = Color.white;
    public Effect beamEffect = ExtendedFx.laserBeam;

    // Temporary Values
    protected int l = 0;
    protected Teamc target;

    /**
     * Creates a Laser bullet that goes on target.
     * @param range The maximum range
     * @param damage Damage per tick
     **/
    public LaserOnTargetType(float range, int damage){
        this.damage = damage;
        this.range = range;
        hitEffect = ExtendedFx.laserhit;
        drawSize = 200f;
        this.lifetime = 30f;
    }

    /**
     * Creates a Laser bullet that goes on target.
     * @param range The maximum range
     * @param damage Damage per tick
     * @param beamC Color of the beam
     **/
    public LaserOnTargetType(float range, int damage, Color beamC){
        this.damage = damage;
        this.range = range;
        this.beamColor = beamC;
        hitEffect = ExtendedFx.laserhit;
        drawSize = 200f;
        this.lifetime = 30f;
    }

    @Override
    public void update(Bullet b) {
        super.update(b);
        autoTarget(b);
    }

    /** AutoTargets the nearest enemy unit/block **/
    public void autoTarget(Bullet b){ //from Prog-mats
        this.target = Units.closestTarget(b.team, b.x, b.y, range * b.fout(),
                e -> e.isValid() && e.checkTarget(collidesAir, collidesGround),
                t -> t.isValid() && collidesGround);
        if( target == null ) {
            l = 0;
            b.time = b.lifetime + 1f;
        }
    }

    @Override
    public void draw(Bullet b) {
        Draw.color(beamColor);
        Vec2 lastVec = new Vec2(b.x, b.y);
        if(target != null) {
            beamEffect.at(lastVec.x, lastVec.y, b.rotation(), beamColor, new Vec2().set(target));
            b.set(target);
        }
    }
}
