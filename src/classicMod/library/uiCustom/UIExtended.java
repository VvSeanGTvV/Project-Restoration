package classicMod.library.uiCustom;

import arc.*;

public class UIExtended {
    public static CutsceneEnding cutsceneEnding;

    public void init(){
        cutsceneEnding = new CutsceneEnding();

    }
    public static float getWidth(){
        return Core.graphics.getWidth();
    }

    public static float getHeight(){
        return Core.graphics.getHeight();
    }
}
