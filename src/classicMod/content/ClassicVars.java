package classicMod.content;

import arc.graphics.Color;
import classicMod.library.ai.RallyAI;

public class ClassicVars {
    public static float ClassicBulletsMultiplier = 4f; //Classic intended use and not other stuff

    public static float MaximumRangeCommand = 2147483647f; //Modifies the range
    public static String CommandOrigin = "attack"; //Public Command Center
    public static RallyAI.UnitState PublicState = RallyAI.UnitState.attack; //Public UnitState
}
