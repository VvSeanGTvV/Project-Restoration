package classicMod.library.blocks.customBlocks;

import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.BlockProducer;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;

import static mindustry.Vars.tilesize;

public class SingleProducer extends BlockProducer {
    public Block produce = null;
    public SingleProducer(String name) {
        super(name);
    }

    public class SingleProducerBuild extends BlockProducerBuild {

        @Override
        public Block recipe() {
            return produce;
        }

        @Override
        public boolean shouldConsume(){
            return super.shouldConsume() && recipe() != null;
        }

        @Override
        public void updateTile(){
            super.updateTile();
            var recipe = recipe();
            boolean produce = recipe != null && efficiency > 0 && payload == null;

            if(produce){
                progress += buildSpeed * edelta();

                if(progress >= recipe.buildCost){
                    consume();
                    payload = new BuildPayload(recipe, team);
                    payload.block().placeEffect.at(x, y, payload.size() / tilesize);
                    payVector.setZero();
                    progress %= 1f;
                }
            }

            heat = Mathf.lerpDelta(heat, Mathf.num(produce), 0.15f);
            time += heat * delta();

            moveOutPayload();
        }
    }
}
