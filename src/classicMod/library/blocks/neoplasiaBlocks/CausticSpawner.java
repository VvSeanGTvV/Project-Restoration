package classicMod.library.blocks.neoplasiaBlocks;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import classicMod.library.drawCustom.BlendingCustom;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;
import mindustry.world.consumers.ConsumeItems;

public class CausticSpawner extends NeoplasmBlock {

    public TextureRegion topRegion, ballRegion;
    public float sclOffset = 0f;
    public float spawnTime = 60f;
    /** Self destructs upon a new unit spawn.**/
    public boolean selfDestruct;
    public Effect spawnEffect;
    public Color spawnColor;

    public UnitType spawn;

    public CausticSpawner(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(name + "-top");
        ballRegion = Core.atlas.find(name + "-ball");
    }

    @Override
    protected TextureRegion[] icons() {
        if (Core.atlas.find(name + "-top").found()) return new TextureRegion[]{Core.atlas.find(name), Core.atlas.find(name + "-top")};
        return new TextureRegion[]{Core.atlas.find(name)};
    }

    public class CausticSpawnerBuild extends NeoplasmBuilding {
        public float progress, speedScl;

        public float fraction(){
            return progress / spawnTime;
        }

        @Override
        public void draw() {
            float frac = fraction();
            drawBeat(1 + sclOffset, 1 + sclOffset, 0.25f);
            Draw.rect(region, x, y);

            drawBeat(Math.min(frac, 1f + sclOffset), Math.min(frac, 1f + sclOffset), 0.25f);
            if (ballRegion.found()) Draw.rect(ballRegion, x, y);

            drawBeat(1 + sclOffset, 1 + sclOffset, 0.25f);
            if (topRegion.found()) Draw.rect(topRegion, x, y);
        }

        public void updateEfficiency(){
            boolean hasItems = false;
            for (var consume : consumeBuilder){
                if (consume instanceof ConsumeItems consumeItems){
                    for (var itemStack : consumeItems.items){
                        hasItems = items.has(itemStack.item);
                        if (hasItems) break;
                    }
                }
            }
            float fl = (hasItems) ? 1f : 0f;
            efficiency(fl);
            efficiency = fl;
        }

        @Override
        public void update() {
            super.update();
            updateEfficiency();
            if (efficiency > 0){
                progress += edelta() * speedScl;
                speedScl = Mathf.lerpDelta(speedScl, 1f, 0.05f);
            } else {
                speedScl = Mathf.lerpDelta(speedScl, 0f, 0.05f);
            }
        }

        @Override
        public void updateBeat() {
            super.updateBeat();
            if (progress >= spawnTime){
                consume();
                progress %= 1f;
                speedScl = 0f;
                if (Units.canCreate(team, spawn)) {
                    if (spawnEffect != null) spawnEffect.at(this.x, this.y, (spawnColor != null) ? spawnColor : Color.white);
                    var unit = spawn.create(team);
                    unit.set(this);
                    unit.rotation(90f);
                    unit.add();
                }
                if (selfDestruct){
                    this.damage(health);
                }
            }
        }
    }
}
