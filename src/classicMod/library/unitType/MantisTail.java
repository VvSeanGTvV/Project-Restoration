package classicMod.library.unitType;


import arc.Core;
import arc.func.Func;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.*;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.graphics.*;
import mindustry.type.Weapon;
import mindustry.world.blocks.environment.Floor;

import static classicMod.content.ClassicVars.empty;
import static mindustry.Vars.world;

public class MantisTail implements Cloneable {
    public float layerOffset;
    public float tailRotationSpeed;
    public String spriteName;
    public TextureRegion TailBegin, TailMiddle, TailEnd;
    public TextureRegion TailBody, TailBodyEnd;
    public TextureRegion TailBodyOutline, TailBodyEndOutline;
    public Func<MantisTail, MantisMountTail> mountType;
    public float timer, lastRot, lastRotEnd, rot, rotEnd;

    public float shadowElevation = -1f;
    public float shadowElevationScl = 1f;


    public Vec2 TailOffsetBegin = new Vec2(0f, -7.25f);
    public float[] AngleOffset = new float[]{0f, 0f};
    public float padding = 0f;
    public float offsetX = 0f;

    public MantisTail(String name){
        spriteName = name;
        mountType = MantisMountTail::new;
    }


    public void update(Unit unit) {
        timer += Time.delta / 20f;
        rot = Mathf.slerpDelta(rot, unit.rotation, 0.35f + tailRotationSpeed);
        rotEnd = Mathf.slerpDelta(rotEnd, unit.rotation, 0.15f + tailRotationSpeed);

        lastRot = (unit.rotation >= 180f) ? rot - 360f : rot;
        lastRotEnd = (unit.rotation >= 180f) ? rotEnd - 360f : rotEnd;
    }

    public void drawOutline(Unit unit){
        float lRot0 = lastRot - unit.rotation;
        float yBody = (TailBody.height / 7.5f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot0 - 90, 0, yBody);

        if (Core.atlas.isFound(TailBodyOutline)) {
            Draw.rect(TailBodyOutline, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot0 - 90);
            Draw.reset();
        }
        float lRot1 = lastRotEnd - unit.rotation;
        yBody += (TailBodyEnd.height / 6.15f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot1 - 90, 0, yBody);
        if (Core.atlas.isFound(TailBodyEndOutline)) {
            Draw.rect(TailBodyEndOutline, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot1 - 90);
            Draw.reset();
        }
    }

    public void drawBodyTail(Unit unit){
        float lRot0 = lastRot - unit.rotation;
        float yBody = (TailBody.height / 7.5f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot0 - 90, 0, yBody);
        Draw.rect(TailBody, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot0 - 90);

        float lRot1 = lastRotEnd - unit.rotation;
        yBody += (TailBodyEnd.height / 6.15f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot1 - 90, 0, yBody);
        Draw.rect(TailBodyEnd, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot1 - 90);
    }

    public void drawTail(Unit unit) {
        var sine0 = Mathf.sin(timer) * 10f;
        float sclr = 1f;
        float unitRot = ((unit.rotation >= 180f) ? unit.rotation - 360f : unit.rotation);
        float rotation = lastRot;
        float rotationOffset = -90f;
        Tmp.v1.trns(unitRot + rotationOffset, TailOffsetBegin.x, TailOffsetBegin.y);
        Draw.rect(TailBegin, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unitRot + rotationOffset);

        float lRot0 = 0f;
        Tmp.v1.trns(rotation + sine0 + lRot0 + AngleOffset[0] + rotationOffset, offsetX - ((lRot0 / 10f) + sine0 / 5f), ((TailBegin.height / 8f) + 6.6f + padding) * sclr);
        Draw.rect(TailMiddle, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, rotation + lRot0 + sine0 + AngleOffset[0] + rotationOffset);
        drawShadowTexture(unit, TailMiddle, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, rotation + lRot0 + sine0 + AngleOffset[0] + rotationOffset);

        Tmp.v1.trns(rotation + sine0 + lRot0 + AngleOffset[1] + rotationOffset,offsetX - ((lRot0 / 20f) + sine0 / 5f), ((TailMiddle.height / 4f) + 0.15f + padding) * sclr);
        Draw.rect(TailEnd, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, rotation + lRot0 + sine0 + sine0 + AngleOffset[1] + rotationOffset);
        drawShadowTexture(unit, TailEnd, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, rotation + lRot0 + sine0 + sine0 + AngleOffset[1] + rotationOffset);
    }

    public void drawShadow(Unit unit) {
        //drawShadowTexture(unit, region, unit.x, unit.y, unit.rotation - 90);

        float lRot0 = lastRot - unit.rotation;
        float yBody = (TailBody.height / 7.5f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot0 - 90, 0, yBody);

        drawShadowTexture(unit, TailBody, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot0 - 90);
        float lRot1 = lastRotEnd - unit.rotation;
        yBody += (TailBodyEnd.height / 6.15f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot1 - 90, 0, yBody);

        drawShadowTexture(unit, TailBodyEndOutline, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot1 - 90);
    }

    public void drawShadowTexture(Unit unit, TextureRegion region, float x1, float y1, float rot1) {
        float shadowTX = -12, shadowTY = -13;
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


    public void load() {
        TailBegin = Core.atlas.find(spriteName + "-0");
        TailMiddle = Core.atlas.find(spriteName + "-1");
        TailEnd = Core.atlas.find(spriteName + "-2");

        TailBody = Core.atlas.find(spriteName + "-mid");
        TailBodyEnd = Core.atlas.find(spriteName + "-end");

        TailBodyOutline = Core.atlas.find(spriteName + "-mid-outline");
        TailBodyEndOutline = Core.atlas.find(spriteName + "-end-outline");
    }

    @Override
    public MantisTail clone() {
        try {
            MantisTail clone = (MantisTail) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static class MantisMountTail {
        /** Float variable associated with this mount */
        public MantisTail tail;

        public MantisMountTail(MantisTail tail) {
            this.tail = tail;
        }
    }
}
