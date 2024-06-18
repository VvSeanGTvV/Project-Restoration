package classicMod.content;

import mindustry.world.meta.*;

public class ExtendedStat {
    public static final Stat
            StatusOutput = new Stat("status-give", StatCat.function),
            StatusDuration = new Stat("status-give-duration", StatCat.function),

            fuel = new Stat("fuel", StatCat.crafting),
            inbetweenTeleport = new Stat("inbetween-teleport", StatCat.items),
            burnTime = new Stat("burn-time", StatCat.crafting),
            canBreak = new Stat("can-break", StatCat.function),
            tierLevel = new Stat("unit-level")
    ;

}
