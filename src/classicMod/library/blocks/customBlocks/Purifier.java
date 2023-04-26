package classicMod.library.blocks.customBlocks;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.production.*;

import java.util.*;

public class Purifier extends GenericCrafter {

    public TextureRegion itemReg;
    public TextureRegion liquidRegion;
    public TextureRegion icoItem;
    public Item selectedItem;
    public Color itemColor;
    public boolean drawIconItem = true;

    public Purifier(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        String spriteName = "restored-mind-purifier";
        region = Core.atlas.find("restored-mind-purifer");
        itemReg = Core.atlas.find(spriteName+"-item");
        liquidRegion = Core.atlas.find(spriteName+"-liquid");
        itemColor = outputItem.item.color;
        selectedItem = outputItem.item;
        if(drawIconItem) {
            if(!Objects.equals(selectedItem.localizedName, selectedItem.name)){ icoItem = Core.atlas.find("restored-mind-drill-icon-"+selectedItem.localizedName); } else {
                drawIconItem = false; //Just turn to disable when it doesn't find it
            }
        }
    }

    @Override
    public TextureRegion[] icons() {
        if(drawIconItem) return new TextureRegion[]{region, icoItem};
        return new TextureRegion[]{region};
    }

    public class PurifierBuild extends GenericCrafterBuild {
        @Override
        public void draw() {
            Draw.rect(region, x, y);
            if(liquids.currentAmount() > 0.001f){
                Drawf.liquid(liquidRegion, x, y, liquids.currentAmount() / liquidCapacity, liquids.current().color);
            }
            Draw.color(itemColor);
            Draw.rect(itemReg, x, y);
        }
    }
}
