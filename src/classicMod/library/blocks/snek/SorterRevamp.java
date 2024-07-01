package classicMod.library.blocks.snek;


import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.DirectionalItemBuffer;
import mindustry.world.Tile;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.distribution.Sorter;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.*;
import static mindustry.Vars.content;

public class SorterRevamp extends Block {
    public TextureRegion cross = Core.atlas.find(name+"-cross", "cross-full");
    public boolean invert;

    public SorterRevamp(String name) {
        super(name);
        update = false;
        destructible = true;
        underBullets = true;
        instantTransfer = false;
        group = BlockGroup.transportation;
        configurable = true;
        unloadable = false;
        saveConfig = true;
        clearOnDoubleTap = true;

        config(Item.class, (SorterRevampBuild tile, Item item) -> tile.sortItem = item);
        configClear((SorterRevampBuild tile) -> tile.sortItem = null);

        region = Core.atlas.find(name);
    }

    @Override
    public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list) {
        drawPlanConfigCenter(plan, plan.config, "center", true);
    }

    @Override
    public boolean outputsItems() {
        return true;
    }

    @Override
    public int minimapColor(Tile tile) {
        var build = (SorterRevampBuild) tile.build;
        return build == null || build.sortItem == null ? 0 : build.sortItem.color.rgba();
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{Core.atlas.find("source-bottom"), Core.atlas.find(name)};
    }

    public class SorterRevampBuild extends Building {
        public @Nullable Item sortItem;

        public Seq<Building> ConnectedBuilding;

        @Override
        public void configured(Unit player, Object value) {
            super.configured(player, value);

            if (!headless) {
                renderer.minimap.update(tile);
            }
        }

        @Override
        public void draw() {
            Draw.rect(Core.atlas.find(name), x, y);
            TextureRegion cross = Core.atlas.find(name+"-cross", "cross-full");
            if (sortItem == null) {
                Draw.rect(cross, x, y);
            } else {
                Draw.color(sortItem.color);
                Fill.square(x, y, tilesize / 2f - 0.00001f);
                Draw.color();
            }

            //super.draw();
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            Building to = getTileTarget(item, this, source);

            return to != null && to.acceptItem(this, item) && to.team == team;
        }

        @Override
        public void handleItem(Building source, Item item) {
            reverse = !reverse;
            Building target = getTileTarget(item, this, source);

            if(target != null) target.handleItem(this, item);
        }

        boolean reverse;
        public @Nullable Building getTileTarget(Item item, Building fromBlock, Building src) {
            int from = relativeToEdge(src.tile);
            if (from == -1) return null;
            Building to = fromBlock.nearby(Mathf.mod(from + 2, 4));
            boolean
                    canFoward = (((item == sortItem) != invert) == enabled) && to != null && to.acceptItem(fromBlock, item) && to.team == team,
                    inv = invert == enabled;

            if(!canFoward || inv){
                to = null;
                var offset = (reverse) ? -1 : 1;
                Building a = fromBlock.nearby(Mathf.mod(from + offset, 4));
                boolean aB = a != null && a.team == team && a.acceptItem(fromBlock, item);
                if (aB) {
                    to = a;
                }

                var offsetb = (reverse) ? 1 : -1;
                Building ab = fromBlock.nearby(Mathf.mod(from + offsetb, 4));
                boolean aBb = ab != null && ab.team == team && ab.acceptItem(fromBlock, item);
                if (!aB && aBb) {
                    to = ab;
                }
            }

            return to;
        }

        @Override
        public void update() {
            reverse = !reverse;
        }


        @Override
        public void buildConfiguration(Table table) {
            ItemSelection.buildTable(SorterRevamp.this, table, content.items(), () -> sortItem, this::configure, selectionRows, selectionColumns);
        }

        @Override
        public Item config() {
            return sortItem;
        }

        @Override
        public byte version() {
            return 2;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.s(sortItem == null ? -1 : sortItem.id);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            sortItem = content.item(read.s());

            if (revision == 1) {
                new DirectionalItemBuffer(20).read(read);
            }
        }
    }
}

