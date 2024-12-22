package classicMod.library.unitType;

import mindustry.ai.types.MissileAI;
import mindustry.entities.Effect;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.unit.MissileUnitType;

public class MissileSmokeUnitType extends UnitType {

    public Effect smokeTrail;

    public MissileSmokeUnitType(String name) {
        super(name);
        playerControllable = false;
        createWreck = false;
        createScorch = false;
        logicControllable = false;
        isEnemy = false;
        useUnitCap = false;
        allowedInPayloads = false;
        flying = true;
        constructor = TimedKillUnit::create;
        envEnabled = -1;
        envDisabled = 0;
        physics = false;
        bounded = false;
        //trailLength = 7;
        hidden = true;
        hoverable = false;
        speed = 4.0F;
        lifetime = 102.0F;
        rotateSpeed = 2.5F;
        range = 6.0F;
        targetPriority = -1.0F;
        //outlineColor = Pal.darkOutline;
        fogRadius = 2.0F;
        loopSound = Sounds.missileTrail;
        loopSoundVolume = 0.05F;
        drawMinimap = false;
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);
        if (smokeTrail != null) smokeTrail.at(unit);
    }
}
