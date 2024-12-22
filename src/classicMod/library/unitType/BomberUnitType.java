package classicMod.library.unitType;

import arc.math.*;
import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

public class BomberUnitType extends UnitType {
    public Effect smokeTrail;
    public float smokeX, smokeY, smokeXRand, smokeYRand;
    public BomberUnitType(String name) {
        super(name);
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);
        float rX = unit.x + Angles.trnsx(unit.rotation - 90, smokeX + Mathf.random(smokeXRand), smokeY + Mathf.random(smokeYRand));
        float rY = unit.y + Angles.trnsy(unit.rotation - 90, smokeX + Mathf.random(smokeXRand), smokeY + Mathf.random(smokeYRand));
        smokeTrail.at(rX, rY);
    }
}
