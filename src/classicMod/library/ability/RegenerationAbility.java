package classicMod.library.ability;

import arc.Core;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.world.meta.Stat;

public class RegenerationAbility extends Ability {

    public float healby; //How much basically by tick

    @Override
        public String localized() {
            return Core.bundle.format("ability.regenability", healby);
        }

    public RegenerationAbility(float healby){ //Use old V5 coding adn reformat into v7 style coding.
        this.healby = healby; //for legacy usage
    }

    public RegenerationAbility(){
    }

    @Override
    public void addStats(Table t){
        t.add("[lightgray]" + Stat.healing.localized() + ": [white]" + healby);
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);
        float healDelta = Time.delta * healby;
        if(unit.health < unit.maxHealth){
            unit.health(unit.health + healDelta);
            clampHealth(unit);
        }
    }

    public void clampHealth(Unit unit){
        unit.health(Mathf.clamp(unit.health, 0, unit.maxHealth));
    }
}
