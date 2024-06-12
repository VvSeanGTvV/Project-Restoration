package classicMod.library.ai;

import classicMod.library.animdustry.JumpingUnitType;
import mindustry.ai.*;
import mindustry.ai.types.*;
import mindustry.entities.units.AIController;
import mindustry.gen.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class JumpingAI extends AIController {

    public float timing;

    public boolean stopMoving;

    public boolean hit;

    private float lastHealth;

    private int hitTimer;

    private float lH;

    @Override
    public void init() {
        super.init();
        lastHealth = unit.health;
    }

    @Override
    public void updateMovement() {
        if(unit.type instanceof JumpingUnitType Ju) {
            Building core = unit.closestEnemyCore();

            if ((core == null || !unit.within(core, 0.5f))) {
                boolean move = (Ju.getTimingSine(this) >= 0.5f && !hit);
                stopMoving = false;

                if(lH != unit.health){ hitTimer = 0; lH = unit.health; }
                if(lastHealth != unit.health){
                    hit = true;
                    hitTimer++;
                    stopMoving = true;
                    move = false;

                    if(hitTimer>200){
                        hit = false;
                        lastHealth = unit.health;
                        hitTimer = 0;
                    }
                }

                if (state.rules.waves && unit.team == state.rules.defaultTeam) {
                    Tile spawner = getClosestSpawner();
                    if (spawner != null && unit.within(spawner, state.rules.dropZoneRadius + 120f)){ move = false; stopMoving = true; }
                    if (spawner == null && core == null){ move = false; stopMoving = true; }
                }

                //no reason to move if there's nothing there
                if (core == null && (!state.rules.waves || getClosestSpawner() == null)) {
                    move = false;
                    stopMoving = true;
                }

                if (move) pathfind(Pathfinder.fieldCore);


            }
        }else{
            unit.remove();
        }
    }


}
