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
import classicMod.library.unitType.MantisTail.*;

import static mindustry.Vars.world;

public class MantisRayType extends UnitType {

    public MantisMountTail[] mountTails = new MantisMountTail[0];
    public Seq<MantisTail> Tails = new Seq<>();
    public TextureRegion eye;

    private boolean isSet;

    public MantisRayType(String name) {
        super(name);
    }

    void setupMantis(MantisRayType def){
        isSet = true;
        mountTails = new MantisMountTail[def.Tails.size];
        for(int i = 0; i < mountTails.length; i++){
            mountTails[i] = def.Tails.get(i).mountType.get(def.Tails.get(i));
        }
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);

        //Tails.each(tail -> tail.update(unit));
        for (MantisMountTail mount : mountTails) {
            mount.tail.update(unit, mount);
        }
    }

    @Override
    public void init() {
        super.init();
        if (!isSet) setupMantis(this);
    }

    @Override
    public void drawShadow(Unit unit) {
        drawShadowTexture(unit, region, unit.x, unit.y, unit.rotation - 90);
    }

    @Override
    public void load() {
        super.load();
        eye = Core.atlas.find(name + "-eye");
        Tails.each(MantisTail::load);
    }

    @Override
    public void draw(Unit unit) {
        //super.draw(unit);

        Draw.z(Layer.flyingUnit - 1f);
        drawShadow(unit);

        Draw.z(Layer.flyingUnit);
        drawOutline(unit);
        
        drawBody(unit);
        for (MantisMountTail Tail : mountTails) {
            drawWholeTail(unit, Tail);
        }
        Draw.rect(eye, unit.x, unit.y, unit.rotation - 90);
    }

    public void drawOutline(Unit unit, MantisMountTail Tail){
        float lRot0 = Tail.lastRot - unit.rotation;
        float yBody = (Tail.tail.TailBody.height / 7.5f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot0 - 90, 0, yBody);

        if (Core.atlas.isFound(Tail.tail.TailBodyOutline)) {
            Draw.rect(Tail.tail.TailBodyOutline, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot0 - 90);
            Draw.reset();
        }
        float lRot1 = Tail.lastRotEnd - unit.rotation;
        yBody += (Tail.tail.TailBodyEnd.height / 6.15f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot1 - 90, 0, yBody);
        if (Core.atlas.isFound(Tail.tail.TailBodyEndOutline)) {
            Draw.rect(Tail.tail.TailBodyEndOutline, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot1 - 90);
            Draw.reset();
        }
    }

    public void drawBodyTail(Unit unit, MantisMountTail Tail){
        float lRot0 = Tail.lastRot - unit.rotation;
        float yBody = (Tail.tail.TailBody.height / 7.5f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot0 - 90, 0, yBody);
        Draw.rect(Tail.tail.TailBody, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot0 - 90);

        float lRot1 = Tail.lastRotEnd - unit.rotation;
        yBody += (Tail.tail.TailBodyEnd.height / 6.15f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot1 - 90, 0, yBody);
        Draw.rect(Tail.tail.TailBodyEnd, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot1 - 90);
    }

    public void drawTail(Unit unit, MantisMountTail Tail) {
        var sine0 = Mathf.sin(Tail.timer) * 10f;
        float sclr = 1f;
        float unitRot = ((unit.rotation >= 180f) ? unit.rotation - 360f : unit.rotation);
        float rotation = Tail.lastRot;
        float rotationOffset = -90f;
        Tmp.v1.trns(unitRot + rotationOffset, Tail.tail.TailOffsetBegin.x, Tail.tail.TailOffsetBegin.y);
        Draw.rect(Tail.tail.TailBegin, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unitRot + rotationOffset);

        float lRot0 = 0f;
        Tmp.v1.trns(rotation + sine0 + lRot0 + Tail.tail.AngleOffset[0] + rotationOffset, Tail.tail.offsetX - ((lRot0 / 10f) + sine0 / 5f), ((Tail.tail.TailBegin.height / 8f) + 6.6f + Tail.tail.padding) * sclr);
        Draw.rect(Tail.tail.TailMiddle, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, rotation + lRot0 + sine0 + Tail.tail.AngleOffset[0] + rotationOffset);
        drawShadowTexture(unit, Tail.tail.TailMiddle, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, rotation + lRot0 + sine0 + Tail.tail.AngleOffset[0] + rotationOffset);

        Tmp.v1.trns(rotation + sine0 + lRot0 + Tail.tail.AngleOffset[1] + rotationOffset,Tail.tail.offsetX - ((lRot0 / 20f) + sine0 / 5f), ((Tail.tail.TailMiddle.height / 4f) + 0.15f + Tail.tail.padding) * sclr);
        Draw.rect(Tail.tail.TailEnd, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, rotation + lRot0 + sine0 + sine0 + Tail.tail.AngleOffset[1] + rotationOffset);
        drawShadowTexture(unit, Tail.tail.TailEnd, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, rotation + lRot0 + sine0 + sine0 + Tail.tail.AngleOffset[1] + rotationOffset);
    }

    public void drawTailShadow(Unit unit, MantisMountTail Tail) {
        //drawShadowTexture(unit, region, unit.x, unit.y, unit.rotation - 90);

        float lRot0 = Tail.lastRot - unit.rotation;
        float yBody = (Tail.tail.TailBody.height / 7.5f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot0 - 90, 0, yBody);

        drawShadowTexture(unit, Tail.tail.TailBody, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot0 - 90);
        float lRot1 = Tail.lastRotEnd - unit.rotation;
        yBody += (Tail.tail.TailBodyEnd.height / 6.15f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot1 - 90, 0, yBody);

        drawShadowTexture(unit, Tail.tail.TailBodyEndOutline, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot1 - 90);
    }

    public void drawWholeTail(Unit unit, MantisMountTail Tail) {
        float z = Draw.z();
        Draw.z(z + Tail.tail.layerOffset);
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
