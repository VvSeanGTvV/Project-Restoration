package classicMod.library.blocks.customBlocks;

import mindustry.game.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.production.*;

import java.util.*;

public class SingleDrill extends Drill {
    /** Can only get that specific item **/
    public Item requiredItem;
    public SingleDrill(String name) {
        super(name);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return Objects.equals(tile.drop().name, requiredItem.name);
    }

    public void matchDrill(Item item){
        this.tier = item.hardness;
    }

    public class SingleDrillBuild extends DrillBuild {

    }

}
