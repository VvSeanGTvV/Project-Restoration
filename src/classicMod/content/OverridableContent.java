package classicMod.content;

import mindustry.content.*;

public class OverridableContent {
    public void loadOverride(){
        Blocks.stone.itemDrop = ClassicItems.stone;
        Blocks.stone.playerUnmineable = true;

        Blocks.craters.itemDrop = ClassicItems.stone;
        Blocks.craters.playerUnmineable = true;

        SectorPresets.onset.alwaysUnlocked = false;
    }
}
