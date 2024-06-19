package classicMod.library.ui.dialog;

import arc.Core;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.Dialog;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import java.awt.*;

import static arc.Core.bundle;
import static classicMod.ClassicMod.*;

public class epicCreditsDialog extends BaseDialog {
    Table in = new Table();
    float scrollbar;
    //ScrollPane pane = new ScrollPane(in);
    //Cell<Table> panel = new Cell<Table>(in);
    public epicCreditsDialog() {
        super("Credits");
        scrollbar = 0f;
        addCloseButton();

        in.center();
        in.image(Tex.clear).height(25).padTop(3f).row();
        in.image(Core.atlas.find("restored-mind-logoMod")).row();
        in.image(Tex.clear).height(25f).padTop(3f).row();

        int i = 0;
        while (bundle.has("mod." + resMod.meta.name + "-credits." + i)) {
            in.add(getModBundle.get(resMod.meta.name + "-credits." + i));
            i++;
        }
        in.draw();
    }

    @Override
    public Element update(Runnable r) {
        in.setTranslation(0, scrollbar);
        scrollbar += 0.01f;
        return super.update(r);
    }
}
