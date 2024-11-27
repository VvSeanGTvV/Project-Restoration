package classicMod.library.blocks.neoplasiaBlocks;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.*;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.Log;
import classicMod.content.ClassicBlocks;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.*;

public class NeoplasiaBlock extends Block {

    public boolean source = false;
    public Color beatColor = Color.valueOf("cd6240");

    public boolean isCord = false;

    public NeoplasiaBlock(String name) {
        super(name);

        update = true;
    }

    public class NeoplasiaBuilding extends Building {


        float beat = 1f, beatTimer = 0;
        boolean ready = false, alreadyBeat = false, grow = false;

        @Override
        public void draw() {
            drawBeat(1, 1);
            super.draw();
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

        public Building nearby(int rotation, short x, short y) {
            Building var10000;
            switch (rotation) {
                case 0:
                    var10000 = Vars.world.build(x + 1, y);
                    break;
                case 1:
                    var10000 = Vars.world.build(x, y + 1);
                    break;
                case 2:
                    var10000 = Vars.world.build(x - 1, y);
                    break;
                case 3:
                    var10000 = Vars.world.build(x, y - 1);
                    break;
                default:
                    var10000 = null;
            }

            return var10000;
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
                boolean keepDirection = Mathf.randomBoolean(0.5f);
                Log.info(keepDirection);
                int randRot = (!keepDirection) ? rotation + Mathf.range(4) : rotation;
                Seq<Tile> acceptedTiles = new Seq<>();
                Seq<Integer> acceptedRot = new Seq<>();

                Tile tile = nearbyTile(randRot);
                Boolean safe = false;
                if (tile.build == null) {
                    for (int a = 0; a < 4; a++) {
                        int rotb = Mathf.mod(rotation + a, 4);
                        Building next = nearby(rotb, tile.x, tile.y);
                        if (next == null) {
                            safe = true;
                        }
                    }
                }
                if (!keepDirection) {
                    boolean accept;
                    for (int i = 0; i < 4; i++) {
                        int rot = Mathf.mod(rotation + i, 4);
                        Tile near = nearbyTile(rot);
                        if (near.build == null) {
                            for (int a = 0; a < 4; a++) {
                                int rotb = Mathf.mod(rot + a, 4);
                                Building next = nearby(rotb, near.x, near.y);
                                if (next == null) {
                                    accept = Mathf.randomBoolean();
                                    if (accept) {
                                        acceptedTiles.add(nearbyTile(rot));
                                        acceptedRot.add(rotb);
                                    }
                                } else {
                                    if (acceptedTiles.contains(near) && acceptedRot.contains(rot)) {
                                        acceptedTiles.remove(near);
                                        //acceptedRot.remove(rot);
                                    }
                                }
                            }
                        }
                    }
                    if (!(acceptedTiles.size > 0)) return;
                    int id = (int) Mathf.clamp(Mathf.range(0, acceptedTiles.size), 0, acceptedTiles.size - 1);
                    tile = acceptedTiles.get(id);
                    randRot = acceptedRot.get(id);
                }

                if (tile != null) {
                    if (tile.build == null) {
                        tile.setBlock(block, team, randRot);
                    }
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
                    //growCord(ClassicBlocks.cord);
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
