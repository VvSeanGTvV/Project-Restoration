package classicMod.library.blocks.customBlocks;

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

public class Cord extends Block implements AutotilerPlus {
    public TextureRegion[] regions;

    public boolean source = false;


    public Cord(String name) {
        super(name);

        group = BlockGroup.transportation;
        hasItems = true;
        update = true;
        solid = false;
        unloadable = false;
        underBullets = true;

        rotate = true;
        conveyorPlacement = true;
        isDuct = true;

        priority = -1.0F;
        //envEnabled = 7;
        noUpdateDisabled = false;
    }

    @Override
    public void init() {
        for (int i = 0; i < 5; i++) {
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

    public class CordBuild extends Building {
        public float beat = 1f, beatTimer = 0;
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

        boolean ready = false, alreadyBeat = false;
        Color beatColor = Color.valueOf("cd6240");

        @Override
        public void draw() {
            float rotation = this.rotdeg();
            int r = this.rotation;

            for(int i = 0; i < 4; ++i) {
                if ((blending & 1 << i) != 0) {
                    int dir = r - i;
                    float rot = i == 0 ? rotation : (float)(dir * 90);
                    drawAt(x + (float)(Geometry.d4x(dir) * 8) * 0.75F, y + (float)(Geometry.d4y(dir) * 8) * 0.75F, 0, rot, i != 0 ? SliceMode.bottom : SliceMode.top);
                }
            }
            Draw.z(Layer.block);
            Draw.scl(xscl * beat, yscl * beat);

            Draw.color(new Color(1.0F, 1.0F, 1.0F, 1.0F).lerp(beatColor, (beat - 1)));
            drawAt(x, y, blendbits, rotation, SliceMode.none);
            Draw.color();

            Draw.reset();
        }

        @Override
        public void updateTile() {
            if (source) {
                beatTimer += delta();
                if (beatTimer >= 30) {
                    beat = 1.5f;
                    beatTimer = 0;
                }
            }

            for(int i = 0; i < 4; ++i) {
                if (i == rotation) continue;
                Building next = nearby(i);
                if (next instanceof CordBuild cordBuild) {
                    if (cordBuild.beat >= 1.2f && !source && !alreadyBeat) ready = true;
                }
            }

            if (ready && !alreadyBeat) {
                if (beatTimer >= 2) {
                    beatTimer = 0;
                    ready = false;
                    alreadyBeat = true;
                    beat = 1.5f;
                }
            }
            if (alreadyBeat){
                if (beatTimer >= 20) {
                    alreadyBeat = false;
                    beatTimer = 0;
                }
            }
            if (ready || alreadyBeat && !source) beatTimer += delta();


            if (beat > 1.1f) {
                beat = Mathf.lerpDelta(beat, 1f, 0.1f);
            } else {
                if (beat > 1) beat = 1;
            }
            //if (beat <= 1f) alreadyBeat = false;
            //Log.info(beat);
        }

        protected void drawAt(float x, float y, int bits, float rotation, Autotiler.SliceMode slice) {
            Draw.rect(sliced(Core.atlas.find(name + "-" + bits), slice), x, y, rotation);
        }

        boolean allSideOccupied(){
            return front() != null && back() != null && left() != null && right() != null;
        }

        public void onProximityUpdate() {
            super.onProximityUpdate();
            Building prevBuild = nearby(Mathf.mod(rotation - 2, 4));
            Tile prev = (prevBuild != null) ? prevBuild.tile : tile;


            //int bit = (left() != null && front() == null) ? 2 : (right() != null && front() == null) ? 2 : 0;

            //Log.info(bits[0]);
            blendbits = (allSideOccupied()) ? 3 : (left() != null && back() == null && right() != null) ? 4 : (left() != null && back() == null) ? 1 : (left() != null && front() == null) ? 1 : (left() != null && back() != null && front() != null) ? 2 :
                    (right() != null && back() == null && left() != null) ? 4 : (right() != null && back() == null) ? 1 : (right() != null && front() == null) ? 1 : (right() != null && back() != null && front() != null) ? 2 : 0;
            xscl = ((rotation == 1 || rotation == 3) && right() != null) ? -1 : ((rotation == 0 || rotation == 2) && left() != null && front() == null) ? 1 : -1;
            yscl = ((rotation == 2 || rotation == 0) && right() != null) ? -1 : ((rotation == 1 || rotation == 3) && left() != null && front() == null) ? 1 : -1;

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
