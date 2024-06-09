package classicMod;

import arc.*;
import arc.files.Fi;
import arc.func.*;
import arc.util.*;
import classicMod.content.*;
import classicMod.library.ai.EffectDroneAI;
import classicMod.library.ai.ReplacementFlyingAI;
import classicMod.library.ai.ReplacementGroundAI;
import classicMod.library.ui.*;
import classicMod.library.ui.menu.*;
import mindustry.Vars;
import mindustry.ai.types.CommandAI;
import mindustry.ai.types.FlyingAI;
import mindustry.ai.types.GroundAI;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.type.UnitType;
import mindustry.ui.dialogs.SettingsMenuDialog.*;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.*;
import mindustry.ui.fragments.*;
import mindustry.world.blocks.legacy.*;

import static arc.Core.*;
import static classicMod.library.ui.menu.MenuUI.*;
import static mindustry.Vars.*;
//v5-java-mod is the current use

public class ClassicMod extends Mod{
    /** Mod's current Version **/
    public static String ModVersion = "3.2 PRE-ALPHA v146";
    /** Mod's current Build **/
    public static final String BuildVer = "11";
    protected LoadedMod resMod = mods.locateMod("restored-mind");
    public ClassicMod(){
        //Log.info("Loaded Classic constructor.");
        //listen for game load eventa


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
                ui.showCustomConfirm("@mod.restored-mind.conflictwarning.title", "@mod.restored-mind.conflictwarning.text", "Yes", "No", lastModVer.file::delete, () -> {
                    Log.err("Disabled, not to have conflicts here!");
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

    private void DeleteFile(Fi file){
        file.delete();
        ui.showOkText("@file.file-deleted", "@file.file-deleted", () -> {});
    }
    
    @Override
    public void init() {
        MenuUI.load();
        AutoUpdate.load();
        if(!settings.getBool("ignore-update")) AutoUpdate.check();

        if(!headless) {
            resMod = mods.locateMod("restored-mind");
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
        ui.settings.addCategory("@setting.restored-mind", "restored-mind-icoMenu", t -> {
            t.pref(new Separator("restored-graphic"));
            t.checkPref("use-planetmenu", true);
            t.checkPref("use-lastplanet-bg", false);
            //t.checkPref("use-custom-logo", false);

            t.pref(new Separator("restored-annoying-window"));
            t.checkPref("ignore-warning", false);
            t.checkPref("ignore-update", false);

            //t.pref(new Separator("restored-update"));
            //t.checkPref("ignore-update", false);
            if(false) {
                t.pref(new Separator("restored-updates"));
                t.checkPref("beta-update", false);
            }

            t.row();
            t.pref(new Separator("restored-content-addon"));
            t.checkPref("content-classic", false);
            t.checkPref("content-v4", false);

            if(false) {
                t.pref(new Separator("restored-backwards-compatible"));
                t.checkPref("backward-v5", false); //TODO make some mods backwards compatiblilty with v5
                t.checkPref("backward-v6", false); //TODO make some mods backwards compatiblilty with v6
            }
            t.row();
            t.add(resMod.meta.displayName+" - Info").padTop(4f).row();
            t.add("Mod Version: "+ModVersion).row();
            t.add("Build Version: "+BuildVer).row();
            t.add("Latest Release: "+!AutoUpdate.overBuild).row();
            //t.add("Latest Pre-Release: "+AutoUpdate.overBuild).row();
            //t.add("Github Build Version: "+AutoUpdate.getLatestBuild()).row();
            //t.areaTextPref("Mod Stats","Mod Version: "+ModVersion+"\nBuild Version: "+BuildVer+"\nPre-Release: "+overBuild);
        });
    }

    @Override
    public void loadContent(){
        boolean Classic = settings.getBool("content-classic");
        boolean Contentv4 = settings.getBool("content-v4");
        Log.info("Loading contents...");
        new ClassicItems().load();
        new ClassicLiquids().load();
        new ClassicBlocks().loadOverride();
        new ClassicBullets().load();
        new ClassicUnitTypes().load();
        new ClassicBlocks().load();
        if(Classic){new ClassicBlocks().loadClassic();}
        if(Contentv4){new ClassicBlocks().loadv4();
        new ExtendedSerpuloTechTree().load();}
        new ExtendedErekirTechTree().load();
        if(Classic){new ClassicTechtree().load();}

        for(UnitType a : content.units()){
            if(a.controller instanceof CommandAI){
                if(a.flying)a.controller = u -> new ReplacementFlyingAI();
                if(!a.flying)a.controller = u -> new ReplacementGroundAI();
            }
        }
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
                    t.add(title).padTop(4f);
                }).get().background(Tex.underline);
            }
            table.row();
        }
    }

}
