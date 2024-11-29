package classicMod.library.blocks.neoplasiaBlocks;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.util.Nullable;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.type.*;
import mindustry.world.Tile;
import mindustry.world.meta.Attribute;

public class Heart extends NeoplasiaBlock {

    public Effect generateEffect;
    public float effectChance;
    public float minEfficiency;
    public float displayEfficiencyScale;
    public boolean displayEfficiency;
    @Nullable
    public LiquidStack outputLiquid;
    public Attribute attribute;
    public Heart(String name) {
        super(name);
        attribute = Attribute.heat;
        hasItems = true;
    }

    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return tile.getLinkedTilesAs(this, tempTiles).sumf((other) -> {
            return other.floor().attributes.get(attribute);
        }) > minEfficiency;
    }

    public class HeartBuilding extends NeoplasiaBuilding {

        @Override
        public boolean acceptItem(Building source, Item item) {
            return true;
        }

        @Override
        public void draw() {
            drawBeat(1.225f, 1.225f, 0.25f);
            Draw.rect(Core.atlas.find(name), x, y);
            Draw.color();

            Draw.reset();
        }
    }
}
