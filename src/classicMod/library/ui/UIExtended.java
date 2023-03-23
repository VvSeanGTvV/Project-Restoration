package classicMod.library.ui;

import arc.*;
import classicMod.library.ui.frag.*;
import classicMod.library.ui.menu.*;

public class UIExtended {
    public static CutsceneEnding cutsceneEnding;
    public static CutsceneEndingFrag cutsceneEndingfrag;
    public static TechTreeDialog Techtree;

    public static void init(){
        cutsceneEnding = new CutsceneEnding();
        cutsceneEndingfrag = new CutsceneEndingFrag();
        Techtree = new TechTreeDialog();


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
