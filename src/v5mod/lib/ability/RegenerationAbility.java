package v5mod.lib.ability;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.scene.actions.ColorAction;
import arc.struct.Seq;
import arc.util.*;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.Ability;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.StatusEffect;

public class RegenerationAbility extends Ability {

    public float healby; //How much basically by tick

    @Override
        public String localized() {
            return Core.bundle.format("ability.regenability", healby);
        }

    public RegenerationAbility(float healby){ //Use old V5 coding adn reformat into v7 style coding.
        this.healby = healby;
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);
        if(unit.health < unit.maxHealth){
            unit.health(unit.health + healby);
            clampHealth(unit);
        }
    }

    public void clampHealth(Unit unit){
        unit.health(Mathf.clamp(unit.health, 0, unit.maxHealth));
    }
}
