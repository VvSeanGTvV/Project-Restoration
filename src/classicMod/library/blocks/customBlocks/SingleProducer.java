package classicMod.library.blocks.customBlocks;

import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.*;
import mindustry.world.consumers.ConsumeItemDynamic;

import static mindustry.Vars.tilesize;

public class SingleProducer extends BlockProducer {
    public Block produce = null;

    public SingleProducer(String name) {

        super(name);
        consumeBuilder.add(new ConsumeItemDynamic((SingleProducer.SingleProducerBuild entity) -> entity.tehRecipe != null ? entity.tehRecipe.requirements : ItemStack.empty));
    }

    public class SingleProducerBuild extends BlockProducerBuild {
        public Block tehRecipe;

        @Override
        public Block recipe() {
            return tehRecipe;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return items.get(item) < getMaximumAccepted(item);
        }

        @Override
        public int getMaximumAccepted(Item item) {
            if (tehRecipe == null) return 0;
            for (ItemStack stack : tehRecipe.requirements) {
                if (stack.item == item) return stack.amount * 2;
            }
            return 0;
        }

        @Override
        public boolean shouldConsume() {
            return super.shouldConsume() && tehRecipe != null;
        }

        @Override
        public void updateTile() {
            if (tehRecipe == null && produce != null) tehRecipe = produce;
            //super.updateTile();
            var recipe = tehRecipe;
            boolean produce = recipe != null && efficiency > 0 && payload == null;

            if (produce) {
                progress += buildSpeed * edelta();

                if (progress >= recipe.buildTime) {
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

            if (payload != null) {
                payload.update(null, this);
            }
        }
    }
}
