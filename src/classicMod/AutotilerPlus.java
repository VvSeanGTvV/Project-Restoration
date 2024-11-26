package classicMod;

import arc.math.Mathf;
import arc.util.*;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Tile;
import mindustry.world.blocks.Autotiler;

public interface AutotilerPlus extends Autotiler {
    default int[] buildReverseBlending(Tile tile, Tile prev, int rotation, BuildPlan[] directional, boolean world) {
        int[] blendresult = AutotilerHolder.blendresult;
        blendresult[0] = 0;
        blendresult[1] = blendresult[2] = 1;
        if (prev.block() instanceof AutotilerPlus autotilerPlus) {
            int num =
                    this.blends(prev, rotation, directional, 0, world)
                            && this.blends(prev, rotation, directional, 1, world)
                            && this.blends(prev, rotation, directional, 3, world) ? 4 :
                            (this.blends(prev, rotation, directional, 1, world) && this.blends(prev, rotation, directional, 3, world)) ? 2 : -1;
            //int num = this.blends(tile, rotation, directional, 2, world) && this.blends(tile, rotation, directional, 1, world) && this.blends(tile, rotation, directional, 3, world) ? 2 : (this.blends(tile, rotation, directional, 1, world) && this.blends(tile, rotation, directional, 3, world) ? 3 : (this.blends(tile, rotation, directional, 1, world) && this.blends(tile, rotation, directional, 2, world) ? 2 : (this.blends(tile, rotation, directional, 3, world) && this.blends(tile, rotation, directional, 2, world) ? 1 : (this.blends(tile, rotation, directional, 1, world) ? 2 : (this.blends(tile, rotation, directional, 3, world) ? 3 : -1)))));
            this.transformCase(num, blendresult);
            blendresult[3] = 0;


            for (int i = 0; i < 4; ++i) {
                if (this.blends(tile, rotation, directional, i, world)) {
                    blendresult[3] |= 1 << i;
                }
            }

            blendresult[4] = 0;

            for (int i = 0; i < 4; ++i) {
                int realDir = Mathf.mod(rotation + i, 4);
                if (this.blends(prev, rotation, directional, i, world) && prev != null && prev.nearbyBuild(realDir) != null && !prev.nearbyBuild(realDir).block.squareSprite) {
                    blendresult[4] |= 1 << i;
                }
            }
        }

        return blendresult;
    }

    @Override
    default boolean blends(Tile tile, int rotation, @Nullable BuildPlan[] directional, int direction, boolean checkWorld) {
        int realDir = Mathf.mod(rotation + direction, 4);
        if (directional != null && directional[realDir] != null) {
            BuildPlan req = directional[realDir];
            if (this.blends(tile, rotation, req.x, req.y, req.rotation, req.block)) {
                return true;
            }
        }

        return checkWorld && this.blends(tile, rotation, direction);
    }

    public static class AutotilerHolder {
        static final int[] blendresult = new int[5];
        static final BuildPlan[] directionals = new BuildPlan[4];

        public AutotilerHolder() {
        }
    }
}
