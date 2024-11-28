package classicMod.library.blocks.neoplasiaBlocks;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.Log;
import classicMod.content.ClassicBlocks;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.gen.Building;
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
    }

    public class NeoplasiaBuilding extends Building {


        boolean lookingAt(Tile tile, int rotation, int otherx, int othery, Block otherblock) {
            Tile facing = Edges.getFacingEdge(otherblock, otherx, othery, tile);
            return facing != null && Point2.equals(tile.x + Geometry.d4(rotation).x, tile.y + Geometry.d4(rotation).y, facing.x, facing.y);
        }

        boolean tookfromSource = false;
        float beat = 1f, beatTimer = 0, tookSourceTimes = 0;
        boolean ready = false, alreadyBeat = false, grow = false;

        @Override
        public void draw() {
            drawBeat(1, 1);
            super.draw();
        }

        public boolean isSource(){
            return source;
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

        public Tile nearbyTile(int rotation, short x, short y) {
            return switch (rotation) {
                case 0 -> Vars.world.tile(x + 1, y);
                case 1 -> Vars.world.tile(x, y + 1);
                case 2 -> Vars.world.tile(x - 1, y);
                case 3 -> Vars.world.tile(x, y - 1);
                default -> null;
            };
        }

        public Building nearby(int rotation, short x, short y) {
            return switch (rotation) {
                case 0 -> Vars.world.build(x + 1, y);
                case 1 -> Vars.world.build(x, y + 1);
                case 2 -> Vars.world.build(x - 1, y);
                case 3 -> Vars.world.build(x, y - 1);
                default -> null;
            };
        }
        public Building nearby(short x, short y, int dx, int dy) {
            return Vars.world.build(x + dx, y + dy);
        }


        public Tile nearbyTile(int rotation, int offsetTrns) {
            Tile var10000;
            int trns = block.size / 2 + 1 + offsetTrns;
            int dx = Geometry.d4(rotation).x * trns, dy = Geometry.d4(rotation).y * trns;
            var10000 = Vars.world.tile(tile.x + dx, tile.y + dy);

            return var10000;
        }

        public Tile nearbyTile(short x, short y, int dx, int dy) {
            return Vars.world.tile(x + dx, y + dy);
        }

        public Tile nearbyTile(int rotation) {
            return nearbyTile(rotation, 0);
        }

        public boolean passable(Block block){
            if (block == null) return false;
            if (
                    block instanceof StaticWall
            ) return false;
            return block == Blocks.air
                    || block instanceof Prop
                    //|| TODO somethin
            ;
        }

        public void coverVent(Block replacmentBlock, Block cordPlacement){
            float steam = 0;
            for (int dy = -1; dy < 2; dy++){
                for (int dx = -1; dx < 2; dx++){
                    Tile tile = Vars.world.tile(this.tile.x + dx, this.tile.y + dy);
                    if (tile.floor() != null){
                        steam += tile.floor().attributes.get(Attribute.steam);
                        if (tile.floor().attributes.get(Attribute.steam) >= 1){
                            if (tile.build == null) tile.setBlock(cordPlacement, team);
                        }
                    }
                }
            }
            if (steam >= 9f){
                Tile replacement = Vars.world.tile(this.tile.x, this.tile.y);
                replacement.setBlock(replacmentBlock, team);
            }
        }

        public boolean front(int rot, short x, short y){
            boolean place = true;
            int dxx = Geometry.d4x(rot);
            int dyy = Geometry.d4y(rot);
            if (dxx != 0) {
                for (int fx = dxx; fx != -(dxx * 2); fx -= dxx) {
                    for (int dy = dxx; dy != -(dxx * 2); dy -= dxx) {
                        int frontRot = -1;
                        Tile front = nearbyTile(x, y, fx, dy);
                        if (front == null) continue;
                        if (front.build != null) {
                            if (front.build != this) frontRot = front.build.rotation;
                        }
                        if (!passable(front.block()) && front.build != this || frontRot != -1) place = false;
                    }
                }
            } else {
                for (int fx = 0; fx != -(dyy * 3); fx -= dyy) {
                    for (int dx = dyy; dx != -(dyy * 2); dx -= dyy) {
                        int frontRot = -1;
                        Tile front = nearbyTile(x, y, fx, dx);
                        if (front == null) continue;
                        if (front.build != null) {
                            if (front.build != this) frontRot = front.build.rotation;
                        }
                        if (!passable(front.block()) && front.build != this || frontRot != -1) place = false;
                    }
                }
            }
            return place;
        }

        public void growCord(Block block){
            if (!isCord) {
                int randRot = (int) Mathf.range(4);
                Tile tile = nearbyTile(randRot);
                if (tile != null) {
                    if (tile.build == null) {
                        tile.setBlock(block, team, randRot);
                    }
                }
            } else {
                boolean keepDirection = Mathf.randomBoolean(0.55f);
                int randRot = (!keepDirection) ? rotation + Mathf.range(4) : rotation;

                Tile tile = nearbyTile(randRot);
                boolean safe = false;
                if (tile.build == null) {
                    safe = front(randRot, tile.x, tile.y);
                }
                if (!safe || !keepDirection) {
                    for (int repeat = 0; repeat < 2; repeat++) {
                        for (int i = 0; i < 4; i++) {
                            int rot = Mathf.mod(randRot + i, 4);
                            safe = front(rot, tile.x, tile.y);
                        }
                    }
                }

                if (safe) {
                    //if (newTile != null) tile = newTile;
                    if (rotation != randRot) this.tile.setBlock(block, team, randRot);
                    tile.setBlock(block, team, randRot);
                }
            }

            grow = false;
        }

        @Override
        public void update() {
            if (source) {
                beatTimer += delta();
                if (beatTimer >= 30) {
                    beat = 1.5f;
                    beatTimer = 0;
                    growCord(ClassicBlocks.cord);
                }
            }

            for(int i = 0; i < 4; ++i) {
                if (i == rotation) continue;
                Building next = nearby(i);
                if (next instanceof NeoplasiaBuilding neoplasiaBuilding) {
                    if (neoplasiaBuilding.beat >= 1.2f && !source && !alreadyBeat) {
                        ready = true;
                        grow = true;
                    }
                }
            }

            if (ready && !alreadyBeat) {
                if (beatTimer >= 2) {
                    if (isCord) coverVent(ClassicBlocks.cordBeat, ClassicBlocks.cord);
                    beatTimer = 0;
                    ready = false;
                    alreadyBeat = true;
                    beat = 1.5f;
                }
            }

            if (alreadyBeat){
                if (beatTimer >= 20) {
                    if (grow) growCord(ClassicBlocks.cord);
                    alreadyBeat = false;
                    beatTimer = 0;
                }
            }
            if (ready || alreadyBeat && !source) beatTimer += delta();


            if (beat > 1.05f) {
                beat = Mathf.lerpDelta(beat, 1f, 0.1f);
            } else {
                if (beat > 1) beat = 1;
            }
        }
    }
}
