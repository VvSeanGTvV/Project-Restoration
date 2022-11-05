package v5mod.lib.ability;
import arc.Core;
import arc.audio.Sound;
import arc.graphics.Blending;
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

public class LightSpeedAbility extends Ability { //Combined of V5 and V7 coding just formatted to be compatible!
    public float damage;
    public float minSpeed;
    public float maxSpeed;
    protected Color color = Pal.lancerLaser; //Do not modify!
    protected TextureRegion speedEffect; //Do not modify! 
    public LightSpeedAbility(float damage, float minSpeed, float maxSpeed) {
        this.damage = damage;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
    }

    @Override
    public void update(Unit unit){
        super.update(unit); 
        if(speedEffect == null){Core.atlas.find(unit.type.name + "-shield");}
        float scl = scld(unit);
        if(Mathf.chance(Time.delta * (0.15 * scl))){
            Fx.hitLancer.at(unit.x, unit.y, Pal.lancerLaser);
            Lightning.create(unit.team(), Pal.lancerLaser, damage * Vars.state.rules.unitDamageMultiplier, unit.x + unit.vel().x, unit.y + unit.vel().y, unit.rotation, 14);
        }
    }

    private float scld(Unit unit) { //make a similar method
        return Mathf.clamp((unit.vel().len() - minSpeed) / (maxSpeed - minSpeed));
    }

    @Override
    public void draw(Unit unit){
        float scl = scld(unit);
        TextureRegion region = Core.atlas.find(unit.type.name + "-shield");
        if(Core.atlas.isFound(region) && !(scl < 0.01f)){
            Draw.color(color);
            Draw.alpha(scl / 2f);
            Draw.blend(Blending.additive);
            Draw.rect(region, unit.x + Mathf.range(scl / 2f), unit.y + Mathf.range(scl / 2f), unit.rotation - 90);
            Draw.blend();
        }
    }

    @Override
    public String localized() {
        return Core.bundle.format("ability.lightspeedability", damage);
    }
}
