package classicMod.library.uiCustom;

import mindustry.core.*;

public class UIExtended extends UI {
    public static CutsceneEnding cutsceneEnding;

    @Override
    public void init(){
        cutsceneEnding = new CutsceneEnding();
    }
}
