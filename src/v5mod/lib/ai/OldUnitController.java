package v5mod.lib.ai;

import mindustry.ai.*;
import mindustry.entities.units.*;
import mindustry.gen.*;

public interface OldUnitController extends UnitController {
    void unit(Unitc unit);
    Unit unit();

    default void command(UnitCommand command){

    }

    default void update(){

    }

    default void removed(Unitc unit){

    }

    default boolean isBeingControlled(Unitc player){
        return false;
    }
}
