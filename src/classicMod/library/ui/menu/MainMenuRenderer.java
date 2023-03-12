package classicMod.library.ui.menu;

import arc.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.fragments.*;

import static classicMod.library.ui.menu.MenuUI.*;
import static mindustry.Vars.*;

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

        Planet lastPlanet = content.getByName(ContentType.planet, Core.settings.getString("lastplanet", "serpulo"));
        MenuBackground bg = (lastPlanet == Planets.erekir ? Erekir : lastPlanet == Planets.serpulo ? Serpulo : solarSystem);
        Reflect.set(MenuFragment.class, ui.menufrag, "renderer", new MainMenuRenderer(bg));
    }
}
