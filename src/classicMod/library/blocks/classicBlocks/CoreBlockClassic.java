package classicMod.library.blocks.classicBlocks;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
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
    public void load(){
        thruster1 = Core.atlas.find("restored-mind-nullTexture");
        thruster2 = Core.atlas.find("restored-mind-nullTexture");
        teamRegion = Core.atlas.find("restored-mind-nullTexture");
        region = Core.atlas.find(name);
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{region};
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

        float scl = Scl.scl(4f) / renderer.getDisplayScale();
        float shake = 0f;
        float rotation = Interp.pow2In.apply(fout) * 135f;
        x += Mathf.range(shake);
        y += Mathf.range(shake);

        Draw.color(Pal.lightTrail);

        Draw.scl(scl);

        Drawf.spinSprite(region, x, y, rotation);


        //if(teamRegions[build.team.id] == teamRegion) Draw.color(build.team.color);

        //Drawf.spinSprite(teamRegions[build.team.id], x, y, rotation);

        Draw.color();
        Draw.scl();
        Draw.reset();
    }
    @Override
    protected void drawLandingThrusters(float x, float y, float rotation, float frame){

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

                //drawTeamTop();
            }else{
                Draw.rect(block.region, x, y);
                //super.draw();
            }
        }

        @Override
        public void drawThrusters(float frame){

        }

        @Override
        public void updateLandParticles(){

        }
    }
}
