package classicMod.library.blocks.customBlocks;

import arc.math.Mathf;
import arc.struct.ObjectMap;
import classicMod.library.blocks.v6devBlocks.ResearchBlock;
import mindustry.gen.Building;
import mindustry.type.Item;
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
        consumeBuilder.add(new ConsumeItemDynamic((SingleProducer.SingleProducerBuild entity) -> entity.recipe() != null ? entity.recipe().requirements : ItemStack.empty));
    }

    public class SingleProducerBuild extends BlockProducerBuild {

        @Override
        public Block recipe() {
            return produce;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return items.get(item) < getMaximumAccepted(item);
        }

        @Override
        public int getMaximumAccepted(Item item){
            if(recipe() == null) return 0;
            for(ItemStack stack : recipe().requirements){
                if(stack.item == item) return stack.amount * 2;
            }
            return 0;
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
