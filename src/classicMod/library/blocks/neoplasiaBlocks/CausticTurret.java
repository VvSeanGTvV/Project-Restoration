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
        BulletType bulletType = new BasicBulletType(5.0F, 16.0F, "shell") {
                {
                    homingPower = 0.19F;
                    homingDelay = 4.0F;
                    width = 7.0F;
                    height = 12.0F;
                    lifetime = 30.0F;
                    shootEffect = Fx.sparkShoot;
                    smokeEffect = Fx.shootBigSmoke;
                    hitColor = backColor = trailColor = Pal.suppress;
                    frontColor = Color.white;
                    trailWidth = 1.5F;
                    trailLength = 5;
                    hitEffect = despawnEffect = Fx.hitBulletColor;
                }
            };

        @Override
        public void draw() {
            drawBeat(1f, 1f, 0.25f);
            Draw.rect(Core.atlas.find(name), x, y);
            Draw.color();

            Draw.reset();
        }

        @Override
        public void updateBeat() {
            for (int i = 0; i < 8; i++){
                bulletType.create(this, x, y, i * -Mathf.mod(i, 1));
            }
        }
    }
}
