package classicMod.library.ability;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.*;
import mindustry.entities.abilities.Ability;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.meta.Stat;

public class ArmorAbility extends Ability {
    

        public float healthMultiplier = 1f;
        protected TextureRegion ArmorPlateStatic; //Do not modify!
        protected TextureRegion ArmorPlateEffect; //Do not modify!
        protected Color color = Pal.accent; //Do not modify!
        protected float warmup; //Do not modify!
        protected float z = Layer.effect; //Do not modify!
        public float unitHealth;
    
        public ArmorAbility() {
        }
    
        public ArmorAbility(float healthMultiplier) {
            this.healthMultiplier = healthMultiplier;
        }

        public void init(Unit unit){
            if(unitHealth == 0f){unitHealth = unit.health;};
        }
    
        @Override
        public String localized() {
            return Core.bundle.format("ability.armorability", healthMultiplier * unitHealth);
        }

        @Override
        public void addStats(Table t){
            t.add("[lightgray]" + Stat.healthMultiplier.localized() + ": [white]" + Math.round(healthMultiplier) + "%");
        }

        //@Override
        //public void load(){
        //    ArmorPlateStatic = Core.atlas.find("projectv5-mod-omega-mech-armor");
        //    ArmorPlateEffect = Core.atlas.find("projectv5-mod-omega-mech-armor-effect");
        //}

        @Override
        public void draw(Unit unit) {
            if(ArmorPlateStatic == null){ArmorPlateStatic=Core.atlas.find(unit.type.name + "-armor", unit.type.region);}
            if(ArmorPlateEffect == null){ArmorPlateEffect=Core.atlas.find(unit.type.name + "-armor-effect", unit.type.region);}
            Draw.alpha(warmup);
            Draw.rect(ArmorPlateStatic, unit.x, unit.y, unit.rotation - 90);
            if(warmup > 0.001){
                Draw.draw(z <= 0 ? Draw.z() : z, () -> {
                    Shaders.armor.progress = warmup;
                    Shaders.armor.region = ArmorPlateEffect;
                    Shaders.armor.time = Time.time / 10;
                    

                    Draw.color(color);//ColorArmor);
                    //Shaders.armor.color.set(Pal.accent).a = warmup;
                    Draw.shader(Shaders.armor);
                    Draw.rect(Shaders.armor.region, unit.x, unit.y, unit.rotation - 90);
                    Draw.shader();
                    Draw.reset();
                });
            }
        }
        
        @Override
        public void update(Unit unit) {
            super.update(unit);
            warmup = Mathf.lerpDelta(warmup, unit.isShooting ? 1 : 0, 0.1f);
            unit.healthMultiplier += warmup * healthMultiplier;
        }
    }
