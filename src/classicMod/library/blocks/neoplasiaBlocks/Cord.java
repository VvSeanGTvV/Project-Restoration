package classicMod.library.blocks.neoplasiaBlocks;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.*;
import classicMod.AutotilerPlus;
import classicMod.content.ClassicBlocks;
import classicMod.library.ai.PathfinderExtended;
import mindustry.Vars;
import mindustry.ai.*;
import mindustry.content.Items;
import mindustry.entities.*;
import mindustry.gen.Building;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.Autotiler;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.itemSize;

public class Cord extends NeoplasmBlock implements AutotilerPlus {
    public TextureRegion[] tiles;
    public TextureRegion[][] regions;

    public boolean source = false;


    public Cord(String name) {
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
        return Point2.equals(tile.x + Geometry.d4(rotation).x, tile.y + Geometry.d4(rotation).y, otherx, othery) || !otherblock.rotatedOutput(otherx, othery) && Edges.getFacingEdge(otherblock, otherx, othery, tile) != null && Edges.getFacingEdge(otherblock, otherx, othery, tile).relativeTo(tile) == rotation || otherblock.rotatedOutput(otherx, othery) && otherblock instanceof Cord && Point2.equals(otherx + Geometry.d4(otherrot).x, othery + Geometry.d4(otherrot).y, tile.x, tile.y);
    }

    public TextureRegion[] icons() {
        return new TextureRegion[]{Core.atlas.find(name + "-0")};
    }

    public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock) {
        return otherblock.outputsItems() && this.blendsArmored(tile, rotation, otherx, othery, otherrot, otherblock) || this.lookingAt(tile, rotation, otherx, othery, otherblock) && otherblock.hasItems;
    }

    public class CordBuild extends NeoplasmBuilding {

        //TODO make it work YIPPE
        int facingRot = 1;
        public float progress;
        public Seq<Integer> ignorePath = new Seq<>();
        public int retry = 0;

        boolean useful;

        @Nullable
        public Item current;
        public int recDir = 0;
        public int blendbits;
        public int xscl;
        public int yscl;
        public int blending;

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
            //handleItem(source, item);
            return !items.any();
        }

        @Override
        public float rotdeg() {
            return (float)(this.rotation * 90);
        }

        @Override
        public void draw() {
            float rotation = this.rotdeg();

            Draw.z(Layer.blockUnder);
            if (current != null){
                Draw.z(Layer.blockUnder + 0.1f);
                Draw.color();
                Draw.scl();
                Draw.rect(current.fullIcon, x, y, itemSize, itemSize);
            }

            drawAt(x, y, blendbits, rotation, SliceMode.none);
            Draw.color();

            Draw.reset();
        }

        boolean validBuilding(Building dest, Item item){
            if (item == null || dest == null) return false;
            return dest.acceptItem(this, item) && dest.team == this.team;
        }

        @Override
        public void growCord(Block block) {

            retry++;
            for (int a = 0; a < 4; a++) {
                Tile man = nearbyTile(Mathf.mod(facingRot + a, 4));
                if (man != null && backTile() != null && backTile() != man){
                    if (man.build instanceof NeoplasmBuilding) {
                        retry = 0;
                        break;
                    }
                }
            }

            /*boolean keepDir = Mathf.randomBoolean(0.98f);
            int i = Mathf.random(1, 4),
                    rot = (keepDir && !ignorePath.contains(facingRot)) ? facingRot : Mathf.mod(facingRot + i, 4);
            Tile near = nearbyTile(rot);
            Tile nearRight = near.nearby(Mathf.mod(rot + 1, 4));
            Tile nearLeft = near.nearby(Mathf.mod(rot - 1, 4));
            //Tile nearFront = near.nearby(rot);
            if (
                    passable(near)
                    && passable(nearRight)
                    && passable(nearLeft)
                    && !ignorePath.contains(rot)
                            //&& passable(nearFront.block())
            ){
                if (!CantReplace(near.block())) near.setBlock(ClassicBlocks.cord, team);
                if (near.build != null && near.build instanceof CordBuild cordBuild) {
                    cordBuild.facingRot = rot;
                    cordBuild.prev = this;
                }
            }*/
            //boolean VentTask = Mathf.randomBoolean(0.98f);
            task = (task != 0) ? task : PathfinderExtended.fieldVent;
            Tile next = pathfind(task); //VentTask) ? pathfind(PathfinderExtended.fieldVent) : pathfind(PathfinderExtended.fieldOres);
            if (
                    passable(next, true)
                    && !ignorePath.contains(facingRot)
                //&& passable(nearFront.block())
            ){
                Seq<Tile> nearTiles = new Seq<>(4);
                for (var d : Geometry.d4) {
                    Tile dTile = Vars.world.tile(next.x + d.x, next.y + d.y);
                    int rot = this.tile.relativeTo(dTile);
                    Tile nearRight = dTile.nearby(Mathf.mod(rot + 1, 4));
                    Tile nearLeft = dTile.nearby(Mathf.mod(rot - 1, 4));
                    Tile nearFront = dTile.nearby(rot);
                    if (
                            passable(dTile, true)
                            && passable(nearRight, false)
                            && passable(nearLeft, false)
                            && passable(nearFront, true)
                            //&& !ignorePath.contains(facingRot)
                            && dTile.relativeTo(this.tile) != -1
                        //&& passable(nearFront.block())
                    ) {
                        nearTiles.add(dTile);
                    }
                }
                if (nearTiles.size <= 0 && next.relativeTo(this.tile) != -1){
                    int rot = this.tile.relativeTo(next);
                    Tile nearRight = next.nearby(Mathf.mod(rot + 1, 4));
                    Tile nearLeft = next.nearby(Mathf.mod(rot - 1, 4));
                    //Tile nearFront = next.nearby(rot);
                    if (
                            passable(next, true)
                                    && passable(nearRight, false)
                                    && passable(nearLeft, false)
                                    //&& passable(nearFront, true)
                                    //&& !ignorePath.contains(facingRot)
                                    && next.relativeTo(this.tile) != -1
                        //&& passable(nearFront.block())
                    ) {
                        nearTiles.add(next);
                    }
                }
                if (nearTiles.size > 0) {
                    Tile nTile = null;
                    if (task == PathfinderExtended.fieldVent && getClosestVent() != null) {
                        nearTiles.sort(tile1 -> tile1.dst(getClosestVent()));
                        nTile = nearTiles.get(0);
                    }
                    if (task == PathfinderExtended.fieldOres && getClosestOre() != null){
                        nearTiles.sort(tile1 -> tile1.dst(getClosestOre()));
                        nTile = nearTiles.get(0);
                    }
                    //int selected = Mathf.clamp(Mathf.random(0, nearTiles.size), 0, nearTiles.size - 1);
                    //nTile = nearTiles.get(selected);
                    if (nTile != null) {
                        int rot = this.tile.relativeTo(nTile);
                        if (!CantReplace(nTile.block())) nTile.setBlock(ClassicBlocks.cord, team);
                        if (nTile.build != null && nTile.build instanceof CordBuild cordBuild) {
                            cordBuild.task = Mathf.randomBoolean(0.98f) ? task :
                                    Mathf.randomBoolean(0.5f) ? PathfinderExtended.fieldOres : PathfinderExtended.fieldVent;
                            cordBuild.facingRot = rot;
                            cordBuild.prev = this;
                        }
                    }
                }
            }
            super.growCord(block);
        }

        @Override
        public boolean deathImminent() {
            return super.deathImminent() || retry >= 5;
        }

        public Tile pathfind(int pathTarget) {
            int costType = Pathfinder.costGround;
            Tile tile = this.tile;
            if (tile != null) {
                Tile targetTile = Vars.pathfinder.getTargetTile(tile, Vars.pathfinder.getField(team, costType, pathTarget));
                if (tile != targetTile && (costType != 2 || targetTile.floor().isLiquid)) {
                    return targetTile;
                }
            }
            return null;
        }
        @Nullable
        public Tile getClosestOre() {
            Seq<Tile> avaliableOres = PathfinderExtended.Ores.copy();
            Tile ore = Geometry.findClosest(x, y, avaliableOres);
            return ore;
        }


        @Nullable
        public Tile getClosestVent() {
            Seq<Tile> avaliableVents = PathfinderExtended.SteamVents.copy().removeAll(tile -> tile.build instanceof Heart.HeartBuilding);
            Tile vent = Geometry.findClosest(x, y, avaliableVents);
            return (vent != null && !(vent.build instanceof Heart.HeartBuilding)) ? vent : null;
        }

        @Override
        public void update() {
            super.update();
            if (prev != null && retry >= 5){
                if (task == PathfinderExtended.fieldVent) {
                    task = PathfinderExtended.fieldOres;
                    retry = 0;
                } else
                if (!prev.ignorePath.contains(facingRot)) prev.ignorePath.add(facingRot);
            }
            this.block.nearbySide(tile.x, tile.y, Mathf.mod(facingRot, 4), 0, Tmp.p1);
            int dx = (Geometry.d4x(facingRot) > 0) ? 1 : 0;
            int dy = (Geometry.d4y(facingRot) > 0) ? 1 : 0;
            Tile other = Vars.world.tile(Tmp.p1.x + dx, Tmp.p1.y + dy);
            if (other != null && other.solid()) {
                int spaces = calculateSpaces(drill.size, other.x, other.y);
                Item drop = other.wallDrop();
                if (drop != null && spaces >= drill.size * drill.size) {
                    tile.setBlock(ClassicBlocks.neoplasiaDrill, team);
                }
            }

            useful = false;
            for (int i = 0; i < 4; i++) {
                Tile man = nearbyTile(Mathf.mod(facingRot + i, 4));
                if (man != null){
                    if (man.build instanceof CausticDrill.CausticDrillBuild ||
                            man.build instanceof CausticTurret.CausticTurretBuild ||
                            man.build instanceof CausticSpawner.CausticSpawnerBuild ||
                            man.build instanceof CordBuild cordBuild && cordBuild.useful) {
                        useful = true;
                        break;
                    }
                }
            }
        }

        @Override
        public void updateBeat() {
            boolean cordMode = true;
            if (grow) { //TODO some AI strategy block
                if (items.has(Items.beryllium) &&
                        left() == null &&
                        right() == null
                ){
                    if (Mathf.randomBoolean(0.5f)) ReplaceTo(ClassicBlocks.renaleSpawner);
                    else if (Mathf.randomBoolean(0.5f)) ReplaceTo(ClassicBlocks.walkySpawner);
                    else ReplaceTo(ClassicBlocks.oxideCrafter);
                    cordMode = false;
                }

                if (items.has(Items.graphite) &&
                        left() == null &&
                        right() == null
                ){
                    ReplaceTo(ClassicBlocks.squidSpawner);
                    cordMode = false;
                }

                if (items.has(Items.oxide) &&
                        left() == null &&
                        right() == null
                ){
                    ReplaceTo(ClassicBlocks.hydroBomberSpawner);
                    cordMode = false;
                }

                if ((Units.closestEnemy(team, x, y, 440f, u -> u.type.killable && u.type.hittable && u.range() > 240f) != null) ||
                        (Units.findEnemyTile(team, x, y, 440f, b -> b.isValid() && (
                                b instanceof Turret.TurretBuild turretBuild && turretBuild.range() >= 200f)
                        ) != null)) {
                    boolean tooClose = Units.closestBuilding(team, x, y, 440f, b -> (b instanceof CausticTurret.CausticTurretBuild && b.block == ClassicBlocks.pore)) != null;
                    if (!tooClose) {
                        ReplaceTo(ClassicBlocks.pore);
                        cordMode = false;
                    }
                }

                if ((Units.closestEnemy(team, x, y, 120f, u -> u.type.killable && u.type.hittable && u.range() > 120f) != null) ||
                        (Units.findEnemyTile(team, x, y, 140f, b -> b.isValid() && (
                                b instanceof Turret.TurretBuild turretBuild && turretBuild.range() >= 80f)
                        ) != null)) {
                    boolean tooClose = Units.closestBuilding(team, x, y, 120f, b -> (b instanceof CausticTurret.CausticTurretBuild && b.block == ClassicBlocks.bloom)) != null;
                    if (!tooClose) {
                        ReplaceTo(ClassicBlocks.bloom);
                        cordMode = false;
                    }
                }

                if ((Units.closestEnemy(team, x, y, 30f, u -> u.type.killable && u.type.hittable && u.range() > 30f) != null) ||
                        (Units.findEnemyTile(team, x, y, 30f, b -> b.isValid() && (
                                b instanceof Turret.TurretBuild turretBuild && turretBuild.range() >= 30f)
                        ) != null)) {
                    boolean tooClose = Units.closestBuilding(team, x, y, 30f, b -> (b instanceof CausticTurret.CausticTurretBuild && b.block == ClassicBlocks.tole)) != null;
                    if (!tooClose) {
                        ReplaceTo(ClassicBlocks.tole);
                        cordMode = false;
                    }
                } if (cordMode) growCord(ClassicBlocks.cord);
            }
            super.updateBeat();
        }

        @Override
        public void updateAfterBeat() {
            if (current != null){
                Seq<NeoplasmBuilding> avaliable = new Seq<>();
                for (int i = 0; i < 4; i++){
                    NeoplasmBuilding dest = getNeoplasia(nearby(Mathf.mod(facingRot + i, 4)));
                    //NeoplasmBuilding dest = getNeoplasia(nearby(facingRot + i));
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

                /*Seq<NeoplasmBuilding> avaliable = new Seq<>();
                for (int i = 0; i < 4; i++){
                    NeoplasmBuilding dest = getNeoplasia(nearby(facingRot + i));
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
                }*/
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
            NeoplasmBuilding behind = getNeoplasia(back());
            if (behind != null && liquids.get(blood) < liquidCapacity) {
                moveFromLiquid(behind, blood);
            }

            NeoplasmBuilding left = getNeoplasia(left());
            if (left != null && liquids.get(blood) < liquidCapacity) {
                moveFromLiquid(left, blood);
            }

            NeoplasmBuilding right = getNeoplasia(right());
            if (right != null && liquids.get(blood) < liquidCapacity) {
                moveFromLiquid(right, blood);
            }
        }

        protected void drawAt(float x, float y, int bits, float rotation, Autotiler.SliceMode slice) {
            Draw.z(Layer.blockUnder);
            drawBeat(xscl, yscl);
            Draw.rect(sliced(Core.atlas.find(name + "-" + bits), slice), x, y, rotation);
            Draw.color();
            Draw.scl();
        }

        public void onProximityUpdate() {
            super.onProximityUpdate();

            int bit = 0;
            for (int i = 0; i < 8; i++){
                Tile mask = Vars.world.tile(tile.x + Geometry.d8(i).x, tile.y + Geometry.d8(i).y);
                if (mask != null && mask.build instanceof NeoplasmBuilding neoplasmBuilding) {
                    bit |= 1 << (i);
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
