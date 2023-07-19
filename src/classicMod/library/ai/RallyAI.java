package classicMod.library.ai;

import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Structs;
import classicMod.library.blocks.legacyBlocks.LegacyCommandCenter;
import classicMod.library.blocks.legacyBlocks.LegacyCommandCenter.LegacyCommandCenterBuild;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Building;
import mindustry.gen.Unit;

import java.util.Objects;

import static classicMod.library.blocks.legacyBlocks.LegacyCommandCenter.MaximumRangeCommand;

public class RallyAI extends AIController {
    public UnitState state = UnitState.attack; //Default Value so it doesn't crap itself.
    public Seq<Building> LegacyCommandCenterArea = new Seq<>();
    public float lastCommandCenterID;
    public Building building;
    public void NearbyCenter(){
        LegacyCommandCenterArea.clear();
        Units.closestBuilding(unit.team, unit.x, unit.y, 4096f, u -> {
            if(u instanceof LegacyCommandCenterBuild){
                if(Objects.equals(((LegacyCommandCenterBuild) u).CommandSelect, state.name())) LegacyCommandCenterArea.add(u);
            }
            return false;
        });
        LegacyCommandCenterArea.sort(Structs.comparingFloat(b -> b.dst2(unit)));
    }

    public Building CenterLocate(){
        var close = 0f;
        Building lB = null;
        for(Building b : LegacyCommandCenterArea){
            if(close==0f) close = b.dst2(unit);
            if(b instanceof LegacyCommandCenterBuild v){
                if(v.block instanceof LegacyCommandCenter c) {
                    if(close > b.dst2(unit)){
                        close = b.dst2(unit);
                        lB = b;
                    }
                }
            }
        }
        return lB;
    }

    public enum UnitState{ //Just reused DriverState code.
        attack, // Attack the units.
        rally; // Rally/Circle around the nearest Command Center.
        //retreat; //Dunno

        public static final UnitState[] all = values();
    }
}
