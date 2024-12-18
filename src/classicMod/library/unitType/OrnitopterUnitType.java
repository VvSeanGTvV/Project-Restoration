package classicMod.library.unitType;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.*;
import arc.struct.Seq;
import arc.util.*;
import classicMod.library.drawCustom.Outliner;
import mindustry.content.Fx;
import mindustry.gen.Unit;
import mindustry.graphics.*;
import mindustry.type.UnitType;

// from xstabux!
public class OrnitopterUnitType extends NeoplasmUnitType {
    public final Seq<WingBlade> blades = new Seq<>();
    public float bladeDeathMoveSlowdown = 0.01f, fallDriftScl = 60f;
    public float fallSmokeX = 0f, fallSmokeY = 0f, fallSmokeChance = 0.1f;

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
        for (WingBlade blade : blades) {
            Outliner.outlineRegion(packer, blade.bladeRegion, outlineColor, blade.spriteName + "-outline", outlineRadius);
            Outliner.outlineRegion(packer, blade.shadeRegion, outlineColor, blade.spriteName + "-top-outline", outlineRadius);
        }
    }

    @Override
    public void load() {
        super.load();
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

        //drawWeapons(unit);
        drawBody(unit);
        drawBlade(unit);
    }

    public void drawBlade(Unit unit){
        float z = unit.elevation > 0.5f ? (lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : groundLayer + Mathf.clamp(hitSize / 4000f, 0, 0.01f);

        applyColor(unit);
        if(unit.type instanceof OrnitopterUnitType copter){
            for(int sign : Mathf.signs){
                long seedOffset = 0;
                for(WingBlade blade : copter.blades()){
                    blade.drawSeed++;
                    float rx = unit.x + Angles.trnsx(unit.rotation - 90, blade.x * sign, blade.y);
                    float ry = unit.y + Angles.trnsy(unit.rotation - 90, blade.x * sign, blade.y);
                    float bladeScl = Draw.scl * blade.bladeSizeScl;
                    float shadeScl = Draw.scl * blade.shadeSizeScl;

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
