package classicMod.library.ui.menu;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;

public class MenuUI {
    public static MenuBackground Erekir, Serpulo, random, solarSystem;

    public static void load() {
        Erekir = new SpaceMenuBackground() {{
            params = new PlanetParams() {{
                planet = Planets.erekir;
                zoom = 1.2f;
            }};
        }};
        Serpulo = new SpaceMenuBackground() {{
            params = new PlanetParams() {{
                planet = Planets.serpulo;
                zoom = 0.7f;
            }};
        }};
        random = new SpaceMenuBackground() {{
            params = new PlanetParams() {{
                Seq<Planet> visible = Vars.content.planets().copy().filter(p -> p.visible);
                planet = visible.get(Mathf.floor((float) (Math.random() * visible.size)));
            }};
        }};
        solarSystem = new SpaceMenuBackground() {{
            params = new PlanetParams() {{
                planet = Planets.sun;
                camPos = new Vec3(0.01, 1, 0);
                zoom = 12f;
            }};
        }};
    }
}
