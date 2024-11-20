package classicMod.library.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.Log;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Prop;

public class ReactiveBlob extends Prop {

    public float brightness = 0.9f;
    public float radius = 200f;

    public ReactiveBlob(String name) {
        super(name);
    }

    @Override
    public void init(){
        lightRadius = radius*2.5f;
        clipSize = Math.max(clipSize, lightRadius * 3f);
        emitLight = true;

        super.init();
    }

    float progress = 0f;
    float detection(float x, float y) {
        return (Units.anyEntities(x, y, 40f, 40f, u -> !u.dead) ? 1f : 0f);
    }
    @Override
    public void drawBase(Tile tile) {
        float x = tile.worldx(), y = tile.worldy();
        progress = Mathf.lerpDelta(progress, detection(x, y), 0.1f);
        Drawf.light(x, y, lightRadius * progress, Color.cyan, brightness * progress);

        Draw.z(layer);
        Draw.rect(Core.atlas.find(name), x, y);
        Draw.alpha(progress);
        Draw.rect(Core.atlas.find(name + "-glow"), x, y);
        Draw.alpha(0f);
        Draw.reset();
    }
}
