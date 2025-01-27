package classicMod.library.ui.dialog;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.util.*;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.core.GameState;
import mindustry.ctype.ContentType;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.*;
import mindustry.input.Binding;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

public class NewLaunchDialog extends Dialog {
    public final PlanetRenderer planets;
    public PlanetParams state = new PlanetParams();
    public float zoom;
    public Label hoverLabel;
    protected boolean wasPaused;
    protected boolean shouldPause;

    public NewLaunchDialog() {
        super("", new Dialog.DialogStyle() {
            {
                this.stageBackground = Tex.windowEmpty;
                this.titleFont = Fonts.def;
                this.background = Tex.windowEmpty;
                this.titleFontColor = Pal.accent;
            }
        });
        this.hidden(() -> {
            if (this.shouldPause && Vars.state.isGame() && !Vars.net.active() && !this.wasPaused) {
                Vars.state.set(GameState.State.playing);
            }

            Sounds.back.play();
        });
        this.shown(() -> {
            if (this.shouldPause && Vars.state.isGame() && !Vars.net.active()) {
                this.wasPaused = Vars.state.is(GameState.State.paused);
                Vars.state.set(GameState.State.paused);
            }
        });

        shouldPause = false;
        this.planets = Vars.renderer.planets;
        state.planet = Vars.content.getByName(ContentType.planet, Core.settings.getString("lastplanet", "serpulo"));
        if(state.planet == null) state.planet = Planets.serpulo;

        this.setFillParent(true);
        this.addListener(new InputListener() {
            public boolean keyDown(InputEvent event, KeyCode key) {
                if (event.targetActor != NewLaunchDialog.this || key != KeyCode.escape && key != KeyCode.back && key != Core.keybinds.get(Binding.planet_map).key) {
                    return false;
                } else {
                    /*if (NewLaunchDialog.this.showing() && NewLaunchDialog.this.newPresets.size > 1) {
                        PlanetDialog.this.newPresets.truncate(1);
                    } else if (PlanetDialog.this.selected != null) {
                        PlanetDialog.this.selectSector((Sector)null);
                    } else {
                        Core.app.post(() -> {
                            PlanetDialog.this.hide();
                        });
                    }*/

                    Core.app.post(NewLaunchDialog.this::hide);

                    return true;
                }
            }
        });
        this.dragged((cx, cy) -> {
            if (Core.input.getTouches() <= 1) {

                Vec3 pos = this.state.camPos;
                float upV = pos.angle(Vec3.Y);
                float xscale = 9.0F;
                float yscale = 10.0F;
                float margin = 1.0F;
                float speed = 1.0F - Math.abs(upV - 90.0F) / 90.0F;
                pos.rotate(this.state.camUp, cx / xscale * speed);
                float amount = cy / yscale;
                amount = Mathf.clamp(upV + amount, margin, 180.0F - margin) - upV;
                pos.rotate(Tmp.v31.set(this.state.camUp).rotate(this.state.camDir, 90.0F), amount);
            }
        });
        this.addListener(new InputListener() {
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                if (event.targetActor == NewLaunchDialog.this) {
                    NewLaunchDialog.this.zoom = Mathf.clamp(NewLaunchDialog.this.zoom + amountY / 10.0F, NewLaunchDialog.this.state.planet.minZoom, 2.0F);
                }

                return true;
            }
        });
        this.addCaptureListener(new ElementGestureListener() {
            float lastZoom = -1.0F;

            public void zoom(InputEvent event, float initialDistance, float distance) {
                if (this.lastZoom < 0.0F) {
                    this.lastZoom = NewLaunchDialog.this.zoom;
                }

                NewLaunchDialog.this.zoom = Mathf.clamp(initialDistance / distance * this.lastZoom, NewLaunchDialog.this.state.planet.minZoom, 2.0F);
            }

            public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button) {
                this.lastZoom = NewLaunchDialog.this.zoom;
            }
        });

        this.hoverLabel = new Label("");

        this.hoverLabel.setStyle(Styles.outlineLabel);
        this.hoverLabel.setAlignment(1);

        addBack();
    }

    public void showPlanetLaunch(Sector sector) {
        state.planet = sector.planet;
        state.otherCamPos = sector.planet.position;
        state.otherCamAlpha = 0f;
        super.show();
    }

    void addBack() {
        this.buttons.button("@back", Icon.left, this::hide).size(200.0F, 54.0F).pad(2.0F).bottom();
    }

    @Override
    public void draw() {
        planets.render(state);
        Draw.flush();
        super.draw();
    }

    @Override
    public void act(float delta) {
        Planet planetHover = null;
        for (var planet : Vars.content.planets()) {
            var pos = planet.intersect(this.planets.cam.getMouseRay(), 1.17F * planet.radius);
            if (!planet.accessible || !planet.visible) continue;
            if (pos != null) {
                planetHover = planet;
                break;
            }
        }
        if (planetHover != null && !Vars.mobile) {
            this.addChild(this.hoverLabel);
            this.hoverLabel.toFront();
            this.hoverLabel.touchable = Touchable.disabled;
            this.hoverLabel.color.a = this.state.uiAlpha;
            Vec3 pos = this.planets.cam.project(Tmp.v31.set(planetHover.position).setLength(0F * planetHover.radius).rotate(Vec3.Y, -planetHover.getRotation()).add(planetHover.position));
            this.hoverLabel.setPosition(pos.x - Core.scene.marginLeft, pos.y - Core.scene.marginBottom, 1);
            this.hoverLabel.getText().setLength(0);
            StringBuilder tx = this.hoverLabel.getText();
            tx.append("[accent][[ [white]").append(planetHover.localizedName).append("[accent] ]");

            this.hoverLabel.invalidateHierarchy();
        } else {
            this.hoverLabel.remove();
        }

        this.state.zoom = Mathf.lerpDelta(this.state.zoom, this.zoom, 0.4F);
        this.state.uiAlpha = Mathf.lerpDelta(this.state.uiAlpha, (float)Mathf.num(this.state.zoom < 1.9F), 0.1F);
    }
}
