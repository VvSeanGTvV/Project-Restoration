package classicMod.library;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import classicMod.content.*;
import mindustry.ai.types.*;
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
    /**
     * Unit's current sprite
     **/
    public String spriteName = "";

    /**
     * Unit's reload
     **/
    public float reloadWeapon = 32f;

    /**
     * Unit's current bullet
     **/
    public BulletType bulletWeapon = ClassicBullets.smol;

    public boolean setOnce;
    /**
     * Unit's current tier, the higher tier it gets, the more buffed it is
     **/
    public int tier = 1;

    public ClassicUnitType(String name) {
        super(name);
        health = 60;
        hitSize = 5f;
        range = 60;
        speed = 0.4f;
        controller = u -> new GroundAI();
        constructor = UnitEntity::create;
        buildSpeed = 0;
        mineTier = 0;
        /*if(tier<=1)outlineColor = Color.valueOf("ffe451");
        if(tier==2)outlineColor = Color.valueOf("f48e20");
        if(tier==3)outlineColor = Color.valueOf("ff6757");
        if(tier==4)outlineColor = Color.valueOf("ff2d86");
        if(tier==5)outlineColor = Color.valueOf("ff2d86");
        if(tier==6)outlineColor = Color.valueOf("cb2dff");
        if(tier>=7)outlineColor = Color.valueOf("362020");*/

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
    public void load() {
        super.load();
        if(!setOnce) {
            speed = speed * tier;
            health = health * tier * 4f;
            maxRange = range / Math.max(tier / 1.5f, 1f) * 2f;
            setOnce = true;
        }

        for(var part : parts){
            part.load("restored-mind-"+spriteName);
        }
        weapons.each(Weapon::load);
        region = Core.atlas.find("restored-mind-"+spriteName);
        fullIcon = region;
        uiIcon = region;
        previewRegion = Core.atlas.find("restored-mind-"+spriteName + "-preview", spriteName);
        legRegion = Core.atlas.find("restored-mind-"+spriteName + "-leg");
        jointRegion = Core.atlas.find("restored-mind-"+spriteName + "-joint");
        baseJointRegion = Core.atlas.find("restored-mind-"+spriteName + "-joint-base");
        footRegion = Core.atlas.find("restored-mind-"+spriteName + "-foot");
        treadRegion = Core.atlas.find("restored-mind-"+spriteName + "-treads");
        itemCircleRegion = Core.atlas.find("ring-item");

        if(treadRegion.found()){
            treadRegions = new TextureRegion[treadRects.length][treadFrames];
            for(int r = 0; r < treadRects.length; r++){
                for(int i = 0; i < treadFrames; i++){
                    treadRegions[r][i] = Core.atlas.find("restored-mind-"+spriteName + "-treads" + r + "-" + i);
                }
            }
        }
        legBaseRegion = Core.atlas.find("restored-mind-"+spriteName + "-leg-base", spriteName + "-leg");
        baseRegion = Core.atlas.find("restored-mind-"+spriteName + "-base");
        cellRegion = Core.atlas.find("restored-mind-"+spriteName + "-cell", Core.atlas.find("power-cell"));
        //when linear filtering is on, it's acceptable to use the relatively low-res 'particle' region
        softShadowRegion =
                squareShape ? Core.atlas.find("square-shadow") :
                        hitSize <= 10f || (Core.settings != null && Core.settings.getBool("linear", true)) ?
                                Core.atlas.find("particle") :
                                Core.atlas.find("circle-shadow");

        outlineRegion = Core.atlas.find("restored-mind-"+spriteName + "-outline");
        shadowRegion = fullIcon;

        wreckRegions = new TextureRegion[3];
        for(int i = 0; i < wreckRegions.length; i++){
            wreckRegions[i] = Core.atlas.find("restored-mind-"+spriteName + "-wreck" + i);
        }

        segmentRegions = new TextureRegion[segments];
        segmentOutlineRegions = new TextureRegion[segments];
        for(int i = 0; i < segments; i++){
            segmentRegions[i] = Core.atlas.find("restored-mind-"+spriteName + "-segment" + i);
            segmentOutlineRegions[i] = Core.atlas.find("restored-mind-"+spriteName + "-segment-outline" + i);
        }

        clipSize = Math.max(region.width * 2f, clipSize);
    }

    @Override
    public void drawOutline(Unit unit) {
        Draw.reset();

        if(Core.atlas.isFound(outlineRegion)){
            Draw.color(outlineColor);
            Draw.rect(outlineRegion, unit.x, unit.y, unit.rotation - 90);
            Draw.reset();
        }
    }
}
