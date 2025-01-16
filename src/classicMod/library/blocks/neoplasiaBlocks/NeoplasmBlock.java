package classicMod.library.blocks.neoplasiaBlocks;

import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.*;
import classicMod.content.*;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.Puddles;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.Attribute;

public class NeoplasmBlock extends Block {

    public boolean source = false;
    public Color beatColor = Color.valueOf("cd6240");

    public boolean isCord = false;

    public NeoplasmBlock(String name) {
        super(name);

        update = true;
        drawTeamOverlay = false;
        destroySound = breakSound = RSounds.splat;
        hasLiquids = true;
        liquidCapacity = 100f;
        liquidPressure = 10f;
    }

    public class NeoplasmBuilding extends Building {
        Block pipe = ClassicBlocks.cord;
        Block core = ClassicBlocks.heart;
        Block drill = ClassicBlocks.neoplasiaDrill;

        float drain = 0.25f;


        public float previousBeat = 0f;

        Liquid blood = Liquids.neoplasm;
        public Seq<Tile> proximityTiles = new Seq<>();

        boolean grown = false, initalized = false;
        float beat = 1, beatTimer = 0, priority = 0, deathTimer = 0, timer = 0;
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
            return building instanceof NeoplasmBuilding;
        }

        public NeoplasmBuilding getNeoplasia(Building building){
            if (building instanceof NeoplasmBuilding cordBuild){
                return cordBuild;
            } else {
                return null;
            }
        }


        public Tile nearbyXY(int dx, int dy) {
            return Vars.world.tile(this.tile.x + dx, this.tile.y + dy);
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

        public boolean passable(Tile tile, boolean checkWall){
            if (tile == null) return false;

            Block block = tile.block();
            if (block == null) return false;
            if (tile.floor() != null && tile.floor().liquidDrop != null) return false;

            return !(
                            (block instanceof StaticWall staticWall && checkWall && block.itemDrop == null) ||
                            block == pipe
                    )
                    && (
                            block == Blocks.air
                            || block instanceof SteamVent
                            || block instanceof Prop
                            || block instanceof NeoplasmBlock
                    )
            ;
        }

        public boolean CantReplace(Block block){
            return
                    block instanceof NeoplasmBlock
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

        public int calculateSpaces(int size, short tileX, short tileY){
            int spaceAvaliable = 0;
            for (int dy = (2 - size); dy < 2; dy++) {
                for (int dx = (2 - size); dx < 2; dx++) {
                    Tile tile = Vars.world.tile(tileX + dx, tileY + dy);
                    if (tile.floor() != null && (tile.build == null || tile.build instanceof Cord.CordBuild)) {
                        spaceAvaliable += 1;
                    }
                }
            }
            Log.info(spaceAvaliable);
            return spaceAvaliable;
        }

        int sizeRounded(int size){
            float dSize = (float) size - 2f;
            if (size % 2 == 1) return (int) (dSize - 0.5f);
            else return (int) dSize;
        }


        /*fun checkValid(x: Int, y: Int, replace: Boolean): Boolean {
            if (!replace) {
                return (x - 1..x + 1).all { x1 ->
                        (y - 1..y + 1).all { y1 ->
                        val tile = Vars.world.tile(x1, y1)
                    tile != null && tile.block() == Blocks.air
                }
                }
            } else {
                return (x - 1..x + 1).all { x1 ->
                        (y - 1..y + 1).all { y1 ->
                        val tile = Vars.world.tile(x1, y1)
                    tile != null &&
                            (tile.block() == Blocks.air ||
                                    tile.block().isSerpuloDistribution ||
                                    tile.block().isErekirDistribution)
                }
                }
            } TODO convert kotlin by Jason01#6845 (.json (ping rely on))

            for (dx = (x-size); dx < (x+size); dx++)
        }*/

        public void ReplaceTo(Block toBlock){
            float spaceAvaliable = 0;
            for (int dy = -sizeRounded(toBlock.size); dy < sizeRounded(toBlock.size) - ((toBlock.size % 2 == 0) ? 1 : 0); dy++) {
                for (int dx = -sizeRounded(toBlock.size); dx < sizeRounded(toBlock.size) - ((toBlock.size % 2 == 0) ? 1 : 0); dx++) {
                    Tile tile = Vars.world.tile(this.tile.x + dx, this.tile.y + dy);
                    if (tile != null) {
                        if (tile.floor() != null && tile.block() != null && (tile.block() == Blocks.air || tile.block() == ClassicBlocks.cord) && (tile.build == null || tile.build instanceof Cord.CordBuild)) {
                            spaceAvaliable += 1;
                        }
                    }
                }
            }
            Log.info(spaceAvaliable);
            if (spaceAvaliable >= toBlock.size * toBlock.size) {
                Tile replacement = Vars.world.tile(this.tile.x, this.tile.y);
                replacement.setBlock(toBlock, team, rotation);
            }
        }

        public void coverOre(Block replacmentBlock) {
            float ore = 0;
            if (tile.drop() != null) {
                for (int dy = 0; dy < 2; dy++) {
                    for (int dx = 0; dx < 2; dx++) {
                        Tile tile = Vars.world.tile(this.tile.x + dx, this.tile.y + dy);
                        if (tile.floor() != null && (tile.build == null || tile.build instanceof Cord.CordBuild)) {
                            ore += (tile.drop() != null) ? 1 : 0;
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
            if (!isCord){
                int rand = Mathf.random(0, proximityTiles.size);
                Tile tile = proximityTiles.get(rand % proximityTiles.size);
                if (tile != null) {
                    if (tile.build == null && passable(tile, true)) {
                        tile.setBlock(block, team);
                        if (tile.build != null && tile.build instanceof Cord.CordBuild cordBuild) {
                            cordBuild.facingRot = cordBuild.relativeTo(this.tile);
                        }
                    }
                }
            }
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
            previousBeat = 0f;
        }

        public void updateAfterBeat(){

        }

        public boolean deathImminent(){
            return previousBeat >= 600f;
        }

        public void death(){
            Fx.neoplasiaSmoke.at(this.x + Mathf.random(1), this.y + Mathf.random(1));
            Events.fire(new EventType.BlockDestroyEvent(this.tile));

            if (this.block.hasLiquids && Vars.state.rules.damageExplosions) {
                this.liquids.each((liquid, amountx) -> {
                    float splash = Mathf.clamp(liquidCapacity / 4.0F, 0.0F, 10.0F);

                    for(int i = 0; (float)i < Mathf.clamp(amountx / 5.0F, 0.0F, 30.0F); ++i) {
                        Time.run((float)i / 2.0F, () -> {
                            Tile other = Vars.world.tileWorld(this.x + (float)Mathf.range(this.block.size * 8 / 2), this.y + (float)Mathf.range(this.block.size * 8 / 2));
                            if (other != null) {
                                Puddles.deposit(other, liquid, splash);
                            }
                        });
                    }

                });
            }

            destroySound.at(this);

            if (this.tile != Vars.emptyTile) {
                this.tile.remove();
            }

            this.remove();
            this.afterDestroyed();
        }

        @Override
        public void killed() {
            death();
            super.killed();
        }

        public float moveFromLiquid(Building from, Liquid liquid) {
            Building next = this;
            if (from == null) {
                return 0.0F;
            } else {
                next = next.getLiquidDestination(from, liquid);
                if (next.team == from.team && next.block.hasLiquids && from.liquids.get(liquid) > 0.1F) {
                    float ofract = next.liquids.get(liquid) / next.block.liquidCapacity;
                    float fract = from.liquids.get(liquid) / from.block.liquidCapacity * from.block.liquidPressure;
                    float flow = Math.min(Mathf.clamp(fract - ofract) * from.block.liquidCapacity, from.liquids.get(liquid));
                    flow = Math.min(flow, next.block.liquidCapacity - next.liquids.get(liquid));
                    if (flow > 0.0F && ofract <= fract && next.acceptLiquid(from, liquid)) {
                        next.handleLiquid(from, liquid, flow);
                        from.liquids.remove(liquid, flow);
                        return flow;
                    }

                    if (!next.block.consumesLiquid(liquid) && next.liquids.currentAmount() / next.block.liquidCapacity > 0.1F && fract > 0.1F) {
                        float fx = (from.x + next.x) / 2.0F;
                        float fy = (from.y + next.y) / 2.0F;
                        Liquid other = next.liquids.current();
                        if (other.blockReactive && liquid.blockReactive) {
                            if ((!(other.flammability > 0.3F) || !(liquid.temperature > 0.7F)) && (!(liquid.flammability > 0.3F) || !(other.temperature > 0.7F))) {
                                if (liquid.temperature > 0.7F && other.temperature < 0.55F || other.temperature > 0.7F && liquid.temperature < 0.55F) {
                                    this.liquids.remove(liquid, Math.min(from.liquids.get(liquid), 0.7F * Time.delta));
                                    if (Mathf.chanceDelta(0.20000000298023224)) {
                                        Fx.steam.at(fx, fy);
                                    }
                                }
                            } else {
                                from.damageContinuous(1.0F);
                                next.damageContinuous(1.0F);
                                if (Mathf.chanceDelta(0.1)) {
                                    Fx.fire.at(fx, fy);
                                }
                            }
                        }
                    }
                }
                return 0.0F;
            }
        }

        public void takeBlood(){
            for(int i = 0; i <proximity.size; ++i) {
                Building other = proximity.get((i) % proximity.size);
                if (other instanceof NeoplasmBuilding neoplasmBuilding) {
                    if (!source && !neoplasmBuilding.deathImminent() && liquids.get(blood) < liquidCapacity) {
                        float amount = Math.min(liquidCapacity, neoplasmBuilding.liquids.get(blood));
                        neoplasmBuilding.liquids.remove(blood, amount);
                        liquids.add(blood, amount);
                    }
                }
            }
        }

        public boolean isGrown(){
            return grown;
        }

        @Override
        public void update() {

            takeBlood();
            timer += delta();
            previousBeat += delta();
            if (timer >= 10f) {
                timer = 0;
                //liquids.remove(blood, drain); //TODO something
            }

            if (!source){
                liquidPressure -= delta() / 100;
                for(int i = 0; i <proximity.size; ++i) {
                    Building other = proximity.get((i) % proximity.size);
                    if (other instanceof NeoplasmBuilding) {
                        liquidPressure = other.block.liquidPressure;
                    }
                }
            }

            if (grown) {


                if (deathImminent()) deathTimer += delta();
                else deathTimer = 0;
                if (deathTimer >= 5) death();

                for(int i = 0; i <proximity.size; ++i) {
                    Building other = proximity.get((i) % proximity.size);
                    if (other instanceof NeoplasmBuilding neoplasmBuilding) {
                        if (neoplasmBuilding.beat >= 1.2f && !source && !alreadyBeat && !neoplasmBuilding.deathImminent()) {
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
                    if (beatTimer >= 10) {
                        alreadyBeat = false;
                        beatTimer = 0;
                    }
                }
                if (ready || alreadyBeat && !source) beatTimer += delta();


                if (beat > 1.05f) {
                    beat = Mathf.lerpDelta(beat, 1f, 0.25f);
                } else {
                    if (beat > 1) {
                        updateAfterBeat();
                        beat = 1;
                    }
                }

            } else {
                if ((this.tile.floor().attributes.get(Attribute.steam) >= 1 || tile.drop() != null) && this instanceof Cord.CordBuild) {
                    if (isCord && tile.drop() != null) coverOre(drill);
                    if (isCord && this.tile.floor().attributes.get(Attribute.steam) >= 1) coverVent(core, pipe);
                }
                if (!initalized) {
                    beat = (float) -block.size / (block.size + 1.25f);
                    initalized = true;
                } else {
                    beat = Mathf.lerpDelta(beat, 1f, 0.1f);
                    if (beat >= 0.95f) {
                        beat = 1f;
                        grown = true;
                    }
                }
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.bool(grown);
            write.bool(initalized);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            grown = read.bool();
            initalized = read.bool();
        }
    }
}
