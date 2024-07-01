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
        unloadable = false;
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
        return new TextureRegion[]{Core.atlas.find(name)};
    }

    public class OverflowGateRevampBuild extends Building {

        Item lastitem;
        Building tehSource;
        @Override
        public void draw() {
            Draw.rect(Core.atlas.find(name), x, y);
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            
            return source.team == team && this.items.total() == 0;
        }

        @Override
        public void handleItem(Building source, Item item) {
            lastitem = item;
            tehSource = source;
        }

        public @Nullable Building getTargetTile(Item item, Building fromBlock, Building source, boolean flip){
            int from = relativeToEdge(source.tile);
            Building to = fromBlock.nearby(Mathf.mod(from + 2, 4));
            boolean
                    canForward = to != null && to.acceptItem(fromBlock, item) && to.team == team && !(to instanceof OverflowGateRevampBuild),
                    inv = invert == enabled;

            if(!canForward || inv){
                Building a = nearby(Mathf.mod(from - 1, 4));
                Building b = nearby(Mathf.mod(from + 1, 4));
                boolean ac = a != null && a.team == team && a.acceptItem(this, item);
                boolean bc = b != null && b.team == team && b.acceptItem(this, item);

                if(!ac && !bc){
                    return inv && canForward ? to : null;
                }

                if(ac && !bc){
                    to = a;
                }else if(bc && !ac){
                    to = b;
                }else{
                    if(rotation == 0){
                        to = a;
                        if(flip) fromBlock.rotation(1);
                    } else {
                       to = b;
                       if(flip) fromBlock.rotation(0);
                    }
                }
            }

            return to;
        }

        @Override
        public void update() {

            if(lastitem != null && tehSource != null){
                Building target = getTargetTile(lastitem, this, tehSource, false);

                if(target != null && lastitem != null){
                    getTargetTile(lastitem, this, tehSource, true);
                    target.handleItem(this, itemDrop);
                    this.items.remove(itemDrop, 1);
                    lastitem = null;
                }
            }

            if(lastitem == null && this.items.total() > 0){
                this.items.clear();
            }

        }

        //dude serious
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
