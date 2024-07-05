package classicMod.library;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Vec2;
import arc.util.*;
import mindustry.entities.Leg;
import mindustry.entities.abilities.Ability;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.UnitType;

import static mindustry.Vars.player;

public class testUnitType extends UnitType {
    private static final Vec2 legOffset = new Vec2();
    public float legTrns = 1f, maxStretch = 1.75f, landShake = 0f;
    public float visualElevation = -1f;
    public float legStraightLength = 1f;
    /** If true, legs are locked to the base of the unit instead of being on an implicit rotating "mount". */
    public boolean lockLegBase = false;
    public float baseLegStraightness, legStraightness;
    /** TODO neither of these appear to do much */
    public boolean flipBackLegs = true, flipLegSide = false;

    public testUnitType(String name) {
        super(name);
    }

    public <T extends Unit & Legsc> void drawLegs(T unit){
        applyColor(unit);
        Tmp.c3.set(Draw.getMixColor());

        Leg[] legs = unit.legs();

        float ssize = footRegion.width * Draw.scl * 1.5f;
        float rotation = unit.baseRotation();
        float invDrown = 1f - unit.drownTime;

        if(footRegion.found()){
            for(Leg leg : legs){
                Drawf.shadow(leg.base.x, leg.base.y, ssize, invDrown);
            }
        }

        //legs are drawn front first
        for(int j = legs.length - 1; j >= 0; j--){
            int i = (j % 2 == 0 ? j/2 : legs.length - 1 - j/2);
            Leg leg = legs[i];
            float angle = unit.legAngle(i);
            boolean flip = i >= legs.length/2f;
            int flips = Mathf.sign(flip);

            Vec2 position = unit.legOffset(legOffset, i).add(unit);

            Tmp.v1.set(leg.base).sub(leg.joint).inv().setLength(legExtension);

            if(footRegion.found() && leg.moving && visualElevation > 0){
                float scl = visualElevation * invDrown;
                float elev = Mathf.slope(1f - leg.stage) * scl;
                Draw.color(Pal.shadow);
                Draw.rect(footRegion, leg.base.x + shadowTX * elev, leg.base.y + shadowTY * elev, position.angleTo(leg.base));
                Draw.color();
            }

            Draw.mixcol(Tmp.c3, Tmp.c3.a);

            if(footRegion.found()){
                Draw.rect(footRegion, leg.base.x, leg.base.y, position.angleTo(leg.base));
            }

            Lines.stroke(legRegion.height * Draw.scl * flips);
            Lines.line(legRegion, position.x, position.y, leg.joint.x, leg.joint.y, false);

            Lines.stroke(legBaseRegion.height * Draw.scl * flips);
            Lines.line(legBaseRegion, leg.joint.x + Tmp.v1.x, leg.joint.y + Tmp.v1.y, leg.base.x, leg.base.y, false);

            if(jointRegion.found()){
                Draw.rect(jointRegion, leg.joint.x, leg.joint.y);
            }

            if(baseJointRegion.found()){
                Draw.rect(baseJointRegion, position.x, position.y, rotation);
            }
        }

        if(baseRegion.found()){
            Draw.rect(baseRegion, unit.x, unit.y, rotation - 90);
        }

        Draw.reset();
    }

    public void drawEngine(Unit unit){
        if(!unit.isFlying()) return;

        float scale = unit.elevation;
        float offset = engineOffset/2f + engineOffset/2f*scale;

        if(trailLength > 0 && !naval){
            drawTrail(unit);
        }

        Draw.color(engineColor == null ? unit.team.color : engineColor);
        Fill.circle(
                unit.x + Angles.trnsx(unit.rotation + 180, offset),
                unit.y + Angles.trnsy(unit.rotation + 180, offset),
                (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f)) * scale
        );
        Draw.color(engineColorInner);
        Fill.circle(
                unit.x + Angles.trnsx(unit.rotation + 180, offset - 1f),
                unit.y + Angles.trnsy(unit.rotation + 180, offset - 1f),
                (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f)) / 2f  * scale
        );
        Draw.color();
    }

    @Override
    public void draw(Unit unit){
        Mechc mech = unit instanceof Mechc ? (Mechc)unit : null;
        float z = unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f);

        if(unit.controller().isBeingControlled(player.unit())){
            drawControl(unit);
        }

        if(unit.isFlying() || visualElevation > 0){
            Draw.z(Math.min(Layer.darkness, z - 1f));
            drawShadow(unit);
        }

        Draw.z(z - 0.02f);

        if(mech != null){
            drawMech(mech);

            //side
            legOffset.trns(mech.baseRotation(), 0f, Mathf.lerp(Mathf.sin(mech.walkExtend(true), 2f/Mathf.PI, 1) * mechSideSway, 0f, unit.elevation));

            //front
            legOffset.add(Tmp.v1.trns(mech.baseRotation() + 90, 0f, Mathf.lerp(Mathf.sin(mech.walkExtend(true), 1f/Mathf.PI, 1) * mechFrontSway, 0f, unit.elevation)));

            unit.trns(legOffset.x, legOffset.y);
        }

        if(unit instanceof Tankc){
            drawTank((Unit & Tankc)unit);
        }

        if(unit instanceof Legsc){
            drawLegs((Unit & Legsc)unit);
        }

        Draw.z(Math.min(z - 0.01f, Layer.bullet - 1f));

        if(unit instanceof Payloadc){
            drawPayload((Unit & Payloadc)unit);
        }

        drawSoftShadow(unit);

        Draw.z(z);

        if(unit instanceof Crawlc c){
            drawCrawl(c);
        }

        if(drawBody) drawOutline(unit);
        drawWeaponOutlines(unit);
        if(engineSize > 0) drawEngine(unit);
        if(engines.size > 0) drawEngines(unit);
        if(drawBody) drawBody(unit);
        if(drawCell) drawCell(unit);
        drawWeapons(unit);
        if(drawItems) drawItems(unit);
        drawLight(unit);

        if(unit.shieldAlpha > 0 && drawShields){
            drawShield(unit);
        }

        if(mech != null) {
            unit.trns(-legOffset.x, -legOffset.y);
        }
        int[] decal = {unit.icon().texture.width,unit.icon().texture.height};
        if(decal[1] > 0){
            float base = unit.rotation - 90;
            for(var d : decal){
                Draw.blend();
                Draw.z(d <= 0f ? z : d);
                Draw.scl(d, d);
                Draw.color(d);
                Draw.rect(String.valueOf(d), unit.x + Angles.trnsx(base, decal[1], decal[2]), unit.y + Angles.trnsy(base, decal[1], decal[2]), base + d);
                Draw.blend();
            }
            Draw.reset();
            Draw.z(z);
        }

        for(Ability a : unit.abilities){
            Draw.reset();
            a.draw(unit);
        }

        Draw.reset();
    }

}
