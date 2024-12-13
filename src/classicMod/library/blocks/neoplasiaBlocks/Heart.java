package classicMod.library.blocks.neoplasiaBlocks;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.Nullable;
import classicMod.content.RUnitTypes;
import classicMod.library.ai.PathfinderExtended;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.Attribute;

public class Heart extends NeoplasmBlock {

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

    public class HeartBuilding extends NeoplasmBuilding {

        public UnitType unitType = RUnitTypes.blob;
        public @Nullable Unit unit;

        @Override
        public void growCord(Block block) {
            int randRot = Mathf.range(4);
            Tile tile = nearbyTile(randRot);
            if (tile != null) {
                if (tile.build == null) {
                    tile.setBlock(block, team);
                    if (tile.build != null && tile.build instanceof Cord.CordBuild cordBuild) {
                        cordBuild.facingRot = randRot;
                    }
                }
            }
            super.growCord(block);
        }

        @Nullable
        public Tile getClosestVent() {
            Seq<Tile> avaliableVents = PathfinderExtended.SteamVents;
            Tile vent = Geometry.findClosest(this.x, this.y, avaliableVents.removeAll(tile -> tile.build != null));
            return (vent != null && vent.build == null) ? vent : null;
        }

        @Override
        public void updateBeat() {

            if (Mathf.chance(0.25) && getClosestVent() != null){
                if (!Vars.net.client()) {
                    unit = unitType.create(team);
                    if (unit instanceof BuildingTetherc bt) {
                        bt.building(this);
                    }
                    unit.set(this);
                    unit.rotation(90f);
                    unit.add();
                    //unit.vel.y = launchVelocity;
                    for (int i = 0; i < 5; i++) {
                        Fx.ventSteam.at(this.x + Mathf.random(1), this.y + Mathf.random(1), blood.color);
                    }
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
