package classicMod.library.bullets;


import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import classicMod.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;


public class TeslaOrbType extends PointLaserBulletType { //MIXED VERSION betweem PointBullet and LaserBullet
    //private Array<Vector2> points = new Array<>();
    //private ObjectSet<Enemy> hit = new ObjectSet<>();

    public TeslaOrbType(float range, int damage){
        this.damage = damage;
        this.range = range;
        hitEffect = ExtendedFx.laserhit;
        drawSize = 200f;
        this.lifetime = 30f;
    }

    @Override
    public void draw(Bullet b) { //TODO make multi target version

        Draw.color(Color.white);
        Drawf.laser(Core.atlas.white(), Core.atlas.find("restored-mind-circle"), Core.atlas.find("restored-mind-circle"), b.x, b.y, b.aimX, b.aimY, (3f - Time.time/lifetime*2f)*4);

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
