package classicMod.content;

import arc.graphics.Color;
import classicMod.library.generator.TantrosPlanetGenerator;
import mindustry.content.Planets;
import mindustry.game.Team;
import mindustry.graphics.g3d.*;
import mindustry.type.Planet;
import mindustry.world.meta.Env;

public class RPlanets {
    public static Planet
            tantros
    ;

    public static void load(){
        tantros = new Planet("tantros", Planets.sun, 1f, 2){{
            int division = 6;
            //alwaysUnlocked = true;
            generator = new TantrosPlanetGenerator();
            meshLoader = () -> new HexMesh(this, division);
            cloudMeshLoader = () -> new MultiMesh(
                    //new HexSkyMesh(this, 3, 0.13f, 0.011f, 5, Color.valueOf("c4ebed").a(0.75f), 2, 0.18f, 1.2f, 0.3f),
                    //new HexSkyMesh(this, 5, 0.7f, 0.005f, 5, Color.valueOf("edfeff").a(0.65f), 3, 0.12f, 1.5f, 0.32f),
                    new HexSkyMesh(this, 8, 0.3f, 0.11f, division - 1, Color.valueOf("6babf9").a(0.55f), 2, 0.08f, 1.75f, 0.265f)
            );
            accessible = false;
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
