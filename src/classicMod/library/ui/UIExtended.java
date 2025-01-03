package classicMod.library.ui;

import arc.*;
import arc.graphics.Color;
import arc.scene.Element;
import arc.scene.event.Touchable;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.scene.utils.Elem;
import arc.util.*;
import classicMod.AutoUpdate;
import classicMod.library.EventTypeExtended;
import classicMod.library.ui.dialog.*;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.SettingsMenuDialog;

import java.util.concurrent.atomic.AtomicBoolean;

import static arc.Core.settings;
import static classicMod.ClassicMod.*;
import static mindustry.gen.Icon.icons;

public class UIExtended {
    public static TechTreeDialog Techtree;
    public static ContentUnlockDebugDialog contentUnlockDebugDialog;
    public static StaticImageManager staticImageManager;

    public static void init() {
        Techtree = new TechTreeDialog();
        contentUnlockDebugDialog = new ContentUnlockDebugDialog();
        staticImageManager = new StaticImageManager();
    }

    /**
     * Get the Device's Width resolution
     **/
    public static int getWidth() {
        return Core.graphics.getWidth();
    }

    /**
     * Get the Device's Height resolution
     **/
    public static int getHeight() {
        return Core.graphics.getHeight();
    }

    public static float FPNS(float smoothFrame) {
        float FPS = smoothFrame / Core.graphics.getDeltaTime();
        return FPS / smoothFrame;
    }

    /**
     * An aligner/timing set to perfectly timed with the Client's FPS
     *
     * @param nanoseconds Nanoseconds, timing second
     * @param targetFPS   Target Frame
     * @return FPS Timing correctly to the Client's FPS
     */
    public static float fdelta(float nanoseconds, float targetFPS) {
        float target = 1000 / nanoseconds;
        float fps = FPNS(targetFPS);
        float secFPS = target * fps;
        return targetFPS / secFPS;
    }


    // Settings
    public static class Separator extends SettingsMenuDialog.SettingsTable.Setting { //This is from prog-mats-java!
        float height;
        Color textColor = Color.gray;
        Label.LabelStyle textFontStyle = Styles.outlineLabel;

        public Separator(String name) {
            super(name);
        }

        public Separator(float height) {
            this("");
            this.height = height;
        }

        public Separator(float height, Color textColor) {
            this("");
            this.height = height;
            this.textColor = textColor;
        }

        public Separator(float height, Label.LabelStyle textFontStyle) {
            this("");
            this.height = height;
            this.textFontStyle = textFontStyle;
        }

        public Separator(float height, Color textColor, Label.LabelStyle textFontStyle) {
            this("");
            this.height = height;
            this.textColor = textColor;
            this.textFontStyle = textFontStyle;
        }

        @Override
        public void add(SettingsMenuDialog.SettingsTable table) {
            if (name.isEmpty()) {
                table.image(Tex.clear).height(height).padTop(3f);
            } else {
                table.table(t -> {
                    t.add(title).color(textColor).style(textFontStyle).padTop(4f);
                }).growX().get().background(Tex.underline);
            }
            table.row();
        }
    }

    public static class SliderEventSetting extends SettingsMenuDialog.SettingsTable.Setting {
        int def;
        int min;
        int max;
        int step;
        SettingsMenuDialog.StringProcessor sp;

        Table newSlider = new Table();

        public SliderEventSetting(String name, int def, int min, int max, int step, SettingsMenuDialog.StringProcessor s) {
            super(name);
            this.def = def;
            this.min = min;
            this.max = max;
            this.step = step;
            this.sp = s;
        }

        public void rebuildSlider(){
            newSlider.clear();
            Slider slider = new Slider((float)this.min, (float)this.max, (float)this.step, false);
            slider.setValue((float)Core.settings.getInt(this.name));
            Label value = new Label("", Styles.outlineLabel);
            Table content = new Table();
            content.add(this.title, Styles.outlineLabel).left().growX().wrap();
            content.add(value).padLeft(10.0F).right();
            content.margin(3.0F, 33.0F, 3.0F, 33.0F);
            content.touchable = Touchable.disabled;
            slider.changed(() -> {
                Core.settings.put(this.name, (int)slider.getValue());
                value.setText(this.sp.get((int)slider.getValue()));
            });
            slider.change();
            newSlider.stack(new Element[]{slider, content}).width(Math.min((float)Core.graphics.getWidth() / 1.2F, 460.0F)).left().padTop(4.0F).get();
            //this.addDesc(.stack(new Element[]{slider, content}).width(Math.min((float)Core.graphics.getWidth() / 1.2F, 460.0F)).left().padTop(4.0F).get();
            //newSlider.add(slider);
        }

        @Override
        public void add(SettingsMenuDialog.SettingsTable table) {
            Events.on(EventTypeExtended.UpdateSlide.class, e -> {
                max = (e.max);
                rebuildSlider();
            });

            rebuildSlider();
            table.row();
        }
    }

    public static class ModInformation extends SettingsMenuDialog.SettingsTable.Setting {
        boolean center;
        AtomicBoolean overBuild = new AtomicBoolean(!AutoUpdate.overBuild);
        Table info = new Table();

        public ModInformation(String name, boolean center) {
            super(name);
            this.center = center;
        }

        public void rebuildInformation(){
            info.clear();
            info.add("Mod Version: "+ModVersion).color(Pal.lightishGray).padTop(4f).row();
            info.add("Build Version: "+BuildVer).color(Pal.lightishGray).padTop(4f).row();
            info.add(new Table(){{
                add("Latest Release: ").color((!overBuild.get()) ? Pal.lightishGray : Pal.redLight);
                if (!overBuild.get()) add(new Image(Icon.ok)).color(Pal.heal).size(16f).center(); else add(new Image(Icon.cancel)).color(Pal.remove).size(16f).center();
            }}).padTop(4f).row();
            info.add(new Table(){{
                add("Development Release: ").color((overBuild.get()) ? Pal.lightishGray : Pal.redLight);
                if (overBuild.get()) add(new Image(Icon.ok)).color(Pal.heal).size(16f).center(); else add(new Image(Icon.cancel)).color(Pal.remove).size(16f).center();
            }}).padTop(4f).row();

            if (false) {
                for (var key : icons.keys()) {
                    var ico = icons.get(key);
                    info.add(new Image(ico));
                    info.add(" | " + key);
                    info.row();
                }
            }
        }

        @Override
        public void add(SettingsMenuDialog.SettingsTable table) {
            Events.on(EventTypeExtended.UpdateModInformation.class, e -> {
                overBuild.set(e.overBuild);
                rebuildInformation();
            });


            if (center) table.add(info).center(); else table.add(info);
            rebuildInformation();
            table.row();

        }
    }

    public static class Banner extends SettingsMenuDialog.SettingsTable.Setting { //This is from ArchiveDustry-Java!
        float width;

        public Banner(String name, float width) {
            super(name);
            this.width = width;
        }

        @Override
        public void add(SettingsMenuDialog.SettingsTable table) {
            Image i = new Image(new TextureRegionDrawable(Core.atlas.find(name)), Scaling.fit);
            Cell<Image> ci = table.add(i).padTop(3f);

            if (width > 0) {
                ci.width(width);
            } else {
                ci.grow();
            }

            table.row();
        }
    }

    public static class ButtonSetting extends SettingsMenuDialog.SettingsTable.Setting { //This is from ArchiveDustry-Java!
        Drawable icon;
        Runnable listener;
        float iconSize;
        boolean center;

        public ButtonSetting(String name, Drawable icon, Runnable listener, float iconSize) {
            super(name);
            this.icon = icon;
            this.listener = listener;
            this.iconSize = iconSize;
            this.center = false;
        }

        public ButtonSetting(String name, Drawable icon, Runnable listener, float iconSize, boolean center) {
            super(name);
            this.icon = icon;
            this.listener = listener;
            this.iconSize = iconSize;
            this.center = center;
        }

        public ButtonSetting(String name, Drawable icon, Runnable listener) {
            super(name);
            this.icon = icon;
            this.listener = listener;
        }

        public ButtonSetting(String name, Runnable listener) {
            super(name);
            this.listener = listener;
        }

        @Override
        public void add(SettingsMenuDialog.SettingsTable table) {
            ImageButton b = Elem.newImageButton(icon, listener);
            b.resizeImage(iconSize);
            b.label(() -> title).padLeft(6).growX();
            b.center();

            if (!center) addDesc(table.add(b).left().padTop(3f).get());
            else addDesc(table.add(b).left().center().padTop(3f).get());
            table.row();
        }
    }
}
