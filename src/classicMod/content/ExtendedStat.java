package classicMod.content;

import mindustry.world.meta.*;

import static mindustry.world.meta.StatValues.fixValue;

public class ExtendedStat {
    public static final Stat
            StatusOutput = new Stat("status-give", StatCat.function),
            StatusDuration = new Stat("status-give-duration", StatCat.function),

            fuel = new Stat("fuel", StatCat.crafting),
            inbetweenTeleport = new Stat("inbetween-teleport", StatCat.items),
            burnTime = new Stat("burn-time", StatCat.crafting),
            canBreak = new Stat("can-break", StatCat.function),
            launchSector = new Stat("launch-sector", StatCat.function),
            itemsMovedBase = new Stat("itemsmoved-base", StatCat.items),
            itemsMovedBoost = new Stat("itemsmoved-boost", StatCat.optional),
            tierLevel = new Stat("unit-level")
    ;

    public static StatValue squaredRange(float value, StatUnit unit){
        return table -> {
            String fixed = fixValue(value);
            table.add(fixed);
            table.add((unit.space ? " " : "") + unit.localized());
        };
    }

}
