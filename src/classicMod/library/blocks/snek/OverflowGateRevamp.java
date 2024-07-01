package classicMod.library.blocks.snek;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
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
        update = false;
        destructible = true;
        group = BlockGroup.transportation;
        instantTransfer = false;
        unloadable = false;
        canOverdrive = false;
        itemCapacity = 0;

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

        boolean reverse;
        @Override
        public boolean acceptItem(Building source, Item item){
            Building to = getTileTarget(item, this, source);

            return to != null && to.acceptItem(this, item) && to.team == team;
        }

        @Override
        public void draw() {
            Draw.rect(Core.atlas.find(name), x, y);
        }

        @Override
        public void handleItem(Building source, Item item){
            Building target = getTileTarget(item, this, source);

            if(target != null) target.handleItem(this, item);
        }

        public @Nullable Building getTileTarget(Item item, Building fromBlock, Building src) {
            int from = relativeToEdge(src.tile);
            if (from == -1) return null;
            Building to = fromBlock.nearby(Mathf.mod(from + 2, 4));
            boolean
                    canFoward = to != null && to.acceptItem(fromBlock, item) && to.team == team,
                    inv = invert == enabled;

            if(!canFoward || inv){
                int retry = 0;
                while (retry < 2) {
                    var offset = (reverse) ? 1 : -1;
                    Building a = fromBlock.nearby(Mathf.mod(from + offset, 4));
                    boolean aB = a != null && a.team == team && a.acceptItem(fromBlock, item);
                    reverse = !reverse;
                    Log.info("overflow output " + a + " yes " + aB + " opposite " + reverse);
                    if (aB) {
                        to = a;
                        break;
                    }
                    retry++;
                }
            }

            return to;
        }

        public @Nullable Building getTileTarget(Item item, Building src, boolean flip){
            int from = relativeToEdge(src.tile);
            if(from == -1) return null;
            Building to = nearby((from + 2) % 4);
            boolean
                    //fromInst = src.block.instantTransfer, < ignore this bud
                    canForward = to != null && to.team == team && to.acceptItem(this, item),
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
                    to = (rotation & (1 << from)) == 0 ? a : b;
                    if(flip) rotation ^= (1 << from);
                }
            }

            return to;
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
