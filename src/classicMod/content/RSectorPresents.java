package classicMod.content;

import arc.Core;
import mindustry.content.*;
import mindustry.gen.Icon;
import mindustry.type.SectorPreset;

public class RSectorPresents extends SectorPresets {

    public static SectorPreset
    silverCrags
    ;

    public static void load() {
        silverCrags = new SectorPreset("silverCrags", Planets.serpulo, 25) {{
            rules = rules -> {
                rules.waves = true; // bruh forgor to set this while editing the map
                rules.winWave = 25;
                rules.solarMultiplier = 1.5f;
            };
            difficulty = 3;
        }};
    }
}
