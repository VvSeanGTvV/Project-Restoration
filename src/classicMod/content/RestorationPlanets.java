package classicMod.content;

import arc.graphics.Color;
import classicMod.library.generator.TantrosPlanetGenerator;
import mindustry.content.Planets;
import mindustry.game.Team;
import mindustry.graphics.g3d.HexMesh;
import mindustry.type.Planet;
import mindustry.world.meta.Env;

public class RestorationPlanets {
    public static Planet
            tantros
    ;

    public static void load(){
        tantros = new Planet("tantros", Planets.sun, 1f, 2){{
            alwaysUnlocked = true;
            generator = new TantrosPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 12);
            accessible = true;
            visible = true;
            atmosphereColor = Color.valueOf("3db899");
            iconColor = Color.valueOf("597be3");
            startSector = 10;
            atmosphereRadIn = -0.01f;
            atmosphereRadOut = 0.3f;
            defaultEnv = Env.underwater | Env.terrestrial;
            ruleSetter = r -> {
                r.waveTeam = Team.blue;
                r.placeRangeCheck = false;
                r.showSpawns = false;
            };
        }};
    }
}
