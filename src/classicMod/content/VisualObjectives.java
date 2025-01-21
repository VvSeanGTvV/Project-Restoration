package classicMod.content;

import arc.Core;
import arc.util.Log;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Objectives;
import mindustry.type.SectorPreset;

import static arc.Core.settings;
import static mindustry.content.SectorPresets.planetaryTerminal;

public class VisualObjectives extends Objectives {

    /** this is just visual usage not that useful **/
    public static class LaunchedPlanetaryAccelerator implements Objective{
        public SectorPreset sector;
        public UnlockableContent accelerator;

        public LaunchedPlanetaryAccelerator(SectorPreset sector, UnlockableContent accelerator){
            this.sector = sector;
            this.accelerator = accelerator;
        }

        public LaunchedPlanetaryAccelerator(UnlockableContent accelerator){
            this.sector = planetaryTerminal;
            this.accelerator = accelerator;
        }

        @Override
        public boolean complete() {
            return (boolean)settings.get("launched-planetary", false);
        }

        @Override
        public String display(){
            return Core.bundle.format("requirement.launchedAccelerator", accelerator.localizedName);
        }
    }

}
