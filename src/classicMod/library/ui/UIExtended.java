package classicMod.library.ui;

import arc.Core;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.Cell;
import arc.scene.utils.Elem;
import arc.util.Scaling;
import classicMod.library.ui.dialog.TechTreeDialog;
import mindustry.gen.Tex;
import mindustry.ui.dialogs.SettingsMenuDialog;

public class UIExtended {
    public static TechTreeDialog Techtree;

    public static void init() {
        Techtree = new TechTreeDialog();
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
        float FPSttarget = smoothFrame / 100;
        return FPSttarget / Core.graphics.getDeltaTime();
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
        float fpstarget = target * fps;
        return targetFPS / fpstarget;
    }

    public static class Separator extends SettingsMenuDialog.SettingsTable.Setting { //This is from prog-mats-java!
        float height;

        public Separator(String name) {
            super(name);
        }

        public Separator(float height) {
            this("");
            this.height = height;
        }

        @Override
        public void add(SettingsMenuDialog.SettingsTable table) {
            if (name.isEmpty()) {
                table.image(Tex.clear).height(height).padTop(3f);
            } else {
                table.table(t -> {
                    t.add(title).padTop(4f);
                }).get().background(Tex.underline);
            }
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

        public ButtonSetting(String name, Drawable icon, Runnable listener, float iconSize) {
            super(name);
            this.icon = icon;
            this.listener = listener;
            this.iconSize = iconSize;
        }

        @Override
        public void add(SettingsMenuDialog.SettingsTable table) {
            ImageButton b = Elem.newImageButton(icon, listener);
            b.resizeImage(iconSize);
            b.label(() -> title).padLeft(6).growX();
            b.center();

            addDesc(table.add(b).left().padTop(3f).get());
            table.row();
        }
    }
}
