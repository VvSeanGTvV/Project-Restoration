package classicMod.library.unitType;

import arc.graphics.Color;
import arc.math.*;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.type.unit.NeoplasmUnitType;


public class BomberUnitType extends NewNeoplasmUnitType {
    public Effect smokeTrail;
    public Color smokeColor;
    public float smokeX, smokeY, smokeXRand, smokeYRand;
    public float trailChance;
    public BomberUnitType(String name) {
        super(name);
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);
        if (unit.vel.len() > 0f) {
            float rX = unit.x + Angles.trnsx(unit.rotation - 90, smokeX + Mathf.random(smokeXRand), smokeY + Mathf.random(smokeYRand));
            float rY = unit.y + Angles.trnsy(unit.rotation - 90, smokeX + Mathf.random(smokeXRand), smokeY + Mathf.random(smokeYRand));
            if (smokeTrail != null && trailChance > 0.0F && Mathf.chanceDelta(trailChance)) smokeTrail.at(rX, rY, unit.rotation - 180, (smokeColor != null) ? smokeColor : Color.white);
        }
    }
}
