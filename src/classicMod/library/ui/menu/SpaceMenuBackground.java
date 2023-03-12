package classicMod.library.ui.menu;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.geom.*;
import mindustry.graphics.g3d.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class SpaceMenuBackground extends MenuBackground {
    public static FrameBuffer menuBuffer;
    public static PlanetParams menuParams;
    public PlanetParams params;

    @Override
    public void render() {
        int size = Math.max(graphics.getWidth(), graphics.getHeight());

        if(menuBuffer == null){
            menuBuffer = new FrameBuffer(size, size);
        }

        menuBuffer.begin(Color.clear);

        params.alwaysDrawAtmosphere = true;
        params.drawUi = false;

        if (menuParams == null){
            menuParams = params;
        }

        if (Core.settings.getBool("fos-rotatemenucamera")){
            menuParams.camPos.rotate(Vec3.Y, 0.1f);
        }

        renderer.planets.render(menuParams);

        menuBuffer.end();

        Draw.rect(Draw.wrap(menuBuffer.getTexture()), (float) graphics.getWidth() / 2, (float) graphics.getHeight() / 2, graphics.getWidth(), graphics.getHeight());
    }
}