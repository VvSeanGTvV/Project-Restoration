package classicMod.library.drawCustom;

import arc.graphics.*;

public class BlendingCustom {
    public static final Blending
            Bloom = new Blending(Gl.srcAlpha, Gl.blendDstAlpha, Gl.one, Gl.oneMinusSrcAlpha);
}
