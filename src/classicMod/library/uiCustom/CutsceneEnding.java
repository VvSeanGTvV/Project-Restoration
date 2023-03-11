package classicMod.library.uiCustom;

import arc.*;
import arc.math.*;
import arc.scene.actions.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.dialogs.*;

import static arc.scene.actions.Actions.*;
import static mindustry.Vars.*;



public class CutsceneEnding extends BaseDialog {
    public CutsceneEnding() {
        super("");

        addCloseListener();
        shouldPause = true;

        buttons.defaults().size(210f, 64f);
        buttons.button("@menu", Icon.left, () -> {
            hide();
            ui.paused.runExitSave();
        });

        buttons.button("@continue", Icon.ok, this::hide);
    }

    public void runCutscene(Planet planet) {
        //TODO make a video runnable in Mindustry *pain*
        cont.clear();

        setTranslation(0f, -Core.graphics.getHeight());
        color.a = 255f;

        show(Core.scene, Actions.sequence(parallel(fadeIn(1.1f, Interp.fade), translateBy(0f, Core.graphics.getHeight(), 6f, Interp.pow5Out))));

        int framesTotal = 1059;
        int DelayPerFrame = 1000000; //TODO functionality
        int i;
        for(i=0; i < framesTotal;) {
            i++;
            delay(1000);

            cont.image(Core.atlas.find("restored-min-frameEnd"+i)).size(Core.graphics.getWidth(),Core.graphics.getHeight());

            //Drawable frameI = atlas.drawable("restored-mind-frameEnd" + i);
            //frameI.draw(Core.graphics.getWidth() / 2, Core.graphics.getHeight() / 2, Core.graphics.getWidth(), Core.graphics.getHeight());
        }
        if(i>=framesTotal){
            this.hide();
            ui.campaignComplete.show(planet);
        }

        //videoDemo.create(); //TODO plz request this to Anuke THX!
        //videoDemo.render();
    }
}
