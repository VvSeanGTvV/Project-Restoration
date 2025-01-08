package classicMod.library.unitType;

import arc.struct.Seq;
import arc.util.Log;
import classicMod.library.ai.NeoplasmAIController;
import mindustry.entities.Units;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.type.unit.NeoplasmUnitType;

public class NewNeoplasmUnitType extends NeoplasmUnitType {
    public NewNeoplasmUnitType(String name) {
        super(name);
    }

    @Override
    public void update(Unit unit) {
        if (unit.controller() instanceof NeoplasmAIController neoplasmAIController) {
            if (neoplasmAIController.DodgeTile == null) neoplasmAIController.DodgeTile = new Seq<>();
        }
        super.update(unit);
    }

    @Override
    public void killed(Unit unit) {
        Unit uNeo = Units.closest(unit.team, unit.x, unit.y, u -> u.controller() instanceof NeoplasmAIController);
        if (uNeo != null) {
            if (uNeo.controller() instanceof NeoplasmAIController neoplasmAIController) {
                Log.info("dead update");
                neoplasmAIController.update = true;
                if (unit.tileOn() != null) neoplasmAIController.DodgeTile.add(unit.tileOn());
                Log.info(neoplasmAIController.DodgeTile);
            }
        }

        super.killed(unit);
    }
}
