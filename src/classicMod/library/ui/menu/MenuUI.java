package classicMod.library.ui.menu;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;

import static arc.Core.settings;
import static mindustry.Vars.content;

public class MenuUI {
    public static MenuBackground Tantros, Erekir, Serpulo, random, solarSystem, SortedPlanet;

    public static void load() {
        Erekir = new SpaceMenuBackground() {{
            params = new PlanetParams() {{
                planet = Planets.erekir;

                zoom = 0.6f;
            }};
        }};
        Serpulo = new SpaceMenuBackground() {{
            params = new PlanetParams() {{
                planet = Planets.serpulo;
                //camPos = new Vec3(0, 0, 0);
                zoom = 0.6f;
            }};
        }};
        Tantros = new SpaceMenuBackground() {{
            params = new PlanetParams() {{
                planet = Planets.tantros;
                //camPos = new Vec3(0, 0, 0);
                zoom = 0.6f;
            }};
        }};
        random = new SpaceMenuBackground() {{
            params = new PlanetParams() {{
                Seq<Planet> visible = Vars.content.planets().copy().filter(p -> p.visible);
                planet = visible.get(Mathf.floor((float) (Math.random() * visible.size)));
            }};
        }};

        SortedPlanet = new SpaceMenuBackground() {{
            params = new PlanetParams() {{ //Support test for modded planets! +it's sorted into planets so ;)
                Planet lastPlanet = content.getByName(ContentType.planet, settings.getString("lastplanet", "serpulo"));
                Seq<Planet> visible = Vars.content.planets().copy().filter(p -> {
                    return p.visible && p.accessible;
                });
                visible.forEach(c->{
                    if(c.name==lastPlanet.name){
                        planet = c;
                        zoom = 0.6f;
                    }
                });
            }};
        }};
        solarSystem = new SpaceMenuBackground() {{
            params = new PlanetParams() {{
                planet = Planets.sun;
                camPos = new Vec3(0, 0.5, 0);
                zoom = 12f;
            }};
        }};
    }
}
