package classicMod.library.blocks;

import mindustry.gen.Building;
import mindustry.world.blocks.payloads.*;

public class BallisticSilo extends PayloadBlock {


    public BallisticSilo(String name){
        super(name);
    }

    public class BallisticSiloBuild extends PayloadBlockBuild<BuildPayload>{
        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return this.payload == null && payload instanceof BuildPayload b && b.build instanceof NuclearWarhead.NuclearWarheadBuild;
        }

        @Override
        public void updateTile(){
            moveInPayload();
        }

        @Override
        public void draw(){
            super.draw();
            drawPayload();
        }
    }
}
