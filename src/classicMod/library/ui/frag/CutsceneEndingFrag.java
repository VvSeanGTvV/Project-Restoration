package classicMod.library.ui.frag;

import arc.scene.*;
import mindustry.graphics.*;
import mindustry.ui.fragments.*;

public class CutsceneEndingFrag extends MenuFragment {
    private MenuRenderer renderer;
    @Override
    public void build(Group parent) {
        renderer = new MenuRenderer();
        parent.setZIndex(999);
        parent.fill((x, y, w, h) -> renderer.render());
    }
}
