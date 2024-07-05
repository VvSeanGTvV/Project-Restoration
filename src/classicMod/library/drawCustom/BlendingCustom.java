package classicMod.library.drawCustom;

import arc.graphics.Blending;
import arc.graphics.Gl;

public class BlendingCustom {
    public static final Blending
    Bloom = new Blending(Gl.srcAlpha, Gl.one, Gl.blendSrcAlpha, Gl.oneMinusSrcAlpha);
}
