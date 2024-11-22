package classicMod.library.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.Prop;
import mindustry.world.consumers.Consume;
import mindustry.world.meta.*;

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
        hideDetails = true;

        //super.init();
    }

    @Override
    public void setBars() {
        return;
    }

    @Override
    public boolean synthetic() {
        return false;
    }

    public class ReactiveBlobBuild extends Building {
        float progress = 0f;
        int variant = 1;

        float detection(float range) {
            float midX = x - (range / 2f), midY = y - (range / 2f);
            return (Units.anyEntities(midX, midY, range, range, u -> !u.dead) ? 1f : 0f);
        }
        @Override
        public void update() {
            progress = Mathf.lerpDelta(progress, detection(lightRadius), 0.05f);
        }

        @Override
        public void draw() {
            if (variants > 0) variant = Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1));
            Drawf.light(x, y, lightRadius * progress, Color.cyan, brightness * progress);

            TextureRegion origin = Core.atlas.find(name);
            if (variants > 0) origin = variantRegions[variant];
            Draw.z(layer);
            Draw.rect(origin, x, y);
            Draw.alpha(progress);
            Draw.rect(Core.atlas.find(origin.asAtlas().name + "-glow"), x, y);
            Draw.alpha(0f);
            Draw.reset();
        }
    }
}
