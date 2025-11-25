package classicMod.content;

import mindustry.content.*;

public class OverridableContent {

    public void loadOverride(){
        Blocks.stone.itemDrop = RItems.stone;
        Blocks.stone.playerUnmineable = true;

        Blocks.craters.itemDrop = RItems.stone;
        Blocks.craters.playerUnmineable = true;

        Blocks.grass.attributes.set(RAttribute.grass, 0.15f);
    }
}
