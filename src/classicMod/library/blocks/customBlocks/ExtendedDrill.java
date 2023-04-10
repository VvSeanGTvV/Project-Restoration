package classicMod.library.blocks.customBlocks;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.*;

import java.util.*;

import static mindustry.Vars.indexer;
import static mindustry.Vars.state;

public class ExtendedDrill extends Drill {
    public String rimString = "restored-mind-default-rim";
    public ItemStack acceptedItem = new ItemStack(Items.copper, 1);
    public @Nullable ItemStack[] acceptedItems;
    protected TextureRegion topRegion = Core.atlas.find(rimString);
    protected TextureRegion itemRegion = Core.atlas.find("restored-mind-drill-middle");
    protected TextureRegion region = Core.atlas.find("restored-mind-drill-bottom");
    protected TextureRegion rotatorRegion = Core.atlas.find("restored-mind-drill-rotator");
    public ExtendedDrill(String name) {
        super(name);
    }
    @Override
    public void init() {
        if (acceptedItems == null && acceptedItem != null) {
            acceptedItems = new ItemStack[]{acceptedItem};
        }
        assert acceptedItems != null;
        for(ItemStack item : acceptedItems) {
            if(tier<item.item.hardness)tier = item.item.hardness;
        }
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.drillTier);
        for(ItemStack item : acceptedItems) {
            stats.add(Stat.drillTier, StatValues.blocks(b -> b instanceof Floor f && !f.wallOre && f.itemDrop != null && f.itemDrop.hardness <= tier && f.itemDrop != blockedItem && Objects.equals(f.itemDrop.name, item.item.name) && (indexer.isBlockPresent(f) || state.isMenu())));
        }
    }

    @Override
    public boolean canMine(Tile tile){
        if(tile == null || tile.block().isStatic()) return false;
        boolean canMineable = false;
        Item drops = tile.drop();
        for(ItemStack item : acceptedItems) {
            canMineable = drops != null && drops.hardness == item.item.hardness && drops != blockedItem;
        }
        return canMineable;
    }

    public class ExtendedDrillBuild extends DrillBuild {
        @Override
        public void updateTile(){
            if(timer(timerDump, dumpTime)){
                dump(dominantItem != null && items.has(dominantItem) ? dominantItem : null);
            }

            if(dominantItem == null){
                return;
            }

            timeDrilled += warmup * delta();

            float delay = getDrillTime(dominantItem);

            if(items.total() < itemCapacity && dominantItems > 0 && efficiency > 0){
                float speed = Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency) * efficiency;

                lastDrillSpeed = (speed * dominantItems * warmup) / delay;
                warmup = Mathf.approachDelta(warmup, speed, warmupSpeed);
                progress += delta() * dominantItems * speed * warmup;

            }else{
                lastDrillSpeed = 0f;
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
                return;
            }

            if(dominantItems > 0 && progress >= delay && items.total() < itemCapacity){
                offload(dominantItem);

                progress %= delay;

                drillEffect.at(x, y);
            }
        }

        @Override
        public void draw() {
            topRegion = Core.atlas.find(rimString);
            itemRegion = Core.atlas.find("restored-mind-drill-middle");
            region = Core.atlas.find("restored-mind-drill-bottom");
            rotatorRegion = Core.atlas.find("restored-mind-drill-rotator");

            Draw.rect(region, x, y);
            Draw.z(Layer.blockCracks);
            drawDefaultCracks();

            Draw.z(Layer.blockAfterCracks);
            Drawf.spinSprite(rotatorRegion, x, y, timeDrilled * rotateSpeed);
            Draw.rect(topRegion,x , y);
            if(dominantItem != null){
                Draw.color(dominantItem.color);
                Draw.rect(itemRegion, x, y);
                Draw.color();
            }
        }
    }
}
