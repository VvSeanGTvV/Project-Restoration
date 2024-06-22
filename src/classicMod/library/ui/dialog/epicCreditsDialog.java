package classicMod.library.ui.dialog;

import arc.Core;
import arc.Graphics;
import arc.func.Cons;
import arc.graphics.Camera;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.input.KeyCode;
import arc.math.Mathf;
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
import mindustry.world.meta.Stat;

import java.awt.*;
import java.util.concurrent.TimeUnit;

import static arc.Core.*;
import static classicMod.ClassicMod.*;
import static classicMod.content.ExtendedMusic.*;
import static mindustry.Vars.control;
import static mindustry.Vars.state;

public class epicCreditsDialog extends Dialog {

    Image logo = new Image(new TextureRegionDrawable(Core.atlas.find("restored-mind-logoMod")), Scaling.fit);

    Table in = new Table(){{
        add(logo).size(570f, 90f).row();
        image(Tex.clear).height(55).padTop(3f).row();
        row();
        //image(Tex.clear).height(25).padTop(3f).row();
        //image(Core.atlas.find("restored-mind-logoMod")).row();
        //image(Tex.clear).height(25f).padTop(3f).row();

        add(bundle.get("credits.text")).row();
        add(getModBundle.get(resMod.meta.name + "-credits.author")).row();

        int i = 0;
        while (bundle.has("mod." + resMod.meta.name + "-credits." + i)) {
            add(getModBundle.get(resMod.meta.name + "-credits." + i));
            row();
            i++;
        }
        image(Tex.clear).height(55).padTop(3f).row();
        add(bundle.get("contributors")).row();
        image(Tex.clear).height(55).padTop(3f).row();

        if(!contributors.isEmpty()){
            contributors.each(a -> {
                add(a);
                row();
            });
        }
    }};
    float TableHeight;
    float halfTableHeight;

    Table staticTable = new Table(){{
        add(getModBundle.get(resMod.meta.name + "-credits.mobile" + app.isMobile()));
    }};

    float scrollbar;

    DialogStyle baller = new DialogStyle(){{
        background = Styles.none;
    }};

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
        playMusic(credits);
        //addCloseButton();
        //staticTable.setTranslation(-(camera.width+128f), -(camera.height+128f));
        //cont.add(staticTable);
        cont.add(in).align(Align.bottom);
        show();
    }
    int doubleTapTimer;
    boolean onHold;
    boolean firstTap;

    float DurationDialog = 2f;
    @Override
    public void act(float delta) {
        control.sound.stop();
        super.act(delta);
        if(TableHeight <= 0){
            TableHeight = in.getHeight();
            halfTableHeight = TableHeight / 1.75f;
        }
        //Log.info("IN HEIGHT " +in.getHeight());
        //Log.info("IN prefHEIGHT " +in.getPrefHeight());
        //Log.info("IN minHEIGHT " +in.getMinHeight());
        //Log.info("IN maxHEIGHT " +in.getMaxHeight());

        scrollbar += Time.delta / DurationDialog;
        //cont.clearChildren();

        in.update(() -> {
            setTranslation(0f, scrollbar - (halfTableHeight + Core.camera.height));
        });


        setStyle(baller);

        Log.info(scrollbar);
        Log.info(TableHeight * 1.375f);
        Log.info(scrollbar >= (TableHeight * 1.375f));
        Log.info((float) getModBundle.get(resMod.meta.name + "-credits.mobile" + app.isMobile()).length() / 2);

        if(Core.input.keyDown(KeyCode.escape)) FinishedCredits();
        if(scrollbar >= (TableHeight * 1.375f)) FinishedCredits();
        if(Core.app.isMobile()){
            if(firstTap){
                if(!Core.input.isTouched()){ onHold = false; }
                if(!onHold) {
                    doubleTapTimer++;
                    if (Core.input.isTouched()) FinishedCredits();
                    if (doubleTapTimer > 100){ firstTap = false; doubleTapTimer = 0; }
                }
            } else {
                if(Core.input.isTouched()){ firstTap = true; onHold = true; }
            }
            //if(((scrollbar > (TableHeight)) && TableHeight > 0) || Core.input.isTouched()) this.hide();
        }
    }

    public void FinishedCredits(){
        control.sound.update();
        stopMusic();
        this.hide();
    }

    @Override
    public void draw() {
        float IE = ((float) graphics.getWidth() / 1000);
        float IA = ((float) graphics.getWidth() / 225);
        staticTable.x = (((float) getModBundle.get(resMod.meta.name + "-credits.mobile" + app.isMobile()).length() / 2) * IA * IE);
        staticTable.y = 20f;

        Styles.black.draw(0, 0, UIExtended.getWidth(), UIExtended.getHeight());
        staticTable.draw();
        Draw.flush();
        super.draw();
    }
}
