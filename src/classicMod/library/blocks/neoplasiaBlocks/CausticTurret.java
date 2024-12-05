package classicMod.library.blocks.neoplasiaBlocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.entities.bullet.*;
import mindustry.graphics.Pal;

public class CausticTurret extends NeoplasiaBlock {
    public CausticTurret(String name) {
        super(name);
    }

    public class CausticTurretBuilding extends NeoplasiaBuilding {

        @Override
        public void draw() {
            drawBeat(1f, 1f, 0.25f);
            Draw.rect(Core.atlas.find(name), x, y);
            Draw.color();

            Draw.reset();
        }

        @Override
        public void updateBeat() {
            BulletType bulletType = new BasicBulletType(5.0F, 16.0F) {
                {
                    this.homingPower = 0.19F;
                    this.homingDelay = 4.0F;
                    this.width = 7.0F;
                    this.height = 12.0F;
                    this.lifetime = 30.0F;
                    this.shootEffect = Fx.sparkShoot;
                    this.smokeEffect = Fx.shootBigSmoke;
                    this.hitColor = this.backColor = this.trailColor = Pal.suppress;
                    this.frontColor = Color.white;
                    this.trailWidth = 1.5F;
                    this.trailLength = 5;
                    this.hitEffect = this.despawnEffect = Fx.hitBulletColor;
                }
            };
            for (int i = 0; i < 8; i++){
                bulletType.create(this, x, y, i * -Mathf.mod(i, 1));
            }
        }
    }
}
