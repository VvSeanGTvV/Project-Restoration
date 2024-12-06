package classicMod.library.blocks.neoplasiaBlocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.*;
import arc.util.Log;
import classicMod.content.ClassicBlocks;
import mindustry.content.*;
import mindustry.entities.Units;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.world.Tile;

public class CausticTurret extends NeoplasiaBlock {

    public BulletType bulletType;
    public float range = 60f;
    public CausticTurret(String name) {
        super(name);
    }

    public class CausticTurretBuilding extends NeoplasiaBuilding {

        boolean shoot = false;

        Healthc target;

        @Override
        public void draw() {
            drawBeat(1f, 1f, 0.25f);
            Draw.rect(Core.atlas.find(name), x, y);
            Draw.color();

            Draw.reset();
        }

        @Override
        public void updateBeat() {
            shoot = target != null;
            if (shoot) {
                int bulletCount = 7;
                float targetAngle = angleTo(target);
                bulletType.create(this, x, y, targetAngle);
                for (int i = 0; i < bulletCount; i++){
                    int invert = -Mathf.round((float) bulletCount / 2);
                    bulletType.create(this, x, y, ((invert + i) * 15f) + targetAngle);
                }
            }
        }

        @Override
        public boolean deathImminent() {
            return super.deathImminent() || target == null;
        }

        @Override
        public void Death() {
            for (int i = 0; i < 4; i++) {
                int rot = Mathf.mod((rotation + i), 4);
                Tile tile = nearbyTile(rot, -1);
                this.tile.setBlock(ClassicBlocks.cord, team);
                if (tile != null) {
                    tile.setBlock(ClassicBlocks.cord, team, rot);
                }
            }
        }

        @Override
        public void update() {
            target = (Units.closestEnemy(team, x, y, range, u -> u.type.killable && u.type.hittable) != null) ? Units.closestEnemy(team, x, y, range, u -> u.type.killable && u.type.hittable) : Units.findEnemyTile(team, x, y, range, Building::isValid);

            super.update();
        }
    }
}
