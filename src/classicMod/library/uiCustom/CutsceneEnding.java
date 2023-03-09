package classicMod.library.uiCustom;

import arc.*;
import arc.math.*;
import arc.scene.actions.*;
import arc.scene.style.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.dialogs.*;

import static arc.Core.*;
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

    public void runCutscene(Planet planet){
        //TODO make a gif cutscene by indiviual frames *pain*
        cont.clear();

        setTranslation(0f, -Core.graphics.getHeight());
        color.a = 255f;

        show(Core.scene, Actions.sequence(parallel(fadeIn(1.1f, Interp.fade), translateBy(0f, Core.graphics.getHeight(), 6f, Interp.pow5Out))));

        int framesTotal = 200; //TODO idk automate the video to screenshot
        int DelayperFrame = 10;
        for(int i = 0; i < framesTotal ; ) { //TODO 60 FPS or 30 FPS video ;)
            int c = 0;
            if(c<DelayperFrame) {
                for (c = 0; c < DelayperFrame; c++) {
                }
                i++;
                c = 0;
            }
            Drawable frameI = atlas.drawable("restored-mind-frameEnd" + i);
            frameI.draw(Core.graphics.getWidth() / 2, Core.graphics.getHeight() / 2, Core.graphics.getWidth(), Core.graphics.getHeight());
            /*
            BaseDialog dialog = new BaseDialog("");
            dialog.cont.image(atlas.find("restored-mind-frameEnd"+i)).pad(Core.graphics.getHeight()).row();
             */
        }
        this.hide();
        ui.campaignComplete.show(planet);
    }
}
