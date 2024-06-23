package classicMod.library.ui.menu;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.geom.*;
import mindustry.graphics.g3d.*;

import static arc.Core.*;
import static classicMod.library.ui.UIExtended.fdelta;
import static mindustry.Vars.*;

public class SpaceMenuBackground extends MenuBackground {
    public static FrameBuffer menuBuffer;
    public static PlanetParams menuParams;
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

        menuParams.camPos.rotate(Vec3.Y, fdelta(50f, 60f));

        renderer.planets.render(menuParams);

        menuBuffer.end();

        Draw.rect(Draw.wrap(menuBuffer.getTexture()), (float) graphics.getWidth() / 2, (float) graphics.getHeight() / 2, graphics.getWidth(), graphics.getHeight());
    }
}