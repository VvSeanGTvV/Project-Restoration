package classicMod.library.drawCustom;

import arc.graphics.*;

public class BlendingCustom {
    public static final Blending
            Bloom = new Blending(Gl.srcAlpha, Gl.one, Gl.one, Gl.oneMinusDstAlpha);
}
