package classicMod.library.blocks.customBlocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.*;
import arc.util.io.*;
import classicMod.content.*;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.*;

public class GenericSmelter extends GenericCrafter {
    private final ItemStack defaultFuelItem = new ItemStack(Items.coal, 1);

    /**
     * How long does a fuel last per item.
     **/
    public float burnTime = 60f;
    /**
     * Flame Color upon a fuel burn.
     **/
    public Color flameColor = Color.valueOf("ffb879");

    /**
     * Burning Effect upon on fuel depletion or consumed.
     **/
    public Effect burnEffect = RFx.fuelburn;

    private @Nullable ItemStack[] fuelItems;

    public GenericSmelter(String name) {
        super(name);
        if (craftEffect == Fx.none) craftEffect = RFx.smelt;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("fuel-left", (GenericSmelter.GenericSmelterBuild e) -> new Bar(Core.bundle.format("bar.fuel-left"), Pal.ammo, e::progress));
    }

    public void consumeFuels(ItemStack[] itemStacks){
        if(itemStacks != null) fuelItems = itemStacks;
    }

    @Override
    public void setStats() {
        stats.timePeriod = craftTime;
        super.setStats();
        if (fuelItems != null) {
            stats.add(ExtendedStat.fuel, StatValues.items(burnTime, fuelItems));
        }
        stats.remove(Stat.productionTime);

        if((hasItems && itemCapacity > 0) || outputItems != null){
            stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);
        }
    }

    @Override
    public void init() {
        if (fuelItems == null && defaultFuelItem != null) {
            fuelItems = new ItemStack[]{defaultFuelItem};
        }

        if (outputItems != null) hasItems = true;
        if (outputLiquids != null) hasLiquids = true;

        super.init();
    }

    public class GenericSmelterBuild extends GenericCrafterBuild {
        public float fuelProgress;
        public float activeScl;
        protected boolean accepted;

        public float progress() {
            if (efficiency <= 0) return 0;
            return 1f - fuelProgress;
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
         * @param item Gets the item and consume that specific fuel, including with item amount.
         **/
        public void consumeFuel(ItemStack[] item) {
            for (ItemStack itemStack : item) {
                Item it1 = itemStack.item;
                this.items.remove(it1, itemStack.amount);
            }
        }

        public boolean hasFuel(ItemStack[] fuelItemStack){
            return this.items.has(fuelItemStack);
        }

        @Override
        public void updateTile() {
            if (efficiency > 0 && hasFuel(fuelItems)) {
                //if (this.items.has(fuelItems)) {
                    activeScl = Mathf.lerpDelta(activeScl, warmupTarget(), warmupSpeed);
                    fuelProgress += getProgressIncrease(burnTime);
                    if (fuelProgress >= 1f) {
                        consumeFuel(fuelItems);
                        fuelProgress %= 1f;
                        burnEffect.at(this.x + Mathf.range(2f), this.y + Mathf.range(2f));
                    }
                //}


                progress += getProgressIncrease(craftTime);
                warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed);

                //continuously output based on efficiency
                if (outputLiquids != null) {
                    float inc = getProgressIncrease(1f);
                    for (var output : outputLiquids) {
                        handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
                    }
                }

                if (wasVisible && Mathf.chanceDelta(updateEffectChance)) {
                    updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4));
                }
            } else {
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
                activeScl = Mathf.lerpDelta(activeScl, 0f, warmupSpeed);
                if (fuelProgress != 0 && fuelProgress < 1) {
                    fuelProgress %= 1f;
                }
            }

            //TODO may look bad, revert to edelta() if so
            totalProgress += warmup * Time.delta;

            if (progress >= 1f) {
                craft();
            }

            dumpOutputs();
        }


        @Override
        public void craft() {
            consume();

            if (outputItems != null) {
                for (var output : outputItems) {
                    for (int i = 0; i < output.amount; i++) {
                        offload(output.item);
                    }
                }
            }

            if (wasVisible) {
                craftEffect.at(x, y, flameColor);
            }
            progress %= 1f;
        }

        @Override
        public void draw() {
            super.draw();
            Draw.z(Layer.block);
            Draw.rect(region, tile.drawx(), tile.drawy());

            if (activeScl > 0) {
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
        public void write(Writes write) {
            super.write(write);

            write.f(progress);
            write.f(fuelProgress);
            write.f(warmup);

            if (legacyReadWarmup) write.f(0f);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            progress = read.f();
            fuelProgress = read.f();
            warmup = read.f();

            if (legacyReadWarmup) read.f();
        }
    }
}
