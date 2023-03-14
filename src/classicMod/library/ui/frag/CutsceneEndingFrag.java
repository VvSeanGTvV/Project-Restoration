package classicMod.library.ui.frag;

import arc.scene.*;
import arc.scene.ui.layout.*;
import mindustry.graphics.*;
import mindustry.ui.fragments.*;

import static mindustry.Vars.*;

public class CutsceneEndingFrag extends MenuFragment {
    private MenuRenderer renderer;
    @Override
    public void build(Group parent) {
        renderer = new MenuRenderer();
        Group group = new WidgetGroup();
        group.setFillParent(true);
        group.visible(() -> !ui.editor.isShown());

        parent.addChild(group);

        parent = group;

        parent.setZIndex(50);
        parent.fill((x, y, w, h) -> renderer.render());
    }
}
