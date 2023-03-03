package classicMod.lib.ability;

import arc.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.gen.*;

public class SurroundRegenAbility extends Ability {

    public float healAmount = 1, reload = 100, healRange = 60;

    protected float timer;
    protected boolean wasHealed = false;
    
    public SurroundRegenAbility(float healAmount, float reload, float healRange){
        this.healAmount = healAmount;
        this.reload = reload;
        this.healRange = healRange;
    }

    @Override
        public String localized() {
            return Core.bundle.format("ability.surroundregenability", healAmount, healRange);
        }

    @Override
    public void update(Unit unit){
        timer += Time.delta;

        if(timer >= reload){
            wasHealed = false;

            Units.nearby(unit.team(), unit.x, unit.y, healRange, other -> {
                if(other.health < other.maxHealth()){
                    Fx.heal.at(unit);
                    wasHealed = true;
                }
                other.heal(healAmount);
            });

            if(wasHealed){
                Fx.healWave.at(unit);
            }

            timer = 0f;
        }
    }
}
