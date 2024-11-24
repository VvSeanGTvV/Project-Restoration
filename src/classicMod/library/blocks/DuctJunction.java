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
import mindustry.type.Item;
import mindustry.world.Block;

import static mindustry.Vars.*;

public class DuctJunction extends Block {
    public float speed = 5.0F;
    public int capacity = 6;
    public Color transparentColor = new Color(0.4F, 0.4F, 0.4F, 0.1F);

    public DuctJunction(String name) {
        super(name);

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
    protected TextureRegion[] icons() {
        return new TextureRegion[]{Core.atlas.find(name + "-bottom"), Core.atlas.find(name)};
    }

    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.alpha(0.5F);
        Draw.rect(name + "-bottom", plan.drawx(), plan.drawy(), (float)(plan.rotation * 90));
        Draw.color();
        Draw.rect(name, plan.drawx(), plan.drawy(), (float)(plan.rotation * 90));
    }

    public class DuctJunctionBuild extends Building {
        public Seq<itemData> itemDataSeq;

        public DuctJunctionBuild() {
            itemDataSeq = new Seq<>(capacity);
        }

        @Override
        public void updateTile() {
            for(int i = 0; i < capacity; ++i) {
                if (i < itemDataSeq.size && itemDataSeq.get(i) != null) {
                    itemData data = itemDataSeq.get(i);
                    float progress = data.progress;

                    Building dest = nearby(data.rotation);
                    progress += edelta() / (speed + 0.05f) * 2f;

                    data.updateProgress(progress);
                    if (dest != null){
                        if (progress >= 2f - 1F) {
                            Item item = data.item;
                            if (item != null && dest.acceptItem(this, item) && dest.team == this.team) {
                                dest.handleItem(this, item);
                                itemDataSeq.remove(data);
                            }
                        }
                    } else {
                        data.updateProgress(0);
                    }
                }
            }
        }

        public int acceptStack(Item item, int amount, Teamc source) {
            return 0;
        }

        @Override
        public void handleItem(Building source, Item item) {
            int relative = source.relativeTo(this.tile);
            if (relative != -1) itemDataSeq.add(new itemData(item, relative, -1f));
        }

        boolean accepts(int dir, int maximum){
            int number = 0;
            for (var itemPos : itemDataSeq){
                if (itemPos.rotation == dir) number++;
            }
            return number < maximum;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            int relative = source.relativeTo(this.tile);
            if (relative != -1 && accepts(relative, capacity)){
                Building to = nearby(relative);
                return to != null && to.team == this.team;
            } else {
                return false;
            }
        }

        @Override
        public void draw() {
            Draw.z(Layer.blockUnder);
            Draw.rect(name + "-bottom", x, y);

            drawItems();

            Draw.z(Layer.blockUnder + 0.2f);
            Draw.color(transparentColor);
            Draw.rect(name + "-bottom", x, y, rotation);
            Draw.color();
            Draw.rect(name, x, y);

            Draw.reset();
        }

        public void drawItems(){
            for(var data : itemDataSeq){
                Draw.z(29.6F);
                float progress = data.progress;
                int r = data.rotation;
                int recDir = r - 2;
                Item current = data.item;
                Tmp.v1.set(Geometry.d4x(recDir) * tilesize / 2f, Geometry.d4y(recDir) * tilesize / 2f)
                        .lerp(Geometry.d4x(r) * tilesize / 2f, Geometry.d4y(r) * tilesize / 2f,
                                Mathf.clamp((progress + 1f) / 2f));
                Tmp.v2.set(Geometry.d4x(r), Geometry.d4y(r)).lerp(Vec2.ZERO, Mathf.clamp((progress + 1f) / 2f));

                Draw.rect(current.fullIcon, x + Tmp.v1.x - Tmp.v2.x, y + Tmp.v1.y - Tmp.v2.y, itemSize, itemSize);
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.i(itemDataSeq.size);
            for (var data : itemDataSeq){
                write.i(data.rotation);
                write.i(data.item.id);
                write.f(data.progress);
            }
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            itemDataSeq.clear();

            int size = read.i();
            for (int i = 0; i < size; i++){
                int r = read.i();
                int id = read.i();
                Item item = content.item(id);
                float progress = read.f();
                itemDataSeq.add(new itemData(item, r, progress));
            }
        }

        public class itemData {
            int rotation;
            float progress;
            Item item;

            public itemData(Item item, int rotation, float progress){
                this.rotation = rotation;
                this.progress = progress;
                this.item = item;
            }

            public itemData(Item item, int rotation){
                this.rotation = rotation;
                this.progress = 0f;
                this.item = item;
            }


            public void updateProgress(float progress){
                this.progress = progress;
            }
        }
    }
}
