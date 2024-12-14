package classicMod.library.blocks.neoplasiaBlocks;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.type.UnitType;
import mindustry.world.consumers.ConsumeItems;

public class CausticSpawner extends NeoplasmBlock {

    public TextureRegion topRegion, ballRegion, bottomRegion;
    public float spawnTime = 60f;

    public UnitType spawn;

    public CausticSpawner(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(name + "-top");
        ballRegion = Core.atlas.find(name + "-ball");
        bottomRegion = Core.atlas.find(name + "-bottom");
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{Core.atlas.find(name + "-bottom"), Core.atlas.find(name), Core.atlas.find(name + "-top")};
    }

    public class CausticSpawnerBuild extends NeoplasmBuilding {
        public float progress, speedScl;

        public float fraction(){
            return progress / spawnTime;
        }

        @Override
        public void draw() {
            float frac = fraction();
            drawBeat(1, 1, 0.25f);
            Draw.rect(bottomRegion, x, y);
            Draw.rect(region, x, y);

            drawBeat(Math.min(frac, 1f), Math.min(frac, 1f), 0.25f);
            Draw.rect(ballRegion, x, y);

            drawBeat(1, 1, 0.25f);
            Draw.rect(topRegion, x, y);
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
                    var unit = spawn.create(team);
                    unit.set(this);
                    unit.rotation(90f);
                    unit.add();
                }
                
            }
        }
    }
}
