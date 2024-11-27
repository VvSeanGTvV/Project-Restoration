package classicMod.library.blocks.neoplasiaBlocks;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.world.Block;

public class NeoplasiaBlock extends Block {

    public boolean source = false;
    public Color beatColor = Color.valueOf("cd6240");

    public NeoplasiaBlock(String name) {
        super(name);

        update = true;
    }

    public class NeoplasiaBuilding extends Building {

        float beat = 1f, beatTimer = 0;
        boolean ready = false, alreadyBeat = false;

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
                if (next instanceof NeoplasiaBuilding neoplasiaBuilding) {
                    if (neoplasiaBuilding.beat >= 1.2f && !source && !alreadyBeat) ready = true;
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


            if (beat > 1.05f) {
                beat = Mathf.lerpDelta(beat, 1f, 0.1f);
            } else {
                if (beat > 1) beat = 1;
            }
        }
    }
}
