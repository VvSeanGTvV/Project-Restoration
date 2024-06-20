package classicMod.library.ui.dialog;

import arc.Core;
import arc.func.Cons;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.Dialog;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.*;
import arc.util.Time;
import classicMod.library.ui.UIExtended;
import mindustry.core.UI;
import mindustry.gen.*;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import java.awt.*;
import java.util.concurrent.TimeUnit;

import static arc.Core.bundle;
import static classicMod.ClassicMod.*;

public class epicCreditsDialog extends Dialog {
    Table in = new Table();
    float scrollbar = 0f;

    /*public void addCloseListener(){
        closeOnBack();
    }

    public void addCloseButton(float width){
        buttons.defaults().size(width, 64f);
        buttons.button("@back", Icon.left, this::hide).size(width, 64f);

        addCloseListener();
    }

    @Override
    public void addCloseButton(){
        addCloseButton(210f);
    }*/

    //ScrollPane pane = new ScrollPane(in);

    public epicCreditsDialog() throws InterruptedException {
        super();
        //addCloseButton();
        in.add(new Table() {{
            center();
            image(Tex.clear).height(25).padTop(3f).row();
            image(Core.atlas.find("restored-mind-logoMod")).row();
            image(Tex.clear).height(25f).padTop(3f).row();

            add(bundle.get("credits.text")).row();

            int i = 0;
            while (bundle.has("mod." + resMod.meta.name + "-credits." + i)) {
                add(getModBundle.get(resMod.meta.name + "-credits." + i));
                row();
                i++;
            }

            add(bundle.get("contributors"));
            image(Tex.clear).height(55).padTop(3f).row();

            if(!contributors.isEmpty()){
                contributors.each(a -> {
                    add(a);
                    row();
                });
            }
        }});
        show();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        float maxScroll = 550f;
        scrollbar += 2f  * Time.delta;
        cont.clearChildren();
        //in.setTranslation(0, b);;
        in.update(() -> setTranslation((float) 0, scrollbar - (UIExtended.getHeight() + maxScroll)));
        cont.add(in);
        if(scrollbar >= (UIExtended.getHeight() + maxScroll)) this.hide();
    }
}
