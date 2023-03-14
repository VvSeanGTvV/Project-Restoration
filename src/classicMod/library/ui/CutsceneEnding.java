package classicMod.library.ui;

import arc.util.*;
import classicMod.library.ui.frag.*;
import classicMod.library.ui.menu.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.dialogs.*;

import java.io.*;

import static classicMod.library.ui.menu.MenuUI.*;
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

    public void runCutscene(Planet planet) throws IOException {
        //TODO make a video runnable in Mindustry *pain*
        cont.clear();

        //setTranslation(0f, -Core.graphics.getHeight());
        //color.a = 255f;
        show();

        //show(Core.scene, Actions.sequence(parallel(fadeIn(1.1f, Interp.fade), translateBy(0f, Core.graphics.getHeight(), 6f, Interp.pow5Out))));

        /*int framesTotal = 530;
        int DelayPerFrame = 1000000; //TODO functionality
        int i, c = 0;
        for(i=0; i < framesTotal;) {
            if (c >= DelayPerFrame) {
                cont.clear();
                c=0;
                i++;
                //--- CONVERTER ---
                String inputFile = "./assets/cutscene/frameEnd/frameEnd" + i + ".jpg";
                BufferedImage bufferedImage = ImageIO.read(new File(inputFile));
                ImageIO.write(bufferedImage, "png", new File("./assets/TemporaryFiles/imgFrame.png"));

                cont.image(Core.atlas.find("restored-mind-imgFrame")).size(Core.graphics.getWidth(), Core.graphics.getHeight());

                //Drawable frameI = atlas.drawable("restored-mind-frameEnd" + i);
                //frameI.draw(Core.graphics.getWidth() / 2, Core.graphics.getHeight() / 2, Core.graphics.getWidth(), Core.graphics.getHeight());
            }else{
                c++;
            }
        }
        if(i>=framesTotal){
            this.hide();
            ui.campaignComplete.show(planet);
        }*/
        Reflect.set(CutsceneEndingFrag.class, UIExtended.cutsceneEndingfrag, "renderer", new MainMenuRenderer(SortedPlanet));


        //videoDemo.create();
        //videoDemo.render();
    }
}
