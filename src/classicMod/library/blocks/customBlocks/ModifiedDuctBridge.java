package classicMod.library.blocks.customBlocks;

import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.distribution.*;

public class ModifiedDuctBridge extends DirectionBridge {
    public float speed = 5f;

    public ModifiedDuctBridge(String name){
        super(name);

        itemCapacity = 4;
        hasItems = true;
        underBullets = true;
        isDuct = true;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(region, plan.drawx(), plan.drawy(), plan.rotation * 90);
    }

    public class ModifiedDuctBridgeBuild extends DirectionBridgeBuild{
        public float progress = 0f;
        public boolean transporter = false;

        @Override
        public void updateTile(){
            var link = findLink();
            if(link != null){
                if(occupied.length == 0){transporter = true;}else{transporter=false;}
                link.occupied[rotation % 4] = this;
                if(items.any() && link.items.total() < link.block.itemCapacity){
                    progress += edelta();
                    while(progress > speed){
                        Item next = items.take();
                        if(next != null && link.items.total() < link.block.itemCapacity){
                            link.handleItem(this, next);
                        }
                        progress -= speed;
                    }
                }
            }

            if(link == null && items.any()){
                Item next = items.first();
                if(moveForward(next)){
                    items.remove(next, 1);
                }
            }

            for(int i = 0; i < 4; i++){
                if(occupied[i] == null || occupied[i].rotation != i || !occupied[i].isValid()){
                    occupied[i] = null;
                }
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            //only accept if there's an output point.
            if(findLink() == null) return false;

            int rel = this.relativeToEdge(source.tile);
            return items.total() < itemCapacity && rel != rotation && occupied[(rel + 2) % 4] == null;
        }

        @Override
        public void draw() {
            if(!transporter){
                Draw.rect(name, x, y, rotdeg());
            }else{
                Draw.rect(name, x, y, rotdeg()-180f);
            }
        }
    }
}
