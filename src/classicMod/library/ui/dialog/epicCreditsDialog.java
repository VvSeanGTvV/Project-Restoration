package classicMod.library.ui.dialog;

import arc.Core;
import arc.func.Cons;
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

public class epicCreditsDialog extends Dialog {
    Table in = new Table();
    float scrollbar;

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
    //Cell<Table> panel = new Cell<Table>(in);
    public epicCreditsDialog() {
        super();
        scrollbar = 0f;
        //addCloseButton();

        in.center();
        in.image(Tex.clear).height(25).padTop(3f).row();
        in.image(Core.atlas.find("restored-mind-logoMod")).row();
        in.image(Tex.clear).height(25f).padTop(3f).row();

        int i = 0;
        while (bundle.has("mod." + resMod.meta.name + "-credits." + i)) {
            in.add(getModBundle.get(resMod.meta.name + "-credits." + i));
            in.row();
            i++;
        }
        show();
    }

    @Override
    public Element update(Runnable r) {
        cont.add(in);
        in.setTranslation(0, scrollbar);
        scrollbar += 0.01f;
        cont.clearChildren();
        return super.update(r);
    }
}
