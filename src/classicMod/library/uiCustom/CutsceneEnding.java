package classicMod.library.uiCustom;

import arc.*;
import arc.math.*;
import arc.scene.actions.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.files.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.video.*;
import com.badlogic.gdx.video.VideoPlayer.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.dialogs.*;

import java.io.*;

import static arc.scene.actions.Actions.*;
import static mindustry.Vars.*;



public class CutsceneEnding extends BaseDialog {
    protected VideoPlayer videoPlayer; //TODO video stuff
    protected SpriteBatch batch;
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
        videoPlayer = VideoPlayerCreator.createVideoPlayer();
        batch = new SpriteBatch();

        setTranslation(0f, -Core.graphics.getHeight());
        color.a = 255f;

        show(Core.scene, Actions.sequence(parallel(fadeIn(1.1f, Interp.fade), translateBy(0f, Core.graphics.getHeight(), 6f, Interp.pow5Out))));

        /*int framesTotal = 2648;
        int DelayPerFrame = 1000; //TODO remove this junk
        for(int i = 1; i < framesTotal ; ) {
            int c = 0;
            if(c<DelayPerFrame) {
                for (c = 0; c < DelayPerFrame; c++) {
                }
                i++;
                c = 0;
            }
            Drawable frameI = atlas.drawable("restored-mind-frameEnd" + i);
            frameI.draw(Core.graphics.getWidth() / 2, Core.graphics.getHeight() / 2, Core.graphics.getWidth(), Core.graphics.getHeight());

        }*/



        try {
            videoPlayer.play(Gdx.files.local("assets/cutscene/cutscenEnd.mp4"));
        } catch (FileNotFoundException e){
            throw new RuntimeException(e);
        }
        render();
        videoPlayer.setOnCompletionListener(new CompletionListener() {
            @Override
            public void onCompletionListener(FileHandle file) {
                UIExtended.cutsceneEnding.hide();
                ui.campaignComplete.show(planet);
            }
        });
        //this.hide();
        //ui.campaignComplete.show(planet);
    }

    public void render(){
        Texture frame = videoPlayer.getTexture();

        videoPlayer.update();

        batch.begin();
        if(frame!=null){
            batch.draw(frame, 0, 0, getWidth(), getHeight());
        }
        batch.end();
    }
}
