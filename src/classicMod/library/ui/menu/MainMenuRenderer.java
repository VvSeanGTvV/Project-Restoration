package classicMod.library.ui.menu;

import mindustry.graphics.MenuRenderer;

import static mindustry.Vars.mobile;

public class MainMenuRenderer extends MenuRenderer {
    private final int width = !mobile ? 100 : 60, height = !mobile ? 50 : 40;
    public MenuBackground background;


    public MainMenuRenderer(MenuBackground background) {
        if (background != null) {
            this.background = background;
        } else {
            //a default BG just in case
            this.background = MenuUI.Erekir;
        }

        MainMenuGenerate();
    }

    public void MainMenuGenerate() {
        background.generateWorld(width, height);
    }

    @Override
    public void render() {
        super.render();
        background.render();
    }
}
