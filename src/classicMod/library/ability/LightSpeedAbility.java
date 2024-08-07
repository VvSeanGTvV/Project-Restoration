package classicMod.library.ability;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Lightning;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.world.meta.Stat;

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

    @Override
    public void addStats(Table t){
        t.add("[lightgray]" + Stat.damage.localized() + ": [white]" + damage);
        //t.add("[lightgray]" + Stat.damage.localized() + ": [white]" + maxSpeed);
        //t.add("[lightgray]" + Stat.damage.localized() + ": [white]" + minSpeed);
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
