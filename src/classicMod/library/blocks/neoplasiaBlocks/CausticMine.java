package classicMod.library.blocks.neoplasiaBlocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.entities.*;
import mindustry.gen.Unit;

public class CausticMine extends NeoplasmBlock {

    public Effect explosionEffect;
    public Color explosionColor;

    public float damage;

    public CausticMine(String name) {
        super(name);

        destructible = true;
        solid = false;
        targetable = false;
    }

    public class CausticMineBuild extends NeoplasmBuilding {
        Seq<Unit> inRange = new Seq<>();

        @Override
        public void unitOn(Unit unit) {
            if (unit.team != this.team) {
                triggered();

                while (Units.closestEnemy(team, x, y, 60f, u -> !inRange.contains(u)) != null) {
                    inRange.add(Units.closestEnemy(team, x, y, 60f, u -> !inRange.contains(u)));
                }

                for (var uni : inRange){
                    uni.damage(damage);
                }

                damage(health);
            }
        }

        @Override
        public void drawCracks(){
            //no
        }

        public void triggered() {
            if (explosionEffect != null) explosionEffect.at(this.x, this.y, (explosionColor != null) ? explosionColor : Color.white);
        }

        @Override
        public void draw() {
            drawBeat(1f, 1f, 0.25f);
            Draw.rect(Core.atlas.find(name), x, y);
            Draw.color();

            Draw.reset();
        }
    }
}
