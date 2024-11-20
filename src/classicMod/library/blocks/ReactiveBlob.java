package classicMod.library.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.Log;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.world.*;
import mindustry.world.blocks.environment.Prop;

public class ReactiveBlob extends Block {
    public float layer = 32.0F;

    public float brightness = 0.8f;
    public float radius = 65f;

    public ReactiveBlob(String name) {
        super(name);
        breakable = true;
        alwaysReplace = true;
        instantDeconstruct = true;
        breakEffect = Fx.breakProp;
        breakSound = Sounds.rockBreak;
        update = true;
    }

    @Override
    public void init(){
        lightRadius = radius*2.5f;
        clipSize = Math.max(clipSize, lightRadius * 3f);
        emitLight = true;

        //super.init();
    }

    public class ReactiveBlobBuild extends Building {
        float progress = 0f;

        float detection(float range) {
            float midX = x - (range / 2f), midY = y - (range / 2f);
            return (Units.anyEntities(midX, midY, range, range, u -> !u.dead) ? 1f : 0f);
        }
        @Override
        public void update() {
            super.update();
            progress = Mathf.lerpDelta(progress, detection(lightRadius), 0.05f);
            Drawf.light(x, y, lightRadius * progress, Color.cyan, brightness * progress);
        }

        @Override
        public void draw() {
            Draw.z(layer);
            Draw.rect(Core.atlas.find(name), x, y);
            Draw.alpha(progress);
            Draw.rect(Core.atlas.find(name + "-glow"), x, y);
            Draw.alpha(0f);
            Draw.reset();
        }
    }
}
