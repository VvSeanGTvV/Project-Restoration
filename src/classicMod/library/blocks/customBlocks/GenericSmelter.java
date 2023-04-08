package classicMod.library.blocks.customBlocks;

import arc.math.*;
import arc.util.*;
import classicMod.content.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.*;

public class GenericSmelter extends GenericCrafter {
    /** Fuel to power up the smelter. **/
    public ItemStack fuelItem = new ItemStack(Items.coal, 1);
    /** How long does the fuel last. **/
    public float burnTime = 50f;
    public @Nullable ItemStack[] fuelItems;
    public @Nullable ItemStack[] TemproraryItems;
    public @Nullable ItemStack[] arrayListItems;

    public GenericSmelter(String name) {
        super(name);
    }

    @Override
    public void setStats(){
        stats.timePeriod = craftTime;
        super.setStats();

        stats.remove(Stat.output); //prevent duplication
        stats.remove(Stat.productionTime); //prevent duplication
        if((hasItems && itemCapacity > 0) || outputItems != null){
            stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);
        }

        if(fuelItem != null){
            stats.add(ExtendedStat.fuel, StatValues.items(burnTime, fuelItems));
        }

        if(outputItems != null){
            stats.add(Stat.output, StatValues.items(craftTime, outputItems));
        }

        if(outputLiquids != null){
            stats.add(Stat.output, StatValues.liquids(1f, outputLiquids));
        }
    }

    @Override
    public void init(){
        if(outputItems == null && outputItem != null){
            outputItems = new ItemStack[]{outputItem};
        }
        if(fuelItems == null && fuelItem != null){
            fuelItems = new ItemStack[]{fuelItem};
        }
        if(outputLiquids == null && outputLiquid != null){
            outputLiquids = new LiquidStack[]{outputLiquid};
        }
        //write back to outputLiquid, as it helps with sensing
        if(outputLiquid == null && outputLiquids != null && outputLiquids.length > 0){
            outputLiquid = outputLiquids[0];
        }
        outputsLiquid = outputLiquids != null;

        if(outputItems != null) hasItems = true;
        if(outputLiquids != null) hasLiquids = true;

        super.init();
    }

    public class GenericSmelterBuild extends GenericCrafterBuild {
        public float fuelProgress;
        public boolean hasFuel;

        protected boolean accepted;

        @Override
        public boolean acceptItem(Building source, Item item) {
            accepted = false;
            for (ItemStack itemStack : fuelItems) {
                accepted = itemStack.item.equals(item) && this.items.get(itemStack.item) < this.getMaximumAccepted(itemStack.item);
            }
            return accepted || this.block.consumesItem(item) && this.items.get(item) < this.getMaximumAccepted(item);
            //return  this.block.consumesItem(item) && this.items.get(item) < this.getMaximumAccepted(item) || !this.items.has(fuelItems);
        }

        public void consumeFuel(ItemStack[] item, int amount) {
            for (ItemStack itemStack : item) {
                Item it1 = itemStack.item;
                this.items.remove(it1, amount);
            }
        }

        @Override
        public void updateTile(){
            hasFuel = this.items.has(fuelItems);
            if(this.items.has(fuelItems)){
                fuelProgress += getProgressIncrease(burnTime);
                if(fuelProgress >= 1f){
                    consumeFuel(fuelItems, 1);
                    fuelProgress %= 1f;
                }
            }
            if(efficiency > 0 && hasFuel){

                progress += getProgressIncrease(craftTime);
                warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed);

                //continuously output based on efficiency
                if(outputLiquids != null){
                    float inc = getProgressIncrease(1f);
                    for(var output : outputLiquids){
                        handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
                    }
                }

                if(wasVisible && Mathf.chanceDelta(updateEffectChance)){
                    updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4));
                }
            }else{
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }

            //TODO may look bad, revert to edelta() if so
            totalProgress += warmup * Time.delta;

            if(progress >= 1f){
                craft();
            }

            dumpOutputs();
        }


        @Override
        public void craft(){
            consume();

            if(outputItems != null){
                for(var output : outputItems){
                    for(int i = 0; i < output.amount; i++){
                        offload(output.item);
                    }
                }
            }

            if(wasVisible){
                craftEffect.at(x, y);
            }
            progress %= 1f;
        }
    }
}
