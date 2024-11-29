package classicMod.library.blocks.neoplasiaBlocks;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.*;
import arc.util.Nullable;
import mindustry.game.Team;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.production.Drill;

import java.util.Iterator;

public class DrillBeat extends NeoplasiaBlock {

    protected final ObjectIntMap<Item> oreCount = new ObjectIntMap<>();
    protected final Seq<Item> itemArray = new Seq<>();

    @Nullable
    protected Item returnItem;
    protected int returnCount;

    public DrillBeat(String name) {
        super(name);

        hasItems = true;
    }

    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if (this.isMultiblock()) {
            Iterator var4 = tile.getLinkedTilesAs(this, tempTiles).iterator();

            Tile other;
            do {
                if (!var4.hasNext()) {
                    return false;
                }

                other = (Tile)var4.next();
            } while(!this.canMine(other));

            return true;
        } else {
            return this.canMine(tile);
        }
    }


    public boolean canMine(Tile tile) {
        if (tile != null && !tile.block().isStatic()) {
            Item drops = tile.drop();
            return drops != null;
        } else {
            return false;
        }
    }

    public Item getDrop(Tile tile) {
        return tile.drop();
    }

    protected void countOre(Tile tile) {
        returnItem = null;
        returnCount = 0;
        oreCount.clear();
        itemArray.clear();
        Iterator<Tile> var2 = tile.getLinkedTilesAs(this, tempTiles).iterator();

        while(var2.hasNext()) {
            Tile other = var2.next();
            if (canMine(other)) {
                oreCount.increment(getDrop(other), 0, 1);
            }
        }

        ObjectIntMap.Keys<Item> var4 = oreCount.keys().iterator();

        while(var4.hasNext()) {
            Item item = (Item)var4.next();
            itemArray.add(item);
        }

        itemArray.sort((item1, item2) -> {
            int type = Boolean.compare(!item1.lowPriority, !item2.lowPriority);
            if (type != 0) {
                return type;
            } else {
                int amounts = Integer.compare(oreCount.get(item1, 0), oreCount.get(item2, 0));
                return amounts != 0 ? amounts : Integer.compare(item1.id, item2.id);
            }
        });
        if (itemArray.size != 0) {
            returnItem = (Item)itemArray.peek();
            returnCount = oreCount.get((Item)itemArray.peek(), 0);
        }
    }

    public class DrillBeatBuilding extends NeoplasiaBuilding {

        public Item dominantItem;

        public void onProximityUpdate() {
            super.onProximityUpdate();
            DrillBeat.this.countOre(this.tile);
            dominantItem = DrillBeat.this.returnItem;
            //this.dominantItems = Drill.this.returnCount;
        }

        public boolean shouldAmbientSound() {
            return efficiency > 0.01F && items.total() < itemCapacity;
        }

        @Override
        public void draw() {
            drawBeat(1f, 1f, 0.25f);
            Draw.rect(Core.atlas.find(name), x, y);
            Draw.color();

            Draw.reset();
        }

        @Override
        public void updateBeat() {
            if (timer(DrillBeat.this.timerDump, 5.0F)) {
                dump(dominantItem != null && items.has(dominantItem) ? dominantItem : null);
            }
            if (items.total() < itemCapacity) {
                if (Mathf.randomBoolean(0.5f)) offload(dominantItem);
            }
        }
    }
}
