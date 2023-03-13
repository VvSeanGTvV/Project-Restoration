package classicMod.library.ui.menu;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.graphics.g3d.*;
import mindustry.type.*;
import mindustry.ui.fragments.*;

import static arc.Core.*;
import static classicMod.library.ui.menu.MenuUI.*;
import static mindustry.Vars.*;

public class SpaceMenuBackground extends MenuBackground {
    public static FrameBuffer menuBuffer;
    public static PlanetParams menuParams;
    Planet lastPlanet = null;
    public PlanetParams params;

    @Override
    public void render() {
        int size = Math.max(graphics.getWidth(), graphics.getHeight());

        if (menuBuffer == null) {
            menuBuffer = new FrameBuffer(size, size);
        }

        menuBuffer.begin(Color.clear);

        params.alwaysDrawAtmosphere = true;
        params.drawUi = false;

        if (menuParams == null) {
            menuParams = params;
        }

        menuParams.camPos.rotate(Vec3.Y, 0.05f);
        if (lastPlanet != content.getByName(ContentType.planet, Core.settings.getString("lastplanet", "serpulo"))){
            lastPlanet = content.getByName(ContentType.planet, Core.settings.getString("lastplanet", "serpulo"));
            MenuBackground bg = solarSystem;
            if(lastPlanet.name == Planets.erekir.name){
                bg = Erekir;
            } else if (lastPlanet.name == Planets.serpulo.name) {
                bg = Serpulo;
            }
            //MenuBackground bg = (lastPlanet.name == Planets.erekir.name ? Erekir : lastPlanet.name == Planets.serpulo.name ? Serpulo : solarSystem);
            Reflect.set(MenuFragment.class, ui.menufrag, "renderer", new MainMenuRenderer(bg));
        }

        renderer.planets.render(menuParams);

        menuBuffer.end();

        Draw.rect(Draw.wrap(menuBuffer.getTexture()), (float) graphics.getWidth() / 2, (float) graphics.getHeight() / 2, graphics.getWidth(), graphics.getHeight());
    }
}