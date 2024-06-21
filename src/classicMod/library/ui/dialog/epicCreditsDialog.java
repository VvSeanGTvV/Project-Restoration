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
import arc.util.Log;
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

    float rowList;
    Table in = new Table(Styles.grayPanel){{
        rowList = 0;
        add(i).size(570f, 90f).row();
        row();
        rowList++;
        //image(Tex.clear).height(25).padTop(3f).row();
        //image(Core.atlas.find("restored-mind-logoMod")).row();
        //image(Tex.clear).height(25f).padTop(3f).row();

        add(bundle.get("credits.text")).row();
        rowList++;

        int i = 0;
        while (bundle.has("mod." + resMod.meta.name + "-credits." + i)) {
            add(getModBundle.get(resMod.meta.name + "-credits." + i));
            row();
            rowList++;
            i++;
        }

        add(bundle.get("contributors")).row();
        //image(Tex.clear).height(55).padTop(3f).row();
        rowList++;

        if(!contributors.isEmpty()){
            contributors.each(a -> {
                add(a);
                row();
                rowList++;
            });
        }
    }};
    float TableHeight = in.getHeight();
    float halfTableHeight = TableHeight / 2;

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
        scrollbar = 0f;
        //addCloseButton();

        show();
    }

    DialogStyle baller = new DialogStyle(){{
       background = Styles.none;
    }};

    @Override
    public void act(float delta) {
        super.act(delta);
        float barDef = (halfTableHeight);
        if(TableHeight <= 0){
            TableHeight = in.getHeight();
            halfTableHeight = TableHeight / 2;
        }
        //Log.info("IN HEIGHT " +in.getHeight());
        //Log.info("IN prefHEIGHT " +in.getPrefHeight());
        //Log.info("IN minHEIGHT " +in.getMinHeight());
        //Log.info("IN maxHEIGHT " +in.getMaxHeight());

        scrollbar += 1.25f * Time.delta;
        cont.clearChildren();

        in.update(() -> {
            setTranslation((float) 0, scrollbar - (halfTableHeight + Core.camera.height));
        });

        cont.add(in).align(Align.bottom);
        setStyle(baller);
        //if(scrollbar > ((TableHeight * 2f))) this.hide();
        Log.info(scrollbar);
        Log.info(TableHeight);
        Log.info(scrollbar >= (halfTableHeight * 2f));
        if(scrollbar > ((halfTableHeight * 2f)) && TableHeight > 0) this.hide();
    }

    @Override
    public void draw() {
        Styles.black.draw(0, 0, UIExtended.getWidth(), UIExtended.getHeight());
        super.draw();
    }
}
