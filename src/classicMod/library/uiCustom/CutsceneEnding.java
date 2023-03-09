package classicMod.library.uiCustom;

import arc.*;
import arc.math.*;
import arc.scene.actions.*;
import arc.scene.style.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.dialogs.*;

import static arc.Core.atlas;
import static arc.scene.actions.Actions.*;

public class CutsceneEnding extends BaseDialog {
    public CutsceneEnding() {
        super("");

        addCloseListener();
        shouldPause = true;

        buttons.defaults().size(210f, 64f);
        buttons.button("@menu", Icon.left, () -> {
            hide();
            Vars.ui.paused.runExitSave();
        });

        buttons.button("@continue", Icon.ok, this::hide);
    }

    public void runCutscene(Planet planet){
        //TODO make a gif cutscene by indiviual frames *pain*
        cont.clear();

        setTranslation(0f, -Core.graphics.getHeight());
        color.a = 0f;

        show(Core.scene, Actions.sequence(parallel(fadeIn(1.1f, Interp.fade), translateBy(0f, Core.graphics.getHeight(), 6f, Interp.pow5Out))));

        int framesTotal = 200; //TODO idk automate the video to screenshot
        for(int i = 0; i < framesTotal ; i++){ //TODO 60 FPS or 30 FPS video ;)
            Drawable frameI = atlas.drawable("restored-mind-frameEnd"+i);
            frameI.draw(Core.graphics.getWidth()/2,Core.graphics.getHeight()/2,Core.graphics.getWidth(),Core.graphics.getHeight());
            /*
            BaseDialog dialog = new BaseDialog("");
            dialog.cont.image(atlas.find("restored-mind-frameEnd"+i)).pad(Core.graphics.getHeight()).row();
             */
        }
    }
}
