package classicMod.library.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.*;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.io.TypeIO;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.Junction;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;

import static mindustry.Vars.*;

public class DuctJunction extends Block {
    public float speed = 5.0F;
    public Color transparentColor = new Color(0.4F, 0.4F, 0.4F, 0.1F);
    public TextureRegion bottomRegion;

    public DuctJunction(String name) {
        super(name);

        group = BlockGroup.transportation;
        hasItems = true;
        update = true;
        solid = false;
        unloadable = false;
        underBullets = true;

        conveyorPlacement = true;
        isDuct = true;

        priority = -1.0F;
        envEnabled = 7;
        noUpdateDisabled = false;
    }

    @Override
    public void load(){
        super.load();
        squareSprite = true;
        bottomRegion = Core.atlas.find(name + "-bottom");
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{Core.atlas.find(name + "-bottom"), Core.atlas.find(name)};
    }

    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.alpha(0.5F);
        Draw.rect(name + "-bottom", plan.drawx(), plan.drawy(), (float)(plan.rotation * 90));
        Draw.color();
        Draw.rect(name, plan.drawx(), plan.drawy(), (float)(plan.rotation * 90));
    }

    @Override
    public void init() {
        super.init();
        stats.remove(Stat.itemCapacity);
    }

    public class DuctJunctionBuild extends Building {
        Item[] itemdata = new Item[4];
        float[] times = new float[4];


        @Override
        public void draw(){
            Draw.z(Layer.blockUnder);
            Draw.rect(bottomRegion, x, y);

            Draw.z(Layer.blockUnder + 0.1f);

            for(int i = 0; i < 4; i++){
                Item current = itemdata[i];

                if(current != null){
                    float progress = (Mathf.clamp((times[i] + 1f) / (2f - 1f/speed)) - 0.5f) * 2f;

                    Draw.rect(current.fullIcon,
                            x + Geometry.d4x(i) * tilesize / 2f * progress,
                            y + Geometry.d4y(i) * tilesize / 2f * progress,
                            itemSize, itemSize
                    );
                }
            }

            Draw.color(transparentColor);
            Draw.rect(bottomRegion, x, y);
            Draw.color();

            Draw.z(Layer.blockUnder + 0.2f);

            Draw.rect(region, x, y);
        }

        @Override
        public void updateTile(){
            float inc = edelta() / speed * 2f;

            for(int i = 0; i < 4; i++){
                Item item = itemdata[i];
                if(item != null){

                    times[i] += inc;
                    if(times[i] >= (1f - 1f/speed)){
                        Building next = nearby(i);

                        if(next != null && next.team == team && next.acceptItem(this, item)){
                            next.handleItem(this, item);
                            itemdata[i] = null;
                            items.remove(item, 1);
                            times[i] %= (1f - 1f/speed);
                        }
                    }
                }else{
                    //TODO: reset progress or not?
                    times[i] = 0f;
                }
            }
        }

        @Override
        public void handleItem(Building source, Item item){
            int relative = source.relativeTo(tile);
            if(relative == -1) return;
            itemdata[relative] = item;
            times[relative] = -1f;
            items.add(item, 1);
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            int relative = source.relativeTo(tile);

            if(relative == -1 || itemdata[relative] != null) return false;
            Building to = nearby(relative);
            return to != null && to.team == team;
        }

        @Override
        public int acceptStack(Item item, int amount, Teamc source){
            return 0;
        }

        @Override
        public int removeStack(Item item, int amount){
            int removed = 0;
            for(int i = 0; i < 4 && amount > 0; i++){
                if(itemdata[i] == item){
                    amount --;
                    removed ++;
                    itemdata[i] = null;
                    items.remove(item, 1);
                }
            }
            return removed;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            for(int i = 0; i < 4; i++){
                write.f(times[i]);
                TypeIO.writeItem(write, itemdata[i]);
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            for(int i = 0; i < 4; i++){
                times[i] = read.f();
                itemdata[i] = TypeIO.readItem(read);
            }
        }
    }
}
