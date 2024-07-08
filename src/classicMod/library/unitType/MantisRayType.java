package classicMod.library.unitType;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.UnitType;
import mindustry.world.blocks.environment.Floor;

import static mindustry.Vars.world;

public class MantisRayType extends UnitType {

    public TextureRegion TailBegin;
    public TextureRegion TailMiddle;
    public TextureRegion TailEnd;

    public TextureRegion TailBody;
    public TextureRegion TailBodyEnd;

    public TextureRegion TailBodyOutline, TailBodyEndOutline;

    public Vec2 TailOffsetBegin = new Vec2(0f, -7.25f);
    public float[] AngleOffset = new float[]{0f, 0f};
    public float padding = 0f;
    public float offsetX = 0f;
    private float timer;

    public MantisRayType(String name) {
        super(name);
    }


    @Override
    public void update(Unit unit) {
        super.update(unit);
        this.timer += Time.delta / 20f;
    }

    @Override
    public void load() {
        super.load();
        TailBegin = Core.atlas.find(name + "-tail-0");
        TailMiddle = Core.atlas.find(name + "-tail-1");
        TailEnd = Core.atlas.find(name + "-tail-2");

        TailBody = Core.atlas.find(name + "-tail-mid");
        TailBodyEnd = Core.atlas.find(name + "-tail-end");

        TailBodyOutline = Core.atlas.find(name + "-tail-mid-outline");
        TailBodyEndOutline = Core.atlas.find(name + "-tail-end-outline");
    }

    @Override
    public void draw(Unit unit) {
        super.draw(unit);


        Draw.z(Layer.flyingUnit - 1f);
        drawShadow(unit);
        Draw.z(Layer.flyingUnit);


        Draw.z(Layer.flyingUnit);
        drawOutline(unit);
        drawBody(unit);
        drawTail(unit);

        //Draw.rect(TailEnd, unit.x + TailOffset[2].x, unit.y + TailOffset[2].y);
    }

    public void drawTail(Unit unit){
        var sine0 = Mathf.sin(this.timer) * 10f;
        float sclr = 1f;
        Tmp.v1.trns(unit.rotation - 90, TailOffsetBegin.x, TailOffsetBegin.y);
        Draw.rect(TailBegin, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation - 90);

        Tmp.v1.trns(unit.rotation + sine0 + AngleOffset[0] - 90, offsetX - (sine0 / 5f), ((TailBegin.height / 8f) + 6.6f + padding) * sclr);
        Draw.rect(TailMiddle, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + sine0 + AngleOffset[0] - 90);
        drawTailShadow(unit, TailMiddle, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + sine0 + AngleOffset[0] - 90);

        sclr = 1f;
        Tmp.v1.trns(unit.rotation + sine0 + AngleOffset[1] - 90, offsetX - (sine0 / 5f) - (Mathf.sin(this.timer) / 2f), ((TailMiddle.height / 4f) + 0.15f + padding) * sclr);
        Draw.rect(TailEnd, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + sine0 + sine0 + AngleOffset[1] - 90);
        drawTailShadow(unit, TailEnd, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + sine0 + sine0 + AngleOffset[1] - 90);
    }

    float lastRot = 0f;
    float lastRotEnd = 0f;
    public void drawBody(Unit unit){
        applyColor(unit);

        Draw.rect(region, unit.x, unit.y, unit.rotation - 90);

        lastRot = Mathf.lerp(lastRot, unit.rotation, 0.1f);
        float lRot0 = lastRot - unit.rotation;
        float yBody = (TailBody.height / 7.5f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot0 - 90, 0, yBody);
        Draw.rect(TailBody, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot0 - 90);

        lastRotEnd = Mathf.lerp(lastRotEnd, unit.rotation, 0.05f);
        float lRot1 = lastRotEnd - unit.rotation;
        yBody += (TailBodyEnd.height / 6.15f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot1 - 90, 0, yBody);
        Draw.rect(TailBodyEnd, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot1 - 90);
    }

    public void drawOutline(Unit unit){
        Draw.reset();

        if(Core.atlas.isFound(outlineRegion)){
            applyColor(unit);
            applyOutlineColor(unit);
            Draw.rect(outlineRegion, unit.x, unit.y, unit.rotation - 90);
            Draw.reset();
        }
        float lRot0 = lastRot - unit.rotation;
        float yBody = (TailBody.height / 7.5f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot0 - 90, 0, yBody);
        if(Core.atlas.isFound(TailBodyOutline)){
            applyColor(unit);
            applyOutlineColor(unit);
            Draw.rect(TailBodyOutline, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot0 - 90);
            Draw.reset();
        }
        float lRot1 = lastRotEnd - unit.rotation;
        yBody += (TailBodyEnd.height / 6.15f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot1 - 90, 0, yBody);
        if(Core.atlas.isFound(TailBodyEndOutline)){
            applyColor(unit);
            applyOutlineColor(unit);
            Draw.rect(TailBodyEndOutline, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot1 - 90);
            Draw.reset();
        }
    }

    @Override
    public void createIcons(MultiPacker packer) {
        super.createIcons(packer);

        var atlasA = Core.atlas.find(name + "-tail-mid").asAtlas();
        if (atlasA != null) {
            String regionName = atlasA.name;
            Pixmap outlined = Pixmaps.outline(Core.atlas.getPixmap(atlasA), outlineColor, outlineRadius);

            Drawf.checkBleed(outlined);

            packer.add(MultiPacker.PageType.main, regionName + "-outline", outlined);
        }

        var atlasB = Core.atlas.find(name + "-tail-end").asAtlas();
        if (atlasB != null) {
            String regionName = atlasB.name;
            Pixmap outlined = Pixmaps.outline(Core.atlas.getPixmap(atlasB), outlineColor, outlineRadius);

            Drawf.checkBleed(outlined);

            packer.add(MultiPacker.PageType.main, regionName + "-outline", outlined);
        }
    }

    public void drawTailShadow(Unit unit, TextureRegion region, float x1, float y1, float rot1) {
        Draw.z(Layer.flyingUnit - 1f);
        float e = Mathf.clamp(unit.elevation, shadowElevation, 1f) * shadowElevationScl * (1f - unit.drownTime);
        float x = x1 + shadowTX * e, y = y1 + shadowTY * e;
        Floor floor = world.floorWorld(x, y);

        float dest = floor.canShadow ? 1f : 0f;
        //yes, this updates state in draw()... which isn't a problem, because I don't want it to be obvious anyway
        unit.shadowAlpha = unit.shadowAlpha < 0 ? dest : Mathf.approachDelta(unit.shadowAlpha, dest, 0.11f);
        Draw.color(Pal.shadow, Pal.shadow.a * unit.shadowAlpha);

        Draw.rect(region, x1 + shadowTX * e, y1 + shadowTY * e, rot1);
        Draw.color();
        Draw.z(Layer.flyingUnit);
    }

    /*@Override
    public void createIcons(MultiPacker packer) {
        super.createIcons(packer);

        var atlasB = Core.atlas.find(name + "-tail-2").asAtlas();
        if (atlasB != null) {
            String regionName = atlasB.name;
            Pixmap outlined = Pixmaps.outline(Core.atlas.getPixmap(Core.atlas.find(name)), outlineColor, outlineRadius);

            Drawf.checkBleed(outlined);

            packer.add(MultiPacker.PageType.main, regionName + "-outline", outlined);
        }
    }*/
}
