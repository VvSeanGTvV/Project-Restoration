package classicMod.library.blocks.customBlocks;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.Log;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;

public class GenericFusion extends GenericSmelter {

    TextureRegion rotor;
    public GenericFusion(String name) {
        super(name);
    }

    public void load() {
        rotor = Core.atlas.find(name + "-rotor");
        super.load();
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{region, Core.atlas.find(name + "-rotor-icon")};
    }

    public class GenericFusionBuild extends GenericSmelterBuild {
        @Override
        public void draw() {
            drawer.draw(this);
            Drawf.spinSprite(rotor, x, y, totalProgress * 1.2f);
            Drawf.spinSprite(rotor, x, y, (totalProgress * 1.2f) - 225f);
        }
    }

}
