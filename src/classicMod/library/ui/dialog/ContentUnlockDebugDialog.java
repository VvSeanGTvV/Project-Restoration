package classicMod.library.ui.dialog;

import arc.scene.ui.Dialog;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

public class ContentUnlockDebugDialog extends BaseDialog {

    public ContentUnlockDebugDialog() {
        super("@CUD.title");
        addCloseButton();

        cont.pane(new Table() {
            {
                for (var Content : Vars.content.items()){
                    table(Styles.grayPanel, info -> {
                        info.image(Content.fullIcon).size(16f).pad(10f).left();
                        info.add(Content.localizedName).left();

                        info.table(yes -> {
                            yes.button("@locked", Content::clearUnlock).size(210f, 64f).pad(2.5f);
                            yes.button("@unlock", Content::unlock).size(210f, 64f).pad(2.5f);
                        }).right().pad(2.5f);
                    });
                }
            }
        });
    }
}
