package classicMod.content;

import classicMod.library.ai.RallyAI;

public class RVars {
    public static float ClassicBulletsMultiplier = 4f; //Classic intended use and not other stuff

    public static float MaximumRangeCommand = Float.MAX_VALUE; //Modifies the range
    public static String CommandOrigin = "attack"; //Public Command Center
    public static RallyAI.UnitState PublicState = RallyAI.UnitState.attack; //Public UnitState
    public static String empty = "restored-mind-nullTexture"; //empty
}
