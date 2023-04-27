package classicMod.library.blocks.customBlocks;

import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;

import static mindustry.Vars.*;
import static mindustry.Vars.tilesize;

public class ModifiedDuctBridge extends DirectionBridge {
    public float speed = 5f;
    public boolean dirFlip = false;

    public ModifiedDuctBridge(String name){
        super(name);

        itemCapacity = 4;
        hasItems = true;
        underBullets = true;
        isDuct = true;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        if(!dirFlip){
            Draw.rect(region, plan.drawx(), plan.drawy(), plan.rotation * 90);
        } else {
            Draw.rect(region, plan.drawx(), plan.drawy(), plan.rotation * -90);
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid, boolean line){
        int length = range;
        Building found = null;
        int dx = Geometry.d4x(rotation), dy = Geometry.d4y(rotation);

        //find the link
        for(int i = 1; i <= range; i++){
            Tile other = world.tile(x + dx * i, y + dy * i);

            if(other != null && other.build instanceof DirectionBridgeBuild build && build.block == this && build.team == player.team()){
                length = i;
                found = other.build;
                dirFlip = true;
                break;
            } else {
                dirFlip = false;
            }
        }

        if(line || found != null){
            Drawf.dashLine(Pal.placing,
                    x * tilesize + dx * (tilesize / 2f + 2),
                    y * tilesize + dy * (tilesize / 2f + 2),
                    x * tilesize + dx * (length) * tilesize,
                    y * tilesize + dy * (length) * tilesize
            );
        }

        if(found != null){
            if(line){
                Drawf.square(found.x, found.y, found.block.size * tilesize/2f + 2.5f, 0f);
            }else{
                Drawf.square(found.x, found.y, 2f);
            }
        }
    }

    public class ModifiedDuctBridgeBuild extends DirectionBridgeBuild{
        public float progress = 0f;
        public boolean transporter = false;

        @Override
        public void updateTile(){
            var link = findLink();
            if(link != null){
                transporter = (link.occupied.length > 0);
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
                transporter = false;
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
