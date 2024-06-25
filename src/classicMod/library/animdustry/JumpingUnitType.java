package classicMod.library.animdustry;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureAtlas;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import classicMod.content.ExtendedFx;
import classicMod.library.ai.JumpingAI;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.MultiPacker;
import mindustry.type.UnitType;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.tilesize;
import static mindustry.core.UI.packer;

public class JumpingUnitType extends UnitType {

    public TextureRegion ouch, outlineOuchRegion;

    public boolean onlySlide = false;

    public Effect StompEffect = ExtendedFx.dynamicWaveBig;
    public Effect StompExplosionEffect = ExtendedFx.dynamicSmallBomb;
    public Color StompColor = Color.valueOf("ffd27e");
    public boolean StompExplosion = false;

    public float healPercent = 0f;
    public float healRange = 0f;

    public JumpingUnitType(String name) {
        super(name);
        controller = u -> new JumpingAI();
        outlineRadius = 1;
        flying = false;

        outlineColor = Color.black;
        logicControllable = playerControllable = allowedInPayloads = false;
        //lowAltitude = drawCell = isEnemy = false;
    }

    @Override
    public void setStats() {
        stats.add(Stat.health, health);
        stats.add(Stat.size, StatValues.squared(hitSize / tilesize, StatUnit.blocks));
        if(healPercent > 0f && healRange > 0f){
            stats.add(Stat.healing, healPercent, StatUnit.percent);
            stats.add(Stat.range, StatValues.squared(healRange / tilesize, StatUnit.none));
        }
    }

    @Override
    public void update(Unit unit) {
        if(unit.controller() instanceof JumpingAI ai) {
            ai.timing += 0.15f * Time.delta;
            if(getTimingSine(ai) > 0f){
                ai.timingY -= 0.275f * Time.delta;
            }

            if(ai.hit) ai.hitTimer += 2.25f * Time.delta;
        }
    }

    @Override
    public void init() {
        super.init();

        ouch = Core.atlas.find(name + "-hit");
        region = Core.atlas.find(name);
        outlineOuchRegion = Core.atlas.find(name + "-hit-outline");
    }

    boolean flip;
    @Override
    public void draw(Unit unit) {
        if(unit.controller() instanceof JumpingAI ai) {
            ouch = Core.atlas.find(name + "-hit"); region = Core.atlas.find(name); outlineOuchRegion = Core.atlas.find(name + "-hit-outline");

            int direction = Mathf.round((unit.rotation / 90) % 4);
            if(!(direction == 1 || direction == 3)) flip = (direction == 0);
            Draw.xscl = Mathf.sign(flip);
            var sine = Mathf.sin(ai.timing);
            Draw.z(Layer.groundUnit);

            if (sine < -0.85f){ ai.timing = 2f; ai.timingY = 0.5f; }
            if ((sine > 0f && !ai.stopMoving) && !onlySlide) {
                var Ysine = Mathf.sin(Mathf.sin(ai.timingY) * 3);
                if(!ai.hit) Draw.rect(region, unit.x, unit.y + 2 + Ysine * 3, (((float) region.width / 2) + sine * 5) * Draw.xscl, ((float) region.height / 2) - sine * 10);
                if(ai.hit){
                    drawOuchOutline(unit);
                    Draw.rect(ouch, unit.x, unit.y + 2 + Ysine * 3, (((float) ouch.width / 2) + sine * 5) * Draw.xscl, ((float) ouch.height / 2) - sine * 10);
                }
            } else {
                if(!ai.hit) Draw.rect(region, unit.x, unit.y + 2, (((float) region.width / 2) * Draw.xscl), (float) region.height / 2);
                if(ai.hit){
                    drawOuchOutline(unit);
                    Draw.rect(ouch, unit.x, unit.y + 2, (((float) ouch.width / 2) * Draw.xscl), ((float) ouch.height / 2));
                }
            }
            Draw.xscl = -1f;
        }
    }

    public void drawOuchOutline(Unit unit){
        Draw.reset();

        if(Core.atlas.isFound(outlineOuchRegion)){
            applyColor(unit);
            applyOutlineColor(unit);
            Draw.rect(outlineOuchRegion, unit.x, unit.y + 2, (((float) ouch.width / 2) * Draw.xscl), ((float) ouch.height / 2));
            //Draw.rect(outlineOuchRegion, unit.x, unit.y, unit.rotation - 90);
            Draw.reset();
        }
    }

    @Override
    public void createIcons(MultiPacker packer) {
        super.createIcons(packer);

        var toOutline = new Seq<TextureRegion>();
        getRegionsToOutline(toOutline);
        var atlas = Core.atlas.find(name + "-hit").asAtlas();
            if(atlas != null){
                String regionName = atlas.name;
                Pixmap outlined = Pixmaps.outline(Core.atlas.getPixmap(region), outlineColor, outlineRadius);

                Drawf.checkBleed(outlined);

                packer.add(MultiPacker.PageType.main, regionName + "-outline", outlined);
            }
    }

    public float getTimingSine(JumpingAI ai){
        return Mathf.sin(ai.timing);
    }
}
