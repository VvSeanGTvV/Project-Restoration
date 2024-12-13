package classicMod.library.blocks.neoplasiaBlocks;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.Log;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.type.UnitType;
import mindustry.world.consumers.ConsumeItems;

public class CausticSpawner extends NeoplasmBlock {

    public TextureRegion topRegion, ballRegion, bottomRegion;

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

    public class CausticSpawnerBuilding extends NeoplasmBuilding {
        public float progress, speedScl;

        @Override
        public void draw() {
            drawBeat(1, 1, 0.25f);
            Draw.rect(bottomRegion, x, y);
            Draw.rect(region, x, y);

            drawBeat(Math.min(progress, 1f), Math.min(progress, 1f), 0.25f);
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
            if (progress >= 120f){

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
