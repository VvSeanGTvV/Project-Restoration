package classicMod.library.blocks.neoplasiaBlocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import classicMod.content.*;
import classicMod.library.ai.*;
import mindustry.Vars;
import mindustry.ai.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.Building;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.Autotiler;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.meta.*;

import java.awt.*;

import static classicMod.content.RVars.CordCanDrill;
import static classicMod.content.RVars.pathfinderCustom;
import static mindustry.Vars.itemSize;

public class CausticCord extends NeoplasmBlock implements Autotiler {
    public TextureRegion[] tiles;
    public TextureRegion[][] regions;

    public boolean source = false;


    public CausticCord(String name) {
        super(name);

        group = BlockGroup.transportation;
        hasItems = true;
        targetable = true;
        solid = false;
        unloadable = false;

        rotate = false;
        isCord = true;

        itemCapacity = 1;
        liquidCapacity = 50f;
        priority = -1.0F;
        //envEnabled = 7;
        noUpdateDisabled = false;
    }

    @Override
    public void init() {
        for (int i = 0; i < 46; i++) {
            tiles = new TextureRegion[]{Core.atlas.find(name + "-" + (i))};
        }
        super.init();
    }

    public boolean blendsArmored(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock) {
        return Point2.equals(tile.x + Geometry.d4(rotation).x, tile.y + Geometry.d4(rotation).y, otherx, othery) || !otherblock.rotatedOutput(otherx, othery) && Edges.getFacingEdge(otherblock, otherx, othery, tile) != null && Edges.getFacingEdge(otherblock, otherx, othery, tile).relativeTo(tile) == rotation || otherblock.rotatedOutput(otherx, othery) && otherblock instanceof CausticCord && Point2.equals(otherx + Geometry.d4(otherrot).x, othery + Geometry.d4(otherrot).y, tile.x, tile.y);
    }

    public TextureRegion[] icons() {
        return new TextureRegion[]{Core.atlas.find(name + "-0")};
    }

    public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock) {
        return otherblock.outputsItems() && this.blendsArmored(tile, rotation, otherx, othery, otherrot, otherblock) || this.lookingAt(tile, rotation, otherx, othery, otherblock) && otherblock.hasItems;
    }

    public class CordBuild extends NeoplasmBuilding {

        public int successScore = 0;        // how “good” this cord’s path is
        public float pheromone = 0f;        // slime-mold-like trail strength
        public float pheromoneDecay = 0.002f;
        public float pheromoneDeposit = 0.05f;

        int facingRot = 1;
        public float progress;
        public Seq<Integer> ignorePath = new Seq<>();
        public int retry = 0, growRestart = 0;

        public Seq<Tile> Queue = new Seq<>();

        @Nullable
        public Item current;
        public int blendbits;
        public int xscl;
        public int yscl;

        public int task = 0;

        @Nullable
        public CordBuild prev;

        int[] bitmask = new int[]{
                39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
                38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
                39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
                38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
                3,  4,  3,  4, 15, 40, 15, 20,  3,  4,  3,  4, 15, 40, 15, 20,
                5, 28,  5, 28, 29, 10, 29, 23,  5, 28,  5, 28, 31, 11, 31, 32,
                3,  4,  3,  4, 15, 40, 15, 20,  3,  4,  3,  4, 15, 40, 15, 20,
                2, 30,  2, 30,  9, 46,  9, 22,  2, 30,  2, 30, 14, 44, 14,  6,
                39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
                38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
                39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
                38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
                3,  0,  3,  0, 15, 42, 15, 12,  3,  0,  3,  0, 15, 42, 15, 12,
                5,  8,  5,  8, 29, 35, 29, 33,  5,  8,  5,  8, 31, 34, 31,  7,
                3,  0,  3,  0, 15, 42, 15, 12,  3,  0,  3,  0, 15, 42, 15, 12,
                2,  1,  2,  1,  9, 45,  9, 19,  2,  1,  2,  1, 14, 18, 14, 13
        };

        @Override
        public void handleItem(Building source, Item item) {
            current = item;
            super.handleItem(source, item);
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return !items.any();
        }

        @Override
        public float rotdeg() {
            return rotation * 90f;
        }

        @Override
        public void draw() {
            float rotation = rotdeg();

            Draw.z(Layer.blockUnder);
            TextureRegion region = sliced(Core.atlas.find(name + "-" + blendbits), SliceMode.none);
            drawAt(x, y, blendbits, rotation, SliceMode.none);

            Draw.color();
            Draw.reset();

            if (current != null) {
                Draw.z(Layer.blockUnder + 0.1f);
                Draw.color();
                Draw.rect(current.fullIcon, x, y, itemSize, itemSize);
            }

            Draw.color();
            Draw.reset();
        }

        boolean validBuilding(Building dest, Item item){
            if (item == null || dest == null) return false;
            return dest.acceptItem(this, item) && dest.team == this.team;
        }

        public boolean shouldFuseWith(CordBuild other) {
            if (other == null) return false;
            return other.successScore > this.successScore ||
                    other.pheromone > this.pheromone;
        }

        public boolean beneficialLoop(Tile next) {
            float phero = 0f;
            if (next.build instanceof CordBuild c) {
                phero = c.pheromone;
            }

            boolean nearTarget =
                    (task == PathfinderV2.fieldVent && next.floor().attributes.get(Attribute.steam) > 0) ||
                            (task == PathfinderV2.fieldCore && Units.findEnemyTile(team, next.x, next.y, 200f, b -> true) != null);

            return phero > 0.2f || nearTarget;
        }

        public void reinforcePath() {
            successScore += 3;
            pheromone = Mathf.clamp(pheromone + pheromoneDeposit, 0f, 10f);

            CordBuild cur = this.prev;
            int depth = 0;

            while (cur != null && depth < 20) {
                cur.successScore += 1;
                cur.pheromone = Mathf.clamp(cur.pheromone + pheromoneDeposit * 0.5f, 0f, 10f);
                cur = cur.prev;
                depth++;
            }
        }

        /** Try to avoid solid blocks and dangerous tiles by probing neighbors if needed. */
        private Tile smartNextTile(Tile from, int pathTarget){
            Tile direct = pathfind(pathTarget);
            if (direct == null) return null;

            // if direct tile is fine, use it
            if (!direct.solid() && !direct.dangerous()) return direct;

            // otherwise, probe 4 neighbors and pick the best safe one
            Tile best = null;
            int bestCost = Integer.MAX_VALUE;

            for (int i = 0; i < 4; i++){
                Tile n = from.nearby(i);
                if (n == null || n.solid()) continue;
                if (n.dangerous()) continue;

                Tile step = pathfindFrom(n, pathTarget);
                if (step == null) continue;

                int cost = (int) step.dst2(from);
                if (cost < bestCost){
                    bestCost = cost;
                    best = n;
                }
            }

            return best != null ? best : direct;
        }

        public Tile pathfind(int pathTarget) {
            int costType = Pathfinder.costNeoplasm;
            Tile tile = this.tile;
            if (tile != null) {
                Tile targetTile = pathfinderCustom.getTargetTile(tile, pathfinderCustom.getField(team, costType, pathTarget), false);
                if (tile != targetTile) {
                    return targetTile;
                }
            }
            return null;
        }

        public Tile pathfindFrom(Tile from, int pathTarget){
            int costType = Pathfinder.costNeoplasm;
            if (from != null) {
                Tile targetTile = pathfinderCustom.getTargetTile(from, pathfinderCustom.getField(team, costType, pathTarget), false);
                if (from != targetTile) {
                    return targetTile;
                }
            }
            return null;
        }

        @Override
        public void growCord(Block block) {
            retry++;
            growRestart++;

            // task escalation
            if (task <= 0) task = PathfinderV2.fieldVent;

            if (growRestart >= 3) {
                if (task == PathfinderV2.fieldVent) task = PathfinderV2.fieldOres;
                else if (task == PathfinderV2.fieldOres) task = PathfinderV2.fieldCore;
                else task = PathfinderV2.fieldVent;
                growRestart = 0;
            }

            // smarter next tile selection
            Tile next = smartNextTile(tile, task);
            if (next == null) {
                growRestart = 3;
                return;
            }

            // fusion with better cords
            for (int i = 0; i < 4; i++) {
                Tile near = next.nearby(i);
                if (near != null && near.build instanceof CordBuild other) {
                    if (shouldFuseWith(other)) {
                        this.prev = other;
                        this.task = other.task;
                        this.successScore = Math.max(this.successScore, other.successScore);
                        this.pheromone += other.pheromone * 0.5f;
                        return;
                    }
                }
            }

            // avoid loops unless beneficial
            if (next.build instanceof CordBuild && !beneficialLoop(next)) {
                growRestart++;
                return;
            }

            // place cord if possible
            if (!CantReplace(next.block())) next.setBlock(RBlocks.cord, team);

            if (next.build instanceof CordBuild cordBuild) {
                cordBuild.task = task;
                cordBuild.facingRot = tile.relativeTo(next);
                cordBuild.prev = this;

                // deposit pheromone
                this.pheromone = Mathf.clamp(this.pheromone + pheromoneDeposit, 0f, 10f);
            }

            // reinforce if valuable
            if (next.floor().attributes.get(Attribute.steam) > 0) reinforcePath();

            growRestart = 0;
            super.growCord(block);
        }

        @Override
        public void update() {
            super.update();
            pheromone = Mathf.clamp(pheromone - pheromoneDecay, 0f, 10f);

            if (back() instanceof NeoplasmBuilding neoplasmBuilding){
                if (neoplasmBuilding.reset){
                    reset = true;
                }
            }
            if (Queue.size > 0) coverQueue(pipe);

            if (growRestart >= 2){
                if (task == PathfinderV2.fieldVent) {
                    task = PathfinderV2.fieldOres;
                } else if (task == PathfinderV2.fieldOres) {
                    task = PathfinderV2.fieldCore;
                } else {
                    task = PathfinderV2.fieldVent;
                }
                growRestart = 0;
            }

            this.block.nearbySide(tile.x, tile.y, Mathf.mod(facingRot, 4), 0, Tmp.p1);
            int dx = (Geometry.d4x(facingRot) > 0) ? 1 : 0;
            int dy = (Geometry.d4y(facingRot) > 0) ? 1 : 0;
            Tile other = Vars.world.tile(Tmp.p1.x + dx, Tmp.p1.y + dy);
            if (other != null && other.solid()) {
                int spaces = calculateSpaces(drill.size, other.x, other.y);
                Item drop = other.wallDrop();
                if (drop != null && spaces >= drill.size * drill.size && CordCanDrill.contains(drop)) {
                    tile.setBlock(RBlocks.neoplasiaDrill, team);
                }
            }
        }

        public int getTotal(Item item, int size){
            int total = 0;
            for (int dy = -size; dy < size; dy++) {
                for (int dx = -size; dx < size; dx++) {
                    Tile tileOn = Vars.world.tile(tile.x + dx, tile.y + dy);
                    if (tileOn != null && tileOn.build instanceof CordBuild cordBuild){
                        total += (cordBuild.items.has(item)) ? 1 : 0;
                    }
                }
            }
            return total;
        }

        @Override
        public void updateBeat() {
            boolean cordMode = true;

            if (grow && !reset) {
                // UNIT SPAWN LOGIC (resource + local density based)
                if ((items.has(Items.beryllium) && left() == null && right() == null) ||
                        (getTotal(Items.beryllium, 3) >= 3)){

                    if (Mathf.chance(0.5f)) ReplaceTo(RBlocks.renaleSpawner);
                    else if (Mathf.chance(0.5f)) ReplaceTo(RBlocks.walkySpawner);
                    else ReplaceTo(RBlocks.oxideCrafter);
                    cordMode = false;
                }

                if ((items.has(Items.graphite) && left() == null && right() == null) ||
                        (getTotal(Items.graphite, 3) >= 3)){

                    if (Mathf.chance(0.5f)) ReplaceTo(RBlocks.muleSpawner);
                    else ReplaceTo(RBlocks.squidSpawner);
                    cordMode = false;
                }

                if ((items.has(Items.oxide) && left() == null && right() == null) ||
                        (getTotal(Items.oxide, 3) >= 3)){

                    ReplaceTo(RBlocks.hydroBomberSpawner);
                    cordMode = false;
                }

                // DEFENSE LOGIC – distance‑tiered, avoid clustering

                // bombs for very close enemies
                if (Units.closestEnemy(team, x, y, 220f, u -> u.type.killable && u.type.hittable && u.isGrounded()) != null) {
                    boolean tooClose = Units.closestBuilding(team, x, y, 60f, b -> (b.block == RBlocks.neoplasiaBomb)) != null;
                    if (!tooClose && left() == null && right() == null) {
                        ReplaceTo(RBlocks.neoplasiaBomb);
                        cordMode = false;
                    }
                }

                // long‑range pore
                if ((Units.closestEnemy(team, x, y, 640f, u -> u.type.killable && u.type.hittable) != null) ||
                        (Units.findEnemyTile(team, x, y, 640f, b -> b.isValid() && (b instanceof Turret.TurretBuild)) != null)) {

                    boolean tooClose = Units.closestBuilding(team, x, y, 240f,
                            b -> (b instanceof CausticTurret.CausticTurretBuild && b.block == RBlocks.pore)) != null;
                    if (!tooClose) {
                        ReplaceTo(RBlocks.pore);
                        cordMode = false;
                    }
                }

                // mid‑range bloom
                if ((Units.closestEnemy(team, x, y, 120f, u -> u.type.killable && u.type.hittable) != null) ||
                        (Units.findEnemyTile(team, x, y, 140f, b -> b.isValid() && (b instanceof Turret.TurretBuild)) != null)) {

                    boolean tooClose = Units.closestBuilding(team, x, y, 115f,
                            b -> (b instanceof CausticTurret.CausticTurretBuild && b.block == RBlocks.bloom)) != null;
                    if (!tooClose) {
                        ReplaceTo(RBlocks.bloom);
                        cordMode = false;
                    }
                }

                // close‑range tole
                if ((Units.closestEnemy(team, x, y, 30f, u -> u.type.killable && u.type.hittable) != null) ||
                        (Units.findEnemyTile(team, x, y, 30f, b -> b.isValid() && (b instanceof Turret.TurretBuild)) != null)) {

                    boolean tooClose = Units.closestBuilding(team, x, y, 15f,
                            b -> (b instanceof CausticTurret.CausticTurretBuild && b.block == RBlocks.tole)) != null;
                    if (!tooClose) {
                        ReplaceTo(RBlocks.tole);
                        cordMode = false;
                    }
                }

                if (cordMode) growCord(RBlocks.cord);
            }

            if (reset){
                ready = alreadyBeat = grow = false;
                beatTimer = 0f;
                reset = false;
            }

            super.updateBeat();
        }

        @Override
        public void updateAfterBeat() {
            if (current != null){
                Seq<NeoplasmBuilding> avaliable = new Seq<>();
                for (int i = 0; i < 4; i++){
                    NeoplasmBuilding dest = getNeoplasm(nearby(Mathf.mod(facingRot + i, 4)));
                    Item item = items.first();
                    if (validBuilding(dest, item)) avaliable.add(dest);
                }
                if (avaliable.size > 0) {
                    int selected = Mathf.clamp(Mathf.random(0, avaliable.size), 0, avaliable.size - 1);
                    Item item = items.first();
                    Building dest = avaliable.get(selected);
                    if (item != null && validBuilding(dest, item)) {
                        current = null;
                        items.clear();
                        dest.handleItem(this, item);
                    }
                }
            }
        }

        public void coverQueue(Block cordPlacement){
            for (var tile : Queue){
                if (tile.build == null || tile.build instanceof CausticCord.CordBuild) {
                    tile.setBlock(cordPlacement, team, 0);
                    Queue.remove(tile);
                }
            }
        }

        @Override
        public void death() {
            if (current != null) {
                current = null;
                items.clear();
            }
            super.death();
        }

        @Override
        public Building back() {
            int trns = this.block.size / 2 + 1;
            return this.nearby(Geometry.d4(this.facingRot + 2).x * trns, Geometry.d4(this.facingRot + 2).y * trns);
        }

        public Tile backTile() {
            int trns = this.block.size / 2 + 1;
            return this.nearbyXY(Geometry.d4(this.facingRot + 2).x * trns, Geometry.d4(this.facingRot + 2).y * trns);
        }

        @Override
        public Building right() {
            int trns = this.block.size / 2 + 1;
            return this.nearby(Geometry.d4(this.facingRot + 3).x * trns, Geometry.d4(this.facingRot + 3).y * trns);
        }

        @Override
        public Building left() {
            int trns = this.block.size / 2 + 1;
            return this.nearby(Geometry.d4(this.facingRot + 1).x * trns, Geometry.d4(this.facingRot + 1).y * trns);
        }

        @Override
        public Building front() {
            int trns = this.block.size / 2 + 1;
            return this.nearby(Geometry.d4(this.facingRot).x * trns, Geometry.d4(this.facingRot).y * trns);
        }

        @Override
        public void takeBlood() {
            NeoplasmBuilding behind = getNeoplasm(back());
            if (behind != null && liquids.get(blood) < liquidCapacity) {
                moveFromLiquid(behind, blood);
            }

            NeoplasmBuilding left = getNeoplasm(left());
            if (left != null && liquids.get(blood) < liquidCapacity) {
                moveFromLiquid(left, blood);
            }

            NeoplasmBuilding right = getNeoplasm(right());
            if (right != null && liquids.get(blood) < liquidCapacity) {
                moveFromLiquid(right, blood);
            }
        }

        protected void drawAt(float x, float y, int bits, float rotation, Autotiler.SliceMode slice) {
            Draw.z(Layer.blockUnder);

            TextureRegion textureRegion = (sliced(Core.atlas.find(name + "-" + bits), slice));
            float color = Draw.getColor().toFloatBits();

            float xs = (xscl > 0) ? xscl + ((beat - 1f) * 1) : xscl - ((beat - 1f) * 1);
            float ys = (yscl > 0) ? yscl + ((beat - 1f) * 1) : yscl - ((beat - 1f) * 1);
            float w = textureRegion.width * textureRegion.scl() * xs;
            float h = textureRegion.height * textureRegion.scl() * -ys;
            float u = textureRegion.u, u2 = textureRegion.u2;
            float v = textureRegion.v, v2 = textureRegion.v2;
            Draw.color(new Color(1.0F, 1.0F, 1.0F, 1.0F).lerp(beatColor, (beat - 1)));
            float x0 = x - w / 2f;
            float y0 = y - h / 2f;

            boolean flipY = !(facingRot == 1);
            boolean flipX = !(facingRot == 3);

            float stretchFactor = 6f;
            float stretchFront = (((front() instanceof NeoplasmBuilding neo) ? (neo.beat - 1) : 0f)) * stretchFactor;
            float stretchBack = (((back() instanceof NeoplasmBuilding neo) ? (neo.beat - 1) : 0f)) * stretchFactor;
            float stretchLeft = (((left() instanceof NeoplasmBuilding neo) ? (neo.beat - 1) : 0f)) * stretchFactor;
            float stretchRight = (((right() instanceof NeoplasmBuilding neo) ? (neo.beat - 1) : 0f)) * stretchFactor;

            float[] vertices = {
                    x0 - ((left() instanceof NeoplasmBuilding && !flipX) ? stretchLeft : (right() instanceof NeoplasmBuilding && flipX) ? stretchRight : 0f),
                    y0 + ((back() instanceof NeoplasmBuilding && !flipY) ? stretchBack : (front() instanceof NeoplasmBuilding && flipY) ? stretchFront : 0f),
                    color, u, v, 0f,

                    x0 + w + ((left() instanceof NeoplasmBuilding && !flipX) ? stretchLeft : (right() instanceof NeoplasmBuilding && flipX) ? stretchRight : 0f),
                    y0 + ((front() instanceof NeoplasmBuilding && !flipY) ? stretchFront : (back() instanceof NeoplasmBuilding && flipY) ? stretchBack : 0f),
                    color, u2, v, 0f,

                    x0 + w + ((right() instanceof NeoplasmBuilding && !flipX) ? stretchRight : (left() instanceof NeoplasmBuilding && flipX) ? stretchLeft : 0f),
                    y0 + h - ((front() instanceof NeoplasmBuilding && !flipY) ? stretchFront : ((back() instanceof NeoplasmBuilding && flipY) ? stretchBack : 0f)),
                    color, u2, v2, 0f,

                    x0 - ((right() instanceof NeoplasmBuilding && !flipX) ? stretchRight : (left() instanceof NeoplasmBuilding && flipX) ? stretchLeft : 0f),
                    y0 + h - ((back() instanceof NeoplasmBuilding && !flipY) ? stretchBack : (front() instanceof NeoplasmBuilding && flipY) ? stretchFront : 0f),
                    color, u, v2, 0f
            };
            Draw.vert((sliced(Core.atlas.find(name + "-" + bits), slice)).texture, vertices, 0, vertices.length);

            Draw.color();
            Draw.scl();
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();

            int bit = 0;
            for (int i = 0; i < 8; i++){
                Tile neighborTile = Vars.world.tile(tile.x + Geometry.d8(i).x, tile.y + Geometry.d8(i).y);
                if (neighborTile != null && neighborTile.build instanceof NeoplasmBuilding neoplasmBuilding) {
                    bit |= 1 << i;
                    neoplasmBuilding.ready = neoplasmBuilding.alreadyBeat = neoplasmBuilding.grow = false;
                    neoplasmBuilding.reset = true;
                    neoplasmBuilding.beatTimer = 0f;
                }
            }

            blendbits = bitmask[bit];
            xscl = 1;
            yscl = 1;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(facingRot);
            write.i(task);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            facingRot = read.i();
            task = read.i();
        }
    }
}
