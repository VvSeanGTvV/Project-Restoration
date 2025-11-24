package classicMod.content;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import classicMod.library.ai.*;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.type.Item;

public class RVars extends Vars {
    public static float ClassicBulletsMultiplier = 4f; //Classic intended use and not other stuff

    public static float MaximumRangeCommand = Float.MAX_VALUE; //Modifies the range
    public static String CommandOrigin = "attack"; //Public Command Center
    public static RallyAI.UnitState PublicState = RallyAI.UnitState.attack; //Public UnitState
    public static String empty = "restored-mind-nullTexture"; //empty

    /** constructors id registered custom **/
    public static ObjectMap<String, Integer> idcMap = new ObjectMap<>();

    public static PathfinderCustom pathfinderCustom;

    /** What neoplasm can drill, this is important to prevent drilling unnecessary ores.
     * TODO: Probably dynamic would be good here
     **/
    public static Seq<Item> CordCanDrill = new Seq<>(
            new Item[]{
                    Items.beryllium,
                    Items.tungsten,
            }
    );

    @Override
    public void loadAsync() {
        super.loadAsync();
        init_RVars();
    }

    public static void init_RVars() {
        pathfinderCustom = new PathfinderCustom();
    }
}
