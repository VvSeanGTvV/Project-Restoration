package classicMod.library.ui.menu;

import arc.scene.style.Drawable;
import arc.scene.ui.ImageButton;
import arc.util.Align;

public class MobileButton extends ImageButton {

    public MobileButton(Drawable icon, String text, Runnable listener){
        super(icon);
        clicked(listener);
        row();
        add(text).growX().wrap().center().get().setAlignment(Align.center, Align.center);
    }
}
