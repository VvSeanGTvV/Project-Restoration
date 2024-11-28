package classicMod.library.blocks.neoplasiaBlocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.util.*;
import classicMod.AutotilerPlus;
import mindustry.gen.Building;
import mindustry.graphics.*;
import mindustry.type.Item;
import mindustry.world.*;
import mindustry.world.blocks.Autotiler;
import mindustry.world.meta.BlockGroup;

public class Cord extends NeoplasiaBlock implements AutotilerPlus {
    public TextureRegion[] regions;

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

        priority = -1.0F;
        //envEnabled = 7;
        noUpdateDisabled = false;
    }

    @Override
    public void init() {
        for (int i = 0; i < 6; i++) {
            regions = new TextureRegion[]{Core.atlas.find(name + "-" + i)};
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
        public void draw() {
            float rotation = this.rotdeg();
            int r = this.rotation;

            Draw.z(29.5F);
            for(int i = 0; i < 4; ++i) {
                if ((blending & 1 << i) != 0) {
                    int dir = r + i;
                    float rot = i == 0 ? rotation : (float)(dir * 90);
                    drawBeat(xscl, yscl);
                    drawAt(x + (float)(Geometry.d4x(dir) * 8) * 0.75F, y + (float)(Geometry.d4y(dir) * 8) * 0.75F, 0, rot, i != 0 ? SliceMode.bottom : SliceMode.top);
                }
            }

            drawBeat(xscl, yscl);
            drawAt(x, y, blendbits, rotation, SliceMode.none);
            Draw.color();

            Draw.reset();
        }

        protected void drawAt(float x, float y, int bits, float rotation, Autotiler.SliceMode slice) {
            Draw.rect(sliced(Core.atlas.find(name + "-" + bits), slice), x, y, rotation);
        }

        boolean allSideOccupied(){
            return isNeoplasia(front()) && isNeoplasia(back()) && isNeoplasia(right()) && isNeoplasia(left());
        }
        boolean noSideOccupied(){
            return getNeoplasia(front()) == null && getNeoplasia(left()) == null && getNeoplasia(right()) == null;
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
            blendbits = (allSideOccupied()) ? 3 :(noSideOccupied()) ? 5 : (isNeoplasia(left()) && !isNeoplasia(back()) && isNeoplasia(right())) ? 4 : (isNeoplasia(left()) && !isNeoplasia(back())) ? 1 : (isNeoplasia(left()) && !isNeoplasia(front())) ? 1 : (isNeoplasia(left()) && isNeoplasia(back()) && isNeoplasia(front())) ? 2 :
                    (isNeoplasia(right()) && !isNeoplasia(back()) && isNeoplasia(left())) ? 4 : (isNeoplasia(right()) && !isNeoplasia(back())) ? 1 : (isNeoplasia(right()) && !isNeoplasia(front())) ? 1 : (isNeoplasia(right()) && isNeoplasia(back()) && isNeoplasia(front())) ? 2 : 0;
            xscl =
                    ((rotation == 1 || rotation == 2) && (isNeoplasia(right()) || isNeoplasia(left())) && !isNeoplasia(back())) ? 1 :
                    ((rotation == 3 || rotation == 0) && (isNeoplasia(left()) || isNeoplasia(right()))&& !isNeoplasia(back())) ? 1 :
                    ((rotation == 1 || rotation == 3) && isNeoplasia(right())) ? -1 : ((rotation == 1 || rotation == 3) && isNeoplasia(left())) ? 1 :
                    ((rotation == 0 || rotation == 2) && isNeoplasia(left())) ? 1 : ((rotation == 0 || rotation == 2) && isNeoplasia(right())) ? -1 : 1;
            yscl =
                    ((rotation == 1) && isNeoplasia(left()) && !isNeoplasia(back())) ? 1 : ((rotation == 3) && isNeoplasia(right()) && !isNeoplasia(back())) ? -1 :
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
