package classicMod.library.blocks.neoplasiaBlocks;

import arc.util.Nullable;
import mindustry.entities.Effect;
import mindustry.type.*;
import mindustry.world.blocks.production.GenericCrafter;

public class CausticCrafter extends NeoplasmBlock{

    public Item from, to;
    public Effect craftEffect;
    public CausticCrafter(String name) {
        super(name);
    }

    public class CausticCrafterBuild extends NeoplasmBuilding {
        public void ConvertTo(Item toItem, Item fromItem){
            if (toItem == null || fromItem == null || !items.has(fromItem)) return;
            int total = 1;
            items.remove(fromItem, total);
            items.add(toItem, total);
        }

        @Override
        public void updateBeat() {
            ConvertTo(from, to);
            this.dump(to);
        }
    }
}
