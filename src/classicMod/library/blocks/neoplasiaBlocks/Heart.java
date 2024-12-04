package classicMod.library.blocks.neoplasiaBlocks;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.Nullable;
import classicMod.content.RUnitTypes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.*;
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

        public UnitType unitType = RUnitTypes.blob;
        public @Nullable Unit unit;

        @Override
        public void updateBeat() {
            if (Mathf.chance(0.5)){
                if (!Vars.net.client()) {
                    unit = unitType.create(team);
                    if (unit instanceof BuildingTetherc bt) {
                        bt.building(this);
                    }
                    unit.set(this);
                    unit.rotation(90f);
                    unit.add();
                    //unit.vel.y = launchVelocity;
                    //Fx.producesmoke.at(this);
                    //Effect.shake(4f*1.5f, 5f, this);
                    //units.add(unit);
                }
            }

            super.updateBeat();
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return true;
        }

        @Override
        public void draw() {
            drawBeat(1f, 1f, 0.25f);
            Draw.rect(Core.atlas.find(name), x, y);
            Draw.color();

            Draw.reset();
        }
    }
}
