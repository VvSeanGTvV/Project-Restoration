package classicMod.library.ui.dialog;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import arc.util.*;
import mindustry.content.Planets;
import mindustry.gen.*;
import mindustry.graphics.g3d.*;
import mindustry.ui.Styles;

import java.util.Objects;

import static arc.Core.*;
import static classicMod.ClassicMod.*;
import static classicMod.content.RMusic.*;
import static classicMod.library.ui.UIExtended.fdelta;
import static mindustry.Vars.*;

public class CreditsCutsceneDialog extends Dialog {

    public final PlanetRenderer planets = renderer.planets;
    Image logo = new Image(new TextureRegionDrawable(Core.atlas.find("restored-mind-logoMod")), Scaling.fit);
    TextureRegion iconRegion = new TextureRegion(resMod.iconTexture);
    Image icon = new Image(new TextureRegionDrawable(iconRegion), Scaling.fit);
    PlanetParams state = new PlanetParams() {{
        planet = Planets.serpulo;
        //camPos = new Vec3(0, 0, 0);
        zoom = 0.5f;
    }};

    Table credit = new Table() {{
        //add(icon).size(120f, 90f);
        add(logo).size(570f, 174f).row();
        image(Tex.clear).height(27.5f).padTop(3f).row();
        add("v" + ModVersion).row();
        add("build " + BuildVer).row();
        image(Tex.clear).height(27.5f).padTop(3f).row();
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
    }};

    Table contribute = new Table() {{
        float width = 85f;
        image(Tex.clear).height(35).padTop(3f).row();
        image(Tex.clear).width(bundle.get("contributors").length() + width / 2f);
        add(bundle.get("contributors")).row();
        image(Tex.clear).height(35).padTop(3f).row();
        if (!contributors.isEmpty()) {
            for(int i=1; i < contributors.size / 2; i += 2){
                if (i < contributors.size) add(contributors.get(i));
                image(Tex.clear).width(width);
                if (i+1 < contributors.size) add(contributors.get(i+1));
                row();
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
    public CreditsCutsceneDialog() {
        super();

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

    int stage = 0;
    @Override
    public void act(float delta) {
        control.sound.stop();
        super.act(delta);

        setStyle(baller);

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

        resetStage();
        scrollbar = 0f; i = stage = 0;
        alpha = 1f; contributeY = 0f;
        dontFadeOut = false;

        super.hide();
    }

    @Override
    public Dialog show() {
        once = false;
        hidden = false;

        resetStage();
        scrollbar = 0f; i = stage = 0;
        alpha = 1f; contributeY = 0f;
        playMusic(seq);
        hidden = false;
        dontFadeOut = false;


        return super.show();
    }

    boolean once, setVec, dontFadeOut = false;
    float alpha = 1f, contributeY = 0f;
    float i;

    public void drawEsc(float centerX){
        String keybind = getModBundle.get(resMod.meta.name + "-credits.mobile" + app.isMobile());
        String keybindNotification = (!(app.isMobile())) ? keybinds.get(MenuKeybind).key.toString().toUpperCase() + " " + keybind : keybind;
        //if (!once && !hidden) { staticTable.add(keybindNotification); once = true; }

        if (app.isMobile()) {
            if (!once && !hidden && firstTap) { staticTable.add(keybindNotification); once = true; }
            if (!firstTap && once) { once = false; staticTable.clearChildren(); }
        } else {
            if (!once && !hidden) { staticTable.add(keybindNotification); once = true; }
        }
        if (hidden) staticTable.clearChildren();

        staticTable.x = centerX - keybindNotification.length();
        staticTable.y = 14f;

        staticTable.draw();
    }

    public void resetStage(){
        contributeY = 0f;
        setVec = false;
        state.zoom = 0.5f;
        state.camPos.set(0f,0f, 4f);
        alpha = 1f;
    }

    public void changeStage(int change){
        resetStage();

        stage = change;
    }

    @Override //TODO revamp the cutscene
    public void draw() {
        int FPSCAP = (!app.isMobile()) ? settings.getInt("fpscap") : Mathf.round((float) settings.getInt("fpscap"));
        i += Time.delta;
        var timingCam = Time.delta / 5;
        var Wui = (TextureRegionDrawable) Tex.whiteui;
        alpha = (!dontFadeOut) ? Mathf.lerpDelta(alpha, 0f, 0.05f) : 1f;
        Drawable background = Wui.tint(0f, 0f, 0f, alpha);
        Drawable backgroundG = Wui.tint(0f, 0f, 0f, 0.65f);

        float centerX = graphics.getWidth() / 2f;
        float centerY = graphics.getHeight() / 2f;

        // Before Draw Text
        planets.render(state);
        backgroundG.draw(0, 0, graphics.getWidth(), graphics.getHeight());

        if (stage == 0){
            int maxLimit = 5;
            float fadeTime = 1f;

            state.planet = Planets.sun;

            credit.x = centerX;
            credit.y = centerY;
            credit.draw();

            if (!setVec) {
                state.camPos.rotate(Vec3.X, 25f);
                state.zoom = 5f;
                setVec = true;
            }
            state.camPos.rotate(Vec3.Y, timingCam);

            if (i >= (float) maxLimit * 60) {
                alpha = ((float) (i - (maxLimit * 60)) / (fadeTime * 60));
                if (i >= (maxLimit * 60) + (fadeTime * 60)) {
                    changeStage(1);
                    i = 0;
                }
            }
        }
        if (stage == 1) {
            int maxLimit = 7;
            float fadeTime = 1f;

            contributeY += Time.delta * 2;
            state.planet = Planets.erekir;

            contribute.x = centerX;
            contribute.y = centerY + (contributeY - (contribute.getMinHeight() - (50f * graphics.getAspect())));
            contribute.draw();

            if (!setVec) {
                state.camPos.rotate(Vec3.X, -5f);
                setVec = true;
            }
            state.camPos.rotate(Vec3.Z, timingCam);
            state.camPos.rotate(Vec3.Y, -timingCam);
            state.zoom += timingCam / 40;

            if (i >= (float) maxLimit * 60) {
                alpha = ((float) (i - (maxLimit * 60)) / (fadeTime * 60));
                if (i >= (maxLimit * 60) + (fadeTime * 60)) {
                    changeStage(2);
                    contribute.clearChildren();
                    if (!contributors.isEmpty()) {
                        for(int i = contributors.size / 2; i < contributors.size; i += 2){
                            if (i < contributors.size) contribute.add(contributors.get(i));
                            contribute.image(Tex.clear).width(85f);
                            if (i+1 < contributors.size) contribute.add(contributors.get(i+1));
                            contribute.row();
                        }
                    }
                    i = 0;
                }
            }
        }
        if (stage == 2) {
            int maxLimit = 5;
            float fadeTime = 1f;

            contributeY += Time.delta * 2;
            state.planet = Planets.serpulo;

            contribute.x = centerX;
            contribute.y = (centerY + (contribute.getMinHeight() + (50f / graphics.getAspect()))) - contributeY;
            contribute.draw();

            if (!setVec) {
                state.zoom = 4f;
                state.camPos.rotate(Vec3.X, -5f);
                setVec = true;
            }
            state.camPos.rotate(Vec3.Z, -timingCam);
            state.camPos.rotate(Vec3.X, timingCam);
            state.zoom -= timingCam / 40;

            if (i >= (float) maxLimit * 60) {
                alpha = ((float) (i - (maxLimit * 60)) / (fadeTime * 60));
                if (i >= (maxLimit * 60) + (fadeTime * 60)) {
                    dontFadeOut = true;
                    changeStage(3);
                    FinishedCredits();
                    i = 0;
                }
            }
        }


        background.draw(0, 0, graphics.getWidth(), graphics.getHeight());

        drawEsc(centerX);
        /*float width = (centerX - (contribute.getMaxWidth()));//!mobile ? credit.getMinWidth() + ((float) graphics.getWidth() / 2) : ;



        planets.render(state);
        background.draw(0, 0, graphics.getWidth(), graphics.getHeight());
        staticTable.draw();

        state.camPos.rotate(Vec3.Y, fdelta(250f, 120f));
        if (i >= 650) {
            alpha = ((float) i / 1000);
            if (i >= 1000) {
                alpha = 1f;
                Seq<Planet> visible = Vars.content.planets().copy().filter(p -> p.visible);
                visible.remove(state.planet);
                state.planet = visible.get(Mathf.floor((float) (Math.random() * visible.size)));
                i = 0;
            }
        }
        credit.x = width;
        credit.y = scrollbar - (credit.getMinHeight() / 2f);

        contribute.x = credit.x;
        contribute.y = (credit.y + (contribute.getMinHeight() / 2f)) + (credit.getMinHeight() / 2f);

        contribute.draw();
        credit.draw();*/

        Draw.flush();
        super.draw();
    }
}
