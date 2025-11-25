package classicMod.library.blocks.customBlocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.struct.ObjectFloatMap;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.*;
import classicMod.content.*;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.ConsumeItemEfficiency;
import mindustry.world.consumers.ConsumeItemFilter;
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

    /**
     * minimum flammability for it to be useable.
     **/
    private float minFlammability = 1f;

    private final Seq<ItemStack> fuelItems = new Seq<>();

    public GenericSmelter(String name) {
        super(name);
        if (craftEffect == Fx.none) craftEffect = RFx.smelt;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("fuel-left", (GenericSmelterBuild e) -> new Bar(Core.bundle.format("bar.fuel-left"), Pal.ammo, e::progress));
    }

    /**
     * What items can be consumable to be used as fuel.
     * @param minFlammability Minimum flammability for it to work.
     * @param amount How much it consume per fuel.
     **/
    public void consumeFuels(float minFlammability, int amount){
        this.minFlammability = minFlammability;
        for (Item item : Vars.content.items()) {
            if (item.flammability >= minFlammability)
                fuelItems.add(new ItemStack(item, amount));
        }
    }

    /**
     * What items can be consumable to be used as fuel. (defaults amount of 1/fuel)
     * @param minFlammability Minimum flammability for it to work.
     **/
    public void consumeFuels(float minFlammability){
        consumeFuels(minFlammability, 1);
    }

    @Override
    public void setStats() {
        stats.timePeriod = craftTime;
        super.setStats();
        if (fuelItems != null && fuelItems.size != 0) {
            stats.add(ExtendedStat.fuel, ExtendedStat.items(burnTime, fuelItems));
        }
        stats.remove(Stat.productionTime);

        if((hasItems && itemCapacity > 0) || outputItems != null){
            stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);
        }
    }

    @Override
    public void init() {

        if (fuelItems == null || fuelItems.size == 0) {
            consumeFuels(this.minFlammability);
        }

        if (outputItems != null) hasItems = true;
        if (outputLiquids != null) hasLiquids = true;

        super.init();
    }

    @Override
    public void afterPatch() {
        super.afterPatch();
    }

    public class GenericSmelterBuild extends GenericCrafterBuild {
        public float fuelProgress, activeScl, efficiencyMultiplier = 1f, itemDurationMultiplier = 1;
        protected boolean accepted;

        public float progress() {
            if (efficiency <= 0) return 0;
            return 1f - fuelProgress;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            accepted = item.flammability >= minFlammability && this.items.get(item) < this.getMaximumAccepted(item);
            return accepted || this.block.consumesItem(item) && this.items.get(item) < this.getMaximumAccepted(item);
        }

        /**
         * Consumes fuel/item only and can be useful for GenericSmelter.
         * @param itemStack Gets the item and consume that specific fuel, including with item amount.
         **/
        public void consumeFuel(ItemStack itemStack) {
            Item it1 = itemStack.item;
            this.items.remove(it1, itemStack.amount);
        }

        public boolean hasFuel(Seq<ItemStack> fuelItemStack){
            boolean yes = false;
            for (ItemStack itemStack : fuelItemStack)
            {
                yes = this.items.has(itemStack.item, itemStack.amount);
                if (yes) break;
            }
            return yes;
        }

        public ItemStack getFirst(Seq<ItemStack> fuelItemStack){
            ItemStack first = null;
            for (ItemStack itemStack : fuelItemStack)
            {
                boolean yes = this.items.has(itemStack.item, itemStack.amount);
                if (yes) {
                    first = itemStack;
                    break;
                }
            }
            return first;
        }

        @Override
        public void updateTile() {
            Log.info(hasFuel(fuelItems));
            if (efficiency > 0 && hasFuel(fuelItems)) {
                activeScl = Mathf.lerpDelta(activeScl, warmupTarget(), warmupSpeed);
                fuelProgress += getProgressIncrease(burnTime);
                efficiencyMultiplier = getFirst(fuelItems).item.flammability;
                if (fuelProgress >= 1f) {
                    consumeFuel(getFirst(fuelItems));
                    fuelProgress %= 1f;
                    burnEffect.at(this.x + Mathf.range(2f), this.y + Mathf.range(2f));
                }
                float productionEfficiency = efficiency * efficiencyMultiplier;



                progress += getProgressIncrease(craftTime) * productionEfficiency;
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
