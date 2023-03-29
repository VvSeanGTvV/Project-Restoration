package classicMod;

import arc.*;
import arc.func.*;
import arc.util.*;
import classicMod.content.*;
import classicMod.library.ui.*;
import classicMod.library.ui.menu.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.ui.dialogs.SettingsMenuDialog.*;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.*;
import mindustry.ui.fragments.*;
import mindustry.world.blocks.legacy.*;

import static arc.Core.*;
import static classicMod.library.ui.menu.MenuUI.*;
import static mindustry.Vars.*;
//v5-java-mod is the current use

public class ClassicMod extends Mod{
    private final String ModVersion = "2.1 Beta";
    private final String BuildVer = "8";
    protected LoadedMod resMod = mods.locateMod("restored-mind");
    public ClassicMod(){
        //Log.info("Loaded Classic constructor.");
        //listen for game load event


        Events.on(ClientLoadEvent.class, e -> {
            loadSettings();
            Core.app.post(UIExtended::init);
            //MenuBackground bg = solarSystem;
            boolean usePlanetBG = settings.getBool("use-planetmenu");
            boolean uselastPlanet = settings.getBool("use-lastplanet-bg");
            if (usePlanetBG) {
                if (uselastPlanet) {
                    Reflect.set(MenuFragment.class, ui.menufrag, "renderer", new MainMenuRenderer(SortedPlanet));
                } else {
                    Reflect.set(MenuFragment.class, ui.menufrag, "renderer", new MainMenuRenderer(random));
                }
            }
            boolean ingnoreWarning = settings.getBool("ignore-warning");
            if (!ingnoreWarning) {
                ui.showOkText("@mod.restored-mind.earlyaccess.title", "@mod.restored-mind.earlyaccess.text", () -> {
                });
            }

            if(settings.getBool("backward-v5", true)){ //TODO compatible to v5
                if(!settings.getBool("backward-v6", false)){
                    content.blocks().each(b -> {
                        if(b instanceof LegacyUnitFactory block){
                            block.subclass = classicMod.library.blocks.legacyBlocks.LegacyUnitFactory.class;
                        }
                        if(b instanceof LegacyMechPad block){
                            block.subclass = classicMod.library.blocks.legacyBlocks.MechPad.class;
                        }
                    });
                }
            }

            LoadedMod lastModVer = mods.locateMod("classicv5");
            if (lastModVer != null) {
                Log.err("Incompatible with classicv5 hjson mod and conflicts with this mod!");
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
        AutoUpdate.load();
        if(!settings.getBool("ignore-update")) AutoUpdate.check();
        if(!headless) {
            resMod.meta.version = BuildVer;
            Func<String, String> getModBundle = value -> bundle.get("mod." + value);

            StringBuilder contributors = new StringBuilder(getModBundle.get(resMod.meta.name + ".author"));
            contributors.append("\n\n").append("[#FCC21B]Credits:[]");
            int i = 0;
            while (bundle.has("mod." + resMod.meta.name + "-credits." + i)) {
                contributors.append("\n        ").append(getModBundle.get(resMod.meta.name + "-credits." + i));
                i++;
            }
            resMod.meta.author = contributors.toString();
        }
    }

    private void loadSettings() {
        ui.settings.addCategory("@setting.restored-mind", "restored-mind-vanguard", t -> {
            t.pref(new Separator("restored-menu-bg"));
            t.checkPref("use-planetmenu", true);
            t.checkPref("use-lastplanet-bg", false);

            t.pref(new Separator("restored-annoying-window"));
            t.checkPref("ignore-warning", false);

            t.pref(new Separator("restored-update"));
            t.checkPref("ignore-update", false);
            if(false) {
                t.pref(new Separator("restored-updates"));
                t.checkPref("beta-update", false);
            }
            t.button("Check Updates", AutoUpdate::check);

            t.pref(new Separator("restored-backwards-compatible"));
            t.checkPref("backward-v5", false); //TODO make some mods backwards compatiblilty with v5
            if(false) {
                t.checkPref("backward-v6", false); //TODO make some mods backwards compatiblilty with v6
            }
            t.areaTextPref("Mod Stats","Mod Version: "+ModVersion+"\n[#FCC21B]Credits:[]"+resMod.meta.author+"\nBuild Version: "+BuildVer);
            t.areaTextPref("YEY?","Mod Version: "+ModVersion);
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

    static class Separator extends Setting { //This is from prog-mats-java!
        float height;

        public Separator(String name){
            super(name);
        }

        public Separator(float height){
            this("");
            this.height = height;
        }

        @Override
        public void add(SettingsTable table){
            if(name.isEmpty()){
                table.image(Tex.clear).height(height).padTop(3f);
            }else{
                table.table(t -> {
                    t.add(title).padTop(3f);
                }).get().background(Tex.underline);
            }
            table.row();
        }
    }

}
