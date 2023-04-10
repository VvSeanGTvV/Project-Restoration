package classicMod.library.blocks.customBlocks;

import arc.*;
import arc.graphics.g2d.*;
import classicMod.content.*;
import mindustry.content.*;
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
        for(Item item : requiredItem) {
            if (tier < item.hardness) tier = item.hardness;
        }
        drillTime = 5*60;
        drillEffect = ExtendedFx.spark;
        drawRim = false;
        drawMineItem = true;
        drawSpinSprite = true;
        topRegion = Core.atlas.find(rimString);
        itemRegion = Core.atlas.find("restored-mind-drill-middle");
        region = Core.atlas.find("restored-mind-drill-bottom");
        rotatorRegion = Core.atlas.find("restored-mind-drill-rotator");
    }

    @Override
    public boolean canMine(Tile tile){
        if(tile == null || tile.block().isStatic()) return false;
        boolean Mineable = false;
        Item drops = tile.drop();
        for(Item item : requiredItem) {
            Mineable = Objects.equals(drops.name, item.name);
        }
        return drops != null && Mineable && drops != blockedItem;
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
    }

}
