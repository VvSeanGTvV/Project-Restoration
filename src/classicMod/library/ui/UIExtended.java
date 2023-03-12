package classicMod.library.ui;

import arc.*;

public class UIExtended {
    public static CutsceneEnding cutsceneEnding;
    //public static VideoDemo videoDemo; TODO plz request this from Anuke not meh

    public static void init(){
        cutsceneEnding = new CutsceneEnding();
        //videoDemo = new VideoDemo();

    }
    public static int getWidth(){
        return Core.graphics.getWidth();
    }

    public static int getHeight(){
        return Core.graphics.getHeight();
    }
}
