package classicMod.library.unitType;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.Time;
import classicMod.content.RFx;
import classicMod.library.ai.JumpingAI;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.graphics.*;
import mindustry.type.UnitType;
import mindustry.world.meta.*;

import static classicMod.content.ExtendedStat.squaredRange;
import static classicMod.library.ui.UIExtended.fdelta;
import static mindustry.Vars.tilesize;

public class JumpingUnitType extends UnitType {

    public TextureRegion ouch, outlineOuchRegion, body, bodyOutline;

    public boolean onlySlide = false;

    public Effect StompEffect = RFx.dynamicWaveBig;
    public Effect StompExplosionEffect = RFx.dynamicSmallBomb;
    public Color StompColor = Color.valueOf("ffd27e");
    public boolean StompExplosion = false;

    public float healPercent = 0f;
    public float healRange = 0f;
    boolean flip;

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
        if (healPercent > 0f && healRange > 0f) {
            stats.add(Stat.healing, healPercent, StatUnit.percent);
            stats.add(Stat.range, squaredRange((healRange / tilesize), StatUnit.blocks));
        }
    }

    @Override
    public void update(Unit unit) {
        if (unit.controller() instanceof JumpingAI ai) {
            ai.timing += 0.15f * Time.delta;
            if (getTimingSine(ai) > 0f) {
                ai.timingY -= 0.275f * Time.delta;
            }
            if (ai.hit) ai.hitDelay += fdelta(100f, 60f);
        }
    }

    @Override
    public void init() {
        super.init();

        ouch = Core.atlas.find(name + "-hit");
        body = Core.atlas.find(name);
        outlineOuchRegion = Core.atlas.find(name + "-hit-outline");
        bodyOutline = Core.atlas.find(name + "-outline");
    }

    @Override
    public void draw(Unit unit) {
        if (unit.controller() instanceof JumpingAI ai) {
            ouch = Core.atlas.find(name + "-hit");
            body = Core.atlas.find(name);
            outlineOuchRegion = Core.atlas.find(name + "-hit-outline");
            bodyOutline = Core.atlas.find(name + "-outline");
            Draw.reset();

            int direction = Mathf.round((unit.rotation / 90) % 4);
            if (!(direction == 1 || direction == 3)) flip = (direction == 0);
            Draw.xscl = Mathf.sign(flip);
            var sine = Mathf.sin(ai.timing);
            Draw.z(Layer.groundUnit);

            applyColor(unit);
            if (sine < -0.85f) {
                ai.timing = 2f;
                ai.timingY = 0.5f;
            }
            if ((sine > 0f && !ai.stopMoving) && !onlySlide) {
                var Ysine = Mathf.sin(Mathf.sin(ai.timingY) * 3);
                if (!ai.hit) {
                    Draw.rect(body, unit.x, unit.y + 2 + Ysine * 3, (((float) body.width / 2) + sine * 5) * Mathf.sign(flip), ((float) body.height / 2) - sine * 10);
                }
                if (ai.hit) {
                    drawOuchOutline(unit, Mathf.sign(flip));
                    Draw.rect(ouch, unit.x, unit.y + 2 + Ysine * 3, (((float) ouch.width / 2) + sine * 5) * Mathf.sign(flip), ((float) ouch.height / 2) - sine * 10);
                }
            } else {
                if (!ai.hit) {
                    Draw.rect(body, unit.x, unit.y + 2, (((float) body.width / 2) * Mathf.sign(flip)), (float) body.height / 2);
                }
                if (ai.hit) {
                    drawOuchOutline(unit, Mathf.sign(flip));
                    Draw.rect(ouch, unit.x, unit.y + 2, (((float) ouch.width / 2) * Mathf.sign(flip)), ((float) ouch.height / 2));
                }
            }

            Draw.reset();
            Draw.xscl = -1f;
        }
    }

    public void drawOuchOutline(Unit unit, float xscl) {
        if (Core.atlas.isFound(outlineOuchRegion)) {
            applyColor(unit);
            applyOutlineColor(unit);
            Draw.rect(outlineOuchRegion, unit.x, unit.y + 2, (((float) ouch.width / 2) * xscl), (float) ouch.height / 2);
            Draw.reset();
        }
    }

    @Override
    public void createIcons(MultiPacker packer) {
        super.createIcons(packer);

        var atlasA = Core.atlas.find(name + "-hit").asAtlas();
        if (atlasA != null) {
            String regionName = atlasA.name;
            Pixmap outlined = Pixmaps.outline(Core.atlas.getPixmap(atlasA), outlineColor, outlineRadius);

            Drawf.checkBleed(outlined);

            packer.add(MultiPacker.PageType.main, regionName + "-outline", outlined);
        }

        var atlasB = Core.atlas.find(name).asAtlas();
        if (atlasB != null) {
            String regionName = atlasB.name;
            Pixmap outlined = Pixmaps.outline(Core.atlas.getPixmap(Core.atlas.find(name)), outlineColor, outlineRadius);

            Drawf.checkBleed(outlined);

            packer.add(MultiPacker.PageType.main, regionName + "-outline", outlined);
        }
    }

    public float getTimingSine(JumpingAI ai) {
        return Mathf.sin(ai.timing);
    }
}
