package classicMod.library.blocks.neoplasiaBlocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.*;
import classicMod.AutotilerPlus;
import classicMod.content.ClassicBlocks;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.Building;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.Autotiler;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.itemSize;

public class Cord extends NeoplasiaBlock implements AutotilerPlus {
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

        rotate = true;
        isCord = true;

        itemCapacity = 1;
        liquidCapacity = 50f;
        priority = -1.0F;
        //envEnabled = 7;
        noUpdateDisabled = false;
    }

    @Override
    public void init() {
        for (int i = 0; i < 5; i++) {
            tiles = new TextureRegion[]{Core.atlas.find(name + "-" + i)};
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

    public class CordBuild extends NeoplasiaBuilding {

        //TODO make it work YIPPE
        public float progress;
        @Nullable
        public Item current;
        public int recDir = 0;
        public int blendbits;
        public int xscl;
        public int yscl;
        public int blending;
        @Nullable
        public Building next;
        @Nullable
        public CordBuild nextc;


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
        public void draw() {
            float rotation = this.rotdeg();
            int r = this.rotation;

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
            boolean keepDir = Mathf.randomBoolean(0.98f);
            int i = Mathf.random(1, 4);
            int rot = (keepDir) ? rotation : Mathf.mod(rotation + i, 4);
            Tile near = nearbyTile(rot);
            Tile nearRight = near.nearby(Mathf.mod(rot + 1, 4));
            Tile nearLeft = near.nearby(Mathf.mod(rot - 1, 4));
            //Tile nearFront = near.nearby(rot);
            if (
                    passable(near)
                            && passable(nearRight)
                            && passable(nearLeft)
                            //&& passable(nearFront.block())
            ){
                if (!CantReplace(near.block())) near.setBlock(ClassicBlocks.cord, team, rot);
            }
            super.growCord(block);
        }

        @Override
        public void updateBeat() {
            if (grow) {
                if ((Units.closestEnemy(team, x, y, 120f, u -> u.type.killable && u.type.hittable) != null) || (Units.findEnemyTile(team, x, y, 120f, Building::isValid) != null)) {
                    bloomTurret();
                } else {
                    growCord(ClassicBlocks.cord);
                }
            }
            if (current != null){
                Seq<NeoplasiaBuilding> avaliable = new Seq<>();
                for (int i = 0; i < 5; i++){
                    NeoplasiaBuilding dest = getNeoplasia(nearby(i));
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

        @Override
        public void update() {
            if (!startBuild) {
                Liquid neoplasm = blood;
                Tile behind = nearbyTile(Mathf.mod(rotation + 2, 4));
                if (back() == null && behind != null) {
                    float leakAmount = liquids.get(neoplasm) / 1.5F;
                    Puddles.deposit(behind, this.tile, neoplasm, liquids.get(neoplasm), true, true);
                    liquids.remove(neoplasm, leakAmount);
                }
            }
            super.update();
        }

        protected void drawAt(float x, float y, int bits, float rotation, Autotiler.SliceMode slice) {
            Draw.z(Layer.blockUnder);
            drawBeat(xscl, yscl);
            Draw.rect(sliced(Core.atlas.find(name + "-" + bits), slice), x, y, rotation);
            Draw.color();
            Draw.scl();
        }

        boolean allSideOccupied(){
            return isNeoplasia(front()) && isNeoplasia(back()) && isNeoplasia(right()) && isNeoplasia(left());
        }

        boolean SideOccupied(){
            return isNeoplasia(left()) && isNeoplasia(right());
        }

        boolean EitherSideOccupied(){
            return isNeoplasia(left()) || isNeoplasia(right());
        }

        boolean noConnectedNearby(){
            return getNeoplasia(front()) == null && getNeoplasia(left()) == null && getNeoplasia(right()) == null && !isNeoplasia(back());
        }

        int blending(NeoplasiaBuilding neoplasiaBuilding, int rotation){
            int blend = 0;
            if (neoplasiaBuilding == null) return 0;
            if (neoplasiaBuilding.block == null) return 0;
            if (neoplasiaBuilding.block instanceof NeoplasiaBlock neoplasiaBlock){
                if (!neoplasiaBlock.squareSprite) blend = rotation;
            }
            return blend;
        }

        public void onProximityUpdate() {
            super.onProximityUpdate();
            Building prevBuild = nearby(Mathf.mod(rotation - 2, 4));
            Tile prev = (prevBuild != null) ? prevBuild.tile : tile;


            //int bit = (left() != null && front() == null) ? 2 : (right() != null && front() == null) ? 2 : 0;

            //Log.info(bits[0]);

            blendbits =
                    (allSideOccupied()) ? 3 :
                    (!EitherSideOccupied() && !isNeoplasia(front())) ? 5 :
                    (isNeoplasia(back()) && isNeoplasia(left()) && !isNeoplasia(front())) ? 1 :
                    (isNeoplasia(back()) && isNeoplasia(right()) && !isNeoplasia(front())) ? 1 :
                    (SideOccupied()) ? 4 :
                    (isNeoplasia(left()) && !isNeoplasia(back()) && isNeoplasia(right())) ? 5 :
                    (isNeoplasia(left()) && !isNeoplasia(back())) ? 1 :
                    (isNeoplasia(left()) && !isNeoplasia(front())) ? 1 :
                    (isNeoplasia(left()) && isNeoplasia(back()) && isNeoplasia(front())) ? 2 :
                    (isNeoplasia(right()) && !isNeoplasia(back()) && isNeoplasia(left())) ? 4 :
                    (isNeoplasia(right()) && !isNeoplasia(back())) ? 1 :
                    (isNeoplasia(right()) && !isNeoplasia(front())) ? 1 :
                    (isNeoplasia(right()) && isNeoplasia(back()) && isNeoplasia(front())) ? 2 : 0;
            xscl =
                    ((rotation == 2 || rotation == 0) && (isNeoplasia(left())) && !isNeoplasia(front())) ? -1 :
                    ((rotation == 1 || rotation == 3) && (isNeoplasia(left())) && !isNeoplasia(front())) ? -1 :
                    ((rotation == 1 || rotation == 0 || rotation == 3 || rotation == 2) && (isNeoplasia(right()) || isNeoplasia(left())) && !isNeoplasia(back())) ? 1 :
                    ((rotation == 1 || rotation == 3) && isNeoplasia(right())) ? -1 : ((rotation == 1 || rotation == 3) && isNeoplasia(left())) ? 1 :
                    ((rotation == 0 || rotation == 2) && isNeoplasia(left())) ? 1 : ((rotation == 0 || rotation == 2) && isNeoplasia(right())) ? -1 : 1;
            yscl =
                    ((rotation == 1 || rotation == 3) && (isNeoplasia(left())) && !isNeoplasia(front())) ? 1 :
                    ((rotation == 1 || rotation == 0 || rotation == 3 || rotation == 2) && (isNeoplasia(right())) && !isNeoplasia(back())) ? -1 :
                    ((rotation == 1 || rotation == 0 || rotation == 3 || rotation == 2) && (isNeoplasia(left())) && !isNeoplasia(back())) ? 1 :
                    ((rotation == 2 || rotation == 0) && isNeoplasia(right())) ? -1 : ((rotation == 2 || rotation == 0) && isNeoplasia(left())) ? 1 :
                    ((rotation == 1 || rotation == 3) && isNeoplasia(left())) ? 1 : ((rotation == 1 || rotation == 3) && isNeoplasia(right())) ? -1 : 1;


            /*blending =
                    (isNeoplasia(left())) && (rotation == 0 || rotation == 2) ? blending(getNeoplasia(left()), 2) :
                    (isNeoplasia(right())) && (rotation == 0 || rotation == 2) ? blending(getNeoplasia(right()), 1) :
                    (isNeoplasia(left())) && (rotation == 1 || rotation == 3) ? blending(getNeoplasia(left()), 1) :
                    (isNeoplasia(right())) && (rotation == 1 || rotation == 3) ? blending(getNeoplasia(right()), 2) :
                    blending(getNeoplasia(back()), 4)
            ;*/

            next = this.front();
            Building var3 = this.next;
            CordBuild var10001;
            if (var3 instanceof CordBuild) {
                CordBuild d = (CordBuild) var3;
                var10001 = d;
            } else {
                var10001 = null;
            }

            nextc = var10001;
        }
    }
}
