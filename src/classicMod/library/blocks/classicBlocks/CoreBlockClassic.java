package classicMod.library.blocks.classicBlocks;

import arc.*;
import arc.graphics.g2d.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.storage.*;

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
    }
}
