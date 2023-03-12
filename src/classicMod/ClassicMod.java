package classicMod;

import arc.*;
import arc.util.*;
import classicMod.content.*;
import classicMod.library.ui.*;
import classicMod.library.ui.menu.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.ui.fragments.*;

import static classicMod.library.ui.menu.MenuUI.*;
import static mindustry.Vars.*;
//v5-java-mod is the current use

public class ClassicMod extends Mod{
    public ClassicMod(){
        //Log.info("Loaded Classic constructor.");

        //listen for game load event
        Events.on(ClientLoadEvent.class, e -> {
            Core.app.post(UIExtended::init);
            ui.showOkText("@mod.v5disclaimer.title", "@mod.v5disclaimer.text", () -> {});
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

        //MenuBackground bg = (tn == 2 ? Erekir : tn == 3 ? Serpulo : tn == 4 ? random : tn == 5 ? solarSystem : null);
        Reflect.set(MenuFragment.class, ui.menufrag, "renderer", new MainMenuRenderer(solarSystem));
    }
    
    @Override
    public void init() {
        MenuUI.load()
    }

    @Override
    public void loadContent(){
        Log.info("Loading contents...");
        new ClassicBullets().load();
        new ClassicUnitTypes().load();
        new ClassicBlocks().load();
        new ExtendedSerpuloTechTree().load();
        new ExtendedErekirTechTree().load();
    }

}
