package classicMod.library.blocks.customBlocks;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.distribution.Duct;

import static mindustry.Vars.itemSize;
import static mindustry.Vars.tilesize;

public class DuctOvercharge extends Duct {

    public TextureRegion[] glowRegions;
    public float glowAlpha = 1f;
    public Color glowColor = Pal.redLight;

    public float baseEfficiency = 0f;

    public DuctOvercharge(String name) {
        super(name);
    }

    @Override
    public void init() {
        for(int i=0; i<5; i++){
            glowRegions = new TextureRegion[]{Core.atlas.find(name + "-glow-" + i)};
        }
        super.init();
    }

    public class DuctOverchargeBuild extends DuctBuild {
        @Override
        public void draw() {
            float rotation = rotdeg();
            int r = this.rotation;

            //draw extra ducts facing this one for tiling purposes
            for(int i = 0; i < 4; i++){
                if((blending & (1 << i)) != 0){
                    int dir = r - i;
                    float rot = i == 0 ? rotation : (dir)*90;
                    drawAtWithGlow(x + Geometry.d4x(dir) * tilesize*0.75f, y + Geometry.d4y(dir) * tilesize*0.75f, 0, rot, i != 0 ? SliceMode.bottom : SliceMode.top);
                }
            }

            //draw item
            if(current != null){
                Draw.z(Layer.blockUnder + 0.1f);
                Tmp.v1.set(Geometry.d4x(recDir) * tilesize / 2f, Geometry.d4y(recDir) * tilesize / 2f)
                        .lerp(Geometry.d4x(r) * tilesize / 2f, Geometry.d4y(r) * tilesize / 2f,
                                Mathf.clamp((progress + 1f) / 2f));

                Draw.rect(current.fullIcon, x + Tmp.v1.x, y + Tmp.v1.y, itemSize, itemSize);
            }

            Draw.scl(xscl, yscl);
            drawAt(x, y, blendbits, rotation, SliceMode.none);
            Draw.reset();
        }

        protected void drawAtWithGlow(float x, float y, int bits, float rotation, SliceMode slice){
            Draw.z(Layer.blockUnder);
            Draw.rect(sliced(botRegions[bits], slice), x, y, rotation);

            Draw.z(Layer.blockUnder + 0.2f);
            Draw.color(transparentColor);
            Draw.rect(sliced(botRegions[bits], slice), x, y, rotation);
            Draw.color();
            Draw.rect(sliced(topRegions[bits], slice), x, y, rotation);

            if(sliced(glowRegions[bits], slice).found() && power != null && power.status > 0f){
                Draw.z(Layer.blockAdditive);
                Draw.color(glowColor, glowAlpha * power.status);
                Draw.blend(Blending.additive);
                Draw.rect(sliced(glowRegions[bits], slice), x, y, rotation * 90);
                Draw.blend();
                Draw.color();
                Draw.z(Layer.block - 0.1f);
            }
        }

        @Override
        public void updateTile() {
            float eff = enabled ? (efficiency + baseEfficiency) : 1f;
            progress += edelta() / speed * 2f * eff;
            super.updateTile();
        }
    }
}
