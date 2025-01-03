package classicMod.library.ui.dialog;

import arc.graphics.Texture;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import classicMod.ClassicMod;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import java.util.concurrent.atomic.AtomicInteger;

import static mindustry.Vars.dataDirectory;

public class StaticImageManager extends BaseDialog {

    Dialog warningDelete = new Dialog();

    static Table ImageManger = new Table();

    public static void rebuildManager(){
        AtomicInteger i = new AtomicInteger();
        ImageManger.clearChildren();
        dataDirectory.child("prjRes-background").walk(fi -> {
            Texture image = new Texture(fi);

            i.getAndIncrement();
            ImageManger.table(Styles.grayPanel, imData -> {
                imData.table(imTable -> {
                    imTable.image(new TextureRegion(image)).size(128f);
                }).pad(10f);
                imData.table(imButton -> {
                    imButton.add(fi.name()).pad(5f).row();
                    imButton.button("@delete", Icon.trash, () -> {
                        Vars.ui.showConfirm("@confirm", "@delete.file-disclaimer", () -> {
                            fi.delete();
                            rebuildManager();
                            ClassicMod.rebuildStaticImage();
                        });
                    }).size(210f, 64f);
                });
            });
            if (i.get() >= 2) ImageManger.row();
        });
    }

    public StaticImageManager() {
        super("@SIM.title");

        rebuildManager();
        addCloseButton();

        cont.add(new ScrollPane(ImageManger));
    }


}
