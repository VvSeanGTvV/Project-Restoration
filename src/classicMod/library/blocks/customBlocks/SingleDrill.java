package classicMod.library.blocks.customBlocks;

import mindustry.content.*;
import mindustry.game.*;
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
    protected boolean canPlacable = false;
    public SingleDrill(String name) {
        super(name);
        drillTime = 5;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        canPlacable = false;
        for(Item item : requiredItem) {
            canPlacable = Objects.equals(tile.drop().name, item.name);
        }
        return canPlacable;
    }

    public void matchDrill(Item item){
        this.tier = item.hardness;
    }

    @Override
    public void setStats() {
        //super.setStats();
        stats.remove(Stat.drillTier);
        for(Item item : requiredItem) {
            stats.add(Stat.drillTier, StatValues.blocks(b -> b instanceof Floor f && !f.wallOre && f.itemDrop != null && f.itemDrop.hardness <= tier && f.itemDrop != blockedItem && Objects.equals(f.itemDrop.name, item.name) && (indexer.isBlockPresent(f) || state.isMenu())));
        }
    }

    public class SingleDrillBuild extends DrillBuild {

    }

}
