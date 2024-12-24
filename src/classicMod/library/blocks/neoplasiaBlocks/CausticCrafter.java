package classicMod.library.blocks.neoplasiaBlocks;

import arc.Core;
import arc.graphics.g2d.Draw;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.type.Item;

public class CausticCrafter extends NeoplasmBlock{

    public int perBeat = 0;
    public Item from, to;
    public Effect craftEffect;
    public CausticCrafter(String name) {
        super(name);
        itemCapacity = 10;
        hasItems = true;
    }

    public class CausticCrafterBuild extends NeoplasmBuilding {
        int beat = 0;
        public void ConvertTo(Item toItem, Item fromItem){
            if (toItem == null || fromItem == null || !items.has(fromItem)) return;
            int total = 1;
            items.remove(fromItem, total);
            items.add(toItem, total);
        }
        
        @Override
        public void draw() {
            drawBeat(1f, 1f, 0.25f);
            Draw.rect(Core.atlas.find(name), x, y);
            Draw.color();

            Draw.reset();
        }

        

        @Override
        public boolean acceptItem(Building source, Item item) {
            return item == from && items.get(item) < itemCapacity;
        }

        @Override
        public void updateBeat() {
            beat++;
            if (beat >= perBeat) {
                beat = 0;
                if (craftEffect != null) craftEffect.at(x, y, to.color);
                ConvertTo(to, from);
                this.dump(to);
            }
            super.updateBeat();
        }
    }
}
