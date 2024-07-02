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
import mindustry.content.Blocks;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.DirectionalItemBuffer;
import mindustry.world.blocks.distribution.OverflowGate;
import mindustry.world.meta.BlockGroup;

import java.awt.*;

import static mindustry.Vars.net;
import static mindustry.Vars.state;

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

        localizedName = (invert) ? Blocks.underflowGate.localizedName : Blocks.overflowGate.localizedName;
        region = Core.atlas.find(name);
    }

    @Override
    public boolean unlocked() {
        var teh = (invert) ? Blocks.underflowGate : Blocks.overflowGate;
        return net != null && net.client() ?
                alwaysUnlocked || unlocked || state.rules.researched.contains(name) :
                unlocked || alwaysUnlocked || teh.unlocked();
    }

    @Override
    protected TextureRegion[] icons() {
        var teh = (invert) ? Core.atlas.find("underflow-gate") : Core.atlas.find("overflow-gate");
        return new TextureRegion[]{teh};
    }

    public class OverflowGateRevampBuild extends Building {

        Item lastItem;
        Building lastInput;
        float time;
        boolean alreadyHandled = true;

        @Override
        public void draw() {
            var teh = (invert) ? Core.atlas.find("underflow-gate") : Core.atlas.find("overflow-gate");
            Draw.rect(teh, x, y);
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            //Building target = getTargetTile(lastitem, this, source, false);
            alreadyHandled = false;

            return source.team == team && this.items.total() == 0;
        }

        @Override
        public void handleItem(Building source, Item item){
            items.add(item, 1);
            lastItem = item;
            time = 0f;
            lastInput = source;

            if(item != null){
                Building target = getTargetTile(item, source, false);

                if(target != null){
                    getTargetTile(item, source, true);
                    target.handleItem(this, item);
                    items.remove(item, 1);
                    alreadyHandled = true;
                }
            }
        }

        public @Nullable Building getTargetTile(Item item, Building source, boolean flip){
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
                    return inv && canForward ? to : null;
                }

                if(ac && !bc){
                    to = a;
                }else if(bc && !ac){
                    to = b;
                }else{
                    if(rotation == 0){
                        to = a;
                        if(flip) rotation = 1;
                    } else {
                        to = b;
                        if(flip) rotation = 0;
                    }
                }
            }

            return to;
        }

        @Override
        public void update() {
            if(lastItem == null && items.total() > 0){
                items.clear();
            }

            if(items.total() > 0 && lastItem != null && !alreadyHandled) {
                Building target = getTargetTile(lastItem, lastInput, false);

                if (target != null) {
                    getTargetTile(lastItem, lastInput, true);
                    target.handleItem(this, lastItem);
                    items.remove(lastItem, 1);
                    lastItem = null;
                    alreadyHandled = true;
                }
            }
        }

        @Override
        public int removeStack(Item item, int amount){
            int result = super.removeStack(item, amount);
            if(result != 0 && item == lastItem){
                lastItem = null;
            }
            return result;
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
