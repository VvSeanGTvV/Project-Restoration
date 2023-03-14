package classicMod.library.ui;

import arc.*;
import classicMod.library.ui.frag.*;

public class UIExtended {
    public static CutsceneEnding cutsceneEnding;
    public static CutsceneEndingFrag cutsceneEndingfrag;

    public static void init(){
        cutsceneEnding = new CutsceneEnding();
        cutsceneEndingfrag = new CutsceneEndingFrag();


    }
    public static int getWidth(){
        return Core.graphics.getWidth();
    }

    public static int getHeight(){
        return Core.graphics.getHeight();
    }
}
