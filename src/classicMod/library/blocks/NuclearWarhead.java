package classicMod.library.blocks;

import mindustry.gen.*;
import mindustry.world.*;

public class NuclearWarhead extends Block{
    public float radius = 100f;

    public NuclearWarhead(String name){
        super(name);
        solid = true;
        update = true;
    }

    public static class NuclearWarheadBuild extends Building{

    }
}
