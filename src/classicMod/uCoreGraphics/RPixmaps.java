package classicMod.uCoreGraphics;

import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.graphics.g2d.PixmapRegion;
import arc.util.Tmp;

public class RPixmaps extends Pixmaps {

    public static Pixmap replaceWhite(PixmapRegion source, Color replaceTo){
        Pixmap out = new Pixmap(source.width, source.height);

        for(int y = 0; y < source.height; y++){
            for(int x = 0; x < source.width; x++){
                int c1 = source.getRaw(x, y);
                boolean isWhite = c1 == 0xFFFFFFFF;

                int val = Tmp.c1.set(isWhite ? replaceTo.rgba() : c1).rgba();
                out.setRaw(x, y, val);
            }
        }

        return out;
    }

    public static Pixmap copyPixmap(PixmapRegion source){
        Pixmap out = new Pixmap(source.width, source.height);

        for(int y = 0; y < source.height; y++){
            for(int x = 0; x < source.width; x++){
                int c1 = source.getRaw(x, y);

                int val = Tmp.c1.set(c1).rgba();
                out.setRaw(x, y, val);
            }
        }

        return out;
    }
}
