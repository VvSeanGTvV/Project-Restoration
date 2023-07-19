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

public class RallyAI extends AIController {
    public UnitState state = UnitState.attack; //Default Value so it doesn't crap itself.
    public Seq<Building> LegacyCommandCenterArea = new Seq<>();
    public float lastCommandCenterID;
    public Building fallbackLocation;
    public void NearbyCenter(){
        LegacyCommandCenterArea.clear();
        Units.closestBuilding(unit.team, unit.x, unit.y, Float.MAX_VALUE, u -> {
            if(u instanceof LegacyCommandCenterBuild){
                if(Objects.equals(((LegacyCommandCenterBuild) u).CommandSelect, state.name())) LegacyCommandCenterArea.add(u);
            }
            return false;
        });
        LegacyCommandCenterArea.sort(Structs.comparingFloat(b -> b.dst2(unit)));
        Log.info(LegacyCommandCenterArea);
    }

    public enum UnitState{ //Just reused DriverState code.
        attack, // Attack the units.
        rally; // Rally/Circle around the nearest Command Center.
        //retreat; //Dunno

        public static final UnitState[] all = values();
    }
}
