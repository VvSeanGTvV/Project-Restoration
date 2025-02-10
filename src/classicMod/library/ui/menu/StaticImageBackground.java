package classicMod.library.ui.menu;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.graphics.gl.FrameBuffer;
import arc.math.geom.Vec3;
import arc.scene.ui.Image;
import arc.struct.*;
import arc.util.Timer;

import static arc.Core.*;
import static classicMod.library.ui.UIExtended.fdelta;
import static mindustry.Vars.*;

public class StaticImageBackground extends MenuBackground {

    public static FrameBuffer menuBuffer;
    public TextureRegion frame = new TextureRegion();
    public Image menuBG = new Image(frame);
    public ObjectMap<String, TextureRegion> imageData = new ObjectMap<>();
    public Seq<String> image = new Seq<>();

    @Override
    public void render() {
        int size = Math.max(graphics.getWidth(), graphics.getHeight());

        if (menuBuffer == null) {
            menuBuffer = new FrameBuffer(size, size);
        }

        menuBuffer.begin(Color.clear);

        menuBG.setFillParent(true);

        int select = (settings.getInt("staticimage") >= image.size) ? settings.getInt("staticimage") - 1 : settings.getInt("staticimage");
        frame.set(imageData.get(image.get(select)));
        menuBG.getRegion().set(frame);

        Draw.flush();

        Draw.rect(frame, (float) graphics.getWidth() / 2, (float) graphics.getHeight() / 2, graphics.getWidth(), graphics.getHeight());

        menuBuffer.end();
        TextureRegion buffer = Draw.wrap(menuBuffer.getTexture());
        buffer.flip(false, true);
        Draw.rect(buffer, (float) graphics.getWidth() / 2, (float) graphics.getHeight() / 2, graphics.getWidth(), graphics.getHeight());
    }
}
