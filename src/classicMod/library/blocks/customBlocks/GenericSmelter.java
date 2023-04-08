package classicMod.library.blocks.customBlocks;

import arc.util.*;
import classicMod.content.*;
import mindustry.content.*;
import mindustry.type.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.*;

public class GenericSmelter extends GenericCrafter {
    /** Fuel to power up the smelter. **/
    public ItemStack fuelItem = new ItemStack(Items.coal, 1);
    public @Nullable ItemStack[] fuelItems;

    public GenericSmelter(String name) {
        super(name);
    }

    @Override
    public void setStats(){
        stats.timePeriod = craftTime;
        super.setStats();
        if((hasItems && itemCapacity > 0) || outputItems != null){
            stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);
        }

        if(fuelItem != null){
            stats.add(ExtendedStat.fuel, StatValues.items(craftTime, fuelItems));
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

    public void consumeFuel() {
        ItemStack[] var1 = fuelItems;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            var1[var3].amount = var1[var3].amount - 1;
        }
    }
    public class GenericSmelterBuild extends GenericCrafterBuild {
        @Override
        public void craft(){
            consume();
            consumeFuel();

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
