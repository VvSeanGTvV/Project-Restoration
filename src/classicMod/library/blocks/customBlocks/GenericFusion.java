package classicMod.library.blocks.customBlocks;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.Log;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;

public class GenericFusion extends GenericSmelter {

    TextureRegion topB;
    public GenericFusion(String name) {
        super(name);
    }

    public void load() {
        topB = Core.atlas.find(name + "-topB");
        super.load();
    }

    public class GenericFusionBuild extends GenericSmelterBuild {
        @Override
        public void draw() {
            drawer.draw(this);
            Drawf.spinSprite(topB, x, y, totalProgress * 1.2f);
        }
    }

}
