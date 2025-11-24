package classicMod.library.unitType.unit;

import arc.graphics.Color;
import mindustry.entities.Effect;
import mindustry.gen.*;

public interface Jumperc extends Builderc, Drawc, ElevationMovec, Entityc, Healthc, Hitboxc, Itemsc, Mechc, Minerc, Physicsc, Posc, Rotc, Shieldc, Statusc, Syncc, Teamc, Unitc, Velc, Weaponsc {

    Effect stompEffect();
    void stompEffect(Effect var1);

    Effect stompEffectExplosion();
    void stompEffectExplosion(Effect var1);

    Color stompColor();
    void stompColor(Color var1);

    boolean stompExplosion();

    float timing();

    float timingY();

    boolean stopMoving();
    void stopMoving(boolean var1);

    boolean hit();

    float hitDelay();

    float lastHealth();
}
