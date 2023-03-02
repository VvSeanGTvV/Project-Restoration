package v5mod.lib.ai;

import mindustry.ai.*;
import mindustry.gen.*;

public interface OldUnitController {
    void unit(Unitc unit);
    Unitc unit();

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
