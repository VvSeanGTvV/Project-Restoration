package classicMod.library.ai;

import arc.struct.Seq;
import arc.util.Structs;
import classicMod.library.blocks.legacyBlocks.LegacyCommandCenter.LegacyCommandCenterBuild;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Building;

import java.util.Objects;

import static classicMod.content.RVars.MaximumRangeCommand;

public class RallyAI extends AIController {
    public UnitState state, defaultState; //Default Value so it doesn't crap itself.
    public Seq<LegacyCommandCenterBuild> LegacyCommandCenterArea = new Seq<>();
    public float lastCommandCenterID;
    private int lastNum;
    public Building building;

    @Override
    public void init() {
        super.init();

    }

    @Override
    public void updateUnit() {
        super.updateUnit();
        if (state == null) {
            NearbyCommand();
            if (LegacyCommandCenterArea.size > 0){
                state = RallyAI.UnitState.all[LegacyCommandCenterArea.get(0).config()];
            } else {
                state = UnitState.attack;
            }
        }
    }

    public void updateState(UnitState unitState){
        defaultState = unitState;
        state = unitState;
    }

    public void NearbyCommand(){
        LegacyCommandCenterArea.clear();
        Units.closestBuilding(unit.team, unit.x, unit.y, MaximumRangeCommand, u -> {
            if(u instanceof LegacyCommandCenterBuild lccb){
                LegacyCommandCenterArea.add(lccb);
            }
            return false;
        });
    }

    public void NearbyCenter(){
        LegacyCommandCenterArea.clear();
        Units.closestBuilding(unit.team, unit.x, unit.y, MaximumRangeCommand, u -> {
            if(u instanceof LegacyCommandCenterBuild lccb){
                if(Objects.equals(((LegacyCommandCenterBuild) u).CommandSelect, state.name())) LegacyCommandCenterArea.add(lccb);
            }
            return false;
        });
        if(LegacyCommandCenterArea.size != lastNum){
            LegacyCommandCenterArea.sort(Structs.comparingFloat(b -> b.dst2(unit)));
            lastNum = LegacyCommandCenterArea.size;
        }
    }

    public enum UnitState{ //Just reused DriverState code.
        attack, // Attack the units.
        rally; // Rally/Circle around the nearest Command Center.
        //retreat; //Dunno

        public static final UnitState[] all = values();
        public static final String[] allString = new String[]{"attack", "rally"};
    }
}
