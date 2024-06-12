package classicMod.library.ai;

import arc.math.*;
import classicMod.library.ai.animdustry.JumpingUnitType;
import mindustry.ai.*;
import mindustry.ai.types.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class JumpingAI extends GroundAI {

    @Override
    public void updateMovement() {
        if(unit.type instanceof JumpingUnitType Ju) {
            Building core = unit.closestEnemyCore();

            if ((core == null || !unit.within(core, 0.5f))) {
                boolean move = (Ju.timSine >= 0.5f);

                if (state.rules.waves && unit.team == state.rules.defaultTeam) {
                    Tile spawner = getClosestSpawner();
                    if (spawner != null && unit.within(spawner, state.rules.dropZoneRadius + 120f)) move = false;
                    if (spawner == null && core == null) move = false;
                }

                //no reason to move if there's nothing there
                if (core == null && (!state.rules.waves || getClosestSpawner() == null)) {
                    move = false;
                }

                if (move) pathfind(Pathfinder.fieldCore);
            }
        }else{
            unit.remove();
        }
    }


}
