package classicMod.library.ui.dialog;

import arc.*;
import arc.graphics.g2d.Draw;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.scene.Scene;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import arc.util.*;
import classicMod.library.ui.menu.MainMenuRenderer;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.gen.*;
import mindustry.graphics.g3d.*;
import mindustry.ui.Styles;

import java.util.Objects;

import static arc.Core.*;
import static classicMod.ClassicMod.*;
import static classicMod.content.ExtendedMusic.*;
import static classicMod.library.ui.UIExtended.fdelta;
import static mindustry.Vars.*;

public class epicCreditsDialog extends Dialog {

    public final PlanetRenderer planets = renderer.planets;
    Image logo = new Image(new TextureRegionDrawable(Core.atlas.find("restored-mind-logoMod")), Scaling.fit);
    PlanetParams state = new PlanetParams() {{
        planet = Planets.serpulo;
        //camPos = new Vec3(0, 0, 0);
        zoom = 0.6f;
    }};

    Table credit = new Table() {{
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
        image(Tex.clear).height(35).padTop(3f).row();
        add(bundle.get("contributors")).row();
        image(Tex.clear).height(35).padTop(3f).row();
    }};

    Table contribute = new Table() {{
        if (!contributors.isEmpty()) {
            int ia = 1;
            for (String c : contributors) {
                add(c);
                //row();
                if (++ia % 3 == 0) {
                    row();
                }
            }
        }
    }};

    Table staticTable = new Table() {{

    }};

    float scrollbar;

    DialogStyle baller = new DialogStyle() {{
        background = Styles.none;
    }};
    int doubleTapTimer;
    boolean onHold;
    boolean firstTap;

    //ScrollPane pane = new ScrollPane(in);

    KeyBinds.KeyBind MenuKeybind;

    KeyBinds.KeyBind findKeybind(String name){
        for (var keybind : keybinds.getKeybinds()){
            if (Objects.equals(keybind.name(), name)) return keybind;
        }
        return null;
    }

    boolean hidden;
    public epicCreditsDialog() {
        super();
        scrollbar = 0f;
        playMusic(credits);
        //addCloseButton();
        //staticTable.setTranslation(-(camera.width+128f), -(camera.height+128f));
        //cont.add(staticTable);
        //cont.add(in).align(Align.bottom);
        hidden = false;
        show();

        MenuKeybind = findKeybind("menu");
    }

    public void addCloseListener() {
        closeOnBack();
    }

    public void addCloseButton(float width) {
        buttons.defaults().size(width, 64f);
        buttons.button("@back", Icon.left, this::hide).size(width, 64f);

        addCloseListener();
    }

    @Override
    public void addCloseButton() {
        addCloseButton(210f);
    }

    @Override
    public void act(float delta) {
        control.sound.stop();
        super.act(delta);
        if ((credit.y + contribute.y) >= (credit.getMaxHeight() + contribute.getMaxHeight() + ((float) graphics.getHeight() / 2)) * 3f) {
            FinishedCredits();
            return;
        }
        //Log.info("IN HEIGHT " +in.getHeight());
        //Log.info("IN prefHEIGHT " +in.getPrefHeight());
        //Log.info("IN minHEIGHT " +in.getMinHeight());
        //Log.info("IN maxHEIGHT " +in.getMaxHeight());
        var bot = (Vars.mobile) ? 120f : 60f; //alignment for mobile kinda off bud
        scrollbar += fdelta(650f, bot);
        //cont.clearChildren();

        setStyle(baller);

        //Log.info(credit.getMinWidth());

        //Log.info("Crolld "+ scrollbar);
        //Log.info("Crollf "+ scrollbar * 1.15f);
        //Log.info("Crollh "+ TableHeight * 1.462f);
        //Log.info(scrollbar * 1.15f >= (TableHeight * 1.462f));
        //Log.info((float) getModBundle.get(resMod.meta.name + "-credits.mobile" + app.isMobile()).length() / 2);

        if (input.keyDown(keybinds.get(MenuKeybind).key)) FinishedCredits();
        if (app.isMobile()) {

            if (app.isAndroid()) {
                if (input.keyDown(keybinds.get(MenuKeybind).key)) FinishedCredits();
            }

            if (firstTap) {
                if (!input.isTouched()) {
                    onHold = false;
                }
                if (!onHold) {
                    doubleTapTimer++;
                    if (input.isTouched()) FinishedCredits();
                    if (doubleTapTimer > 100) {
                        firstTap = false;
                        doubleTapTimer = 0;
                    }
                }
            } else {
                if (input.isTouched()) {
                    firstTap = true;
                    onHold = true;
                }
            }
            //if(((scrollbar > (TableHeight)) && TableHeight > 0) || Core.input.isTouched()) this.hide();
        }
    }

    public void FinishedCredits() {
        control.sound.update();
        stopMusic();
        this.hide();
    }

    
    @Override
    public void hide() {
        once = false;
        hidden = true;
        super.hide();
    }

    boolean once;
    @Override
    public void draw() {
        var Wui = (TextureRegionDrawable) Tex.whiteui;
        Drawable background = Wui.tint(0f, 0f, 0f, 0.65f);

        float centerX = graphics.getWidth() / 2f;
        float width = (centerX - (contribute.getMaxWidth()));//!mobile ? credit.getMinWidth() + ((float) graphics.getWidth() / 2) : ;

        String keybind = getModBundle.get(resMod.meta.name + "-credits.mobile" + app.isMobile());
        String keybindNotification = (!(app.isMobile())) ? keybinds.get(MenuKeybind).key.toString().toUpperCase() + keybind : keybind;
        if (!once && !hidden) { staticTable.add(keybindNotification); once = true; }
        staticTable.x = staticTable.getMaxWidth() + keybindNotification.length() * 10f;
        staticTable.y = 14f;

        planets.render(state);
        background.draw(0, 0, graphics.getWidth(), graphics.getHeight());
        staticTable.draw();

        state.camPos.rotate(Vec3.Y, fdelta(250f, 120f));
        credit.x = width;
        credit.y = scrollbar - credit.getMinHeight();

        contribute.x = credit.x;
        contribute.y = scrollbar - ((credit.getMinHeight() / 2.5f) + contribute.getMinHeight());

        contribute.draw();
        credit.draw();

        Draw.flush();
        super.draw();
    }
}
