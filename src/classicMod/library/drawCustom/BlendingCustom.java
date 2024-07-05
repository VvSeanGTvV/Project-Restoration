package classicMod.library.drawCustom;

import arc.graphics.Blending;
import arc.graphics.Gl;

public class BlendingCustom {
    public static final Blending
    Bloom = new Blending(Gl.blendSrcAlpha, Gl.oneMinusSrcAlpha, Gl.blendSrcAlpha, Gl.oneMinusSrcAlpha);
}
