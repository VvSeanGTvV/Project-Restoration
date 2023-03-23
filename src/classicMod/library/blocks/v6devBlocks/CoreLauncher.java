package classicMod.library.blocks.v6devBlocks;

import arc.Graphics.*;
import arc.Graphics.Cursor.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class CoreLauncher extends Block{
    public int range = 1;
    public Vec2 Pos = new Vec2(0,0);
    TextureRegion podRegion;

    public CoreLauncher(String name){
        super(name);

        hasItems = true;
        configurable = true;
        update = true;
        //will be enabled when needed (if at all)
        //buildPlaceability = BuildPlaceability.sectorCaptured;
    }

    public class CoreLauncherBuild extends Building{
        protected Block defaultCore = Vars.state.rules.sector.info.bestCoreType;

        @Override
        public void updateTile(){
            super.updateTile();
            Pos = new Vec2 (this.x, this.y);
            if(defaultCore != null){
                requirements(category, ItemStack.mult(defaultCore.requirements, 3));
            }
        }

        @Override
        public boolean configTapped(){

            if(state.isCampaign() && this.isValid()){
                Vars.ui.planet.show();
            }
            return false;
        }

        @Override
        public Cursor getCursor(){
            return this.isValid() ? SystemCursor.hand : SystemCursor.arrow;
        }

        public void launch(){
            LaunchCoreComp ent = (LaunchCoreComp) LaunchCoreComp.create();
            ent.set(Pos);
            ent.block(Blocks.coreShard);
            float launchDuration = 0;
            ent.lifetime(launchDuration);
            ent.add();

            //cons.;
        }
    }

    static abstract class LaunchCoreComp extends LaunchCore implements Drawc, Timedc {
        float x, y;

        transient Interval in = new Interval();
        Block block;

        @Override
        public void draw(){
            float alpha = fout(Interp.pow5Out);
            float scale = (1f - alpha) * 1.4f + 1f;
            float cx = cx(), cy = cy();
            float rotation = fin() * (140f + Mathf.randomSeedRange(id(), 50f));

            Draw.z(Layer.effect + 0.001f);

            Draw.color(Pal.engine);

            float rad = 0.2f + fslope();
            float rscl = (block.size - 1) * 0.85f;

            Fill.light(cx, cy, 10, 25f * (rad + scale-1f) * rscl, Tmp.c2.set(Pal.engine).a(alpha), Tmp.c1.set(Pal.engine).a(0f));

            Draw.alpha(alpha);
            for(int i = 0; i < 4; i++){
                Drawf.tri(cx, cy, 6f * rscl, 40f * (rad + scale-1f) * rscl, i * 90f + rotation);
            }

            Draw.color();

            Draw.z(Layer.weather - 1);

            TextureRegion region = block.fullIcon;
            float rw = region.width * Draw.scl * scale, rh = region.height * Draw.scl * scale;

            Draw.alpha(alpha);
            Draw.rect(region, cx, cy, rw, rh, rotation - 45);

            Tmp.v1.trns(225f, fin(Interp.pow3In) * 250f);

            Draw.z(Layer.flyingUnit + 1);
            Draw.color(0, 0, 0, 0.22f * alpha);
            Draw.rect(region, cx + Tmp.v1.x, cy + Tmp.v1.y, rw, rh, rotation - 45);

            Draw.reset();
        }

        /*@Override
        float cx(){
            return x + fin(Interp.pow2In) * (12f + Mathf.randomSeedRange(id() + 3, 4f));
        }

        @Override
        float cy(){
            return y + fin(Interp.pow5In) * (100f + Mathf.randomSeedRange(id() + 2, 30f));
        }*/

        @Override
        public void update(){
            float r = 4f;
            if(in.get(3f - fin()*2f)){
                Fx.rocketSmokeLarge.at(cx() + Mathf.range(r), cy() + Mathf.range(r), fin());
            }
        }
    }
}
