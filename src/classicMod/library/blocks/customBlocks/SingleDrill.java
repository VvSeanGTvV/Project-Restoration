package classicMod.library.blocks.customBlocks;

import arc.Core;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Nullable;
import classicMod.content.RFx;
import classicMod.uCoreGraphics.RPixmaps;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.graphics.*;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.production.Drill;
import mindustry.world.meta.*;

import java.util.Objects;

import static mindustry.Vars.*;

public class SingleDrill extends Drill {
    /**
     * Can only get that specific item
     **/
    public Item requiredItem = Items.copper;
    public TextureRegion topRegion;
    public TextureRegion itemRegion;
    public TextureRegion region;
    public TextureRegion rotatorRegion;
    public TextureRegion icoItem;

    /**
     * Draws the custom icon for the drill's icon
     **/
    public boolean drawIconItem = false;

    public SingleDrill(String name) {
        super(name);
        tier = requiredItem.hardness;
        drillEffect = RFx.spark;
        updateEffect = Fx.none;
        hasLiquids = false;
        drawRim = false;
        drawMineItem = false;
        drawSpinSprite = true;
    }

    @Override
    public void load() {
        super.load();
        itemRegion = Core.atlas.find("restored-mind-drill-middle");
        region = Core.atlas.find("restored-mind-drill-bottom");
        rotatorRegion = Core.atlas.find("restored-mind-drill-rotator");
        topRegion = Core.atlas.find("restored-mind-default-rim");
        icoItem = Core.atlas.find("restored-mind-drill-border-" + requiredItem.name);
    }

    @Override
    public void createIcons(MultiPacker packer) {
        Seq<Pixmap> toDispose = new Seq<>();
        var atlasA = Core.atlas.find("restored-mind-drill-border").asAtlas();
        if (atlasA != null) {
            String regionName = atlasA.name;
            Pixmap out = RPixmaps.replaceWhite(Core.atlas.getPixmap(atlasA), requiredItem.color);

            Drawf.checkBleed(out);

            packer.add(MultiPacker.PageType.main, regionName + "-" + requiredItem.name, out);
        }

        toDispose.each(Pixmap::dispose);
        super.createIcons(packer);
    }

    @Override
    public TextureRegion[] icons() {
        if (drawIconItem) return new TextureRegion[]{region, rotatorRegion, topRegion, Core.atlas.find("restored-mind-drill-border-" + requiredItem.name)};
        return new TextureRegion[]{region, rotatorRegion, topRegion};
    }

    @Override
    public boolean canMine(Tile tile) {
        if (tile == null || tile.block().isStatic()) return false;
        boolean Mineable = false;
        Item drops = tile.drop();
        if (drops != null) Mineable = Objects.equals(drops.name, requiredItem.name);
        return drops != null && Mineable && drops != blockedItem;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        Tile tile = world.tile(x, y);
        if(tile == null) return;
        float s = iconSmall / 4f;

        Draw.rect(region, x, y);
        Draw.rect(rotatorRegion, x, y);
        Draw.rect(topRegion, x, y);
        if (drawIconItem && valid) Draw.rect(icoItem, x, y, s, s);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.remove(Stat.drillTier);

        stats.add(Stat.drillTier, StatValues.blocks(b -> b instanceof Floor f && !f.wallOre && f.itemDrop != null && f.itemDrop != blockedItem && Objects.equals(f.itemDrop.name, requiredItem.name) && (indexer.isBlockPresent(f) || state.isMenu())));

    }

    public class SingleDrillBuild extends DrillBuild {
        @Override
        public void updateTile() {
            if (timer(timerDump, dumpTime)) {
                dump(dominantItem != null && items.has(dominantItem) ? dominantItem : null);
            }

            if (dominantItem == null) {
                return;
            }

            timeDrilled += warmup * delta();

            float delay = getDrillTime(dominantItem);

            if (items.total() < itemCapacity && dominantItems > 0 && efficiency > 0) {
                float speed = Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency) * efficiency;

                lastDrillSpeed = (speed * dominantItems * warmup) / delay;
                warmup = Mathf.approachDelta(warmup, speed, warmupSpeed);
                progress += delta() * dominantItems * speed * warmup;

            } else {
                lastDrillSpeed = 0f;
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
                return;
            }

            if (dominantItems > 0 && progress >= delay && items.total() < itemCapacity) {
                offload(dominantItem);

                progress %= delay;

                drillEffect.at(x, y);
            }
        }

        @Override
        public void draw() {
            topRegion = Core.atlas.find("restored-mind-default-rim");
            itemRegion = Core.atlas.find("restored-mind-drill-middle");
            region = Core.atlas.find("restored-mind-drill-bottom");
            rotatorRegion = Core.atlas.find("restored-mind-drill-rotator");

            Draw.rect(region, x, y);
            Draw.z(Layer.blockCracks);
            drawDefaultCracks();

            Draw.z(Layer.blockAfterCracks);
            Drawf.spinSprite(rotatorRegion, x, y, timeDrilled * rotateSpeed);
            Draw.rect(topRegion, x, y);
            if (dominantItem != null) {
                Draw.color(dominantItem.color);
                Draw.rect(itemRegion, x, y);
                Draw.color();
            }
        }
    }

}
