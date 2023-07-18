package classicMod.library.ai;

import arc.struct.Seq;
import arc.util.Structs;
import classicMod.library.blocks.legacyBlocks.LegacyCommandCenter;
import classicMod.library.blocks.legacyBlocks.LegacyCommandCenter.LegacyCommandCenterBuild;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Building;

import java.util.Objects;

public class RallyAI extends AIController {
    public UnitState state = UnitState.attack; //Default Value so it doesn't crap itself.
    public Seq<Building> LegacyCommandCenterArea = new Seq<>();
    public void NearbyCenter(){
        LegacyCommandCenterArea.clear();
        Units.closestBuilding(unit.team, unit.x, unit.y, unit.range() - 10f, u -> {
            if(u instanceof LegacyCommandCenterBuild){
                if(Objects.equals(((LegacyCommandCenterBuild) u).CommandSelect, state.name())) LegacyCommandCenterArea.add(u);
            }
            return false;
        });
        LegacyCommandCenterArea.sort(Structs.comparingFloat(b -> b.dst2(unit)));
    }

    public Building SortBuilding(Seq<Building> a, UnitState q) {
        Building local = null;
        for (Building b : a) {
            if (b instanceof LegacyCommandCenterBuild v) {
                if (v.block instanceof LegacyCommandCenter n) {
                    if (v.within(unit, n.MaximumRangeCommand)) {
                        if(v.CommandSelect == q.name()) local = v;
                    }
                }
            }
        }
        return local;
    }

    public enum UnitState{ //Just reused DriverState code.
        attack, // Attack the units.
        rally; // Rally/Circle around the nearest Command Center.
        //retreat; //Dunno

        public static final UnitState[] all = values();
    }
}
