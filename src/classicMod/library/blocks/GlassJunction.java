package classicMod.library.blocks;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.world.*;

import static mindustry.Vars.*;

public class GlassJunction extends Block {
    public float speed = 5.0F;
    public Color transparentColor = new Color(0.4F, 0.4F, 0.4F, 0.1F);

    public GlassJunction(String name) {
        super(name);

        hasItems = true;
        update = true;
        solid = false;
        unloadable = false;
        underBullets = true;
        isDuct = true;
        priority = -1.0F;
        envEnabled = 7;
        noUpdateDisabled = false;
    }

    public class GlassJunctionBuild extends Building {
        public Seq<itemPosition> itemPositions = new Seq<>(6);

        @Override
        public void updateTile() {
            for(var itemPos : itemPositions){
                float progress = itemPos.progress;
                progress += (this.delta() / speed * 2.15f);
                itemPos.updateProgress(progress);
                if (progress >= 2.05f){
                    Item item = itemPos.item;
                    Building dest = this.nearby(itemPos.rotation);
                    if (item != null && dest != null && dest.acceptItem(this, item) && dest.team == this.team) {
                        dest.handleItem(this, item);
                        itemPositions.remove(itemPos);
                    }
                }
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            int relative = source.relativeTo(this.tile);
            if (relative != -1 && itemPositions.size < 6){
                Building to = this.nearby(relative);
                itemPositions.add(new itemPosition(item, relative, 0f));
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
                Draw.z(Layer.blockUnder + 0.1f);
                float progress = itemPos.progress;
                int r = itemPos.rotation;
                int recDir = r - 2;
                Item current = itemPos.item;
                Tmp.v1.set(Geometry.d4x(recDir) * tilesize / 2f, Geometry.d4y(recDir) * tilesize / 2f)
                        .lerp(Geometry.d4x(r) * tilesize / 2f, Geometry.d4y(r) * tilesize / 2f,
                                Mathf.clamp((progress - 0.05f) / 2f));

                Draw.rect(current.fullIcon, x + Tmp.v1.x, y + Tmp.v1.y, itemSize, itemSize);
            }

            Draw.z(Layer.blockUnder + 0.2f);
            Draw.color(transparentColor);
            Draw.rect(name + "-bottom", x, y, rotation);
            Draw.color();
            Draw.rect(name, x, y);

            Draw.reset();

            /*if (current != null) {
                Draw.z(Layer.blockUnder + 0.1f);
                Tmp.v1.set(Geometry.d4x(recDir) * tilesize / 2f, Geometry.d4y(recDir) * tilesize / 2f)
                        .lerp(Geometry.d4x(r) * tilesize / 2f, Geometry.d4y(r) * tilesize / 2f,
                                Mathf.clamp((progress + 1f) / 2f));

                Draw.rect(current.fullIcon, x + Tmp.v1.x, y + Tmp.v1.y, itemSize, itemSize);
            }*/

            //super.draw();
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

            public void updateProgress(float progress){
                this.progress = progress;
            }
        }
    }
}
