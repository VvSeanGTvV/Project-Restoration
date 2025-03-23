package classicMod.library.blocks.neoplasiaBlocks;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.*;
import arc.util.Nullable;
import classicMod.content.RUnitTypes;
import classicMod.library.ai.PathfinderExtended;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;

public class CausticHeart extends NeoplasmBlock {

    public float minEfficiency;
    public Attribute attribute;
    public int unitCap;
    public CausticHeart(String name) {
        super(name);
        attribute = Attribute.heat;
        hasItems = true;

        liquidCapacity = 1000f;
        priority = 2.0F;
        flags = EnumSet.of(BlockFlag.core);
        unitCapModifier = 10;
        itemCapacity = 10;
        unitCap = 50;
    }

    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return tile.getLinkedTilesAs(this, tempTiles).sumf((other) -> {
            return other.floor().attributes.get(attribute);
        }) > minEfficiency;
    }

    public class HeartBuilding extends NeoplasmBuilding {

        public boolean inDanger;
        public UnitType unitType = RUnitTypes.blob;
        public @Nullable Unit unit;

        @Nullable
        public Tile getClosestVent() {
            Seq<Tile> avaliableVents = PathfinderExtended.SteamVents.copy().removeAll(tile -> tile.build instanceof CausticHeart.HeartBuilding);
            Tile vent = Geometry.findClosest(this.x, this.y, avaliableVents);
            return (vent != null && !(vent.build instanceof CausticHeart.HeartBuilding)) ? vent : null;
        }

        public boolean canCreate(Team team, UnitType type) {
            return team.data().countType(type) < unitCap && !type.isBanned();
        }

        @Override
        public void updateBeat() {

            if (Mathf.chance(0.25) && getClosestVent() != null){
                if (!Vars.net.client() && canCreate(team, unitType)) {
                    unit = unitType.create(team);
                    unit.set(this);
                    unit.rotation(90f);
                    unit.add();
                    //unit.vel.y = launchVelocity;
                    for (int i = 0; i < 5; i++) {
                        Fx.neoplasiaSmoke.at(this.x + Mathf.random(1), this.y + Mathf.random(1));
                    }
                    //Effect.shake(4f*1.5f, 5f, this);
                    //units.add(unit);
                }
            }

            super.updateBeat();
        }

        @Override
        public void update() {
            if (liquids.get(blood) < liquidCapacity) liquids.add(blood, Math.min(liquidCapacity - liquids.get(blood), liquidCapacity));
            super.update();
            inDanger = (health < maxHealth);
            if (grown){
                if (source) {
                    priority = 0;
                    beatTimer += delta();
                    if (beatTimer >= 25 || (beatTimer >= 15 && inDanger)) {
                        beat = 1.5f;
                        beatTimer = 0;
                        updateBeat();
                        growCord(pipe);
                    }
                }
            }
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
