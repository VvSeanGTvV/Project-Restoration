package classicMod.library.blocks;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.io.Reads;
import classicMod.content.ClassicBlocks;
import mindustry.content.*;
import mindustry.gen.Building;
import mindustry.graphics.*;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.*;
import mindustry.world.blocks.legacy.LegacyBlock;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.meta.Stat;

@Deprecated
public class LiquidConverter extends LegacyBlock { //LEGACY
    public Block replacement = ClassicBlocks.cellSynthesisChamber;

    public LiquidConverter(String name){
        super(name);
        update = true;
        hasPower = true;
        hasItems = true;
        solid = false;
    }

    @Override
    public void removeSelf(Tile tile){
        int rot = tile.build == null ? 0 : tile.build.rotation;
        tile.setBlock(replacement, tile.team(), rot);
    }

    public class LiquidConverterBuild extends Building {

    }
}
