package classicMod.lib.ability;


import arc.Core;
import arc.math.Mathf;
import arc.util.*;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.Ability;
import mindustry.gen.*;
import mindustry.graphics.*;

public class LightLandingAbility extends Ability {

    public float damage = 17f;
    protected boolean onLand; //Do not modify!
    public LightLandingAbility(float damage) {
        this.damage = damage;
    }

    @Override
    public void update(Unit unit){
        super.update(unit); //Make like one time mechanism
        if(!unit.isFlying()){
            if(onLand == false){
                Fx.landShock.at(unit);
                Effect.shake(1f, 1f, unit);
                for(int i = 0; i < 8; i++){
                    Time.run(Mathf.random(8f), () -> Lightning.create(unit.team(), Pal.lancerLaser, damage * Vars.state.rules.unitDamageMultiplier, unit.x, unit.y, Mathf.random(360f), 14));
                }
                onLand = true;
            }
        } else {
            onLand = false;
        }
    }

    @Override
    public String localized() {
        return Core.bundle.format("ability.lightlandability", damage);
    }
}
