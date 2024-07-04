package classicMod.library.blocks.customBlocks;

import arc.math.Mathf;
import arc.struct.ObjectMap;
import mindustry.gen.Building;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.BlockProducer;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.consumers.ConsumeItemDynamic;

import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;
import static mindustry.type.ItemStack.with;

public class SingleProducer extends BlockProducer {
    public Block produce = null;
    public SingleProducer(String name) {
        super(name);

        ObjectMap<Block, ItemStack[]> stacks = new ObjectMap<>();
        consume(new ConsumeItemDynamic((BlockProducerBuild e) -> {
            Block block = e.recipe();

            if(block != null){
                ItemStack[] clone = stacks.get(block, () -> ItemStack.copy(block.requirements));
                for(int i = 0; i < clone.length; i++){
                    clone[i].amount = Mathf.ceil(block.requirements[i].amount * state.rules.buildCostMultiplier);
                }
                return clone;
            }else{
                return ItemStack.empty;
            }
        }));
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
