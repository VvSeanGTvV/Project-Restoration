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
import mindustry.world.blocks.distribution.Junction;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.*;

public class DuctJunction extends Block {
    public float speed = 5.0F;
    public int capacity = 6;
    public Color transparentColor = new Color(0.4F, 0.4F, 0.4F, 0.1F);

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
        public itemData[][] itemBuffer = new itemData[4][capacity];
        public int[] index = new int[5];

        boolean validBuilding(Building dest, Item item){
            if (item == null || dest == null) return false;
            return dest.acceptItem(this, item) && dest.team == this.team;
        }

        @Override
        public void updateTile() {
            for (int i = 0; i < 4; ++i) {
                if (index[i] > 0) {
                    itemData data = itemBuffer[i][0];
                    if (index[i] > capacity) {
                        index[i] = capacity;
                    }

                    float progress = data.progress;

                    Building dest = nearby(data.rotation);
                    progress += edelta() / (speed + 0.05f) * 2f;

                    data.updateProgress((dest == null) ? 0f : progress);
                    if (dest != null) {
                        if (progress >= 2f - 1F) {
                            Item item = data.item;
                            if (item != null && validBuilding(dest, item)) {
                                dest.handleItem(this, item);
                                //itemBuffer[i][t - 1] = null;
                                System.arraycopy(itemBuffer[i], 1, itemBuffer[i], 0, Math.max(index[i] - 1, 0));
                                index[i]--;
                                //itemDataSeq.remove(data);
                            }
                        }
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
            if (relative != -1){
                itemBuffer[relative][index[relative]++] = new itemData(item, relative);
            }
        }

        int totalNonNull(int dir){
            int number = 0;
            for (var data : itemBuffer[dir]){
                if (data != null) number++;
            }
            return number;
        }

        boolean accepts(int dir, int maximum){
            return index[dir] < itemBuffer[dir].length;
        }
        @Override
        public boolean acceptItem(Building source, Item item) {
            int relative = source.relativeTo(this.tile);
            if (relative != -1 && accepts(relative, capacity)){
                Building to = nearby(relative);
                return to != null && to.team == this.team && to.acceptItem(this, item);
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
            for(int i = 0; i < 4; ++i){
                for(var data : itemBuffer[i]){
                    if (data == null) continue;

                    Draw.z(29.6F);
                    float progress = data.progress;
                    int r = data.rotation;
                    int recDir = r - 2;
                    Item current = data.item;
                    Tmp.v1.set(Geometry.d4x(recDir) * tilesize / 2f, Geometry.d4y(recDir) * tilesize / 2f)
                            .lerp(Geometry.d4x(r) * tilesize / 2f, Geometry.d4y(r) * tilesize / 2f,
                                    Mathf.clamp((progress + 1f) / 2f));
                    Tmp.v2.set(Geometry.d4x(r) * 0.95f, Geometry.d4y(r) * 0.95f).lerp(Vec2.ZERO, Mathf.clamp((progress + 1f) / 2f));

                    Draw.rect(current.fullIcon, x + Tmp.v1.x - Tmp.v2.x, y + Tmp.v1.y - Tmp.v2.y, itemSize, itemSize);
                }
            }

        }

        @Override
        public void write(Writes write) {
            super.write(write);

            for(int r = 0; r < 4; ++r) {
                write.i(index[r]);
                for (var data : itemBuffer[r]) {
                    if (data == null) continue;
                    write.i(data.rotation);
                    write.i(data.item.id);
                    write.f(data.progress);
                }
            }
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            for(int r = 0; r < 4; ++r) {
                int size = read.i();
                index[r] = size;
                for (int i = 0; i < size; i++) {
                    if (size < itemBuffer[r].length) {
                        int a = read.i();
                        int id = read.i();
                        Item item = content.item(id);
                        float progress = read.f();
                        itemBuffer[r][i] = new itemData(item, a, progress);
                    }
                    //itemDataSeq.add(new itemData(item, r, progress));
                }
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
                this.progress = -1f;
                this.item = item;
            }


            public void updateProgress(float progress){
                this.progress = progress;
            }
        }
    }
}
