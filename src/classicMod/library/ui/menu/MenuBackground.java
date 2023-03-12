package classicMod.library.ui.menu;

import mindustry.world.Tiles;

// Original code from Project Unity
// Author: @Goobrr
// it does not look like the original code, but it works pretty much the same way
public abstract class MenuBackground {
    /** Used in terrain menus. Generates a world displayed in the menu. */
    void generateWorld(int width, int height) {}
    /** Used in generateWorld() to generate the tiles. */
    void generate(Tiles tiles) {}

    /** Renders the menu BG itself. */
    void render() {}
}
