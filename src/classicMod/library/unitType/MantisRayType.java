package classicMod.library.unitType;

import arc.Core;
import arc.func.Func;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.*;
import classicMod.library.drawCustom.BlendingCustom;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.weapons.RepairBeamWeapon;
import mindustry.world.blocks.environment.Floor;

import static mindustry.Vars.world;

public class MantisRayType extends UnitType {

    public Seq<MantisTail> Tails = new Seq<>();
    public TextureRegion eye;

    public MantisRayType(String name) {
        super(name);
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);
        Tails.each(tail -> tail.update(unit));
    }

    @Override
    public void drawShadow(Unit unit) {
        drawShadowTexture(unit, region, unit.x, unit.y, unit.rotation - 90);
    }

    @Override
    public void load() {
        super.load();
        eye = Core.atlas.find(name + "-eye");
        for (MantisTail Tail : Tails) {
            Tail.load();
        }
    }

    @Override
    public void draw(Unit unit) {
        //super.draw(unit);

        Draw.z(Layer.flyingUnit - 1f);
        drawShadow(unit);

        Draw.z(Layer.flyingUnit);
        drawOutline(unit);
        
        drawBody(unit);
        for (MantisTail Tail : Tails) {
            drawWholeTail(unit, Tail);
        }
        Draw.rect(eye, unit.x, unit.y, unit.rotation - 90);
    }

    public void drawOutline(Unit unit, MantisTail Tail){
        float lRot0 = Tail.lastRot - unit.rotation;
        float yBody = (Tail.TailBody.height / 7.5f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot0 - 90, 0, yBody);

        if (Core.atlas.isFound(Tail.TailBodyOutline)) {
            Draw.rect(Tail.TailBodyOutline, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot0 - 90);
            Draw.reset();
        }
        float lRot1 = Tail.lastRotEnd - unit.rotation;
        yBody += (Tail.TailBodyEnd.height / 6.15f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot1 - 90, 0, yBody);
        if (Core.atlas.isFound(Tail.TailBodyEndOutline)) {
            Draw.rect(Tail.TailBodyEndOutline, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot1 - 90);
            Draw.reset();
        }
    }

    public void drawBodyTail(Unit unit, MantisTail Tail){
        float lRot0 = Tail.lastRot - unit.rotation;
        float yBody = (Tail.TailBody.height / 7.5f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot0 - 90, 0, yBody);
        Draw.rect(Tail.TailBody, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot0 - 90);

        float lRot1 = Tail.lastRotEnd - unit.rotation;
        yBody += (Tail.TailBodyEnd.height / 6.15f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot1 - 90, 0, yBody);
        Draw.rect(Tail.TailBodyEnd, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot1 - 90);
    }

    public void drawTail(Unit unit, MantisTail Tail) {
        var sine0 = Mathf.sin(Tail.timer) * 10f;
        float sclr = 1f;
        float unitRot = ((unit.rotation >= 180f) ? unit.rotation - 360f : unit.rotation);
        float rotation = Tail.lastRot;
        float rotationOffset = -90f;
        Tmp.v1.trns(unitRot + rotationOffset, Tail.TailOffsetBegin.x, Tail.TailOffsetBegin.y);
        Draw.rect(Tail.TailBegin, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unitRot + rotationOffset);

        float lRot0 = 0f;
        Tmp.v1.trns(rotation + sine0 + lRot0 + Tail.AngleOffset[0] + rotationOffset, Tail.offsetX - ((lRot0 / 10f) + sine0 / 5f), ((Tail.TailBegin.height / 8f) + 6.6f + Tail.padding) * sclr);
        Draw.rect(Tail.TailMiddle, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, rotation + lRot0 + sine0 + Tail.AngleOffset[0] + rotationOffset);
        drawShadowTexture(unit, Tail.TailMiddle, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, rotation + lRot0 + sine0 + Tail.AngleOffset[0] + rotationOffset);

        Tmp.v1.trns(rotation + sine0 + lRot0 + Tail.AngleOffset[1] + rotationOffset,Tail.offsetX - ((lRot0 / 20f) + sine0 / 5f), ((Tail.TailMiddle.height / 4f) + 0.15f + Tail.padding) * sclr);
        Draw.rect(Tail.TailEnd, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, rotation + lRot0 + sine0 + sine0 + Tail.AngleOffset[1] + rotationOffset);
        drawShadowTexture(unit, Tail.TailEnd, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, rotation + lRot0 + sine0 + sine0 + Tail.AngleOffset[1] + rotationOffset);
    }

    public void drawTailShadow(Unit unit, MantisTail Tail) {
        //drawShadowTexture(unit, region, unit.x, unit.y, unit.rotation - 90);

        float lRot0 = Tail.lastRot - unit.rotation;
        float yBody = (Tail.TailBody.height / 7.5f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot0 - 90, 0, yBody);

        drawShadowTexture(unit, Tail.TailBody, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot0 - 90);
        float lRot1 = Tail.lastRotEnd - unit.rotation;
        yBody += (Tail.TailBodyEnd.height / 6.15f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot1 - 90, 0, yBody);

        drawShadowTexture(unit, Tail.TailBodyEndOutline, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot1 - 90);
    }

    public void drawWholeTail(Unit unit, MantisTail Tail){
        float z = Draw.z();
        Draw.z(z + Tail.layerOffset);
        drawTailShadow(unit, Tail);

        unit.type.applyColor(unit);

        drawBodyTail(unit, Tail);
        drawOutline(unit, Tail);
        drawTail(unit, Tail);

        Draw.z(z);
    }


    public void drawBody(Unit unit) {
        applyColor(unit);

        Draw.rect(region, unit.x, unit.y, unit.rotation - 90);
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

    public void drawShadowTexture(Unit unit, TextureRegion region, float x1, float y1, float rot1) {
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
}
