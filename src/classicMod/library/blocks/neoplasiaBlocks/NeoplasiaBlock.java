package classicMod.library.blocks.neoplasiaBlocks;

import arc.graphics.Color;
import arc.graphics.g2d.*;
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
        drawTeamOverlay = false;
    }

    public class NeoplasiaBuilding extends Building {

        public Seq<Tile> proximityTiles = new Seq<>();

        boolean startBuild = true, initalize = false;
        float beat = 1f, beatTimer = 0, priority = 0;
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
            boolean acceptable = true;
            if (block == null) return false;

            if (block instanceof Floor floor){
                if (floor.liquidDrop != null){
                    Log.info(floor.liquidDrop)
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
            if (!isCord) {
                int randRot = (int) Mathf.range(4);
                Tile tile = nearbyTile(randRot);
                if (tile != null) {
                    if (tile.build == null) {
                        tile.setBlock(block, team, randRot);
                    }
                }
            } else {
                boolean keepDir = Mathf.randomBoolean(0.95f);
                int i = Mathf.random(1, 4);
                int rot = (keepDir) ? rotation : Mathf.mod(rotation + i, 4);
                Tile near = nearbyTile(rot);
                Tile nearRight = near.nearby(Mathf.mod(rot + 1, 4));
                Tile nearLeft = near.nearby(Mathf.mod(rot - 1, 4));
                Tile nearFront = near.nearby(rot);
                if (
                       passable(near.block())
                    && passable(nearRight.block())
                    && passable(nearLeft.block())
                    && passable(nearFront.block())
                ){
                    if (!CantReplace(near.block())) near.setBlock(ClassicBlocks.cord, team, rot);
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

        }

        @Override
        public void update() {
            if (!startBuild) {
                if (source) {
                    priority = 0;
                    beatTimer += delta();
                    if (beatTimer >= 30) {
                        beat = 1.5f;
                        beatTimer = 0;
                        growCord(ClassicBlocks.cord);
                    }
                }

                for(int i = 0; i <proximity.size; ++i) {
                    this.incrementDump(proximity.size);
                    Building other = proximity.get((i) % proximity.size);
                    if (other instanceof NeoplasiaBuilding neoplasiaBuilding) {
                        if (neoplasiaBuilding.beat >= 1.2f && !source && !alreadyBeat) {
                            if (neoplasiaBuilding.isSource()) priority = 10;
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
                    beat = Mathf.lerpDelta(beat, 1f, 0.1f);
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

        public class tileSafe {
            Tile tile;
            int rot;

            public tileSafe(Tile tile, int rot){
                this.tile = tile;
                this.rot = rot;
            }

            @Override
            public String toString() {
                return "TILE : " + tile +
                       " | ROT : " + rot;
            }
        }
    }
}
