package classicMod.library.blocks.neoplasiaBlocks;

import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.Log;
import classicMod.content.*;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.Puddles;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.*;
import mindustry.world.blocks.Attributes;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.Attribute;

public class NeoplasiaBlock extends Block {

    public boolean source = false;
    public Color beatColor = Color.valueOf("cd6240");

    public boolean isCord = false;

    public NeoplasiaBlock(String name) {
        super(name);

        update = true;
        drawTeamOverlay = false;
        destroySound = breakSound = RSounds.splat;
        hasLiquids = true;
        liquidCapacity = 100f;
    }

    public class NeoplasiaBuilding extends Building {

        Liquid blood = Liquids.neoplasm;
        public Seq<Tile> proximityTiles = new Seq<>();

        boolean startBuild = true, initalize = false;
        float beat = 1, beatTimer = 0, priority = 0, deathTimer = 0;
        boolean ready = false, alreadyBeat = false, grow = false;

        @Override
        public void draw() {
            drawBeat(1, 1);
            super.draw();
        }

        public boolean isSource(){
            return source;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return liquid == blood;
        }

        public void drawBeat(float xscl, float yscl){
            drawBeat(xscl, yscl, 1);
        }

        public void drawBeat(float xscl, float yscl, float offsetSclBeat){
            float xs = (xscl > 0) ? xscl + ((beat - 1f) * offsetSclBeat) : xscl - ((beat - 1f) * offsetSclBeat);
            float ys = (yscl > 0) ? yscl + ((beat - 1f) * offsetSclBeat) : yscl - ((beat - 1f) * offsetSclBeat);
            Draw.scl(xs, ys);
            Draw.color(new Color(1.0F, 1.0F, 1.0F, 1.0F).lerp(beatColor, (beat - 1)));
        }

        public boolean isNeoplasia(Building building){
            if (building == null) return false;
            return building instanceof NeoplasiaBuilding;
        }

        public NeoplasiaBuilding getNeoplasia(Building building){
            if (building instanceof NeoplasiaBuilding cordBuild){
                return cordBuild;
            } else {
                return null;
            }
        }


        public Tile nearbyTile(int rotation, int offsetTrns) {
            Tile var10000;
            int trns = block.size / 2 + 1 + offsetTrns;
            int dx = Geometry.d4(rotation).x * trns, dy = Geometry.d4(rotation).y * trns;
            var10000 = Vars.world.tile(tile.x + dx, tile.y + dy);

            return var10000;
        }

        public Tile nearbyTile(int rotation) {
            return nearbyTile(rotation, 0);
        }

        public boolean passable(Block block){
            boolean acceptable = true;
            if (block == null) return false;

            if (block instanceof Floor floor){
                if (floor.liquidDrop != null){
                    Log.info(floor.liquidDrop);
                    return false;
                }
            }

            return !(
                            block instanceof StaticWall ||
                            block == ClassicBlocks.cord
                    )
                    && (
                            block == Blocks.air
                            || block instanceof SteamVent
                            || block instanceof Prop
                            || block instanceof NeoplasiaBlock
                    )
            ;
        }

        public boolean CantReplace(Block block){
            return
                    block instanceof NeoplasiaBlock
                    ;
        }

        public void coverVent(Block replacmentBlock, Block cordPlacement){
            float steam = 0;
            if (this.tile.floor().attributes.get(Attribute.steam) >= 1) {
                for (int dy = -1; dy < 2; dy++) {
                    for (int dx = -1; dx < 2; dx++) {
                        Tile tile = Vars.world.tile(this.tile.x + dx, this.tile.y + dy);
                        if (tile.floor() != null && (tile.build == null || tile.build instanceof Cord.CordBuild)) {
                            steam += tile.floor().attributes.get(Attribute.steam);
                            if (tile.floor().attributes.get(Attribute.steam) >= 1) {
                                if (tile.build == null) tile.setBlock(cordPlacement, team, rotation);
                            }
                        }
                    }
                }
            }
            if (steam >= 9f){
                Tile replacement = Vars.world.tile(this.tile.x, this.tile.y);
                replacement.setBlock(replacmentBlock, team, rotation);
            }
        }

        public void bloomTurret(){
            float spaceAvaliable = 0;
            for (int dy = -1; dy < 2; dy++) {
                for (int dx = -1; dx < 2; dx++) {
                    Tile tile = Vars.world.tile(this.tile.x + dx, this.tile.y + dy);
                    if (tile.floor() != null && (tile.build == null || tile.build instanceof Cord.CordBuild)) {
                        spaceAvaliable += 1;
                    }
                }
            }
            if (spaceAvaliable >= 9) {
                Tile replacement = Vars.world.tile(this.tile.x, this.tile.y);
                replacement.setBlock(ClassicBlocks.bloom, team, rotation);
            }
        }

        public void coverOre(Block replacmentBlock, Block cordPlacement) {
            float ore = 0;
            if (tile.drop() != null) {
                for (int dy = 0; dy < 2; dy++) {
                    for (int dx = 0; dx < 2; dx++) {
                        Tile tile = Vars.world.tile(this.tile.x + dx, this.tile.y + dy);
                        if (tile.floor() != null && (tile.build == null || tile.build instanceof Cord.CordBuild)) {
                            ore += (tile.drop() != null) ? 1 : 0;
                            if (tile.floor().attributes.get(Attribute.steam) >= 1) {
                                if (tile.build == null) tile.setBlock(cordPlacement, team, rotation);
                            }
                        }
                    }
                }
            }
            if (ore >= 4f) {
                Tile replacement = Vars.world.tile(this.tile.x, this.tile.y);
                replacement.setBlock(replacmentBlock, team, rotation);
            }
        }

        public void growCord(Block block){
            grow = false;
        }

        @Override
        public void updateProximity() {
            proximityTiles.clear();
            Point2[] nearby = Edges.getEdges(size);
            for (Point2 point : nearby) {
                Tile other = Vars.world.tile(this.tile.x + point.x, this.tile.y + point.y);
                if (other != null) {
                    proximityTiles.add(other);
                }
            }

            super.updateProximity();
        }

        public void updateBeat(){

        }

        public boolean deathImminent(){
            return (liquids.get(blood) <= 1f);
        }

        public void Death(){
            Events.fire(new EventType.BlockDestroyEvent(this.tile));
            Liquid neoplasm = blood;
            float leakAmount = liquids.get(neoplasm);
            Puddles.deposit(this.tile, this.tile, neoplasm, liquids.get(neoplasm), true, true);
            liquids.remove(neoplasm, leakAmount);
            destroySound.at(this);

            if (this.tile != Vars.emptyTile) {
                this.tile.remove();
            }

            this.remove();
            this.afterDestroyed();
        }

        @Override
        public void killed() {
            Death();
        }

        @Override
        public void update() {
            if (!source) {
                NeoplasiaBuilding behind = getNeoplasia(back());
                if (behind != null && liquids.get(blood) < liquidCapacity) {
                    float amount = Math.min(liquidCapacity, behind.liquids.get(blood));
                    liquids.add(blood, amount);
                    behind.liquids.remove(blood, amount);
                }
            }
            if (!startBuild) {
                if (source) {
                    if (liquids.get(blood) < liquidCapacity) liquids.add(blood, Math.min(liquidCapacity - liquids.get(blood), liquidCapacity));
                    priority = 0;
                    beatTimer += delta();
                    if (beatTimer >= 25) {
                        beat = 1.5f;
                        beatTimer = 0;
                        updateBeat();
                        growCord(ClassicBlocks.cord);
                    }
                }

                if (deathImminent()) deathTimer += delta();
                else deathTimer = 0;
                if (deathTimer >= 5) Death();

                for(int i = 0; i <proximity.size; ++i) {
                    Building other = proximity.get((i) % proximity.size);
                    if (other instanceof NeoplasiaBuilding neoplasiaBuilding) {
                        if (neoplasiaBuilding.beat >= 1.2f && !source && !alreadyBeat && !neoplasiaBuilding.deathImminent()) {
                            ready = true;
                            grow = true;
                        }
                    }
                }

                if (ready && !alreadyBeat) {
                    if (beatTimer >= 2) {
                        if (priority > 0) priority -= 1;
                        updateBeat();
                        beatTimer = 0;
                        ready = false;
                        alreadyBeat = true;
                        beat = 1.5f;
                    }
                }

                if (alreadyBeat) {
                    if (beatTimer >= 20) {
                        alreadyBeat = false;
                        beatTimer = 0;
                    }
                }
                if (ready || alreadyBeat && !source) beatTimer += delta();


                if (beat > 1.05f) {
                    beat = Mathf.lerpDelta(beat, 1f, 0.15f);
                } else {
                    if (beat > 1) beat = 1;
                }

            } else {
                if ((this.tile.floor().attributes.get(Attribute.steam) >= 1 || tile.drop() != null) && this instanceof Cord.CordBuild) {
                    if (isCord && tile.drop() != null) coverOre(ClassicBlocks.neoplasiaDrill, ClassicBlocks.cord);
                    if (isCord && this.tile.floor().attributes.get(Attribute.steam) >= 1) coverVent(ClassicBlocks.heart, ClassicBlocks.cord);
                }
                if (!initalize) {
                    beat = (float) -block.size / (block.size + 1.25f);
                    initalize = true;
                } else {
                    beat = Mathf.lerpDelta(beat, 1f, 0.1f);
                    if (beat >= 0.95f) {
                        beat = 1f;
                        startBuild = false;
                    }
                }
            }
        }
    }
}
