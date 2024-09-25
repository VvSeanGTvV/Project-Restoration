package classicMod.library.blocks.customBlocks;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.*;

import static mindustry.Vars.*;

public class ShieldWallColor extends ShieldWall {

    public Color shieldColor = Color.blue;
    public ShieldWallColor(String name){
        super(name);

        update = true;
    }

    public class ShieldWallColorBuild extends ShieldWallBuild {
        @Override
        public void draw(){
            Draw.rect(block.region, x, y);

            if(shieldRadius > 0){
                float radius = shieldRadius * tilesize * size / 2f;

                Draw.z(Layer.shields);

                Draw.color(shieldColor, Color.white, Mathf.clamp(hit));

                if(renderer.animateShields){
                    Fill.square(x, y, radius);
                }else{
                    Lines.stroke(1.5f);
                    Draw.alpha(0.09f + Mathf.clamp(0.08f * hit));
                    Fill.square(x, y, radius);
                    Draw.alpha(1f);
                    Lines.poly(x, y, 4, radius, 45f);
                    Draw.reset();
                }

                Draw.reset();

                Drawf.additive(glowRegion, glowColor, (1f - glowMag + Mathf.absin(glowScl, glowMag)) * shieldRadius, x, y, 0f, Layer.blockAdditive);
            }
        }
    }
}
