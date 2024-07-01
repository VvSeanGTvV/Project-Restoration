package classicMod.library.blocks.snek;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Interval;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.io.Reads;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.DirectionalItemBuffer;
import mindustry.world.meta.BlockGroup;

import java.awt.*;

/* Build 102 Overflow gate modified code to v7 */
public class OverflowGateRevamp extends Block {
    public float speed = 1f;
    public boolean invert = false;

    public OverflowGateRevamp(String name){
        super(name);
        hasItems = true;
        underBullets = true;
        update = true;
        destructible = true;
        group = BlockGroup.transportation;
        unloadable = true;
        canOverdrive = false;
        itemCapacity = 1;

        region = Core.atlas.find(name);
    }

    @Override
    public boolean outputsItems(){
        return true;
    }

    @Override
    protected TextureRegion[] icons() {
        var teh = (invert) ? Core.atlas.find("underflow-gate") : Core.atlas.find("overflow-gate");
        return new TextureRegion[]{teh};
    }

    public class OverflowGateRevampBuild extends Building {

        Item lastitem;
        Building tehSource;
        float time;
        
        @Override
        public void draw() {
            var teh = (invert) ? Core.atlas.find("underflow-gate") : Core.atlas.find("overflow-gate");
            Draw.rect(teh, x, y);
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            //Building target = getTargetTile(lastitem, this, source, false);
            
            return source.team == team && this.items.total() == 0 && lastitem == null;
        }

        @Override
        public void handleItem(Building source, Item item){
            items.add(item, 1);
            lastItem = item;
            time = 0f;
            tehSource = source.tile();
        }

        public @Nullable Building getTargetTile(Item item, Building fromBlock, Building source, boolean flip){
            int from = relativeToEdge(source.tile);
            Building to = nearby(Mathf.mod(from + 2, 4));
            boolean
                    canForward = to != null && to.acceptItem(this, item) && to.team == team && !(to instanceof OverflowGateRevampBuild),
                    inv = invert == enabled;

            if(!canForward || inv){
                Building a = nearby(Mathf.mod(from - 1, 4));
                Building b = nearby(Mathf.mod(from + 1, 4));
                boolean ac = a != null && a.team == team && a.acceptItem(this, item);
                boolean bc = b != null && b.team == team && b.acceptItem(this, item);

                if(!ac && !bc){
                    if(!inv) return null; else return to;
                }

                if(ac && !bc){
                    to = a;
                }else if(bc && !ac){
                    to = b;
                }else{
                    to = (rotation & (1 << from)) == 0 ? a : b;
                    if(flip) rotation ^= (1 << from);
                }
            }

            return to;
        }

        @Override
        public void update() {

            if(lastItem == null && items.any()){
                lastItem = items.first();
            }

            if(lastItem != null){
                time += 1f / speed * delta();
                Building target = getTileTarget(lastItem, this, lastInput, false);

                if(target != null && (time >= 1f)){
                    getTileTarget(lastItem, this, lastInput, true);
                    target.handleItem(this, lastItem);
                    items.remove(lastItem, 1);
                    lastItem = null;
                }
            }
            
        }

        @Override
        public byte version(){
            return 4;
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            if(revision == 1){
                new DirectionalItemBuffer(25).read(read);
            }else if(revision == 3){
                read.i();
            }

            items.clear();
        }
    }
}
