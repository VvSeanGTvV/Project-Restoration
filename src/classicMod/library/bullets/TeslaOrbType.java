package classicMod.library.bullets;


import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import classicMod.content.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;


public class TeslaOrbType extends BulletType { //MIXED VERSION betweem PointBullet and LaserBullet
    //private Array<Vector2> points = new Array<>();
    //private ObjectSet<Enemy> hit = new ObjectSet<>();
    private static float cdist = 0f;
    private static Unit result;

    public float trailSpacing = 10f;

    private int damage = 0;
    private float range = 0;
    private float lifetime = 30f;
    private float life = 0f;
    private float px, py;
    private Vec2 previous = new Vec2();

    public TeslaOrbType(float range, int damage){
        this.damage = damage;
        this.range = range;
        hitEffect = ExtendedFx.laserhit;
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        previous = new Vec2(b.x, b.y);

        px = b.x + b.lifetime * b.vel.x;
        py = b.y + b.lifetime * b.vel.y;
        float rot = b.rotation();

        Geometry.iterateLine(0f, b.x, b.y, px, py, trailSpacing, (x, y) -> {
            trailEffect.at(x, y, rot);
        });

        //b.time = b.lifetime;
        b.set(px, py);

        //calculate hit entity

        cdist = 0f;
        result = null;
        float range = 1f;

        Units.nearbyEnemies(b.team, px - range, py - range, range*2f, range*2f, e -> {
            if(e.dead() || !e.checkTarget(collidesAir, collidesGround) || !e.hittable()) return;

            e.hitbox(Tmp.r1);
            if(!Tmp.r1.contains(px, py)) return;

            float dst = e.dst(px, py) - e.hitSize;
            if((result == null || dst < cdist)){
                result = e;
                cdist = dst;
            }
        });

        if(result != null){
            b.collision(result, px, py);
        }else if(collidesTiles){
            Building build = Vars.world.buildWorld(px, py);
            if(build != null && build.team != b.team){
                build.collision(b);
            }
        }

        b.remove();

        b.vel.setZero();
    }

    @Override
    public void update(Bullet b){
        life += Time.delta;

        if(life >= lifetime){
            this.removed(b);
        }
    }


    @Override
    public void draw(Bullet b) { //TODO make multi target version
        Draw.color(Color.white);
        Draw.alpha(1f-life/lifetime);

        Lines.stroke(3f);
        Lines.line(previous.x, previous.y, px, py, true);
        //Lines.spikes();

        float rad = 3f;

        Draw.rect("restored-mind-circle", px, py, rad, rad);
        if(previous.epsilonEquals(b.x,b.y,0.001f)){
            Draw.rect("restored-mind-circle", b.x, b.y, rad, rad);
        }

        Draw.reset();

        /*if(points.size == 0) return;

        float range = 1f;

        Vec2 previous = vector.set(x, y);

        for(Vec2 enemy : points){


            float x1 = previous.x + Mathf.range(range),
                    y1 = previous.y + Mathf.range(range),
                    x2 = target.x + Mathf.range(range),
                    y2 = enemy.y + Mathf.range(range);

            Draw.color(Color.white);
            Draw.alpha(1f-life/lifetime);

            Lines.stroke(3f - life/lifetime*2f);
            Lines.line(x1, y1, x2, y2);

            float rad = 7f - life/lifetime*5f;

            Draw.rect("circle", x2, y2, rad, rad);

            if(previous.epsilonEquals(x, y, 0.001f)){
                Draw.rect("circle", x, y, rad, rad);
            }

            //Draw.color(Color.WHITE);

            //Draw.stroke(2f - life/lifetime*2f);
            //Draw.line(x1, y1, x2, y2);

            Draw.reset();

            previous = enemy;
        }*/
    }
}
