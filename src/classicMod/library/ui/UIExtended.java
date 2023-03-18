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
    /** Get the Device's Width resolution **/
    public static int getWidth(){
        return Core.graphics.getWidth();
    }
    /** Get the Device's Height resolution **/
    public static int getHeight(){
        return Core.graphics.getHeight();
    }
}
