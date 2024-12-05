package classicMod.library.blocks.neoplasiaBlocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.entities.bullet.*;
import mindustry.gen.Building;
import mindustry.graphics.Pal;

public class CausticTurret extends NeoplasiaBlock {

    public BulletType bulletType;
    public float range = 60f;
    public CausticTurret(String name) {
        super(name);
    }

    public class CausticTurretBuilding extends NeoplasiaBuilding {

        boolean shoot = false;

        @Override
        public void draw() {
            drawBeat(1f, 1f, 0.25f);
            Draw.rect(Core.atlas.find(name), x, y);
            Draw.color();

            Draw.reset();
        }

        @Override
        public void updateBeat() {
            shoot = (Units.closestEnemy(team, x, y, range, u -> u.type.killable && u.type.hittable) != null) || (Units.findEnemyTile(team, x, y, range, Building::isValid) != null);
            if (shoot) {
                int bulletCount = 7;
                for (int i = 0; i < bulletCount; i++) {
                    float angle = 180f / bulletCount;
                    bulletType.create(this, x, y, i * angle);
                }
            }
        }
    }
}
