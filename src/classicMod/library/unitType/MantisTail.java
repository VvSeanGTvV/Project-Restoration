package classicMod.library.unitType;


import arc.Core;
import arc.func.Func;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.*;
import mindustry.entities.Sized;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.graphics.*;
import mindustry.type.Weapon;
import mindustry.type.weapons.RepairBeamWeapon;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.units.RepairTurret;

import static mindustry.Vars.world;

public class MantisTail extends Weapon {
    public TextureRegion TailBegin, TailMiddle, TailEnd;
    public TextureRegion TailBody, TailBodyEnd;
    public TextureRegion TailBodyOutline, TailBodyEndOutline;


    public float shadowElevation = -1f;
    public float shadowElevationScl = 1f;


    public Vec2 TailOffsetBegin = new Vec2(0f, -7.25f);
    public float[] AngleOffset = new float[]{0f, 0f};
    public float padding = 0f;
    public float offsetX = 0f;

    public MantisTail(){
        super("skat");
    }

    {
        //must be >0 to prevent various bugs
        reload = 1f;
        predictTarget = false;
        autoTarget = false;
        controllable = false;
        rotate = false;
        useAmmo = false;
        mountType = MantisMountTail::new;
        recoil = 0f;
        noAttack = true;
        useAttackRange = false;
    }

    @Override
    public void update(Unit unit, WeaponMount mount) {
        super.update(unit, mount);
        MantisMountTail tail = (MantisMountTail)mount;

        tail.timer += Time.delta / 20f;
        tail.rot = Mathf.slerpDelta(tail.rot, unit.rotation, 0.35f);
        tail.rotEnd = Mathf.slerpDelta(tail.rotEnd, unit.rotation, 0.15f);

        tail.lastRot = (unit.rotation >= 180f) ? tail.rot - 360f : tail.rot;
        tail.lastRotEnd = (unit.rotation >= 180f) ? tail.rotEnd - 360f : tail.rotEnd;
    }

    @Override
    protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation) {
        //Do nothing HAHAHAHAHAHA sorry
    }

    public void drawOutline(Unit unit, MantisMountTail tail){
        float lRot0 = tail.lastRot - unit.rotation;
        float yBody = (TailBody.height / 7.5f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot0 - 90, 0, yBody);

        if (Core.atlas.isFound(TailBodyOutline)) {
            Draw.rect(TailBodyOutline, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot0 - 90);
            Draw.reset();
        }
        float lRot1 = tail.lastRotEnd - unit.rotation;
        yBody += (TailBodyEnd.height / 6.15f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot1 - 90, 0, yBody);
        if (Core.atlas.isFound(TailBodyEndOutline)) {
            Draw.rect(TailBodyEndOutline, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot1 - 90);
            Draw.reset();
        }
    }

    public void drawBodyTail(Unit unit, MantisMountTail tail){
        float lRot0 = tail.lastRot - unit.rotation;
        float yBody = (TailBody.height / 7.5f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot0 - 90, 0, yBody);
        Draw.rect(TailBody, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot0 - 90);

        float lRot1 = tail.lastRotEnd - unit.rotation;
        yBody += (TailBodyEnd.height / 6.15f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot1 - 90, 0, yBody);
        Draw.rect(TailBodyEnd, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot1 - 90);
    }

    public void drawTail(Unit unit, MantisMountTail tail) {
        var sine0 = Mathf.sin(tail.timer) * 10f;
        float sclr = 1f;
        float unitRot = ((unit.rotation >= 180f) ? unit.rotation - 360f : unit.rotation);
        float rotation = tail.lastRot; //((unit.rotation >= 180f) ? unit.rotation - 360f : unit.rotation);
        float rotationOffset = -90f;
        Tmp.v1.trns(unitRot + rotationOffset, TailOffsetBegin.x, TailOffsetBegin.y);
        Draw.rect(TailBegin, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unitRot + rotationOffset);

        float lRot0 = 0f;
        Tmp.v1.trns(rotation + sine0 + lRot0 + AngleOffset[0] + rotationOffset, offsetX - ((lRot0 / 10f) + sine0 / 5f), ((TailBegin.height / 8f) + 6.6f + padding) * sclr);
        Draw.rect(TailMiddle, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, rotation + lRot0 + sine0 + AngleOffset[0] + rotationOffset);
        drawShadowTexture(unit, TailMiddle, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, rotation + lRot0 + sine0 + AngleOffset[0] + rotationOffset);

        sclr = 1f;
        Tmp.v1.trns(rotation + sine0 + lRot0 + AngleOffset[1] + rotationOffset,offsetX - ((lRot0 / 20f) + sine0 / 5f), ((TailMiddle.height / 4f) + 0.15f + padding) * sclr);
        Draw.rect(TailEnd, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, rotation + lRot0 + sine0 + sine0 + AngleOffset[1] + rotationOffset);
        drawShadowTexture(unit, TailEnd, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, rotation + lRot0 + sine0 + sine0 + AngleOffset[1] + rotationOffset);
    }

    public void drawShadow(Unit unit, MantisMountTail tail) {
        drawShadowTexture(unit, region, unit.x, unit.y, unit.rotation - 90);

        float lRot0 = tail.lastRot - unit.rotation;
        float yBody = (TailBody.height / 7.5f) + 0f;
        Tmp.v1.trns(unit.rotation + lRot0 - 90, 0, yBody);

        drawShadowTexture(unit, TailBody, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + lRot0 - 90);
        float lRot1 = tail.lastRotEnd - unit.rotation;
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

    @Override
    public void draw(Unit unit, WeaponMount mount){
        super.draw(unit, mount);
        MantisMountTail tail = (MantisMountTail)mount;

        float z = Draw.z();
        Draw.z(z + layerOffset);

        if(shadow > 0){
            drawShadow(unit, tail);
        }

        unit.type.applyColor(unit);

        Draw.z(Layer.flyingUnit - 2f);
        if(shadow <= 0){
            drawShadow(unit, tail);
        }
        Draw.z(Layer.flyingUnit + 1f);
        drawBodyTail(unit, tail);
        drawTail(unit, tail);
        Draw.z(Layer.flyingUnit - 1f);
        drawOutline(unit, tail);

        Draw.z(z);
    }

    @Override
    public void load() {
        super.load();
        String name = "restored-mind-skat";
        TailBegin = Core.atlas.find(name + "-tail-0");
        TailMiddle = Core.atlas.find(name + "-tail-1");
        TailEnd = Core.atlas.find(name + "-tail-2");

        TailBody = Core.atlas.find(name + "-tail-mid");
        TailBodyEnd = Core.atlas.find(name + "-tail-end");

        TailBodyOutline = Core.atlas.find(name + "-tail-mid-outline");
        TailBodyEndOutline = Core.atlas.find(name + "-tail-end-outline");
    }

    public static class MantisMountTail extends WeaponMount{
        /** Float variable associated with this mount */
        public float timer, lastRot, lastRotEnd, rot, rotEnd;
        public MantisMountTail(Weapon weapon){
            super(weapon);
        }
    }
}
