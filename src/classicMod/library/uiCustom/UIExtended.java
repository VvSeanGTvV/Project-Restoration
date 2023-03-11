package classicMod.library.uiCustom;

import arc.*;
import classicMod.library.*;

public class UIExtended {
    public static CutsceneEnding cutsceneEnding;
    public static VideoDemo videoDemo;

    public static void init(){
        cutsceneEnding = new CutsceneEnding();
        videoDemo = new VideoDemo();

    }
    public static float getWidth(){
        return Core.graphics.getWidth();
    }

    public static float getHeight(){
        return Core.graphics.getHeight();
    }
}
