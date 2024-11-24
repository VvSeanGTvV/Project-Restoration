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
        public Seq<itemPosition> itemPositions;

        public DuctJunctionBuild() {
            itemPositions = new Seq<>(capacity);
        }

        @Override
        public void updateTile() {
            for(int i = 0; i < capacity; ++i) {
                if (i < itemPositions.size && itemPositions.get(i) != null) {
                    itemPosition itemPos = itemPositions.get(i);
                    float progress = itemPos.progress;

                    Building dest = nearby(itemPos.rotation);
                    progress += edelta() / (speed + 0.05f) * 2f;

                    itemPos.updateProgress(progress);
                    if (dest != null){
                        if (progress >= 2f - 1F) {
                            Item item = itemPos.item;
                            if (item != null && dest.acceptItem(this, item) && dest.team == this.team) {
                                dest.handleItem(this, item);
                                itemPositions.remove(itemPos);
                            }
                        }
                    } else {
                        itemPos.updateProgress(0);
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
            if (relative != -1) itemPositions.add(new itemPosition(item, relative, -1f));
        }

        boolean accepts(int dir, int maximum){
            int number = 0;
            for (var itemPos : itemPositions){
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

            for(var itemPos : itemPositions){
                Draw.z(29.6F);
                float progress = itemPos.progress;
                int r = itemPos.rotation;
                int recDir = r - 2;
                Item current = itemPos.item;
                Tmp.v1.set(Geometry.d4x(recDir) * tilesize / 2f, Geometry.d4y(recDir) * tilesize / 2f)
                        .lerp(Geometry.d4x(r) * tilesize / 2f, Geometry.d4y(r) * tilesize / 2f,
                                Mathf.clamp((progress + 1f) / 2f));
                Tmp.v2.set(Geometry.d4x(r), Geometry.d4y(r)).lerp(Vec2.ZERO, Mathf.clamp((progress + 1f) / 2f));

                Draw.rect(current.fullIcon, x + Tmp.v1.x - Tmp.v2.x, y + Tmp.v1.y - Tmp.v2.y, itemSize, itemSize);
            }

            Draw.z(Layer.blockUnder + 0.2f);
            Draw.color(transparentColor);
            Draw.rect(name + "-bottom", x, y, rotation);
            Draw.color();
            Draw.rect(name, x, y);

            Draw.reset();
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.i(itemPositions.size);
            for (var itemPos : itemPositions){
                write.i(itemPos.rotation);
                write.i(itemPos.item.id);
                write.f(itemPos.progress);
            }
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            itemPositions.clear();

            int size = read.i();
            for (int i = 0; i < size; i++){
                int r = read.i();
                int id = read.i();
                Item item = content.item(id);
                float progress = read.f();
                itemPositions.add(new itemPosition(item, r, progress));
            }
        }

        public class itemPosition {
            int rotation;
            float progress;
            Item item;

            public itemPosition(Item item, int rotation, float progress){
                this.rotation = rotation;
                this.progress = progress;
                this.item = item;
            }

            public itemPosition(Item item, int rotation){
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
