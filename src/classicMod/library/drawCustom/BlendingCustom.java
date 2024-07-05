package classicMod.library.drawCustom;

import arc.graphics.*;

public class BlendingCustom {
    public static final Blending
            Bloom = new Blending(Gl.blendSrcAlpha, Gl.one, Gl.zero, Gl.oneMinusSrcAlpha);
}
