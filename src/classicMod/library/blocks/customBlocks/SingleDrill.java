package classicMod.library.blocks.customBlocks;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import classicMod.content.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.*;

import java.util.*;

import static mindustry.Vars.*;

public class SingleDrill extends Drill {
    /** Can only get that specific item **/
    public Item[] requiredItem = new Item[]{Items.copper};
    /** Drill's rim texture in string **/
    public String rimString = "restored-mind-default-rim";
    protected TextureRegion bottomRegion;
    protected TextureRegion rimRegion;
    protected boolean canPlacable = false;
    public SingleDrill(String name) {
        super(name);
        drillTime = 5*60;
        drillEffect = ExtendedFx.spark;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        canPlacable = false;
        for(Item item : requiredItem) {
            canPlacable = Objects.equals(tile.drop().name, item.name);
        }
        return canPlacable;
    }

    @Override
    public void init() {
        rimRegion = Core.atlas.find(rimString);
        itemRegion = Core.atlas.find("restored-mind-drill-middle");
        bottomRegion = Core.atlas.find("restored-mind-drill-bottom");
        rotatorRegion = Core.atlas.find("restored-mind-drill-rotator");
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{Core.atlas.find("restored-mind-drill-bottom"), Core.atlas.find("restored-mind-drill-rotator"), Core.atlas.find("restored-mind-default-rim")};
    }

    public void matchDrill(Item item){
        this.tier = item.hardness;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.drillTier);
        for(Item item : requiredItem) {
            stats.add(Stat.drillTier, StatValues.blocks(b -> b instanceof Floor f && !f.wallOre && f.itemDrop != null && f.itemDrop.hardness <= tier && f.itemDrop != blockedItem && Objects.equals(f.itemDrop.name, item.name) && (indexer.isBlockPresent(f) || state.isMenu())));
        }
    }

    public class SingleDrillBuild extends DrillBuild {
        @Override
        public void updateTile(){
            matchDrill(dominantItem);
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

                if(Mathf.chanceDelta(updateEffectChance * warmup))
                    updateEffect.at(x + Mathf.range(size * 2f), y + Mathf.range(size * 2f));
            }else{
                lastDrillSpeed = 0f;
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
                return;
            }

            if(dominantItems > 0 && progress >= delay && items.total() < itemCapacity){
                offload(dominantItem);

                progress %= delay;
                drillEffect.at(x, y);

                //if(wasVisible && Mathf.chanceDelta(updateEffectChance * warmup)) drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), dominantItem.color);
            }
        }

        @Override
        public void draw(){
            rimRegion = Core.atlas.find(rimString);
            itemRegion = Core.atlas.find("restored-mind-drill-middle");
            bottomRegion = Core.atlas.find("restored-mind-drill-bottom");
            rotatorRegion = Core.atlas.find("restored-mind-drill-rotator");

            Draw.rect(bottomRegion, x, y);
            Draw.z(Layer.blockCracks);
            drawDefaultCracks();

            Draw.z(Layer.blockAfterCracks);

            Drawf.spinSprite(rotatorRegion, x, y, timeDrilled * rotateSpeed);

            Draw.rect(rimRegion, x, y);

            Draw.color(dominantItem.color);
            Draw.rect(itemRegion, x, y);
            Draw.color();
        }
    }

}
