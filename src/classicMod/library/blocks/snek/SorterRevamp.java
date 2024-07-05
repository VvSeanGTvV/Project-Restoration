package classicMod.library.blocks.snek;


import arc.Core;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.*;
import arc.util.io.*;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.type.Item;
import mindustry.world.*;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.*;

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
        itemCapacity = 1;

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

        Boolean r0 = false, r1 = false;
        @Override
        public boolean acceptItem(Building source, Item item) {
            Building to = getTargetTile(item, this, source, r0);

            r0 = !r0;
            return to != null && to.acceptItem(this, item) && to.team == team;
        }

        @Override
        public void handleItem(Building source, Item item) {
            var to = getTargetTile(item, this, source, r1);
            if(to != null) { to.handleItem(this, item); }
            r1 = !r1;
        }

        public @Nullable Building getTargetTile(Item item, Building fromBlock, Building source, boolean flip){
            int from = relativeToEdge(source.tile);
            Building to = fromBlock.nearby(Mathf.mod(from + 2, 4));
            boolean
                    canFoward = to != null && to.acceptItem(fromBlock, item) && to.team == team && (((item == sortItem) != invert) == enabled),
                    inv = invert == enabled;

            if(!canFoward || inv){
                if(!inv) to = null;
                var offset = (flip) ? -1 : 1;
                Building a = fromBlock.nearby(Mathf.mod(from + offset, 4));
                boolean aB = a != null && a.team == team && a.acceptItem(fromBlock, item);
                if (aB) {
                    to = a;
                }
            }

            return to;
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

