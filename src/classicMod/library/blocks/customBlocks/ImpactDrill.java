package classicMod.library.blocks.customBlocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.graphics.Drawf;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.production.Drill;
import mindustry.world.meta.*;

import static classicMod.content.ExtendedStat.drillablesStack;
import static mindustry.Vars.*;

public class ImpactDrill extends Drill{
    public int outputAmount = 5;
    public float warmupTime = 60f;

    public ImpactDrill(String name){
        super(name);

        itemCapacity = 20;
        //does not drill in the traditional sense, so this is not even used
        hardnessDrillMultiplier = 0f;
        //generally at center
        drillEffectRnd = 0f;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Tile tile = world.tile(x, y);
        if(tile == null) return;

        countOre(tile);

        if(returnItem != null){
            float width = drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", (((outputAmount / (60 / drillTime)) / 2f) / 2f) * returnCount, 2), x, y, valid);
            float dx = x * tilesize + offset - width/2f - 4f, dy = y * tilesize + offset + size * tilesize / 2f + 5, s = iconSmall / 4f;
            Draw.mixcol(Color.darkGray, 1f);
            Draw.rect(returnItem.fullIcon, dx, dy - 1, s, s);
            Draw.reset();
            Draw.rect(returnItem.fullIcon, dx, dy, s, s);

            if(drawMineItem){
                Draw.color(returnItem.color);
                Draw.rect(itemRegion, tile.worldx() + offset, tile.worldy() + offset);
                Draw.color();
            }
        }else{
            Tile to = tile.getLinkedTilesAs(this, tempTiles).find(t -> t.drop() != null && (t.drop().hardness > tier || t.drop() == blockedItem));
            Item item = to == null ? null : to.drop();
            if(item != null){
                drawPlaceText(Core.bundle.get("bar.drilltierreq"), x, y, valid);
            }
        }
    }

    @Override
    public float getDrillTime(Item item){
        return ((outputAmount / (60 / drillTime)) / 2f) / 2f;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.drillSpeed);
        stats.remove(Stat.drillTier);

        stats.add(Stat.drillTier, drillablesStack(((outputAmount / (60 / drillTime)) / 2f) / 2f, outputAmount, drillMultipliers, b -> b instanceof Floor f && !f.wallOre && f.itemDrop != null &&
                f.itemDrop.hardness <= tier && f.itemDrop != blockedItem && (indexer.isBlockPresent(f) || state.isMenu())));

        stats.add(Stat.drillSpeed, ((outputAmount / (60 / drillTime)) / 2f) / 2f, StatUnit.itemsSecond);
    }

    public class ImpactDrilllBuild extends DrillBuild {

        float warmup = 0;
        @Override
        public void updateTile(){
            if(dominantItem == null){
                return;
            }
            if(timer(timerDump, dumpTime)){
                dump(items.has(dominantItem) ? dominantItem : null);
            }

            if(items.total() <= itemCapacity - outputAmount && dominantItems > 0 && efficiency > 0){

                warmup = Mathf.lerpDelta(warmup, warmupTime, efficiency);
                float wlD = (warmup / warmupTime);
                float speed = efficiency * (wlD);
                timeDrilled += speed;

                if (warmup >= warmupTime) {
                    lastDrillSpeed = dominantItems / drillTime * speed;
                    progress += delta() * dominantItems * speed;
                }
            }else{
                lastDrillSpeed = 0f;
                return;
            }

            if(dominantItems > 0 && progress >= drillTime && items.total() < itemCapacity){
                for(int i = 0; i < outputAmount; i++){
                    offload(dominantItem);
                }

                progress %= drillTime;
                drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), dominantItem.color);
            }
        }

        @Override
        public void draw(){
            var speedOffset = (warmup / (warmupTime / 2f));
            Draw.rect(region, x, y);
            drawDefaultCracks();

            //TODO change this draw bud

            if(dominantItem != null && drawMineItem){
                Draw.color(dominantItem.color);
                Draw.rect(itemRegion, x, y);
                Draw.color();
            }

            Drawf.spinSprite(rotatorRegion, x, y, timeDrilled * (rotateSpeed + speedOffset));
            Draw.rect(topRegion, x, y);
        }
    }
}