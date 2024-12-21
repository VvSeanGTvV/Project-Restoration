package classicMod.library.drawCustom;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.graphics.*;

/** @author xstabux **/
public class Outliner {

    /** @author MEEP of Faith#7277 */
    public static void outlineRegion(MultiPacker packer, TextureRegion textureRegion, String outputName) {
        outlineRegion(packer, textureRegion, Pal.darkerMetal, outputName, 3);
    }

    /** @author MEEP of Faith#7277 */
    public static void outlineRegion(MultiPacker packer, TextureRegion textureRegion, Color outlineColor, String outputName) {
        outlineRegion(packer, textureRegion, outlineColor, outputName, 3);
    }

    /** @author MEEP of Faith#7277 */
    public static void outlineRegion(MultiPacker packer, TextureRegion[] textures, Color outlineColor, String outputName) {
        outlineRegion(packer, textures, outlineColor, outputName, 4);
    }

    /**
     * Outlines a list of regions. Run in createIcons.
     *
     * @author MEEP of Faith#7277
     */
    public static void outlineRegion(MultiPacker packer, TextureRegion[] textures, Color outlineColor, String outputName, int radius) {
        for (int i = 0; i < textures.length; i++) {
            outlineRegion(packer, textures[i], outlineColor, outputName + "-" + i, radius);
        }
    }

    /**
     * Outlines a given textureRegion. Run in createIcons.
     *
     * @param textureRegion
     * 	The texture you want to generate outline with
     * @param outlineColor
     * 	The color1 of the outline, default is Pal.darkerMetal
     * @param outputName
     * 	The outline name
     * @param outlineRadius
     * 	The thiccness of the outline, default is 3 or 4
     * @author MEEP of Faith#7277
     */
    public static void outlineRegion(MultiPacker packer, TextureRegion textureRegion, Color outlineColor, String outputName, int outlineRadius) {
        if (textureRegion == null) return;
        PixmapRegion region = Core.atlas.getPixmap(textureRegion);
        Pixmap out = Pixmaps.outline(region, outlineColor, outlineRadius);
        if (Core.settings.getBool("linear", true)) {
            Pixmaps.bleed(out);
        }
        packer.add(MultiPacker.PageType.main, outputName, out);
    }

    public static void outlineTemplateRegion(MultiPacker packer, TextureRegion textureRegion, Color outlineColor, String outputName) {
        if (textureRegion == null) return;
        PixmapRegion region = Core.atlas.getPixmap(textureRegion);
        Pixmap out = outlineTemplate(region, outlineColor);
        if (Core.settings.getBool("linear", true)) {
            Pixmaps.bleed(out);
        }
        packer.add(MultiPacker.PageType.main, outputName, out);
    }

    public static void outlineRegion(MultiPacker packer, TextureRegion textureRegion, Color outlineColor, String outputName, int Xmin, int Xmax, int Ymin, int Ymax, int xOffset, int yOffset) {
        if (textureRegion == null) return;
        PixmapRegion region = Core.atlas.getPixmap(textureRegion);
        Pixmap out = outline(region, outlineColor, Xmin, Xmax, Ymin, Ymax, xOffset, yOffset);
        if (Core.settings.getBool("linear", true)) {
            Pixmaps.bleed(out);
        }
        packer.add(MultiPacker.PageType.main, outputName, out);
    }

    public static Pixmap outlineTemplate(PixmapRegion region, Color color) {
        Pixmap out = region.crop();
        Pixmap pixmap = out.copy();
        int col = color.rgba();

        for(int x = 0; x < region.width; ++x) {
            for(int y = 0; y < region.height; ++y) {
                if (!out.empty(x, y))
                    pixmap.set(x, y, col);
            }
        }

        return pixmap;
    }

    public static Pixmap outline(PixmapRegion region, Color color, int Xmin, int Xmax, int Ymin, int Ymax, int xOffset, int yOffset) {
        int outlineColor = color.rgba8888();
        Pixmap out = region.crop();

        for(int x = 0; x < region.width; ++x) {
            for(int y = 0; y < region.height; ++y) {
                if (region.getA(x, y) < 255) {
                    boolean found = false;

                    label44:
                    for(int rx = Xmin; rx < Xmax; ++rx) {
                        for(int ry = Ymin; ry < Ymax; ++ry) {
                            if (Structs.inBounds(rx + x, ry + y, region.width, region.height) && (Xmin >= rx) && (Ymin >= ry)  && region.getA(rx + x, ry + y) != 0) {
                                found = true;
                                break label44;
                            }
                        }
                    }

                    if (found) {
                        out.set(x + xOffset, y + yOffset, outlineColor);
                    }
                }
            }
        }

        return out;
    }
}
