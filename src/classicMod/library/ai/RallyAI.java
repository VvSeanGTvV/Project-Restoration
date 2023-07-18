package classicMod.library.ai;

import arc.struct.Seq;
import classicMod.library.blocks.legacyBlocks.LegacyCommandCenter.LegacyCommandCenterBuild;
import mindustry.Vars;
import mindustry.entities.units.AIController;
import mindustry.world.Block;
import mindustry.world.Build;
import mindustry.world.meta.BlockFlag;

public class RallyAI extends AIController {
    public Seq<LegacyCommandCenterBuild> CommandCenterList = new Seq<>();
    public UnitState state = UnitState.attack; //Default Value so it doesn't crap itself.

    public LegacyCommandCenterBuild findCommandCenterNear(){
        var baseTargets = (Seq<LegacyCommandCenterBuild>)(Seq) Vars.indexer.getFlagged(unit.team, BlockFlag.storage);
        if(baseTargets.isEmpty()) return null;
        return sortTargets(baseTargets);
    }

    LegacyCommandCenterBuild sortTargets(Seq<LegacyCommandCenterBuild> targets){
        //find sort by "most desirable" first
        for(LegacyCommandCenterBuild B : targets){
            if(unit.within(B, unit.range())){
                return B;
            }
        }
        return null;
    }

    public enum UnitState{ //Just reused DriverState code.
        attack, // Attack the units.
        rally, // Rally/Circle around the nearest Command Center.
        retreat; //Dunno

        public static final UnitState[] all = values();
    }
}
