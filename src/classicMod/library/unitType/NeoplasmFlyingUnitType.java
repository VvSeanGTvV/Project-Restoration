package classicMod.library.unitType;

import mindustry.entities.Effect;
import mindustry.gen.Unit;
import mindustry.type.unit.NeoplasmUnitType;

public class NeoplasmFlyingUnitType extends NewNeoplasmUnitType {

    public Effect smokeTrail;

    public NeoplasmFlyingUnitType(String name) {
        super(name);
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);
        if (smokeTrail != null) smokeTrail.at(unit);
    }
}
