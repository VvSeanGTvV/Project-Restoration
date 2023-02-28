package v5mod;

import arc.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.mod.Mods.LoadedMod;
import mindustry.ui.dialogs.*;
import v5mod.content.*;
//v5-java-mod is the current use

public class V5RebornMod extends Mod{
    public V5RebornMod(){
        Log.info("Loaded V5Constructor constructor.");

        //listen for game load event
        Events.on(ClientLoadEvent.class, e -> {
            Vars.ui.showOkText("@mod.v5disclaimer.title", "@mod.v5disclaimer.text", () -> {});
            //show dialog upon startup
            //Time.runTask(10f, () -> {
            //    BaseDialog dialog = new BaseDialog("Welcome to V5 Java Edition!");
                //dialog.cont.add("behold").row();
                //mod sprites are prefixed with the mod name (this mod is called 'example-java-mod' in its config)
            //    dialog.cont.image(Core.atlas.find("projectv5-mod-logoMod")).pad(20f).row();
            //    dialog.cont.add("Welcome to Beta of V5 Java Edition! Currently this is not fully finished or fully ported over!").row();
            //    dialog.cont.button("Continue", dialog::hide).size(130f, 50f);
            //    dialog.show();
            //});
        });
    }

    @Override
    public void loadContent(){
        Log.info("Loading contents...");
        new V5UnitTypes().load();
        new V5Blocks().load();
        new V5TechTree().load();
    }

}
