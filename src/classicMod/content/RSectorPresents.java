package classicMod.content;

import mindustry.content.*;
import mindustry.type.SectorPreset;

public class RSectorPresents extends SectorPresets {

    public static SectorPreset
    silverCrags
    ;

    public static void load() {
        silverCrags = new SectorPreset("silverCrags", Planets.serpulo, 25) {{
            captureWave = 15;
            difficulty = 3;
        }};
    }
}
