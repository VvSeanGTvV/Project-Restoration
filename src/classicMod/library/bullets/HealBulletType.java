package classicMod.library.bullets;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.world.blocks.ConstructBlock;

public class HealBulletType extends BulletType {
    protected float healPercent = 3f;
    protected float height = 7f, width = 2f;
    protected Color backColor = Pal.heal, frontColor = Color.white;

    public HealBulletType(float speed, float damage){
        super(speed, damage);

        shootEffect = Fx.shootHeal;
        smokeEffect = Fx.hitLaser;
        hitEffect = Fx.hitLaser;
        despawnEffect = Fx.hitLaser;
        collidesTeam = true;
        hittable = false;
        reflectable = false;
    }

    public HealBulletType(){
        this(1f, 1f);
    }

    /*@Override
    public boolean collides(Bullet b, Building tile){
        return tile.team != b.team || tile.healthf() < 1f;
    }*/

    @Override
    public boolean testCollision(Bullet b, Building tile) {
        return tile.team != b.team || tile.healthf() < 1f;
    }

    @Override
    public void draw(Bullet b){
        Draw.color(backColor);
        Lines.stroke(width);
        Lines.lineAngleCenter(b.x, b.y, b.rotation(), height);
        Draw.color(frontColor);
        Lines.lineAngleCenter(b.x, b.y, b.rotation(), height / 2f);
        Draw.reset();
    }

    @Override
    public void hitTile(Bullet b, Building tile, float x, float y, float initialHealth, boolean direct) {
        super.hit(b);

        if(tile.team == b.team && !(tile.block instanceof ConstructBlock)){
            Fx.healBlockFull.at(tile.x, tile.y, tile.block.size, Pal.heal);
            tile.heal(healPercent / 100f * tile.maxHealth());
        }
    }
}
