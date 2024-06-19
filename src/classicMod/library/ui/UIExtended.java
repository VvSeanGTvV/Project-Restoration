package classicMod.library.ui;

import arc.*;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Cell;
import arc.scene.utils.Elem;
import arc.util.Scaling;
import classicMod.library.ui.dialog.*;
import mindustry.gen.Tex;
import mindustry.ui.dialogs.SettingsMenuDialog;

public class UIExtended {
    public static CutsceneEnding cutsceneEnding;
    public static TechTreeDialog Techtree;
    public static epicCreditsDialog CreditsEpic;

    public static void init(){
        cutsceneEnding = new CutsceneEnding();
        Techtree = new TechTreeDialog();
        CreditsEpic = new epicCreditsDialog();
    }

    public static class Separator extends SettingsMenuDialog.SettingsTable.Setting { //This is from prog-mats-java!
        float height;

        public Separator(String name){
            super(name);
        }

        public Separator(float height){
            this("");
            this.height = height;
        }

        @Override
        public void add(SettingsMenuDialog.SettingsTable table){
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

    public static class Banner extends SettingsMenuDialog.SettingsTable.Setting { //This is from ArchiveDustry-Java!
        float width;

        public Banner(String name, float width){
            super(name);
            this.width = width;
        }

        @Override
        public void add(SettingsMenuDialog.SettingsTable table){
            Image i = new Image(new TextureRegionDrawable(Core.atlas.find(name)), Scaling.fit);
            Cell<Image> ci = table.add(i).padTop(3f);

            if(width > 0){
                ci.width(width);
            }else{
                ci.grow();
            }

            table.row();
        }
    }

    public static class ButtonSetting extends SettingsMenuDialog.SettingsTable.Setting { //This is from ArchiveDustry-Java!
        Drawable icon;
        Runnable listener;
        float iconSize;

        public ButtonSetting(String name, Drawable icon, Runnable listener, float iconSize){
            super(name);
            this.icon = icon;
            this.listener = listener;
            this.iconSize = iconSize;
        }

        @Override
        public void add(SettingsMenuDialog.SettingsTable table){
            ImageButton b = Elem.newImageButton(icon, listener);
            b.resizeImage(iconSize);
            b.label(() -> title).padLeft(6).growX();
            b.left();

            addDesc(table.add(b).left().padTop(3f).get());
            table.row();
        }
    }

    /** Get the Device's Width resolution **/
    public static int getWidth(){
        return Core.graphics.getWidth();
    }
    /** Get the Device's Height resolution **/
    public static int getHeight(){
        return Core.graphics.getHeight();
    }
}
