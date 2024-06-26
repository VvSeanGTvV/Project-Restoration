package classicMod.library.blocks.customBlocks;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import classicMod.content.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.*;

public class GenericSmelter extends GenericCrafter {
    /** Fuel to power up the smelter. **/
    public ItemStack fuelItem = new ItemStack(Items.coal, 1);
    /** is Fuel an optional booster. **/
    public boolean fuelBooster = false;
    /** How long does the fuel last. **/
    public float burnTime = 60f;
    /** Color of the flame when using fuel **/
    public Color flameColor = Color.valueOf("ffb879");
    public @Nullable ItemStack[] fuelItems;
    /** The burning effect for every fuel is depleted and consumed another one **/
    public Effect burnEffect = ExtendedFx.fuelburn;

    public GenericSmelter(String name) {
        super(name);
        craftEffect = ExtendedFx.smelt;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("fuel-left", (GenericSmelter.GenericSmelterBuild e) -> new Bar(Core.bundle.format("bar.fuel-left"), Pal.ammo, e::progress));
    }

    @Override
    public void setStats(){
        stats.timePeriod = craftTime;

        /*if((hasItems && itemCapacity > 0) || outputItems != null){
            stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);
        }

        if(outputItems != null){
            stats.add(Stat.output, StatValues.items(craftTime, outputItems));
        }

        if(outputLiquids != null){
            stats.add(Stat.output, StatValues.liquids(1f, outputLiquids));
        }*/

        if(fuelItem != null){
            stats.add(ExtendedStat.fuel, StatValues.items(burnTime, fuelItems));
        }

        super.setStats();
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
        public float activeScl;
        public boolean hasFuel;
        protected boolean accepted;

        public float progress(){
            if(efficiency<=0) return 0;
            return 1f-fuelProgress;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            accepted = false;
            for (ItemStack itemStack : fuelItems) {
                accepted = itemStack.item.equals(item) && this.items.get(itemStack.item) < this.getMaximumAccepted(itemStack.item);
            }
            return accepted || this.block.consumesItem(item) && this.items.get(item) < this.getMaximumAccepted(item);
        }

        /**
         * Consumes fuel/item only and can be useful for GenericSmelter.
         * @param item Gets the item and consume that specific fuel.
         * @param amount Gets the required amount, and consume at the specific amount it has. [Deprecated soon]
         **/
        public void consumeFuel(ItemStack[] item, int amount) {
            for (ItemStack itemStack : item) {
                Item it1 = itemStack.item;
                this.items.remove(it1, amount);
            }
        }

        @Override
        public void updateTile(){
            hasFuel = this.items.has(fuelItems);
            if(efficiency > 0){
                if(this.items.has(fuelItems)) {
                    activeScl = Mathf.lerpDelta(activeScl, warmupTarget(), warmupSpeed);
                    fuelProgress += getProgressIncrease(burnTime);
                    if (fuelProgress >= 1f) {
                        consumeFuel(fuelItems, 1); //TODO make it consume multiple items with different numbers and not fixed num.
                        fuelProgress %= 1f;
                        burnEffect.at(this.x + Mathf.range(2f), this.y + Mathf.range(2f));
                    }
                } else {
                    activeScl = Mathf.lerpDelta(activeScl, 0f, warmupSpeed);
                    if(fuelProgress != 0 && fuelProgress < 1){
                        fuelProgress %= 1f;
                        consumeFuel(fuelItems, 1);
                    }
                }
            } else {
               activeScl = Mathf.lerpDelta(activeScl, 0f, warmupSpeed);
               if(fuelProgress != 0 && fuelProgress < 1){
                   fuelProgress %= 1f;
                   consumeFuel(fuelItems, 1);
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
                craftEffect.at(x, y, flameColor);
            }
            progress %= 1f;
        }

        @Override
        public void draw() {
            super.draw();
            Draw.z(Layer.block);
            Draw.rect(region, tile.drawx(), tile.drawy());

            if(activeScl > 0) {
                float g = 0.1f;

                Draw.alpha(((1f - g) + Mathf.absin(Time.time, 8f, g)) * activeScl);

                Draw.tint(flameColor);
                Fill.circle(tile.drawx(), tile.drawy(), 2f + Mathf.absin(Time.time, 5f, 0.8f));
                Draw.color(1f, 1f, 1f, activeScl);
                Fill.circle(tile.drawx(), tile.drawy(), 1f + Mathf.absin(Time.time, 5f, 0.7f));

                Draw.color();
            }
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.f(progress);
            write.f(fuelProgress);
            write.f(warmup);

            if(legacyReadWarmup) write.f(0f);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            progress = read.f();
            fuelProgress = read.f();
            warmup = read.f();

            if(legacyReadWarmup) read.f();
        }
    }
}
