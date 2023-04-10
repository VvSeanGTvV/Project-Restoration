package classicMod.library.blocks.customBlocks;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import classicMod.content.*;
import mindustry.content.*;
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
    public Item requiredItem = Items.copper;
    public boolean itemSingular = true;
    /** Drill's rim texture in string **/
    public String rimString = "restored-mind-default-rim";
    protected TextureRegion topRegion = Core.atlas.find(rimString);
    protected TextureRegion itemRegion = Core.atlas.find("restored-mind-drill-middle");
    protected TextureRegion region = Core.atlas.find("restored-mind-drill-bottom");
    protected TextureRegion rotatorRegion = Core.atlas.find("restored-mind-drill-rotator");
    public SingleDrill(String name) {
        super(name);
        tier = requiredItem.hardness;
        drillEffect = ExtendedFx.spark;
        drawRim = false;
        drawMineItem = true;
        drawSpinSprite = true;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Tile tile = world.tile(x, y);
        if(tile == null) return;

        countOre(tile);

        if(returnItem != null && !Objects.equals(returnItem.name, requiredItem.name)){
            float width = drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", 60f / getDrillTime(returnItem) * returnCount, 2), x, y, valid);
            float dx = x * tilesize + offset - width/2f - 4f, dy = y * tilesize + offset + size * tilesize / 2f + 5, s = iconSmall / 4f;
            Draw.mixcol(Color.darkGray, 1f);
            Draw.rect(returnItem.fullIcon, dx, dy - 1, s, s);
            Draw.reset();
            Draw.rect(returnItem.fullIcon, dx, dy, s, s);

            /*if(drawMineItem){
                Draw.color(returnItem.color);
                Draw.rect(Core.atlas.find("restored-mind-drill-middle"), tile.worldx() + offset, tile.worldy() + offset);
                Draw.color();
            }*/
        }else{
            Tile to = tile.getLinkedTilesAs(this, tempTiles).find(t -> t.drop() != null && (t.drop().hardness > tier || t.drop() == blockedItem));
            Item item = to == null ? null : to.drop();
            if(item != null){
                drawPlaceText(Core.bundle.get("bar.drilltierreq"), x, y, valid);
            }
        }
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{Core.atlas.find("restored-mind-drill-bottom"), Core.atlas.find("restored-mind-drill-rotator"), Core.atlas.find(rimString)};
    }

    @Override
    public boolean canMine(Tile tile){
        if(tile == null || tile.block().isStatic()) return false;
        boolean Mineable = false;
        Item drops = tile.drop();
        Mineable = Objects.equals(drops.name, requiredItem.name);
        return drops != null && Mineable && drops != blockedItem;
    }

    @Override
    public void setStats() {
        super.setStats();
        tier = requiredItem.hardness;
        stats.remove(Stat.drillTier);
       
        stats.add(Stat.drillTier, StatValues.blocks(b -> b instanceof Floor f && !f.wallOre && f.itemDrop != null && f.itemDrop.hardness <= tier && f.itemDrop != blockedItem && Objects.equals(f.itemDrop.name, requiredItem.name) && (indexer.isBlockPresent(f) || state.isMenu())));
        
    }

    public class SingleDrillBuild extends DrillBuild {
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
