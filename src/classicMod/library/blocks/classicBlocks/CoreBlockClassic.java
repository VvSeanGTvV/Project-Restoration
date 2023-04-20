package classicMod.library.blocks.classicBlocks;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.storage.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class CoreBlockClassic extends CoreBlock {
    /** Allows the Core to have infinity capacity and will not fill up any moment **/
    public boolean infinityCapacity;
    public CoreBlockClassic(String name) {
        super(name);
    }

    @Override
    public void init() {
        super.init();
        if(infinityCapacity) itemCapacity = Integer.MAX_VALUE;
    }

    @Override
    public void setBars() {

        addBar("capacity", (CoreBuild e) -> new Bar(
                () -> Core.bundle.format("bar.capacity", UI.formatAmount(e.storageCapacity)),
                () -> Pal.items,
                () -> e.items.total() / ((float)e.storageCapacity * content.items().count(UnlockableContent::unlockedNow))
        ));
        if(infinityCapacity) removeBar("capacity");
    }

    @Override
    public void drawLanding(CoreBuild build, float x, float y){
        float fout = renderer.getLandTime() / coreLandDuration;

        if(renderer.isLaunching()) fout = 1f - fout;

        float fin = 1f - fout;

        float scl = Scl.scl(4f) / renderer.getDisplayScale();
        float shake = 0f;
        float s = region.width * region.scl() * scl * 3.6f * Interp.pow2Out.apply(fout);
        float rotation = Interp.pow2In.apply(fout) * 135f;
        x += Mathf.range(shake);
        y += Mathf.range(shake);
        float thrustOpen = 0.25f;
        float thrusterFrame = fin >= thrustOpen ? 1f : fin / thrustOpen;
        float thrusterSize = Mathf.sample(thrusterSizes, fin);

        //when launching, thrusters stay out the entire time.
        if(renderer.isLaunching()){
            Interp i = Interp.pow2Out;
            thrusterFrame = i.apply(Mathf.clamp(fout*13f));
            thrusterSize = i.apply(Mathf.clamp(fout*9f));
        }

        Draw.color(Pal.lightTrail);
        //TODO spikier heat
        Draw.rect("circle-shadow", x, y, s, s);

        Draw.scl(scl);

        //draw thruster flame
        float strength = (1f + (size - 3)/2.5f) * scl * thrusterSize * (0.95f + Mathf.absin(2f, 0.1f));
        float offset = (size - 3) * 3f * scl;

        for(int i = 0; i < 4; i++){
            Tmp.v1.trns(i * 90 + rotation, 1f);

            Tmp.v1.setLength((size * tilesize/2f + 1f)*scl + strength*2f + offset);
            Draw.color(build.team.color);
            Fill.circle(Tmp.v1.x + x, Tmp.v1.y + y, 6f * strength);

            Tmp.v1.setLength((size * tilesize/2f + 1f)*scl + strength*0.5f + offset);
            Draw.color(Color.white);
            Fill.circle(Tmp.v1.x + x, Tmp.v1.y + y, 3.5f * strength);
        }


        Drawf.spinSprite(region, x, y, rotation);

        Draw.alpha(Interp.pow4In.apply(thrusterFrame));
        Draw.alpha(1f);

        if(teamRegions[build.team.id] == teamRegion) Draw.color(build.team.color);

        Drawf.spinSprite(teamRegions[build.team.id], x, y, rotation);

        Draw.color();
        Draw.scl();
        Draw.reset();
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.remove(Stat.buildTime);
        if(infinityCapacity) stats.remove(Stat.itemCapacity);
    }

    public class CoreBuildClassic extends CoreBuild {
        @Override
        public void draw(){

            if(thrusterTime > 0){

                Draw.rect(block.region, x, y);

                drawTeamTop();
            }else{
                super.draw();
            }
        }

        @Override
        public void updateLandParticles(){
            
        }
    }
}
