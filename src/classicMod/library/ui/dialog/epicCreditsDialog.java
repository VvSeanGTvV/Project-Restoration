package classicMod.library.ui.dialog;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Camera;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.Dialog;
import arc.scene.ui.Image;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.*;
import arc.util.Align;
import arc.util.Scaling;
import arc.util.Time;
import classicMod.library.ui.UIExtended;
import mindustry.Vars;
import mindustry.core.UI;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import java.awt.*;
import java.util.concurrent.TimeUnit;

import static arc.Core.bundle;
import static classicMod.ClassicMod.*;

public class epicCreditsDialog extends Dialog {

    Image i = new Image(new TextureRegionDrawable(Core.atlas.find("restored-mind-logoMod")), Scaling.fit);
    Table in = new Table(){{
        add(i).size(540f).row();
        row();
        //image(Tex.clear).height(25).padTop(3f).row();
        //image(Core.atlas.find("restored-mind-logoMod")).row();
        //image(Tex.clear).height(25f).padTop(3f).row();

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
    }};
    Table imageLogo = new Table();
    float scrollbar = 0f;

    public void addCloseListener(){
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
    }

    //ScrollPane pane = new ScrollPane(in);

    public epicCreditsDialog() {
        super();
        //addCloseButton();
        show();
    }

    DialogStyle baller = new DialogStyle(){{
       background = Styles.none;
    }};

    @Override
    public void act(float delta) {
        super.act(delta);
        float maxScroll = 3.28f * Core.camera.height;
        float barDef = (UIExtended.getHeight() + in.getRows());
        //float maxY = in.ge();
        scrollbar += 1.25f  * Time.delta;
        cont.clearChildren();
        //in.setTranslation(0, b);;
        in.update(() -> setTranslation((float) 0, scrollbar - (barDef)));
        cont.update(() -> {setTranslation(0, 0); setBackground(Styles.black);});
        cont.add(in);
        setStyle(baller);
        if(scrollbar >= ((barDef * 2f))) this.hide();



    }

    @Override
    public void draw() {
        Styles.black.draw(0, 0, UIExtended.getWidth(), UIExtended.getHeight());
        super.draw();
    }
}
