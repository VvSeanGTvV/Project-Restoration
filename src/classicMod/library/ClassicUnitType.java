package classicMod.library;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import classicMod.content.*;
import mindustry.content.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.*;

import static classicMod.content.ClassicSounds.*;
import static mindustry.Vars.*;

public class ClassicUnitType extends UnitType {
    public float reloadWeapon = 32f;
    public BulletType bulletWeapon = ClassicBullets.smol;
    /**
     * Unit's current tier, the higher tier it gets, the more buffed it is
     **/
    public int tier = 1;
    /**
     * Unit's outline tier color
     **/
    public final static Color[] tierColors = {
            Color.valueOf("ffe451"), Color.valueOf("f48e20"), Color.valueOf("ff6757"),
            Color.valueOf("ff2d86"), Color.valueOf("cb2dff"), Color.valueOf("362020")};

    public ClassicUnitType(String name) {
        super(name);
        health = 60 * tier * 4f;
        hitSize = 5f;
        range = 60;
        speed = 0.4f * tier;

        weapons.add(new Weapon("restored-mind-nullTexture") {{
            x = 2f;
            y = 0;
            top = true;

            reload = reloadWeapon;
            alternate = true;
            //ejectEffect = ExtendedFx.shellEjectSmall;
            mirror = true;
            shootSound = shootDefault;
            shootX = -2.5f;


            bullet = bulletWeapon;
        }});
    }

    @Override
    public void setStats(){
        stats.add(ExtendedStat.tierLevel, tier);
        stats.add(Stat.health, health);
        stats.add(Stat.armor, armor);
        stats.add(Stat.speed, speed * 60f / tilesize, StatUnit.tilesSecond);
        stats.add(Stat.size, StatValues.squared(hitSize / tilesize, StatUnit.blocks));
        stats.add(Stat.itemCapacity, itemCapacity);
        stats.add(Stat.range, (int)(maxRange / tilesize), StatUnit.blocks);

        if(abilities.any()){
            var unique = new ObjectSet<String>();

            for(Ability a : abilities){
                if(a.display && unique.add(a.localized())){
                    stats.add(Stat.abilities, a.localized());
                }
            }
        }

        stats.add(Stat.flying, flying);

        if(!flying){
            stats.add(Stat.canBoost, canBoost);
        }

        if(mineTier >= 1){
            stats.addPercent(Stat.mineSpeed, mineSpeed);
            stats.add(Stat.mineTier, StatValues.blocks(b ->
                    b.itemDrop != null &&
                            (b instanceof Floor f && (((f.wallOre && mineWalls) || (!f.wallOre && mineFloor))) ||
                                    (!(b instanceof Floor) && mineWalls)) &&
                            b.itemDrop.hardness <= mineTier && (!b.playerUnmineable || Core.settings.getBool("doubletapmine"))));
        }
        if(buildSpeed > 0){
            stats.addPercent(Stat.buildSpeed, buildSpeed);
        }
        if(sample instanceof Payloadc){
            stats.add(Stat.payloadCapacity, StatValues.squared(Mathf.sqrt(payloadCapacity / (tilesize * tilesize)), StatUnit.blocks));
        }

        var reqs = getFirstRequirements();

        if(reqs != null){
            stats.add(Stat.buildCost, StatValues.items(reqs));
        }

        if(weapons.any()){
            stats.add(Stat.weapons, StatValues.weapons(this, weapons));
        }

        if(immunities.size > 0){
            var imm = immunities.toSeq().sort();
            //it's redundant to list wet for naval units
            if(naval){
                imm.remove(StatusEffects.wet);
            }
            stats.add(Stat.immunities, StatValues.statusEffects(imm));
        }
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);
        if(speed != speed * tier) speed = speed * tier; //this works im keeping it
        if(health != health * tier * 4f) health = health * tier * 4f; //this works im keeping it
    }

    @Override
    public void drawOutline(Unit unit) {
        Draw.reset();

        if (Core.atlas.isFound(outlineRegion)) {
            Draw.color(tierColors[tier]);
            Draw.rect(outlineRegion, unit.x, unit.y, unit.rotation - 90);
            Draw.reset();

        }
    }
}
