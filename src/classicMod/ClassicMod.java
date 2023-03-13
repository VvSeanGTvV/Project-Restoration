package classicMod;

import arc.*;
import arc.util.*;
import classicMod.content.*;
import classicMod.library.ui.*;
import classicMod.library.ui.menu.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.type.*;
import mindustry.ui.fragments.*;

import static arc.Core.*;
import static classicMod.library.ui.menu.MenuUI.*;
import static mindustry.Vars.*;
//v5-java-mod is the current use

public class ClassicMod extends Mod{
    private String ModVersion;
    public ClassicMod(){
        //Log.info("Loaded Classic constructor.");
        //listen for game load event


        Events.on(ClientLoadEvent.class, e -> {
            loadSettings();
            Core.app.post(UIExtended::init);
            LoadedMod mod = mods.locateMod("restored-mind");
            ModVersion = mod.meta.minGameVersion;
            ui.showOkText("@mod.restored-mind.earlyaccess.title", "@mod.restored-mind.earlyaccess.text", () -> {
            });
            Planet lastPlanet;
            //MenuBackground bg = solarSystem;
            lastPlanet = content.getByName(ContentType.planet, settings.getString("lastplanet", "serpulo"));
            MenuBackground bg = (lastPlanet.name == Planets.erekir.name ? Erekir : lastPlanet.name == Planets.serpulo.name ? Serpulo : lastPlanet.name == Planets.tantros.name ? Tantros : solarSystem);
            boolean usePlanetBG = settings.getBool("@mod.restored-mind.use-planetmenu");
            boolean uselastPlanet = settings.getBool("@mod.restored-mind.use-lastplanet-bg");
            if (usePlanetBG) {
                if (uselastPlanet) {
                    Reflect.set(MenuFragment.class, ui.menufrag, "renderer", new MainMenuRenderer(SortedPlanet));
                } else {
                    Reflect.set(MenuFragment.class, ui.menufrag, "renderer", new MainMenuRenderer(random));
                }
            }

            LoadedMod lastModVer = mods.locateMod("classicv5");
            if (lastModVer != null) {
                ui.showCustomConfirm("@mod.restored-mind.conflictwarning.title", "@mod.restored-mind.conflictwarning.text", "@yes", "@no", () -> {
                    lastModVer.meta.hidden = true;
                }, () -> {
                });
            }

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
    }
    
    @Override
    public void init() {
        MenuUI.load();
    }

    private void loadSettings() {
        ui.settings.addCategory("@setting.restored-mind", "restored-mind-icon", t -> {
            t.checkPref("@mod.restored-mind.use-planetmenu", true);
            t.checkPref("@mod.restored-mind.use-lastplanet-bg", true);
            //t.checkPref("fos-realisticmode", false);
            //t.checkPref("fos-damagedisplay", true);
            t.textPref("Modmetathingy","This mod's current version:"+ModVersion);
        });
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
