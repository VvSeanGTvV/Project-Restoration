package classicMod.library.unitType;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.Seq;
import arc.util.*;
import classicMod.library.drawCustom.Outliner;
import mindustry.content.Fx;
import mindustry.gen.Unit;
import mindustry.graphics.*;
import mindustry.content.*;
import mindustry.entities.abilities.*;
import mindustry.type.unit.NeoplasmUnitType;
import mindustry.world.meta.*;
import mindustry.type.*;

// from xstabux!
public class OrnitopterUnitType extends NewNeoplasmUnitType {
    public final Seq<WingBlade> blades = new Seq<>();
    public float bladeDeathMoveSlowdown = 0.01f, fallDriftScl = 60f;
    public float fallSmokeX = 0f, fallSmokeY = 0f, fallSmokeChance = 0.1f;
    public TextureRegion healthCellRegion, tuskRegion;

    public OrnitopterUnitType(String name) {
        super(name);
    }

    public float bladeMoveSpeedScl = 1f;

    private float driftAngle;
    private boolean hasDriftAngle = false;
    public float driftAngle() {
        return driftAngle;
    }

    public Seq<WingBlade> blades(){
        return blades;
    }

    @Override
    public void update(Unit unit) {
        OrnitopterUnitType type = (OrnitopterUnitType) unit.type;
        float rX = unit.x + Angles.trnsx(unit.rotation - 90, type.fallSmokeX, type.fallSmokeY);
        float rY = unit.y + Angles.trnsy(unit.rotation - 90, type.fallSmokeX, type.fallSmokeY);

        blades.each(wingBlade -> wingBlade.update(unit));
        // When dying
        if (unit.dead || health <= 0) {
            if (Mathf.chanceDelta(type.fallSmokeChance)) {
                Fx.fallSmoke.at(rX, rY);
                Fx.burning.at(rX, rY);
            }

            // Compute random drift angle if not already set
            if (!hasDriftAngle) {
                float speed = Math.max(Math.abs(unit.vel().x), Math.abs(unit.vel().y));
                float maxAngle = Math.min(180f, speed * type.fallDriftScl); // Maximum drift angle based on speed
                driftAngle = (Angles.angle(unit.x, unit.y, unit.x + unit.vel().x, unit.y + unit.vel().y) + Mathf.range(maxAngle)) % 360f;
                hasDriftAngle = true;
            }

            // Drift in random direction
            float driftSpeed = Math.max(0f, unit.vel().len() - unit.type().drag) * type.accel;
            float driftX = driftSpeed * Mathf.cosDeg(driftAngle);
            float driftY = driftSpeed * Mathf.sinDeg(driftAngle);
            unit.move(driftX, driftY);

            unit.rotation = Mathf.lerpDelta(unit.rotation, driftAngle, 0.01f);

            bladeMoveSpeedScl = Mathf.lerpDelta(bladeMoveSpeedScl, 0f, type.bladeDeathMoveSlowdown);
        } else {
            hasDriftAngle = false; // Reset the drift angle flag
            bladeMoveSpeedScl = Mathf.lerpDelta(bladeMoveSpeedScl, 1f, type.bladeDeathMoveSlowdown);
        }

        type.fallSpeed = 0.01f;
    }

    @Override
    public void createIcons(MultiPacker packer) {
        super.createIcons(packer);
        if (tuskRegion != null) Outliner.outlineTemplateRegion(packer, Core.atlas.find(name + "-tusk-template"), outlineColor, name + "-tusk-outline");
        if (healthCellRegion != null) Outliner.outlineRegion(packer, healthCellRegion, outlineColor, name + "-health-outline", outlineRadius);
        for (WingBlade blade : blades) {
            Outliner.outlineRegion(packer, blade.bladeRegion, outlineColor, blade.spriteName + "-outline", outlineRadius);
            Outliner.outlineRegion(packer, blade.shadeRegion, outlineColor, blade.spriteName + "-top-outline", outlineRadius);
        }
    }

    @Override
    public void load() {
        super.load();
        tuskRegion = Core.atlas.find(name + "-tusk");
        healthCellRegion = Core.atlas.find(name + "-health");
        for (WingBlade blade : blades()){
            blade.load();
        }
    }

    @Override
    public void draw(Unit unit) {
        //super.draw(unit);

        Draw.z(Layer.flyingUnit - 1f);
        drawShadow(unit);

        Draw.z(Layer.flyingUnit);
        drawOutline(unit);
        drawWeaponOutlines(unit);

        drawWeapons(unit);
        drawHealthCell(unit);
        drawBody(unit);
        drawBlades(unit);
        drawTusk(unit);
    }


    public void drawTusk(Unit unit) {
        applyColor(unit);
        Draw.rect(Core.atlas.find(name + "-tusk-outline"), unit.x, unit.y, unit.rotation - 90.0F);
        Draw.rect(tuskRegion, unit.x, unit.y, unit.rotation - 90.0F);
        Draw.reset();
    }

    public Color cellColor(Unit unit, Color color) {
        float f = Mathf.clamp(unit.healthf());
        return Tmp.c1.set(Color.black).lerp(color, f + Mathf.absin(Time.time, Math.max(f * 5.0F, 1.0F), 1.0F - f));
    }

    public void drawHealthCell(Unit unit){
        applyColor(unit);
        Draw.color(cellColor(unit, Color.white));
        Draw.rect(Core.atlas.find(name + "-health-outline"), unit.x, unit.y, unit.rotation - 90.0F);
        Draw.rect(healthCellRegion, unit.x, unit.y, unit.rotation - 90.0F);
        Draw.reset();
    }

    public void drawBlades(Unit unit){
        float z = Draw.z();
        //float z = unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f);

        applyColor(unit);
        if(unit.type instanceof OrnitopterUnitType copter){
            for(int sign : Mathf.signs){
                long seedOffset = 0;
                for(WingBlade blade : copter.blades()){
                    float rx = unit.x + Angles.trnsx(unit.rotation - 90, blade.x * sign, blade.y);
                    float ry = unit.y + Angles.trnsy(unit.rotation - 90, blade.x * sign, blade.y);
                    float bladeScl = Draw.scl * blade.bladeSizeScl;
                    float shadeScl = Draw.scl * blade.shadeSizeScl;

                    /*Draw.rect(blade.bladeRegion, rx, ry, --Bugfixes intention
                            blade.bladeRegion.width * bladeScl * sign,
                            blade.bladeRegion.height * bladeScl,
                            unit.rotation - 90 + sign
                    );*/


                    if(blade.bladeRegion.found()){
                        Draw.z(z + blade.layerOffset);
                        Draw.alpha(blade.blurRegion.found() ? 1 - (copter.bladeMoveSpeedScl / 0.8f) : 1);
                        Draw.rect(
                                blade.bladeOutlineRegion, rx, ry,
                                blade.bladeOutlineRegion.width * bladeScl * sign,
                                blade.bladeOutlineRegion.height * bladeScl,
                                unit.rotation - 90 + sign * Mathf.randomSeed(blade.drawSeed + (seedOffset++), blade.bladeMaxMoveAngle, -blade.bladeMinMoveAngle)
                        );
                        Draw.mixcol(Color.white, unit.hitTime);
                        Draw.rect(blade.bladeRegion, rx, ry,
                                blade.bladeRegion.width * bladeScl * sign,
                                blade.bladeRegion.height * bladeScl,
                                unit.rotation - 90 + sign * Mathf.randomSeed(blade.drawSeed + (seedOffset++), blade.bladeMaxMoveAngle, -blade.bladeMinMoveAngle)
                        );
                        Draw.reset();
                    }

                    if(blade.blurRegion.found()){
                        Draw.z(z + blade.layerOffset);
                        Draw.alpha(copter.bladeMoveSpeedScl * blade.blurAlpha * (unit.dead() ? copter.bladeMoveSpeedScl * 0.5f : 1));
                        Draw.rect(
                                blade.blurRegion, rx, ry,
                                blade.blurRegion.width * bladeScl * sign,
                                blade.blurRegion.height * bladeScl,
                                unit.rotation - 90 + sign * Mathf.randomSeed(blade.drawSeed + (seedOffset++), blade.bladeMaxMoveAngle, -blade.bladeMinMoveAngle)
                        );
                        Draw.reset();
                    }

                    if(blade.shadeRegion.found()){
                        Draw.z(z + blade.layerOffset + 0.001f);
                        Draw.alpha(copter.bladeMoveSpeedScl * blade.blurAlpha * (unit.dead ? copter.bladeMoveSpeedScl * 0.5f : 1));
                        Draw.rect(
                                blade.shadeRegion, rx, ry,
                                blade.shadeRegion.width * shadeScl * sign,
                                blade.shadeRegion.height * shadeScl,
                                unit.rotation - 90 + sign * Mathf.randomSeed(blade.drawSeed + (seedOffset++), blade.bladeMaxMoveAngle, -blade.bladeMinMoveAngle)
                        );
                        Draw.mixcol(Color.white, unit.hitTime);
                        Draw.reset();
                    }
                }
            }
        }
    }
}
