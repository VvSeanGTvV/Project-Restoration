package classicMod.library.bullets;


import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import classicMod.content.*;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;


public class TeslaOrbType extends BulletType {
    /** Array of the listed target **/
    public @Nullable Teamc[] ArrayTarget;
    /** Array of the listed target's position **/
    public @Nullable Vec2[] ArrayVec2;
    /** Maximum hits before despawning **/
    public int hitCap;

    public Effect beamEffect = ExtendedFx.teslaBeam;

    // Temporary Values
    protected int l = 0;
    protected Teamc target;

    /**
     * Creates a Tesla orb that jumps other enemy's unit/block.
     * @param range The maximum range that the arc can jump to other team's unit/block.
     * @param damage Damage per tick
     * @param maxHits Maximum hits before despawning immediately.
     **/
    public TeslaOrbType(float range, int damage, int maxHits){
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
        super.update(b);
        if(l >= hitCap*2) { //Allows to detect whether if the bullet hit count has reached maximum peak.
            l = 0;
            ArrayTarget = null;
            ArrayVec2 = null;
            b.time = b.lifetime + 1f;
        }
        autoTarget(b);
        b.type.pierce = true;
        b.type.pierceCap = Integer.MAX_VALUE;
        if (ArrayTarget != null) for (Teamc target : ArrayTarget) {
            float x = target.getX();
            float y = target.getY();
            this.ArrayVec2 = new Vec2[]{new Vec2(x, y)};
            b.set(x, y);
            l++;
        }
    }

    /** AutoTargets the nearest enemy unit/block while keeping track on a listed array, this could be saved on {@link #ArrayTarget} **/
    public void autoTarget(Bullet b){ //from Prog-mats
        this.target = Units.closestTarget(b.team, b.x, b.y, (range / Vars.tilesize) * b.fout(),
                e -> e.isValid() && e.checkTarget(collidesAir, collidesGround) && !b.collided.contains(e.id) && !invalidateTargetArray(this.ArrayTarget, e),
                t -> false);
        if( target != null && target instanceof Unit) {
            this.ArrayTarget = new Teamc[]{this.target};
        } else {
            l = 0;
            ArrayTarget = null;
            ArrayVec2 = null;
            b.time = b.lifetime + 1f;
        }
    }

    public boolean invalidateTargetArray(Teamc[] Array, Teamc Current){
        boolean invalid = false;
        if(Array == null) return false;
        for(var Teamc : Array){
            invalid = (Current.id() == Teamc.id());
            if(invalid) break;
        }
        return invalid;
    }

    @Override
    public void draw(Bullet b) {
        Draw.color(Color.white);
        Vec2 lastVec = new Vec2(b.x, b.y);
        if(ArrayVec2 != null) for (Vec2 vec2 : ArrayVec2){
            beamEffect.at(lastVec.x, lastVec.y, b.rotation(), Color.white, new Vec2().set(vec2));

            b.vel = new Vec2();
            if(lastVec!=vec2) lastVec = vec2;
        }
    }
}
